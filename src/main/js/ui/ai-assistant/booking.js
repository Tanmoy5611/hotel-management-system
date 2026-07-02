import { ensureCustomerSession, hasCustomerNavigation, postJson, readJsonResponse } from './api.js';
import { extractDateRange, extractRoomNumber, isBookingIntent, isCancelIntent, isConfirmIntent } from './booking-intent.js';
import { appendBookingList, appendBookingQuote, appendLoginPrompt, appendMessage } from './rendering.js';

export async function handleBookingMessage(message, messages, chatState, input) {
  // Return true when the message belongs to the booking flow instead of AI search
  const bookingIntent = isBookingIntent(message);
  const cancelIntent = isCancelIntent(message);
  const dates = extractDateRange(message);
  const roomNumber = extractRoomNumber(message);
  const continuingBooking = chatState.bookingStep || chatState.selectedRoomId || chatState.selectedRoomNumber;

  if (isConfirmIntent(message) && chatState.pendingQuote) {
    // Confirmation uses the stored quote so users can reply with "yes"
    if (!await ensureCustomerSession()) {
      resetBookingState(chatState);
      appendLoginPrompt(messages);
      return true;
    }

    await confirmPendingBooking(messages, chatState);
    return true;
  }

  if (cancelIntent) {
    // Cancellation starts by listing only bookings owned by the logged-in customer
    if (!await ensureCustomerSession()) {
      appendLoginPrompt(messages);
      return true;
    }

    await showCancelableBookings(messages);
    return true;
  }

  if (!bookingIntent && !continuingBooking && !roomNumber) {
    return false;
  }

  if (!await ensureCustomerSession()) {
    resetBookingState(chatState);
    appendLoginPrompt(messages);
    return true;
  }

  const roomId = chatState.selectedRoomId;

  if (bookingIntent && !roomId && !roomNumber) {
    // No room chosen yet, so ask for a room before asking for dates
    chatState.bookingStep = 'room';
    appendMessage(messages, 'bot', 'Sure. Which room do you want to book? You can type a room number like Room 533, or search first and click Book on a room card.');
    input.focus();
    return true;
  }

  if (roomNumber && !roomId) {
    chatState.selectedRoomNumber = roomNumber;
    chatState.bookingStep = 'dates';
  }

  const effectiveRoomNumber = roomNumber || chatState.selectedRoomNumber;

  if (!roomId && !effectiveRoomNumber) {
    chatState.bookingStep = 'room';
    appendMessage(messages, 'bot', 'Please choose a room first. You can click Book on any room card, or type a room number like Room 533.');
    input.focus();
    return true;
  }

  if (!dates) {
    // Dates are the last required input before quoting availability and price
    chatState.bookingStep = 'dates';
    appendMessage(messages, 'bot', `Great. What check-in and check-out dates do you want for Room ${effectiveRoomNumber}? Example: 2026-07-10 to 2026-07-13`);
    input.focus();
    return true;
  }

  chatState.bookingStep = 'quote';
  await quoteBooking(messages, chatState, {
    roomId,
    roomNumber: effectiveRoomNumber,
    checkIn: dates.checkIn,
    checkOut: dates.checkOut,
  });
  return true;
}

export async function confirmPendingBooking(messages, chatState) {
  if (!chatState.pendingQuote) {
    appendMessage(messages, 'bot', 'Please ask me to book a room with dates first, then I can confirm it.');
    return;
  }

  const quote = chatState.pendingQuote;
  const loading = appendMessage(messages, 'bot', 'Confirming your booking and rechecking availability...');

  try {
    // Server rechecks availability before creating the actual stay
    const result = await postJson('/api/ai/bookings/confirm', {
      roomId: quote.roomId,
      checkIn: quote.checkIn,
      checkOut: quote.checkOut,
    });
    loading.remove();
    resetBookingState(chatState);

    appendMessage(messages, 'bot', result.message);
  } catch (error) {
    loading.remove();
    appendMessage(messages, 'bot', bookingErrorMessage(error));
  }
}

export async function showCancelableBookings(messages) {
  const loading = appendMessage(messages, 'bot', 'Loading your current bookings...');

  try {
    // GET is enough here because the actual cancellation happens later
    const response = await fetch('/api/ai/bookings', {
      headers: {
        Accept: 'application/json',
      },
    });

    if (!response.ok) {
      throw {
        status: response.status,
        message: null,
      };
    }

    const bookings = await readJsonResponse(response);
    loading.remove();

    if (!bookings || bookings.length === 0) {
      appendMessage(messages, 'bot', 'You do not have any bookings to cancel.');
      return;
    }

    appendMessage(messages, 'bot', 'Which booking should I cancel?');
    appendBookingList(messages, bookings);
  } catch (error) {
    loading.remove();
    appendMessage(messages, 'bot', bookingErrorMessage(error));
  }
}

export async function cancelBooking(messages, stayId) {
  const loading = appendMessage(messages, 'bot', 'Cancelling your selected booking...');

  try {
    const result = await postJson('/api/ai/bookings/cancel', { stayId });
    loading.remove();
    appendMessage(messages, 'bot', result.message);
  } catch (error) {
    loading.remove();
    appendMessage(messages, 'bot', bookingErrorMessage(error));
  }
}

export function resetBookingState(chatState) {
  chatState.selectedRoomId = null;
  chatState.selectedRoomNumber = null;
  chatState.pendingQuote = null;
  chatState.bookingStep = null;
}

async function quoteBooking(messages, chatState, request) {
  const loading = appendMessage(messages, 'bot', 'Checking real room availability and total price...');

  try {
    const quote = await postJson('/api/ai/bookings/quote', request);
    loading.remove();

    if (!quote.available) {
      appendMessage(messages, 'bot', quote.message);
      chatState.pendingQuote = null;
      return;
    }

    chatState.pendingQuote = quote;
    chatState.bookingStep = 'confirm';
    appendMessage(messages, 'bot', quote.message);
    appendBookingQuote(messages, quote);
  } catch (error) {
    loading.remove();
    appendMessage(messages, 'bot', bookingErrorMessage(error));
  }
}

function bookingErrorMessage(error) {
  // Keep authorization errors friendly because the bot is visible before login
  if (error.status === 401 || error.status === 403) {
    if (hasCustomerNavigation()) {
      return 'I could not send the secure booking request. Please refresh this page once, then try the booking again.';
    }

    return 'Please login as a customer first, then come back to the bot to book or cancel a room.';
  }

  return error.message || 'I could not complete that booking request right now. Please check the details and try again.';
}

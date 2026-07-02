import { getCsrfHeaders } from '../../utils/csrf.js';
import { ensureCustomerSession } from './api.js';
import { cancelBooking, confirmPendingBooking, handleBookingMessage, resetBookingState } from './booking.js';
import { buildClarifyingReplies, buildFollowUpReplies } from './replies.js';
import { appendLoginPrompt, appendMessage, appendQuickReplies, appendRooms } from './rendering.js';

export function initChat() {
  // Pages without the floating assistant simply skip this module
  const widget = document.querySelector('[data-ai-chat-widget]');

  if (!widget) {
    return;
  }

  const form = widget.querySelector('[data-ai-chat-form]');

  if (!form) {
    return;
  }

  const toggle = widget.querySelector('[data-ai-chat-toggle]');
  const close = widget.querySelector('[data-ai-chat-close]');
  const windowElement = widget.querySelector('[data-ai-chat-window]');
  const input = form.querySelector('[data-ai-chat-input]');
  const messages = widget.querySelector('[data-ai-chat-messages]');
  const chatState = {
    // Kept in memory so booking can continue across several chat messages
    selectedRoomId: null,
    selectedRoomNumber: null,
    pendingQuote: null,
    bookingStep: null,
  };

  setChatOpen(widget, toggle, windowElement, false);
  appendMessage(messages, 'bot', 'Hi! I am your hotel assistant. How can I help you today?');
  appendQuickReplies(messages, [
    'Cheap room in Antwerp',
    'Spa hotel',
    'Suite under €600',
    'Double room with sea view',
    'Book a room',
    'Cancel booking',
  ], input, form);

  messages.addEventListener('click', async (event) => {
    // Result cards and booking rows use delegated click handling
    const bookButton = event.target.closest('[data-ai-book-room]');
    const cancelButton = event.target.closest('[data-ai-cancel-booking]');
    const confirmButton = event.target.closest('[data-ai-confirm-booking]');

    if (bookButton) {
      if (!await ensureCustomerSession()) {
        resetBookingState(chatState);
        appendLoginPrompt(messages);
        return;
      }

      chatState.selectedRoomId = Number(bookButton.dataset.aiBookRoom);
      chatState.selectedRoomNumber = Number(bookButton.dataset.aiRoomNumber);
      chatState.bookingStep = 'dates';
      appendMessage(messages, 'bot', `Great choice. What check-in and check-out dates do you want for Room ${chatState.selectedRoomNumber}? Example: 2026-07-10 to 2026-07-13`);
      input.focus();
      return;
    }

    if (confirmButton) {
      if (!await ensureCustomerSession()) {
        resetBookingState(chatState);
        appendLoginPrompt(messages);
        return;
      }

      await confirmPendingBooking(messages, chatState);
      return;
    }

    if (cancelButton) {
      if (!await ensureCustomerSession()) {
        appendLoginPrompt(messages);
        return;
      }

      await cancelBooking(messages, Number(cancelButton.dataset.aiCancelBooking));
    }
  });

  toggle.addEventListener('click', () => {
    // Preserve focus behavior so keyboard users land directly in the chat input
    const shouldOpen = windowElement.hidden;
    setChatOpen(widget, toggle, windowElement, shouldOpen);

    if (shouldOpen) {
      input.focus();
    }
  });

  close.addEventListener('click', () => {
    setChatOpen(widget, toggle, windowElement, false);
    toggle.focus();
  });

  form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const message = input.value.trim();

    if (!message) {
      return;
    }

    appendMessage(messages, 'user', message);
    input.value = '';

    if (await handleBookingMessage(message, messages, chatState, input)) {
      // Booking messages are handled locally and should not be sent to Python
      return;
    }

    const loading = appendMessage(messages, 'bot', 'Let me check the best matches...');

    try {
      // Spring enriches the text request with room data before calling Python
      const response = await fetch('/api/ai/chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
          ...getCsrfHeaders(),
        },
        body: JSON.stringify({ message }),
      });

      if (!response.ok) {
        throw new Error('AI room finder is unavailable.');
      }

      const data = await response.json();
      loading.remove();
      appendMessage(messages, 'bot', data.reply || 'Here are the best matches.');

      if (data.rooms && data.rooms.length > 0) {
        appendRooms(messages, data.rooms, true);
        appendQuickReplies(messages, buildFollowUpReplies(message, data), input, form);
      } else {
        appendQuickReplies(messages, buildClarifyingReplies(data), input, form);
      }
    } catch {
      loading.remove();
      appendMessage(messages, 'bot', 'I cannot reach the AI service right now. Please start the Python AI service on port 8001 and try again.');
    }
  });
}

function setChatOpen(widget, toggle, windowElement, isOpen) {
  widget.classList.toggle('is-open', isOpen);
  windowElement.hidden = !isOpen;
  toggle.hidden = isOpen;
  toggle.setAttribute('aria-expanded', String(isOpen));
}

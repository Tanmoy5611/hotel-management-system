import { escapeHtml, formatMoney } from './format.js';
import { renderSuggestion } from './suggestions.js';

export function appendMessage(container, sender, text) {
  // Use textContent for chat messages so user text cannot inject HTML
  const message = document.createElement('div');
  message.className = `ai-chat-message ai-chat-message--${sender}`;
  message.textContent = text;
  container.append(message);
  scrollToBottom(container);

  return message;
}

export function appendRooms(container, rooms, bookable = false) {
  // Room cards use a string renderer because each item has the same fixed shape
  const group = document.createElement('div');
  group.className = 'ai-chat-results';
  group.innerHTML = rooms.map((room) => renderSuggestion(room, { bookable })).join('');
  container.append(group);
  scrollToBottom(container);
}

export function appendBookingQuote(container, quote) {
  const group = document.createElement('div');
  group.className = 'ai-booking-summary';
  group.innerHTML = `
    <strong>Booking summary</strong>
    <span>Room ${quote.roomNumber} - ${escapeHtml(quote.hotelName)}</span>
    <span>${escapeHtml(quote.city)} · ${escapeHtml(quote.roomType)}</span>
    <span>${escapeHtml(quote.checkIn)} to ${escapeHtml(quote.checkOut)} · ${quote.nights} night${quote.nights === 1 ? '' : 's'}</span>
    <span>€${formatMoney(quote.pricePerNight)} per night · Total €${formatMoney(quote.totalPrice)}</span>
    <span>Discount ${formatMoney(quote.discountPercentage)}% · Final €${formatMoney(quote.finalPrice)}</span>
    <button type="button" data-ai-confirm-booking>Confirm booking</button>
  `;
  container.append(group);
  scrollToBottom(container);
}

export function appendBookingList(container, bookings) {
  const group = document.createElement('div');
  group.className = 'ai-booking-list';

  group.innerHTML = bookings.map((booking) => `
    <div class="ai-booking-row">
      <span>
        <strong>Room ${booking.roomNumber} - ${escapeHtml(booking.hotelName)}</strong>
      </span>
      <small>${escapeHtml(booking.checkIn)} to ${escapeHtml(booking.checkOut)} · €${formatMoney(booking.finalPrice)}</small>
      <button type="button" data-ai-cancel-booking="${booking.stayId}">Cancel</button>
    </div>
  `).join('');

  container.append(group);
  scrollToBottom(container);
}

export function appendQuickReplies(container, replies, input, form) {
  // Quick replies reuse the normal submit path to avoid duplicate chat logic
  if (!replies || replies.length === 0) {
    return;
  }

  const group = document.createElement('div');
  group.className = 'ai-chat-chips';

  replies.forEach((reply) => {
    const button = document.createElement('button');
    button.type = 'button';
    button.textContent = reply;
    button.addEventListener('click', () => {
      input.value = reply;
      form.requestSubmit();
    });
    group.append(button);
  });

  container.append(group);
  scrollToBottom(container);
}

export function appendLoginPrompt(container) {
  // Login is shown as a chip so it matches the assistant interaction style
  appendMessage(container, 'bot', 'Please login as a customer first, then come back to the bot to book or cancel a room.');

  const group = document.createElement('div');
  group.className = 'ai-chat-chips';

  const button = document.createElement('button');
  button.type = 'button';
  button.textContent = 'Login';
  button.addEventListener('click', () => {
    window.location.href = '/login';
  });

  group.append(button);
  container.append(group);
  scrollToBottom(container);
}

function scrollToBottom(container) {
  container.scrollTop = container.scrollHeight;
}

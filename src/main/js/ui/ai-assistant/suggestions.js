import { escapeHtml } from './format.js';

export function renderSuggestion(room, options = {}) {
  // Convert the model score into a customer-friendly confidence percentage
  const percent = Math.round((room.score || 0) * 100);
  const price = Number(room.pricePerNight || 0).toFixed(2);
  const bookButton = options.bookable ? `
        <button type="button"
                class="ai-room-card__book"
                data-ai-book-room="${room.roomId}"
                data-ai-room-number="${room.roomNumber}">
          Book
        </button>
      ` : '';

  // Escape dynamic labels because this card is rendered as HTML
  return `
    <article class="ai-room-card">
      <span class="ai-room-card__score">${percent}%</span>
      <span class="ai-room-card__body">
        <a href="/rooms/${room.roomId}">
          <strong>Room ${room.roomNumber} - ${escapeHtml(room.hotelName)}</strong>
        </a>
        <small>${escapeHtml(room.city)} · ${escapeHtml(room.roomType)} · €${price}/night</small>
        <em>${escapeHtml(room.reason || 'Relevant match')}</em>
      </span>
      ${bookButton}
    </article>
  `;
}

import { renderSuggestion } from './suggestions.js';

export function initRecommendations() {
  // Recommendation cards appear only on pages with the dashboard placeholder
  const container = document.querySelector('[data-ai-recommendations]');

  if (!container) {
    return;
  }

  const list = container.querySelector('[data-ai-recommendation-list]');
  const status = container.querySelector('[data-ai-recommendation-status]');

  fetch('/api/ai/recommendations', { headers: { Accept: 'application/json' } })
    .then((response) => {
      if (!response.ok) {
        throw new Error('AI recommendations are unavailable.');
      }

      return response.json();
    })
    .then((data) => {
      const recommendations = data.recommendations || [];

      if (recommendations.length === 0) {
        // Empty history is normal for new customers
        status.textContent = 'Book a room to unlock personal recommendations.';
        return;
      }

      list.innerHTML = recommendations.map((room) => renderSuggestion(room)).join('');
      status.textContent = 'Recommended for you';
    })
    .catch(() => {
      status.textContent = 'AI recommendations are unavailable right now.';
    });
}

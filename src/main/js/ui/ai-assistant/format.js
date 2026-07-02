export function formatMoney(value) {
  return Number(value || 0).toFixed(2);
}

export function escapeHtml(value) {
  // Room and hotel text is inserted into template strings, so escape it first
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

export function toggleText(button) {
  // Bootstrap updates aria-expanded first, then this code updates the label
  setTimeout(() => {
    const icon = button.querySelector('i');
    const label = button.querySelector('span');
    const expanded = button.getAttribute('aria-expanded') === 'true';

    if (expanded) {
      icon.className = 'bi bi-chevron-up me-1';
      label.textContent = 'Read less';
    } else {
      icon.className = 'bi bi-chevron-down me-1';
      label.textContent = 'Read more';
    }
  }, 50);
}

export function registerReadMoreHandler() {
  // Thymeleaf still calls toggleText from inline onclick attributes
  window.toggleText = toggleText;
}
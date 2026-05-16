export function initDescriptionCounter() {
  // Description forms share the same textarea and counter ids
  const textarea = document.getElementById('description-input');
  const counter = document.getElementById('charCounter');

  if (!textarea || !counter) {
    return;
  }

  const max = textarea.maxLength || 4000;

  function updateCounter() {
    // Warning classes make long descriptions visible before the hard limit
    const length = textarea.value.length;

    counter.textContent = `${length} / ${max}`;
    counter.classList.remove('warning', 'danger');

    if (length > max * 0.8) {
      counter.classList.add('warning');
    }

    if (length >= max) {
      counter.classList.add('danger');
    }
  }

  textarea.addEventListener('input', updateCounter);
  updateCounter();
}
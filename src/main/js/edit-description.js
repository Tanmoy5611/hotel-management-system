import '../scss/edit-description.scss';
import { initDescriptionCounter } from './ui/description-word-counter.js';

document.addEventListener('DOMContentLoaded', () => {
  // Shared entry for standalone edit-description pages
  initDescriptionCounter();
});
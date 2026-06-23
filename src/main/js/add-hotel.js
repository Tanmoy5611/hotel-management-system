import '../scss/edit-description.scss';
import { initAddHotelForm } from './api/add-hotel-api.js';
import { initDescriptionCounter } from './ui/description-word-counter.js';

document.addEventListener('DOMContentLoaded', () => {
  // Starts the counter and REST form behavior for this page
  initDescriptionCounter();
  initAddHotelForm();
});

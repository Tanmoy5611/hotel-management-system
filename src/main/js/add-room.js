import '../scss/edit-description.scss';
import { initAddRoomForm } from './api/add-room-api.js';
import { initDescriptionCounter } from './ui/description-word-counter.js';

document.addEventListener('DOMContentLoaded', () => {
  // Page entry for the Add Room form and its REST submit
  initDescriptionCounter();
  initAddRoomForm();
});

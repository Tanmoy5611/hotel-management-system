import { initGuestRoomDates } from './ui/guest-form.js';
import { initGuestValidation } from './validation/guest-validation.js';

document.addEventListener('DOMContentLoaded', () => {
  // Page entry for the Add Guest form
  const form = document.getElementById('guest-form');

  initGuestRoomDates();
  initGuestValidation(form);
});
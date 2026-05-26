import '../scss/room-detail.scss';
import { initRoomDescriptionEditor } from './api/room-description-api.js';
import { initRoomDeleteButtons } from './api/rooms-api.js';
import { initDescriptionCounter } from './ui/description-word-counter.js';
import { registerReadMoreHandler } from './ui/read-more.js';
import { initCreatedRoomToast } from './ui/room-create-toast.js';

// The inline read-more buttons need this before the user clicks them
registerReadMoreHandler();

document.addEventListener('DOMContentLoaded', () => {
  // Room detail combines REST actions, toasts, counters, and edit behavior
  initRoomDeleteButtons();
  initCreatedRoomToast();
  initRoomDescriptionEditor();
  initDescriptionCounter();
});

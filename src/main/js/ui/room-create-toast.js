import { Toast } from 'bootstrap';
import { showAnimatedToast } from '../animations/toast-animation.js';

export function initCreatedRoomToast() {
  // The toast is present only after redirecting from a successful room create
  const toastElement = document.getElementById('createdToast');

  if (!toastElement) {
    return;
  }

  showAnimatedToast(toastElement, Toast);

  const url = new URL(window.location);
  url.searchParams.delete('created');

  // Remove the flag so refreshing the page does not show the success toast again
  window.history.replaceState({}, document.title, url.pathname);
}
import { getCsrfHeaders } from '../utils/csrf.js';

export function initRoomDeleteButtons() {
  // One module handles delete buttons on both the list and detail pages
  const deleteButtons = document.querySelectorAll('.delete-room-btn');

  deleteButtons.forEach((button) => {
    const roomId = button.getAttribute('data-room-id');

    button.addEventListener('click', async () => {
      try {
        const response = await fetch(`/api/rooms/${roomId}`, {
          method: 'DELETE',
          headers: {
            Accept: 'application/json',
            ...getCsrfHeaders(),
          },
        });

        if (response.status === 204) {
          // List pages remove the card, detail pages go back to the room overview
          const roomCard = document.querySelector(`#room-${roomId}`);

          if (roomCard) {
            roomCard.remove();
          } else {
            window.location.href = '/rooms';
          }
        } else if (response.status === 404) {
          const errorBody = await response.json();
          alert(errorBody.message);
        } else if (response.status === 403) {
          alert('You are not allowed to delete this room.');
        } else {
          alert('Unexpected server error.');
        }
      } catch {
        alert('Server connection error.');
      }
    });
  });
}
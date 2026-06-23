import { getCsrfHeaders } from '../utils/csrf.js';

export function initAddRoomForm() {
  // The add-room form is submitted as JSON because the backend exposes a REST endpoint
  const form = document.getElementById('add-room-form');

  if (!form) {
    return;
  }

  form.addEventListener('submit', async (event) => {
    event.preventDefault();

    if (!form.checkValidity()) {
      form.classList.add('was-validated');
      return;
    }

    const formData = new FormData(form);

    // The API expects typed values instead of raw input strings
    const payload = {
      number: Number.parseInt(formData.get('number'), 10),
      type: formData.get('type'),
      pricePerNight: Number.parseFloat(formData.get('pricePerNight')),
      seaView: form.querySelector('#seaView').checked,
      photoUrl: formData.get('photoUrl'),
      description: formData.get('description'),
      hotelId: formData.get('hotelId'),
    };

    try {
      const response = await fetch('/api/rooms', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
          ...getCsrfHeaders(),
        },
        body: JSON.stringify(payload),
      });

      if (response.status === 201) {
        // Load the response resource through its Location header
        const location = response.headers.get('Location');
        const createdRoom = await loadCreatedRoom(location);

        showCreatedRoom(createdRoom);
        form.reset();
      } else if (response.status === 400) {
        const errorBody = await response.json();
        alert(errorBody.message || 'Validation failed.');
      } else if (response.status === 409) {
        const errorBody = await response.json();
        const errorDiv = document.getElementById('formError');
        const errorMessage = document.getElementById('formErrorMessage');

        errorMessage.textContent = errorBody.message;
        errorDiv.classList.remove('d-none');
      } else {
        alert(`Server error: ${response.status}`);
      }
    } catch {
      alert('Could not connect to server.');
    }
  });
}

async function loadCreatedRoom(location) {
  if (!location) {
    throw new Error('Created room location is missing.');
  }

  const response = await fetch(location, {
    headers: {
      Accept: 'application/json',
    },
  });

  if (response.status !== 200) {
    throw new Error('Could not load the created room.');
  }

  return response.json();
}

function showCreatedRoom(room) {
  const result = document.getElementById('createdRoom');
  const message = document.getElementById('createdRoomMessage');
  const link = document.getElementById('createdRoomLink');

  // Use the API response to update the page without a navigation
  message.textContent = `Room ${room.number} at ${room.hotelName} was created.`;
  link.href = `/rooms/${room.id}`;
  result.classList.remove('d-none');
}
import { getCsrfHeaders } from '../utils/csrf.js';

export function initAddHotelForm() {
  // This page keeps the normal MVC form as a fallback when JavaScript is unavailable
  const form = document.getElementById('add-hotel-form');

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
    // Convert browser form values into the JSON shape expected by the API
    const payload = {
      name: formData.get('name'),
      city: formData.get('city'),
      country: formData.get('country'),
      openedOn: formData.get('openedOn'),
      stars: Number.parseInt(formData.get('stars'), 10),
      hasSpa: form.querySelector('#hasSpa').checked,
      imageUrl: formData.get('imageUrl'),
      description: formData.get('description'),
    };

    try {
      const response = await fetch('/api/hotels', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
          ...getCsrfHeaders(),
        },
        body: JSON.stringify(payload),
      });

      if (response.status === 201) {
        // The API returns the created hotel so the page can update immediately
        const hotel = await response.json();
        showCreatedHotel(hotel);
        form.reset();
        form.classList.remove('was-validated');
        hideError();
        return;
      }

      const errorBody = await response.json();
      showError(errorBody.message || 'Hotel could not be created.');
    } catch {
      showError('Could not connect to the server.');
    }
  });
}

// showCreatedHotel() is called by the API response
function showCreatedHotel(hotel) {
  const result = document.getElementById('createdHotel');
  const message = document.getElementById('createdHotelMessage');
  const link = document.getElementById('createdHotelLink');

  // Link to the normal MVC detail page for the new hotel
  message.textContent = `${hotel.name} was created.`;
  link.href = `/hotels/${hotel.hotelId}`;
  result.classList.remove('d-none');
}

// showError() is called by the API response
function showError(message) {
  const error = document.getElementById('formError');
  const errorMessage = document.getElementById('formErrorMessage');

  errorMessage.textContent = message;
  error.classList.remove('d-none');
}

function hideError() {
  document.getElementById('formError').classList.add('d-none');
}

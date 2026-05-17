import { Toast } from 'bootstrap';
import { showAnimatedToast } from '../animations/toast-animation.js';
import { getCsrfHeaders } from '../utils/csrf.js';

export function initRoomDescriptionEditor() {
  // This editor exists only on the room detail page for admins
  const editBtn = document.getElementById('edit-btn');
  const saveBtn = document.getElementById('save-btn');
  const cancelBtn = document.getElementById('cancel-btn');
  const descFirst = document.getElementById('desc-first');
  const descFull = document.getElementById('room-desc-full');
  const descriptionInput = document.getElementById('description-input');
  const editSection = document.getElementById('edit-section');
  const displaySection = document.getElementById('display-section');

  if (!editBtn) {
    return;
  }

  editBtn.addEventListener('click', () => {
    descriptionInput.value = `${descFirst.innerText.trim()} ${descFull.innerText.trim()}`;
    displaySection.classList.add('d-none');
    editSection.classList.remove('d-none');
  });

  cancelBtn.addEventListener('click', () => {
    editSection.classList.add('d-none');
    displaySection.classList.remove('d-none');
  });

  saveBtn.addEventListener('click', async () => {
    // PATCH keeps the request focused on the description field only
    const roomId = saveBtn.getAttribute('data-room-id');
    const newDescription = descriptionInput.value.trim();

    try {
      const response = await fetch(`/api/rooms/${roomId}/description`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          ...getCsrfHeaders(),
        },
        body: JSON.stringify({
          description: newDescription,
        }),
      });

      if (response.status === 204) {
        updateDescriptionText(newDescription, descFirst, descFull);
        editSection.classList.add('d-none');
        displaySection.classList.remove('d-none');
        showAnimatedToast(document.getElementById('updateToast'), Toast);
      } else {
        alert('Error saving description');
      }
    } catch {
      alert('Network error');
    }
  });
}

function updateDescriptionText(newDescription, descFirst, descFull) {
  // The page displays the first sentence separately from the collapsible text
  const dotIndex = newDescription.indexOf('.');
  const firstPart = dotIndex !== -1 ? newDescription.substring(0, dotIndex + 1) : newDescription;
  const secondPart = dotIndex !== -1 ? newDescription.substring(dotIndex + 1) : '';

  descFirst.innerText = firstPart;

  const fullTextSpan = descFull.querySelector('span');
  if (fullTextSpan) {
    fullTextSpan.innerText = secondPart;
  }
}
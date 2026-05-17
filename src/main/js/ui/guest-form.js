export function initGuestRoomDates() {
  // Booking dates are only useful when a room is selected
  const roomSelect = document.getElementById('roomSelect');
  const checkIn = document.getElementById('checkIn');
  const checkOut = document.getElementById('checkOut');

  if (!roomSelect || !checkIn || !checkOut) {
    return;
  }

  function toggleDateFields() {
    const roomSelected = roomSelect.value !== '';

    checkIn.disabled = !roomSelected;
    checkOut.disabled = !roomSelected;

    if (!roomSelected) {
      checkIn.value = '';
      checkOut.value = '';
    }
  }

  toggleDateFields();
  roomSelect.addEventListener('change', toggleDateFields);
}
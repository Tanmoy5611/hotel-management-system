document.addEventListener('DOMContentLoaded', () => {
  const cancelModal = document.getElementById('cancelBookingModal');

  if (!cancelModal) {
    return;
  }

  const cancelForm = cancelModal.querySelector('[data-booking-cancel-form]');
  const guestElement = cancelModal.querySelector('[data-booking-modal-guest]');
  const roomElement = cancelModal.querySelector('[data-booking-modal-room]');
  const datesElement = cancelModal.querySelector('[data-booking-modal-dates]');

  // Fills the shared cancel modal from the clicked booking button
  cancelModal.addEventListener('show.bs.modal', (event) => {
    const trigger = event.relatedTarget;

    if (!trigger) {
      return;
    }

    cancelForm.action = trigger.dataset.bookingAction;
    guestElement.textContent = trigger.dataset.bookingGuest;
    roomElement.textContent = trigger.dataset.bookingRoom;
    datesElement.textContent = trigger.dataset.bookingDates;
  });
});

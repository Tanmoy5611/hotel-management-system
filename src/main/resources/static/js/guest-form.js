document.addEventListener("DOMContentLoaded", () => {

    const roomSelect = document.getElementById("roomSelect");
    const checkIn = document.getElementById("checkIn");
    const checkOut = document.getElementById("checkOut");

    // safety check
    if (!roomSelect || !checkIn || !checkOut) return;

    // function to toggle date fields
    function toggleDateFields() {
        const roomSelected = roomSelect.value !== "";

        checkIn.disabled = !roomSelected;
        checkOut.disabled = !roomSelected;

        // clear values if disabled
        if (!roomSelected) {
            checkIn.value = "";
            checkOut.value = "";
        }
    }

    // run on page load
    toggleDateFields();

    // listen for changes
    roomSelect.addEventListener("change", toggleDateFields);
});
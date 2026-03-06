document.addEventListener("DOMContentLoaded", function () {
    const textarea = document.getElementById("description-input");
    const counter = document.getElementById("charCounter");

    // Stop script if elements are not present
    if (!textarea || !counter) return;

    // Maximum characters allowed
    const max = textarea.maxLength || 4000;

    function updateCounter() {
        const length = textarea.value.length;

        // Display current character count
        counter.textContent = `${length} / ${max}`;

        // Remove previous warning styles
        counter.classList.remove("warning", "danger");

        // Show warning when approaching limit
        if (length > max * 0.8) {
            counter.classList.add("warning");
        }

        // Show danger when limit is reached
        if (length >= max) {
            counter.classList.add("danger");
        }
    }

    // Update counter while user is typing
    textarea.addEventListener("input", updateCounter);

    // Run once when page loads
    updateCounter();
});
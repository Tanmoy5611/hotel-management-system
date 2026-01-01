// Toggles the visibility of discount field (based on whether VIP checkbox is selected)
function toggleDiscount() {
    const vipBox = document.getElementById("vipBox");
    const discountDiv = document.getElementById("discountField");

    if (vipBox.checked) {
        // Show discount field when VIP is checked
        discountDiv.classList.remove("d-none");
    } else {
        // Hide discount field when VIP is unchecked
        discountDiv.classList.add("d-none");
    }
}

// This ensures correct visibility if the form reloads with validation errors
document.addEventListener("DOMContentLoaded", toggleDiscount);
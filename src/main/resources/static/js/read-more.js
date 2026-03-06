function toggleText(button) {

    // Small delay so Bootstrap updates aria-expanded first
    setTimeout(() => {

        const icon = button.querySelector("i");
        const label = button.querySelector("span");

        // Check if the collapse is currently expanded
        const expanded = button.getAttribute("aria-expanded") === "true";

        // Update icon and text depending on state
        if (expanded) {
            icon.className = "bi bi-chevron-up me-1";
            label.textContent = "Read less";
        } else {
            icon.className = "bi bi-chevron-down me-1";
            label.textContent = "Read more";
        }

    }, 50);
}
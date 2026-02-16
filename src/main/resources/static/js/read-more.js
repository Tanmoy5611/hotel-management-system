function toggleText(button) {
    // Small delay to allow Bootstrap's collapse to update the 'aria-expanded' attribute
    setTimeout(() => {
        const icon = button.querySelector("i");
        const label = button.querySelector("span");
        const isExpanded = button.getAttribute("aria-expanded") === "true";

        if (isExpanded) {
            icon.className = "bi bi-chevron-up me-1";
            label.textContent = "Read less";
        } else {
            icon.className = "bi bi-chevron-down me-1";
            label.textContent = "Read more";
        }
    }, 50);
}
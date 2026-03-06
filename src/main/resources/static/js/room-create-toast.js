document.addEventListener("DOMContentLoaded", () => {

    // Get the toast element from the page
    const toastElement = document.getElementById("createdToast");

    // Only run if the toast exists (room was just created)
    if (toastElement) {

        // Create a Bootstrap toast instance
        const toast = new bootstrap.Toast(toastElement, {
            delay: 3000   // toast disappears after 3 seconds
        });

        toast.show();

        // Remove "?created=true" from the URL after showing the toast
        const url = new URL(window.location);
        url.searchParams.delete("created");

        // Update browser history without reloading the page
        window.history.replaceState({}, document.title, url.pathname);

    }

});
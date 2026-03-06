document.addEventListener("DOMContentLoaded", () => {

    // Get buttons from the page
    const editBtn = document.getElementById("edit-btn");
    const saveBtn = document.getElementById("save-btn");
    const cancelBtn = document.getElementById("cancel-btn");

    // Elements showing the description
    const descFirst = document.getElementById("desc-first");
    const descFull = document.getElementById("room-desc-full");

    // Textarea used for editing the description
    const descriptionInput = document.getElementById("description-input");

    // Sections for display mode and edit mode
    const editSection = document.getElementById("edit-section");
    const displaySection = document.getElementById("display-section");

    // Stop script if edit button does not exist
    if (!editBtn) return;


    // Edit button - swtich to edit mode
    editBtn.addEventListener("click", () => {
        // Combine first and second part of description
        descriptionInput.value =
            descFirst.innerText.trim() +
            " " +
            descFull.innerText.trim();

        // Hide description view and show edit textarea
        displaySection.classList.add("d-none");
        editSection.classList.remove("d-none");

    });


    // Cancel button - return to display mode
    cancelBtn.addEventListener("click", () => {

        editSection.classList.add("d-none");
        displaySection.classList.remove("d-none");

    });


    // Save button - send patch request to API to update description
    saveBtn.addEventListener("click", async () => {

        const roomId = saveBtn.getAttribute("data-room-id");
        const newDescription = descriptionInput.value.trim();

        try {

            // Call backend API to update description
            const response = await fetch(`/api/rooms/${roomId}/description`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    description: newDescription
                })
            });

            if (response.status === 204) {

                // Split description into first sentence + rest
                const dotIndex = newDescription.indexOf(".");

                let firstPart;
                let secondPart;

                if (dotIndex !== -1) {

                    firstPart = newDescription.substring(0, dotIndex + 1);
                    secondPart = newDescription.substring(dotIndex + 1);

                } else {

                    firstPart = newDescription;
                    secondPart = "";

                }

                // Update text on the page
                descFirst.innerText = firstPart;
                descFull.querySelector("span").innerText = secondPart;

                // Return to display mode
                editSection.classList.add("d-none");
                displaySection.classList.remove("d-none");

                // Toast message
                const toastElement = document.getElementById("updateToast");

                if (toastElement) {
                    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
                    toast.show();
                }

            } else {

                alert("Error saving description");

            }

        } catch (error) {

            alert("Network error");

        }

    });

});
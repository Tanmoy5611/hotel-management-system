document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("add-room-form");
    if (!form) return;

    form.addEventListener("submit", async (event) => {

        event.preventDefault();

        const formData = new FormData(form);

        const payload = {
            number: parseInt(formData.get("number")),
            type: formData.get("type"),
            pricePerNight: parseFloat(formData.get("pricePerNight")),
            seaView: form.querySelector("#seaView").checked,
            photoUrl: formData.get("photoUrl"),
            description: formData.get("description"),
            hotelId: formData.get("hotelId")
        };

        try {
            const response = await fetch("/api/rooms", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",

                        // CSRF header
                    ...getCsrfHeaders()
                },
                body: JSON.stringify(payload)
            });

            if (response.status === 201) {

                const createdRoom = await response.json();

                // Redirect to room detail with success flag
                window.location.href =
                    `/rooms/${createdRoom.id}?created=true`;

            } else if (response.status === 400) {

                const errorBody = await response.json();
                alert(errorBody.message || "Validation failed.");

            } else if (response.status === 409) {

                const errorBody = await response.json();

                // For better Bootstrap UI
                const errorDiv = document.getElementById("formError");
                const errorMessage = document.getElementById("formErrorMessage");

                errorMessage.textContent = errorBody.message;
                errorDiv.classList.remove("d-none");

            } else {
                alert("Server error: " + response.status);
            }

        } catch (error) {
            alert("Could not connect to server.");
        }

    });

});
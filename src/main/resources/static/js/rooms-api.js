document.addEventListener("DOMContentLoaded", () => {

    const deleteButtons = document.querySelectorAll(".delete-room-btn");

    deleteButtons.forEach(button => {

        const roomId = button.getAttribute("data-room-id");

        button.addEventListener("click", async () => {

            try {
                const response = await fetch(`/api/rooms/${roomId}`, {
                    method: "DELETE",
                    headers: {
                        "Accept": "application/json"
                    }
                });

                // SUCCESS - 204 No Content
                if (response.status === 204) {

                    // 1. Rooms list page (card exists)
                    const roomCard = document.querySelector(`#room-${roomId}`);

                    if (roomCard) {
                        roomCard.remove();
                    }
                    // 2. Room detail page (no card → redirect)
                    else {
                        window.location.href = "/rooms";
                    }

                }

                // NOT FOUND - 404 with JSON error body
                else if (response.status === 404) {

                    const errorBody = await response.json();
                    alert(errorBody.message);
                }

                // Other errors
                else {
                    alert("Unexpected server error.");
                }

            } catch (error) {
                alert("Server connection error.");
            }

        });

    });

});
// Handles filtering users in the admin user table

const searchInput = document.getElementById("userSearch");
const table = document.getElementById("usersTable");

searchInput.addEventListener("keyup", function () {

    const filter = searchInput.value.toLowerCase();
    const rows = table.getElementsByTagName("tr");

    for (let i = 1; i < rows.length; i++) {

        const emailCell = rows[i].getElementsByTagName("td")[0];

        if (emailCell) {

            const email = emailCell.textContent.toLowerCase();

            rows[i].style.display =
                email.includes(filter) ? "" : "none";

        }

    }

});
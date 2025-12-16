function toggleDiscount() {
    const vipBox = document.getElementById("vipBox");
    const discountDiv = document.getElementById("discountField");

    if (vipBox.checked) {
        discountDiv.classList.remove("d-none");
    } else {
        discountDiv.classList.add("d-none");
    }
}

document.addEventListener("DOMContentLoaded", toggleDiscount);
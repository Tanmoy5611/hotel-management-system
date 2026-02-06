    document.addEventListener("DOMContentLoaded", function () {
    const textarea = document.getElementById("description");
    const counter = document.getElementById("charCounter");

    if (!textarea || !counter) return;

    const max = textarea.maxLength || 4000;

    function updateCounter() {
    const length = textarea.value.length;
    counter.textContent = `${length} / ${max}`;

    counter.classList.remove("warning", "danger");

    if (length > max * 0.8) {
    counter.classList.add("warning");
}
    if (length >= max) {
    counter.classList.add("danger");
}
}

    // Live typing
    textarea.addEventListener("input", updateCounter);

    // Initial load (important for edit pages)
    updateCounter();
});
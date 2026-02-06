    function toggleText(link) {
    const icon = link.querySelector("i");
    const textNode = link.childNodes[link.childNodes.length - 1];

    if (link.getAttribute("aria-expanded") === "true") {
    icon.className = "bi bi-chevron-down me-1";
    textNode.textContent = " Read more";
} else {
    icon.className = "bi bi-chevron-up me-1";
    textNode.textContent = " Read less";
}
}

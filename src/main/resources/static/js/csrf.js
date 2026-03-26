function getCsrfHeaders() {
    return {
        [document.querySelector('meta[name="_csrf_header"]').content]:
        document.querySelector('meta[name="_csrf"]').content
    };
}
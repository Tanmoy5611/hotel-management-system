// Bootstrap custom form validation script (Prevents form submission if HTML5 validation fails)
(() => {
    'use strict';

    // Select all forms that require validation
    const forms = document.querySelectorAll('.needs-validation');
    // Loop over each form
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {

            // If form is invalid, stop submission
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            // Add Bootstrap validation styles
            form.classList.add('was-validated');
        }, false);
    });
})();
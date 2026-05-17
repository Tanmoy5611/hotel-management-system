export function initBootstrapValidation() {
  // This keeps the original Bootstrap validation behavior for simple forms
  const forms = document.querySelectorAll('.needs-validation');

  forms.forEach((form) => {
    form.addEventListener('submit', (event) => {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }

      form.classList.add('was-validated');
    });
  });
}
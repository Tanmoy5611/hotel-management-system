export function togglePassword() {
  // The same helper works for login and user creation forms
  const passwordField = document.getElementById('passwordField')
    ?? document.getElementById('registerPasswordField');
  const eyeIcon = document.getElementById('eyeIcon') ?? document.getElementById('registerEyeIcon');

  if (!passwordField || !eyeIcon) {
    return;
  }

  if (passwordField.type === 'password') {
    passwordField.type = 'text';
    eyeIcon.classList.remove('bi-eye');
    eyeIcon.classList.add('bi-eye-slash');
  } else {
    passwordField.type = 'password';
    eyeIcon.classList.remove('bi-eye-slash');
    eyeIcon.classList.add('bi-eye');
  }
}

export function registerPasswordToggle() {
  // Thymeleaf buttons call this function from onclick attributes
  window.togglePassword = togglePassword;
}

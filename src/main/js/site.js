import 'bootstrap';
import '../scss/site.scss';
import { initBootstrapValidation } from './validation/bootstrap-validation.js';

// Site is the shared bundle loaded by every Thymeleaf page
const htmlElement = document.documentElement;
const currentTheme = getSavedTheme()
  ?? (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');

htmlElement.setAttribute('data-bs-theme', currentTheme);
htmlElement.style.colorScheme = currentTheme;

document.addEventListener('DOMContentLoaded', () => {
  // Shared Bootstrap-style validation stays available for older forms
  initBootstrapValidation();
  initThemeToggle(currentTheme);
});

function initThemeToggle(theme) {
  // The theme toggle is optional because not every layout has the button
  const themeToggle = document.getElementById('themeToggle');

  if (!themeToggle) {
    return;
  }

  const themeIcon = themeToggle.querySelector('i');
  updateIcon(themeIcon, theme);

  themeToggle.addEventListener('click', () => {
    const newTheme = htmlElement.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';

    htmlElement.setAttribute('data-bs-theme', newTheme);
    htmlElement.style.colorScheme = newTheme;
    saveTheme(newTheme);
    updateIcon(themeIcon, newTheme);
  });
}

function getSavedTheme() {
  try {
    return localStorage.getItem('theme');
  } catch {
    return null;
  }
}

function saveTheme(theme) {
  try {
    localStorage.setItem('theme', theme);
  } catch {
    // If localStorage is blocked, the current page still keeps the theme
  }
}

function updateIcon(themeIcon, theme) {
  if (!themeIcon) {
    return;
  }

  if (theme === 'dark') {
    themeIcon.classList.replace('bi-moon-fill', 'bi-sun-fill');
  } else {
    themeIcon.classList.replace('bi-sun-fill', 'bi-moon-fill');
  }
}
import 'bootstrap';
import '../scss/site.scss';
import { initBootstrapValidation } from './validation/bootstrap-validation.js';

// Site is the shared bundle loaded by every Thymeleaf page
const htmlElement = document.documentElement;
const systemThemeQuery = window.matchMedia('(prefers-color-scheme: dark)');

applyTheme(getThemePreference());

document.addEventListener('DOMContentLoaded', () => {
  // Shared Bootstrap-style validation stays available for older forms
  initBootstrapValidation();
  initThemeControls();
});

systemThemeQuery.addEventListener('change', () => {
  if (getThemePreference() === 'system') {
    applyTheme('system');
    updateThemeButtons('system');
    updateThemeCycleButtons('system');
  }
});

// Wires profile dropdown theme buttons to stored theme preferences
function initThemeControls() {
  const themeButtons = document.querySelectorAll('[data-theme-option]');
  const themeCycleButtons = document.querySelectorAll('[data-theme-cycle]');
  const initialPreference = getThemePreference();

  updateThemeButtons(initialPreference);
  updateThemeCycleButtons(initialPreference);

  themeButtons.forEach((button) => {
    button.addEventListener('click', () => {
      const themePreference = button.dataset.themeOption;

      saveThemePreference(themePreference);
      applyTheme(themePreference);
      updateThemeButtons(themePreference);
      updateThemeCycleButtons(themePreference);
    });
  });

  themeCycleButtons.forEach((button) => {
    button.addEventListener('click', () => {
      const themePreference = getNextThemePreference(getThemePreference());

      saveThemePreference(themePreference);
      applyTheme(themePreference);
      updateThemeButtons(themePreference);
      updateThemeCycleButtons(themePreference);
    });
  });
}

// Applies the resolved Bootstrap theme to the whole document
function applyTheme(themePreference) {
  const resolvedTheme = resolveTheme(themePreference);

  htmlElement.setAttribute('data-bs-theme', resolvedTheme);
  htmlElement.style.colorScheme = resolvedTheme;
}

// Converts light, dark, or system preference into an actual theme name
function resolveTheme(themePreference) {
  if (themePreference === 'system') {
    return systemThemeQuery.matches ? 'dark' : 'light';
  }

  return themePreference === 'dark' ? 'dark' : 'light';
}

// Updates the active visual state for the theme option buttons
function updateThemeButtons(themePreference) {
  document.querySelectorAll('[data-theme-option]').forEach((button) => {
    const isActive = button.dataset.themeOption === themePreference;

    button.classList.toggle('is-active', isActive);
    button.setAttribute('aria-pressed', String(isActive));
  });
}

// Keeps the compact anonymous navbar theme button in sync with the stored preference
function updateThemeCycleButtons(themePreference) {
  const iconByTheme = {
    light: 'bi-sun',
    dark: 'bi-moon',
    system: 'bi-display',
  };
  const labelByTheme = {
    light: 'Theme: Light',
    dark: 'Theme: Dark',
    system: 'Theme: System',
  };

  document.querySelectorAll('[data-theme-cycle]').forEach((button) => {
    const icon = button.querySelector('i');

    button.setAttribute('title', labelByTheme[themePreference]);
    button.setAttribute('aria-label', labelByTheme[themePreference]);

    if (icon) {
      icon.className = `bi ${iconByTheme[themePreference]}`;
    }
  });
}

// Cycles through the three theme choices from the compact navbar control
function getNextThemePreference(themePreference) {
  const order = ['system', 'light', 'dark'];
  const currentIndex = order.indexOf(themePreference);

  return order[(currentIndex + 1) % order.length];
}

// Reads the saved theme preference and falls back to system
function getThemePreference() {
  try {
    const savedTheme = localStorage.getItem('theme');

    return ['light', 'dark', 'system'].includes(savedTheme) ? savedTheme : 'system';
  } catch {
    return 'system';
  }
}

// Stores the selected theme preference when browser storage is available
function saveThemePreference(themePreference) {
  try {
    localStorage.setItem('theme', themePreference);
  } catch {
    // If localStorage is blocked, the current page still keeps the theme
  }
}
(function () {
    const htmlElement = document.documentElement;

    const currentTheme = getSavedTheme() ||
        (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');

    htmlElement.setAttribute('data-bs-theme', currentTheme);
    htmlElement.style.colorScheme = currentTheme;

    document.addEventListener('DOMContentLoaded', () => {
        const themeToggle = document.getElementById('themeToggle');
        if (!themeToggle) {
            return;
        }

        const themeIcon = themeToggle.querySelector('i');
        updateIcon(themeIcon, currentTheme);

        themeToggle.addEventListener('click', () => {
            const newTheme = htmlElement.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';

            htmlElement.setAttribute('data-bs-theme', newTheme);
            htmlElement.style.colorScheme = newTheme;
            saveTheme(newTheme);
            updateIcon(themeIcon, newTheme);
        });
    });

    function getSavedTheme() {
        try {
            return localStorage.getItem('theme');
        } catch (ignored) {
            return null;
        }
    }

    function saveTheme(theme) {
        try {
            localStorage.setItem('theme', theme);
        } catch (ignored) {
            // If localStorage is blocked, the current page still keeps the theme.
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
})();
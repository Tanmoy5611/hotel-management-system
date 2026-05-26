import { initApplicationUserSearch } from './ui/application-user-search.js';

document.addEventListener('DOMContentLoaded', () => {
  // Admin dashboard search is scoped to the rendered user table
  initApplicationUserSearch();
});

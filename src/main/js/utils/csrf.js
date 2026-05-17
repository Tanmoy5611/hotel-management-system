export function getCsrfHeaders() {
  // Pages without CSRF meta tags can still import this helper safely
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]');
  const csrfToken = document.querySelector('meta[name="_csrf"]');

  if (!csrfHeader || !csrfToken) {
    return {};
  }

  return {
    [csrfHeader.content]: csrfToken.content,
  };
}
import { getCsrfHeaders } from '../../utils/csrf.js';

export async function postJson(url, body) {
  // Booking endpoints need CSRF headers because they change server state
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
      ...getCsrfHeaders(),
    },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    throw await createFetchError(response);
  }

  return readJsonResponse(response);
}

export async function createFetchError(response) {
  // Login redirects often return HTML, so normalize them before UI handling
  if (isLoginOrHtmlResponse(response)) {
    return {
      status: response.status || 403,
      message: null,
    };
  }

  try {
    const body = await readJsonResponse(response);
    return {
      status: response.status,
      message: body?.message || body?.error,
    };
  } catch {
    return {
      status: response.status,
      message: null,
    };
  }
}

export async function readJsonResponse(response) {
  // Guard against accidental HTML responses being parsed as JSON
  const contentType = response.headers.get('content-type') || '';

  if (!contentType.toLowerCase().includes('application/json')) {
    throw {
      status: response.status || 0,
      message: null,
    };
  }

  return response.json();
}

export async function ensureCustomerSession() {
  try {
    // The session endpoint keeps booking buttons honest before protected calls
    const response = await fetch('/api/ai/bookings/session', {
      headers: {
        Accept: 'application/json',
      },
    });

    if (!response.ok) {
      if (response.status === 404) {
        return hasCustomerNavigation();
      }
      return false;
    }

    const data = await readJsonResponse(response);
    return data.customer === true || hasCustomerNavigation();
  } catch {
    return hasCustomerNavigation();
  }
}

export function hasCustomerNavigation() {
  // Navbar state is a local fallback when an older backend lacks the session endpoint
  return Boolean(document.querySelector('a[href="/my"], a[href$="/my"], [data-customer-nav]'));
}

function isLoginOrHtmlResponse(response) {
  const contentType = response.headers.get('content-type') || '';
  const url = response.url || '';

  return response.status === 401
    || response.status === 403
    || response.redirected
    || url.includes('/login')
    || contentType.toLowerCase().includes('text/html');
}

// Starts the live weather card on the home page
export function initWeatherWidget() {
  const widget = document.querySelector('[data-weather-widget]');

  if (!widget) {
    return;
  }

  const elements = getWeatherElements(widget);
  const state = {
    currentSearchLocation: '',
    requestId: 0,
  };

  // Load live weather automatically when the home page opens
  loadWeatherForCurrentLocation(elements, state);

  // The button stays available as a manual refresh
  elements.locateButton?.addEventListener('click', () => {
    loadWeatherForCurrentLocation(elements, state);
  });

  // Copies the detected city into the normal hotel search field
  elements.useSearchButton?.addEventListener('click', () => {
    if (!state.currentSearchLocation) {
      return;
    }

    const searchInput = document.querySelector('.booking-search input[name="q"]');

    if (searchInput) {
      searchInput.value = state.currentSearchLocation;
      searchInput.focus();
    }
  });
}

// Collects all weather card elements in one place
function getWeatherElements(widget) {
  return {
    locationElement: widget.querySelector('[data-weather-location]'),
    tempElement: widget.querySelector('[data-weather-temp]'),
    conditionElement: widget.querySelector('[data-weather-condition]'),
    humidityElement: widget.querySelector('[data-weather-humidity]'),
    windElement: widget.querySelector('[data-weather-wind]'),
    sourceElement: widget.querySelector('[data-weather-source]'),
    locateButton: widget.querySelector('[data-weather-locate]'),
    useSearchButton: widget.querySelector('[data-weather-use-search]'),
  };
}

// Reads browser location, calls the backend, and updates the card
async function loadWeatherForCurrentLocation(elements, state) {
  if (!navigator.geolocation) {
    setWeatherMessage(elements, 'Location is not available in this browser');
    return;
  }

  const requestId = state.requestId + 1;
  state.requestId = requestId;
  setWeatherMessage(elements, 'Reading your live location...');
  elements.locateButton.disabled = true;

  try {
    const { coords } = await getBrowserPositionWithRetry();
    const weather = await fetchCurrentWeather(coords.latitude, coords.longitude, coords.accuracy);

    if (requestId !== state.requestId) {
      return;
    }

    state.currentSearchLocation = weather.searchLocation;
    renderWeather(elements, weather);
    elements.useSearchButton.hidden = !state.currentSearchLocation;
  } catch (error) {
    if (requestId !== state.requestId) {
      return;
    }

    setWeatherMessage(elements, getWeatherErrorMessage(error));
  } finally {
    if (requestId === state.requestId) {
      elements.locateButton.disabled = false;
    }
  }
}

// First tries a fast cached or approximate location, then falls back to precise GPS
async function getBrowserPositionWithRetry() {
  try {
    return await getBrowserPosition({
      enableHighAccuracy: false,
      maximumAge: 10 * 60 * 1000,
      timeout: 6000,
    });
  } catch (error) {
    if (error.code !== 3) {
      throw error;
    }

    return getBrowserPosition({
      enableHighAccuracy: true,
      maximumAge: 5 * 60 * 1000,
      timeout: 12000,
    });
  }
}

// Wraps the callback based browser geolocation API in a promise
function getBrowserPosition(options) {
  return new Promise((resolve, reject) => {
    navigator.geolocation.getCurrentPosition(resolve, reject, options);
  });
}

// Calls our Spring API instead of calling weather providers from the browser
async function fetchCurrentWeather(latitude, longitude, accuracyMeters) {
  try {
    return await fetchCurrentWeatherOnce(latitude, longitude, accuracyMeters);
  } catch (error) {
    await wait(700);
    return fetchCurrentWeatherOnce(latitude, longitude, accuracyMeters);
  }
}

// Performs one backend weather request
async function fetchCurrentWeatherOnce(latitude, longitude, accuracyMeters) {
  const url = new URL('/api/weather/current', window.location.origin);
  const params = new URLSearchParams({
    latitude,
    longitude,
  });

  if (Number.isFinite(accuracyMeters)) {
    params.set('accuracyMeters', accuracyMeters);
  }

  url.search = params;

  const response = await fetch(url);

  if (!response.ok) {
    throw new Error('Weather request failed');
  }

  const data = await response.json();

  if (!data) {
    throw new Error('Weather response was empty');
  }

  return data;
}

// Shows a clearer message for location permission and timeout problems
function getWeatherErrorMessage(error) {
  if (error.code === 1) {
    return 'Allow location to show live weather';
  }

  if (error.code === 3) {
    return 'Location took too long, try again';
  }

  return 'Weather is unavailable right now';
}

// Small delay helper used before retrying a temporary provider failure
function wait(milliseconds) {
  return new Promise((resolve) => {
    window.setTimeout(resolve, milliseconds);
  });
}

// Updates the card after the backend returns live weather
function renderWeather(elements, weather) {
  elements.locationElement.textContent = weather.location;
  elements.tempElement.textContent = `${Math.round(weather.temperatureCelsius)}°C`;
  elements.conditionElement.textContent = weather.condition;
  elements.humidityElement.textContent = `Humidity ${Math.round(weather.relativeHumidity)}%`;
  elements.windElement.textContent = `Wind ${Math.round(weather.windSpeedKmh)} km/h`;
  elements.sourceElement.textContent = getWeatherSourceText(weather);
}

// Resets the card when permission or provider calls fail
function setWeatherMessage(elements, message) {
  elements.conditionElement.textContent = message;
  elements.tempElement.textContent = '--';
  elements.humidityElement.textContent = 'Humidity --';
  elements.windElement.textContent = 'Wind --';
  elements.sourceElement.textContent = 'Waiting for precise location';
  elements.useSearchButton.hidden = true;
}

// Shows provider and browser accuracy so location problems are visible
function getWeatherSourceText(weather) {
  const sourceParts = [weather.provider || 'Weather provider'];

  if (Number.isFinite(weather.accuracyMeters)) {
    sourceParts.push(`location about ${Math.round(weather.accuracyMeters)} m`);
  }

  return sourceParts.join(' | ');
}

export function isBookingIntent(message) {
  return /\b(book|booking|reserve|reservation)\b/i.test(message);
}

export function isCancelIntent(message) {
  return /\b(cancel|delete)\b/i.test(message) && /\b(booking|reservation|room|stay)\b/i.test(message);
}

export function isConfirmIntent(message) {
  return /\b(confirm|yes|book it|finalize|go ahead)\b/i.test(message);
}

export function extractDateRange(message) {
  // Prefer ISO dates because the Spring API receives LocalDate values
  const isoDates = [...message.matchAll(/\b(\d{4}-\d{2}-\d{2})\b/g)].map((match) => match[1]);
  if (isoDates.length >= 2) {
    return { checkIn: isoDates[0], checkOut: isoDates[1] };
  }

  const europeanDates = [...message.matchAll(/\b(\d{1,2})[/-](\d{1,2})[/-](\d{4})\b/g)]
    .map((match) => `${match[3]}-${match[2].padStart(2, '0')}-${match[1].padStart(2, '0')}`);

  if (europeanDates.length >= 2) {
    return { checkIn: europeanDates[0], checkOut: europeanDates[1] };
  }

  return null;
}

export function extractRoomNumber(message) {
  // Allow both "Room 204" and a bare number during the booking flow
  const explicitRoom = message.match(/\broom\s*#?\s*(\d{1,5})\b/i);
  if (explicitRoom) {
    return Number(explicitRoom[1]);
  }

  const onlyNumber = message.match(/^\s*(\d{1,5})\s*$/);
  return onlyNumber ? Number(onlyNumber[1]) : null;
}

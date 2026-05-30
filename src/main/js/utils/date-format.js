import { DateTime } from 'luxon';

// Luxon keeps date parsing reliable before comparing form dates
export function formatDisplayDate(value) {
  const date = DateTime.fromISO(value);
  return date.isValid ? date.toFormat('dd/MM/yyyy') : value;
}

export function isFutureDate(value) {
  const date = DateTime.fromISO(value);
  return date.isValid && date.startOf('day') > DateTime.now().startOf('day');
}

export function isAfterDate(firstValue, secondValue) {
  const firstDate = DateTime.fromISO(firstValue);
  const secondDate = DateTime.fromISO(secondValue);

  return firstDate.isValid && secondDate.isValid && firstDate > secondDate;
}

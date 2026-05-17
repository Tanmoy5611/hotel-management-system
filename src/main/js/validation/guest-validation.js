import Joi from 'joi';
import { formatDisplayDate, isAfterDate, isFutureDate } from '../utils/date-format.js';

// Joi schema keeps all guest form rules in one readable place
const guestSchema = Joi.object({
  fullName: Joi.string().trim().min(3).max(80).required().messages({
    'string.empty': 'Full name is required.',
    'string.min': 'Full name must contain at least 3 characters.',
    'string.max': 'Full name must be shorter than 80 characters.',
  }),
  email: Joi.string().trim().email({ tlds: false }).required().messages({
    'string.empty': 'Email is required.',
    'string.email': 'Enter a valid email address.',
  }),
  dob: Joi.string().required().custom((value, helpers) => {
    if (isFutureDate(value)) {
      return helpers.error('date.future');
    }

    return value;
  }).messages({
    'string.empty': 'Date of birth is required.',
    'date.future': 'Date of birth cannot be in the future.',
  }),
  avatarUrl: Joi.string().trim().uri({ scheme: ['http', 'https'] }).allow('').messages({
    'string.uri': 'Avatar URL must start with http:// or https://.',
  }),
  discountPercentage: Joi.number().min(0).max(100).allow(null).messages({
    'number.min': 'Discount cannot be negative.',
    'number.max': 'Discount cannot be higher than 100%.',
  }),
  roomId: Joi.string().allow(''),
  checkIn: Joi.string().allow(''),
  checkOut: Joi.string().allow(''),
}).custom((value, helpers) => {
  const hasRoom = value.roomId !== '';
  const hasCheckIn = value.checkIn !== '';
  const hasCheckOut = value.checkOut !== '';

  if (hasRoom && (!hasCheckIn || !hasCheckOut)) {
    return helpers.error('booking.missingDates');
  }

  if (!hasRoom && (hasCheckIn || hasCheckOut)) {
    return helpers.error('booking.roomRequired');
  }

  if (hasCheckIn && hasCheckOut && !isAfterDate(value.checkOut, value.checkIn)) {
    return helpers.error('booking.invalidRange');
  }

  return value;
}).messages({
  'booking.missingDates': 'Choose both check-in and check-out dates when assigning a room.',
  'booking.roomRequired': 'Select a room before entering booking dates.',
  'booking.invalidRange': 'Check-out must be after check-in.',
});

const fieldNames = [
  'fullName',
  'email',
  'dob',
  'avatarUrl',
  'discountPercentage',
  'roomId',
  'checkIn',
  'checkOut',
];

export function initGuestValidation(form) {
  // The form can be missing when the bundle is loaded on a redirected page
  if (!form) {
    return;
  }

  // Validate while typing so the user gets feedback before submitting
  fieldNames.forEach((fieldName) => {
    const field = findField(form, fieldName);
    field?.addEventListener('input', () => validateGuestForm(form, { showAll: false }));
    field?.addEventListener('change', () => validateGuestForm(form, { showAll: false }));
  });

  form.addEventListener('submit', (event) => {
    const valid = validateGuestForm(form, { showAll: true });

    if (!valid) {
      event.preventDefault();
      event.stopPropagation();
    }
  });
}

function validateGuestForm(form, options = {}) {
  // Convert is enabled so number inputs are validated as numbers
  const data = readGuestForm(form);
  const result = guestSchema.validate(data, {
    abortEarly: false,
    convert: true,
  });

  clearErrors(form);

  if (!result.error) {
    return true;
  }

  result.error.details.forEach((detail) => {
    const fieldName = detail.path[0] ?? 'checkOut';
    showFieldError(form, fieldName, detail.message);
  });

  if (options.showAll) {
    form.classList.add('was-validated');
  }

  return false;
}

function readGuestForm(form) {
  // Reading through FormData would ignore disabled date inputs in this form
  return {
    fullName: getValue(form, 'fullName'),
    email: getValue(form, 'email'),
    dob: getValue(form, 'dob'),
    avatarUrl: getValue(form, 'avatarUrl'),
    discountPercentage: getNumberValue(form, 'discountPercentage'),
    roomId: getValue(form, 'roomId'),
    checkIn: getValue(form, 'checkIn'),
    checkOut: getValue(form, 'checkOut'),
  };
}

function getValue(form, fieldName) {
  return findField(form, fieldName)?.value.trim() ?? '';
}

function getNumberValue(form, fieldName) {
  const value = getValue(form, fieldName);
  return value === '' ? null : Number(value);
}

function findField(form, fieldName) {
  return form.querySelector(`[name="${fieldName}"]`);
}

function clearErrors(form) {
  // Old client-side errors must be cleared before showing the next result
  fieldNames.forEach((fieldName) => {
    const field = findField(form, fieldName);
    const feedback = getFeedbackElement(field);

    field?.classList.remove('is-invalid');
    field?.setCustomValidity('');

    if (feedback) {
      feedback.textContent = '';
    }
  });
}

function showFieldError(form, fieldName, message) {
  // Object-level booking errors are shown near the check-out input
  const field = findField(form, fieldName) ?? findField(form, 'checkOut');
  const feedback = getFeedbackElement(field);

  if (!field) {
    return;
  }

  field.classList.add('is-invalid');
  field.setCustomValidity(message);

  if (feedback) {
    feedback.textContent = message.includes('Check-out') && fieldName === 'checkOut'
      ? `${message} Selected check-in: ${formatDisplayDate(getValue(form, 'checkIn'))}.`
      : message;
  }
}

function getFeedbackElement(field) {
  // Feedback elements are created by JavaScript so the Thymeleaf markup stays clean
  if (!field) {
    return null;
  }

  const wrapper = field.closest('.col-12, .col-md-6') ?? field.parentElement;
  let feedback = wrapper.querySelector('.client-validation-error');

  if (!feedback) {
    feedback = document.createElement('div');
    feedback.className = 'invalid-feedback client-validation-error';
    wrapper.appendChild(feedback);
  }

  return feedback;
}
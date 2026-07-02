export function buildFollowUpReplies(message, data) {
  // Missing filters become helpful next-step chips after a successful search
  const filters = data.filters || {};
  const replies = [];

  if (!filters.city) {
    replies.push('Focus on Antwerp');
  }
  if (!filters.hasSpa) {
    replies.push(`${message} with spa`);
  }
  if (!filters.maxPrice) {
    replies.push(`${message} under €200`);
  } else {
    replies.push('Show cheaper rooms');
  }
  if (filters.roomType !== 'SUITE') {
    replies.push('Show suites only');
  }

  return replies.slice(0, 4);
}

export function buildClarifyingReplies(data) {
  // No-match responses guide the user toward searchable room criteria
  const filters = data.filters || {};
  const replies = [];

  if (!filters.city) {
    replies.push('Antwerp');
    replies.push('Ghent');
  }
  if (!filters.roomType) {
    replies.push('Double room');
    replies.push('Suite');
  }
  if (!filters.maxPrice) {
    replies.push('Under €200');
  }

  return replies.length > 0 ? replies.slice(0, 4) : [
    'Cheap room',
    'Luxury suite',
    'Spa hotel',
  ];
}

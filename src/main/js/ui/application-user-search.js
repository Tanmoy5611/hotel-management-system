export function initApplicationUserSearch() {
  // Admin filtering stays client-side because the table is already rendered
  const searchInput = document.getElementById('userSearch');
  const table = document.getElementById('usersTable');

  if (!searchInput || !table) {
    return;
  }

  searchInput.addEventListener('keyup', () => {
    const filter = searchInput.value.toLowerCase();
    const rows = table.getElementsByTagName('tr');

    for (let index = 1; index < rows.length; index++) {
      const emailCell = rows[index].getElementsByTagName('td')[0];

      if (emailCell) {
        const email = emailCell.textContent.toLowerCase();
        rows[index].style.display = email.includes(filter) ? '' : 'none';
      }
    }
  });
}

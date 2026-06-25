export function initApplicationUserSearch() {
  // Admin filtering stays client-side because the table is already rendered
  const searchInput = document.getElementById('userSearch');
  const roleFilter = document.getElementById('roleFilter');
  const table = document.getElementById('usersTable');

  if (!searchInput || !table) {
    return;
  }

  const filterRows = () => {
    const filter = searchInput.value.toLowerCase();
    // Role dropdown filters ADMIN, STAFF, and CUSTOMER rows
    const selectedRole = roleFilter ? roleFilter.value : '';
    const rows = table.getElementsByTagName('tr');

    for (let index = 1; index < rows.length; index++) {
      const row = rows[index];
      const emailCell = rows[index].getElementsByTagName('td')[0];

      if (emailCell) {
        const email = emailCell.textContent.toLowerCase();
        const role = row.dataset.role || '';
        const matchesEmail = email.includes(filter);
        const matchesRole = selectedRole === '' || role === selectedRole;

        row.style.display = matchesEmail && matchesRole ? '' : 'none';
      }
    }
  };

  searchInput.addEventListener('keyup', filterRows);

  if (roleFilter) {
    roleFilter.addEventListener('change', filterRows);
  }
}
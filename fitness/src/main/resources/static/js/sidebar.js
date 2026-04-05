/**
 * Sidebar HTML template — included in every app page
 */
function renderSidebar() {
  return `
<aside class="sidebar" id="sidebar">
  <div class="sidebar-logo">
    <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <path d="M22 12h-4l-3 9L9 3l-3 9H2"/>
    </svg>
    Fit<span>Track</span>
  </div>
  <nav class="sidebar-nav">
    <a class="nav-item" href="/dashboard.html" data-page="dashboard.html">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
      Dashboard
    </a>
    <a class="nav-item" href="/activities.html" data-page="activities.html">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
      Activities
    </a>
    <a class="nav-item" href="/nutrition.html" data-page="nutrition.html">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2a10 10 0 1 0 10 10"/><path d="M12 2v10l4.24 4.24"/></svg>
      Nutrition
    </a>
    <a class="nav-item" href="/progress.html" data-page="progress.html">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/></svg>
      Progress
    </a>
    <a class="nav-item" href="/recommendations.html" data-page="recommendations.html">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4"/><path d="M12 8h.01"/></svg>
      Recommendations
    </a>
    <a class="nav-item" href="/profile.html" data-page="profile.html">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
      Profile
    </a>
  </nav>
  <div class="sidebar-footer">
    <div class="user-info">
      <div class="avatar" id="sidebarAvatar">U</div>
      <div style="flex:1; min-width:0;">
        <div class="user-name" id="sidebarName">User</div>
        <div class="user-email" id="sidebarEmail" style="overflow:hidden;text-overflow:ellipsis;white-space:nowrap;"></div>
      </div>
    </div>
    <button class="btn btn-ghost btn-sm btn-full mt-4" id="logoutBtn">Logout</button>
  </div>
</aside>`;
}

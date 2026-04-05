/**
 * FitTrack - Shared app utilities
 */

// Redirect to login if not authenticated
function requireAuth() {
  if (!localStorage.getItem('token')) {
    window.location.href = '/index.html';
  }
}

// Populate sidebar user info
function initSidebar() {
  const firstName = localStorage.getItem('firstName') || '';
  const lastName = localStorage.getItem('lastName') || '';
  const email = localStorage.getItem('email') || '';
  const avatarEl = document.getElementById('sidebarAvatar');
  const nameEl = document.getElementById('sidebarName');
  const emailEl = document.getElementById('sidebarEmail');
  if (avatarEl) avatarEl.textContent = (firstName[0] || 'U').toUpperCase();
  if (nameEl) nameEl.textContent = `${firstName} ${lastName}`.trim() || 'User';
  if (emailEl) emailEl.textContent = email;

  // Highlight active nav item
  const path = window.location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.nav-item').forEach(el => {
    if (el.dataset.page === path) el.classList.add('active');
  });

  // Logout
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) logoutBtn.addEventListener('click', () => {
    localStorage.clear();
    window.location.href = '/index.html';
  });

  // Mobile sidebar toggle
  const toggleBtn = document.getElementById('sidebarToggle');
  const sidebar = document.querySelector('.sidebar');
  if (toggleBtn && sidebar) {
    toggleBtn.addEventListener('click', () => sidebar.classList.toggle('open'));
  }
}

// Format date for display
function formatDate(dateStr) {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

function formatDateTime(dateStr) {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
}

// Format activity type
function formatType(type) {
  if (!type) return '—';
  return type.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
}

// Show/hide loading state on button
function setLoading(btn, loading) {
  if (loading) {
    btn.disabled = true;
    btn._text = btn.innerHTML;
    btn.innerHTML = '<div class="spinner"></div>';
  } else {
    btn.disabled = false;
    btn.innerHTML = btn._text || btn.innerHTML;
  }
}

// Show alert message
function showAlert(containerId, message, type = 'error') {
  const el = document.getElementById(containerId);
  if (!el) return;
  el.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
  setTimeout(() => { if (el) el.innerHTML = ''; }, 4000);
}

// Meal type colors
const MEAL_COLORS = {
  BREAKFAST: 'orange', LUNCH: 'green', DINNER: 'blue',
  SNACK: 'purple', PRE_WORKOUT: 'red', POST_WORKOUT: 'info'
};

// Activity type colors
const ACTIVITY_COLORS = {
  RUNNING: 'green', WALKING: 'blue', CYCLING: 'orange', SWIMMING: 'info',
  WEIGHT_TRAINING: 'red', BENCH_PRESS: 'red', DEADLIFT: 'red',
  SQUAT: 'red', HITT: 'danger', CROSSFIT: 'danger',
  YOGA: 'purple', PILATES: 'purple', STRETCHING: 'purple',
};

function activityColor(type) {
  return ACTIVITY_COLORS[type] || 'blue';
}

// Activity icons
const ACTIVITY_ICONS = {
  RUNNING: '🏃', WALKING: '🚶', CYCLING: '🚴', SWIMMING: '🏊',
  WEIGHT_TRAINING: '🏋️', BENCH_PRESS: '🏋️', DEADLIFT: '🏋️', SQUAT: '🏋️',
  PULL_UP: '💪', PUSH_UP: '💪', PLANK: '🧘', POWERLIFTING: '🏋️',
  YOGA: '🧘', PILATES: '🧘', STRETCHING: '🤸', MEDITATION: '🧠',
  HITT: '⚡', CROSSFIT: '⚡', CARDIO: '❤️', JUMP_ROPE: '⚡',
  ZUMBA: '💃',
};

function activityIcon(type) {
  return ACTIVITY_ICONS[type] || '🏅';
}

// Nepali food database with nutrition per 100g
const NEPALI_FOODS = [
  { name: 'Dal Bhat (Rice + Dal)', calories: 180, protein: 8, carbs: 32, fat: 2, serving: 300 },
  { name: 'Momo (Steamed Veg)', calories: 120, protein: 5, carbs: 18, fat: 3, serving: 150 },
  { name: 'Momo (Steamed Chicken)', calories: 150, protein: 10, carbs: 16, fat: 5, serving: 150 },
  { name: 'Chiura (Beaten Rice)', calories: 350, protein: 7, carbs: 78, fat: 1, serving: 100 },
  { name: 'Dhido (Millet Porridge)', calories: 160, protein: 4, carbs: 33, fat: 1.5, serving: 200 },
  { name: 'Gundruk Soup', calories: 40, protein: 3, carbs: 6, fat: 0.5, serving: 250 },
  { name: 'Sel Roti', calories: 280, protein: 4, carbs: 52, fat: 7, serving: 100 },
  { name: 'Roti (Wheat)', calories: 265, protein: 8, carbs: 50, fat: 3, serving: 100 },
  { name: 'Sukuti (Dried Meat)', calories: 300, protein: 45, carbs: 2, fat: 12, serving: 80 },
  { name: 'Kheer (Rice Pudding)', calories: 150, protein: 5, carbs: 28, fat: 4, serving: 200 },
  { name: 'Lassi (Sweet)', calories: 100, protein: 4, carbs: 16, fat: 2.5, serving: 250 },
  { name: 'Chiya (Milk Tea)', calories: 60, protein: 2, carbs: 9, fat: 2, serving: 200 },
  { name: 'Thukpa (Noodle Soup)', calories: 200, protein: 9, carbs: 35, fat: 4, serving: 350 },
  { name: 'Samosa (Fried)', calories: 260, protein: 5, carbs: 32, fat: 13, serving: 100 },
  { name: 'Jeri (Sweet)', calories: 370, protein: 3, carbs: 68, fat: 10, serving: 100 },
  { name: 'Bhatmas (Roasted Soy)', calories: 430, protein: 35, carbs: 25, fat: 18, serving: 50 },
  { name: 'Nimbu Pani (Lemon Water)', calories: 25, protein: 0, carbs: 7, fat: 0, serving: 250 },
  { name: 'Aalu Tama (Potato Bamboo)', calories: 130, protein: 4, carbs: 24, fat: 3, serving: 200 },
  { name: 'Chicken Curry', calories: 190, protein: 22, carbs: 6, fat: 9, serving: 200 },
  { name: 'Egg (Boiled)', calories: 155, protein: 13, carbs: 1, fat: 11, serving: 100 },
  { name: 'Banana', calories: 89, protein: 1, carbs: 23, fat: 0.3, serving: 120 },
  { name: 'Paneer (Cottage Cheese)', calories: 265, protein: 18, carbs: 3, fat: 20, serving: 100 },
  { name: 'Curd / Dahi', calories: 60, protein: 3.5, carbs: 5, fat: 2.5, serving: 150 },
  { name: 'Saag (Leafy Greens)', calories: 25, protein: 3, carbs: 4, fat: 0.5, serving: 150 },
  { name: 'Cauliflower Curry', calories: 80, protein: 3, carbs: 12, fat: 3, serving: 200 },
];

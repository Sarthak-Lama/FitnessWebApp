/**
 * FitTrack API Client
 */
const API_BASE = '/api';

const api = {
  _getHeaders(includeUserId = true) {
    const headers = { 'Content-Type': 'application/json' };
    const token = localStorage.getItem('token');
    if (token) headers['Authorization'] = `Bearer ${token}`;
    if (includeUserId) {
      const userId = localStorage.getItem('userId');
      if (userId) headers['X-User-ID'] = userId;
    }
    return headers;
  },

  async _request(method, path, body) {
    const res = await fetch(`${API_BASE}${path}`, {
      method,
      headers: this._getHeaders(),
      body: body ? JSON.stringify(body) : undefined,
    });
    if (res.status === 401) {
      localStorage.clear();
      window.location.href = '/index.html';
      return;
    }
    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: `HTTP ${res.status}` }));
      throw new Error(err.message || JSON.stringify(err));
    }
    if (res.status === 204) return null;
    return res.json();
  },

  // Auth
  register: (data) => api._request('POST', '/auth/register', data),
  login:    (data) => api._request('POST', '/auth/login', data),

  // Dashboard
  getDashboard: () => api._request('GET', '/dashboard'),

  // Activities (legacy)
  trackActivity:           (data)       => api._request('POST', '/activities', data),
  getActivities:           ()           => api._request('GET',  '/activities'),
  generateRecommendation:  (data)       => api._request('POST', '/recommendation/generate', data),
  getActivityRecommendations: (actId)   => api._request('GET',  `/recommendation/activity/${actId}`),
  getUserRecommendations:  ()           => {
    const userId = localStorage.getItem('userId') || '';
    return api._request('GET', `/recommendation/user/${userId}`);
  },
  getDailyRecommendation:  ()           => api._request('GET',  '/recommendation/daily'),

  // Nutrition
  logFood:        (data) => api._request('POST',   '/nutrition', data),
  getFoodEntries: ()     => api._request('GET',    '/nutrition'),
  getTodayFood:   ()     => api._request('GET',    '/nutrition/today'),
  deleteFood:     (id)   => api._request('DELETE', `/nutrition/${id}`),

  // Profile
  saveProfile: (data) => api._request('POST', '/profile', data),
  getProfile:  ()     => api._request('GET',  '/profile'),

  // Workout — Gym Sets
  logSet:               (data)    => api._request('POST', '/workout/set', data),
  getTodaySets:         ()        => api._request('GET',  '/workout/sets/today'),
  getAllSets:            ()        => api._request('GET',  '/workout/sets'),
  getSetsByMuscleGroup: (group)   => api._request('GET',  `/workout/sets/muscle/${group}`),
  getExerciseProgress:  ()        => api._request('GET',  '/workout/exercises/progress'),

  // Calorie Summary
  getCalorieSummary: (date) => api._request('GET', date ? `/calorie-summary?date=${date}` : '/calorie-summary'),

  // Workout — Cardio
  logCardio:      (data) => api._request('POST', '/workout/cardio', data),
  getTodayCardio: ()     => api._request('GET',  '/workout/cardio/today'),
  getAllCardio:    ()     => api._request('GET',  '/workout/cardio'),

  // Progress / Weight
  logWeight:    (data) => api._request('POST', '/progress/weight', data),
  getWeightLogs: ()    => api._request('GET',  '/progress/weight'),

  // Progress / Measurements
  logMeasurement:  (data) => api._request('POST', '/progress/measurements', data),
  getMeasurements: ()     => api._request('GET',  '/progress/measurements'),

  // Progress / Weekly
  getWeeklyProgress: () => api._request('GET', '/progress/weekly'),
};

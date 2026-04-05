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
  login: (data) => api._request('POST', '/auth/login', data),

  // Dashboard
  getDashboard: () => api._request('GET', '/dashboard'),

  // Activities
  trackActivity: (data) => api._request('POST', '/activities', data),
  getActivities: () => api._request('GET', '/activities'),
  generateRecommendation: (data) => api._request('POST', '/recommendation/generate', data),
  getActivityRecommendations: (activityId) => api._request('GET', `/recommendation/activity/${activityId}`),
  getUserRecommendations: () => {
    const userId = localStorage.getItem('userId');
    return api._request('GET', `/recommendation/user/${userId}`);
  },

  // Nutrition
  logFood: (data) => api._request('POST', '/nutrition', data),
  getFoodEntries: () => api._request('GET', '/nutrition'),
  getTodayFood: () => api._request('GET', '/nutrition/today'),
  deleteFood: (id) => api._request('DELETE', `/nutrition/${id}`),

  // Profile
  saveProfile: (data) => api._request('POST', '/profile', data),
  getProfile: () => api._request('GET', '/profile'),

  // Progress / Weight
  logWeight: (data) => api._request('POST', '/progress/weight', data),
  getWeightLogs: () => api._request('GET', '/progress/weight'),
};

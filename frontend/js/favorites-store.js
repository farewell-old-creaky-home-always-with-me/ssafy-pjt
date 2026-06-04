const BASE_STORAGE_KEY = 'ssafy-home-favorites';

const FavoritesStore = {
  // 현재 로그인한 사용자의 관심지역 저장소 키 반환
  getStorageKey() {
    // UserStore가 로드되지 않았거나 로그인하지 않은 경우 게스트 키 사용
    if (!window.UserStore || !window.UserStore.isLoggedIn()) {
      return `${BASE_STORAGE_KEY}-guest`;
    }
    const currentUser = window.UserStore.getCurrentUser();
    return `${BASE_STORAGE_KEY}-${currentUser.userId}`;
  },

  load() {
    try {
      const raw = localStorage.getItem(this.getStorageKey());
      return raw ? JSON.parse(raw) : [];
    } catch { return []; }
  },

  save(arr) {
    localStorage.setItem(this.getStorageKey(), JSON.stringify(arr));
  },

  add({ name, region }) {
    const items = this.load();
    const newItem = {
      id: crypto.randomUUID(),
      name,
      region,
      addedAt: new Date().toISOString(),
    };
    const updated = [newItem, ...items];
    this.save(updated);
    return updated;
  },

  remove(id) {
    const updated = this.load().filter(f => f.id !== id);
    this.save(updated);
    return updated;
  },

  isFavorite(name) {
    return this.load().some(f => f.name === name);
  },

  count() {
    return this.load().length;
  },
};

window.FavoritesStore = FavoritesStore;

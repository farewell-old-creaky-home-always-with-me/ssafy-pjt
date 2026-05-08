// js/commercial.js

const CATEGORIES = [
  { key: '', label: '전체', icon: 'layout-grid', color: '#4F46E5' },
  { key: 'I2', label: '음식점/카페', icon: 'utensils-crossed', color: '#EB5757' },
  { key: 'G2', label: '편의점/마트', icon: 'store', color: '#2D9CDB' },
  { key: 'Q1', label: '의료/병원', icon: 'hospital', color: '#27AE60' },
  { key: 'P1', label: '교육/학원', icon: 'book-open', color: '#F2994A' },
];

let mapContainer, map;
let markers = [];
let activeCategory = '';
let selectedShop = null;
let sheetExpanded = false;
let currentDongCode = '11680'; // 기본값: 서울 강남구 (시군구코드)
let storesData = [];

window.addEventListener('DOMContentLoaded', () => {
  initMap();
  
  // 관심지역 확인
  if (window.FavoritesStore) {
    const favorites = window.FavoritesStore.load();
    if (favorites.length > 0) {
      currentDongCode = favorites[0].signguCode || favorites[0].dongCode?.slice(0, 5) || '11680';
    }
  }

  renderTabs();
  fetchStoreList(currentDongCode, activeCategory);
});

function initMap() {
  mapContainer = document.getElementById('map');
  const options = {
    center: new kakao.maps.LatLng(37.4979, 127.0276), // 기본 역삼1동
    level: 4
  };
  map = new kakao.maps.Map(mapContainer, options);

  // 중앙 핀(map-center-hint) 제거
  const hint = document.getElementById('map-center-hint');
  if (hint && hint.parentNode) {
    hint.parentNode.removeChild(hint);
  }
}

async function fetchStoreList(dongCode, categoryCode) {
  const params = new URLSearchParams({
    serviceKey: CONFIG.COMMERCIAL_API_KEY,
    divId: 'signguCd',
    key: dongCode,
    numOfRows: CONFIG.MAX_ROWS,
    pageNo: 1,
    type: 'json'
  });

  if (categoryCode) {
    params.append('indsLclsCd', categoryCode);
  }

  try {
    const res = await fetch(`${CONFIG.COMMERCIAL_BASE_URL}storeListInDong?${params}`);
    const json = await res.json();
    
    let items = [];
    if (json.body && json.body.items) {
      items = json.body.items;
      if (!Array.isArray(items)) {
         items = [items];
      }
    }
    
    storesData = items;
    renderAll();
  } catch (err) {
    console.error('상권 API 오류:', err);
    storesData = [];
    renderAll();
  }
}

function renderAll() {
  clearMarkers();
  renderTabs();
  renderMarkers();
  closeSheet(); // 데이터가 바뀌면 바닥 시트를 닫음
}

function clearMarkers() {
  markers.forEach(m => m.setMap(null));
  markers = [];
}

function renderMarkers() {
  const catItem = CATEGORIES.find(c => c.key === activeCategory) || CATEGORIES[0];
  const bounds = new kakao.maps.LatLngBounds();
  let validStoreCount = 0;

  storesData.forEach((store, i) => {
    if (!store.lon || !store.lat) return;
    
    const storeLatLng = new kakao.maps.LatLng(parseFloat(store.lat), parseFloat(store.lon));
    bounds.extend(storeLatLng);
    validStoreCount++;

    const content = document.createElement('div');
    content.className = 'marker-btn';
    content.style.position = 'relative'; 
    content.style.cursor = 'pointer';
    content.innerHTML = `
      <div class="marker-dot" style="background:${catItem.color}"><i data-lucide="${catItem.icon}"></i></div>
      <div class="marker-label">${store.bizesNm}</div>
    `;
    
    content.onclick = () => selectShop(store.bizesId, store);

    const overlay = new kakao.maps.CustomOverlay({
      position: storeLatLng,
      content: content,
      yAnchor: 1
    });

    overlay.setMap(map);
    markers.push(overlay);
  });

  lucide.createIcons();

  if (validStoreCount > 0) {
    map.setBounds(bounds);
  }
}

window.selectCategory = function(key) {
  activeCategory = key;
  renderTabs();
  fetchStoreList(currentDongCode, activeCategory);
};

function renderTabs() {
  const container = document.getElementById('cat-tabs');
  container.innerHTML = CATEGORIES.map(cat => {
    const isActive = activeCategory === cat.key;
    const count = isActive ? storesData.length : '-';
    return `
      <button class="cat-tab ${isActive ? 'active' : ''}"
        style="${isActive ? `background-color:${cat.color}` : ''}"
        onclick="selectCategory('${cat.key}')">
        <i data-lucide="${cat.icon}"></i>
        ${cat.label}
        <span class="cat-tab-count" style="${isActive ? 'background:rgba(255,255,255,0.2)' : 'background:rgba(156,163,175,0.4)'}">${count}</span>
      </button>`;
  }).join('');
  lucide.createIcons();
}

window.selectShop = function(id, store) {
  selectedShop = store;
  sheetExpanded = true;
  
  const catItem = CATEGORIES.find(c => c.key === activeCategory) || CATEGORIES[0];
  const sheet = document.getElementById('bottom-sheet');
  sheet.style.display = 'block';
  sheet.className = 'bottom-sheet expanded';

  document.getElementById('sheet-name').textContent = selectedShop.bizesNm;
  document.getElementById('sheet-address').textContent = selectedShop.rdnmAdr || selectedShop.ldongAdr || '주소 미제공';
  document.getElementById('sheet-icon').innerHTML = `<i data-lucide="${catItem.icon}" style="color:${catItem.color};width:20px;height:20px"></i>`;
  document.getElementById('sheet-icon').style.background = `${catItem.color}15`;

  document.getElementById('sheet-detail-grid').innerHTML = `
    <div class="sheet-detail-item">
      <i data-lucide="tag"></i>
      <div><p class="detail-label">중분류</p><p class="detail-value">${selectedShop.indsMclsNm || '-'}</p></div>
    </div>
    <div class="sheet-detail-item">
      <i data-lucide="tag"></i>
      <div><p class="detail-label">소분류</p><p class="detail-value">${selectedShop.indsSclsNm || '-'}</p></div>
    </div>
    <div class="sheet-detail-item">
      <i data-lucide="map-pin"></i>
      <div><p class="detail-label">지번주소</p><p class="detail-value">${selectedShop.ldongAdr || '-'}</p></div>
    </div>`;
  lucide.createIcons();
};

window.toggleSheet = function() {
  sheetExpanded = !sheetExpanded;
  const sheet = document.getElementById('bottom-sheet');
  sheet.className = `bottom-sheet ${sheetExpanded ? 'expanded' : 'collapsed'}`;
};

window.closeSheet = function() {
  selectedShop = null;
  sheetExpanded = false;
  document.getElementById('bottom-sheet').style.display = 'none';
};

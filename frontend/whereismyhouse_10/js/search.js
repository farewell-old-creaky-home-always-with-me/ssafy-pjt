// ── XML 파싱 및 법정동 추출 ──────────────────────────────

/**
 * XML 파일들에서 법정동 목록을 추출합니다.
 * @returns {Promise<string[]>} 중복 제거된 정렬된 법정동 목록
 */
async function loadDongListFromXML() {
  const xmlFiles = [
    './resources/ssafy_home/AptInfo.xml',
    './resources/ssafy_home/AptDealHistory.xml',
    './resources/ssafy_home/AptRentHistory.xml',
    './resources/ssafy_home/HomeDealHistory.xml',
    './resources/ssafy_home/HomeRentHistory.xml'
  ];

  const dongSet = new Set();

  for (const file of xmlFiles) {
    try {
      const response = await fetch(file);
      const xmlText = await response.text();
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(xmlText, 'text/xml');
      
      // <법정동> 태그 추출
      const dongElements = xmlDoc.getElementsByTagName('법정동');
      for (let i = 0; i < dongElements.length; i++) {
        const dongText = dongElements[i].textContent.trim();
        if (dongText) {
          dongSet.add(dongText);
        }
      }
    } catch (error) {
      console.warn(`Failed to load ${file}:`, error);
    }
  }

  // Set을 배열로 변환하고 정렬
  return Array.from(dongSet).sort();
}

/**
 * select 요소에 법정동 옵션들을 추가합니다.
 * @param {string[]} dongList - 법정동 목록
 */
function populateDongSelect(dongList) {
  const selectElement = document.getElementById('filter-dong');
  if (!selectElement) return;

  // 기존 옵션 제거 (첫 번째 "전체" 옵션 제외)
  while (selectElement.options.length > 1) {
    selectElement.remove(1);
  }

  // 새로운 옵션 추가
  dongList.forEach(dong => {
    const option = document.createElement('option');
    option.value = dong;
    option.textContent = dong;
    selectElement.appendChild(option);
  });

  console.log(`✓ 법정동 ${dongList.length}개 로드 완료`);
}

/**
 * 페이지 로드 시 법정동 목록을 초기화합니다.
 */
async function initializeDongList() {
  try {
    const dongList = await loadDongListFromXML();
    populateDongSelect(dongList);
  } catch (error) {
    console.error('법정동 목록 로드 실패:', error);
  }
}

/**
 * XML 파일들에서 실거래 데이터를 추출합니다.
 * @returns {Promise<Array>} 실거래 데이터 배열
 */
async function loadRealEstateDataFromXML() {
  const files = [
    { path: './resources/ssafy_home/AptDealHistory.xml', buildingType: '아파트', transactionType: '매매', nameTag: '아파트' },
    { path: './resources/ssafy_home/AptRentHistory.xml', buildingType: '아파트', transactionType: '전월세', nameTag: '아파트' },
    { path: './resources/ssafy_home/HomeDealHistory.xml', buildingType: '다세대', transactionType: '매매', nameTag: '연립다세대' },
    { path: './resources/ssafy_home/HomeRentHistory.xml', buildingType: '다세대', transactionType: '전월세', nameTag: '연립다세대' }
  ];

  const allData = [];
  let idCounter = 1;

  for (const fileInfo of files) {
    try {
      const response = await fetch(fileInfo.path);
      const xmlText = await response.text();
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(xmlText, 'text/xml');
      
      const items = xmlDoc.getElementsByTagName('item');
      
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        
        // 데이터 추출
        const name = getTagValue(item, fileInfo.nameTag);
        const area = parseFloat(getTagValue(item, '전용면적')) || 0;
        const floor = parseInt(getTagValue(item, '층')) || 0;
        const buildYear = parseInt(getTagValue(item, '건축년도')) || 0;
        const dong = getTagValue(item, '법정동').trim();
        const jibun = getTagValue(item, '지번');
        const regionCode = getTagValue(item, '지역코드');
        const year = getTagValue(item, '년');
        const month = getTagValue(item, '월');
        const day = getTagValue(item, '일');
        
        // 가격 처리
        let price = '';
        if (fileInfo.transactionType === '매매') {
          const amount = getTagValue(item, '거래금액').trim();
          price = formatPrice(amount);
        } else {
          const deposit = getTagValue(item, '보증금액').trim();
          const monthly = getTagValue(item, '월세금액').trim();
          price = formatRentPrice(deposit, monthly);
        }
        
        // 날짜 처리
        const date = year && month && day ? `${year}.${month.padStart(2, '0')}.${day.padStart(2, '0')}` : '';
        
        // 유효한 데이터만 추가
        if (name && dong && buildYear) {
          allData.push({
            id: idCounter++,
            name: name,
            area: area,
            floor: floor,
            price: price,
            date: date,
            buildYear: buildYear,
            dong: dong,
            jibun: jibun,
            regionCode: regionCode,
            buildingType: fileInfo.buildingType,
            transactionType: fileInfo.transactionType
          });
        }
      }
      
      console.log(`✓ ${fileInfo.path} 로드 완료: ${items.length}건`);
    } catch (error) {
      console.warn(`Failed to load ${fileInfo.path}:`, error);
    }
  }

  console.log(`✓ 총 ${allData.length}건의 실거래 데이터 로드 완료`);
  return allData;
}

/**
 * XML 노드에서 태그 값을 추출하는 헬퍼 함수
 */
function getTagValue(node, tagName) {
  const elements = node.getElementsByTagName(tagName);
  return elements.length > 0 ? elements[0].textContent : '';
}

/**
 * 매매 가격 포맷팅 (만원 단위)
 */
function formatPrice(amount) {
  if (!amount) return '0';
  const cleaned = amount.replace(/,/g, '').trim();
  const num = parseInt(cleaned);
  if (isNaN(num)) return amount;
  
  if (num >= 10000) {
    const eok = Math.floor(num / 10000);
    const man = num % 10000;
    return man > 0 ? `${eok}억 ${man.toLocaleString()}` : `${eok}억`;
  }
  return num.toLocaleString();
}

/**
 * 전월세 가격 포맷팅
 */
function formatRentPrice(deposit, monthly) {
  const dep = parseInt(deposit.replace(/,/g, '').trim()) || 0;
  const mon = parseInt(monthly.replace(/,/g, '').trim()) || 0;
  
  let result = '';
  if (dep >= 10000) {
    const eok = Math.floor(dep / 10000);
    const man = dep % 10000;
    result = man > 0 ? `보 ${eok}억 ${man.toLocaleString()}` : `보 ${eok}억`;
  } else if (dep > 0) {
    result = `보 ${dep.toLocaleString()}`;
  }
  
  if (mon > 0) {
    result += (result ? ' / ' : '') + `월 ${mon.toLocaleString()}`;
  }
  
  return result || '0';
}

// ── Mock Data ──────────────────────────────────────────────
// XML에서 로드된 실제 데이터로 대체됩니다
let REAL_ESTATE_DATA = [];

const APT_IMAGES = [
  "https://images.unsplash.com/photo-1486325212027-8081e485255e?w=600&q=80",
  "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=600&q=80",
  "https://images.unsplash.com/photo-1460317442991-0ec209397118?w=600&q=80",
  "https://images.unsplash.com/photo-1515263487990-61b07816dd07?w=600&q=80",
  "https://images.unsplash.com/photo-1523217582562-09d0def993a6?w=600&q=80",
];

// ── State ──────────────────────────────────────────────────
const PAGE_SIZE = 10;
const WINDOW_SIZE = 5;

let filters = { dong:'', buildingType:'아파트', transactionType:'매매', yearStart:1970, yearEnd:2026 };
let sortKey = 'date';
let sortDir = 'desc';
let currentPage = 1;
let filteredData = [];
let selectedId = null;
let sidebarOpen = false;

let map;
let markers = [];
let geocoder;
let infowindow;

// ── Filter Logic ───────────────────────────────────────────
function setBuildingType(type) {
  filters.buildingType = type;
  document.getElementById('btn-apt').className   = 'toggle-btn' + (type === '아파트' ? ' active-blue' : '');
  document.getElementById('btn-multi').className = 'toggle-btn' + (type === '다세대' ? ' active-blue' : '');
}

function setTransactionType(type) {
  filters.transactionType = type;
  document.getElementById('btn-sale').className = 'toggle-btn' + (type === '매매'  ? ' active-navy' : '');
  document.getElementById('btn-rent').className = 'toggle-btn' + (type === '전월세'? ' active-navy' : '');
}

function applyFilter() {
  filters.dong      = document.getElementById('filter-dong').value;
  filters.yearStart = parseInt(document.getElementById('year-start').value) || 1970;
  filters.yearEnd   = parseInt(document.getElementById('year-end').value)   || 2026;
  currentPage = 1;
  runSearch();
  if (sidebarOpen) toggleMobileFilter();
}

function resetFilter() {
  filters = { dong:'', buildingType:'아파트', transactionType:'매매', yearStart:1970, yearEnd:2026 };
  document.getElementById('filter-dong').value    = '';
  document.getElementById('year-start').value     = '1970';
  document.getElementById('year-end').value       = '2026';
  setBuildingType('아파트');
  setTransactionType('매매');
  currentPage = 1;
  runSearch();
}

function runSearch() {
  filteredData = REAL_ESTATE_DATA.filter(item => {
    if (filters.dong && item.dong !== filters.dong) return false;
    if (filters.buildingType && item.buildingType !== filters.buildingType) return false;
    if (filters.transactionType && item.transactionType !== filters.transactionType) return false;
    if (item.buildYear < filters.yearStart || item.buildYear > filters.yearEnd) return false;
    return true;
  });
  applySortInternal();
  renderTable();
  renderPagination();
  if (typeof updateMarkers === 'function') updateMarkers(filteredData);
}

// ── Sort Logic ─────────────────────────────────────────────
function setSort(key) {
  if (sortKey === key) {
    sortDir = sortDir === 'asc' ? 'desc' : 'asc';
  } else {
    sortKey = key;
    sortDir = 'asc';
  }
  currentPage = 1;
  applySortInternal();
  renderTable();
  renderPagination();
}

function applySortInternal() {
  filteredData.sort(function(a, b) {
    var av = a[sortKey], bv = b[sortKey];
    if (sortKey === 'price') {
      av = parseFloat(String(av).replace(/[^0-9.]/g, ''));
      bv = parseFloat(String(bv).replace(/[^0-9.]/g, ''));
    }
    if (av < bv) return sortDir === 'asc' ? -1 : 1;
    if (av > bv) return sortDir === 'asc' ?  1 : -1;
    return 0;
  });
  updateSortIcons();
}

function updateSortIcons() {
  var keys = ['name','area','floor','price','date'];
  keys.forEach(function(k) {
    var el = document.getElementById('sort-icon-' + k);
    if (!el) return;
    if (k === sortKey) {
      el.setAttribute('data-lucide', sortDir === 'asc' ? 'chevron-up' : 'chevron-down');
    } else {
      el.setAttribute('data-lucide', 'chevrons-up-down');
    }
  });
  lucide.createIcons({ nodes: [document.querySelector('thead')] });
}

// ── Render Table ───────────────────────────────────────────
function renderTable() {
  var total = filteredData.length;
  var totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
  if (currentPage > totalPages) currentPage = totalPages;

  var start = (currentPage - 1) * PAGE_SIZE;
  var end   = Math.min(start + PAGE_SIZE, total);
  var pageData = filteredData.slice(start, end);

  document.getElementById('result-count').textContent = total;
  document.getElementById('result-range').textContent =
    total > 0 ? (start + 1) + '–' + end + ' / ' + total : '';

  var tbody = document.getElementById('result-tbody');
  if (pageData.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:3rem;color:#9ca3af">검색 결과가 없습니다.</td></tr>';
    return;
  }

  tbody.innerHTML = pageData.map(function(item) {
    var sel = selectedId === item.id ? ' selected' : '';
    return '<tr class="' + sel + '" onclick="openModal(' + item.id + ')">' +
      '<td style="font-weight:500;color:#1A3C6E">' + item.name + '</td>' +
      '<td>' + item.area.toFixed(2) + '</td>' +
      '<td>' + item.floor + '층</td>' +
      '<td style="font-weight:600;color:#1A3C6E">' + item.price + '</td>' +
      '<td style="color:#9ca3af">' + item.date + '</td>' +
    '</tr>';
  }).join('');
}

// ── Pagination ─────────────────────────────────────────────
function renderPagination() {
  var total = filteredData.length;
  var totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
  var pg = document.getElementById('pagination');

  var startP = Math.max(1, currentPage - Math.floor(WINDOW_SIZE / 2));
  var endP   = Math.min(totalPages, startP + WINDOW_SIZE - 1);
  if (endP - startP + 1 < WINDOW_SIZE) startP = Math.max(1, endP - WINDOW_SIZE + 1);

  var html = '';
  html += '<button class="page-btn" onclick="goPage(1)" ' + (currentPage === 1 ? 'disabled' : '') + '><i data-lucide="chevrons-left"></i></button>';
  html += '<button class="page-btn" onclick="goPage(' + (currentPage - 1) + ')" ' + (currentPage === 1 ? 'disabled' : '') + '><i data-lucide="chevron-left"></i></button>';

  for (var i = startP; i <= endP; i++) {
    html += '<button class="page-btn' + (i === currentPage ? ' active' : '') + '" onclick="goPage(' + i + ')">' + i + '</button>';
  }

  html += '<button class="page-btn" onclick="goPage(' + (currentPage + 1) + ')" ' + (currentPage === totalPages ? 'disabled' : '') + '><i data-lucide="chevron-right"></i></button>';
  html += '<button class="page-btn" onclick="goPage(' + totalPages + ')" ' + (currentPage === totalPages ? 'disabled' : '') + '><i data-lucide="chevrons-right"></i></button>';

  pg.innerHTML = html;
  lucide.createIcons({ nodes: [pg] });
}

function goPage(n) {
  var totalPages = Math.max(1, Math.ceil(filteredData.length / PAGE_SIZE));
  if (n < 1 || n > totalPages) return;
  currentPage = n;
  renderTable();
  renderPagination();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ── Modal ──────────────────────────────────────────────────
function openModal(id) {
  var item = REAL_ESTATE_DATA.find(function(d) { return d.id === id; });
  if (!item) return;
  selectedId = id;
  renderTable();

  document.getElementById('modal-title').textContent    = item.name;
  document.getElementById('modal-subtitle').textContent = item.dong + ' ' + item.jibun;
  document.getElementById('modal-price').textContent    = item.price;
  document.getElementById('modal-date').textContent     = '거래일: ' + item.date;
  document.getElementById('modal-img').src              = APT_IMAGES[id % APT_IMAGES.length];

  document.getElementById('modal-info-grid').innerHTML = [
    { icon:'building-2', label:'건물유형',  value: item.buildingType },
    { icon:'handshake',  label:'거래유형',  value: item.transactionType },
    { icon:'calendar', label:'건축연도',  value: item.buildYear + '년' },
    { icon:'map-pin',  label:'동',        value: item.dong },
    { icon:'hash',     label:'지번',      value: item.jibun },
    { icon:'landmark', label:'지역코드',  value: item.regionCode },
    { icon:'maximize-2',label:'전용면적', value: item.area.toFixed(2) + ' ㎡' },
    { icon:'layers',   label:'층',        value: item.floor + '층' },
  ].map(function(inf) {
    return '<div class="info-item">' +
      '<div class="info-item-icon"><i data-lucide="' + inf.icon + '"></i></div>' +
      '<div><p class="info-item-label">' + inf.label + '</p><p class="info-item-value">' + inf.value + '</p></div>' +
    '</div>';
  }).join('');

  updateFavBtn(item);

  var overlay = document.getElementById('detail-modal');
  overlay.classList.remove('hidden');
  requestAnimationFrame(function() { overlay.classList.add('visible'); });
  lucide.createIcons({ nodes: [overlay] });

  if (geocoder && map && typeof kakao !== 'undefined') {
    var address = item.dong + ' ' + (item.jibun || '');
    geocoder.addressSearch(address, function (result, status) {
      if (status === kakao.maps.services.Status.OK) {
        var coords = new kakao.maps.LatLng(result[0].y, result[0].x);
        infowindow.close();
        map.panTo(coords);

        var iwContent = `<div style="padding:5px;font-size:12px;color:#1A3C6E;"><span style="font-weight:bold;">${item.name}</span><br>금액: ${item.price}</div>`;

        infowindow.setContent(iwContent);
        infowindow.setPosition(coords);
        infowindow.open(map);
      }
    });
  }
}

function closeModal() {
  var overlay = document.getElementById('detail-modal');
  overlay.classList.remove('visible');
  setTimeout(function() { overlay.classList.add('hidden'); }, 200);
  selectedId = null;
  renderTable();
}

function handleModalOverlayClick(e) {
  if (e.target === document.getElementById('detail-modal')) closeModal();
}

function toggleMobileFilter() {
  sidebarOpen = !sidebarOpen;
  var sidebar = document.getElementById('filter-sidebar');
  if (sidebarOpen) {
    sidebar.classList.remove('sidebar-hidden');
  } else {
    sidebar.classList.add('sidebar-hidden');
  }
}

function updateFavBtn(item) {
  var isFav = window.FavoritesStore ? window.FavoritesStore.isFavorite(item.name) : false;
  var btn = document.getElementById('fav-btn');
  var icon = document.getElementById('fav-icon');
  var lbl = document.getElementById('fav-label');
  if (!btn || !lbl || !icon) return;
  
  icon.setAttribute('data-lucide', 'heart');
  if (isFav) {
    btn.style.background = '#FEE2E2';
    btn.style.borderColor = '#EB5757';
    btn.style.color = '#EB5757';
    lbl.textContent = '관심지역 해제';
  } else {
    btn.style.background = '';
    btn.style.borderColor = '';
    btn.style.color = '';
    lbl.textContent = '관심지역 추가';
  }
  lucide.createIcons({ nodes: [btn] });
}

function toggleFavorite() {
  if (!selectedId || !window.FavoritesStore) return;
  var item = REAL_ESTATE_DATA.find(function(d) { return d.id === selectedId; });
  if (!item) return;
  
  if (window.FavoritesStore.isFavorite(item.name)) {
    var items = window.FavoritesStore.load();
    var fav = items.find(function(f) { return f.name === item.name; });
    if (fav) window.FavoritesStore.remove(fav.id);
  } else {
    window.FavoritesStore.add({
      name: item.name,
      region: item.dong
    });
  }
  updateFavBtn(item);
  updateFavBadge();
}

// ── Map ───────────────────────────────────────────────────
function initMap() {
  var mapContainer = document.getElementById('map');
  if (!mapContainer || typeof kakao === 'undefined') return;
  var mapOption = {
    center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 중심
    level: 5
  };
  map = new kakao.maps.Map(mapContainer, mapOption);
  geocoder = new kakao.maps.services.Geocoder();
  infowindow = new kakao.maps.InfoWindow({ zIndex: 1 });
}

// ── Markers ──────────────────────────────────────
function updateMarkers(dataList) {
  if (!map || typeof kakao === 'undefined') return;
  markers.forEach(function (marker) {
    marker.setMap(null);
  });
  markers = [];

  if (dataList.length === 0) return;

  var bounds = new kakao.maps.LatLngBounds();

  dataList.forEach(function (item) {
    var address = item.dong + ' ' + (item.jibun || '');
    geocoder.addressSearch(address, function (result, status) {
      if (status === kakao.maps.services.Status.OK) {
        var coords = new kakao.maps.LatLng(result[0].y, result[0].x);
        var marker = new kakao.maps.Marker({
          map: map,
          position: coords
        });
        markers.push(marker);
        bounds.extend(coords);
        map.setBounds(bounds);

        var iwContent = `<div style="padding:5px;font-size:12px;color:#1A3C6E;">
          <span style="font-weight:bold;">${item.name}</span><br>
          금액: ${item.price}
        </div>`;

        kakao.maps.event.addListener(marker, 'click', function () {
          infowindow.setContent(iwContent);
          infowindow.open(map, marker);
        });
      }
    });
  });
}

// ── Initialization ─────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async function() {
  // 맵 초기화
  initMap();

  // 법정동 목록 로드
  await initializeDongList();
  
  // 실거래 데이터 로드
  REAL_ESTATE_DATA = await loadRealEstateDataFromXML();
  
  // 초기 검색 실행
  runSearch();
  
  // 아이콘 초기화
  lucide.createIcons();
});

// ── Mobile menu toggle ──
document.addEventListener('DOMContentLoaded', () => {
  const hamburger = document.getElementById('navbar-hamburger');
  const mobileMenu = document.getElementById('mobile-menu');
  const menuIconOpen = document.getElementById('menu-icon-open');
  const menuIconClose = document.getElementById('menu-icon-close');

  if (hamburger && mobileMenu) {
    hamburger.addEventListener('click', () => {
      const isOpen = mobileMenu.classList.toggle('open');
      if (menuIconOpen) menuIconOpen.style.display = isOpen ? 'none' : 'block';
      if (menuIconClose) menuIconClose.style.display = isOpen ? 'block' : 'none';
    });
  }

  // ── Favorites badge count ──
  updateFavBadge();
  
  // ── Update navbar auth state ──
  updateNavbarAuth();

  // ── Active nav highlight ──
  const path = window.location.pathname;
  document.querySelectorAll('.nav-link, .mobile-nav-link').forEach(link => {
    const href = link.getAttribute('href') || '';
    // Normalize href to pathname
    let linkPath = href;
    try { linkPath = new URL(href, window.location.href).pathname; } catch(e) {}
    if (linkPath === path || (linkPath !== '/' && path.startsWith(linkPath))) {
      link.classList.add('active');
    }
  });
});

function updateFavBadge() {
  const count = (window.FavoritesStore ? window.FavoritesStore.count() : 0);
  document.querySelectorAll('.fav-badge').forEach(el => {
    if (count > 0) {
      el.textContent = count > 99 ? '99+' : count;
      el.style.display = 'flex';
    } else {
      el.style.display = 'none';
    }
  });
  // Hamburger dot
  document.querySelectorAll('.hamburger-dot').forEach(el => {
    el.style.display = count > 0 ? 'block' : 'none';
  });
}

// ── Utilities ──
function formatDate(iso) {
  const d = new Date(iso);
  return `${d.getFullYear()}.${String(d.getMonth()+1).padStart(2,'0')}.${String(d.getDate()).padStart(2,'0')}`;
}

function formatPhone(val) {
  let v = val.replace(/[^\d]/g, '');
  if (v.length > 11) v = v.slice(0, 11);
  if (v.length > 7) return `${v.slice(0,3)}-${v.slice(3,7)}-${v.slice(7)}`;
  if (v.length > 3) return `${v.slice(0,3)}-${v.slice(3)}`;
  return v;
}

function showFieldError(inputEl, msg) {
  const wrap = inputEl.closest('.form-group') || inputEl.parentElement.parentElement;
  let errEl = wrap.querySelector('.field-error');
  if (!errEl) {
    errEl = document.createElement('div');
    errEl.className = 'field-error';
    errEl.innerHTML = '<i data-lucide="alert-circle"></i><span></span>';
    wrap.appendChild(errEl);
    if (window.lucide) lucide.createIcons({ nodes: [errEl] });
  }
  errEl.querySelector('span').textContent = msg;
  errEl.style.display = 'flex';
  inputEl.classList.add('input-error');
}

function clearFieldError(inputEl) {
  const wrap = inputEl.closest('.form-group') || inputEl.parentElement.parentElement;
  const errEl = wrap.querySelector('.field-error');
  if (errEl) errEl.style.display = 'none';
  inputEl.classList.remove('input-error');
}

window.formatDate = formatDate;
window.formatPhone = formatPhone;
window.showFieldError = showFieldError;
window.clearFieldError = clearFieldError;
window.updateFavBadge = updateFavBadge;

// ── Update navbar auth state ──
function updateNavbarAuth() {
  // UserStore가 로드되지 않았으면 스킵
  if (!window.UserStore) return;
  
  const isLoggedIn = UserStore.isLoggedIn();
  const currentUser = UserStore.getCurrentUser();
  
  // 데스크톱 navbar
  const navbarAuth = document.querySelector('.navbar-auth');
  if (navbarAuth) {
    if (isLoggedIn && currentUser) {
      navbarAuth.innerHTML = `
        <div class="navbar-divider"></div>
        <a href="./profile.html" class="nav-link"><i data-lucide="user-circle"></i> ${currentUser.name}님</a>
        <button id="logout-btn" class="nav-link" style="background:none;border:none;cursor:pointer;"><i data-lucide="log-out"></i> 로그아웃</button>
      `;
      if (window.lucide) lucide.createIcons({ nodes: [navbarAuth] });
      
      const logoutBtn = document.getElementById('logout-btn');
      if (logoutBtn) {
        logoutBtn.addEventListener('click', handleLogout);
      }
    } else {
      navbarAuth.innerHTML = `
        <div class="navbar-divider"></div>
        <a href="./login.html" class="nav-link"><i data-lucide="log-in"></i> 로그인</a>
        <a href="./profile.html" class="nav-link"><i data-lucide="user-circle"></i> 마이페이지</a>
      `;
      if (window.lucide) lucide.createIcons({ nodes: [navbarAuth] });
    }
  }
  
  // 모바일 메뉴 - 마지막 두 링크만 교체
  const mobileMenu = document.getElementById('mobile-menu');
  if (mobileMenu) {
    // 기존 로그인/마이페이지 링크 제거
    const existingAuthLinks = mobileMenu.querySelectorAll('.mobile-nav-link[href*="login"], .mobile-nav-link[href*="profile"], .mobile-logout-btn');
    existingAuthLinks.forEach(link => link.remove());
    
    if (isLoggedIn && currentUser) {
      mobileMenu.insertAdjacentHTML('beforeend', `
        <a href="./profile.html" class="mobile-nav-link"><i data-lucide="user-circle"></i> ${currentUser.name}님</a>
        <button class="mobile-nav-link mobile-logout-btn" style="background:none;border:none;cursor:pointer;text-align:left;width:100%;"><i data-lucide="log-out"></i> 로그아웃</button>
      `);
      if (window.lucide) lucide.createIcons({ nodes: [mobileMenu] });
      
      const mobileLogoutBtn = mobileMenu.querySelector('.mobile-logout-btn');
      if (mobileLogoutBtn) {
        mobileLogoutBtn.addEventListener('click', handleLogout);
      }
    } else {
      mobileMenu.insertAdjacentHTML('beforeend', `
        <a href="./login.html" class="mobile-nav-link"><i data-lucide="log-in"></i> 로그인</a>
        <a href="./profile.html" class="mobile-nav-link"><i data-lucide="user-circle"></i> 마이페이지</a>
      `);
      if (window.lucide) lucide.createIcons({ nodes: [mobileMenu] });
    }
  }
  
  // 로그인 상태가 변경되면 관심지역 배지도 업데이트
  updateFavBadge();
}

function handleLogout() {
  if (!window.UserStore) return;
  
  if (confirm('로그아웃 하시겠습니까?')) {
    UserStore.logout();
    // 로그아웃 후 관심지역 배지 업데이트 (게스트 모드는 0개)
    updateFavBadge();
    alert('로그아웃되었습니다.');
    window.location.href = './index.html';
  }
}

window.updateNavbarAuth = updateNavbarAuth;
window.handleLogout = handleLogout;

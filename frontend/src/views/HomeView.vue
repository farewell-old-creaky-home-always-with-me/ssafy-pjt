<template>
  <section class="hero">
    <div class="hero-bg" style="background-image:url('https://images.unsplash.com/photo-1768006241304-6b833ee11611?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxTZW91bCUyMGNpdHlzY2FwZSUyMGFwYXJ0bWVudCUyMGJ1aWxkaW5ncyUyMHNreWxpbmV8ZW58MXx8fHwxNzcyNzY0NTQ2fDA&ixlib=rb-4.1.0&q=80&w=1080')"></div>
    <div class="hero-overlay"></div>
    <div class="hero-content">
      <div class="hero-badge">
        <span class="hero-badge-dot"></span>
        공공데이터 기반 실거래가 조회 서비스
      </div>
      <h1>구해줘! <span class="blue">SSAFY HOME</span></h1>
      <p class="hero-sub">
        원하는 지역의 부동산 실거래가를 간편하게 조회하세요.<br>
        아파트, 다세대 등 다양한 거래유형을 지원합니다.
      </p>
      <div class="hero-search-wrap">
        <div class="hero-search-box" :class="{ focused: searchFocused }">
          <div class="hero-search-fields">
            <div class="hero-search-input-wrap">
              <Search :size="18" />
              <input
                v-model="heroInput"
                type="text"
                class="hero-input"
                placeholder="법정동을 입력하세요 (예: 역삼동)"
                @focus="searchFocused = true"
                @blur="searchFocused = false"
              />
            </div>
            <div class="hero-select-wrap">
              <select v-model="heroTxType" class="hero-select">
                <option value="apt-sale">아파트 매매</option>
                <option value="apt-rent">아파트 전월세</option>
                <option value="multi-sale">다세대 매매</option>
                <option value="multi-rent">다세대 전월세</option>
              </select>
              <ChevronDown :size="16" />
            </div>
            <button class="hero-search-btn" @click="router.push('/search')">검색하기</button>
          </div>
        </div>
        <div class="hero-tags">
          <button class="hero-tag" @click="heroInput = '역삼동'">강남구 역삼동</button>
          <button class="hero-tag" @click="heroInput = '서초동'">서초구 서초동</button>
          <button class="hero-tag" @click="heroInput = '잠실동'">송파구 잠실동</button>
          <button class="hero-tag" @click="heroInput = '합정동'">마포구 합정동</button>
        </div>
      </div>
    </div>
  </section>

  <section class="stats-section">
    <div class="container">
      <div class="section-header">
        <h2>실시간 부동산 시장 현황</h2>
        <p>공공데이터 기반 최신 거래 통계를 확인하세요</p>
      </div>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-card-top">
            <div class="stat-icon" style="background:rgba(45,156,219,0.1)"><BarChart3 style="color:#2D9CDB" :size="20" /></div>
            <div class="stat-change up"><TrendingUp :size="14" /> +12.5%</div>
          </div>
          <p class="stat-label">오늘 거래량</p>
          <div class="stat-value"><span class="stat-number">1,247</span><span class="stat-unit">건</span></div>
          <p class="stat-desc">전일 대비</p>
        </div>
        <div class="stat-card">
          <div class="stat-card-top">
            <div class="stat-icon" style="background:rgba(26,60,110,0.1)"><Building2 style="color:#1A3C6E" :size="20" /></div>
            <div class="stat-change up"><TrendingUp :size="14" /> +3.2%</div>
          </div>
          <p class="stat-label">평균 매매가</p>
          <div class="stat-value"><span class="stat-number">8억 4,200</span><span class="stat-unit">만원</span></div>
          <p class="stat-desc">전월 대비</p>
        </div>
        <div class="stat-card">
          <div class="stat-card-top">
            <div class="stat-icon" style="background:rgba(16,185,129,0.1)"><Landmark style="color:#059669" :size="20" /></div>
            <div class="stat-change down"><TrendingDown :size="14" /> -1.8%</div>
          </div>
          <p class="stat-label">평균 전세가</p>
          <div class="stat-value"><span class="stat-number">4억 1,800</span><span class="stat-unit">만원</span></div>
          <p class="stat-desc">전월 대비</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ChevronDown, BarChart3, TrendingUp, TrendingDown, Building2, Landmark } from 'lucide-vue-next'
import '../../css/pages/home.css'

const router = useRouter()
const heroInput = ref('')
const heroTxType = ref('apt-sale')
const searchFocused = ref(false)
</script>

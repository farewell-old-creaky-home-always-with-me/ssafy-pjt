<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Shield, MapPin, Building2, Play, Loader2, AlertCircle, Search } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/authStore.js'
import { adminApi } from '@/api/index.js'
import '@css/pages/batch.css'

const router = useRouter()
const authStore = useAuthStore()

const regionLoading = ref(false)
const regionResult = ref(null)
const regionError = ref('')

const houseLoading = ref(false)
const houseResult = ref(null)
const houseError = ref('')
const collectAllRegions = ref(true)
const regionKeyword = ref('')
const regionResults = ref([])
const selectedRegion = ref(null)
const searchingRegions = ref(false)

const houseForm = reactive({
  regionCode: '',
  yearMonth: '',
  houseType: 'APARTMENT',
  dealType: 'SALE',
})

function formatRegion(region) {
  return [region.sidoName, region.sigunguName, region.dongName]
    .filter(Boolean)
    .join(' ')
}

async function searchRegions() {
  const keyword = regionKeyword.value.trim()
  selectedRegion.value = null
  regionResults.value = []
  houseForm.regionCode = ''

  if (collectAllRegions.value || keyword.length < 2) {
    return
  }

  searchingRegions.value = true
  try {
    regionResults.value = await adminApi.searchRegions(keyword)
  } catch (err) {
    houseError.value = err.data?.message ?? err.message ?? '지역 검색에 실패했습니다.'
  } finally {
    searchingRegions.value = false
  }
}

function selectRegion(region) {
  selectedRegion.value = region
  houseForm.regionCode = region.regionCode
  regionKeyword.value = formatRegion(region)
  regionResults.value = []
}

function handleCollectAllChange() {
  if (!collectAllRegions.value) return
  selectedRegion.value = null
  regionKeyword.value = ''
  regionResults.value = []
  houseForm.regionCode = ''
}

function houseDealPayload() {
  return {
    ...houseForm,
    regionCode: collectAllRegions.value ? '' : houseForm.regionCode,
  }
}

function collectedRegionLabel() {
  const regionCode = houseResult.value?.parameters?.regionCode
  if (regionCode === 'ALL') return '전체 행정구역'
  return selectedRegion.value ? formatRegion(selectedRegion.value) : regionCode
}

async function handleCollectRegionCodes() {
  regionLoading.value = true
  regionResult.value = null
  regionError.value = ''
  try {
    regionResult.value = await adminApi.collectRegionCodes()
  } catch (err) {
    regionError.value = err.data?.message ?? err.message ?? '실행에 실패했습니다.'
  } finally {
    regionLoading.value = false
  }
}

async function handleCollectHouseDeals() {
  houseResult.value = null
  houseError.value = ''
  if (!collectAllRegions.value && !selectedRegion.value) {
    houseError.value = '지역을 검색해서 선택하거나 전체 수집을 선택해 주세요.'
    return
  }
  if (!/^\d{6}$/.test(houseForm.yearMonth)) {
    houseError.value = '기준 연월은 숫자 6자리로 입력해 주세요.'
    return
  }
  houseLoading.value = true
  try {
    houseResult.value = await adminApi.collectHouseDeals(houseDealPayload())
  } catch (err) {
    houseError.value = err.data?.message ?? err.message ?? '실행에 실패했습니다.'
  } finally {
    houseLoading.value = false
  }
}

async function handleLogout() {
  try {
    await authStore.logout()
  } finally {
    router.push('/login')
  }
}
</script>

<template>
  <div class="batch-page">
    <header class="batch-header">
      <div class="batch-header-title">
        <Shield :size="18" />
        <span>SSAFY HOME 관리자</span>
      </div>
      <div class="batch-header-user">
        <span>{{ authStore.user?.name }}</span>
        <button class="btn btn-ghost btn-sm" @click="handleLogout">로그아웃</button>
      </div>
    </header>

    <div class="batch-wrap">
      <section class="batch-section">
        <h2 class="batch-section-title">
          <MapPin :size="16" /> 법정동 코드 수집
        </h2>
        <p class="batch-section-desc">전국 법정동 코드를 최신 상태로 동기화합니다.</p>
        <button
          class="btn btn-primary btn-sm"
          :disabled="regionLoading"
          @click="handleCollectRegionCodes"
        >
          <Loader2 v-if="regionLoading" :size="14" class="animate-spin" />
          <Play v-else :size="14" />
          {{ regionLoading ? '실행 중...' : '실행' }}
        </button>
        <div v-if="regionResult" class="batch-result">
          <span class="batch-result-label">실행 완료</span>
          <span>Job ID: {{ regionResult.jobExecutionId }}</span>
          <span>상태: {{ regionResult.status }}</span>
        </div>
        <div v-if="regionError" class="general-error" style="margin-top:0.75rem;margin-bottom:0">
          <AlertCircle :size="16" /><span>{{ regionError }}</span>
        </div>
      </section>

      <section class="batch-section">
        <h2 class="batch-section-title">
          <Building2 :size="16" /> 주택 거래 데이터 수집
        </h2>
        <p class="batch-section-desc">지역·연월·주택 유형별 실거래가 데이터를 수집합니다.</p>

        <div class="batch-form-grid">
          <div class="batch-region-field">
            <div class="batch-field-head">
              <label class="form-label">수집 지역</label>
              <label class="batch-check">
                <input
                  v-model="collectAllRegions"
                  type="checkbox"
                  @change="handleCollectAllChange"
                />
                <span>전체</span>
              </label>
            </div>
            <div class="batch-search">
              <Search :size="16" />
              <input
                v-model="regionKeyword"
                class="batch-input batch-search-input"
                placeholder="예: 역삼동"
                :disabled="collectAllRegions"
                @input="searchRegions"
              />
            </div>
            <div v-if="searchingRegions" class="batch-search-state">
              <Loader2 :size="14" class="animate-spin" />
              <span>검색 중</span>
            </div>
            <div v-else-if="regionResults.length > 0" class="batch-region-results">
              <button
                v-for="region in regionResults"
                :key="region.regionCode"
                type="button"
                class="batch-region-option"
                @click="selectRegion(region)"
              >
                <span>{{ region.dongName || region.sigunguName }}</span>
                <small>{{ formatRegion(region) }}</small>
              </button>
            </div>
            <div v-if="selectedRegion" class="batch-selected-region">
              <MapPin :size="14" />
              <span>{{ formatRegion(selectedRegion) }}</span>
            </div>
          </div>
          <div>
            <label class="form-label" style="margin-bottom:0.5rem;display:block">기준 연월 (6자리)</label>
            <input
              v-model="houseForm.yearMonth"
              class="batch-input"
              placeholder="예: 202501"
              maxlength="6"
            />
          </div>
          <div>
            <label class="form-label" style="margin-bottom:0.5rem;display:block">주택 유형</label>
            <select v-model="houseForm.houseType" class="batch-input">
              <option value="APARTMENT">아파트</option>
              <option value="MULTI_FAMILY">연립·다세대</option>
            </select>
          </div>
          <div>
            <label class="form-label" style="margin-bottom:0.5rem;display:block">거래 유형</label>
            <select v-model="houseForm.dealType" class="batch-input">
              <option value="SALE">매매</option>
            </select>
          </div>
        </div>

        <button
          class="btn btn-primary btn-sm"
          :disabled="houseLoading"
          @click="handleCollectHouseDeals"
        >
          <Loader2 v-if="houseLoading" :size="14" class="animate-spin" />
          <Play v-else :size="14" />
          {{ houseLoading ? '실행 중...' : '실행' }}
        </button>
        <div v-if="houseResult" class="batch-result">
          <span class="batch-result-label">실행 완료</span>
          <span>Job ID: {{ houseResult.jobExecutionId }}</span>
          <span>상태: {{ houseResult.status }}</span>
          <span>지역: {{ collectedRegionLabel() }} / {{ houseResult.parameters?.yearMonth }}</span>
        </div>
        <div v-if="houseError" class="general-error" style="margin-top:0.75rem;margin-bottom:0">
          <AlertCircle :size="16" /><span>{{ houseError }}</span>
        </div>
      </section>
    </div>
  </div>
</template>

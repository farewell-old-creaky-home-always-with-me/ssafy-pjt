<template>
  <div class="favorites-page">
    <div class="favorites-wrap">
      <div class="favorites-header">
        <div>
          <h1 style="color:#1A3C6E;font-size:1.375rem;font-weight:700">관심지역</h1>
          <p style="color:#9ca3af;font-size:0.8125rem;margin-top:0.25rem">
            저장된 관심지역 <span style="color:#2D9CDB">{{ favoritesStore.count }}</span>개
          </p>
        </div>
        <button class="btn btn-primary btn-sm" @click="showAdd = !showAdd">
          <Plus v-if="!showAdd" :size="16" /><X v-else :size="16" />
          {{ showAdd ? '취소' : '관심지역 추가' }}
        </button>
      </div>

      <!-- Add form -->
      <div v-if="showAdd" class="add-form-card">
        <h3 style="color:#1A3C6E;font-size:0.9375rem;font-weight:600;margin-bottom:1rem">새 관심지역 등록</h3>
        <div class="add-form-fields">
          <div style="flex:1">
            <label class="form-label" style="font-size:0.75rem;margin-bottom:0.375rem;display:block">지역코드 (10자리)</label>
            <input v-model="newRegionCode" type="text" class="input-plain" placeholder="예: 1168010100" maxlength="10" />
          </div>
          <div style="display:flex;align-items:flex-end">
            <button class="btn btn-navy btn-sm" style="white-space:nowrap;height:2.5rem" :disabled="adding" @click="handleAdd">
              <Loader2 v-if="adding" :size="14" class="animate-spin" />추가
            </button>
          </div>
        </div>
        <p style="color:#9ca3af;font-size:0.75rem;margin-top:0.5rem">
          지역코드는 검색 페이지의 실거래가 상세 정보에서 확인할 수 있습니다.
        </p>
      </div>

      <!-- Search -->
      <div v-if="favoritesStore.count > 0" class="search-bar">
        <Search :size="18" />
        <input v-model="searchQuery" type="text" placeholder="지역명으로 검색..." />
      </div>

      <!-- Error -->
      <div v-if="favoritesStore.error" style="display:flex;align-items:center;gap:0.5rem;padding:1rem 1.25rem;background:#FEF2F2;border:1px solid #FECACA;border-radius:0.75rem;color:#DC2626;font-size:0.875rem;margin-bottom:1rem">
        <AlertTriangle :size="16" style="flex-shrink:0" />
        {{ favoritesStore.error }}
      </div>

      <!-- List -->
      <div id="fav-list-container">
        <div v-if="filtered.length === 0" class="empty-state">
          <div class="empty-icon"><Heart :size="32" /></div>
          <p style="color:#1A3C6E;font-size:1rem;font-weight:600;margin-bottom:0.25rem">
            {{ searchQuery ? '검색 결과가 없습니다' : '등록된 관심지역이 없습니다' }}
          </p>
          <p style="color:#9ca3af;font-size:0.8125rem;text-align:center">
            {{ searchQuery ? '다른 검색어로 시도해 주세요' : '상단의 추가 버튼으로 관심지역을 등록해 보세요' }}
          </p>
        </div>
        <div v-else class="fav-list">
          <div v-for="item in filtered" :key="item.favoriteId" class="fav-item">
            <div class="fav-icon"><MapPin :size="18" /></div>
            <div class="fav-info">
              <p class="fav-name">{{ item.dongName || item.sigunguName }}</p>
              <div class="fav-meta">
                <span><MapPin :size="12" />{{ item.sidoName }} {{ item.sigunguName }}</span>
                <span><Calendar :size="12" />{{ formatDate(item.createdAt) }}</span>
              </div>
            </div>
            <button class="fav-delete-btn" @click="startDelete(item)" aria-label="삭제"><Trash2 :size="16" /></button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Delete modal -->
  <div v-if="pendingDelete" class="modal-overlay visible">
    <div class="modal-backdrop" @click="pendingDelete = null"></div>
    <div class="modal-box">
      <div style="padding:2rem 1.5rem 0.5rem;text-align:center">
        <div class="confirm-modal-icon"><AlertTriangle :size="24" /></div>
        <h3 style="color:#1A3C6E;font-size:1.125rem;font-weight:700;margin-bottom:0.5rem">관심지역 삭제</h3>
        <p style="color:#9ca3af;font-size:0.8125rem">이 관심지역을 삭제하시겠습니까?</p>
      </div>
      <div style="display:flex;gap:0.75rem;padding:1.5rem">
        <button class="btn btn-ghost btn-full" @click="pendingDelete = null">취소</button>
        <button class="btn btn-danger btn-full" :disabled="deleting" @click="confirmDelete">
          <Loader2 v-if="deleting" :size="14" class="animate-spin" />삭제
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, X, Search, Heart, MapPin, Calendar, Trash2, AlertTriangle, Loader2 } from 'lucide-vue-next'
import { useFavoritesStore } from '../stores/favorites.js'
import { formatDate } from '../utils/date.js'
import '../../css/pages/favorites.css'

const favoritesStore = useFavoritesStore()

const showAdd = ref(false)
const newRegionCode = ref('')
const adding = ref(false)
const searchQuery = ref('')
const pendingDelete = ref(null)
const deleting = ref(false)

const filtered = computed(() => {
  const q = searchQuery.value.toLowerCase()
  return favoritesStore.items.filter(f =>
    !q || ((f.dongName ?? '') + (f.sigunguName ?? '') + (f.sidoName ?? '')).toLowerCase().includes(q)
  )
})


async function handleAdd() {
  if (!newRegionCode.value.trim() || newRegionCode.value.length !== 10) {
    alert('지역코드는 10자리여야 합니다')
    return
  }
  adding.value = true
  try {
    await favoritesStore.addFavorite(newRegionCode.value.trim())
    newRegionCode.value = ''
    showAdd.value = false
  } catch (err) {
    alert(err.data?.message ?? '추가에 실패했습니다')
  } finally {
    adding.value = false
  }
}

function startDelete(item) { pendingDelete.value = item }

async function confirmDelete() {
  if (!pendingDelete.value) return
  deleting.value = true
  try {
    await favoritesStore.removeFavorite(pendingDelete.value.favoriteId)
  } finally {
    deleting.value = false
    pendingDelete.value = null
  }
}

onMounted(() => favoritesStore.fetchFavorites())
</script>

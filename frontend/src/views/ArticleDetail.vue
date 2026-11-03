<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getArticle, deleteArticle, API_BASE } from '../api/article'
import { useAuth } from '../auth'

const props = defineProps({ id: String })
const router = useRouter()
const { state } = useAuth()

const article = ref(null)
const loading = ref(true)
const error = ref('')

// 只有作者本人能看到编辑/删除按钮
const isOwner = computed(
  () => article.value && state.username && article.value.author === state.username
)

function imageList(images) {
  if (!images) return []
  return images.split(',').filter(Boolean)
}

function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleString('zh-CN')
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await getArticle(props.id)
    article.value = data
  } catch (e) {
    error.value = '文章不存在或后端未启动。'
  } finally {
    loading.value = false
  }
}

function goEdit() {
  router.push(`/edit/${props.id}`)
}

async function goDelete() {
  if (!confirm('确定要删除这篇文章吗？此操作不可恢复。')) return
  try {
    await deleteArticle(props.id)
    router.push('/')
  } catch (e) {
    if (e.response?.status === 403) {
      alert('只能删除自己的文章。')
    } else {
      alert('删除失败，请稍后重试。')
    }
  }
}

onMounted(load)
// 路由参数变化时重新加载（同一组件切换不同文章）
watch(() => props.id, load)
</script>

<template>
  <section class="detail">
    <p v-if="loading" class="tip">加载中…</p>
    <p v-else-if="error" class="tip error">{{ error }}</p>

    <article v-else-if="article" class="article">
      <RouterLink to="/" class="back">← 返回首页</RouterLink>
      <h1 class="article-title">{{ article.title }}</h1>
      <div class="article-meta">
        <span class="article-date">{{ formatDate(article.createdAt) }}</span>
        <span v-if="article.author" class="article-author">✍️ {{ article.author }}</span>
        <span v-if="article.updatedAt && article.updatedAt !== article.createdAt" class="article-date">
          （编辑于 {{ formatDate(article.updatedAt) }}）
        </span>
      </div>

      <div v-if="imageList(article.images).length" class="gallery">
        <img
          v-for="(img, i) in imageList(article.images)"
          :key="i"
          :src="API_BASE + img"
          :alt="article.title"
        />
      </div>

      <!-- 正文按换行展示 -->
      <div class="article-content">
        <p v-for="(line, i) in article.content.split('\n')" :key="i">{{ line }}</p>
      </div>

      <!-- 仅作者可见的操作按钮 -->
      <div v-if="isOwner" class="article-actions">
        <button class="btn-edit" @click="goEdit">✏️ 编辑</button>
        <button class="btn-delete" @click="goDelete">🗑️ 删除</button>
      </div>
    </article>
  </section>
</template>

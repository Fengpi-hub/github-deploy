<script setup>
import { ref, onMounted } from 'vue'
import { listArticles, API_BASE } from '../api/article'

const articles = ref([])
const loading = ref(true)
const error = ref('')

// 把后端逗号分隔的图片字段转成数组
function imageList(images) {
  if (!images) return []
  return images.split(',').filter(Boolean)
}

// 把正文截成摘要
function excerpt(content) {
  if (!content) return ''
  return content.replace(/\n/g, ' ').slice(0, 60) + (content.length > 60 ? '…' : '')
}

function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleDateString('zh-CN')
}

onMounted(async () => {
  try {
    const { data } = await listArticles()
    articles.value = data
  } catch (e) {
    error.value = '加载失败，请确认后端服务（Spring Boot :8080）已启动。'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <section class="home">
    <h1 class="page-title">🍊 今天吃点什么水果？</h1>

    <p v-if="loading" class="tip">加载中…</p>
    <p v-else-if="error" class="tip error">{{ error }}</p>

    <div v-else-if="articles.length === 0" class="tip">
      还没有文章，去 <RouterLink to="/admin">写第一篇</RouterLink> 吧！
    </div>

    <div v-else class="card-grid">
      <RouterLink
        v-for="a in articles"
        :key="a.id"
        :to="`/article/${a.id}`"
        class="card"
      >
        <div class="card-cover">
          <img
            v-if="imageList(a.images).length"
            :src="API_BASE + imageList(a.images)[0]"
            :alt="a.title"
          />
          <div v-else class="card-cover-empty">🍎</div>
        </div>
        <div class="card-body">
          <h2 class="card-title">{{ a.title }}</h2>
          <p class="card-excerpt">{{ excerpt(a.content) }}</p>
          <span class="card-date">{{ formatDate(a.createdAt) }}</span>
        </div>
      </RouterLink>
    </div>
  </section>
</template>

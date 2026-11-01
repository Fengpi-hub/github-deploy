<script setup>
import { ref, onMounted, watch } from 'vue'
import { getArticle, API_BASE } from '../api/article'

const props = defineProps({ id: String })

const article = ref(null)
const loading = ref(true)
const error = ref('')

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
      <span class="article-date">{{ formatDate(article.createdAt) }}</span>

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
    </article>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticle, updateArticle, API_BASE } from '../api/article'

const props = defineProps({ id: String })
const router = useRouter()

const article = ref(null)
const title = ref('')
const content = ref('')
const files = ref([])        // 选中的新图片 File 对象
const previews = ref([])     // 本地预览地址
const clearImages = ref(false)
const submitting = ref(false)
const message = ref('')
const isError = ref(false)
const loading = ref(true)
const notFound = ref(false)

function imageList(images) {
  if (!images) return []
  return images.split(',').filter(Boolean)
}

function onFileChange(e) {
  const list = Array.from(e.target.files || [])
  files.value = list
  previews.value = list.map((f) => URL.createObjectURL(f))
}

async function submit() {
  if (!title.value.trim() || !content.value.trim()) {
    message.value = '标题和正文都不能为空～'
    isError.value = true
    return
  }
  submitting.value = true
  message.value = ''
  try {
    const { data } = await updateArticle(
      props.id,
      { title: title.value, content: content.value },
      files.value,
      clearImages.value
    )
    message.value = '保存成功！正在跳转到文章…'
    isError.value = false
    setTimeout(() => router.push(`/article/${data.id}`), 800)
  } catch (e) {
    if (e.response?.status === 403) {
      message.value = '只能修改自己的文章。'
    } else if (e.response?.status === 401) {
      message.value = '登录已失效，请重新登录。'
    } else {
      message.value = '保存失败，请稍后重试。'
    }
    isError.value = true
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    const { data } = await getArticle(props.id)
    article.value = data
    title.value = data.title
    content.value = data.content
  } catch (e) {
    notFound.value = true
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <section class="admin">
    <p v-if="loading" class="tip">加载中…</p>
    <p v-else-if="notFound" class="tip error">文章不存在或无权访问。</p>

    <div v-else>
      <h1 class="page-title">✏️ 编辑文章</h1>
      <form class="editor" @submit.prevent="submit">
        <label class="field">
          <span>标题</span>
          <input v-model="title" type="text" placeholder="文章标题" />
        </label>

        <label class="field">
          <span>正文</span>
          <textarea
            v-model="content"
            rows="10"
            placeholder="介绍一下这种水果的味道、营养、挑选技巧…（换行会被保留）"
          ></textarea>
        </label>

        <div class="field" v-if="imageList(article.images).length">
          <span>现有图片</span>
          <div class="preview-grid">
            <div v-for="(img, i) in imageList(article.images)" :key="i" class="preview-item">
              <img :src="API_BASE + img" alt="图片" />
            </div>
          </div>
          <label class="checkbox">
            <input type="checkbox" v-model="clearImages" />
            清除现有图片（勾选后保存将删除全部旧图）
          </label>
        </div>

        <div class="field">
          <span>替换图片（可选，上传后会替换全部旧图）</span>
          <input type="file" accept="image/*" multiple @change="onFileChange" />
          <div v-if="previews.length" class="preview-grid">
            <div v-for="(p, i) in previews" :key="i" class="preview-item">
              <img :src="p" alt="预览" />
            </div>
          </div>
        </div>

        <div class="actions">
          <button type="submit" :disabled="submitting">
            {{ submitting ? '保存中…' : '保存修改' }}
          </button>
        </div>

        <p v-if="message" :class="['msg', isError ? 'error' : 'ok']">{{ message }}</p>
      </form>
    </div>
  </section>
</template>

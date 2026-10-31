<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createArticle } from '../api/article'

const router = useRouter()

const title = ref('')
const content = ref('')
const files = ref([])        // 选中的 File 对象
const previews = ref([])     // 本地预览地址
const submitting = ref(false)
const message = ref('')
const isError = ref(false)

// 选择图片：支持多选，生成本地预览
function onFileChange(e) {
  const list = Array.from(e.target.files || [])
  files.value = list
  previews.value = list.map((f) => URL.createObjectURL(f))
}

function removeImage(index) {
  files.value.splice(index, 1)
  URL.revokeObjectURL(previews.value[index])
  previews.value.splice(index, 1)
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
    const { data } = await createArticle(
      { title: title.value, content: content.value },
      files.value
    )
    message.value = '发布成功！正在跳转到文章…'
    isError.value = false
    setTimeout(() => router.push(`/article/${data.id}`), 800)
  } catch (e) {
    message.value = '发布失败，请确认后端服务（Spring Boot :8080）已启动，且图片大小在 10MB 以内。'
    isError.value = true
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="admin">
    <h1 class="page-title">✍️ 发布水果文章</h1>

    <form class="editor" @submit.prevent="submit">
      <label class="field">
        <span>标题</span>
        <input v-model="title" type="text" placeholder="例如：🍇 葡萄的营养价值" />
      </label>

      <label class="field">
        <span>正文</span>
        <textarea
          v-model="content"
          rows="10"
          placeholder="介绍一下这种水果的味道、营养、挑选技巧…（换行会被保留）"
        ></textarea>
      </label>

      <div class="field">
        <span>插图（可多选）</span>
        <input type="file" accept="image/*" multiple @change="onFileChange" />
        <div v-if="previews.length" class="preview-grid">
          <div v-for="(p, i) in previews" :key="i" class="preview-item">
            <img :src="p" alt="预览" />
            <button type="button" class="remove" @click="removeImage(i)">×</button>
          </div>
        </div>
      </div>

      <div class="actions">
        <button type="submit" :disabled="submitting">
          {{ submitting ? '发布中…' : '发布文章' }}
        </button>
      </div>

      <p v-if="message" :class="['msg', isError ? 'error' : 'ok']">{{ message }}</p>
    </form>
  </section>
</template>

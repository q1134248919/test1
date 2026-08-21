import request from './request'

export const fetchMemories = () => request.get('/memories')

export const uploadMemory = (file, caption = '') => {
  const form = new FormData()
  form.append('file', file)
  if (caption) form.append('caption', caption)
  return request.post('/memories', form, { timeout: 60000 })
}

export const deleteMemory = (id) => request.delete(`/memories/${id}`)

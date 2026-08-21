import request from './request'

export const fetchTodos = () => request.get('/todos')
export const addTodo = (data) => request.post('/todos', data)
export const toggleTodo = (id) => request.put(`/todos/${id}/toggle`)
export const deleteTodo = (id) => request.delete(`/todos/${id}`)

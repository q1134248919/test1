import request from './request'

export const createFamily = (data) => request.post('/family', data)
export const joinFamily = (data) => request.post('/family/join', data)
export const fetchMyFamily = () => request.get('/family/mine')
export const leaveFamily = () => request.post('/family/leave')
export const dissolveFamily = () => request.delete('/family')
export const refreshInviteCode = () => request.post('/family/invite-code')

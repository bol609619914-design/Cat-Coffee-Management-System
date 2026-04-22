import http from './http'

export const login = (data) => http.post('/auth/login', data)
export const register = (data) => http.post('/auth/register', data)
export const refreshToken = (data) => http.post('/auth/refresh', data)
export const logout = () => http.post('/auth/logout')
export const changePassword = (data) => http.post('/auth/password/change', data)
export const fetchCurrentUser = () => http.get('/auth/me')

export const fetchDashboard = () => http.get('/dashboard')
export const uploadFile = (data, biz = 'common') => http.post(`/files/upload?biz=${biz}`, data)

export const fetchCats = (params) => http.get('/cats', { params })
export const saveCat = (data) => http.post('/cats', data)
export const deleteCat = (id) => http.delete(`/cats/${id}`)

export const fetchDrinks = (params) => http.get('/drinks', { params })
export const saveDrink = (data) => http.post('/drinks', data)
export const deleteDrink = (id) => http.delete(`/drinks/${id}`)

export const fetchTables = (params) => http.get('/tables', { params })
export const saveTable = (data) => http.post('/tables', data)
export const deleteTable = (id) => http.delete(`/tables/${id}`)

export const fetchReservations = (params) => http.get('/reservations', { params })
export const saveReservation = (data) => http.post('/reservations', data)
export const deleteReservation = (id) => http.delete(`/reservations/${id}`)

export const fetchOrders = (params) => http.get('/orders', { params })
export const saveOrder = (data) => http.post('/orders', data)
export const deleteOrder = (id) => http.delete(`/orders/${id}`)

export const fetchPointSummary = () => http.get('/marketing/points/summary')
export const fetchPointFlows = (params) => http.get('/marketing/points/flows', { params })

export const fetchCouponTemplates = (params) => http.get('/marketing/coupons/templates', { params })
export const saveCouponTemplate = (data) => http.post('/marketing/coupons/templates', data)
export const deleteCouponTemplate = (id) => http.delete(`/marketing/coupons/templates/${id}`)
export const issueCoupon = (id, data) => http.post(`/marketing/coupons/templates/${id}/issue`, data)
export const receiveCoupon = (id) => http.post(`/marketing/coupons/templates/${id}/receive`)
export const fetchUserCoupons = (params) => http.get('/marketing/coupons/user', { params })

export const fetchReviews = (params) => http.get('/marketing/reviews', { params })
export const fetchReviewOptions = () => http.get('/marketing/reviews/options')
export const saveReview = (data) => http.post('/marketing/reviews', data)
export const deleteReview = (id) => http.delete(`/marketing/reviews/${id}`)

export const fetchActivities = (params) => http.get('/marketing/activities', { params })
export const saveActivity = (data) => http.post('/marketing/activities', data)
export const deleteActivity = (id) => http.delete(`/marketing/activities/${id}`)

export const fetchSystemUsers = (params) => http.get('/system/users', { params })
export const saveSystemUser = (data) => http.post('/system/users', data)
export const deleteSystemUser = (id) => http.delete(`/system/users/${id}`)
export const resetSystemUserPassword = (id, data) => http.post(`/system/users/${id}/reset-password`, data)

export const fetchSystemRoles = (params) => http.get('/system/roles', { params })
export const saveSystemRole = (data) => http.post('/system/roles', data)
export const deleteSystemRole = (id) => http.delete(`/system/roles/${id}`)

export const fetchSystemPermissions = (params) => http.get('/system/permissions', { params })
export const saveSystemPermission = (data) => http.post('/system/permissions', data)
export const deleteSystemPermission = (id) => http.delete(`/system/permissions/${id}`)

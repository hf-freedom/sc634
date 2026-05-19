import axios from 'axios'
const api = axios.create({
  baseURL: 'http://localhost:8003/api',
  timeout: 10000
})
export default {
  getUsers() { return api.get('/users') },
  getUser(id) { return api.get(`/users/${id}`) },
  createUser(user) { return api.post('/users', user) },
  getCourses() { return api.get('/courses') },
  getCourse(id) { return api.get(`/courses/${id}`) },
  createCourse(course) { return api.post('/courses', course) },
  updateCourse(id, course) { return api.put(`/courses/${id}`, course) },
  getExamQuestions(id) { return api.get(`/courses/${id}/questions`) },
  enrollCourse(userId, courseId) { return api.post(`/learning/enroll?userId=${userId}&courseId=${courseId}`) },
  getLearningProgress(userId, courseId) { return api.get(`/learning/progress?userId=${userId}&courseId=${courseId}`) },
  completeTask(userId, courseId, taskId) { return api.post(`/learning/complete-task?userId=${userId}&courseId=${courseId}&taskId=${taskId}`) },
  canTakeExam(userId, courseId) { return api.get(`/learning/can-take-exam?userId=${userId}&courseId=${courseId}`) },
  getEnrollments(userId) { return api.get(`/learning/enrollments/${userId}`) },
  startExam(userId, courseId) { return api.post(`/exam/start?userId=${userId}&courseId=${courseId}`) },
  submitExam(examRecordId, answers) { return api.post(`/exam/submit?examRecordId=${examRecordId}`, { answers }) },
  canRetakeExam(userId, courseId) { return api.get(`/exam/can-retake?userId=${userId}&courseId=${courseId}`) },
  getExamRecords(userId, courseId) { return api.get(`/exam/records?userId=${userId}&courseId=${courseId}`) },
  getCertificates(userId) { return api.get(`/certificates/user/${userId}`) },
  getStatistics() { return api.get('/statistics') }
}

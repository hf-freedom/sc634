import { createRouter, createWebHashHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Admin from '../views/Admin.vue'
import Employee from '../views/Employee.vue'
const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/admin', name: 'Admin', component: Admin },
  { path: '/employee', name: 'Employee', component: Employee }
]
const router = createRouter({
  history: createWebHashHistory(),
  routes
})
export default router

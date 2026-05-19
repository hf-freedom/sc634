<template>
  <div class="employee">
    <el-container style="height: 100vh">
      <el-header style="background: #409EFF; color: white; padding: 0 30px; display: flex; align-items: center; justify-content: space-between;">
        <h1 style="margin: 0; font-size: 20px;">员工学习中心 - 当前用户: {{ currentUser?.name }}</h1>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </el-header>
      <el-container>
        <el-aside width="200px" style="background: white; border-right: 1px solid #eee;">
          <el-menu :default-active="activeMenu" @select="handleSelect">
            <el-menu-item index="courses">📚 课程列表</el-menu-item>
            <el-menu-item index="learning">📖 我的学习</el-menu-item>
            <el-menu-item index="exams">✍️ 考试中心</el-menu-item>
            <el-menu-item index="certificates">🏆 我的证书</el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <div v-if="activeMenu === 'courses'">
            <h2>可报名课程</h2>
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="8" v-for="course in courses" :key="course.id">
                <el-card shadow="hover">
                  <h3>{{ course.title }}</h3>
                  <p style="color: #909399; height: 60px; overflow: hidden;">{{ course.description }}</p>
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span>任务数: {{ course.learningTasks?.length || 0 }}</span>
                    <el-button type="primary" size="small" @click="enrollCourse(course.id)" :disabled="isEnrolled(course.id) || !isCourseValid(course)">
                      {{ isEnrolled(course.id) ? '已报名' : (!isCourseValid(course) ? '课程不完整' : '立即报名') }}
                    </el-button>
                  </div>
                  <el-tag v-if="!isCourseValid(course)" type="danger" size="small" style="margin-top: 10px;">
                    课程不完整（缺少学习任务或考试题）
                  </el-tag>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <div v-if="activeMenu === 'learning'">
            <h2>我的学习</h2>
            <el-table :data="enrollments" border style="width: 100%; margin-top: 20px;">
              <el-table-column prop="courseId" label="课程ID" width="100"></el-table-column>
              <el-table-column prop="courseTitle" label="课程名称"></el-table-column>
              <el-table-column label="学习进度" width="200">
                <template #default="scope">
                  <el-progress :percentage="getProgressPercentage(scope.row.courseId)"></el-progress>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="openLearning(scope.row.courseId)">继续学习</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div v-if="activeMenu === 'exams'">
            <h2>考试中心</h2>
            <el-row :gutter="20" style="margin: 20px 0;">
              <el-col :span="6">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 32px; color: #409EFF; font-weight: bold;">
                      {{ enrollments.length }}
                    </div>
                    <div style="color: #909399; margin-top: 5px;">已报名课程</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 32px; color: #67C23A; font-weight: bold;">
                      {{ passedExamCount }}
                    </div>
                    <div style="color: #909399; margin-top: 5px;">已通过考试</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 32px; color: #E6A23C; font-weight: bold;">
                      {{ allExamRecords.length }}
                    </div>
                    <div style="color: #909399; margin-top: 5px;">总考试次数</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 32px; color: #F56C6C; font-weight: bold;">
                      {{ certificates.length }}
                    </div>
                    <div style="color: #909399; margin-top: 5px;">获得证书数</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-table :data="enrollments" border style="width: 100%; margin-top: 20px;">
              <el-table-column prop="courseId" label="课程ID" width="100"></el-table-column>
              <el-table-column prop="courseTitle" label="课程名称" width="180"></el-table-column>
              <el-table-column label="学习状态" width="120">
                <template #default="scope">
                  <el-tag :type="isLearningComplete(scope.row.courseId) ? 'success' : 'warning'">
                    {{ isLearningComplete(scope.row.courseId) ? '已完成' : '学习中' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="已考次数" width="100" align="center">
                <template #default="scope">
                  <span style="font-weight: bold; color: #409EFF;">
                    {{ getExamCount(scope.row.courseId) }}
                  </span> / 3
                </template>
              </el-table-column>
              <el-table-column label="历史最高分" width="120" align="center">
                <template #default="scope">
                  <span v-if="getHighestScore(scope.row.courseId) !== null" style="font-weight: bold; color: #67C23A;">
                    {{ getHighestScore(scope.row.courseId) }} 分
                  </span>
                  <span v-else style="color: #909399;">-</span>
                </template>
              </el-table-column>
              <el-table-column label="考试状态" width="200">
                <template #default="scope">
                  <div v-if="getLatestExam(scope.row.courseId)?.passed">
                    <el-tag type="success" size="small">✅ 已通过考试</el-tag>
                  </div>
                  <div v-else>
                    <el-tag v-if="!isLearningComplete(scope.row.courseId)" type="info" size="small">
                      📚 需先完成学习
                    </el-tag>
                    <el-tag v-else-if="needsRelearn(scope.row.courseId)" type="danger" size="small">
                      🔄 补考超限，需重修
                    </el-tag>
                    <el-tag v-else-if="getExamCount(scope.row.courseId) === 0" type="primary" size="small">
                      🎯 首次考试
                    </el-tag>
                    <el-tag v-else type="warning" size="small">
                      🔁 第 {{ getExamCount(scope.row.courseId) + 1 }} 次补考
                    </el-tag>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button v-if="getLatestExam(scope.row.courseId)?.passed" size="small" type="success" @click="activeMenu = 'certificates'">
                    📜 查看证书
                  </el-button>
                  <el-button v-else size="small" type="primary" 
                    @click="startExam(scope.row.courseId)"
                    :disabled="!canTakeExam(scope.row.courseId)">
                    <span v-if="getExamCount(scope.row.courseId) === 0">🎯 开始考试</span>
                    <span v-else>🔁 参加补考</span>
                  </el-button>
                  <el-button v-if="needsRelearn(scope.row.courseId)" size="small" type="warning" @click="openLearning(scope.row.courseId)">
                    🔄 重新学习
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="allExamRecords.length > 0" style="margin-top: 30px;">
              <h3>考试记录</h3>
              <el-table :data="allExamRecords" border style="width: 100%; margin-top: 20px;">
                <el-table-column prop="courseId" label="课程ID" width="100"></el-table-column>
                <el-table-column prop="courseTitle" label="课程名称" width="180"></el-table-column>
                <el-table-column label="考试类型" width="120">
                  <template #default="scope">
                    <el-tag :type="scope.attemptNumber === 1 ? 'primary' : 'warning'" size="small">
                      {{ scope.attemptNumber === 1 ? '首次考试' : '第' + scope.attemptNumber + '次补考' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="score" label="得分" width="100" align="center">
                  <template #default="scope">
                    <span :style="{ fontWeight: 'bold', color: scope.passed ? '#67C23A' : '#F56C6C' }">
                      {{ scope.score }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column label="是否通过" width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.passed ? 'success' : 'danger'" size="small">
                      {{ scope.passed ? '通过' : '未通过' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="submitTime" label="提交时间"></el-table-column>
              </el-table>
            </div>
          </div>
          <div v-if="activeMenu === 'certificates'">
            <h2>我的证书</h2>
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="8" v-for="cert in certificates" :key="cert.id">
                <el-card shadow="hover" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                  <h3 style="color: white;">🏆 培训证书</h3>
                  <p>证书编号: {{ cert.certificateNumber }}</p>
                  <p>课程ID: {{ cert.courseId }}</p>
                  <p>颁发日期: {{ formatDate(cert.issuedAt) }}</p>
                  <p>有效期至: {{ formatDate(cert.expiredAt) }}</p>
                  <el-tag v-if="isExpired(cert.expiredAt)" type="danger">已过期</el-tag>
                  <el-tag v-else-if="isExpiring(cert.expiredAt)" type="warning">即将过期</el-tag>
                  <el-tag v-else type="success">有效</el-tag>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-if="certificates.length === 0" description="暂无证书" style="margin-top: 50px;"></el-empty>
          </div>
        </el-main>
      </el-container>
    </el-container>
    <el-dialog v-model="showLearningDialog" title="课程学习" width="800px">
      <div v-if="currentCourse">
        <h3>{{ currentCourse.title }}</h3>
        <el-timeline>
          <el-timeline-item v-for="(task, index) in currentCourse.learningTasks" :key="task.id"
            :type="isTaskCompleted(task.id) ? 'success' : ''"
            :icon="isTaskCompleted(task.id) ? 'Check' : ''">
            <h4>{{ task.title }}</h4>
            <p>{{ task.content }}</p>
            <el-button v-if="!isTaskCompleted(task.id)" size="small" type="primary" @click="completeTask(task.id)">
              完成任务
            </el-button>
            <el-tag v-else type="success">已完成</el-tag>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
    <el-dialog v-model="showExamDialog" title="在线考试" width="900px" :close-on-click-modal="false">
      <div v-if="currentExam">
        <el-alert :title="'考试进行中，共 ' + examQuestions.length + ' 题'" type="info" show-icon style="margin-bottom: 20px;"></el-alert>
        <div v-for="(q, index) in examQuestions" :key="q.id" style="margin-bottom: 30px; padding: 20px; background: #f9f9f9; border-radius: 8px;">
          <h4>第 {{ index + 1 }} 题 ({{ q.score }}分)</h4>
          <p style="font-size: 16px; margin: 15px 0;">{{ q.question }}</p>
          <el-radio-group v-model="userAnswers[index]" style="display: flex; flex-direction: column; gap: 10px;">
            <el-radio v-for="(opt, optIndex) in q.options" :key="optIndex" :label="optIndex">
              {{ String.fromCharCode(65 + optIndex) }}. {{ opt }}
            </el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" size="large" @click="submitExam">提交试卷</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="showExamResultDialog" title="考试结果" width="600px" :close-on-click-modal="false">
      <div v-if="examResult" style="text-align: center; padding: 20px;">
        <div :style="{ fontSize: '80px', marginBottom: '20px' }">
          {{ examResult.passed ? '🎉' : '😢' }}
        </div>
        <h2 :style="{ color: examResult.passed ? '#67C23A' : '#F56C6C', marginBottom: '20px' }">
          {{ examResult.passed ? '恭喜，考试通过！' : '很遗憾，考试未通过' }}
        </h2>
        <el-descriptions :column="1" border style="margin: 30px 0;">
          <el-descriptions-item label="考试得分">
            <span style="font-size: 24px; font-weight: bold; color: #409EFF;">{{ examResult.score }}</span> 分
          </el-descriptions-item>
          <el-descriptions-item label="考试状态">
            <el-tag :type="examResult.passed ? 'success' : 'danger'" size="large">
              {{ examResult.passed ? '通过' : '未通过' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="考试次数">
            第 {{ examResult.attemptNumber }} 次
          </el-descriptions-item>
        </el-descriptions>
        <el-alert v-if="!examResult.passed" title="补考提示" type="warning" show-icon>
          您还有补考机会，请再接再厉！补考3次未通过需重新学习课程。
        </el-alert>
      </div>
      <template #footer>
        <el-button type="primary" @click="showExamResultDialog = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import api from '../api'
export default {
  name: 'Employee',
  data() {
    return {
      activeMenu: 'courses',
      currentUser: { id: 2, name: '张三', role: 'EMPLOYEE' },
      courses: [],
      enrollments: [],
      learningProgresses: {},
      certificates: [],
      showLearningDialog: false,
      showExamDialog: false,
      showExamResultDialog: false,
      currentCourse: null,
      currentExam: null,
      examResult: null,
      examQuestions: [],
      userAnswers: [],
      examRecords: {},
      allExamRecords: [],
      canTakeExamMap: {},
      passedExamCount: 0
    }
  },
  mounted() {
    this.loadCourses()
    this.loadEnrollments()
    this.loadCertificates()
  },
  methods: {
    handleSelect(key) {
      this.activeMenu = key
    },
    async loadCourses() {
      const res = await api.getCourses()
      this.courses = res.data.data || []
    },
    async loadEnrollments() {
      const res = await api.getEnrollments(this.currentUser.id)
      this.enrollments = res.data.data || []
      for (const enroll of this.enrollments) {
        const course = this.courses.find(c => c.id === enroll.courseId)
        if (course) {
          enroll.courseTitle = course.title
        }
        await this.loadLearningProgress(enroll.courseId)
        await this.loadExamRecords(enroll.courseId)
        await this.loadCanTakeExam(enroll.courseId)
      }
    },
    async loadCanTakeExam(courseId) {
      if (!this.isLearningComplete(courseId)) {
        this.canTakeExamMap[courseId] = false
        return
      }
      const res = await api.canRetakeExam(this.currentUser.id, courseId)
      this.canTakeExamMap[courseId] = res.data.data
    },
    async loadLearningProgress(courseId) {
      const res = await api.getLearningProgress(this.currentUser.id, courseId)
      this.learningProgresses[courseId] = res.data.data
    },
    async loadExamRecords(courseId) {
      const res = await api.getExamRecords(this.currentUser.id, courseId)
      const records = res.data.data || []
      const course = this.courses.find(c => c.id === courseId)
      records.forEach(r => {
        r.courseTitle = course?.title || '未知课程'
      })
      this.examRecords[courseId] = records
      this.allExamRecords = Object.values(this.examRecords).flat()
      this.updatePassedCount()
    },
    updatePassedCount() {
      let count = 0
      for (const courseId in this.examRecords) {
        const records = this.examRecords[courseId]
        if (records.some(r => r.passed)) {
          count++
        }
      }
      this.passedExamCount = count
    },
    async loadCertificates() {
      const res = await api.getCertificates(this.currentUser.id)
      this.certificates = res.data.data || []
    },
    isEnrolled(courseId) {
      return this.enrollments.some(e => e.courseId === courseId)
    },
    isCourseValid(course) {
      const hasTasks = course.learningTasks && course.learningTasks.length > 0
      const hasQuestions = course.examQuestions && course.examQuestions.length > 0
      return hasTasks && hasQuestions
    },
    async enrollCourse(courseId) {
      try {
        const res = await api.enrollCourse(this.currentUser.id, courseId)
        if (res.data.success) {
          await this.loadEnrollments()
          alert('报名成功！')
        } else {
          alert(res.data.message || '报名失败！')
        }
      } catch (e) {
        alert('报名失败：' + (e.response?.data?.message || e.message))
      }
    },
    getProgressPercentage(courseId) {
      const progress = this.learningProgresses[courseId]
      if (!progress) return 0
      const course = this.courses.find(c => c.id === courseId)
      const total = course?.learningTasks?.length || 0
      if (total === 0) return 0
      return Math.round((progress.completedTaskIds?.length || 0) / total * 100)
    },
    isLearningComplete(courseId) {
      const course = this.courses.find(c => c.id === courseId)
      const totalTasks = course?.learningTasks?.length || 0
      if (totalTasks === 0) return false
      return this.getProgressPercentage(courseId) >= 100
    },
    isTaskCompleted(taskId) {
      const progress = this.learningProgresses[this.currentCourse.id]
      return progress?.completedTaskIds?.includes(taskId) || false
    },
    openLearning(courseId) {
      this.currentCourse = this.courses.find(c => c.id === courseId)
      this.showLearningDialog = true
    },
    async completeTask(taskId) {
      await api.completeTask(this.currentUser.id, this.currentCourse.id, taskId)
      await this.loadLearningProgress(this.currentCourse.id)
      await this.loadCanTakeExam(this.currentCourse.id)
    },
    canTakeExam(courseId) {
      return this.canTakeExamMap[courseId] || false
    },
    needsRelearn(courseId) {
      const records = this.examRecords[courseId] || []
      if (records.some(r => r.passed)) return false
      return records.length >= 3
    },
    getLatestExam(courseId) {
      const records = this.examRecords[courseId] || []
      return records[0]
    },
    getExamCount(courseId) {
      const records = this.examRecords[courseId] || []
      return records.length
    },
    getHighestScore(courseId) {
      const records = this.examRecords[courseId] || []
      if (records.length === 0) return null
      return Math.max(...records.map(r => r.score))
    },
    async startExam(courseId) {
      try {
        const res = await api.startExam(this.currentUser.id, courseId)
        if (res.data.success) {
          this.currentExam = res.data.data
          const questionsRes = await api.getExamQuestions(courseId)
          this.examQuestions = questionsRes.data.data || []
          if (this.examQuestions.length === 0) {
            alert('该课程暂无考试题目！')
            return
          }
          this.userAnswers = new Array(this.examQuestions.length).fill(null)
          this.showExamDialog = true
        } else {
          alert(res.data.message || '无法开始考试！')
        }
      } catch (e) {
        alert('开始考试失败：' + (e.response?.data?.message || e.message))
      }
    },
    async submitExam() {
      if (this.userAnswers.includes(null)) {
        alert('请完成所有题目！')
        return
      }
      try {
        const res = await api.submitExam(this.currentExam.id, this.userAnswers)
        if (res.data.success) {
          const result = res.data.data
          this.examResult = result
          this.showExamDialog = false
          this.showExamResultDialog = true
          await this.loadExamRecords(this.currentExam.courseId)
          await this.loadCanTakeExam(this.currentExam.courseId)
          await this.loadCertificates()
        } else {
          alert(res.data.message || '提交失败！')
        }
      } catch (e) {
        alert('提交失败：' + (e.response?.data?.message || e.message))
      }
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleDateString('zh-CN')
    },
    isExpired(expiredAt) {
      return new Date(expiredAt) < new Date()
    },
    isExpiring(expiredAt) {
      const expireDate = new Date(expiredAt)
      const now = new Date()
      const days = (expireDate - now) / (1000 * 60 * 60 * 24)
      return days <= 30 && days > 0
    }
  }
}
</script>
<style scoped>
.employee {
  width: 100%;
}
</style>

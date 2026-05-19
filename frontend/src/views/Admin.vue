<template>
  <div class="admin">
    <el-container style="height: 100vh">
      <el-header style="background: #409EFF; color: white; padding: 0 30px; display: flex; align-items: center; justify-content: space-between;">
        <h1 style="margin: 0; font-size: 20px;">管理员控制台</h1>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </el-header>
      <el-container>
        <el-aside width="200px" style="background: white; border-right: 1px solid #eee;">
          <el-menu :default-active="activeMenu" @select="handleSelect">
            <el-menu-item index="courses">📚 课程管理</el-menu-item>
            <el-menu-item index="statistics">📊 统计分析</el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <div v-if="activeMenu === 'courses'">
            <div style="display: flex; justify-content: space-between; margin-bottom: 20px;">
              <h2>课程列表</h2>
              <el-button type="primary" @click="showCourseDialog = true">+ 新建课程</el-button>
            </div>
            <el-table :data="courses" border style="width: 100%;">
              <el-table-column prop="id" label="ID" width="80"></el-table-column>
              <el-table-column prop="title" label="课程名称"></el-table-column>
              <el-table-column prop="description" label="描述"></el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button size="small" @click="editCourse(scope.row)">编辑</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div v-if="activeMenu === 'statistics'">
            <h2>统计数据</h2>
            <el-row :gutter="20" style="margin: 20px 0;">
              <el-col :span="6">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 36px; color: #409EFF;">{{ statistics?.totalCourses || 0 }}</div>
                    <div style="color: #909399; margin-top: 10px;">课程总数</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 36px; color: #67C23A;">{{ statistics?.totalUsers || 0 }}</div>
                    <div style="color: #909399; margin-top: 10px;">用户总数</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 36px; color: #E6A23C;">{{ statistics?.courseCompletionRate?.toFixed(1) }}%</div>
                    <div style="color: #909399; margin-top: 10px;">课程完成率</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 36px; color: #F56C6C;">{{ statistics?.examPassRate?.toFixed(1) }}%</div>
                    <div style="color: #909399; margin-top: 10px;">考试通过率</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 36px; color: #909399;">{{ statistics?.expiringCertificatesCount || 0 }}</div>
                    <div style="color: #E6A23C; margin-top: 10px;">即将过期证书 (30天内)</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card shadow="hover">
                  <div style="text-align: center;">
                    <div style="font-size: 36px; color: #F56C6C;">{{ statistics?.expiredCertificatesCount || 0 }}</div>
                    <div style="color: #909399; margin-top: 10px;">已过期证书</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-main>
      </el-container>
    </el-container>
    <el-dialog v-model="showCourseDialog" title="新建课程" width="800px">
      <el-form :model="newCourse" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="newCourse.title"></el-input>
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input type="textarea" v-model="newCourse.description"></el-input>
        </el-form-item>
        <el-divider content-position="left">学习任务</el-divider>
        <div v-for="(task, index) in newCourse.learningTasks" :key="index" style="display: flex; gap: 10px; margin-bottom: 10px;">
          <el-input v-model="task.title" placeholder="任务标题" style="flex: 1;"></el-input>
          <el-input v-model="task.content" placeholder="任务内容" style="flex: 1;"></el-input>
          <el-button @click="removeTask(index)">删除</el-button>
        </div>
        <el-button @click="addTask">+ 添加任务</el-button>
        <el-divider content-position="left">考试规则</el-divider>
        <el-form-item label="及格分数">
          <el-input-number v-model="newCourse.examRule.passingScore" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="最大补考次数">
          <el-input-number v-model="newCourse.examRule.maxRetakeCount" :min="0" :max="10"></el-input-number>
        </el-form-item>
        <el-form-item label="证书有效期(天)">
          <el-input-number v-model="newCourse.examRule.certificateValidityDays" :min="1" :max="3650"></el-input-number>
        </el-form-item>
        <el-divider content-position="left">考试题目</el-divider>
        <div v-for="(q, qIndex) in newCourse.examQuestions" :key="qIndex" style="border: 1px solid #eee; padding: 15px; margin-bottom: 10px;">
          <el-form-item label="题目">
            <el-input v-model="q.question" type="textarea"></el-input>
          </el-form-item>
          <el-form-item label="选项A">
            <el-input v-model="q.options[0]"></el-input>
          </el-form-item>
          <el-form-item label="选项B">
            <el-input v-model="q.options[1]"></el-input>
          </el-form-item>
          <el-form-item label="选项C">
            <el-input v-model="q.options[2]"></el-input>
          </el-form-item>
          <el-form-item label="选项D">
            <el-input v-model="q.options[3]"></el-input>
          </el-form-item>
          <el-form-item label="正确答案">
            <el-select v-model="q.correctAnswerIndex" placeholder="选择">
              <el-option :label="A" :value="0"></el-option>
              <el-option :label="B" :value="1"></el-option>
              <el-option :label="C" :value="2"></el-option>
              <el-option :label="D" :value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="分值">
            <el-input-number v-model="q.score" :min="1" :max="100"></el-input-number>
          </el-form-item>
          <el-button @click="removeQuestion(qIndex)">删除题目</el-button>
        </div>
        <el-button @click="addQuestion">+ 添加题目</el-button>
      </el-form>
      <template #footer>
        <el-button @click="showCourseDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCourse">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import api from '../api'
export default {
  name: 'Admin',
  data() {
    return {
      activeMenu: 'courses',
      courses: [],
      statistics: null,
      showCourseDialog: false,
      editingCourse: null,
      newCourse: {
        title: '',
        description: '',
        learningTasks: [],
        examRule: { passingScore: 60, maxRetakeCount: 2, certificateValidityDays: 365 },
        examQuestions: []
      }
    }
  },
  mounted() {
    this.loadCourses()
    this.loadStatistics()
  },
  methods: {
    handleSelect(key) {
      this.activeMenu = key
      if (key === 'statistics') {
        this.loadStatistics()
      }
    },
    async loadCourses() {
      const res = await api.getCourses()
      this.courses = res.data.data || []
    },
    async loadStatistics() {
      const res = await api.getStatistics()
      this.statistics = res.data.data
    },
    addTask() {
      this.newCourse.learningTasks.push({ id: Date.now(), title: '', content: '', durationMinutes: 30, orderIndex: this.newCourse.learningTasks.length })
    },
    removeTask(index) {
      this.newCourse.learningTasks.splice(index, 1)
    },
    addQuestion() {
      this.newCourse.examQuestions.push({
        id: Date.now(),
        question: '',
        options: ['', '', '', ''],
        correctAnswerIndex: 0,
        score: 20
      })
    },
    removeQuestion(index) {
      this.newCourse.examQuestions.splice(index, 1)
    },
    async saveCourse() {
      if (!this.newCourse.title || this.newCourse.title.trim() === '') {
        alert('请输入课程名称！')
        return
      }
      if (this.newCourse.learningTasks.length === 0) {
        alert('请至少添加一个学习任务！')
        return
      }
      if (this.newCourse.learningTasks.some(task => !task.title || task.title.trim() === '')) {
        alert('请完善所有学习任务的标题！')
        return
      }
      if (!this.newCourse.examRule.passingScore || this.newCourse.examRule.passingScore <= 0) {
        alert('请设置有效的及格分数！')
        return
      }
      if (this.newCourse.examQuestions.length === 0) {
        alert('请至少添加一道考试题目！')
        return
      }
      if (this.newCourse.examQuestions.some(q => !q.question || q.question.trim() === '')) {
        alert('请完善所有考试题目的题干！')
        return
      }
      const totalScore = this.newCourse.examQuestions.reduce((sum, q) => sum + (q.score || 0), 0)
      if (totalScore < this.newCourse.examRule.passingScore) {
        alert(`题目总分(${totalScore})不能小于及格分数(${this.newCourse.examRule.passingScore})！`)
        return
      }
      if (this.editingCourse) {
        await api.updateCourse(this.editingCourse.id, this.newCourse)
      } else {
        await api.createCourse(this.newCourse)
      }
      this.showCourseDialog = false
      this.loadCourses()
      this.resetNewCourse()
      alert('保存成功！')
    },
    editCourse(course) {
      this.editingCourse = course
      this.newCourse = JSON.parse(JSON.stringify(course))
      this.showCourseDialog = true
    },
    resetNewCourse() {
      this.newCourse = {
        title: '',
        description: '',
        learningTasks: [],
        examRule: { passingScore: 60, maxRetakeCount: 2, certificateValidityDays: 365 },
        examQuestions: []
      }
      this.editingCourse = null
    }
  }
}
</script>
<style scoped>
.admin {
  width: 100%;
}
</style>

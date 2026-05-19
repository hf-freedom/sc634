<template>
  <div class="home">
    <el-container style="height: 100vh">
      <el-header style="background: #409EFF; color: white; padding: 0 30px; display: flex; align-items: center; justify-content: space-between;">
        <h1 style="margin: 0; font-size: 24px;">培训考试认证系统</h1>
      </el-header>
      <el-main style="display: flex; align-items: center; justify-content: center; gap: 50px;">
        <el-card shadow="hover" style="width: 300px; text-align: center; cursor: pointer;" @click="$router.push('/admin')">
          <div style="font-size: 60px; margin-bottom: 20px;">👨‍💼</div>
          <h2>管理员入口</h2>
          <p style="color: #909399;">课程管理、统计分析</p>
        </el-card>
        <el-card shadow="hover" style="width: 300px; text-align: center; cursor: pointer;" @click="$router.push('/employee')">
          <div style="font-size: 60px; margin-bottom: 20px;">👨‍💻</div>
          <h2>员工入口</h2>
          <p style="color: #909399;">课程报名、学习考试</p>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>
<script>
export default {
  name: 'Home'
}
</script>
<style scoped>
.home {
  width: 100%;
}
</style>

<script setup>
import { onMounted, ref } from 'vue'
import { fetchDashboard } from '../api/modules'

const dashboard = ref({
  catCount: 0,
  reservationCount: 0,
  activeOrderCount: 0,
  todayRevenue: 0,
  hotDrinks: [],
  reservationStatusSummary: []
})

const loadData = async () => {
  dashboard.value = await fetchDashboard()
}

onMounted(loadData)
</script>

<template>
  <section>
    <div class="page-header">
      <div>
        <h2>经营看板</h2>
        <p>围绕猫咪、座位、预约与订单的运营总览。</p>
      </div>
      <el-button type="primary" @click="loadData">刷新数据</el-button>
    </div>

    <div class="card-grid">
      <div class="metric-card">
        <h3>猫咪总数</h3>
        <strong>{{ dashboard.catCount }}</strong>
      </div>
      <div class="metric-card">
        <h3>预约总量</h3>
        <strong>{{ dashboard.reservationCount }}</strong>
      </div>
      <div class="metric-card">
        <h3>活跃订单</h3>
        <strong>{{ dashboard.activeOrderCount }}</strong>
      </div>
      <div class="metric-card">
        <h3>今日营收</h3>
        <strong>¥{{ dashboard.todayRevenue }}</strong>
      </div>
    </div>

    <div class="panel-grid">
      <div class="panel">
        <h3>热销饮品</h3>
        <div class="list-box">
          <div v-for="item in dashboard.hotDrinks" :key="item.name" class="list-row">
            <span>{{ item.name }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </div>

      <div class="panel">
        <h3>预约状态分布</h3>
        <div class="list-box">
          <div v-for="item in dashboard.reservationStatusSummary" :key="item.name" class="list-row">
            <span>{{ item.name }}</span>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<template>
  <a-alert
      message="系统使用提示"
      description="
      启动前，请确认已启动redis。
      本地 cd -> D/downloads/REDIS
        cmd: redis-server --service-start
      使用后请结束
        cmd: redis-server --service-stop
      重启电脑后若报错先确认windows服务中redis服务是否启动。"
      type="info"
      closable
      show-icon
      style="white-space: pre"
  />
  <div>
    <a-row>
      <a-col :span="24">
        <a-card>
          <a-row>
            <a-col :span="8">
              <a-statistic title="总阅读量" :value="statistic.viewCount">
                <template #suffix>
                  <UserOutlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="8">
              <a-statistic title="总点赞量" :value="statistic.voteCount">
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="8">
              <a-statistic title="点赞率" :value="statistic.voteCount / statistic.viewCount * 100"
                           :precision="2"
                           suffix="%"
                           :value-style="{ color: '#cf1322' }"
              >
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
    <br/>
    <a-row :gutter="16">
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col span="12">
              <a-statistic title="今日阅读"
                           :value="statistic.todayViewCount"
                           style="{  }">
                <template #suffix>
                  <UserOutlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col span="12">
              <a-statistic title="今日点赞"
                           :value="statistic.todayVoteCount">
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col span="12">
              <a-statistic title="预计今日阅读"
                           :value="statistic.todayViewIncrease"
                           :value-style="{ color: '#0000ff' }">
                <template #suffix>
                  <UserOutlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col span="12">
              <a-statistic title="预计今日阅读增长"
                           :value="statistic.todayVoteIncreaseRateAbs"
                           :precision="2"
                           suffix="%"
                           class="demo-class"
                           :value-style="statistic.todayViewIncreaseRate < 0 ? {color: '#3f8600'} : {color: '#cf1322'}"
              >
                <template #prefix>
                  <arrow-down-outlined v-if="statistic.todayViewIncreaseRate < 0"/>
                  <arrow-up-outlined v-if="statistic.todayViewIncreaseRate >= 0"/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
    <br/>
    <a-row>
      <a-col :span="24">
        <div id="main" style=" width: 100%; height: 300px; "></div>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
declare let echarts: any;

export default defineComponent({
  name: 'the-welcome',
  setup() {
    const statistic = ref();
    statistic.value = {};

    const getStatistic = () => {
      axios.get("/ebook-snapshot/get-statistic").then((response) => {
        const data = response.data;
        if (data.success) {
          const statisticResp = data.content;
          // 昨天是第0条，今天是第一条
          statistic.value.viewCount = statisticResp[1].viewCount;
          statistic.value.voteCount = statisticResp[1].voteCount;
          statistic.value.viewIncrease = statisticResp[1].viewIncrease;
          statistic.value.voteIncrease = statisticResp[1].voteIncrease;

          const now = new Date();
          const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);

          statistic.value.todayViewIncrease = parseInt(String(statisticResp[1].viewIncrease / nowRate));
          statistic.value.todayViewIncreaseRate = (statistic.value.todayViewIncrease - statisticResp[0].viewIncrease)
              / statisticResp[0].viewIncrease * 100;
          statistic.value.todayViewIncreaseRateAbs = Math.abs(statistic.value.todayViewIncreaseRate);

        } else {
          message.error(data.message);
        }
      });
    };

    const init30DayEcharts = (list: any) => {
      const myChart = echarts.init(document.getElementById('main'));

      const xAxis = [];
      const seriesView = [];
      const seriesVote = [];
      for (let i=0; i<list.length; i++) {
        const record = list[i];
        xAxis.push(record.date);
        seriesView.push(record.viewIncrease);
        seriesVote.push(record.voteIncrease);
      }

      const option = {
        title: {
          text: '30天趋势图'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['总阅读量', '总点赞量']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundryGap: false,
          data: xAxis
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总阅读量',
            type: 'line',
            data: seriesView,
            smooth: true
          },
          {
            name: '总点赞量',
            type: 'line',
            data: seriesVote,
            smooth: true
          }
        ]
      };

      myChart.setOption(option);
    };

    const get30Statistic = () => {
      axios.get("/ebook-snapshot/get-30-statistic").then((response) => {
        const data = response.data;
        if (data.success) {
          const statisticList = data.content;
          init30DayEcharts(statisticList);
        } else {
          message.error(data.message);
        }
      });
    };

    onMounted(() => {
      getStatistic();
      get30Statistic();
    });

    return {
      statistic,
    }
  }
});
</script>

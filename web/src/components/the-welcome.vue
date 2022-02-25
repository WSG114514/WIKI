<template>
  <div>
    <a-row>
      <a-col :span="24">
        <a-card>
          <a-row>
            <a-col :span="8">
              <a-statistic title="总阅读量" :value="statistic.viewCount" style="margin-right: 50px">
                <template #suffix>
                  <UserOutlined />
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="8">
              <a-statistic title="总点赞量" :value="statistic.voteCount" style="margin-right: 50px">
                <template #suffix>
                  <like-outlined />
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="8">
              <a-statistic
                  title="点赞率"
                  :value="statistic.voteCount / statistic.viewCount * 100"
                  :precision="2"
                  suffix="%"
                  :value-style="{ color: '#f83333' }"
                  style="margin-right: 50px"
              >
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row><br>
    <a-row :gutter="16">
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col :span="12">
              <a-statistic title="今日阅读" :value="statistic.todayViewCount" style="margin-right: 50px">
                <template #suffix>
                  <UserOutlined />
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="12">
              <a-statistic title="今日点赞" :value="statistic.todayVoteCount" style="margin-right: 50px">
                <template #suffix>
                  <like-outlined />
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col :span="12">
              <a-statistic title="预计今日阅读" :value="statistic.todayViewIncrease" style="margin-right: 50px; color: #72a9e2">
                <template #suffix>
                  <UserOutlined />
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="12">
              <a-statistic
                  title="预计今日阅读增长"
                  :value="9.3"
                  :precision="2"
                  suffix="%"
                  class="demo-class"
                  :value-style="{ color: '#cf1322' }"
              >
                <template #prefix>
                  <arrow-down-outlined v-if="statistic.todayViewIncreaseRate < 0"/>
                  <arrow-up-outlined v-if="statistic.todayViewIncreaseRate > 0"/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
    <br>
    <a-row>
      <a-col :span="24">
        <div id="main" style="width: 100%;height:300px;"></div>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
declare let echarts: any;
declare let hexMd5: any;
export default defineComponent({
  name: 'the-header',
  setup() {
    const statistic = ref();
    statistic.value = {};
    const getStatistic = () => {
      axios.get('/ebook-snapshot/get-snapshot').then((response) => {
        const data = response.data;
        if (data.success) {
          const statisticResp = data.content;
          statistic.value.viewCount = statisticResp[1].viewCount;
          statistic.value.voteCount = statisticResp[1].voteCount;
          statistic.value.todayViewCount = statisticResp[1].viewIncrease;
          statistic.value.todayVoteCount = statisticResp[1].voteIncrease;

          const now = new Date();
          const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);
          statistic.value.todayViewIncrease = parseInt(String(statisticResp[1].viewIncrease / nowRate));
          statistic.value.todayViewIncreaseRate = (statistic.value.todayViewIncrease - statisticResp[0].viewIncrease) / statisticResp[0].viewIncrease * 100;
          statistic.value.todayViewIncreaseRateAbs = Math.abs(statistic.value.todayViewIncreaseRate);
        }
      });

      axios.get('/ebook-snapshot/get-30-statistic').then((response) => {
        const data = response.data;
        debugger;
        if (data.success) {
          testEcharts(data.content);
        }else {
          message.error(data.content);
        }
      });
    };

    const testEcharts = (list: any) => {
      const myChart = echarts.init(document.getElementById('main'));
      const xAxis = [];
      const seriesVote = [];
      const seriesView = [];
      debugger;
      for (let i = 0; i < list.length; i++) {
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
          boundaryGap: false,
          data: xAxis
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总阅读量',
            type: 'line',
            smooth: true,
            data: seriesView
          },
          {
            name: '总点赞量',
            type: 'line',
            smooth: true,
            data: seriesVote
          }
        ]
      };

      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    }

    onMounted(()=> {
      getStatistic();
    });

    return {
      statistic
    };
  }
});
</script>
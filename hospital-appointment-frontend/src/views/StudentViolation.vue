<template>
  <div class="page">
    <a-card class="shell" :bordered="false">
      <!-- 标题 -->
      <h2>我的违规记录</h2>

      <a-table 
        v-if="!loading || tableData.length > 0"
        :dataSource="tableData" 
        :columns="columns" 
        rowKey="violationRecordID" 
        style="margin-top: 16px"
        :loading="loading"
      >
        <template #violationType="{ record }">
          <a-tag 
            :color="getViolationTypeColor(record.violationType)"
          >
            {{ getViolationTypeText(record.violationType) }}
          </a-tag>
        </template>

        <template #status="{ record }">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>

        <template #action="{ record }">
          <div v-if="canAppeal(record.status)">
            <a-button type="link" @click="appealViolation(record)">
              申诉
            </a-button>
          </div>
          <div v-else>
            <a-button type="link" disabled :title="getAppealDisabledReason(record.status)">
              申诉
            </a-button>
          </div>
        </template>
      </a-table>
      
      <div v-if="!loading && tableData.length === 0 && !errorInfo" style="text-align: center; padding: 20px;">
        <p>暂无违规记录</p>
      </div>

      <!-- 申诉弹窗 -->
      <a-modal 
        v-model:open="appealModalVisible" 
        title="提交申诉" 
        @ok="submitAppeal"
        @cancel="closeAppealModal"
        okText="提交"
        cancelText="取消"
        :confirmLoading="appealLoading"
      >
        <div v-if="currentAppealRecord">
          <p><strong>违规类型：</strong>{{ getViolationTypeText(currentAppealRecord.violationType) }}</p>
          <p><strong>违规详情：</strong>{{ currentAppealRecord.details || '无' }}</p>
          <p><strong>违规时间：</strong>{{ currentAppealRecord.violationTime || '无' }}</p>
          <a-form layout="vertical">
            <a-form-item label="申诉理由：" :required="true">
              <a-textarea 
                v-model:value="appealReason" 
                placeholder="请输入您的申诉理由"
                :rows="4"
              />
            </a-form-item>
          </a-form>
        </div>
      </a-modal>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from "vue";
import { message } from "ant-design-vue";
import { useStudentStore } from '@/store/studentStore';
import { violationService, type ViolationRecord } from '@/api/resourceService';

const API_BASE_URL = 'http://localhost:3000/api'

// 表格数据
const tableData = ref<ViolationRecord[]>([]);
const loading = ref<boolean>(false);
const errorInfo = ref<string>('');
const studentId = ref<number | string>(0);

// 申诉相关状态
const appealModalVisible = ref<boolean>(false);
const currentAppealRecord = ref<ViolationRecord | null>(null);
const appealReason = ref<string>('');
const appealLoading = ref<boolean>(false);

// 表格列
const columns = [
  { title: "违规类型", dataIndex: "violationType", slots: { customRender: "violationType" } },
  { title: "违规详情", dataIndex: "details" },
  { title: "扣分", dataIndex: "deductPoints" },
  { title: "违规时间", dataIndex: "violationTime" },
  { title: "处理状态", dataIndex: "status", slots: { customRender: "status" } },
  { title: "操作", slots: { customRender: "action" } }
];

// 获取数据
const loadData = async () => {
  loading.value = true;
  errorInfo.value = '';
  
  try {
    const studentStore = useStudentStore();
    const id = studentStore.studentInfo.studentID || localStorage.getItem('userId') || 0;
    studentId.value = id;
    
    console.log('获取到的学生ID:', id);
    
    if (!id) {
      errorInfo.value = "未获取到学生信息";
      message.error("未获取到学生信息");
      return;
    }

    // 调用查询违规记录的API
    const response = await violationService.satisfiesionRecords({ 
      studentId: typeof id === 'string' ? parseInt(id) : id 
    });
    
    console.log('API响应:', response);
    
    // 根据实际响应结构处理数据
    if (response.data && response.data.data) {
      // 如果后端返回的是ViolationQueryResult格式
      if (response.data.data.violations !== undefined) {
        tableData.value = response.data.data.violations;
        console.log('从Violations字段获取的数据:', response.data.data.violations);
      } else {
        // 如果直接返回数组
        tableData.value = Array.isArray(response.data.data) ? response.data.data : [];
        console.log('直接从data获取的数据:', response.data.data);
      }
    } else {
      errorInfo.value = "获取数据格式错误";
      message.error("获取数据格式错误");
    }
  } catch (error: unknown) {
    console.error("获取违规记录失败:", error);
    errorInfo.value = "获取违规记录失败";
    
    // 检查错误类型
    if (error instanceof Error) {
      console.error("错误详情:", error.message);
      message.error(`获取违规记录失败: ${error.message}`);
    } else {
      message.error("获取违规记录失败");
    }
  } finally {
    loading.value = false;
  }
};

// 获取违规类型文字
const getViolationTypeText = (type: number): string => {
  const typeMap: Record<number, string> = {
    1: "违规占座",
    2: "签到超时",
    3: "暂离超时",
    4: "研讨室人数不足",
    5: "未签退",
    6: "早退"
  };
  return typeMap[type] || "未知类型";
};

// 获取违规类型颜色
const getViolationTypeColor = (type: number): string => {
  const colorMap: Record<number, string> = {
    1: "red",
    2: "orange",
    3: "gold",
    4: "volcano",
    5: "magenta",
    6: "red"
  };
  return colorMap[type] || "default";
};

// 获取状态文字
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    1: "已生成",
    2: "待申诉",
    3: "已确认",
    4: "申诉中",
    5: "已撤销"
  };
  return statusMap[status] || "未知状态";
};

// 获取状态颜色
const getStatusColor = (status: number): string => {
  const colorMap: Record<number, string> = {
    1: "blue",    // 已生成
    2: "orange",  // 待申诉
    3: "green",   // 已确认
    4: "purple",  // 申诉中
    5: "gray"     // 已撤销
  };
  return colorMap[status] || "default";
};

// 判断是否可以申诉
const canAppeal = (status: number): boolean => {
  // 只有"已生成"(1)和"待申诉"(2)状态可以申诉
  return status === 1 || status === 2;
};

// 获取申诉禁用原因
const getAppealDisabledReason = (status: number): string => {
  if (status === 3) return "违规记录已确认，无法申诉";
  if (status === 4) return "已在申诉中";
  if (status === 5) return "违规记录已撤销";
  return "无法申诉";
};

// 打开申诉弹窗
const appealViolation = (record: ViolationRecord) => {
  currentAppealRecord.value = record;
  appealReason.value = '';
  appealModalVisible.value = true;
};

// 提交申诉
const submitAppeal = async () => {
  if (!currentAppealRecord.value) {
    message.error("当前违规记录信息无效");
    return;
  }

  if (!appealReason.value.trim()) {
    message.error("请输入申诉理由");
    return;
  }

  appealLoading.value = true;

  try {
    // 创建一个包含申诉理由的违规记录对象
    const appealRecord = {
      ...currentAppealRecord.value,
      appealReason: appealReason.value,
      appealTime: new Date().toISOString() // 添加申诉时间
    };

    // 获取认证令牌
    const token = localStorage.getItem('token');
    
    // 使用fetch API调用后端申诉接口
    const response = await fetch(`${API_BASE_URL}/violation/appeal`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}` // 添加认证头
      },
      body: JSON.stringify(appealRecord)
    });

    // 检查响应状态
    if (!response.ok) {
      const errorText = await response.text();
      let errorMessage = '申诉提交失败';
      try {
        const errorData = JSON.parse(errorText);
        errorMessage = errorData.message || errorMessage;
      } catch {
        errorMessage = errorText || errorMessage;
      }
      throw new Error(errorMessage);
    }

    // 尝试解析响应数据
    const responseData = await response.json();
    
    if (responseData && responseData.code === 200) {
      message.success(responseData.message || "申诉提交成功");
      
      // 关闭弹窗
      closeAppealModal();
      
      // 重新加载数据
      await loadData();
    } else {
      message.error(responseData.message || "申诉提交失败");
    }
  } catch (error: unknown) {
    console.error("提交申诉失败:", error);
    
    // 检查错误类型并处理
    if (error instanceof Error) {
      message.error(error.message || "申诉提交失败");
    } else {
      message.error("申诉提交失败");
    }
  } finally {
    appealLoading.value = false;
  }
};

// 关闭申诉弹窗
const closeAppealModal = () => {
  appealModalVisible.value = false;
  currentAppealRecord.value = null;
  appealReason.value = '';
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
h2 {
  margin-bottom: 16px;
}
</style>
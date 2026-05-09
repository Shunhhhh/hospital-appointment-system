<template>
  <div class="notice-page">
    <a-card class="shell" :bordered="false">
      <div class="header">
        <div class="title">我的通知</div>
      </div>

      <!-- 批量操作工具栏 -->
      <div class="batch-actions" v-if="selectedRowKeys.length > 0">
        <a-button type="primary" @click="batchMarkAsRead" :disabled="selectedRowKeys.length === 0">
          批量已读
        </a-button>
        <a-button @click="batchDelete" :disabled="selectedRowKeys.length === 0" danger>
          批量删除
        </a-button>
        <span style="margin-left: 16px">
          已选 {{ selectedRowKeys.length }} 项
        </span>
      </div>

      <!-- 通知列表 -->
      <a-table
        :dataSource="noticeList"
        :columns="columns"
        :rowKey="getRowKey"
        :pagination="paginationOptions"
        :row-selection="rowSelection"
        :loading="loading"
      >
        <!-- 类型标签 -->
        <template #typeTag="{ record }">
          <a-tag :color="getNoticeTypeColor(record.notificationType)">
            {{ getNoticeTypeText(record.notificationType) }}
          </a-tag>
        </template>

        <!-- 是否已读 -->
        <template #readStatus="{ record }">
          <a-tag :color="record.notificationStatus === 2 ? 'green' : 'orange'">
            {{ record.notificationStatus === 2 ? '已读' : '未读' }}
          </a-tag>
        </template>

        <!-- 操作列 -->
        <template #actions="{ record }">
          <a-button type="link" @click="viewNotice(record)">查看</a-button>
          <a-button 
            type="link" 
            @click="markAsRead(record.notificationID)"
            :disabled="record.notificationStatus === 2"
          >
            {{ record.notificationStatus === 2 ? '已读' : '标记已读' }}
          </a-button>
          <a-button type="link" danger @click="deleteNotice(record.notificationID)">删除</a-button>
        </template>
      </a-table>
    </a-card>

    <!-- 通知详情模态框 -->
    <a-modal 
      v-model:visible="detailModalVisible" 
      title="通知详情" 
      :footer="null"
      width="600px"
    >
      <div v-if="currentNotice">
        <h3>{{ currentNotice.title }}</h3>
        <div class="notice-meta">
          <span class="type-tag">
            <a-tag :color="getNoticeTypeColor(currentNotice.notificationType)">
              {{ getNoticeTypeText(currentNotice.notificationType) }}
            </a-tag>
          </span>
          <span class="time">{{ currentNotice.sendTime }}</span>
        </div>
        <div class="notice-content">
          <p>{{ currentNotice.notificationContent }}</p>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { message, Table } from 'ant-design-vue';
import { useStudentStore } from '@/store/studentStore';
import { notificationService, NoticeRecord } from '@/api/notificationService';

// 通知类型枚举
const NOTICE_TYPES = {
  VIOLATION: 1,      // 违规通知
  FEEDBACK: 2,       // 反馈通知
  REMINDER: 3,       // 提醒通知
  SYSTEM: 4          // 系统通知
};

// 通知类型文本映射
const noticeTypeTextMap: Record<number, string> = {
  [NOTICE_TYPES.VIOLATION]: '违规通知',
  [NOTICE_TYPES.FEEDBACK]: '反馈通知',
  [NOTICE_TYPES.REMINDER]: '提醒通知',
  [NOTICE_TYPES.SYSTEM]: '系统通知'
};

// 通知类型颜色映射
const noticeTypeColorMap: Record<number, string> = {
  [NOTICE_TYPES.VIOLATION]: 'red',
  [NOTICE_TYPES.FEEDBACK]: 'blue',
  [NOTICE_TYPES.REMINDER]: 'orange',
  [NOTICE_TYPES.SYSTEM]: 'green'
};

// 通知状态枚举
const NOTICE_STATUS = {
  UNREAD: 1,  // 未查看
  READ: 2     // 已查看
};

// 通知列表数据
const noticeList = ref<NoticeRecord[]>([]);
const loading = ref<boolean>(true);
const detailModalVisible = ref<boolean>(false);
const currentNotice = ref<NoticeRecord | null>(null);

// 选中项相关
const selectedRowKeys = ref<string[]>([]);

// 表格列定义
const columns = [
  {
    title: '标题',
    dataIndex: 'title',
    ellipsis: true
  },
  {
    title: '类型',
    dataIndex: 'notificationType',
    slots: { customRender: 'typeTag' },
    width: 150
  },
  {
    title: '状态',
    dataIndex: 'notificationStatus',
    slots: { customRender: 'readStatus' },
    width: 120
  },
  {
    title: '时间',
    dataIndex: 'sendTime',
    width: 200
  },
  {
    title: '操作',
    key: 'actions',
    slots: { customRender: 'actions' },
    width: 180
  }
];

// 定义表格选择配置
const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: onSelectChange,
  selections: [
    Table.SELECTION_ALL,
    Table.SELECTION_INVERT
  ]
}));

// 分页配置
const paginationOptions = {
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
  pageSizeOptions: ['10', '20', '50'],
  defaultPageSize: 10
};

// 选中项变化处理
const onSelectChange = (changedKeys: string[]) => {
  selectedRowKeys.value = changedKeys;
};

// 获取行键值
const getRowKey = (record: NoticeRecord) => record.notificationID;

// 获取通知类型文本
const getNoticeTypeText = (type: number | undefined): string => {
  if (type === undefined) return '未知类型';
  return noticeTypeTextMap[type] || '未知类型';
};

// 获取通知类型颜色
const getNoticeTypeColor = (type: number | undefined): string => {
  if (type === undefined) return 'default';
  return noticeTypeColorMap[type] || 'default';
};

// 从后端API加载通知数据
const loadNoticeData = async () => {
  loading.value = true;
  try {
    // 获取学生ID，可能来自store或从登录信息中获取
    const studentStore = useStudentStore();
    const studentId = studentStore.studentInfo?.studentID || 1001; // 使用正确的属性名

    // 调用后端API查询通知
    const response = await notificationService.queryNotifications({
      studentId: studentId
    });

    if (response.code === 200) {
      // 处理响应数据
      if (response.data && response.data.notifications) {
        noticeList.value = response.data.notifications;
      } else {
        noticeList.value = [];
        message.info(response.message || '没有找到符合条件的通知');
      }
    } else {
      console.error('获取通知数据失败:', response.message);
      message.error(response.message || '获取通知失败');
    }
  } catch (error) {
    console.error('加载通知数据异常:', error);
    message.error('加载通知失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 标记单条为已读
const markAsRead = async (id: string) => {
  try {
    // 调用后端API标记为已读
    const response = await notificationService.markAsRead(id);
    
    if (response.code === 200) {
      const notice = noticeList.value.find(n => n.notificationID === id);
      if (notice) {
        notice.notificationStatus = NOTICE_STATUS.READ;
        notice.readTime = new Date().toISOString().slice(0, 19).replace('T', ' ');
        message.success('已标记为已读');
        
        // 如果当前在选中列表中，更新选中项
        if (selectedRowKeys.value.includes(id)) {
          selectedRowKeys.value = selectedRowKeys.value.filter(key => key !== id);
        }
      }
    } else {
      message.error(response.message || '标记已读失败');
    }
  } catch (error) {
    console.error('标记已读失败:', error);
    message.error('标记已读失败');
  }
};

// 批量标记为已读（
const batchMarkAsRead = async () => {
  try {
    // 获取学生ID
    const studentStore = useStudentStore();
    const studentId = studentStore.studentInfo?.studentID || 1001;
    
    // 调用后端API批量标记学生的所有通知为已读
    const response = await notificationService.batchMarkAsRead(studentId);
    
    if (response.code === 200) {
      // 更新所有通知为已读状态
      noticeList.value.forEach(notice => {
        notice.notificationStatus = NOTICE_STATUS.READ;
        notice.readTime = new Date().toISOString().slice(0, 19).replace('T', ' ');
      });
      
      selectedRowKeys.value = []; // 清空选中项
    } else {
      message.error(response.message || '一键标记通知为已读失败');
    }
  } catch (error) {
    console.error('一键标记所有通知为已读失败:', error);
    message.error('一键标记所有通知为已读失败');
  }
};

// 删除单条通知
const deleteNotice = async (id: string) => {
  try {
    // 调用后端API删除通知
    const response = await notificationService.deleteNotification(id);
    
    if (response.code === 200) {
      const index = noticeList.value.findIndex(n => n.notificationID === id);
      if (index !== -1) {
        noticeList.value.splice(index, 1);
        message.success('删除成功');
        
        // 如果当前在选中列表中，更新选中项
        if (selectedRowKeys.value.includes(id)) {
          selectedRowKeys.value = selectedRowKeys.value.filter(key => key !== id);
        }
      }
    } else {
      message.error(response.message || '删除失败');
    }
  } catch (error) {
    console.error('删除失败:', error);
    message.error('删除失败');
  }
};

// 批量删除
const batchDelete = async () => {
  if (selectedRowKeys.value.length === 0) return;
  
  try {
    // 获取学生ID
    const studentStore = useStudentStore();
    const studentId = studentStore.studentInfo?.studentID || 1001;
    // 调用后端API批量删除
    const response = await notificationService.batchDeleteAll(studentId);
    
    if (response.code === 200) {
      noticeList.value = noticeList.value.filter(notice => 
        !selectedRowKeys.value.includes(notice.notificationID)
      );
      
      message.success(`成功删除 ${selectedRowKeys.value.length} 条通知`);
      selectedRowKeys.value = [];
    } else {
      message.error(response.message || '批量删除失败');
    }
  } catch (error) {
    console.error('批量删除失败:', error);
    message.error('批量删除失败');
  }
};

// 查看通知详情
const viewNotice = (notice: NoticeRecord) => {
  currentNotice.value = notice;
  detailModalVisible.value = true;
  
  // 如果未读，则自动标记为已读
  if (notice.notificationStatus === NOTICE_STATUS.UNREAD) {
    markAsRead(notice.notificationID);
  }
};

// 页面加载
onMounted(() => {
  loadNoticeData();
});
</script>

<style scoped>
.notice-page {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
  width: 100%;
}

.shell {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  font-weight: bold;
}

.batch-actions {
  margin-bottom: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 4px;
  display: flex;
  align-items: center;
}

.batch-actions .ant-btn {
  margin-right: 8px;
}

.notice-meta {
  display: flex;
  justify-content: space-between;
  margin: 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.notice-content {
  line-height: 1.8;
}

:deep(.ant-table) {
  background: #fff;
  border-radius: 4px;
}
</style>
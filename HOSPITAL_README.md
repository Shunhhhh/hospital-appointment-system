# 医院就诊预约系统

> 基于自习室预约系统改造的医院门诊预约挂号系统

## 项目概述

本项目是将原有的高校自习室座位预约系统改造为医院门诊预约挂号系统，支持患者线上预约、医生排班管理、就诊记录管理等功能。

## 技术栈

### 后端
- **Spring Boot** - Java Web框架
- **MyBatis-Plus** - ORM框架
- **MySQL** - 数据库

### 前端
- **Vue 3** - 渐进式JavaScript框架
- **TypeScript** - 类型安全的JavaScript超集
- **Vite** - 新一代前端构建工具
- **Element Plus** - Vue 3 UI组件库
- **Ant Design Vue** - UI组件库

## 项目结构

```
code/
├── hospital_appointment.sql          # 医院预约系统数据库脚本
├── HOSPITAL_README.md               # 本说明文件
├── SelfStudyRoom/                   # 前端项目
│   └── src/
│       ├── api/hospital/            # 医院系统API接口
│       │   ├── department.ts         # 科室管理API
│       │   ├── doctor.ts             # 医生管理API
│       │   ├── patient.ts            # 患者管理API
│       │   ├── schedule.ts           # 排班管理API
│       │   ├── appointment.ts        # 挂号预约API
│       │   └── review.ts             # 评价管理API
│       ├── views/hospital/          # 医院系统页面组件
│       │   ├── HospitalHome.vue      # 医院首页
│       │   ├── DoctorList.vue         # 医生列表
│       │   ├── DoctorSchedule.vue     # 预约挂号
│       │   ├── MyAppointments.vue     # 我的挂号
│       │   ├── PatientLogin.vue       # 患者登录
│       │   ├── PatientRegister.vue    # 患者注册
│       │   └── DoctorWorkbench.vue    # 医生工作台
│       └── router/
│           └── hospital.ts            # 医院系统路由配置
└── study-room-backend/               # 后端项目
    └── src/main/java/com/
        └── hospital/appointment/     # 医院预约系统包
            ├── entity/               # 实体类
            ├── mapper/               # 数据访问层
            ├── service/              # 业务逻辑层
            ├── controller/           # 控制层
            ├── util/                 # 工具类
            └── exception/            # 异常处理
```

## 数据库表

| 表名 | 说明 |
|------|------|
| `admin` | 管理员表 |
| `department` | 科室表 |
| `doctor` | 医生表 |
| `patient` | 患者表 |
| `doctor_schedule` | 医生排班表 |
| `appointment` | 挂号记录表 |
| `visit_record` | 就诊记录表 |
| `medical_record` | 门诊病历表 |
| `prescription` | 处方表 |
| `review` | 就医评价表 |
| `notification` | 通知消息表 |
| `waitlist_record` | 候补记录表 |
| `operation_log` | 操作日志表 |

## 核心功能

### 患者端
- 用户注册与登录
- 科室浏览与搜索
- 医生查询与筛选
- 在线预约挂号
- 挂号管理（查看/取消）
- 就诊签到
- 就医评价

### 医生端
- 医生工作台
- 今日患者队列
- 接诊管理
- 病历书写
- 处方开具

### 管理端
- 科室管理
- 医生管理
- 排班管理
- 挂号管理
- 评价管理
- 数据统计

## 快速开始

### 1. 初始化数据库

```sql
mysql -u root -p
source hospital_appointment.sql
source hospital_extra_seed.sql
```

### 2. 启动后端

```bash
cd hospital-appointment-system\hospital-appointment-backend
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd hospital-appointment-system\hospital-appointment-frontend
npm install
npm run dev
```

### 4. 访问系统

打开浏览器访问 http://localhost:5173/hospital/home

## 登录说明

### 测试账号

密码都为password

患者账号
张三, 13900001001
李四, 13900001002
王五, 13900001003

医生账号
张明, 13800001001
李华, 13800001002
王芳, 13800001003
刘强, 13800001004
陈静, 13800001005

管理员账号
系统管理员,1


## 概念映射

| 自习室系统 | 医院预约系统 |
|-----------|-------------|
| 学生用户 | 患者用户 |
| 自习室 | 科室/门诊部 |
| 座位 | 医生的号源 |
| 预约座位 | 预约挂号 |
| 签到/签退 | 签到就诊/完成就诊 |
| 反馈 | 就医评价 |

## 后续优化建议

1. **支付集成** - 对接微信支付/支付宝
2. **短信通知** - 集成短信网关发送通知
3. **实名认证** - 对接公安实名认证接口
4. **电子病历** - 完善病历模板和历史查询
5. **检查检验** - 对接LIS/PACS系统
6. **数据分析** - 就诊量统计、热度分析

## 许可证

MIT License

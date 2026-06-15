/*
 医院就诊预约系统数据库
 Hospital Appointment System Database
 改造自自习室预约系统
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE hospital_appointment;
USE hospital_appointment;
SET NAMES utf8mb4;

-- =============================================
-- 1. 管理员表 (保留原有结构，可扩展)
-- =============================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `adminID` int NOT NULL COMMENT '管理员编号，登录账号',
  `adminPassword` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理员登录密码',
  `adminPosition` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理员职位',
  `adminPermission` int NOT NULL COMMENT '管理员权限等级',
  `adminPhoneNumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理员手机号',
  `adminName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理员姓名',
  PRIMARY KEY (`adminID`) USING BTREE,
  UNIQUE INDEX `uk_admin_id`(`adminID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

INSERT INTO `admin` VALUES (1, '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', '系统管理员', 1, '99999999999', '系统管理员');

-- =============================================
-- 2. 科室表 (改造自自习室)
-- =============================================
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `departmentID` bigint NOT NULL AUTO_INCREMENT COMMENT '科室编号',
  `departmentName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '科室名称',
  `departmentType` tinyint NOT NULL COMMENT '科室类型：1-内科 2-外科 3-儿科 4-妇科 5-产科 6-骨科 7-眼科 8-耳鼻喉科 9-口腔科 10-皮肤科 11-中医科 12-急诊科',
  `departmentLocation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '科室位置',
  `departmentDesc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '科室简介',
  `departmentIcon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科室图标',
  `departmentStatus` tinyint NOT NULL DEFAULT 1 COMMENT '科室状态：0-停诊 1-正常',
  `displayOrder` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`departmentID`) USING BTREE,
  UNIQUE INDEX `uk_department_id`(`departmentID` ASC) USING BTREE,
  INDEX `idx_department_type`(`departmentType` ASC) USING BTREE,
  INDEX `idx_department_status`(`departmentStatus` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '科室表' ROW_FORMAT = Dynamic;

INSERT INTO `department` VALUES 
(1, '内科', 1, '门诊楼2楼', '内科是医院基础科室，涵盖心血管内科、呼吸内科、消化内科等', 'icon-internal', 1, 1, NOW()),
(2, '外科', 2, '门诊楼3楼', '外科包含普通外科、骨科、神经外科等', 'icon-surgery', 1, 2, NOW()),
(3, '儿科', 3, '门诊楼4楼', '专门诊治0-14岁儿童疾病的科室', 'icon-pediatric', 1, 3, NOW()),
(4, '妇科', 4, '门诊楼5楼', '诊治女性生殖系统疾病的科室', 'icon-gynecology', 1, 4, NOW()),
(5, '骨科', 6, '门诊楼3楼东侧', '诊治骨骼、关节、肌肉等运动系统疾病的科室', 'icon-orthopedics', 1, 5, NOW()),
(6, '眼科', 7, '门诊楼6楼', '诊治眼及视路疾病的科室', 'icon-ophthalmology', 1, 6, NOW()),
(7, '口腔科', 9, '门诊楼6楼西侧', '诊治口腔疾病的科室', 'icon-dental', 1, 7, NOW()),
(8, '皮肤科', 10, '门诊楼4楼西侧', '诊治皮肤疾病的科室', 'icon-dermatology', 1, 8, NOW()),
(9, '中医科', 11, '门诊楼7楼', '中医诊疗科室', 'icon-tcm', 1, 9, NOW()),
(10, '急诊科', 12, '急诊楼1楼', '24小时急诊服务', 'icon-emergency', 1, 10, NOW());

-- 预约挂号页面补充科室数据
INSERT INTO `department` VALUES
(11, '心血管内科', 1, '外滩院区 门诊楼2楼A区', '开展冠心病、高血压、心律失常、心衰等心血管疾病诊疗，提供专家门诊与专病门诊。', 'icon-cardiology', 1, 11, NOW()),
(12, '血液科', 1, '外滩院区 门诊楼2楼B区', '诊治贫血、白细胞异常、淋巴瘤、骨髓增生异常等血液系统疾病。', 'icon-hematology', 1, 12, NOW()),
(13, '心身医学科', 1, '月湖院区 门诊楼3楼A区', '提供焦虑、抑郁、睡眠障碍、躯体化症状等心身疾病评估与治疗。', 'icon-psychosomatic', 1, 13, NOW()),
(14, '呼吸内科/呼吸与危重症', 1, '外滩院区 门诊楼2楼C区', '诊治慢阻肺、哮喘、肺结节、肺部感染及呼吸危重症。', 'icon-respiratory', 1, 14, NOW()),
(15, '消化内科', 1, '外滩院区 门诊楼3楼B区', '开展胃肠、肝胆胰疾病诊治及消化内镜相关诊疗。', 'icon-digestive', 1, 15, NOW()),
(16, '内分泌科', 1, '月湖院区 门诊楼2楼B区', '诊治糖尿病、甲状腺疾病、肥胖、骨质疏松等内分泌代谢疾病。', 'icon-endocrine', 1, 16, NOW()),
(17, '神经内科', 1, '外滩院区 门诊楼3楼C区', '诊治脑卒中、头痛头晕、癫痫、帕金森病、周围神经病等。', 'icon-neurology', 1, 17, NOW()),
(18, '风湿免疫科', 1, '月湖院区 门诊楼3楼B区', '诊治类风湿关节炎、系统性红斑狼疮、痛风、强直性脊柱炎等。', 'icon-rheumatology', 1, 18, NOW()),
(19, '老年医学科', 1, '月湖院区 门诊楼4楼A区', '提供老年慢病综合管理、衰弱评估、多病共治及用药管理。', 'icon-geriatrics', 1, 19, NOW()),
(20, '全科医学科', 1, '外滩院区 门诊楼1楼A区', '承担常见病、慢病复诊、健康咨询和双向转诊入口服务。', 'icon-general', 1, 20, NOW()),
(21, '理疗康复针灸科', 11, '月湖院区 康复楼2楼', '提供针灸、推拿、理疗、运动康复及疼痛康复治疗。', 'icon-rehab', 1, 21, NOW()),
(22, '耳鼻咽喉头颈外科', 8, '外滩院区 门诊楼5楼A区', '诊治鼻炎鼻窦炎、咽喉疾病、耳科疾病及头颈部常见疾病。', 'icon-ent', 1, 22, NOW());

-- =============================================
-- 3. 医生表 (新增)
-- =============================================
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor` (
  `doctorID` bigint NOT NULL AUTO_INCREMENT COMMENT '医生工号',
  `doctorName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '医生姓名',
  `doctorPassword` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码',
  `doctorGender` tinyint NOT NULL COMMENT '性别：1-男 2-女',
  `doctorPhone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `doctorEmail` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `departmentID` bigint NOT NULL COMMENT '所属科室ID',
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '职称：主任医师 副主任医师 主治医师 住院医师',
  `specialty` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '专长',
  `doctorIntro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '医生简介',
  `doctorPhoto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '医生照片URL',
  `registrationFee` decimal(10,2) NOT NULL DEFAULT 10.00 COMMENT '挂号费',
  `doctorStatus` tinyint NOT NULL DEFAULT 1 COMMENT '医生状态：0-离职 1-在职 2-停诊',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`doctorID`) USING BTREE,
  UNIQUE INDEX `uk_doctor_id`(`doctorID` ASC) USING BTREE,
  INDEX `idx_department`(`departmentID` ASC) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  CONSTRAINT `fk_doctor_department` FOREIGN KEY (`departmentID`) REFERENCES `department` (`departmentID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医生表' ROW_FORMAT = Dynamic;

INSERT INTO `doctor` VALUES 
(1001, '张明', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800001001', 'zhangming@hospital.com', 1, '主任医师', '心血管疾病、高血压、冠心病', '从事内科临床工作30年，擅长心血管疾病的诊治', NULL, 30.00, 1, NOW()),
(1002, '李华', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800001002', 'lihua@hospital.com', 1, '副主任医师', '糖尿病、甲状腺疾病', '内分泌科专家，20年临床经验', NULL, 20.00, 1, NOW()),
(1003, '王芳', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800001003', 'wangfang@hospital.com', 4, '主任医师', '妇科肿瘤、子宫内膜异位症', '妇科微创手术专家', NULL, 30.00, 1, NOW()),
(1004, '刘强', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800001004', 'liuqiang@hospital.com', 5, '副主任医师', '骨折、关节疾病、运动损伤', '骨科微创手术专家', NULL, 20.00, 1, NOW()),
(1005, '陈静', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800001005', 'chenjing@hospital.com', 3, '主治医师', '儿童呼吸道疾病、儿童哮喘', '专注儿童呼吸系统疾病10年', NULL, 15.00, 1, NOW());

-- 预约挂号页面补充医生数据，默认密码同测试账号
INSERT INTO `doctor` VALUES
(1011, '王世奇', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002011', 'wangshiqi@hospital.com', 11, '主任医师', '复杂冠心病介入治疗、缓慢型心律失常、永久起搏器植入', '长期从事冠心病及缓慢型心律失常的介入诊疗工作，擅长复杂冠心病介入治疗、永久起搏器植入术和心内科急危重症救治。', NULL, 30.30, 1, NOW()),
(1012, '林若舟', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002012', 'linruozhou@hospital.com', 11, '副主任医师', '高血压规范化治疗、冠脉CTA评估、心衰长期管理', '从事心血管内科临床与教学工作，擅长高血压、冠心病、心衰等常见病和慢病管理。', NULL, 26.00, 1, NOW()),
(1013, '周清禾', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002013', 'zhouqinghe@hospital.com', 11, '主治医师', '心悸、胸闷、心律失常初筛、心血管慢病随访', '熟悉心血管内科常见病诊疗，提供心律失常、高血压和胸痛胸闷规范化评估。', NULL, 18.00, 1, NOW()),
(1014, '陆雯', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002014', 'luwen@hospital.com', 12, '主任医师', '贫血、血小板减少、淋巴瘤、骨髓增生异常综合征', '长期从事血液系统疾病诊治，擅长贫血、出凝血异常及血液肿瘤综合治疗。', NULL, 30.30, 1, NOW()),
(1015, '郑昊', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002015', 'zhenghao@hospital.com', 12, '副主任医师', '白细胞异常、骨髓检查解读、血液病随访', '擅长血液科常见疾病诊疗及骨髓报告解读，重视患者长期随访。', NULL, 26.00, 1, NOW()),
(1016, '陈泊宁', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002016', 'chenboning@hospital.com', 13, '主任医师', '焦虑障碍、抑郁障碍、睡眠障碍、心身疾病综合评估', '擅长焦虑抑郁、睡眠障碍及躯体化症状的综合评估与个体化治疗。', NULL, 30.30, 1, NOW()),
(1017, '孟予安', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002017', 'mengyuan@hospital.com', 13, '主治医师', '失眠、压力相关障碍、青少年情绪问题', '长期关注睡眠医学和压力相关障碍，提供心理评估和药物治疗建议。', NULL, 18.00, 1, NOW()),
(1018, '赵景行', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002018', 'zhaojingxing@hospital.com', 14, '主任医师', '慢阻肺、哮喘、肺结节、呼吸危重症', '从事呼吸与危重症医学临床工作多年，擅长慢性气道疾病和肺结节规范化诊疗。', NULL, 30.30, 1, NOW()),
(1019, '唐雨棠', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002019', 'tangyutang@hospital.com', 14, '副主任医师', '肺部感染、咳嗽、支气管哮喘、肺功能评估', '擅长呼吸道感染、慢性咳嗽、哮喘和肺功能报告解读。', NULL, 26.00, 1, NOW()),
(1020, '沈知微', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002020', 'shenzhiwei@hospital.com', 15, '主任医师', '胃食管反流、炎症性肠病、胃肠镜诊疗、肝胆胰疾病', '擅长消化系统疑难疾病诊治及消化内镜相关诊疗。', NULL, 30.30, 1, NOW()),
(1021, '何砚秋', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002021', 'heyanqiu@hospital.com', 15, '主治医师', '胃炎、肠易激综合征、幽门螺杆菌、消化不良', '熟悉消化内科常见病诊治，擅长慢性胃炎、消化不良和幽门螺杆菌管理。', NULL, 18.00, 1, NOW()),
(1022, '许嘉宁', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002022', 'xujianing@hospital.com', 16, '主任医师', '糖尿病、甲状腺结节、肥胖症、骨质疏松', '擅长糖尿病强化管理、甲状腺疾病、肥胖及骨质疏松的诊治。', NULL, 30.30, 1, NOW()),
(1023, '顾南星', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002023', 'gunanxing@hospital.com', 16, '副主任医师', '血糖波动、妊娠糖尿病、甲亢甲减、代谢综合征', '从事内分泌代谢疾病诊疗，擅长糖尿病、甲状腺和代谢综合征管理。', NULL, 26.00, 1, NOW()),
(1024, '白景澄', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002024', 'baijingcheng@hospital.com', 17, '主任医师', '脑卒中、头痛头晕、帕金森病、癫痫', '擅长脑血管病、运动障碍疾病和神经系统疑难症状的诊疗。', NULL, 30.30, 1, NOW()),
(1025, '叶澜', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002025', 'yelan@hospital.com', 17, '主治医师', '头痛、眩晕、睡眠障碍、周围神经病', '擅长神经内科常见症状评估，尤其是头痛眩晕和周围神经病诊治。', NULL, 18.00, 1, NOW()),
(1026, '邵承宇', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002026', 'shaochengyu@hospital.com', 18, '主任医师', '类风湿关节炎、系统性红斑狼疮、痛风、强直性脊柱炎', '擅长常见风湿免疫疾病的规范化治疗和长期随访管理。', NULL, 30.30, 1, NOW()),
(1027, '宋芷晴', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002027', 'songzhiqing@hospital.com', 18, '副主任医师', '痛风、高尿酸血症、干燥综合征、免疫指标解读', '擅长痛风、高尿酸血症和自身免疫相关疾病的诊疗。', NULL, 26.00, 1, NOW()),
(1028, '梁思远', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002028', 'liangsiyuan@hospital.com', 19, '主任医师', '老年慢病综合管理、衰弱评估、多重用药管理', '专注老年共病管理、衰弱评估和多重用药风险评估。', NULL, 30.30, 1, NOW()),
(1029, '纪云舒', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002029', 'jiyunshu@hospital.com', 19, '主治医师', '高血压、糖尿病、老年综合评估、慢病随访', '擅长老年常见慢病随访和健康管理。', NULL, 18.00, 1, NOW()),
(1030, '秦越', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002030', 'qinyue@hospital.com', 20, '副主任医师', '全科常见病、慢病复诊、健康咨询、转诊评估', '提供全科常见病诊疗、慢病随访和就医路径咨询。', NULL, 20.00, 1, NOW()),
(1031, '夏安禾', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002031', 'xiaanhe@hospital.com', 20, '主治医师', '发热、咳嗽、腹痛、慢病复诊、健康体检解读', '熟悉全科门诊常见症状处理，提供慢病复诊和体检报告解读。', NULL, 15.00, 1, NOW()),
(1032, '苏砚', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002032', 'suyan@hospital.com', 21, '主任中医师', '颈肩腰腿痛、针灸推拿、脑卒中后康复、运动损伤康复', '擅长针灸、推拿、理疗及运动康复，关注疼痛和功能恢复。', NULL, 25.00, 1, NOW()),
(1033, '梅清辞', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 2, '13800002033', 'meiqingci@hospital.com', 21, '主治医师', '针灸调理、康复评定、慢性疼痛、肩颈腰背不适', '擅长慢性疼痛、肩颈腰背不适及康复训练指导。', NULL, 15.00, 1, NOW()),
(1034, '包卫亮', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002034', 'baoweiliang@hospital.com', 22, '主治医师', '鼻炎鼻窦炎、咽喉疾病、儿童耳鼻喉门诊、头颈外科初筛', '擅长耳鼻咽喉头颈外科常见病、多发病的诊治。', NULL, 18.00, 1, NOW());

-- =============================================
-- 4. 患者表 (改造自学生用户)
-- =============================================
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient` (
  `patientID` bigint NOT NULL AUTO_INCREMENT COMMENT '患者ID',
  `patientName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '患者姓名',
  `patientPassword` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码',
  `idCard` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证号',
  `patientGender` tinyint NOT NULL COMMENT '性别：1-男 2-女',
  `patientBirthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `patientPhone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `patientEmail` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `patientAddress` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '住址',
  `medicalCardNo` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '就诊卡号',
  `insuranceType` tinyint NULL DEFAULT NULL COMMENT '医保类型：1-城镇职工 2-城乡居民 3-自费',
  `insuranceNo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '医保卡号',
  `allergyHistory` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '过敏史',
  `medicalHistory` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '既往病史',
  `emergencyContact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `emergencyPhone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系电话',
  `creditScore` int NOT NULL DEFAULT 100 COMMENT '信用积分',
  `noshowCount` int NOT NULL DEFAULT 0 COMMENT '爽约次数',
  `isBlacklist` tinyint NOT NULL DEFAULT 0 COMMENT '是否黑名单：0-否 1-是',
  `patientStatus` tinyint NOT NULL DEFAULT 1 COMMENT '账号状态：0-禁用 1-正常',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`patientID`) USING BTREE,
  UNIQUE INDEX `uk_patient_id`(`patientID` ASC) USING BTREE,
  UNIQUE INDEX `uk_id_card`(`idCard` ASC) USING BTREE,
  UNIQUE INDEX `uk_medical_card`(`medicalCardNo` ASC) USING BTREE,
  INDEX `idx_phone`(`patientPhone` ASC) USING BTREE,
  INDEX `idx_blacklist`(`isBlacklist` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '患者表' ROW_FORMAT = Dynamic;

INSERT INTO `patient` VALUES 
(2001, '张三', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', '110101199001011234', 1, '1990-01-01', '13900001001', 'zhangsan@email.com', '北京市朝阳区', 'MZ20250001', 1, 'YI00000001', '青霉素过敏', '无', '李四', '13900001002', 100, 0, 0, 1, NOW()),
(2002, '李小红', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', '110101199502022345', 2, '1995-02-02', '13900001002', 'lixiaohong@email.com', '北京市海淀区', 'MZ20250002', 2, NULL, '无', '高血压病史5年', '王五', '13900001003', 100, 0, 0, 1, NOW());

-- =============================================
-- 4. 反馈表 (新增)
-- =============================================
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `feedbackID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈编号',
  `studentID` bigint NOT NULL COMMENT '患者ID',
  `processAdminID` bigint NULL DEFAULT NULL COMMENT '处理管理员ID',
  `feedbackType` tinyint NOT NULL COMMENT '反馈类型：1-积分 2-挂号 3-报告查询 4-问诊',
  `feedbackContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈内容',
  `processStatus` tinyint NOT NULL DEFAULT 1 COMMENT '处理状态：1-待处理 2-处理中 3-已回复 4-已关闭',
  `feedbackTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '反馈时间',
  `replyContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '回复内容',
  `replyTime` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `contactInfo` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `relatedResourceID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联资源ID',
  `priority` tinyint NOT NULL DEFAULT 2 COMMENT '优先级：1-低 2-中 3-高',
  PRIMARY KEY (`feedbackID`) USING BTREE,
  UNIQUE INDEX `uk_feedback_id`(`feedbackID` ASC) USING BTREE,
  INDEX `idx_feedback_student`(`studentID` ASC) USING BTREE,
  INDEX `idx_feedback_status`(`processStatus` ASC) USING BTREE,
  INDEX `idx_feedback_time`(`feedbackTime` ASC) USING BTREE,
  INDEX `idx_feedback_admin`(`processAdminID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '反馈表' ROW_FORMAT = Dynamic;

-- =============================================
-- 5. 医生排班表 (改造自座位表)
-- =============================================
DROP TABLE IF EXISTS `doctor_schedule`;
CREATE TABLE `doctor_schedule` (
  `scheduleID` bigint NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `doctorID` bigint NOT NULL COMMENT '医生ID',
  `scheduleDate` date NOT NULL COMMENT '出诊日期',
  `timeSlot` tinyint NOT NULL COMMENT '时段：1-上午 2-下午 3-夜诊',
  `startTime` time NOT NULL COMMENT '开始时间',
  `endTime` time NOT NULL COMMENT '结束时间',
  `totalSlots` int NOT NULL COMMENT '总号源数',
  `remainingSlots` int NOT NULL COMMENT '剩余号源',
  `registeredSlots` int NOT NULL DEFAULT 0 COMMENT '已预约数',
  `price` decimal(10,2) NOT NULL COMMENT '挂号费',
  `registrationType` tinyint NOT NULL DEFAULT 1 COMMENT '号源类型：1-普通门诊 2-专家门诊 3-特需门诊',
  `scheduleStatus` tinyint NOT NULL DEFAULT 1 COMMENT '排班状态：0-已停诊 1-可预约 2-已约满',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`scheduleID`) USING BTREE,
  UNIQUE INDEX `uk_schedule_id`(`scheduleID` ASC) USING BTREE,
  INDEX `idx_doctor_date`(`doctorID` ASC, `scheduleDate` ASC) USING BTREE,
  INDEX `idx_date`(`scheduleDate` ASC) USING BTREE,
  INDEX `idx_status`(`scheduleStatus` ASC) USING BTREE,
  CONSTRAINT `fk_schedule_doctor` FOREIGN KEY (`doctorID`) REFERENCES `doctor` (`doctorID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医生排班表' ROW_FORMAT = Dynamic;

-- 插入一周的排班数据
INSERT INTO `doctor_schedule` (`doctorID`, `scheduleDate`, `timeSlot`, `startTime`, `endTime`, `totalSlots`, `remainingSlots`, `price`, `registrationType`, `scheduleStatus`) VALUES
(1001, CURDATE(), 1, '08:00:00', '12:00:00', 20, 15, 30.00, 2, 1),
(1001, CURDATE(), 2, '14:00:00', '18:00:00', 15, 12, 30.00, 2, 1),
(1001, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, '08:00:00', '12:00:00', 20, 20, 30.00, 2, 1),
(1002, CURDATE(), 1, '08:00:00', '12:00:00', 25, 20, 20.00, 1, 1),
(1002, CURDATE(), 2, '14:00:00', '18:00:00', 20, 18, 20.00, 1, 1),
(1002, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, '08:00:00', '12:00:00', 25, 25, 20.00, 1, 1),
(1003, CURDATE(), 1, '08:00:00', '12:00:00', 15, 10, 30.00, 2, 1),
(1003, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 1, '08:00:00', '12:00:00', 15, 15, 30.00, 2, 1),
(1004, CURDATE(), 2, '14:00:00', '18:00:00', 12, 8, 20.00, 1, 1),
(1005, CURDATE(), 1, '08:00:00', '12:00:00', 30, 25, 15.00, 1, 1),
(1005, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, '08:00:00', '12:00:00', 30, 30, 15.00, 1, 1);

-- 预约挂号页面补充号源：新增医生未来7天上下午门诊，均可预约
INSERT INTO `doctor_schedule`
(`doctorID`, `scheduleDate`, `timeSlot`, `startTime`, `endTime`, `totalSlots`, `remainingSlots`, `registeredSlots`, `price`, `registrationType`, `scheduleStatus`)
SELECT d.doctorID,
       DATE_ADD(CURDATE(), INTERVAL days.day_offset DAY) AS scheduleDate,
       slots.timeSlot,
       slots.startTime,
       slots.endTime,
       CASE WHEN d.title LIKE '%主任%' THEN 36 ELSE 30 END AS totalSlots,
       CASE WHEN d.title LIKE '%主任%' THEN 36 ELSE 30 END AS remainingSlots,
       0 AS registeredSlots,
       d.registrationFee AS price,
       CASE WHEN d.title LIKE '%主任%' THEN 2 ELSE 1 END AS registrationType,
       1 AS scheduleStatus
FROM doctor d
JOIN (
  SELECT 0 AS day_offset UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
) days
JOIN (
  SELECT 1 AS timeSlot, '08:00:00' AS startTime, '12:00:00' AS endTime
  UNION ALL
  SELECT 2 AS timeSlot, '14:00:00' AS startTime, '18:00:00' AS endTime
) slots
WHERE d.doctorID BETWEEN 1011 AND 1034;

-- =============================================
-- 6. 挂号记录表 (改造自预约记录)
-- =============================================
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment` (
  `appointmentID` varchar(36) NOT NULL COMMENT '挂号记录ID',
  `patientID` bigint NOT NULL COMMENT '患者ID',
  `scheduleID` bigint NOT NULL COMMENT '排班ID',
  `doctorID` bigint NOT NULL COMMENT '医生ID',
  `departmentID` bigint NOT NULL COMMENT '科室ID',
  `appointmentDate` date NOT NULL COMMENT '预约就诊日期',
  `timeSlot` tinyint NOT NULL COMMENT '时段：1-上午 2-下午 3-夜诊',
  `appointmentNumber` int NOT NULL COMMENT '就诊序号',
  `chiefComplaint` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '主诉/病情描述',
  `appointmentStatus` tinyint NOT NULL DEFAULT 0 COMMENT '挂号状态：0-待支付 1-已预约 2-已签到 3-就诊中 4-已完成 5-已取消 6-已退号 7-已爽约',
  `paymentStatus` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态：0-待支付 1-已支付 2-已退款',
  `paymentAmount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `paymentMethod` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '支付方式',
  `paymentTime` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `cancelReason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '取消原因',
  `cancelTime` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `isReviewed` tinyint NOT NULL DEFAULT 0 COMMENT '是否已评价：0-否 1-是',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`appointmentID`) USING BTREE,
  UNIQUE INDEX `uk_appointment_id`(`appointmentID` ASC) USING BTREE,
  INDEX `idx_patient`(`patientID` ASC) USING BTREE,
  INDEX `idx_doctor`(`doctorID` ASC) USING BTREE,
  INDEX `idx_date`(`appointmentDate` ASC) USING BTREE,
  INDEX `idx_status`(`appointmentStatus` ASC) USING BTREE,
  CONSTRAINT `fk_appointment_patient` FOREIGN KEY (`patientID`) REFERENCES `patient` (`patientID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_appointment_schedule` FOREIGN KEY (`scheduleID`) REFERENCES `doctor_schedule` (`scheduleID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_appointment_doctor` FOREIGN KEY (`doctorID`) REFERENCES `doctor` (`doctorID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_appointment_department` FOREIGN KEY (`departmentID`) REFERENCES `department` (`departmentID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '挂号记录表' ROW_FORMAT = Dynamic;

-- =============================================
-- 7. 就诊记录表 (改造自考勤记录)
-- =============================================
DROP TABLE IF EXISTS `visit_record`;
CREATE TABLE `visit_record` (
  `visitID` bigint NOT NULL AUTO_INCREMENT COMMENT '就诊记录ID',
  `appointmentID` varchar(36) NOT NULL COMMENT '挂号记录ID',
  `patientID` bigint NOT NULL COMMENT '患者ID',
  `doctorID` bigint NOT NULL COMMENT '医生ID',
  `departmentID` bigint NOT NULL COMMENT '科室ID',
  `checkInTime` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `seeDoctorTime` datetime NULL DEFAULT NULL COMMENT '开始就诊时间',
  `finishTime` datetime NULL DEFAULT NULL COMMENT '就诊完成时间',
  `waitingNumber` int NULL DEFAULT NULL COMMENT '排队号码',
  `visitStatus` tinyint NOT NULL DEFAULT 1 COMMENT '就诊状态：1-已签到待就诊 2-就诊中 3-已完成 4-取消',
  `chiefComplaint` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '主诉',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`visitID`) USING BTREE,
  UNIQUE INDEX `uk_visit_id`(`visitID` ASC) USING BTREE,
  INDEX `idx_appointment`(`appointmentID` ASC) USING BTREE,
  INDEX `idx_patient`(`patientID` ASC) USING BTREE,
  INDEX `idx_doctor`(`doctorID` ASC) USING BTREE,
  INDEX `idx_status`(`visitStatus` ASC) USING BTREE,
  CONSTRAINT `fk_visit_appointment` FOREIGN KEY (`appointmentID`) REFERENCES `appointment` (`appointmentID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_visit_patient` FOREIGN KEY (`patientID`) REFERENCES `patient` (`patientID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_visit_doctor` FOREIGN KEY (`doctorID`) REFERENCES `doctor` (`doctorID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_visit_department` FOREIGN KEY (`departmentID`) REFERENCES `department` (`departmentID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '就诊记录表' ROW_FORMAT = Dynamic;

-- =============================================
-- 8. 门诊病历表 (新增)
-- =============================================
DROP TABLE IF EXISTS `medical_record`;
CREATE TABLE `medical_record` (
  `recordID` bigint NOT NULL AUTO_INCREMENT COMMENT '病历ID',
  `visitID` bigint NOT NULL COMMENT '就诊记录ID',
  `appointmentID` varchar(36) NOT NULL COMMENT '挂号记录ID',
  `patientID` bigint NOT NULL COMMENT '患者ID',
  `doctorID` bigint NOT NULL COMMENT '医生ID',
  `chiefComplaint` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主诉',
  `presentIllness` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '现病史',
  `pastHistory` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '既往史',
  `allergyHistory` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '过敏史',
  `physicalExamination` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '体格检查',
  `auxiliaryExamination` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '辅助检查',
  `preliminaryDiagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '初步诊断',
  `finalDiagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '最终诊断',
  `treatmentPlan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '治疗方案',
  `medicalAdvice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '医嘱',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`recordID`) USING BTREE,
  UNIQUE INDEX `uk_record_id`(`recordID` ASC) USING BTREE,
  INDEX `idx_visit`(`visitID` ASC) USING BTREE,
  INDEX `idx_patient`(`patientID` ASC) USING BTREE,
  INDEX `idx_doctor`(`doctorID` ASC) USING BTREE,
  CONSTRAINT `fk_record_visit` FOREIGN KEY (`visitID`) REFERENCES `visit_record` (`visitID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '门诊病历表' ROW_FORMAT = Dynamic;

-- =============================================
-- 9. 处方表 (新增)
-- =============================================
DROP TABLE IF EXISTS `prescription`;
CREATE TABLE `prescription` (
  `prescriptionID` bigint NOT NULL AUTO_INCREMENT COMMENT '处方ID',
  `recordID` bigint NOT NULL COMMENT '病历ID',
  `visitID` bigint NOT NULL COMMENT '就诊记录ID',
  `patientID` bigint NOT NULL COMMENT '患者ID',
  `doctorID` bigint NOT NULL COMMENT '开方医生ID',
  `medicineName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '药品名称',
  `medicineSpec` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '药品规格',
  `dosage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单次剂量',
  `usage` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用法（如：口服、静脉注射）',
  `frequency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '频率（如：每日3次）',
  `course` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '疗程',
  `quantity` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '数量',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '单位',
  `price` decimal(10,2) NULL DEFAULT NULL COMMENT '单价',
  `totalPrice` decimal(10,2) NULL DEFAULT NULL COMMENT '总价',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `prescriptionType` tinyint NOT NULL DEFAULT 1 COMMENT '处方类型：1-西药 2-中成药 3-中药',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-待缴费 2-已缴费 3-已发药',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`prescriptionID`) USING BTREE,
  UNIQUE INDEX `uk_prescription_id`(`prescriptionID` ASC) USING BTREE,
  INDEX `idx_record`(`recordID` ASC) USING BTREE,
  INDEX `idx_patient`(`patientID` ASC) USING BTREE,
  INDEX `idx_doctor`(`doctorID` ASC) USING BTREE,
  CONSTRAINT `fk_prescription_record` FOREIGN KEY (`recordID`) REFERENCES `medical_record` (`recordID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '处方表' ROW_FORMAT = Dynamic;

-- =============================================
-- 10. 检查项目表 (新增)
-- =============================================
DROP TABLE IF EXISTS `checkup_item`;
CREATE TABLE `checkup_item` (
  `itemID` bigint NOT NULL AUTO_INCREMENT COMMENT '检查项目ID',
  `itemName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `itemCode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目代码',
  `departmentID` bigint NULL DEFAULT NULL COMMENT '所属科室',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '检查类别',
  `price` decimal(10,2) NOT NULL COMMENT '检查费用',
  `reportTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '出报告时间',
  `prepInstructions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '检查前准备',
  `itemDesc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '项目说明',
  `itemStatus` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-停用 1-启用',
  PRIMARY KEY (`itemID`) USING BTREE,
  UNIQUE INDEX `uk_item_id`(`itemID` ASC) USING BTREE,
  INDEX `idx_department`(`departmentID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '检查项目表' ROW_FORMAT = Dynamic;

INSERT INTO `checkup_item` (`itemName`, `itemCode`, `category`, `price`, `reportTime`, `prepInstructions`) VALUES
('血常规', 'LAB001', '血液检查', 25.00, '30分钟', '无需空腹'),
('尿常规', 'LAB002', '尿液检查', 20.00, '30分钟', '留取中段尿'),
('肝功能全套', 'LAB003', '血液检查', 80.00, '2小时', '空腹8小时以上'),
('肾功能', 'LAB004', '血液检查', 60.00, '2小时', '空腹8小时以上'),
('血糖', 'LAB005', '血液检查', 15.00, '30分钟', '空腹或餐后2小时'),
('血脂四项', 'LAB006', '血液检查', 50.00, '2小时', '空腹8小时以上'),
('心电图', 'ECG001', '功能检查', 35.00, '15分钟', '检查前静息5分钟'),
('腹部B超', 'US001', '超声检查', 80.00, '30分钟', '空腹'),
('胸部X光', 'XR001', '放射检查', 60.00, '1小时', '无'),
('CT平扫', 'CT001', '放射检查', 350.00, '2小时', '根据检查部位准备');

-- =============================================
-- 11. 检查预约表 (新增)
-- =============================================
DROP TABLE IF EXISTS `checkup_order`;
CREATE TABLE `checkup_order` (
  `orderID` bigint NOT NULL AUTO_INCREMENT COMMENT '检查预约ID',
  `patientID` bigint NOT NULL COMMENT '患者ID',
  `visitID` bigint NULL DEFAULT NULL COMMENT '就诊记录ID',
  `recordID` bigint NULL DEFAULT NULL COMMENT '病历ID',
  `itemID` bigint NOT NULL COMMENT '检查项目ID',
  `appointmentID` varchar(36) NULL DEFAULT NULL COMMENT '关联挂号ID',
  `orderNumber` varchar(20) NOT NULL COMMENT '检查单号',
  `checkupDate` date NULL DEFAULT NULL COMMENT '检查日期',
  `checkupTime` time NULL DEFAULT NULL COMMENT '检查时间',
  `checkupLocation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '检查地点',
  `price` decimal(10,2) NOT NULL COMMENT '检查费用',
  `paymentStatus` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态：0-待支付 1-已支付 2-已退款',
  `orderStatus` tinyint NOT NULL DEFAULT 1 COMMENT '订单状态：1-待检查 2-已完成 3-已取消',
  `reportURL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '报告URL',
  `reportStatus` tinyint NOT NULL DEFAULT 0 COMMENT '报告状态：0-未出 1-已出',
  `reportTime` datetime NULL DEFAULT NULL COMMENT '报告出具时间',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`orderID`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`orderID` ASC) USING BTREE,
  INDEX `idx_patient`(`patientID` ASC) USING BTREE,
  INDEX `idx_visit`(`visitID` ASC) USING BTREE,
  INDEX `idx_item`(`itemID` ASC) USING BTREE,
  CONSTRAINT `fk_checkup_order_patient` FOREIGN KEY (`patientID`) REFERENCES `patient` (`patientID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_checkup_order_item` FOREIGN KEY (`itemID`) REFERENCES `checkup_item` (`itemID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '检查预约表' ROW_FORMAT = Dynamic;

-- =============================================
-- 12. 就医评价表 (改造自反馈表)
-- =============================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `reviewID` varchar(20) NOT NULL COMMENT '评价ID',
  `patientID` bigint NOT NULL COMMENT '患者ID',
  `doctorID` bigint NOT NULL COMMENT '医生ID',
  `appointmentID` varchar(36) NOT NULL COMMENT '挂号记录ID',
  `departmentID` bigint NOT NULL COMMENT '科室ID',
  `visitID` bigint NULL DEFAULT NULL COMMENT '就诊记录ID',
  `overallRating` tinyint NOT NULL COMMENT '总体评分 1-5',
  `attitudeRating` tinyint NULL COMMENT '服务态度评分 1-5',
  `skillRating` tinyint NULL COMMENT '医疗技术评分 1-5',
  `environmentRating` tinyint NULL COMMENT '就医环境评分 1-5',
  `reviewContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价内容',
  `reviewImages` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价图片，多个用逗号分隔',
  `replyContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '医生回复',
  `replyTime` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `isAnonymous` tinyint NOT NULL DEFAULT 0 COMMENT '是否匿名：0-否 1-是',
  `reviewStatus` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-待审核 2-已发布 3-已隐藏',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`reviewID`) USING BTREE,
  UNIQUE INDEX `uk_review_id`(`reviewID` ASC) USING BTREE,
  INDEX `idx_patient`(`patientID` ASC) USING BTREE,
  INDEX `idx_doctor`(`doctorID` ASC) USING BTREE,
  INDEX `idx_rating`(`overallRating` ASC) USING BTREE,
  CONSTRAINT `fk_review_patient` FOREIGN KEY (`patientID`) REFERENCES `patient` (`patientID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_review_doctor` FOREIGN KEY (`doctorID`) REFERENCES `doctor` (`doctorID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_review_appointment` FOREIGN KEY (`appointmentID`) REFERENCES `appointment` (`appointmentID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_review_department` FOREIGN KEY (`departmentID`) REFERENCES `department` (`departmentID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '就医评价表' ROW_FORMAT = Dynamic;

-- =============================================
-- 13. 爽约记录表 (改造自违规记录)
-- =============================================
DROP TABLE IF EXISTS `noshow_record`;
CREATE TABLE `noshow_record` (
  `noshowID` varchar(36) NOT NULL COMMENT '爽约记录ID',
  `patientID` bigint NOT NULL COMMENT '患者ID',
  `appointmentID` varchar(36) NOT NULL COMMENT '挂号记录ID',
  `doctorID` bigint NOT NULL COMMENT '医生ID',
  `departmentID` bigint NOT NULL COMMENT '科室ID',
  `noshowDate` date NOT NULL COMMENT '预约就诊日期',
  `timeSlot` tinyint NOT NULL COMMENT '时段',
  `noshowType` tinyint NOT NULL COMMENT '爽约类型：1-未签到 2-迟到超过30分钟 3-主动取消(超时)',
  `deductCredit` int NOT NULL DEFAULT 10 COMMENT '扣除信用分',
  `processStatus` tinyint NOT NULL DEFAULT 1 COMMENT '处理状态：1-已记录 2-已通知 3-已处理',
  `remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`noshowID`) USING BTREE,
  UNIQUE INDEX `uk_noshow_id`(`noshowID` ASC) USING BTREE,
  INDEX `idx_patient`(`patientID` ASC) USING BTREE,
  INDEX `idx_doctor`(`doctorID` ASC) USING BTREE,
  INDEX `idx_date`(`noshowDate` ASC) USING BTREE,
  CONSTRAINT `fk_noshow_patient` FOREIGN KEY (`patientID`) REFERENCES `patient` (`patientID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_noshow_appointment` FOREIGN KEY (`appointmentID`) REFERENCES `appointment` (`appointmentID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_noshow_doctor` FOREIGN KEY (`doctorID`) REFERENCES `doctor` (`doctorID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '爽约记录表' ROW_FORMAT = Dynamic;

-- =============================================
-- 14. 候补记录表 (改造)
-- =============================================
DROP TABLE IF EXISTS `waitlist_record`;
CREATE TABLE `waitlist_record` (
  `waitlistID` varchar(36) NOT NULL COMMENT '候补记录ID',
  `patientID` bigint NOT NULL COMMENT '患者ID',
  `doctorID` bigint NOT NULL COMMENT '医生ID',
  `departmentID` bigint NOT NULL COMMENT '科室ID',
  `waitlistDate` date NOT NULL COMMENT '候补日期',
  `timeSlot` tinyint NOT NULL COMMENT '候补时段',
  `chiefComplaint` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '病情描述',
  `waitlistStatus` tinyint NOT NULL DEFAULT 0 COMMENT '候补状态：0-候补中 1-已获得号源 2-已过期 3-已取消',
  `notifyStatus` tinyint NOT NULL DEFAULT 0 COMMENT '通知状态：0-未通知 1-已通知 2-已过期',
  `notifyTime` datetime NULL DEFAULT NULL COMMENT '通知时间',
  `validUntil` datetime NULL DEFAULT NULL COMMENT '候补有效截止时间',
  `priority` int NOT NULL DEFAULT 0 COMMENT '优先级',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`waitlistID`) USING BTREE,
  UNIQUE INDEX `uk_waitlist_id`(`waitlistID` ASC) USING BTREE,
  INDEX `idx_patient`(`patientID` ASC) USING BTREE,
  INDEX `idx_doctor`(`doctorID` ASC) USING BTREE,
  INDEX `idx_status`(`waitlistStatus` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  CONSTRAINT `fk_waitlist_patient` FOREIGN KEY (`patientID`) REFERENCES `patient` (`patientID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_waitlist_doctor` FOREIGN KEY (`doctorID`) REFERENCES `doctor` (`doctorID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_waitlist_department` FOREIGN KEY (`departmentID`) REFERENCES `department` (`departmentID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '候补记录表' ROW_FORMAT = Dynamic;

-- =============================================
-- 15. 通知消息表 (保留)
-- =============================================
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `notificationID` varchar(20) NOT NULL COMMENT '通知ID',
  `patientID` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `doctorID` bigint NULL DEFAULT NULL COMMENT '医生ID',
  `adminID` int NULL DEFAULT NULL COMMENT '管理员ID',
  `notificationType` tinyint NOT NULL COMMENT '通知类型：1-预约成功 2-就诊提醒 3-候补成功 4-退号通知 5-医生回复 6-系统通知',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `notificationContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知内容',
  `relatedID` varchar(36) NULL DEFAULT NULL COMMENT '关联ID（挂号ID等）',
  `notificationStatus` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-未读 2-已读',
  `sendTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `readTime` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `expireTime` datetime NULL DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`notificationID`) USING BTREE,
  UNIQUE INDEX `uk_notification_id`(`notificationID` ASC) USING BTREE,
  INDEX `idx_patient`(`patientID` ASC) USING BTREE,
  INDEX `idx_doctor`(`doctorID` ASC) USING BTREE,
  INDEX `idx_status`(`notificationStatus` ASC) USING BTREE,
  INDEX `idx_type`(`notificationType` ASC) USING BTREE,
  CONSTRAINT `fk_notification_patient` FOREIGN KEY (`patientID`) REFERENCES `patient` (`patientID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_notification_doctor` FOREIGN KEY (`doctorID`) REFERENCES `doctor` (`doctorID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_notification_admin` FOREIGN KEY (`adminID`) REFERENCES `admin` (`adminID`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知消息表' ROW_FORMAT = Dynamic;

-- =============================================
-- 16. 操作日志表 (保留)
-- =============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `logID` varchar(20) NOT NULL COMMENT '日志ID',
  `userType` tinyint NOT NULL COMMENT '用户类型：1-患者 2-医生 3-管理员',
  `userID` bigint NOT NULL COMMENT '用户ID',
  `operationType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `operationModule` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '操作模块',
  `logContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日志内容',
  `logLevel` tinyint NOT NULL DEFAULT 2 COMMENT '日志级别：1-紧急 2-警告 3-信息',
  `ipAddress` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'IP地址',
  `userAgent` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '浏览器信息',
  `logTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`logID`) USING BTREE,
  UNIQUE INDEX `uk_log_id`(`logID` ASC) USING BTREE,
  INDEX `idx_user`(`userType` ASC, `userID` ASC) USING BTREE,
  INDEX `idx_time`(`logTime` ASC) USING BTREE,
  INDEX `idx_module`(`operationModule` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- =============================================
-- 17. 检查报告表
-- =============================================
DROP TABLE IF EXISTS `check_report`;
CREATE TABLE `check_report` (
  `reportID` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `patientID` bigint NOT NULL COMMENT '患者ID',
  `doctorID` bigint NOT NULL COMMENT '医生ID',
  `departmentID` bigint NOT NULL COMMENT '科室ID',
  `appointmentID` varchar(36) NULL COMMENT '关联挂号ID',
  `reportName` varchar(100) NOT NULL COMMENT '报告名称',
  `reportType` varchar(20) NOT NULL COMMENT '报告类型：lab-化验 image-影像 physical-体检',
  `reportContent` text NULL COMMENT '检查结果',
  `doctorAdvice` text NULL COMMENT '医生建议',
  `reportStatus` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-处理中 2-已完成',
  `checkDate` date NULL COMMENT '检查日期',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`reportID`),
  INDEX `idx_patient`(`patientID`),
  INDEX `idx_doctor`(`doctorID`),
  INDEX `idx_type`(`reportType`),
  INDEX `idx_date`(`checkDate`),
  CONSTRAINT `fk_report_patient` FOREIGN KEY (`patientID`) REFERENCES `patient` (`patientID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_report_doctor` FOREIGN KEY (`doctorID`) REFERENCES `doctor` (`doctorID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_report_department` FOREIGN KEY (`departmentID`) REFERENCES `department` (`departmentID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '检查报告表' ROW_FORMAT = Dynamic;

INSERT INTO `check_report` (`patientID`, `doctorID`, `departmentID`, `reportName`, `reportType`, `reportContent`, `doctorAdvice`, `reportStatus`, `checkDate`) VALUES
(2001, 1001, 1, '血常规', 'lab', '白细胞计数正常，红细胞计数正常，血红蛋白浓度正常，血小板计数正常。', '建议保持健康饮食和作息，定期复查。', 2, CURDATE()),
(2001, 1001, 1, '肝功能全套', 'lab', 'ALT 35 U/L，AST 28 U/L，GGT 45 U/L，总胆红素 12.5 μmol/L。', '继续保持健康生活习惯，避免过度饮酒。', 2, DATE_ADD(CURDATE(), INTERVAL -2 DAY)),
(2001, 1002, 2, '胸部CT', 'image', '双肺纹理清晰，未见明显实质性病变。', '无特殊处理，如有不适随诊。', 2, DATE_ADD(CURDATE(), INTERVAL -5 DAY)),
(2001, 1001, 1, '心电图', 'lab', '待出具', '待出具', 1, CURDATE());

SET FOREIGN_KEY_CHECKS = 1;

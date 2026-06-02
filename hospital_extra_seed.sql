-- =============================================
-- 医院预约系统增量种子数据
-- 用法：在已存在 hospital_appointment 数据库时执行本文件。
-- 说明：不会删表；科室/医生按固定主键更新，号源按医生+日期+时段防重复插入。
-- =============================================

USE hospital_appointment;
SET NAMES utf8mb4;

INSERT INTO `department`
(`departmentID`, `departmentName`, `departmentType`, `departmentLocation`, `departmentDesc`, `departmentIcon`, `departmentStatus`, `displayOrder`, `createTime`)
VALUES
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
(22, '耳鼻咽喉头颈外科', 8, '外滩院区 门诊楼5楼A区', '诊治鼻炎鼻窦炎、咽喉疾病、耳科疾病及头颈部常见疾病。', 'icon-ent', 1, 22, NOW())
ON DUPLICATE KEY UPDATE
departmentName = VALUES(departmentName),
departmentType = VALUES(departmentType),
departmentLocation = VALUES(departmentLocation),
departmentDesc = VALUES(departmentDesc),
departmentIcon = VALUES(departmentIcon),
departmentStatus = VALUES(departmentStatus),
displayOrder = VALUES(displayOrder);

INSERT INTO `doctor`
(`doctorID`, `doctorName`, `doctorPassword`, `doctorGender`, `doctorPhone`, `doctorEmail`, `departmentID`, `title`, `specialty`, `doctorIntro`, `doctorPhoto`, `registrationFee`, `doctorStatus`, `createTime`)
VALUES
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
(1034, '包卫亮', '$2a$10$Ggxx3lRWbSO67dwWHRWQU.BqTp1m8of.jTHl/nJvKw5OmVF4Lc7b.', 1, '13800002034', 'baoweiliang@hospital.com', 22, '主治医师', '鼻炎鼻窦炎、咽喉疾病、儿童耳鼻喉门诊、头颈外科初筛', '擅长耳鼻咽喉头颈外科常见病、多发病的诊治。', NULL, 18.00, 1, NOW())
ON DUPLICATE KEY UPDATE
doctorName = VALUES(doctorName),
doctorGender = VALUES(doctorGender),
doctorPhone = VALUES(doctorPhone),
doctorEmail = VALUES(doctorEmail),
departmentID = VALUES(departmentID),
title = VALUES(title),
specialty = VALUES(specialty),
doctorIntro = VALUES(doctorIntro),
doctorPhoto = VALUES(doctorPhoto),
registrationFee = VALUES(registrationFee),
doctorStatus = VALUES(doctorStatus);

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
WHERE d.doctorID BETWEEN 1011 AND 1034
  AND NOT EXISTS (
    SELECT 1
    FROM doctor_schedule existed
    WHERE existed.doctorID = d.doctorID
      AND existed.scheduleDate = DATE_ADD(CURDATE(), INTERVAL days.day_offset DAY)
      AND existed.timeSlot = slots.timeSlot
  );

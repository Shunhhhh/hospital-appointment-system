SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- CREATE DATABASE hospital_appointment;
USE hospital_appointment;
-- 新增医生（密码明文为：password）
INSERT INTO doctor
  (doctorName, doctorPassword, doctorGender, doctorPhone, doctorEmail, departmentID, title, specialty, doctorIntro, doctorPhoto, registrationFee, doctorStatus)
VALUES
  ('赵勇', '$2b$10$O7HiEom.PRzBccRN6txnj.a89GFjguY7zd3Nyl2QDsXhbiGQf0.FK', 1, '13800001006', 'zhaoyong@hospital.com', 2, '主治医师', '普外科常见病', '外科门诊常见病诊疗', NULL, 20.00, 1),
  ('孙倩', '$2b$10$O7HiEom.PRzBccRN6txnj.a89GFjguY7zd3Nyl2QDsXhbiGQf0.FK', 2, '13800001007', 'sunqian@hospital.com', 6, '副主任医师', '白内障、青光眼', '眼科专科门诊', NULL, 30.00, 1),
  ('周杰', '$2b$10$O7HiEom.PRzBccRN6txnj.a89GFjguY7zd3Nyl2QDsXhbiGQf0.FK', 1, '13800001008', 'zhoujie@hospital.com', 8, '主治医师', '皮肤过敏、湿疹', '皮肤科常见病诊疗', NULL, 20.00, 1),
  ('刘敏', '$2b$10$O7HiEom.PRzBccRN6txnj.a89GFjguY7zd3Nyl2QDsXhbiGQf0.FK', 2, '13800001009', 'liumin@hospital.com', 7, '主治医师', '龋齿、牙周病', '口腔科常见病诊疗', NULL, 20.00, 1),
  ('高磊', '$2b$10$O7HiEom.PRzBccRN6txnj.a89GFjguY7zd3Nyl2QDsXhbiGQf0.FK', 1, '13800001010', 'gaolei@hospital.com', 10, '主任医师', '急危重症', '急诊科救治', NULL, 30.00, 1);
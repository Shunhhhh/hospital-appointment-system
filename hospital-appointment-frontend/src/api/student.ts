import axios from "axios";

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface StudentUser {
  studentID: number;
  studentName: string;

  studentCollege: string;
  studentPoints: number;
  studentGrade: number;

  studentPhoneNumber: string;
  studentUserName: string;

}

export interface ResetPasswordDTO {
  id: number;        // 对应后端 AuthDto.getId()
  phone: string;     // AuthDto.getPhone()
  password: string;  // AuthDto.getPassword()
}

export const studentService = {
  checkSelfInformation: (studentID: number) =>
    axios.get<ApiResponse<StudentUser>>("/api/student/checkSelfInformation", {
      params: { studentID },
    }),

  modifySelfInformation: (payload: Partial<StudentUser>) =>
    axios.post<ApiResponse<boolean>>("/api/student/modifySelfInformation", payload),

  // 重置密码
  resetStudentPassword: (dto: ResetPasswordDTO) =>
    axios.post<ApiResponse<null>>("/api/auth/resetStudentPassword", dto),
};

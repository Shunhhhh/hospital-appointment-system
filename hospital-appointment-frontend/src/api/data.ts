export interface StudyRoom {
    studyRoomID: number;
    studyRoomCapacity: number;
    studyRoomLocation: string;
    studyRoomType: number;
    studyRoomOpentime: string;
    status: number;
    currentlyIdleSeat: number;
    studyRoomName: string;
    studyRoomClosetime: string;
}

export interface SeminarRoom {
    seminarRoomID: number;
    seminarRoomLocation: string;
    seminarRoomMin: number;
    seminarRoomMax: number;
    seminarRoomStatus: number;
    currentNum: number;
    seminarRoomOpentime: string;
    seminarRoomName: string;
    seminarRoomClosetime: string;
}

export type RoomResource = StudyRoom & {
    isSeminarRoom?: boolean; // 可选的辅助标记
    resourceType: number; // 统一资源类型 (0:自习室, 1:研讨室)
};

export function getRoomTypeText(type: number): string {
  switch (type) {
    case 0: return "普通自习室";
    case 1: return "考研专座";
    case 2: return "研讨室";
    default: return "神秘类型";
  }
}


export function getRoomStatusText(status: number): string {
  switch (status) {
    case 0: return "关闭中";
    case 1: return "开放中";
    case 2: return "维护中";
    default: return "神秘状态";
  }
}

export interface Seat {
  seatID: string;
  seatLocation: string;
  seatType: number;
  seatStatus: number;
  seatBelonging: string;
  seatNumber: number;
  seatCheckInStatus: number;
}

export function getSeatStatusText(status: number): string {
  switch (status) {
    case 0: return "FREE";
    case 1: return "RESERVED";
    case 2: return "UNSIGNED";
    case 3: return "USED";
    case 4: return "AWAY";
    case 5: return "REPAIR";
    default: return "UNKNOWN";
  }
}

export function getSeatTypeText(type: number): string {
  switch (type) {
    case 0: return "NORMAL";
    case 1: return "POSTGRAD";
    default: return "UNKNOWN";
  }
}

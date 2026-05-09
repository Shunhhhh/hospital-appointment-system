<template>
    <div class="page-container">
        <!-- 顶部：返回按钮 -->
        <a-button class="btn-back" type="default" @click="router.back()">
            <template #icon>
                <LeftOutlined />
            </template>
            返回
        </a-button>

        <!-- 标题 -->
        <div class="page-title">
            <h2 class="title-text">{{ roomName }} - 座位状态</h2>
            <div class="sub-text">
                时间范围：{{ startTime }} - {{ endTime }}
            </div>
        </div>

        <!-- 信息区 + 图例 -->
        <a-card class="info-card" :bordered="false">
            <div class="info-row">
                <div class="info-item">
                    <div class="info-label">房间ID</div>
                    <div class="info-value">{{ roomId }}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">座位数量</div>
                    <div class="info-value">{{ seatCount }}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">温馨提示</div>
                    <div class="info-value">维修状态座位暂不可约，请耐心等待</div>
                </div>
            </div>

            <div class="legend">
                <span class="legend-title">状态图例：</span>
                <a-tag class="tag" color="green">可预约</a-tag>
                <a-tag class="tag" color="gold">部分可约</a-tag>
                <a-tag class="tag" color="default">不可预约</a-tag>
                <a-tag class="tag" color="blue">维修中</a-tag>
            </div>
        </a-card>

        <!-- 座位区 -->
        <a-card class="seat-card" :bordered="false">
            <a-skeleton v-if="loading" active />

            <a-empty
                v-else-if="!seats || seats.length === 0"
                description="暂无座位数据"
            />

            <div v-else class="seat-grid">
                <div
                    v-for="seat in seats"
                    :key="seat.seatID"
                    class="seat"
                    :class="[
                      getSeatStatusText(seat.seatStatus),
                      ['REPAIR', 'USED'].includes(getSeatStatusText(seat.seatStatus)) ? 'seat-disabled' : ''
                    ]"

                    @click="onSelect(seat)"
                    :title="`座位 ${seat.seatNumber}`"
                >
                    <div class="seat-no">{{ seat.seatNumber }}</div>
                </div>
            </div>
        </a-card>
    </div>
</template>

<script setup lang="ts">
    import { onMounted, ref } from "vue";
    import axios from "axios";
    import { useRoute, useRouter } from "vue-router";
    import { LeftOutlined } from "@ant-design/icons-vue";
    import { Seat, getSeatStatusText, getSeatTypeText } from "@/api/data";
    import { message } from "ant-design-vue";


    const route = useRoute();
    const router = useRouter();

    // 房间信息
    const roomName = String(route.query.roomName || "自习室");
    const seatCount = ref<number>(0);
    const roomId = Number(route.query.roomId || 0);

    // 用户查询的时间范围
    const startTime = String(route.query.start || "08:00");
    const endTime = String(route.query.end || "22:00");

    // 黄色座位部分占用时段（示例）
    const seatPartialTime = {
        start: "10:00",
        end: "12:00",
    };

    const seats = ref<Seat[]>([]);
    const loading = ref(false);

    const statusLabel = (s: string) => {
        const map: Record<string, string> = {
            FREE: "可预约",
            RESERVED: "部分可约",
            UNSIGNED: "未签到",
            USED: "占用中",
            AWAY: "暂离",
            REPAIR: "维修中",
        };
        return map[s] || s;
    };

    const loadSeats = async (roomID: number) => {
        loading.value = true;
        try {
            const res = await axios.get(`/api/seatManage/seats/${roomID}`);


            if (res.data && res.data.data) {
  seats.value = res.data.data;
  seatCount.value = seats.value.length; 
} else {
  seats.value = [];
  seatCount.value = 0;
  console.warn("后端返回数据为空");
}

        } catch (error) {
            console.error("加载座位失败:", error);
            seats.value = [];
        } finally {
            loading.value = false;
        }
    };

    // 点击座位
    const onSelect = (seat: Seat) => {
        const status = getSeatStatusText(seat.seatStatus);
        if (status === "REPAIR" || status === "USED") return;

        let availableStart = startTime;
        let availableEnd = endTime;

        if (status === "RESERVED") {
            if (startTime < seatPartialTime.start && endTime > seatPartialTime.end) {
                availableStart = seatPartialTime.end;
            } else if (startTime >= seatPartialTime.start && startTime < seatPartialTime.end) {
                availableStart = seatPartialTime.end;
            }
        }

        router.push({
            path: "/reservation-form",
            query: {
                seatId: String(seat.seatID),
                type: String(getSeatTypeText(seat.seatType)),
                roomId: String(roomId),
                start: availableStart,
                end: availableEnd,
            },
        });
    };

    onMounted(() => {
        loadSeats(roomId);
    });
</script>

<style scoped>
    .page-container {
        padding: 18px 18px 28px 18px;
        background: #f5f7fb;
        min-height: 100vh;
    }

    .btn-back {
        margin-bottom: 12px;
        border-radius: 10px;
    }

    .page-title {
        padding: 6px 2px 14px 2px;
    }

    .title-text {
        margin: 0;
        font-size: 22px;
        font-weight: 700;
        color: #111827;
        letter-spacing: 0.2px;
    }

    .sub-text {
        margin-top: 6px;
        font-size: 13px;
        color: #6b7280;
    }

    .info-card {
        border-radius: 14px;
        box-shadow: 0 6px 18px rgba(15, 23, 42, 0.06);
        margin-bottom: 14px;
    }

    .info-row {
        display: grid;
        grid-template-columns: repeat(3, minmax(0, 1fr));
        gap: 10px;
        margin-bottom: 12px;
    }

    .info-item {
        background: #f9fafb;
        border: 1px solid #eef2f7;
        border-radius: 12px;
        padding: 10px 12px;
    }

    .info-label {
        font-size: 12px;
        color: #6b7280;
        margin-bottom: 4px;
    }

    .info-value {
        font-size: 14px;
        color: #111827;
        font-weight: 600;
    }

    .legend {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        gap: 8px;
        padding-top: 6px;
        border-top: 1px dashed #e5e7eb;
    }

    .legend-title {
        font-size: 13px;
        color: #374151;
        font-weight: 600;
        margin-right: 4px;
    }

    .tag {
        border-radius: 999px;
        padding: 2px 10px;
        font-size: 12px;
    }

    .seat-card {
        border-radius: 14px;
        box-shadow: 0 6px 18px rgba(15, 23, 42, 0.06);
    }

    .seat-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
        gap: 12px;
        margin-top: 2px;
    }

    .seat {
  height: 56px;
  border-radius: 14px;


  color: rgba(17, 24, 39, 0.85);

  cursor: pointer;
  font-weight: 700;

  display: flex;
  align-items: center;
  justify-content: center;

  border: 1px solid rgba(15, 23, 42, 0.08);
  box-shadow: 0 6px 14px rgba(15, 23, 42, 0.06);

  transition: transform 0.12s ease, box-shadow 0.12s ease, filter 0.12s ease;
}

.seat:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.10);
}


    .seat-no {
  color: rgba(17, 24, 39, 0.85);
  font-size: 18px;
  line-height: 1;
}


    .seat-status {
        margin-top: 4px;
        font-size: 12px;
        opacity: 0.9;
        font-weight: 600;
    }

    /* 可预约：浅绿 */
.FREE {
  background: rgba(82, 196, 26, 0.18);
  border-color: rgba(82, 196, 26, 0.25);
}

/* 部分可约：浅橙 */
.RESERVED {
  background: rgba(250, 173, 20, 0.22);
  border-color: rgba(250, 173, 20, 0.30);
}

/* 未签到：浅红 */
.UNSIGNED {
  background: rgba(255, 77, 79, 0.18);
  border-color: rgba(255, 77, 79, 0.25);
}

/* 暂离：浅紫 */
.AWAY {
  background: rgba(114, 46, 209, 0.18);
  border-color: rgba(114, 46, 209, 0.25);
}

/* 维修：浅蓝 */
.REPAIR {
  background: rgba(24, 144, 255, 0.20);
  border-color: rgba(24, 144, 255, 0.28);
}

/* 占用：浅灰 */
.USED {
  background: rgba(156, 163, 175, 0.22);
  border-color: rgba(156, 163, 175, 0.28);
  cursor: not-allowed;
  box-shadow: none;
}


    .FREE:hover,
    .RESERVED:hover,
    .UNSIGNED:hover,
    .AWAY:hover,
    .REPAIR:hover {
        transform: translateY(-2px);
        opacity: 0.92;
    }

    @media (max-width: 768px) {
        .info-row {
            grid-template-columns: 1fr;
        }
    }

    .seat-disabled {
        cursor: not-allowed !important;
        opacity: 0.75;
        box-shadow: none;
    }

/* 禁用时不抬起 */
    .seat-disabled:hover {
        transform: none !important;
        opacity: 0.75 !important;
    }

</style>


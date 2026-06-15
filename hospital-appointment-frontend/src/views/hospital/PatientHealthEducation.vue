<template>
  <div class="education-page">
    <section class="education-shell card-surface">
      <header class="education-head">
        <div>
          <div class="title-line">
            <h1 class="page-title">健康宣传</h1>
            <span class="head-note">按科室浏览常见健康知识，帮助你更快找到对应科普内容</span>
          </div>
        </div>
      </header>

      <div class="education-layout">
        <aside class="dept-panel card-surface">
          <div class="panel-title">科室</div>
          <button
            v-for="dept in departments"
            :key="dept.key"
            type="button"
            class="dept-item"
            :class="{ active: activeDept === dept.key }"
            @click="activeDept = dept.key"
          >
            {{ dept.label }}
          </button>
        </aside>

        <main class="knowledge-panel card-surface">
          <div class="panel-title">{{ activeDepartment?.label }}</div>
          <p class="panel-desc">{{ activeDepartment?.desc }}</p>

          <div class="knowledge-list">
            <article v-for="item in activeDepartment?.articles || []" :key="item.title" class="knowledge-card">
              <div class="knowledge-tag">{{ item.tag }}</div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.content }}</p>
            </article>
          </div>
        </main>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'

type KnowledgeItem = {
  tag: string
  title: string
  content: string
}

type DepartmentKnowledge = {
  key: string
  label: string
  desc: string
  articles: KnowledgeItem[]
}

const router = useRouter()
const activeDept = ref('internal')

const departments: DepartmentKnowledge[] = [
  {
    key: 'internal',
    label: '内科',
    desc: '关注常见内科疾病、慢病管理和基础健康监测。',
    articles: [
      { tag: '1', title: '慢病随访要点', content: '规律复诊、按时用药、监测血压和血糖变化，有助于更早发现异常并及时调整治疗方案。' },
      { tag: '2', title: '身体不适先观察什么', content: '记录发热、咳嗽、胸闷、头晕等症状持续时间和变化情况，便于医生更快判断病因。' }
    ]
  },
  {
    key: 'surgery',
    label: '外科',
    desc: '关注手术治疗、创伤护理和术后恢复。',
    articles: [
      { tag: '1', title: '术后恢复注意什么', content: '保持伤口清洁干燥，按医嘱复诊换药，出现红肿热痛或渗液时应及时回院处理。' },
      { tag: '2', title: '创伤后如何处理', content: '先止血、包扎并减少活动，怀疑骨折或大面积伤口时不要随意移动，尽快就医。' }
    ]
  },
  {
    key: 'pediatrics',
    label: '儿科',
    desc: '关注儿童发热、咳嗽、腹泻和生长发育。',
    articles: [
      { tag: '1', title: '孩子发热先看什么', content: '先观察精神状态和饮水情况，体温不是唯一标准；若高热持续不退或伴抽搐，应尽快就医。' },
      { tag: '2', title: '儿童腹泻的家庭护理', content: '重点补液，少量多次喂水或口服补液盐；若出现尿少、嗜睡或反复呕吐，要及时就诊。' }
    ]
  },
  {
    key: 'orthopedics',
    label: '骨科',
    desc: '关注骨关节疼痛、骨折、扭伤与术后恢复。',
    articles: [
      { tag: '1', title: '扭伤后第一步怎么做', content: '尽快冰敷、加压、抬高并减少活动，48 小时内避免热敷和揉搓，以免加重肿胀。' },
      { tag: '2', title: '骨质疏松如何预防', content: '保证钙和维生素 D 摄入，适量负重运动，老年人尤其要防跌倒。' }
    ]
  },
  {
    key: 'ophthalmology',
    label: '眼科',
    desc: '关注视力下降、眼干眼涩和常见眼病。',
    articles: [
      { tag: '1', title: '用眼过度怎么缓解', content: '每隔一段时间远眺放松，减少长时间盯屏，必要时可遵医嘱使用人工泪液缓解干涩。' },
      { tag: '2', title: '视力下降时要注意什么', content: '若出现视物模糊、眼痛、畏光或黑影遮挡，应尽快就诊排查屈光、炎症或眼底问题。' }
    ]
  },
  {
    key: 'stomatology',
    label: '口腔科',
    desc: '关注牙痛、龋齿、牙周和口腔黏膜问题。',
    articles: [
      { tag: '1', title: '牙痛时先怎么处理', content: '先避免冷热刺激和辛辣食物，尽快预约口腔检查，不要长期依赖止痛药掩盖症状。' },
      { tag: '2', title: '口腔清洁的日常习惯', content: '每天早晚刷牙，配合牙线或冲牙器清洁牙缝，定期洁牙有助于减少龋齿和牙龈炎。' }
    ]
  },
  {
    key: 'dermatology',
    label: '皮肤科',
    desc: '关注皮疹、瘙痒、痤疮和常见皮肤问题。',
    articles: [
      { tag: '1', title: '皮肤瘙痒怎么护理', content: '减少抓挠，避免过热洗澡和刺激性护肤品；如果反复出现红疹或脱屑，应及时就医。' },
      { tag: '2', title: '痘痘护理的误区', content: '不要频繁挤压或刷酸过度，保持清洁和规律作息；严重痤疮建议由医生评估治疗。' }
    ]
  },
  {
    key: 'tcm',
    label: '中医科',
    desc: '关注体质调理、慢病辅助管理和传统疗法。',
    articles: [
      { tag: '1', title: '中医调理适合哪些情况', content: '适合体质调理、失眠、慢性疲劳和部分疼痛问题，但急性高热或急症仍应优先就医。' },
      { tag: '2', title: '服用中药要注意什么', content: '按医嘱服药，避免自行加减剂量；如同时服用西药，最好告知医生以便判断相互影响。' }
    ]
  },
  {
    key: 'emergency',
    label: '急诊科',
    desc: '负责突发疾病、外伤和危急症状的快速处理。',
    articles: [
      { tag: '1', title: '什么情况要去急诊', content: '突发胸痛、呼吸困难、意识改变、严重外伤或持续高热等情况，应立即前往急诊处理。' },
      { tag: '2', title: '就诊前要准备什么', content: '带好身份证件、既往病历和正在使用的药物清单，能帮助急诊医生更快判断病情。' }
    ]
  }
]

const activeDepartment = computed(() => departments.find((item) => item.key === activeDept.value) || departments[0])
</script>

<style scoped>
.education-page {
  min-height: 100vh;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(74, 144, 226, 0.16), transparent 32%),
    linear-gradient(180deg, #f6f9fe 0%, #edf4ff 100%);
}

.card-surface {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 42px rgba(38, 79, 170, 0.12);
}

.education-shell {
  max-width: 1440px;
  margin: 0 auto;
  padding: 28px;
}

.education-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 20px;
}

.title-line {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  margin: 0;
  color: #153a73;
  font-size: 30px;
}

.head-note {
  color: #6a7590;
  font-size: 14px;
}

.education-layout {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  gap: 18px;
}

.dept-panel,
.knowledge-panel {
  padding: 18px;
}

.panel-title {
  color: #173f7b;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 14px;
}

.panel-desc {
  margin: -4px 0 18px;
  color: #6a7590;
  font-size: 13px;
  line-height: 1.8;
}

.dept-panel {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.dept-item {
  width: 100%;
  padding: 14px 16px;
  border: 0;
  border-radius: 16px;
  background: #f3f7fd;
  color: #40516d;
  font-size: 14px;
  font-weight: 600;
  text-align: left;
  cursor: pointer;
}

.dept-item.active {
  background: linear-gradient(135deg, #2f7cf6 0%, #4d92ff 100%);
  color: #fff;
  box-shadow: 0 10px 22px rgba(47, 124, 246, 0.22);
}

.knowledge-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.knowledge-card {
  padding: 18px;
  border-radius: 18px;
  background: linear-gradient(180deg, #f8fbff 0%, #edf4ff 100%);
}

.knowledge-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 30px;
  height: 30px;
  margin-bottom: 14px;
  border-radius: 10px;
  background: rgba(47, 124, 246, 0.12);
  color: #276ff5;
  font-size: 13px;
  font-weight: 700;
}

.knowledge-card h3 {
  margin: 0 0 10px;
  color: #173f7b;
  font-size: 18px;
  line-height: 1.4;
}

.knowledge-card p {
  margin: 0;
  color: #55647e;
  font-size: 14px;
  line-height: 1.85;
}

@media (max-width: 960px) {
  .education-shell {
    padding: 20px;
  }

  .education-head {
    flex-direction: column;
  }

  .education-layout,
  .knowledge-list {
    grid-template-columns: 1fr;
  }
}
</style>

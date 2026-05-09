<template>
  <TopNav v-if="!route.meta.hideLayout" />
  <router-view />
</template>

<script setup lang="ts">
import { defineAsyncComponent } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const TopNav = defineAsyncComponent(() => import("./components/TopNav.vue"));
</script>

<template>
  <a-drawer
    :open="visible"
    title="设备详情"
    placement="right"
    width="900"
    :mask-closable="true"
    @close="closeDrawer"
  >
    <a-spin :spinning="loading">
    <a-descriptions v-if="detail" :column="2" bordered size="small" class="detail-desc">
      <a-descriptions-item label="设备编号">{{ detail.deviceCode }}</a-descriptions-item>
      <a-descriptions-item label="设备名称">{{ detail.deviceName }}</a-descriptions-item>
      <a-descriptions-item label="设备型号">{{ detail.deviceModel || '-' }}</a-descriptions-item>
      <a-descriptions-item label="生产厂家">{{ detail.manufacturer || '-' }}</a-descriptions-item>
      <a-descriptions-item label="安装位置">{{ detail.location || '-' }}</a-descriptions-item>
      <a-descriptions-item label="启用日期">{{ detail.useDate || '-' }}</a-descriptions-item>
      <a-descriptions-item label="状态">
        <a-badge :status="detail.status === 1 ? 'success' : 'default'" :text="detail.status === 1 ? '正常' : '停用'" />
      </a-descriptions-item>
      <a-descriptions-item label="创建人">{{ detail.createBy || '-' }}</a-descriptions-item>
      <a-descriptions-item label="创建时间">{{ detail.createTime || '-' }}</a-descriptions-item>
      <a-descriptions-item label="更新人">{{ detail.updateBy || '-' }}</a-descriptions-item>
      <a-descriptions-item label="更新时间">{{ detail.updateTime || '-' }}</a-descriptions-item>
      <a-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</a-descriptions-item>
    </a-descriptions>

    <a-divider orientation="left">
      <span class="attachment-title">附件区</span>
      <a-tag color="blue">{{ attachmentCount }}</a-tag>
    </a-divider>

    <DeviceAttachmentPanel v-if="detail" :device-id="detail.id" @count-change="onCountChange" />
    </a-spin>
  </a-drawer>
</template>

<script lang="ts" name="device-detail-drawer" setup>
  import { ref } from 'vue';
  import DeviceAttachmentPanel from './DeviceAttachmentPanel.vue';
  import { getDeviceById } from '../archive.api';

  const visible = ref(false);
  const loading = ref(false);
  const detail = ref<any>(null);
  const attachmentCount = ref(0);

  async function showDrawer(record: Recordable) {
    visible.value = true;
    loading.value = true;
    detail.value = null;
    attachmentCount.value = 0;
    try {
      detail.value = await getDeviceById({ id: record.id });
    } finally {
      loading.value = false;
    }
  }

  function closeDrawer() {
    visible.value = false;
    detail.value = null;
  }

  function onCountChange(count: number) {
    attachmentCount.value = count;
  }

  defineExpose({ showDrawer });
</script>

<style lang="less" scoped>
  .detail-desc {
    margin-bottom: 8px;
  }

  .attachment-title {
    font-weight: 600;
  }
</style>

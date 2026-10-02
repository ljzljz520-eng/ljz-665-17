<template>
  <BasicDrawer
    v-bind="$attrs"
    @register="registerDrawer"
    title="设备详情"
    width="900px"
    :show-footer="false"
    :destroy-on-close="true"
  >
    <a-descriptions v-if="device" bordered :column="2" size="small" class="detail-desc">
      <a-descriptions-item label="设备编号">{{ device.deviceCode }}</a-descriptions-item>
      <a-descriptions-item label="设备名称">{{ device.deviceName }}</a-descriptions-item>
      <a-descriptions-item label="设备型号">{{ device.model || '-' }}</a-descriptions-item>
      <a-descriptions-item label="生产厂商">{{ device.manufacturer || '-' }}</a-descriptions-item>
      <a-descriptions-item label="安装位置">{{ device.location || '-' }}</a-descriptions-item>
      <a-descriptions-item label="启用日期">{{ device.useDate || '-' }}</a-descriptions-item>
      <a-descriptions-item label="状态">
        <a-tag :color="device.status === '1' ? 'green' : 'red'">
          {{ device.status === '1' ? '正常' : device.status === '0' ? '停用' : '-' }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="创建时间">{{ device.createTime || '-' }}</a-descriptions-item>
      <a-descriptions-item label="备注" :span="2">{{ device.remark || '-' }}</a-descriptions-item>
    </a-descriptions>

    <a-divider orientation="left">附件</a-divider>

    <DeviceFilePanel v-if="currentDeviceId" :key="currentDeviceId" :device-id="currentDeviceId" />
  </BasicDrawer>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { getDeviceById } from '/@/api/device/device.api';
  import DeviceFilePanel from './DeviceFilePanel.vue';

  const device = ref<any>(null);
  const currentDeviceId = ref('');

  const [registerDrawer] = useDrawerInner(async (data) => {
    device.value = null;
    currentDeviceId.value = data?.record?.id || '';
    if (currentDeviceId.value) {
      device.value = await getDeviceById({ id: currentDeviceId.value });
    }
  });
</script>

<style lang="less" scoped>
  .detail-desc {
    margin-bottom: 8px;
  }
</style>

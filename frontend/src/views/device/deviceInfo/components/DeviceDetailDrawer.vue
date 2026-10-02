<template>
  <BasicDrawer
    v-bind="$attrs"
    @register="registerDrawer"
    title="设备详情"
    :width="720"
    :showFooter="false"
    class="device-detail-drawer"
  >
    <a-spin :spinning="loading">
      <div class="detail-section">
        <div class="section-title">
        <Icon icon="ant-design:profile-outlined" />
        <span>基本信息</span>
      </div>
      <a-descriptions :column="2" bordered size="small" class="device-desc">
        <a-descriptions-item label="设备编号">{{ device.deviceCode || '—' }}</a-descriptions-item>
        <a-descriptions-item label="设备名称">{{ device.deviceName || '—' }}</a-descriptions-item>
        <a-descriptions-item label="设备型号">{{ device.deviceModel || '—' }}</a-descriptions-item>
        <a-descriptions-item label="生产厂家">{{ device.manufacturer || '—' }}</a-descriptions-item>
        <a-descriptions-item label="出厂序列号">{{ device.serialNumber || '—' }}</a-descriptions-item>
        <a-descriptions-item label="安装位置">{{ device.location || '—' }}</a-descriptions-item>
        <a-descriptions-item label="启用日期">{{ device.useDate || '—' }}</a-descriptions-item>
        <a-descriptions-item label="质保到期日">{{ device.warrantyDate || '—' }}</a-descriptions-item>
        <a-descriptions-item label="创建人">{{ device.createBy || '—' }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ device.createTime || '—' }}</a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">{{ device.remark || '—' }}</a-descriptions-item>
      </a-descriptions>
      </div>

      <!-- 附件区 -->
      <div class="detail-section attachment-section">
        <div class="section-title">
          <Icon icon="ant-design:paper-clip-outlined" />
          <span>设备附件</span>
          <a-radio-group v-model:value="attachmentType" size="small" class="type-filter">
            <a-radio-button value="all">全部</a-radio-button>
            <a-radio-button v-for="opt in ATTACHMENT_TYPE_OPTIONS" :key="opt.value" :value="opt.value">
              {{ opt.label }}
            </a-radio-button>
          </a-radio-group>
        </div>

        <div class="upload-bar">
          <span class="upload-label">附件类型：</span>
          <a-select v-model:value="uploadType" size="small" style="width: 130px" :options="ATTACHMENT_TYPE_OPTIONS" />
          <a-upload
            :show-upload-list="false"
            :multiple="true"
            :before-upload="beforeUpload"
            accept="image/*,.pdf,.doc,.docx,.xls,.xlsx,.zip,.rar"
          >
            <a-button type="primary" :loading="uploading" :disabled="!deviceId">
              <Icon icon="ant-design:upload-outlined" />
              上传附件
            </a-button>
          </a-upload>
        </div>
        <div class="upload-tip">支持上传设备照片、说明书或维保合同；上传文件将自动绑定到当前设备</div>

        <a-table
          class="attachment-table"
          :columns="attachmentColumns"
          :data-source="filteredAttachments"
          :pagination="false"
          rowKey="id"
          size="small"
          :locale="{ emptyText: '暂无附件' }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'attachmentType'">
              <a-tag :color="getTypeMeta(record.attachmentType).color">
                {{ getTypeMeta(record.attachmentType).label }}
              </a-tag>
            </template>
            <template v-else-if="column.dataIndex === 'fileName'">
              <div class="file-name-cell">
                <img
                  v-if="isImage(record)"
                  :src="getFileUrl(record.filePath)"
                  class="file-thumb"
                  @click="previewImage(record)"
                />
                <FileOutlined v-else class="file-icon" />
                <a :title="record.fileName" @click="handleDownload(record)">{{ record.fileName }}</a>
              </div>
            </template>
            <template v-else-if="column.dataIndex === 'fileSize'">
              {{ formatSize(record.fileSize) }}
            </template>
            <template v-else-if="column.dataIndex === 'action'">
              <a @click="handleDownload(record)">下载</a>
              <a-divider type="vertical" />
              <a-popconfirm
                title="确定删除该附件吗？"
                ok-text="删除"
                ok-type="danger"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a class="danger-link">删除</a>
              </a-popconfirm>
            </template>
          </template>
        </a-table>
      </div>
    </a-spin>
  </BasicDrawer>
</template>

<script lang="ts" name="device-detail-drawer" setup>
  import { ref, computed } from 'vue';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { Icon } from '/@/components/Icon';
  import { FileOutlined } from '@ant-design/icons-vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { createImgPreview } from '/@/components/Preview/index';
  import { getFileAccessHttpUrl } from '/@/utils/common/compUtils';
  import { ATTACHMENT_TYPE_MAP, ATTACHMENT_TYPE_OPTIONS } from '../deviceInfo.data';
  import {
    getDeviceById,
    getAttachmentList,
    uploadAttachment,
    deleteAttachment,
  } from '/@/api/device/device';

  const { createMessage } = useMessage();

  const deviceId = ref('');
  const loading = ref(false);
  const uploading = ref(false);
  const device = ref<Recordable>({});
  const attachments = ref<Recordable[]>([]);
  // 列表筛选类型
  const attachmentType = ref<'all' | string>('all');
  // 新上传文件归属的附件类型
  const uploadType = ref<string>('photo');
  const IMAGE_EXT_REG = /\.(jpe?g|png|gif|bmp|webp|svg)$/i;

  const attachmentColumns = [
    { title: '类型', dataIndex: 'attachmentType', width: 100 },
    { title: '文件名', dataIndex: 'fileName' },
    { title: '大小', dataIndex: 'fileSize', width: 100 },
    { title: '上传人', dataIndex: 'createBy', width: 100 },
    { title: '上传时间', dataIndex: 'createTime', width: 160 },
    { title: '操作', dataIndex: 'action', width: 110 },
  ];

  const filteredAttachments = computed(() => {
    if (!attachmentType.value || attachmentType.value === 'all') {
      return attachments.value;
    }
    return attachments.value.filter((item) => item.attachmentType === attachmentType.value);
  });

  const [registerDrawer] = useDrawerInner(async (data) => {
    deviceId.value = data?.record?.id || data?.id || '';
    device.value = {};
    attachments.value = [];
    attachmentType.value = 'all';
    uploadType.value = 'photo';
    await loadDetail();
  });

  async function loadDetail() {
    if (!deviceId.value) return;
    loading.value = true;
    try {
      const [detail, list] = await Promise.all([
        getDeviceById({ id: deviceId.value }),
        getAttachmentList(deviceId.value),
      ]);
      device.value = detail || {};
      attachments.value = list || [];
    } finally {
      loading.value = false;
    }
  }

  function getTypeMeta(type: string) {
    return ATTACHMENT_TYPE_MAP[type] || ATTACHMENT_TYPE_MAP.other;
  }

  function isImage(record) {
    return IMAGE_EXT_REG.test(record.fileName || '');
  }

  function getFileUrl(filePath: string) {
    return getFileAccessHttpUrl(filePath);
  }

  function formatSize(size?: number) {
    if (size === null || size === undefined) return '—';
    if (size < 1024) return `${size} B`;
    if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
    return `${(size / 1024 / 1024).toFixed(2)} MB`;
  }

  function previewImage(record) {
    const imageList = filteredAttachments.value.filter(isImage).map((item) => getFileUrl(item.filePath));
    const idx = imageList.indexOf(getFileUrl(record.filePath));
    createImgPreview({
      imageList,
      index: idx < 0 ? 0 : idx,
    });
  }

  function handleDownload(record) {
    const url = getFileUrl(record.filePath);
    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    link.rel = 'noopener';
    link.download = record.fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  function handleDelete(record) {
    deleteAttachment(record.id).then(() => {
      createMessage.success('删除成功');
      attachments.value = attachments.value.filter((item) => item.id !== record.id);
    });
  }

  function beforeUpload(file: File) {
    // 与后端 spring 上传限制（10MB）保持一致，提前拦截
    const maxSize = 10 * 1024 * 1024;
    if (file.size > maxSize) {
      createMessage.warning(`文件「${file.name}」超过 10MB，无法上传`);
      return false;
    }
    // 返回 false 阻止 a-upload 默认上传，改走自定义接口
    doUpload(file);
    return false;
  }

  async function doUpload(file: File) {
    if (!deviceId.value) {
      createMessage.warning('请先保存设备后再上传附件');
      return;
    }
    const type = uploadType.value;
    uploading.value = true;
    try {
      const res = await uploadAttachment({ file, deviceId: deviceId.value, attachmentType: type });
      if (res?.success === true) {
        createMessage.success(`附件「${file.name}」上传成功`);
        attachments.value = await getAttachmentList(deviceId.value);
      } else {
        createMessage.error(res?.message || '上传失败');
      }
    } catch (e) {
      createMessage.error(`附件「${file.name}」上传失败`);
    } finally {
      uploading.value = false;
    }
  }
</script>

<style lang="less" scoped>
  .detail-section {
    margin-bottom: 24px;
  }
  .section-title {
    display: flex;
    align-items: center;
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 12px;

    span {
      margin-left: 6px;
    }
  }
  .attachment-section {
    .type-filter {
      margin-left: 12px;
      font-weight: normal;
    }
    .upload-bar {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 4px;
    }
    .upload-label {
      color: rgba(0, 0, 0, 0.65);
      font-size: 13px;
    }
    .upload-tip {
      margin: 8px 0 12px;
      color: rgba(0, 0, 0, 0.45);
      font-size: 12px;
    }
  }
  .file-name-cell {
    display: flex;
    align-items: center;
    min-width: 0;

    a {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  .file-thumb {
    width: 28px;
    height: 28px;
    object-fit: cover;
    border-radius: 4px;
    margin-right: 8px;
    cursor: pointer;
    border: 1px solid #eee;
    flex-shrink: 0;
  }
  .file-icon {
    margin-right: 8px;
    color: #8c8c8c;
    flex-shrink: 0;
  }
  .danger-link {
    color: #ff4d4f;
  }
</style>

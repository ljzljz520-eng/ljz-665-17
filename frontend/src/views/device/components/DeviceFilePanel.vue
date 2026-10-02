<template>
  <div class="device-file-panel">
    <div class="panel-header">
      <a-radio-group v-model:value="activeCategory" button-style="solid" size="small">
        <a-radio-button value="all">全部</a-radio-button>
        <a-radio-button value="photo">设备照片</a-radio-button>
        <a-radio-button value="manual">说明书</a-radio-button>
        <a-radio-button value="contract">维保合同</a-radio-button>
        <a-radio-button value="other">其他</a-radio-button>
      </a-radio-group>
      <a-upload
        :show-upload-list="false"
        :multiple="true"
        :custom-request="handleUpload"
        :accept="acceptOfCategory"
      >
        <a-button type="primary" :loading="uploading" pre-icon="ant-design:upload-outlined">
          上传{{ activeCategory === 'all' ? '附件' : categoryLabel(activeCategory) }}
        </a-button>
      </a-upload>
    </div>

    <a-alert
      class="panel-tip"
      type="info"
      show-icon
      message="支持上传设备照片（jpg/png 等）、说明书（pdf/word）和维保合同（pdf/word/图片），单个文件不超过 50MB"
    />

    <a-table
      size="small"
      row-key="id"
      :columns="fileColumns"
      :data-source="filteredFiles"
      :pagination="false"
      :loading="loading"
      :locale="{ emptyText: '暂无附件' }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'fileName'">
          <a class="file-name" @click="handleDownload(record)">
            <Icon :icon="fileIcon(record)" class="file-icon" />
            <span>{{ record.fileName }}</span>
          </a>
        </template>
        <template v-else-if="column.key === 'category'">
          <a-tag :color="categoryColor(record.category)">{{ categoryLabel(record.category) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'fileSize'">{{ formatSize(record.fileSize) }}</template>
        <template v-else-if="column.key === 'createBy'">
          {{ record.createByName || record.createBy || '-' }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-popconfirm
            title="确定删除该附件吗？删除后不可恢复"
            ok-text="删除"
            ok-type="danger"
            cancel-text="取消"
            @confirm="handleDelete(record)"
          >
            <a-button type="link" danger size="small">删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { Icon } from '/@/components/Icon';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useGlobSetting } from '/@/hooks/setting';
  import { getDeviceFileList, uploadDeviceFile, deleteDeviceFile } from '/@/api/device/device.api';

  const props = defineProps<{ deviceId: string }>();

  const { createMessage } = useMessage();
  const globSetting = useGlobSetting();

  const loading = ref(false);
  const uploading = ref(false);
  const fileList = ref<any[]>([]);
  const activeCategory = ref<string>('all');

  const CATEGORY_LABEL: Recordable<string> = {
    photo: '设备照片',
    manual: '说明书',
    contract: '维保合同',
    other: '其他',
  };

  const fileColumns = [
    { title: '文件名', key: 'fileName', ellipsis: true },
    { title: '分类', key: 'category', width: 100 },
    { title: '大小', key: 'fileSize', width: 100, align: 'right' },
    { title: '上传人', key: 'createBy', width: 130 },
    { title: '上传时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
    { title: '操作', key: 'action', width: 80, align: 'center' },
  ];

  const filteredFiles = computed(() => {
    if (activeCategory.value === 'all') {
      return fileList.value;
    }
    return fileList.value.filter((item) => item.category === activeCategory.value);
  });

  const acceptOfCategory = computed(() => {
    switch (activeCategory.value) {
      case 'photo':
        return 'image/*';
      default:
        return undefined;
    }
  });

  function categoryLabel(category: string) {
    return CATEGORY_LABEL[category] || CATEGORY_LABEL.other;
  }

  function categoryColor(category: string) {
    switch (category) {
      case 'photo':
        return 'blue';
      case 'manual':
        return 'green';
      case 'contract':
        return 'orange';
      default:
        return 'default';
    }
  }

  function fileIcon(record: any) {
    const type = (record.fileType || '').toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg'].includes(type)) {
      return 'ant-design:file-image-outlined';
    }
    if (['pdf'].includes(type)) {
      return 'ant-design:file-pdf-outlined';
    }
    if (['doc', 'docx'].includes(type)) {
      return 'ant-design:file-word-outlined';
    }
    if (['xls', 'xlsx'].includes(type)) {
      return 'ant-design:file-excel-outlined';
    }
    return 'ant-design:paper-clip-outlined';
  }

  function formatSize(size?: number) {
    if (!size && size !== 0) {
      return '-';
    }
    if (size < 1024) {
      return `${size} B`;
    }
    if (size < 1024 * 1024) {
      return `${(size / 1024).toFixed(1)} KB`;
    }
    return `${(size / 1024 / 1024).toFixed(2)} MB`;
  }

  async function loadFiles() {
    if (!props.deviceId) {
      return;
    }
    loading.value = true;
    try {
      fileList.value = await getDeviceFileList(props.deviceId);
    } finally {
      loading.value = false;
    }
  }

  /**
   * 自定义上传：把文件和设备ID、附件分类一起提交
   */
  function handleUpload(uploadOption: any) {
    const { file, onSuccess, onError } = uploadOption;
    const category = activeCategory.value === 'all' ? 'other' : activeCategory.value;
    uploading.value = true;
    uploadDeviceFile(
      {
        file,
        filename: file.name,
        data: {
          deviceId: props.deviceId,
          category,
        },
      },
      { isReturnResponse: true }
    )
      .then((res: any) => {
        if (res?.success) {
          createMessage.success('上传成功');
          onSuccess?.(res);
          loadFiles();
        } else {
          createMessage.error(res?.message || '上传失败');
          onError?.(new Error(res?.message || '上传失败'));
        }
      })
      .catch((e) => {
        createMessage.error('上传失败');
        onError?.(e);
      })
      .finally(() => {
        uploading.value = false;
      });
  }

  async function handleDelete(record: any) {
    await deleteDeviceFile(record.id, props.deviceId);
    createMessage.success('删除成功');
    loadFiles();
  }

  function handleDownload(record: any) {
    const filePath = record.filePath || '';
    if (/^https?:\/\//i.test(filePath)) {
      window.open(filePath, '_blank');
      return;
    }
    // 本地存储走平台统一的静态文件下载接口（匿名可访问，强制下载）
    const base = globSetting.domainUrl || globSetting.apiUrl || '';
    window.open(`${base}/sys/common/static/${filePath}`, '_blank');
  }

  defineExpose({ refresh: loadFiles });

  loadFiles();
</script>

<style lang="less" scoped>
  .device-file-panel {
    .panel-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 10px;
      gap: 12px;
      flex-wrap: wrap;
    }

    .panel-tip {
      margin-bottom: 10px;
    }

    .file-name {
      display: inline-flex;
      align-items: center;
      max-width: 100%;

      .file-icon {
        margin-right: 6px;
        flex-shrink: 0;
      }
    }
  }
</style>

<template>
  <div class="device-attachment-panel">
    <div class="attachment-toolbar">
      <a-upload
        :file-list="[]"
        :multiple="true"
        :show-upload-list="false"
        :accept="acceptTypes"
        :before-upload="beforeUpload"
        :custom-request="handleCustomRequest"
      >
        <a-button type="primary" pre-icon="ant-design:upload-outlined" :loading="uploading">上传附件</a-button>
      </a-upload>
      <a-select
        v-model:value="attachmentType"
        style="width: 140px; margin-left: 8px"
        :options="ATTACHMENT_TYPE_OPTIONS"
      />
      <span class="attachment-tip">支持 {{ acceptText }}，单个文件不超过 {{ maxSize }}MB</span>
    </div>

    <a-table
      class="attachment-table"
      size="small"
      :columns="attachmentColumns"
      :data-source="attachmentList"
      :pagination="false"
      :loading="loading"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'attachmentType'">
          <a-tag :color="typeColorMap[record.attachmentType] || 'default'">
            {{ ATTACHMENT_TYPE_MAP[record.attachmentType] || record.attachmentType || '其他' }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'fileName'">
          <a @click="handleDownload(record)" :title="record.fileName">
            <Icon icon="ant-design:paper-clip-outlined" class="file-icon" />
            {{ record.fileName }}
          </a>
        </template>
        <template v-else-if="column.dataIndex === 'fileSize'">
          {{ formatFileSize(record.fileSize) }}
        </template>
        <template v-else-if="column.dataIndex === 'action'">
          <a @click="handleDownload(record)">下载</a>
          <a-divider type="vertical" />
          <a-popconfirm
            title="确认删除该附件？"
            ok-text="确认删除"
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
</template>

<script lang="ts" name="device-attachment-panel" setup>
  import { ref, watch } from 'vue';
  import { message } from 'ant-design-vue';
  import { useGlobSetting } from '/@/hooks/setting';
  import { ATTACHMENT_TYPE_MAP, ATTACHMENT_TYPE_OPTIONS } from '../archive.data';
  import { getAttachmentList, saveAttachment, deleteAttachment } from '../archive.api';
  import { uploadDeviceFile } from '../archive.api';

  const props = defineProps({
    // 设备档案ID，为空表示新增设备尚未保存
    deviceId: {
      type: String,
      default: '',
    },
  });
  const emit = defineEmits(['count-change']);
  const globSetting = useGlobSetting();

  const loading = ref(false);
  const uploading = ref(false);
  const attachmentList = ref<any[]>([]);
  const attachmentType = ref('photo');

  // 允许上传的附件类型：照片、文档、PDF、压缩包等
  const acceptTypes = [
    '.jpg', '.jpeg', '.png', '.gif', '.bmp', '.ico', '.heic',
    '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx',
    '.txt', '.zip', '.rar', '.7z',
  ];
  const acceptText = '图片 / PDF / Office文档 / 压缩包';
  const maxSize = 50;

  const typeColorMap: Recordable = {
    photo: 'blue',
    manual: 'green',
    contract: 'orange',
    other: 'default',
  };

  const attachmentColumns = [
    { title: '类型', dataIndex: 'attachmentType', width: 100, align: 'center' },
    { title: '文件名', dataIndex: 'fileName', ellipsis: true },
    { title: '大小', dataIndex: 'fileSize', width: 100, align: 'center' },
    { title: '上传人', dataIndex: 'uploadByName', width: 120, align: 'center' },
    { title: '上传时间', dataIndex: 'createTime', width: 170, align: 'center' },
    { title: '操作', dataIndex: 'action', width: 120, align: 'center' },
  ];

  watch(
    () => props.deviceId,
    (val) => {
      if (val) {
        loadAttachments(val);
      } else {
        attachmentList.value = [];
        emitCount();
      }
    },
    { immediate: true }
  );

  async function loadAttachments(deviceId: string) {
    loading.value = true;
    try {
      attachmentList.value = await getAttachmentList(deviceId);
      emitCount();
    } finally {
      loading.value = false;
    }
  }

  function emitCount() {
    emit('count-change', attachmentList.value.length);
  }

  /** 上传前校验：类型与大小 */
  function beforeUpload(file: File) {
    if (!props.deviceId) {
      message.warning('请先保存设备档案后再上传附件');
      return false;
    }
    const lowerName = file.name.toLowerCase();
    const extOk = acceptTypes.some((ext) => lowerName.endsWith(ext));
    if (!extOk) {
      message.error(`不支持的文件类型：${file.name}`);
      return false;
    }
    if (file.size / 1024 / 1024 > maxSize) {
      message.error(`文件 ${file.name} 超过 ${maxSize}MB 限制`);
      return false;
    }
    return true;
  }

  /** a-upload 自定义上传：先传文件到通用上传接口，再保存附件关系记录 */
  async function handleCustomRequest(options: Recordable) {
    const { file, onSuccess, onError } = options;
    uploading.value = true;
    try {
      const res: any = await uploadDeviceFile(
        {
          file,
          name: 'file',
          filename: file.name,
          data: { biz: 'device/attachment' },
        },
        () => {}
      );
      // 上传约定：message 为文件存储相对路径
      const filePath = res?.message;
      if (!res?.success || !filePath) {
        throw new Error(res?.message || '文件上传失败');
      }
      await saveAttachment({
        deviceId: props.deviceId,
        attachmentType: attachmentType.value,
        fileName: file.name,
        filePath,
        fileSize: file.size || 0,
      });
      message.success(`附件 ${file.name} 上传成功`);
      onSuccess?.(res, file);
      await loadAttachments(props.deviceId);
    } catch (e: any) {
      message.error(e?.message || '附件保存失败');
      onError?.(e);
    } finally {
      uploading.value = false;
    }
  }

  /** 删除附件（二次确认由 Popconfirm 承担） */
  async function handleDelete(record: Recordable) {
    await deleteAttachment({ id: record.id });
    message.success('附件已删除');
    await loadAttachments(props.deviceId);
  }

  function handleDownload(record: Recordable) {
    if (!record.filePath) {
      message.warning('文件地址缺失，无法下载');
      return;
    }
    // 走同源 API 前缀（dev 代理 / docker nginx 均指向后端），避免容器内地址不可达
    const url = `${globSetting.apiUrl}/sys/common/static/${record.filePath}`;
    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    link.rel = 'noopener';
    link.setAttribute('download', record.fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  function formatFileSize(size?: number) {
    if (!size && size !== 0) return '-';
    if (size < 1024) return `${size} B`;
    if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
    if (size < 1024 * 1024 * 1024) return `${(size / 1024 / 1024).toFixed(1)} MB`;
    return `${(size / 1024 / 1024 / 1024).toFixed(2)} GB`;
  }

  // 暴露刷新方法给父组件
  defineExpose({
    refresh: () => props.deviceId && loadAttachments(props.deviceId),
  });

</script>

<style lang="less" scoped>
  .device-attachment-panel {
    .attachment-toolbar {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      margin-bottom: 12px;
    }

    .attachment-tip {
      margin-left: 12px;
      color: rgba(0, 0, 0, 0.45);
      font-size: 12px;
    }

    .file-icon {
      margin-right: 4px;
    }

    .danger-link {
      color: #ff4d4f;
    }

    .attachment-table {
      :deep(.ant-table-cell) {
        vertical-align: middle;
      }
    }
  }
</style>

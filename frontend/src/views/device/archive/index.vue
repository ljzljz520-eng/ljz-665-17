<template>
  <div class="p-2">
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" pre-icon="ant-design:plus-outlined" @click="handleAdd()"> 新增</a-button>
        <a-button v-if="selectedRowKeys.length > 0" pre-icon="ant-design:delete-outlined" @click="batchHandleDelete">
          批量删除
        </a-button>
      </template>

      <template #status="{ record }">
        <a-badge :status="record.status === 1 ? 'success' : 'default'" :text="record.status === 1 ? '正常' : '停用'" />
      </template>

      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              icon: 'ant-design:eye-outlined',
              tooltip: '详情',
              label: '详情',
              onClick: handleDetail.bind(null, record),
            },
          ]"
          :dropDownActions="[
            {
              icon: 'ant-design:edit-outlined',
              tooltip: '编辑',
              label: '编辑',
              onClick: handleEdit.bind(null, record),
            },
            {
              icon: 'ant-design:delete-outlined',
              color: 'error',
              tooltip: '删除',
              label: '删除',
              popConfirm: {
                title: '确认删除该设备吗？',
                content: '删除后该设备下的全部附件将一并删除，且不可恢复',
                okText: '确认删除',
                cancelText: '取消',
                placement: 'topRight',
                confirm: handleDelete.bind(null, record),
              },
            },
          ]"
        />
      </template>
    </BasicTable>

    <DeviceArchiveModal @register="registerModal" @success="reload" />
    <DeviceDetailDrawer ref="detailDrawerRef" />
  </div>
</template>

<script lang="ts" name="device-archive" setup>
  import { ref } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { columns, searchFormSchema } from './archive.data';
  import { getDeviceList, deleteDevice, batchDeleteDevice } from './archive.api';
  import DeviceArchiveModal from './components/DeviceArchiveModal.vue';
  import DeviceDetailDrawer from './components/DeviceDetailDrawer.vue';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createConfirm, createMessage } = useMessage();
  const detailDrawerRef = ref();

  const [registerModal, { openModal }] = useModal();

  const { tableContext } = useListPage({
    tableProps: {
      title: '设备档案',
      api: getDeviceList,
      columns,
      formConfig: {
        schemas: searchFormSchema,
      },
      actionColumn: {
        width: 180,
      },
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  function handleAdd() {
    openModal(true, { isUpdate: false, record: {} });
  }

  function handleEdit(record: Recordable) {
    openModal(true, { isUpdate: true, record });
  }

  function handleDetail(record: Recordable) {
    detailDrawerRef.value?.showDrawer(record);
  }

  async function handleDelete(record: Recordable) {
    await deleteDevice({ id: record.id });
    createMessage.success('删除成功');
    reload();
  }

  function batchHandleDelete() {
    createConfirm({
      iconType: 'warning',
      title: '确认批量删除',
      content: `确认删除选中的 ${selectedRowKeys.value.length} 台设备吗？删除后设备下的全部附件将一并删除，且不可恢复`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        await batchDeleteDevice({ ids: selectedRowKeys.value.join(',') });
        createMessage.success('批量删除成功');
        reload();
      },
    });
  }
</script>

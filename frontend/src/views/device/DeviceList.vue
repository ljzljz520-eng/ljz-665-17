<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" pre-icon="ant-design:plus-outlined" @click="handleAdd">新增</a-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>批量操作<Icon icon="ant-design:down-outlined" /></a-button>
        </a-dropdown>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getActions(record)" />
      </template>
    </BasicTable>

    <DeviceFormModal @register="registerFormModal" @success="reload" />
    <DeviceDetailDrawer @register="registerDetailDrawer" />
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useDrawer } from '/@/components/Drawer';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { columns, searchFormSchema } from './device.data';
  import { getDeviceList, deleteDevice, batchDeleteDevice } from '/@/api/device/device.api';
  import DeviceFormModal from './components/DeviceFormModal.vue';
  import DeviceDetailDrawer from './components/DeviceDetailDrawer.vue';
  import { Modal } from 'ant-design-vue';

  const [registerFormModal, { openModal }] = useModal();
  const [registerDetailDrawer, { openDrawer }] = useDrawer();

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
      showIndexColumn: true,
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  function getActions(record) {
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '删除',
        popConfirm: {
          title: '删除设备将同时删除其全部附件记录，确认删除？',
          okText: '删除',
          okType: 'danger',
          cancelText: '取消',
          confirm: handleDelete.bind(null, record),
        },
      },
    ];
  }

  function handleAdd() {
    openModal(true, { isUpdate: false });
  }

  function handleEdit(record) {
    openModal(true, { record, isUpdate: true });
  }

  function handleDetail(record) {
    openDrawer(true, { record });
  }

  async function handleDelete(record) {
    await deleteDevice(record.id);
    reload();
  }

  function batchHandleDelete() {
    Modal.confirm({
      title: '批量删除设备',
      content: '删除后这些设备的附件记录将一并清除且不可恢复，确认删除？',
      okText: '删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        await batchDeleteDevice(selectedRowKeys.value.join(','));
        selectedRowKeys.value = [];
        reload();
      },
    });
  }
</script>

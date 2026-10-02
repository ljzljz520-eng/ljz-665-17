<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd">新增</a-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>
            批量操作
            <Icon icon="ant-design:down-outlined"></Icon>
          </a-button>
        </a-dropdown>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getActions(record)" />
      </template>
    </BasicTable>
    <DeviceModal @register="registerModal" @success="reload" />
    <DeviceDetailDrawer @register="registerDrawer" />
  </div>
</template>
<script lang="ts" name="device-deviceInfo" setup>
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useDrawer } from '/@/components/Drawer';
  import { Icon } from '/@/components/Icon';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getDeviceList, deleteDevice, batchDeleteDevice } from '/@/api/device/device';
  import { columns, searchFormSchema } from './deviceInfo.data';
  import DeviceModal from './components/DeviceModal.vue';
  import DeviceDetailDrawer from './components/DeviceDetailDrawer.vue';
  import { useListPage } from '/@/hooks/system/useListPage';

  const { createConfirm, createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();
  const [registerDrawer, { openDrawer }] = useDrawer();

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
          title: '确定删除该设备吗？其下附件将一并删除',
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
    await deleteDevice({ id: record.id });
    createMessage.success('删除成功');
    reload();
  }

  function batchHandleDelete() {
    createConfirm({
      iconType: 'warning',
      title: '确定删除选中设备吗？',
      content: '所选设备及其全部附件记录将被一并删除，且不可恢复。',
      okText: '删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        await batchDeleteDevice({ ids: selectedRowKeys.value });
        selectedRowKeys.value = [];
        createMessage.success('批量删除成功');
        reload();
      },
    });
  }

</script>

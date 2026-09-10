<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" :width="720" @ok="handleSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" name="device-archive-modal" setup>
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from '../archive.data';
  import { saveOrUpdateDevice } from '../archive.api';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true);

  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 12 },
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    if (unref(isUpdate)) {
      // 编辑时只需档案字段（详情接口返回的附件列表不参与表单提交）
      const { attachmentList, ...fields } = data.record || {};
      await setFieldsValue(fields);
    }
  });

  const getTitle = computed(() => (!unref(isUpdate) ? '新增设备档案' : '编辑设备档案'));

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      await saveOrUpdateDevice(values, isUpdate.value);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

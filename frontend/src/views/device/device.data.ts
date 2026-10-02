import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '设备编号',
    dataIndex: 'deviceCode',
    width: 160,
  },
  {
    title: '设备名称',
    dataIndex: 'deviceName',
    width: 180,
  },
  {
    title: '设备型号',
    dataIndex: 'model',
    width: 140,
  },
  {
    title: '生产厂商',
    dataIndex: 'manufacturer',
    width: 180,
  },
  {
    title: '安装位置',
    dataIndex: 'location',
    width: 160,
  },
  {
    title: '启用日期',
    dataIndex: 'useDate',
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 90,
    customRender: ({ text }) => {
      return text === '1' ? '正常' : text === '0' ? '停用' : text || '';
    },
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'deviceCode',
    label: '设备编号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'deviceName',
    label: '设备名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'status',
    label: '设备状态',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'device_status',
      placeholder: '请选择状态',
    },
    colProps: { span: 6 },
  },
];

export const formSchema: FormSchema[] = [
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '设备编号',
    field: 'deviceCode',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入设备编号',
      maxlength: 50,
    },
  },
  {
    label: '设备名称',
    field: 'deviceName',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入设备名称',
      maxlength: 100,
    },
  },
  {
    label: '设备型号',
    field: 'model',
    component: 'Input',
    componentProps: {
      maxlength: 100,
    },
  },
  {
    label: '生产厂商',
    field: 'manufacturer',
    component: 'Input',
    componentProps: {
      maxlength: 100,
    },
  },
  {
    label: '安装位置',
    field: 'location',
    component: 'Input',
    componentProps: {
      maxlength: 200,
    },
  },
  {
    label: '启用日期',
    field: 'useDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
      style: { width: '100%' },
    },
  },
  {
    label: '设备状态',
    field: 'status',
    component: 'JDictSelectTag',
    defaultValue: '1',
    componentProps: {
      dictCode: 'device_status',
      placeholder: '请选择状态',
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: {
      rows: 3,
      maxlength: 500,
    },
  },
];

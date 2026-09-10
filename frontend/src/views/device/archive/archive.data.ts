import { BasicColumn, FormSchema } from '/@/components/Table';

// 附件类型字典
export const ATTACHMENT_TYPE_MAP: Recordable = {
  photo: '设备照片',
  manual: '说明书',
  contract: '维保合同',
  other: '其他',
};

export const ATTACHMENT_TYPE_OPTIONS = [
  { label: '设备照片', value: 'photo' },
  { label: '说明书', value: 'manual' },
  { label: '维保合同', value: 'contract' },
  { label: '其他', value: 'other' },
];

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
    dataIndex: 'deviceModel',
    width: 140,
  },
  {
    title: '生产厂家',
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
    width: 80,
    slots: { customRender: 'status' },
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
    field: 'manufacturer',
    label: '生产厂家',
    component: 'Input',
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
    field: 'deviceCode',
    label: '设备编号',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入设备编号',
      maxlength: 64,
    },
  },
  {
    field: 'deviceName',
    label: '设备名称',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入设备名称',
      maxlength: 128,
    },
  },
  {
    field: 'deviceModel',
    label: '设备型号',
    component: 'Input',
    componentProps: { maxlength: 128 },
  },
  {
    field: 'manufacturer',
    label: '生产厂家',
    component: 'Input',
    componentProps: { maxlength: 128 },
  },
  {
    field: 'location',
    label: '安装位置',
    component: 'Input',
    componentProps: { maxlength: 255 },
  },
  {
    field: 'useDate',
    label: '启用日期',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
      style: { width: '100%' },
    },
  },
  {
    field: 'status',
    label: '状态',
    component: 'RadioGroup',
    defaultValue: 1,
    componentProps: {
      options: [
        { label: '正常', value: 1 },
        { label: '停用', value: 0 },
      ],
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

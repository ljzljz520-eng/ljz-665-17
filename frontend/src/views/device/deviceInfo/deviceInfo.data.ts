import { BasicColumn, FormSchema } from '/@/components/Table';

// 附件类型字典
export const ATTACHMENT_TYPE_MAP: Record<string, { label: string; color: string }> = {
  photo: { label: '设备照片', color: 'blue' },
  manual: { label: '说明书', color: 'green' },
  contract: { label: '维保合同', color: 'orange' },
  other: { label: '其他', color: 'default' },
};

export const ATTACHMENT_TYPE_OPTIONS = Object.keys(ATTACHMENT_TYPE_MAP).map((value) => ({
  value,
  label: ATTACHMENT_TYPE_MAP[value].label,
}));

export const columns: BasicColumn[] = [
  {
    title: '设备编号',
    dataIndex: 'deviceCode',
    width: 140,
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
    width: 160,
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
    title: '创建人',
    dataIndex: 'createBy',
    width: 100,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'deviceName',
    label: '设备名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'deviceCode',
    label: '设备编号',
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
    componentProps: { placeholder: '留空则系统自动生成' },
    colProps: { span: 12 },
  },
  {
    field: 'deviceName',
    label: '设备名称',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
  },
  {
    field: 'deviceModel',
    label: '设备型号',
    component: 'Input',
    colProps: { span: 12 },
  },
  {
    field: 'manufacturer',
    label: '生产厂家',
    component: 'Input',
    colProps: { span: 12 },
  },
  {
    field: 'serialNumber',
    label: '出厂序列号',
    component: 'Input',
    colProps: { span: 12 },
  },
  {
    field: 'location',
    label: '安装位置',
    component: 'Input',
    colProps: { span: 12 },
  },
  {
    field: 'useDate',
    label: '启用日期',
    component: 'DatePicker',
    componentProps: { valueFormat: 'YYYY-MM-DD', style: { width: '100%' } },
    colProps: { span: 12 },
  },
  {
    field: 'warrantyDate',
    label: '质保到期日',
    component: 'DatePicker',
    componentProps: { valueFormat: 'YYYY-MM-DD', style: { width: '100%' } },
    colProps: { span: 12 },
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    colProps: { span: 24 },
  },
];

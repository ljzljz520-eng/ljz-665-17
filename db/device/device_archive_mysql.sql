-- =====================================================================
-- 设备档案 + 设备附件 模块增量脚本（MySQL）
-- 适用：JeecgBoot MySQL 5.7 / 8.x
-- 说明：
--   1. device_attachment.device_id 物理外键关联 device_archive.id，
--      并设置 ON DELETE CASCADE，作为“删除设备不产生孤儿记录”的数据库兜底；
--   2. 应用层删除时也会在同一事务内先删附件记录（并清理物理文件）再删设备；
--   3. 菜单权限同时授权给管理员角色（admin），其他角色请到“角色授权”中分配。
-- =====================================================================

-- ----------------------------
-- 设备档案主表
-- ----------------------------
DROP TABLE IF EXISTS `device_attachment`;
DROP TABLE IF EXISTS `device_archive`;

CREATE TABLE `device_archive` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `device_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编号',
  `device_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备名称',
  `device_model` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备型号',
  `manufacturer` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生产厂家',
  `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安装位置',
  `use_date` date NULL DEFAULT NULL COMMENT '启用日期',
  `status` int(2) NULL DEFAULT 1 COMMENT '设备状态（1正常 0停用）',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_device_code`(`device_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 设备附件表
-- ----------------------------
CREATE TABLE `device_attachment` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `device_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备档案ID',
  `attachment_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'other' COMMENT '附件类型（photo照片 manual说明书 contract维保合同 other其他）',
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名称（原始文件名）',
  `file_path` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件存储相对路径',
  `file_size` bigint(20) NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上传人（账号）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '上传时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_device_attachment_device_id`(`device_id`) USING BTREE,
  CONSTRAINT `fk_device_attachment_device` FOREIGN KEY (`device_id`) REFERENCES `device_archive` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备附件表' ROW_FORMAT = DYNAMIC;

-- =====================================================================
-- 菜单与按钮权限（顶级菜单“设备管理” + 子菜单“设备档案” + 按钮权限）
-- =====================================================================
DELETE FROM `sys_permission` WHERE `id` IN (
  '1900000000000000001',
  '1900000000000000002',
  '1900000000000000003',
  '1900000000000000004',
  '1900000000000000005',
  '1900000000000000006',
  '1900000000000000007',
  '1900000000000000008',
  '1900000000000000009'
);

-- 顶级菜单：设备管理
INSERT INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
VALUES ('1900000000000000001', '', '设备管理', '/device', 'layouts/RouteView', 1, NULL, NULL, 0, NULL, NULL, 5.00, 0, 'ant-design:hdd-outlined', 0, 0, 0, 0, '设备管理目录', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);

-- 子菜单：设备档案
INSERT INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
VALUES ('1900000000000000002', '1900000000000000001', '设备档案', '/device/archive', 'device/archive/index', 1, NULL, NULL, 1, NULL, '1', 1.00, 0, 'ant-design:profile-outlined', 1, 0, 0, 0, '设备档案管理', 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);

-- 按钮权限
INSERT INTO `sys_permission`(`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`)
VALUES
('1900000000000000003', '1900000000000000002', '查询',     '', NULL, 0, NULL, NULL, 2, 'device:archive:list',             '1', 1.00, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0),
('1900000000000000004', '1900000000000000002', '查看详情', '', NULL, 0, NULL, NULL, 2, 'device:archive:query',            '1', 2.00, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0),
('1900000000000000005', '1900000000000000002', '新增',     '', NULL, 0, NULL, NULL, 2, 'device:archive:add',              '1', 3.00, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0),
('1900000000000000006', '1900000000000000002', '编辑',     '', NULL, 0, NULL, NULL, 2, 'device:archive:edit',             '1', 4.00, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0),
('1900000000000000007', '1900000000000000002', '删除',     '', NULL, 0, NULL, NULL, 2, 'device:archive:delete',           '1', 5.00, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0),
('1900000000000000008', '1900000000000000002', '批量删除', '', NULL, 0, NULL, NULL, 2, 'device:archive:deleteBatch',      '1', 6.00, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0),
('1900000000000000009', '1900000000000000002', '上传附件', '', NULL, 0, NULL, NULL, 2, 'device:archive:upload',           '1', 7.00, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0),
('1900000000000000010', '1900000000000000002', '删除附件', '', NULL, 0, NULL, NULL, 2, 'device:archive:deleteAttachment', '1', 8.00, 0, NULL, 1, 0, 0, 0, NULL, 'admin', NOW(), NULL, NULL, 0, 0, '1', 0);

-- 授权给“管理员(admin)”角色：f6817f48af4fb3af11b9e8bf182f618b
DELETE FROM `sys_role_permission` WHERE `permission_id` IN (
  '1900000000000000001','1900000000000000002','1900000000000000003','1900000000000000004',
  '1900000000000000005','1900000000000000006','1900000000000000007','1900000000000000008',
  '1900000000000000009','1900000000000000010'
);
INSERT INTO `sys_role_permission`(`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
VALUES
(MD5('rp-device-01'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000001', NULL, NOW(), NULL),
(MD5('rp-device-02'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000002', NULL, NOW(), NULL),
(MD5('rp-device-03'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000003', NULL, NOW(), NULL),
(MD5('rp-device-04'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000004', NULL, NOW(), NULL),
(MD5('rp-device-05'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000005', NULL, NOW(), NULL),
(MD5('rp-device-06'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000006', NULL, NOW(), NULL),
(MD5('rp-device-07'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000007', NULL, NOW(), NULL),
(MD5('rp-device-08'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000008', NULL, NOW(), NULL),
(MD5('rp-device-09'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000009', NULL, NOW(), NULL),
(MD5('rp-device-10'), 'f6817f48af4fb3af11b9e8bf182f618b', '1900000000000000010', NULL, NOW(), NULL);

-- =====================================================================
-- 设备档案 + 设备附件 模块增量脚本（SQL Server 2019+）
-- 执行库：与 JeecgBoot 业务库相同（docker-compose 默认 temp1218）
-- 说明：
--   1. device_attachment.device_id 外键关联 device_archive.id，
--      ON DELETE CASCADE 作为删除设备时不产生孤儿记录的数据库兜底；
--   2. 应用层删除时也会在同一事务内先删附件（并清理物理文件）再删设备。
-- =====================================================================
SET NOCOUNT ON;
GO

IF OBJECT_ID(N'[device_attachment]', N'U') IS NOT NULL DROP TABLE [device_attachment];
IF OBJECT_ID(N'[device_archive]', N'U') IS NOT NULL DROP TABLE [device_archive];
GO

-- ----------------------------
-- 设备档案主表
-- ----------------------------
CREATE TABLE [device_archive] (
  [id]            nvarchar(32)  NOT NULL,
  [device_code]   nvarchar(64)  NOT NULL,
  [device_name]   nvarchar(128) NOT NULL,
  [device_model]  nvarchar(128) NULL,
  [manufacturer]  nvarchar(128) NULL,
  [location]      nvarchar(255) NULL,
  [use_date]      date          NULL,
  [status]        int           NULL DEFAULT ((1)),
  [remark]        nvarchar(500) NULL,
  [create_by]     nvarchar(50)  NULL,
  [create_time]   datetime2     NULL,
  [update_by]     nvarchar(50)  NULL,
  [update_time]   datetime2     NULL,
  CONSTRAINT [pk_device_archive] PRIMARY KEY CLUSTERED ([id])
);
GO
CREATE UNIQUE INDEX [uk_device_code] ON [device_archive] ([device_code]);
GO

-- ----------------------------
-- 设备附件表
-- ----------------------------
CREATE TABLE [device_attachment] (
  [id]              nvarchar(32)  NOT NULL,
  [device_id]       nvarchar(32)  NOT NULL,
  [attachment_type] nvarchar(20)  NOT NULL DEFAULT (N'other'),
  [file_name]       nvarchar(255) NOT NULL,
  [file_path]       nvarchar(500) NOT NULL,
  [file_size]       bigint        NULL DEFAULT ((0)),
  [create_by]       nvarchar(50)  NULL,
  [create_time]     datetime2     NULL,
  [update_by]       nvarchar(50)  NULL,
  [update_time]     datetime2     NULL,
  CONSTRAINT [pk_device_attachment] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [fk_device_attachment_device] FOREIGN KEY ([device_id])
      REFERENCES [device_archive] ([id]) ON DELETE CASCADE
);
GO
CREATE INDEX [idx_device_attachment_device_id] ON [device_attachment] ([device_id]);
GO

-- =====================================================================
-- 菜单与按钮权限
-- =====================================================================
DELETE FROM [sys_role_permission] WHERE [permission_id] IN (
  N'1900000000000000001',N'1900000000000000002',N'1900000000000000003',
  N'1900000000000000004',N'1900000000000000005',N'1900000000000000006',
  N'1900000000000000007',N'1900000000000000008',N'1900000000000000009',
  N'1900000000000000010'
);
DELETE FROM [sys_permission] WHERE [id] IN (
  N'1900000000000000001',N'1900000000000000002',N'1900000000000000003',
  N'1900000000000000004',N'1900000000000000005',N'1900000000000000006',
  N'1900000000000000007',N'1900000000000000008',N'1900000000000000009',
  N'1900000000000000010'
);
GO

INSERT INTO [sys_permission]([id],[parent_id],[name],[url],[component],[is_route],[component_name],[redirect],[menu_type],[perms],[perms_type],[sort_no],[always_show],[icon],[is_leaf],[keep_alive],[hidden],[hide_tab],[description],[create_by],[create_time],[update_by],[update_time],[del_flag],[rule_flag],[status],[internal_or_external])
VALUES
(N'1900000000000000001', N'',         N'设备管理', N'/device',         N'layouts/RouteView', 1, NULL, NULL, 0, NULL,                               NULL, 5.00, 0, N'ant-design:hdd-outlined',      0, 0, 0, 0, N'设备管理目录', N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0),
(N'1900000000000000002', N'1900000000000000001', N'设备档案', N'/device/archive', N'device/archive/index', 1, NULL, NULL, 1, NULL,                               N'1', 1.00, 0, N'ant-design:profile-outlined', 1, 0, 0, 0, N'设备档案管理', N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0),
(N'1900000000000000003', N'1900000000000000002', N'查询',     N'', NULL, 0, NULL, NULL, 2, N'device:archive:list',             N'1', 1.00, 0, NULL, 1, 0, 0, 0, NULL, N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0),
(N'1900000000000000004', N'1900000000000000002', N'查看详情', N'', NULL, 0, NULL, NULL, 2, N'device:archive:query',            N'1', 2.00, 0, NULL, 1, 0, 0, 0, NULL, N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0),
(N'1900000000000000005', N'1900000000000000002', N'新增',     N'', NULL, 0, NULL, NULL, 2, N'device:archive:add',              N'1', 3.00, 0, NULL, 1, 0, 0, 0, NULL, N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0),
(N'1900000000000000006', N'1900000000000000002', N'编辑',     N'', NULL, 0, NULL, NULL, 2, N'device:archive:edit',             N'1', 4.00, 0, NULL, 1, 0, 0, 0, NULL, N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0),
(N'1900000000000000007', N'1900000000000000002', N'删除',     N'', NULL, 0, NULL, NULL, 2, N'device:archive:delete',           N'1', 5.00, 0, NULL, 1, 0, 0, 0, NULL, N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0),
(N'1900000000000000008', N'1900000000000000002', N'批量删除', N'', NULL, 0, NULL, NULL, 2, N'device:archive:deleteBatch',      N'1', 6.00, 0, NULL, 1, 0, 0, 0, NULL, N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0),
(N'1900000000000000009', N'1900000000000000002', N'上传附件', N'', NULL, 0, NULL, NULL, 2, N'device:archive:upload',           N'1', 7.00, 0, NULL, 1, 0, 0, 0, NULL, N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0),
(N'1900000000000000010', N'1900000000000000002', N'删除附件', N'', NULL, 0, NULL, NULL, 2, N'device:archive:deleteAttachment', N'1', 8.00, 0, NULL, 1, 0, 0, 0, NULL, N'admin', GETDATE(), NULL, NULL, 0, 0, N'1', 0);
GO

-- 授权给“管理员(admin)”角色：f6817f48af4fb3af11b9e8bf182f618b
INSERT INTO [sys_role_permission]([id],[role_id],[permission_id],[data_rule_ids],[operate_date],[operate_ip])
VALUES
(N'1901000000000000001', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000001', NULL, GETDATE(), NULL),
(N'1901000000000000002', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000002', NULL, GETDATE(), NULL),
(N'1901000000000000003', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000003', NULL, GETDATE(), NULL),
(N'1901000000000000004', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000004', NULL, GETDATE(), NULL),
(N'1901000000000000005', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000005', NULL, GETDATE(), NULL),
(N'1901000000000000006', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000006', NULL, GETDATE(), NULL),
(N'1901000000000000007', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000007', NULL, GETDATE(), NULL),
(N'1901000000000000008', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000008', NULL, GETDATE(), NULL),
(N'1901000000000000009', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000009', NULL, GETDATE(), NULL),
(N'1901000000000000010', N'f6817f48af4fb3af11b9e8bf182f618b', N'1900000000000000010', NULL, GETDATE(), NULL);
GO

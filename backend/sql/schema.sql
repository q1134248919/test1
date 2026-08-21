CREATE DATABASE IF NOT EXISTS family_assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE family_assistant;

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       NOT NULL PRIMARY KEY,
    username    VARCHAR(32)  NOT NULL COMMENT '登录名',
    password    VARCHAR(128) NOT NULL COMMENT 'BCrypt 密码',
    nickname    VARCHAR(32)  NOT NULL COMMENT '昵称',
    avatar      VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像',
    phone       VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '手机号',
    family_id   BIGINT       NULL COMMENT '家庭 ID',
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

CREATE TABLE IF NOT EXISTS family_group (
    id          BIGINT       NOT NULL PRIMARY KEY,
    name        VARCHAR(32)  NOT NULL COMMENT '家庭名称',
    invite_code VARCHAR(16)  NOT NULL COMMENT '邀请码',
    owner_id    BIGINT       NOT NULL COMMENT '创建人',
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_invite_code (invite_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭群';

ALTER TABLE family_group MODIFY COLUMN invite_code VARCHAR(16) NOT NULL COMMENT '邀请码';

CREATE TABLE IF NOT EXISTS family_todo (
    id          BIGINT       NOT NULL PRIMARY KEY,
    family_id   BIGINT       NOT NULL COMMENT '家庭 ID',
    title       VARCHAR(100) NOT NULL COMMENT '待办内容',
    done        TINYINT      NOT NULL DEFAULT 0 COMMENT '0未完成 1已完成',
    creator_id  BIGINT       NOT NULL COMMENT '创建人',
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_family_id (family_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭待办';

CREATE TABLE IF NOT EXISTS family_memory (
    id          BIGINT       NOT NULL PRIMARY KEY,
    family_id   BIGINT       NOT NULL COMMENT '家庭 ID',
    file_path   VARCHAR(255) NOT NULL COMMENT '相对路径',
    caption     VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '备注',
    uploader_id BIGINT       NOT NULL COMMENT '上传人',
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_memory_family (family_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家庭记忆';

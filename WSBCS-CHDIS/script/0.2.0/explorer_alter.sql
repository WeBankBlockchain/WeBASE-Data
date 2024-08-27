alter table tb_deposit_info add column cancel_status tinyint(1) DEFAULT 0 COMMENT '取消状态，0-未取消，1-已取消' after deposit_tx_hash;
alter table tb_deposit_info add column card_type int(2) DEFAULT 1 COMMENT '类型：1-实体卡；2-虚拟卡' after description;
alter table tb_deposit_info add index idx_cs (cancel_status);
alter table tb_withdraw_info add column card_type int(2) DEFAULT 1 COMMENT '类型：1-实体卡；2-虚拟卡' after description;
alter table tb_withdraw_info add index idx_cs (cancel_status);
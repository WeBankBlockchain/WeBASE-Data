CREATE TABLE IF NOT EXISTS tb_stat_bac20_transfer (
  check_date VARCHAR(25) COLLATE utf8mb4_bin NOT NULL COMMENT '日期，如2023-01-01',
  check_hour INT COLLATE utf8mb4_bin NOT NULL COMMENT '小时',
  transfer_amount BIGINT(20) COLLATE utf8mb4_bin NOT NULL COMMENT '转移量',
  block_number BIGINT(25) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '最新统计的块高',
  block_timestamp BIGINT(20) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '最新统计的区块时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '修改时间',
  PRIMARY KEY (check_date, check_hour),
  KEY idx_block_number (block_number),
  KEY idx_block_timestamp (block_timestamp)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='元豆转移数据量统计表';
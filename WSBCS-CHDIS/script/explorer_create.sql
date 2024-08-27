CREATE TABLE IF NOT EXISTS shedlock (
  name varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '任务名称',
  lock_until timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '锁定截止时间',
  locked_at timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '锁定时间',
  locked_by varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '锁定者',
  PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='定时任务同步锁';

CREATE TABLE IF NOT EXISTS tb_block (
  chain_id varchar(40) NOT NULL COMMENT '区块链编号',
  block_number bigint(25) COLLATE utf8mb4_bin NOT NULL COMMENT '块高',
  block_hash varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '块hash值',
  block_timestamp datetime COLLATE utf8mb4_bin NOT NULL COMMENT '区块时间',
  sealer_index int(4) COLLATE utf8mb4_bin NOT NULL COMMENT '打包节点索引',
  trans_count bigint(20) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '块包含的交易数',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (block_number)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='区块信息表';

CREATE TABLE IF NOT EXISTS tb_trans (
  chain_id varchar(40) NOT NULL COMMENT '区块链编号',
  trans_hash varchar(128) NOT NULL COMMENT '交易hash',
  block_number bigint(25) NOT NULL COMMENT '所属区块',
  trans_from varchar(64) DEFAULT NULL COMMENT 'from',
  trans_to varchar(64) DEFAULT NULL COMMENT 'to',
  block_timestamp datetime NOT NULL COMMENT '所属块出块时间',
  gas_used bigint(30) DEFAULT '0' COMMENT '交易回执已消耗gas，十进制',
  status int(5) DEFAULT '0' COMMENT '交易回执状态，十进制',
  extra_data varchar(1024) DEFAULT NULL COMMENT '交易中的extra data，如appId, appName',
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modify_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (trans_hash),
  KEY index_number (block_number)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='交易hash信息表';

CREATE TABLE IF NOT EXISTS tb_contract_info (
  chain_id varchar(40) NOT NULL COMMENT '区块链编号',
  contract_address varchar(64) NOT NULL COMMENT '合约地址',
  contract_abi longtext COMMENT '合约abi内容',
  description varchar(1024) COMMENT '描述',
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modify_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (contract_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT = Dynamic COMMENT='合约信息表';

CREATE TABLE IF NOT EXISTS tb_method (
  method_id varchar(128) NOT NULL COMMENT '方法id',
  abi_hash varchar(128) NOT NULL COMMENT 'abi算的hash',
  name varchar(128) COMMENT '方法名',
  method_type varchar(32) COMMENT '方法类型',
  abi_info text COMMENT 'abi内容',
  index_cnt int(2) COMMENT 'abi里index个数',
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modify_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (method_id,abi_hash)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='方法解析信息表';

CREATE TABLE IF NOT EXISTS tb_trans_daily (
  chain_id varchar(40) NOT NULL COMMENT '区块链编号',
  trans_day date NOT NULL COMMENT '日期',
  trans_count int(11) DEFAULT '0' COMMENT '交易数量',
  block_number bigint(25) NOT NULL COMMENT '当前统计到的块高',
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modify_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (trans_day)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='每日交易数据表';

CREATE TABLE IF NOT EXISTS tb_bac20_receipt_event (
  block_number BIGINT(25) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '块高',
  tx_index int(4) COLLATE utf8mb4_bin NOT NULL  COMMENT '交易索引',
  event_index int(4) COLLATE utf8mb4_bin NOT NULL  COMMENT '事件索引',
  app_id VARCHAR(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用id',
  tx_hash VARCHAR(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '交易哈希',
  bac20_address VARCHAR(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '积分合约地址',
  contract_address VARCHAR(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '交易合约地址',
  func_name VARCHAR(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '函数名称',
  operator VARCHAR(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作地址',
  from_address VARCHAR(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '源地址',
  to_address VARCHAR(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '目的地址',
  amount BIGINT(20) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '元豆交易数量',
  block_timestamp BIGINT(20) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '区块时间戳',
  description VARCHAR(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '修改时间',
  PRIMARY KEY (block_number,tx_index,event_index),
  KEY idx_bac20_address (bac20_address),
  KEY idx_operator (operator),
  KEY idx_func_name (func_name),
  KEY idx_from_address (from_address),
  KEY idx_to_address (to_address),
  KEY idx_block_timestamp (block_timestamp)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='元豆事件回执表';

CREATE TABLE IF NOT EXISTS tb_search_cache (
  search_value VARCHAR(25) COLLATE utf8mb4_bin NOT NULL COMMENT '查询条件',
  total_supply BIGINT(20) COLLATE utf8mb4_bin NOT NULL COMMENT '总发行量',
  total_supply_weworld BIGINT(20) COLLATE utf8mb4_bin COMMENT '微众世界待平账量',
  total_balance_weworld BIGINT(20) COLLATE utf8mb4_bin COMMENT '微众世界余额',
  total_used BIGINT(20) COLLATE utf8mb4_bin NOT NULL COMMENT '待分享量（财政账户余额）',
  total_circulate BIGINT(20) COLLATE utf8mb4_bin NOT NULL COMMENT '流通量',
  total_zombie BIGINT(20) COLLATE utf8mb4_bin COMMENT '僵尸账户余额',
  total_supply_merge BIGINT(20) COLLATE utf8mb4_bin COMMENT '订单元豆总额 = 充值总额 - 已出库的总和',
  total_recharge BIGINT(20) COLLATE utf8mb4_bin COMMENT '充值总额（在库 + 在途）',
  total_withdraw BIGINT(20) COLLATE utf8mb4_bin COMMENT '提现总额（在库 + 已出库）',
  total_card_in_library BIGINT(20) COLLATE utf8mb4_bin COMMENT '卡总额RMB（在库）',
  block_number BIGINT(25) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '最新缓存的块高',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '修改时间',
  PRIMARY KEY (search_value)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='查询缓存表';

CREATE TABLE IF NOT EXISTS tb_bac20_user (
  user_address varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '用户地址',
  block_number BIGINT(25) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '最新交易的块高',
  block_timestamp BIGINT(20) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '最新交易的区块时间',
  first_number BIGINT(25) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '第一次交易的块高',
  first_timestamp BIGINT(20) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '第一次交易的区块时间',
  create_time datetime DEFAULT current_timestamp() COMMENT '创建时间',
  modify_time datetime DEFAULT current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (user_address),
  KEY idx_block_number (block_number),
  KEY idx_block_timestamp (block_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='元豆用户表';

CREATE TABLE IF NOT EXISTS tb_stat_user (
  user_amount INT COLLATE utf8mb4_bin NOT NULL COMMENT '用户数量',
  check_date VARCHAR(25) COLLATE utf8mb4_bin NOT NULL COMMENT '日期，如2023-01-01',
  check_hour INT COLLATE utf8mb4_bin NOT NULL COMMENT '小时',
  block_number BIGINT(25) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '最新统计的块高',
  block_timestamp BIGINT(20) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '最新统计的区块时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '修改时间',
  PRIMARY KEY (check_date, check_hour),
  KEY idx_block_number (block_number),
  KEY idx_block_timestamp (block_timestamp)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户统计表';

CREATE TABLE IF NOT EXISTS tb_stat_bac20_amount (
  issue_amount BIGINT(20) COLLATE utf8mb4_bin NOT NULL COMMENT '发行量',
  used_amount BIGINT(20) COLLATE utf8mb4_bin NOT NULL COMMENT '消耗数量',
  check_date VARCHAR(25) COLLATE utf8mb4_bin NOT NULL COMMENT '日期，如2023-01-01',
  check_hour INT COLLATE utf8mb4_bin NOT NULL COMMENT '小时',
  block_number BIGINT(25) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '最新统计的块高',
  block_timestamp BIGINT(20) COLLATE utf8mb4_bin DEFAULT 0 COMMENT '最新统计的区块时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '修改时间',
  PRIMARY KEY (check_date, check_hour),
  KEY idx_block_number (block_number),
  KEY idx_block_timestamp (block_timestamp)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='元豆数据量统计表';

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

-- JD卡信息
CREATE TABLE IF NOT EXISTS tb_card_info (
  card_number varchar(128) NOT NULL COMMENT '卡的唯一号码',
  card_amount int(11) DEFAULT NULL COMMENT '卡的面额，如100，50等',
  type int(2) NOT NULL DEFAULT 1 COMMENT '卡类型，1-JD卡',
  deposit_order_biz varchar(128) DEFAULT NULL COMMENT '充值订单流水',
  withdraw_order_biz varchar(64) DEFAULT NULL COMMENT '提现订单流水',
  withdraw_address varchar(64) DEFAULT NULL COMMENT '提现的地址',
  withdraw_status tinyint(1) DEFAULT 0 COMMENT '提现状态（卡是否已出库），0-卡未出库，1-卡已出库',
  in_block_number bigint(25) DEFAULT NULL COMMENT '充值订单卡入库块高',
  in_timestamp datetime DEFAULT NULL COMMENT '充值订单卡入库时间',
  in_tx_hash varchar(128) DEFAULT NULL COMMENT '充值订单卡入库的交易hash',
  out_block_number bigint(25) DEFAULT NULL COMMENT '提现订单卡出库块高',
  out_timestamp datetime DEFAULT NULL COMMENT '提现订单卡出库时间',
  out_tx_hash varchar(128) DEFAULT NULL COMMENT '提现订单卡出库的交易hash',
  description varchar(1024) DEFAULT NULL COMMENT '描述',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '修改时间',
  PRIMARY KEY (card_number)
) ENGINE=InnoDB  DEFAULT CHARACTER SET utf8mb4 COLLATE=utf8mb4_bin COMMENT='JD卡记录信息';

-- 充值信息
CREATE TABLE IF NOT EXISTS tb_deposit_info (
  deposit_order_biz varchar(128) NOT NULL COMMENT '充值订单流水',
  deposit_address varchar(64) NOT NULL COMMENT '充值订单的接收地址',
  deposit_status tinyint(1) DEFAULT 0 COMMENT '充值订单状态（卡是否已入库），0-卡待入库，1-卡已入库',
  deposit_amount int(11) DEFAULT NULL COMMENT '充值元豆金额，如10000豆',
  deposit_block_number bigint(25) NOT NULL COMMENT '充值订单块高',
  deposit_timestamp datetime DEFAULT NULL COMMENT '充值订单订单时间',
  deposit_tx_hash varchar(128) DEFAULT NULL COMMENT '充值订单的交易哈希',
  cancel_status tinyint(1) DEFAULT 0 COMMENT '取消状态，0-未取消，1-已取消',
  physical_cards text DEFAULT NULL COMMENT '关联的卡列表（json）',
  cards_cnt int(11) DEFAULT NULL COMMENT '卡数量',
  in_block_number bigint(25) DEFAULT NULL COMMENT '充值订单卡入库块高',
  in_timestamp datetime DEFAULT NULL COMMENT '充值订单卡入库时间',
  in_tx_hash varchar(128) DEFAULT NULL COMMENT '充值订单卡入库的交易hash',
  description varchar(1024) DEFAULT NULL COMMENT '描述',
  card_type int(2) DEFAULT 1 COMMENT '类型：1-实体卡；2-虚拟卡',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '修改时间',
  PRIMARY KEY (deposit_order_biz),
  KEY idx_cs (cancel_status)
) ENGINE=InnoDB  DEFAULT CHARACTER SET utf8mb4 COLLATE=utf8mb4_bin COMMENT='元豆充值订单信息';

-- 提现信息
CREATE TABLE IF NOT EXISTS tb_withdraw_info (
  withdraw_order_biz varchar(64) NOT NULL COMMENT '提现订单流水',
  withdraw_address varchar(64) NOT NULL COMMENT '发起提现的地址',
  withdraw_amount int(11) DEFAULT NULL COMMENT '提现元豆金额，如10000豆',
  withdraw_status tinyint(1) DEFAULT 0 COMMENT '提现状态（卡是否已出库），0-卡未出库，1-卡已出库',
  withdraw_block_number bigint(25) NOT NULL COMMENT '提现订单块高',
  withdraw_timestamp datetime DEFAULT NULL COMMENT '提现订单订单时间',
  withdraw_tx_hash varchar(128) DEFAULT NULL COMMENT '提现订单的交易哈希',
  cancel_status tinyint(1) DEFAULT 0 COMMENT '取消状态，0-未取消，1-已取消',
  physical_cards text DEFAULT NULL COMMENT '关联的卡列表（json）',
  cards_cnt int(11) DEFAULT NULL COMMENT '卡数量',
  send_tx_hash varchar(128) DEFAULT NULL COMMENT '用户提现转账的交易哈希',
  out_block_number bigint(25) DEFAULT NULL COMMENT '提现订单卡出库块高',
  out_timestamp datetime DEFAULT NULL COMMENT '提现订单卡出库时间',
  out_tx_hash varchar(128) DEFAULT NULL COMMENT '提现订单卡出库的交易hash',
  description varchar(1024) DEFAULT NULL COMMENT '描述',
  card_type int(2) DEFAULT 1 COMMENT '类型：1-实体卡；2-虚拟卡',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '创建时间',
  modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() COMMENT '修改时间',
  PRIMARY KEY (withdraw_address, withdraw_order_biz),
  KEY idx_ws (withdraw_status),
  KEY idx_wth (withdraw_tx_hash),
  KEY idx_cs (cancel_status)
) ENGINE=InnoDB  DEFAULT CHARACTER SET utf8mb4 COLLATE=utf8mb4_bin COMMENT='元豆提现订单信息';


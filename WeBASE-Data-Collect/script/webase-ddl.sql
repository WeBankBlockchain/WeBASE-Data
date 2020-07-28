
-- ----------------------------
-- Table structure for tb_chain
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_chain (
	id int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
	chain_id int(11) NOT NULL COMMENT '区块链编号',
	chain_name varchar(120) DEFAULT NULL COMMENT '区块链名称',
	chain_type tinyint(4) DEFAULT '0' COMMENT '类型（ 0-非国密 1-国密）',
	description varchar(1024) COMMENT '描述',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY uk_chain (chain_id),
	UNIQUE KEY uk_name (chain_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区块链信息表';


-- ----------------------------
-- Table structure for tb_front
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_front (
	front_id int(11) NOT NULL AUTO_INCREMENT COMMENT '前置服务编号',
	chain_id int(11) NOT NULL COMMENT '所属区块链编号',
	node_id varchar(250) NOT NULL COMMENT '节点编号',
	front_ip varchar(16) NOT NULL COMMENT '前置服务ip',
	front_port int(11) DEFAULT NULL COMMENT '前置服务端口',
	agency varchar(32) NOT NULL COMMENT '所属机构名称',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (front_id),
	UNIQUE KEY uk_chain_node (chain_id,node_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='前置服务信息表';


-- ----------------------------
-- Table structure for tb_group
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_group (
	id int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
	group_id int(11) NOT NULL COMMENT '群组ID',
	chain_id int(11) NOT NULL COMMENT '所属区块链编号',
	genesis_block_hash varchar(128) NOT NULL COMMENT '创世块hash',
	group_name varchar(64) NOT NULL COMMENT '群组名字',
	app_name varchar(128) DEFAULT NULL COMMENT '应用名称',
    app_version varchar(64) DEFAULT NULL COMMENT '应用版本号',
    app_synopsis varchar(2048) DEFAULT NULL COMMENT '应用概要介绍',
    app_icon blob DEFAULT NULL COMMENT '应用图标',
	group_status tinyint(4) DEFAULT '1' COMMENT '状态（1-正常 2-异常）',
	node_count int DEFAULT '0' COMMENT '群组下节点数',
	description text COMMENT '应用描述',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY uk_group_chain (group_id,chain_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='群组信息表';


-- ----------------------------
-- Table structure for tb_front_group_map
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_front_group_map (
	map_id int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
	chain_id int(11) NOT NULL COMMENT '区块链编号',
	front_id int(11) NOT NULL COMMENT '前置服务编号',
	group_id int(11) NOT NULL COMMENT '群组编号',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (map_id),
	UNIQUE KEY uk_chain_front_group (chain_id,front_id,group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='前置群组映射表';


-- ----------------------------
-- Table structure for tb_node
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_node (
	id int(11) NOT NULL AUTO_INCREMENT COMMENT '自增编号',
    node_id varchar(250) NOT NULL  COMMENT '节点编号',
    chain_id int(11) NOT NULL COMMENT '所属区块链编号',
    group_id int(11) NOT NULL COMMENT '所属群组编号',
    org_name varchar(128) DEFAULT NULL COMMENT '所属机构',
    node_ip varchar(16) DEFAULT NULL COMMENT '节点ip',
    p2p_port int(11) DEFAULT NULL COMMENT '节点p2p端口',
    block_number bigint(20) DEFAULT '0' COMMENT '节点块高',
    pbft_view bigint(20) DEFAULT '0' COMMENT 'pbft_view',
    node_active tinyint(4) DEFAULT '2' COMMENT '节点存活标识(1存活，2不存活)',
    description varchar(1024) COMMENT '描述',
    create_time datetime DEFAULT NULL COMMENT '创建时间',
    modify_time datetime DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_node (node_id,chain_id,group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='节点信息表';


-- ----------------------------
-- Table structure for tb_txn_daily
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_txn_daily (
	id int(11) NOT NULL AUTO_INCREMENT COMMENT '自增编号',
	chain_id int(11) NOT NULL COMMENT '所属区块链编号',
	group_id int(11) NOT NULL COMMENT '所属群组编号',
	stat_date date NOT NULL COMMENT '统计日期',
	txn int(11) COMMENT '交易量',
	block_number int(11) DEFAULT '0' COMMENT '当前统计到的块高',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY uk_data (chain_id,group_id,stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='每日交易量记录表';

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_user (
	user_id int(11) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
	chain_id int(11) NOT NULL COMMENT '所属区块链编号',
	group_id int(11) NOT NULL COMMENT '所属群组编号',
	user_name varchar(64) binary NOT NULL COMMENT '用户名',
	address varchar(64) DEFAULT NULL COMMENT '链上地址',
	description varchar(250) DEFAULT NULL COMMENT '备注',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (user_id),
	UNIQUE KEY uk_address (chain_id,group_id,address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';


-- ----------------------------
-- Table structure for tb_contract
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_contract (
	contract_id int(11) NOT NULL AUTO_INCREMENT COMMENT '合约编号',
	chain_id int(11) NOT NULL COMMENT '所属区块链编号',
	group_id int(11) NOT NULL COMMENT '所属群组编号',
	contract_path varchar(24) binary NOT NULL COMMENT '合约所在目录',
	contract_name varchar(120) binary NOT NULL COMMENT '合约名称',
	contract_source mediumtext COMMENT '合约源码',
	contract_address varchar(64) COMMENT '合约地址',
	contract_abi mediumtext COMMENT '编译合约生成的abi文件内容',
	contract_bin mediumtext COMMENT '合约运行时binary，用于合约解析',
	bytecode_bin mediumtext COMMENT '合约bytecode binary，用于部署合约',
	contract_type tinyint(4) DEFAULT '0' COMMENT '合约类型(0-普通合约，1-系统合约)',
	description varchar(1024) COMMENT '描述',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (contract_id),
	UNIQUE KEY uk_group_path_name (chain_id,group_id,contract_path,contract_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合约表';


-- ----------------------------
-- Table structure for tb_method
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_method (
	id int(11) NOT NULL AUTO_INCREMENT COMMENT '自增编号',
	contract_id int(11) NOT NULL COMMENT '所属合约编号',
	chain_id int(11) NOT NULL COMMENT '所属区块链编号',
	group_id int(11) NOT NULL COMMENT '所属群组编号',
	method_id varchar(128) COMMENT '方法id',
	method_name varchar(128) COMMENT '方法名',
	method_type varchar(32) COMMENT '方法类型',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY uk_method (contract_id,method_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='方法解析信息表';

-- ----------------------------
-- Table structure for tb_solc
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_solc (
	id int(11) NOT NULL AUTO_INCREMENT COMMENT '自增编号',
	solc_name varchar(128) COMMENT '编译名称',
	encrypt_type tinyint(4) DEFAULT '0' COMMENT '类型（ 0-非国密 1-国密）',
	md5 varchar(128) COMMENT 'md5',
	file_size bigint(25)COMMENT '文件长度',
	description varchar(1024) COMMENT '描述',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='编译器信息表';

-- ----------------------------
-- Table structure for tb_keywords
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_keywords (
	id int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
	keyword varchar(1024) COMMENT '关键字',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (id),
	UNIQUE KEY uk_keyword (keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='关键字信息表';

-- ----------------------------
-- Table structure for tb_warning_info
-- ----------------------------
CREATE TABLE IF NOT EXISTS tb_warning_info (
	id int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
	keyword varchar(1024) COMMENT '关键字',
    comment varchar(1024) COMMENT '监管添加意见信息',
    chain_id int(11) NOT NULL COMMENT '所属区块链编号',
    group_id int(11) NOT NULL COMMENT '所属群组编号',
    tx_hash varchar(1024) COMMENT '交易hash',
    status tinyint(4) DEFAULT '1' COMMENT '状态（1-处理中 2-已处理）',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (id),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='关键字告警信息表';
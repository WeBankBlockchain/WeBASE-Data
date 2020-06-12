
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
	group_status tinyint(4) DEFAULT '1' COMMENT '状态（1-正常 2-异常）',
	node_count int DEFAULT '0' COMMENT '群组下节点数',
	group_desc varchar(1024) COMMENT '群组描述',
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
	contract_source text COMMENT '合约源码',
	contract_address varchar(64) COMMENT '合约地址',
	contract_abi text COMMENT '编译合约生成的abi文件内容',
	contract_bin text COMMENT '合约运行时binary，用于合约解析',
	bytecode_bin text COMMENT '合约bytecode binary，用于部署合约',
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
	solc_name varchar(128) COMMENT '名称',
	encrypt_type tinyint(4) DEFAULT '0' COMMENT '类型（ 0-非国密 1-国密）',
	md5 varchar(128) COMMENT 'md5',
	file_size bigint(25)COMMENT '文件长度',
	description varchar(1024) COMMENT '描述',
	create_time datetime DEFAULT NULL COMMENT '创建时间',
	modify_time datetime DEFAULT NULL COMMENT '修改时间',
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='前置群组映射表';

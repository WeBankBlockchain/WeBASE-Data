-- ----------------------------
-- 1、init tb_contract data
-- ----------------------------
INSERT INTO tb_contract (contract_id,chain_id,group_id,contract_path,contract_name,contract_address,contract_source,contract_abi,contract_bin,bytecode_bin,contract_type,description,create_time,modify_time) VALUES 
(1,0,0,'/','SystemConfigPrecompiled','0x0000000000000000000000000000000000001000','','[{"constant":false,"inputs":[{"name":"key","type":"string"},{"name":"value","type":"string"}],"name":"setValueByKey","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"}]','','',1,'system contract','2020-06-08 17:50:11','2020-06-08 17:50:11');

INSERT INTO tb_contract (contract_id,chain_id,group_id,contract_path,contract_name,contract_address,contract_source,contract_abi,contract_bin,bytecode_bin,contract_type,description,create_time,modify_time) VALUES 
(2,0,0,'/','TableFactoryPrecompiled','0x0000000000000000000000000000000000001001','','[{"constant":false,"inputs":[{"name":"tableName","type":"string"},{"name":"key","type":"string"},{"name":"valueField","type":"string"}],"name":"createTable","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"}]','','',1,'system contract','2020-06-08 17:50:11','2020-06-08 17:50:11');

INSERT INTO tb_contract (contract_id,chain_id,group_id,contract_path,contract_name,contract_address,contract_source,contract_abi,contract_bin,bytecode_bin,contract_type,description,create_time,modify_time) VALUES 
(3,0,0,'/','CRUDPrecompiled','0x0000000000000000000000000000000000001002','','[{"constant":false,"inputs":[{"name":"tableName","type":"string"},{"name":"key","type":"string"},{"name":"entry","type":"string"},{"name":"condition","type":"string"},{"name":"optional","type":"string"}],"name":"update","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[{"name":"tableName","type":"string"},{"name":"key","type":"string"},{"name":"condition","type":"string"},{"name":"optional","type":"string"}],"name":"select","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"tableName","type":"string"},{"name":"key","type":"string"},{"name":"entry","type":"string"},{"name":"optional","type":"string"}],"name":"insert","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"tableName","type":"string"},{"name":"key","type":"string"},{"name":"condition","type":"string"},{"name":"optional","type":"string"}],"name":"remove","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"}]','','',1,'system contract','2020-06-08 17:50:11','2020-06-08 17:50:11');

INSERT INTO tb_contract (contract_id,chain_id,group_id,contract_path,contract_name,contract_address,contract_source,contract_abi,contract_bin,bytecode_bin,contract_type,description,create_time,modify_time) VALUES 
(4,0,0,'/','ConsensusPrecompiled','0x0000000000000000000000000000000000001003','','[{\"constant\":false,\"inputs\":[{\"name\":\"nodeID\",\"type\":\"string\"}],\"name\":\"addObserver\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"nodeID\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"nodeID\",\"type\":\"string\"}],\"name\":\"addSealer\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]','','',1,'system contract','2020-06-08 17:50:11','2020-06-08 17:50:11');

INSERT INTO tb_contract (contract_id,chain_id,group_id,contract_path,contract_name,contract_address,contract_source,contract_abi,contract_bin,bytecode_bin,contract_type,description,create_time,modify_time) VALUES 
(5,0,0,'/','CNSPrecompiled','0x0000000000000000000000000000000000001004','','[{"constant":true,"inputs":[{"name":"name","type":"string"}],"name":"selectByName","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":true,"inputs":[{"name":"name","type":"string"},{"name":"version","type":"string"}],"name":"selectByNameAndVersion","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"name","type":"string"},{"name":"version","type":"string"},{"name":"addr","type":"string"},{"name":"abi","type":"string"}],"name":"insert","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"}]','','',1,'system contract','2020-06-08 17:50:11','2020-06-08 17:50:11');

INSERT INTO tb_contract (contract_id,chain_id,group_id,contract_path,contract_name,contract_address,contract_source,contract_abi,contract_bin,bytecode_bin,contract_type,description,create_time,modify_time) VALUES 
(6,0,0,'/','PermissionPrecompiled','0x0000000000000000000000000000000000001005','','[{"constant":false,"inputs":[{"name":"table_name","type":"string"},{"name":"addr","type":"string"}],"name":"insert","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[{"name":"table_name","type":"string"}],"name":"queryByName","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"table_name","type":"string"},{"name":"addr","type":"string"}],"name":"remove","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"}]','','',1,'system contract','2020-06-08 17:50:11','2020-06-08 17:50:11');

INSERT INTO tb_contract (contract_id,chain_id,group_id,contract_path,contract_name,contract_address,contract_source,contract_abi,contract_bin,bytecode_bin,contract_type,description,create_time,modify_time) VALUES 
(7,0,0,'/','ContractLifeCyclePrecompiled','0x0000000000000000000000000000000000001007','','[{"constant":true,"inputs":[{"name":"addr","type":"address"}],"name":"getStatus","outputs":[{"name":"","type":"int256"},{"name":"","type":"string"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"addr","type":"address"}],"name":"unfreeze","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"addr","type":"address"}],"name":"freeze","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"contractAddr","type":"address"},{"name":"userAddr","type":"address"}],"name":"grantManager","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[{"name":"addr","type":"address"}],"name":"listManager","outputs":[{"name":"","type":"int256"},{"name":"","type":"address[]"}],"payable":false,"stateMutability":"view","type":"function"}]','','',1,'system contract','2020-06-08 17:50:11','2020-06-08 17:50:11');

-- ----------------------------
-- 2、init tb_method
-- ----------------------------
-- (system config info 0x1000) setValueByKey
-- ecdsa
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (1,0,0,'0xbd291aef','setValueByKey','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- guomi
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (1,0,0,'0x0749b518','setValueByKey','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- (table factory 0x1001) createTable
-- ecdsa
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (2,0,0,'0x56004b6a','createTable','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- guomi
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (2,0,0,'0xc92a7801','createTable','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- (crud info 0x1002) update select remove insert(same as cns's insert)
-- ecdsa
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (3,0,0,'0x2dca76c1','update','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (3,0,0,'0x983c6c4f','select','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (3,0,0,'0xa72a1e65','remove','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (3,0,0,'0xa216464b','insert','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- guomi
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (3,0,0,'0x10bd675b','update','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (3,0,0,'0x7388111f','select','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (3,0,0,'0x81b81824','remove','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (3,0,0,'0xb8eaa08d','insert','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- (consensus info node manage 0x1003) addObserver addSealer remove
-- ecdsa
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (4,0,0,'0x2800efc0','addObserver','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (4,0,0,'0x89152d1f','addSealer','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (4,0,0,'0x80599e4b','remove','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- guomi
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (4,0,0,'0x25e85d16','addObserver','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (4,0,0,'0xdf434acc','addSealer','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (4,0,0,'0x86b733f9','remove','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- (cns info 0x1004) selectByName selectByNameAndVersion insert(same as crud's insert)
-- ecdsa
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (5,0,0,'0x819a3d62','selectByName','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (5,0,0,'0x897f0251','selectByNameAndVersion','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (5,0,0,'0xa216464b','insert','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- guomi
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (5,0,0,'0x078af4af','selectByName','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (5,0,0,'0xec72a422','selectByNameAndVersion','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (5,0,0,'0xb8eaa08d','insert','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- (permission manage 0x1005) insert queryByName remove
-- ecdsa
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (6,0,0,'0x06e63ff8','insert','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (6,0,0,'0x20586031','queryByName','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (6,0,0,'0x44590a7e','remove','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- guomi
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (6,0,0,'0xce0a9fb9','insert','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (6,0,0,'0xbbec3f91','queryByName','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (6,0,0,'0x85d23afc','remove','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- (csm 0x1007) freeze unfreeze grantManager
-- ecdsa
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (7,0,0,'0x8d1fdf2f','freeze','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (7,0,0,'0x45c8b1a6','unfreeze','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (7,0,0,'0xa721fb43','grantManager','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
-- guomi
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (7,0,0,'0xf12c66df','freeze','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (7,0,0,'0x61cb24c3','unfreeze','function','2020-06-08 17:52:02','2020-06-08 17:52:02');
INSERT INTO tb_method (contract_id,chain_id,group_id,method_id,method_name,method_type,create_time,modify_time) VALUES (7,0,0,'0x27c46414','grantManager','function','2020-06-08 17:52:02','2020-06-08 17:52:02');

-- ----------------------------
-- 3、init tb_solc
-- ----------------------------
INSERT INTO tb_solc (id,solc_name,encrypt_type,md5,file_size,description,create_time,modify_time) VALUES 
(1,'soljson-v0.4.25-gm.js',1,'c0810103136fb9177df943346b2dcad4',8273598,'guomi','2020-06-19 11:05:56','2020-06-19 11:05:56')
,(2,'soljson-v0.4.25+commit.59dbf8f1.js',0,'e201c5913e0982cb90cdb2a711e36f63',8276063,'ecdsa','2020-06-19 11:19:10','2020-06-19 11:19:10')
,(3,'soljson-v0.5.1.js',0,'6b99bd0cc1a470bfda4a540d1e2f2b44',9083012,'ecdsa','2020-06-19 18:56:27','2020-06-19 18:56:27')
,(4,'soljson-v0.5.1-gm.js',1,'4a41dd17d82d8047744a184d98abf54a',9232616,'gm','2020-06-19 18:59:32','2020-06-19 18:59:32')
,(5,'soljson-v0.5.10.js',0,'2019c21672436e4e0391271090dccf94',13668994,'ecdsa','2020-06-19 18:57:48','2020-06-19 18:57:48')
,(6,'soljson-v0.5.16.js',0,'de4684e68e8b3551214500ace7db759e',12394706,'ecdsa','2020-06-19 18:58:08','2020-06-19 18:58:08');
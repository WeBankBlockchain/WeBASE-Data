package com.webank.wsbcs.chdis.common.dao.mapper;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import com.webank.wsbcs.chdis.base.entity.data.RspUserFrow;
import com.webank.wsbcs.chdis.common.dao.entity.TbBac20ReceiptEvent;
import com.webank.wsbcs.chdis.common.dao.entity.TbBac20ReceiptEventExample;

@Mapper
@SuppressWarnings("all") // SONAR
public interface TbBac20ReceiptEventMapper {

    @Select({"truncate table tb_bac20_receipt_event"})
    void truncateTable();

    @Delete({"delete from tb_bac20_receipt_event",
            "where block_number >= #{blockNumber,jdbcType=BIGINT}"})
    int deleteBiggerThanBlockNumber(Long blockNumber);

    @Select({"select IFNULL(max(block_number),0) from tb_bac20_receipt_event"})
    long getMaxBlockNumber();

    @Select({"<script>", "select operator from tb_bac20_receipt_event ",
            "where func_name = 'issue' ",
            "<if test=\"bac20Address != null and bac20Address != ''\">",
            "and bac20_address = #{bac20Address,jdbcType=VARCHAR} ", "</if>", "</script>"})
    List<String> getAllBac20Managers(@Param("bac20Address") String bac20Address);

    @Select({"<script>", "select count(distinct(t.address)) from ",
            "(SELECT distinct(t1.from_address) as address from tb_bac20_receipt_event t1 ",
            "where t1.block_number &lt;= #{blockNumber,jdbcType=BIGINT} ",
            "<if test=\"bac20Address != null and bac20Address != ''\">",
            "and t1.bac20_address = #{bac20Address,jdbcType=VARCHAR} ", "</if>",
            "union SELECT distinct(t2.to_address) as address from tb_bac20_receipt_event t2 ",
            "where t2.block_number &lt;= #{blockNumber,jdbcType=BIGINT} ",
            "<if test=\"bac20Address != null and bac20Address != ''\">",
            "and t2.bac20_address = #{bac20Address,jdbcType=VARCHAR} ", "</if>",
            ") t where t.address != '0x0000000000000000000000000000000000000000' ", "</script>"})
    int getBac20UsersCnt(@Param("bac20Address") String bac20Address,
            @Param("blockNumber") Long blockNumber);

    @Select({"<script>", "select count(distinct(operator)) from tb_bac20_receipt_event ",
            "where func_name = 'issue' ",
            "<if test=\"bac20Address != null and bac20Address != ''\">",
            "and bac20_address = #{bac20Address,jdbcType=VARCHAR} ", "</if>",
            "<if test=\"blockNumber != null\">",
            "and block_number &lt;= #{blockNumber,jdbcType=BIGINT} ", "</if>", "</script>"})
    int getBac20ManagersCnt(@Param("bac20Address") String bac20Address,
            @Param("blockNumber") Long blockNumber);

    @Select({"<script>", "select operator,from_address,to_address,func_name,amount ",
            "from tb_bac20_receipt_event ",
            "where bac20_address = #{bac20Address,jdbcType=VARCHAR} ",
            "<if test=\"blockNumber != null\">",
            "and block_number &lt;= #{blockNumber,jdbcType=BIGINT} ", "</if>",
            "<if test=\"blockTimestamp != null\">",
            "and block_timestamp &lt; #{blockTimestamp,jdbcType=BIGINT} ", "</if>",
            "and (from_address = #{userAddress,jdbcType=VARCHAR} or to_address = #{userAddress,jdbcType=VARCHAR}) ",
            "</script>"})
    List<TbBac20ReceiptEvent> getBac20UserEventRecord(@Param("bac20Address") String bac20Address,
            @Param("userAddress") String userAddress, @Param("blockNumber") Long blockNumber,
            @Param("blockTimestamp") Long blockTimestamp);

    @Select({
            "select operator,from_address,to_address,func_name,amount,block_number,tx_index,event_index ",
            "from tb_bac20_receipt_event ",
            "where bac20_address = #{bac20Address,jdbcType=VARCHAR} ",
            "and block_number <= #{blockNumber,jdbcType=INTEGER} ",
            "and (from_address = #{userAddress,jdbcType=VARCHAR} or to_address = #{userAddress,jdbcType=VARCHAR})"})
    List<TbBac20ReceiptEvent> getBac20UserEventRecordUnderBlock(
            @Param("bac20Address") String bac20Address, @Param("userAddress") String userAddress,
            @Param("blockNumber") Long blockNumber);

    @Select({"<script>", "select IFNULL(sum(amount),0) from tb_bac20_receipt_event ",
            "where func_name = 'issue' ",
            "<if test=\"bac20Address != null and bac20Address != ''\">",
            "and bac20_address = #{bac20Address,jdbcType=VARCHAR} ", "</if>",
            "<if test=\"operator != null and operator != ''\">",
            "and operator = #{operator,jdbcType=VARCHAR} ", "</if>",
            "<if test=\"blockNumber != null\">",
            "and block_number &lt;= #{blockNumber,jdbcType=BIGINT} ", "</if>",
            "<if test=\"blockTimestamp != null\">",
            "and block_timestamp &lt; #{blockTimestamp,jdbcType=BIGINT} ", "</if>", "</script>"})
    long getBac20IssueRecordAmount(@Param("bac20Address") String bac20Address,
            @Param("operator") String operator, @Param("blockNumber") Long blockNumber,
            @Param("blockTimestamp") Long blockTimestamp);

    @Select({"<script>", "select IFNULL(sum(amount),0) from tb_bac20_receipt_event ",
            "where func_name = 'destroyFrom' ",
            "<if test=\"bac20Address != null and bac20Address != ''\">",
            "and bac20_address = #{bac20Address,jdbcType=VARCHAR} ", "</if>",
            "<if test=\"operator != null and operator != ''\">",
            "and operator = #{operator,jdbcType=VARCHAR} ", "</if>",
            "<if test=\"blockNumber != null\">",
            "and block_number &lt;= #{blockNumber,jdbcType=BIGINT} ", "</if>",
            "<if test=\"blockTimestamp != null\">",
            "and block_timestamp &lt; #{blockTimestamp,jdbcType=BIGINT} ", "</if>", "</script>"})
    long getBac20DestroyRecordAmount(@Param("bac20Address") String bac20Address,
            @Param("operator") String operator, @Param("blockNumber") Long blockNumber,
            @Param("blockTimestamp") Long blockTimestamp);

    @Select({"<script>", "select operator,from_address,to_address,func_name,amount ",
            "from tb_bac20_receipt_event ", "where 1 = 1 ",
            "<if test=\"bac20Address != null and bac20Address != ''\">",
            "and bac20_address = #{bac20Address,jdbcType=VARCHAR} ", "</if>",
            "and block_timestamp / 1000 > UNIX_TIMESTAMP(#{currentDate,jdbcType=TIMESTAMP} - INTERVAL 1 HOUR)",
            "</script>"})
    List<TbBac20ReceiptEvent> getBac20EventRecordLastHour(
            @Param("bac20Address") String bac20Address, @Param("currentDate") Date currentDate);

    @Select({"<script>",
            "select operator,from_address,to_address,func_name,amount,block_number,block_timestamp ",
            "from tb_bac20_receipt_event ",
            "where bac20_address = #{bac20Address,jdbcType=VARCHAR} ",
            "and block_number > #{blockStart,jdbcType=BIGINT} ",
            "and block_number &lt;= #{blockEnd,jdbcType=BIGINT} ",
            "order by block_number,tx_index,event_index ", "</script>"})
    List<TbBac20ReceiptEvent> getBac20EventRecordByBlockNumber(
            @Param("bac20Address") String bac20Address, @Param("blockStart") Long blockStart,
            @Param("blockEnd") Long blockEnd);

    @SelectProvider(type = TbBac20ReceiptEventSqlProvider.class, method = "selectByExample")
    @Results({
            @Result(column = "block_number", property = "blockNumber", jdbcType = JdbcType.BIGINT,
                    id = true),
            @Result(column = "tx_index", property = "txIndex", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "event_index", property = "eventIndex", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "app_id", property = "appId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR),
            @Result(column = "bac20_address", property = "bac20Address",
                    jdbcType = JdbcType.VARCHAR),
            @Result(column = "contract_address", property = "contractAddress",
                    jdbcType = JdbcType.VARCHAR),
            @Result(column = "func_name", property = "funcName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "operator", property = "operator", jdbcType = JdbcType.VARCHAR),
            @Result(column = "from_address", property = "fromAddress", jdbcType = JdbcType.VARCHAR),
            @Result(column = "to_address", property = "toAddress", jdbcType = JdbcType.VARCHAR),
            @Result(column = "amount", property = "amount", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_timestamp", property = "blockTimestamp",
                    jdbcType = JdbcType.BIGINT),
            @Result(column = "description", property = "description", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "modify_time", property = "modifyTime",
                    jdbcType = JdbcType.TIMESTAMP)})
    List<RspUserFrow> selectFlowByExample(TbBac20ReceiptEventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbBac20ReceiptEventSqlProvider.class, method = "countByExample")
    long countByExample(TbBac20ReceiptEventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @DeleteProvider(type = TbBac20ReceiptEventSqlProvider.class, method = "deleteByExample")
    int deleteByExample(TbBac20ReceiptEventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @Delete({"delete from tb_bac20_receipt_event",
            "where block_number = #{blockNumber,jdbcType=BIGINT}",
            "and tx_index = #{txIndex,jdbcType=INTEGER}",
            "and event_index = #{eventIndex,jdbcType=INTEGER}"})
    int deleteByPrimaryKey(@Param("blockNumber") Long blockNumber,
            @Param("txIndex") Integer txIndex, @Param("eventIndex") Integer eventIndex);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @Insert({"insert into tb_bac20_receipt_event (block_number, tx_index, ",
            "event_index, app_id, ", "tx_hash, bac20_address, ", "contract_address, func_name, ",
            "operator, from_address, ", "to_address, amount, ", "block_timestamp, description, ",
            "create_time, modify_time)",
            "values (#{blockNumber,jdbcType=BIGINT}, #{txIndex,jdbcType=INTEGER}, ",
            "#{eventIndex,jdbcType=INTEGER}, #{appId,jdbcType=VARCHAR}, ",
            "#{txHash,jdbcType=VARCHAR}, #{bac20Address,jdbcType=VARCHAR}, ",
            "#{contractAddress,jdbcType=VARCHAR}, #{funcName,jdbcType=VARCHAR}, ",
            "#{operator,jdbcType=VARCHAR}, #{fromAddress,jdbcType=VARCHAR}, ",
            "#{toAddress,jdbcType=VARCHAR}, #{amount,jdbcType=BIGINT}, ",
            "#{blockTimestamp,jdbcType=BIGINT}, #{description,jdbcType=VARCHAR}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{modifyTime,jdbcType=TIMESTAMP})"})
    int insert(TbBac20ReceiptEvent record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @InsertProvider(type = TbBac20ReceiptEventSqlProvider.class, method = "insertSelective")
    int insertSelective(TbBac20ReceiptEvent record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbBac20ReceiptEventSqlProvider.class, method = "selectByExample")
    @Results({
            @Result(column = "block_number", property = "blockNumber", jdbcType = JdbcType.BIGINT,
                    id = true),
            @Result(column = "tx_index", property = "txIndex", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "event_index", property = "eventIndex", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "app_id", property = "appId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR),
            @Result(column = "bac20_address", property = "bac20Address",
                    jdbcType = JdbcType.VARCHAR),
            @Result(column = "contract_address", property = "contractAddress",
                    jdbcType = JdbcType.VARCHAR),
            @Result(column = "func_name", property = "funcName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "operator", property = "operator", jdbcType = JdbcType.VARCHAR),
            @Result(column = "from_address", property = "fromAddress", jdbcType = JdbcType.VARCHAR),
            @Result(column = "to_address", property = "toAddress", jdbcType = JdbcType.VARCHAR),
            @Result(column = "amount", property = "amount", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_timestamp", property = "blockTimestamp",
                    jdbcType = JdbcType.BIGINT),
            @Result(column = "description", property = "description", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "modify_time", property = "modifyTime",
                    jdbcType = JdbcType.TIMESTAMP)})
    List<TbBac20ReceiptEvent> selectByExample(TbBac20ReceiptEventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @Select({"select",
            "block_number, tx_index, event_index, app_id, tx_hash, bac20_address, contract_address, ",
            "func_name, operator, from_address, to_address, amount, block_timestamp, description, ",
            "create_time, modify_time", "from tb_bac20_receipt_event",
            "where block_number = #{blockNumber,jdbcType=BIGINT}",
            "and tx_index = #{txIndex,jdbcType=INTEGER}",
            "and event_index = #{eventIndex,jdbcType=INTEGER}"})
    @Results({
            @Result(column = "block_number", property = "blockNumber", jdbcType = JdbcType.BIGINT,
                    id = true),
            @Result(column = "tx_index", property = "txIndex", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "event_index", property = "eventIndex", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "app_id", property = "appId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR),
            @Result(column = "bac20_address", property = "bac20Address",
                    jdbcType = JdbcType.VARCHAR),
            @Result(column = "contract_address", property = "contractAddress",
                    jdbcType = JdbcType.VARCHAR),
            @Result(column = "func_name", property = "funcName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "operator", property = "operator", jdbcType = JdbcType.VARCHAR),
            @Result(column = "from_address", property = "fromAddress", jdbcType = JdbcType.VARCHAR),
            @Result(column = "to_address", property = "toAddress", jdbcType = JdbcType.VARCHAR),
            @Result(column = "amount", property = "amount", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_timestamp", property = "blockTimestamp",
                    jdbcType = JdbcType.BIGINT),
            @Result(column = "description", property = "description", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "modify_time", property = "modifyTime",
                    jdbcType = JdbcType.TIMESTAMP)})
    TbBac20ReceiptEvent selectByPrimaryKey(@Param("blockNumber") Long blockNumber,
            @Param("txIndex") Integer txIndex, @Param("eventIndex") Integer eventIndex);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbBac20ReceiptEventSqlProvider.class,
            method = "updateByExampleSelective")
    int updateByExampleSelective(@Param("record") TbBac20ReceiptEvent record,
            @Param("example") TbBac20ReceiptEventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbBac20ReceiptEventSqlProvider.class, method = "updateByExample")
    int updateByExample(@Param("record") TbBac20ReceiptEvent record,
            @Param("example") TbBac20ReceiptEventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbBac20ReceiptEventSqlProvider.class,
            method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(TbBac20ReceiptEvent record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @Update({"update tb_bac20_receipt_event", "set app_id = #{appId,jdbcType=VARCHAR},",
            "tx_hash = #{txHash,jdbcType=VARCHAR},",
            "bac20_address = #{bac20Address,jdbcType=VARCHAR},",
            "contract_address = #{contractAddress,jdbcType=VARCHAR},",
            "func_name = #{funcName,jdbcType=VARCHAR},", "operator = #{operator,jdbcType=VARCHAR},",
            "from_address = #{fromAddress,jdbcType=VARCHAR},",
            "to_address = #{toAddress,jdbcType=VARCHAR},", "amount = #{amount,jdbcType=BIGINT},",
            "block_timestamp = #{blockTimestamp,jdbcType=BIGINT},",
            "description = #{description,jdbcType=VARCHAR},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "modify_time = #{modifyTime,jdbcType=TIMESTAMP}",
            "where block_number = #{blockNumber,jdbcType=BIGINT}",
            "and tx_index = #{txIndex,jdbcType=INTEGER}",
            "and event_index = #{eventIndex,jdbcType=INTEGER}"})
    int updateByPrimaryKey(TbBac20ReceiptEvent record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     */
    @Insert({"<script>", "insert ignore into tb_bac20_receipt_event (block_number, ",
            "tx_index, event_index, ", "app_id, tx_hash, ", "bac20_address, contract_address, ",
            "func_name, operator, ", "from_address, to_address, ", "amount, block_timestamp, ",
            "description, create_time, ", "modify_time)",
            "values<foreach collection=\"list\" item=\"detail\" index=\"index\" separator=\",\">(#{detail.blockNumber,jdbcType=BIGINT}, ",
            "#{detail.txIndex,jdbcType=INTEGER}, #{detail.eventIndex,jdbcType=INTEGER}, ",
            "#{detail.appId,jdbcType=VARCHAR}, #{detail.txHash,jdbcType=VARCHAR}, ",
            "#{detail.bac20Address,jdbcType=VARCHAR}, #{detail.contractAddress,jdbcType=VARCHAR}, ",
            "#{detail.funcName,jdbcType=VARCHAR}, #{detail.operator,jdbcType=VARCHAR}, ",
            "#{detail.fromAddress,jdbcType=VARCHAR}, #{detail.toAddress,jdbcType=VARCHAR}, ",
            "#{detail.amount,jdbcType=BIGINT}, #{detail.blockTimestamp,jdbcType=BIGINT}, ",
            "#{detail.description,jdbcType=VARCHAR}, #{detail.createTime,jdbcType=TIMESTAMP}, ",
            "#{detail.modifyTime,jdbcType=TIMESTAMP})</foreach></script>"})
    int batchInsert(List<TbBac20ReceiptEvent> list);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_bac20_receipt_event
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbBac20ReceiptEventSqlProvider.class, method = "getOneByExample")
    @Results({
            @Result(column = "block_number", property = "blockNumber", jdbcType = JdbcType.BIGINT,
                    id = true),
            @Result(column = "tx_index", property = "txIndex", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "event_index", property = "eventIndex", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "app_id", property = "appId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "tx_hash", property = "txHash", jdbcType = JdbcType.VARCHAR),
            @Result(column = "bac20_address", property = "bac20Address",
                    jdbcType = JdbcType.VARCHAR),
            @Result(column = "contract_address", property = "contractAddress",
                    jdbcType = JdbcType.VARCHAR),
            @Result(column = "func_name", property = "funcName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "operator", property = "operator", jdbcType = JdbcType.VARCHAR),
            @Result(column = "from_address", property = "fromAddress", jdbcType = JdbcType.VARCHAR),
            @Result(column = "to_address", property = "toAddress", jdbcType = JdbcType.VARCHAR),
            @Result(column = "amount", property = "amount", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_timestamp", property = "blockTimestamp",
                    jdbcType = JdbcType.BIGINT),
            @Result(column = "description", property = "description", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "modify_time", property = "modifyTime",
                    jdbcType = JdbcType.TIMESTAMP)})
    Optional<TbBac20ReceiptEvent> getOneByExample(TbBac20ReceiptEventExample example);
}

package com.webank.wsbcs.chdis.common.dao.mapper;

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
import com.webank.wsbcs.chdis.base.entity.data.RspTrendChart;
import com.webank.wsbcs.chdis.common.dao.entity.TbStatBac20Amount;
import com.webank.wsbcs.chdis.common.dao.entity.TbStatBac20AmountExample;

@Mapper
@SuppressWarnings("all")//SONAR
public interface TbStatBac20AmountMapper {

    @Select({"truncate table tb_stat_bac20_amount"})
    void truncateTable();
    
    @Delete({"delete from tb_stat_bac20_amount", "where block_number >= #{blockNumber,jdbcType=BIGINT}"})
    int deleteBiggerThanBlockNumber(Long blockNumber);

    @Select({"select IFNULL(max(block_number),0) table tb_stat_bac20_amount"})
    long selectMaxBlockNumber();

    @Select({"select block_number,issue_amount,used_amount ",
            "from tb_stat_bac20_amount order by block_number desc limit 1"})
    @Results({
            @Result(column = "block_number", property = "blockNumber", jdbcType = JdbcType.BIGINT),
            @Result(column = "issue_amount", property = "issueAmount", jdbcType = JdbcType.BIGINT),
            @Result(column = "used_amount", property = "usedAmount", jdbcType = JdbcType.BIGINT)})
    TbStatBac20Amount selectMaxBlockNumberRecord();

    @Insert({"insert into tb_stat_bac20_amount (issue_amount, used_amount, ",
            "check_date, check_hour, ", "block_number, block_timestamp, create_time, modify_time)",
            "values (#{issueAmount,jdbcType=BIGINT}, #{usedAmount,jdbcType=BIGINT}, ",
            "#{checkDate,jdbcType=VARCHAR}, #{checkHour,jdbcType=INTEGER}, ",
            "#{blockNumber,jdbcType=BIGINT}, #{blockTimestamp,jdbcType=BIGINT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{modifyTime,jdbcType=TIMESTAMP})",
            "ON DUPLICATE KEY UPDATE ",
            "issue_amount = #{issueAmount,jdbcType=BIGINT}, used_amount = #{usedAmount,jdbcType=BIGINT}, ",
            "modify_time = #{modifyTime,jdbcType=TIMESTAMP}"})
    int insertDuplicateUpdate(TbStatBac20Amount record);

    @Insert({"<script>", "insert into tb_stat_bac20_amount (issue_amount, ",
            "used_amount, check_date, ", "check_hour, block_number, ",
            "block_timestamp, create_time, ", "modify_time)",
            "values<foreach collection=\"list\" item=\"detail\" index=\"index\" separator=\",\">(#{detail.issueAmount,jdbcType=BIGINT}, ",
            "#{detail.usedAmount,jdbcType=BIGINT}, #{detail.checkDate,jdbcType=VARCHAR}, ",
            "#{detail.checkHour,jdbcType=INTEGER}, #{detail.blockNumber,jdbcType=BIGINT}, ",
            "#{detail.blockTimestamp,jdbcType=BIGINT}, #{detail.createTime,jdbcType=TIMESTAMP}, ",
            "#{detail.modifyTime,jdbcType=TIMESTAMP}) ", "</foreach>", "ON DUPLICATE KEY UPDATE ",
            "issue_amount = values(issue_amount), ", "used_amount = values(used_amount), ",
            "block_number = values(block_number), ",
            "block_timestamp = values(block_timestamp), modify_time = values(modify_time)</script>"})
    int batchInsertDuplicateUpdate(List<TbStatBac20Amount> list);

    @Select({"select", "issue_amount, check_date, check_hour", "from tb_stat_bac20_amount",
            "where check_date = #{checkDate,jdbcType=VARCHAR}"})
    @Results({@Result(column = "issue_amount", property = "amount", jdbcType = JdbcType.BIGINT),
            @Result(column = "check_date", property = "date", jdbcType = JdbcType.VARCHAR),
            @Result(column = "check_hour", property = "hour", jdbcType = JdbcType.INTEGER)})
    List<RspTrendChart> selectIssueAmountByCheckDate(String checkDate);

    @Select({"select", "used_amount, check_date, check_hour", "from tb_stat_bac20_amount",
            "where check_date = #{checkDate,jdbcType=VARCHAR}"})
    @Results({@Result(column = "used_amount", property = "amount", jdbcType = JdbcType.BIGINT),
            @Result(column = "check_date", property = "date", jdbcType = JdbcType.VARCHAR),
            @Result(column = "check_hour", property = "hour", jdbcType = JdbcType.INTEGER)})
    List<RspTrendChart> selectUsedAmountByCheckDate(String checkDate);

    @Select({"select", "(issue_amount - used_amount) as amount, check_date, check_hour",
            "from tb_stat_bac20_amount", "where check_date = #{checkDate,jdbcType=VARCHAR}"})
    @Results({@Result(column = "amount", property = "amount", jdbcType = JdbcType.BIGINT),
            @Result(column = "check_date", property = "date", jdbcType = JdbcType.VARCHAR),
            @Result(column = "check_hour", property = "hour", jdbcType = JdbcType.INTEGER)})
    List<RspTrendChart> selectCirculateAmountByCheckDate(String checkDate);

    @Select({"select t1.issue_amount as amount, t1.check_date ", "from tb_stat_bac20_amount t1 ",
            "where t1.block_number in ", "(select IFNULL(max(block_number),0) as block_number ",
            "from tb_stat_bac20_amount", "where check_date >= #{dateStart,jdbcType=VARCHAR}",
            "and check_date <= #{dateEnd,jdbcType=VARCHAR} group by check_date)"})
    @Results({@Result(column = "amount", property = "amount", jdbcType = JdbcType.BIGINT),
            @Result(column = "check_date", property = "date", jdbcType = JdbcType.VARCHAR)})
    List<RspTrendChart> selectIssueAmountBetweenCheckDate(@Param("dateStart") String dateStart,
            @Param("dateEnd") String dateEnd);

    @Select({"select t1.used_amount as amount, t1.check_date ", "from tb_stat_bac20_amount t1 ",
            "where t1.block_number in ", "(select IFNULL(max(block_number),0) as block_number ",
            "from tb_stat_bac20_amount", "where check_date >= #{dateStart,jdbcType=VARCHAR}",
            "and check_date <= #{dateEnd,jdbcType=VARCHAR} group by check_date)"})
    @Results({@Result(column = "amount", property = "amount", jdbcType = JdbcType.BIGINT),
            @Result(column = "check_date", property = "date", jdbcType = JdbcType.VARCHAR)})
    List<RspTrendChart> selectUsedAmountBetweenCheckDate(@Param("dateStart") String dateStart,
            @Param("dateEnd") String dateEnd);

    @Select({"select t1.block_number,t1.issue_amount,t1.used_amount ",
            "from tb_stat_bac20_amount t1, (select IFNULL(max(block_number),0) as block_number",
            "from tb_stat_bac20_amount where check_date < #{checkDate,jdbcType=VARCHAR}) t2",
            "where t1.block_number = t2.block_number "})
    @Results({
            @Result(column = "block_number", property = "blockNumber", jdbcType = JdbcType.BIGINT),
            @Result(column = "issue_amount", property = "issueAmount", jdbcType = JdbcType.BIGINT),
            @Result(column = "used_amount", property = "usedAmount", jdbcType = JdbcType.BIGINT)})
    TbStatBac20Amount selectMaxBlockRecordByCheckDate(@Param("checkDate") String checkDate);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbStatBac20AmountSqlProvider.class, method = "countByExample")
    long countByExample(TbStatBac20AmountExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @DeleteProvider(type = TbStatBac20AmountSqlProvider.class, method = "deleteByExample")
    int deleteByExample(TbStatBac20AmountExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @Delete({"delete from tb_stat_bac20_amount", "where check_date = #{checkDate,jdbcType=VARCHAR}",
            "and check_hour = #{checkHour,jdbcType=INTEGER}"})
    int deleteByPrimaryKey(@Param("checkDate") String checkDate,
            @Param("checkHour") Integer checkHour);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @Insert({"insert into tb_stat_bac20_amount (check_date, check_hour, ",
            "issue_amount, used_amount, ", "block_number, block_timestamp, ",
            "create_time, modify_time)",
            "values (#{checkDate,jdbcType=VARCHAR}, #{checkHour,jdbcType=INTEGER}, ",
            "#{issueAmount,jdbcType=BIGINT}, #{usedAmount,jdbcType=BIGINT}, ",
            "#{blockNumber,jdbcType=BIGINT}, #{blockTimestamp,jdbcType=BIGINT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{modifyTime,jdbcType=TIMESTAMP})"})
    int insert(TbStatBac20Amount record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @InsertProvider(type = TbStatBac20AmountSqlProvider.class, method = "insertSelective")
    int insertSelective(TbStatBac20Amount record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbStatBac20AmountSqlProvider.class, method = "selectByExample")
    @Results({
            @Result(column = "check_date", property = "checkDate", jdbcType = JdbcType.VARCHAR,
                    id = true),
            @Result(column = "check_hour", property = "checkHour", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "issue_amount", property = "issueAmount", jdbcType = JdbcType.BIGINT),
            @Result(column = "used_amount", property = "usedAmount", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_number", property = "blockNumber", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_timestamp", property = "blockTimestamp",
                    jdbcType = JdbcType.BIGINT),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "modify_time", property = "modifyTime",
                    jdbcType = JdbcType.TIMESTAMP)})
    List<TbStatBac20Amount> selectByExample(TbStatBac20AmountExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @Select({"select",
            "check_date, check_hour, issue_amount, used_amount, block_number, block_timestamp, ",
            "create_time, modify_time", "from tb_stat_bac20_amount",
            "where check_date = #{checkDate,jdbcType=VARCHAR}",
            "and check_hour = #{checkHour,jdbcType=INTEGER}"})
    @Results({
            @Result(column = "check_date", property = "checkDate", jdbcType = JdbcType.VARCHAR,
                    id = true),
            @Result(column = "check_hour", property = "checkHour", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "issue_amount", property = "issueAmount", jdbcType = JdbcType.BIGINT),
            @Result(column = "used_amount", property = "usedAmount", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_number", property = "blockNumber", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_timestamp", property = "blockTimestamp",
                    jdbcType = JdbcType.BIGINT),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "modify_time", property = "modifyTime",
                    jdbcType = JdbcType.TIMESTAMP)})
    TbStatBac20Amount selectByPrimaryKey(@Param("checkDate") String checkDate,
            @Param("checkHour") Integer checkHour);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbStatBac20AmountSqlProvider.class, method = "updateByExampleSelective")
    int updateByExampleSelective(@Param("record") TbStatBac20Amount record,
            @Param("example") TbStatBac20AmountExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbStatBac20AmountSqlProvider.class, method = "updateByExample")
    int updateByExample(@Param("record") TbStatBac20Amount record,
            @Param("example") TbStatBac20AmountExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @UpdateProvider(type = TbStatBac20AmountSqlProvider.class,
            method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(TbStatBac20Amount record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @Update({"update tb_stat_bac20_amount", "set issue_amount = #{issueAmount,jdbcType=BIGINT},",
            "used_amount = #{usedAmount,jdbcType=BIGINT},",
            "block_number = #{blockNumber,jdbcType=BIGINT},",
            "block_timestamp = #{blockTimestamp,jdbcType=BIGINT},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "modify_time = #{modifyTime,jdbcType=TIMESTAMP}",
            "where check_date = #{checkDate,jdbcType=VARCHAR}",
            "and check_hour = #{checkHour,jdbcType=INTEGER}"})
    int updateByPrimaryKey(TbStatBac20Amount record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @Insert({"<script>", "insert into tb_stat_bac20_amount (check_date, ",
            "check_hour, issue_amount, ", "used_amount, block_number, ",
            "block_timestamp, create_time, ", "modify_time)",
            "values<foreach collection=\"list\" item=\"detail\" index=\"index\" separator=\",\">(#{detail.checkDate,jdbcType=VARCHAR}, ",
            "#{detail.checkHour,jdbcType=INTEGER}, #{detail.issueAmount,jdbcType=BIGINT}, ",
            "#{detail.usedAmount,jdbcType=BIGINT}, #{detail.blockNumber,jdbcType=BIGINT}, ",
            "#{detail.blockTimestamp,jdbcType=BIGINT}, #{detail.createTime,jdbcType=TIMESTAMP}, ",
            "#{detail.modifyTime,jdbcType=TIMESTAMP})</foreach></script>"})
    int batchInsert(List<TbStatBac20Amount> list);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * tb_stat_bac20_amount
     *
     * @mbg.generated
     */
    @SelectProvider(type = TbStatBac20AmountSqlProvider.class, method = "getOneByExample")
    @Results({
            @Result(column = "check_date", property = "checkDate", jdbcType = JdbcType.VARCHAR,
                    id = true),
            @Result(column = "check_hour", property = "checkHour", jdbcType = JdbcType.INTEGER,
                    id = true),
            @Result(column = "issue_amount", property = "issueAmount", jdbcType = JdbcType.BIGINT),
            @Result(column = "used_amount", property = "usedAmount", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_number", property = "blockNumber", jdbcType = JdbcType.BIGINT),
            @Result(column = "block_timestamp", property = "blockTimestamp",
                    jdbcType = JdbcType.BIGINT),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "modify_time", property = "modifyTime",
                    jdbcType = JdbcType.TIMESTAMP)})
    Optional<TbStatBac20Amount> getOneByExample(TbStatBac20AmountExample example);
}

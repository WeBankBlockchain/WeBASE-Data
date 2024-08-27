package com.webank.wsbcs.chdis.base.entity.trans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fisco.bcos.sdk.abi.datatypes.Type;

import java.math.BigInteger;

/**
 * 用于兼容v2 v3的call交易的返回值，List<String>
 * @author marsli
 * @date 2023/3/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeResponse implements Type<String> {

  private String type;
  private String value;
  @Override
  public String getValue() {
    return value;
  }

  @Override
  public String getTypeAsString() {
    return type;
  }

  @Override
  public boolean dynamicType() {
    return false;
  }

  @Override
  public int offset() {
    return 0;
  }
}

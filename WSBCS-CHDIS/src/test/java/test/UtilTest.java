package test;

import org.junit.Test;
import com.webank.wsbcs.chdis.base.util.JsonUtils;

/**
 * @author marsli
 * @date 2023/4/13
 */
public class UtilTest {
  @Test
  public void testJsonEmtpyArray() {
    String funcParam = "[]";
    System.out.println(JsonUtils.toJSONString(funcParam));

  }
}

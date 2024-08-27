package test.trans;

import org.junit.Test;

/**
 * @author marsli
 * @date 2022/10/28
 */
public class BytesTest {

  @Test
  public void testLength() {
    String type0 = "bytes32";
    String type = "bytes[]";
    String type2 = "bytes[5]";
    String type3 = "bytes[][]";
    String type4 = "bytes[5][]";

    System.out.println(type0.split("\\[").length);
    System.out.println(type.split("\\[").length);
    System.out.println(type2.split("\\[").length);
    System.out.println(type3.split("\\[").length);
    System.out.println(type4.split("\\[").length);
  }
}

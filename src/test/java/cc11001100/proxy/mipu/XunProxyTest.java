package cc11001100.proxy.mipu;

import cc11001100.proxy.mipu.register.XunProxy;
import org.junit.Test;

/**
 * @author CC11001100
 */
public class XunProxyTest {

	/**
	 * 测试获取免费ip列表是否正常
	 */
	@Test
	public void testGetXunFreeProxy() {
		XunProxy.getXunFreeProxy().forEach(System.out::println);
	}

}

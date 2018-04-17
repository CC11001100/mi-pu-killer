package cc11001100.proxy.mipu;

import cc11001100.proxy.mipu.util.HttpUtil;
import org.apache.http.HttpHost;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author CC11001100
 */
public class HttpUtilTest {

	@Ignore
	@Test
	public void test_001() {

		boolean result = HttpUtil.test(new HttpHost("60.169.219.123", 32179));
		System.out.println(result);

	}

}

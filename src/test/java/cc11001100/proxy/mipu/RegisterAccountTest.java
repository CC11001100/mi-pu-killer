package cc11001100.proxy.mipu;

import cc11001100.proxy.mipu.domain.User;
import cc11001100.proxy.mipu.register.RegisterAccount;
import org.apache.http.HttpHost;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author CC11001100
 */
public class RegisterAccountTest {

	@Ignore
	@Test
	public void testRegister() {

		User user = RegisterAccount.register(new HttpHost("121.226.166.108", 27049));
		System.out.println(user);

	}

}

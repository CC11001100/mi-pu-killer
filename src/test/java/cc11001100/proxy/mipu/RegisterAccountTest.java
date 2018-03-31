package cc11001100.proxy.mipu;

import cc11001100.proxy.mipu.domain.User;
import org.junit.Test;

/**
 * @author CC11001100
 */
public class RegisterAccountTest {

	@Test
	public void testRegister() {

		User user = RegisterAccount.register();
		System.out.println(user);

	}

}

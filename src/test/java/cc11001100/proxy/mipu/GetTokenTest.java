package cc11001100.proxy.mipu;

import cc11001100.proxy.mipu.domain.User;
import cc11001100.proxy.mipu.register.GetToken;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author CC11001100
 */
public class GetTokenTest {

	@Ignore
	@Test
	public void testLogin() {
//		new GetToken(new User("13001049956@163.com", "123456qwerty")).login();
	}

	@Ignore
	@Test
	public void testGetToken() {

		String token = new GetToken(new User("13001049956@163.com", "123456qwerty")).get();
		System.out.println(token);

	}


}

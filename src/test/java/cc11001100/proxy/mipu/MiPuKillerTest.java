package cc11001100.proxy.mipu;

import cc11001100.proxy.mipu.core.MiPuKiller;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author CC11001100
 */
public class MiPuKillerTest {

	@Test
	public void test_001() {

		MiPuKiller killer = new MiPuKiller();
		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			killer.get().forEach(x -> {
				System.out.println(x.getIp() + ":" + x.getPort());
			});
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}

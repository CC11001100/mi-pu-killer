package cc11001100.proxy.mipu;

import cc11001100.proxy.mipu.core.MiPuKiller;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author CC11001100
 */
public class MiPuKillerTest {

	@Ignore
	@Test
	public void test_001() {

		MiPuKiller killer = new MiPuKiller();
		Set<String> ipSet = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			System.out.println(i);
			killer.get().forEach(x -> {
				String proxy = x.getIp() + ":" + x.getPort();
				ipSet.add(proxy);
				System.out.println(proxy);
			});
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("共有：" + ipSet.size());

	}

	@Test
	public void test_002() {

		MiPuKiller miPuKiller = new MiPuKiller();
		for (int i = 0; i < 10; i++) {
			System.out.println(System.currentTimeMillis());
			miPuKiller.get().forEach(x -> {
				System.out.println(x.getIp() + ":" + x.getPort());
			});
		}

	}


}

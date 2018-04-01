package cc11001100.proxy.mipu.core;

import cc11001100.proxy.mipu.exception.RegisterException;
import cc11001100.proxy.mipu.register.RegisterAccount;
import cc11001100.proxy.mipu.domain.Proxy;
import cc11001100.proxy.mipu.domain.User;
import cc11001100.proxy.mipu.register.XunProxy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CC11001100
 */
public class MiPuKiller {

	private static Logger logger = LogManager.getLogger(MiPuKiller.class);

	private List<User> userList;
	private static final Integer DEFAULT_USER_NUMBER = 5;

	public MiPuKiller() {
		init();
	}

	private void init() {
		JSONObject config = readConfig();
		initUserList(config);
		Runtime.getRuntime().removeShutdownHook(new Thread(() -> {


		}));
	}

	private JSONObject readConfig() {
		String tempDirectory = System.getProperty("java.io.tmpdir");
		String configFilePath = tempDirectory + "/mu_pu_killer_config.json";
		String configContent = "";
		try {
			configContent = FileUtils.readFileToString(new File(configFilePath), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.warn("Not found config file.");
		}
		return JSON.parseObject(configContent);
	}

	private void initUserList(JSONObject config) {
		userList = config.getJSONArray("users").stream().map(userWrapper -> {
			JSONObject user = (JSONObject) userWrapper;
			String name = user.getString("name");
			String passwd = user.getString("passwd");
			return new User(name, passwd);
		}).collect(Collectors.toList());

		if (userList.size() < DEFAULT_USER_NUMBER) {
			supplementUser(DEFAULT_USER_NUMBER);
		}

		config.put("users", userList);
	}

	private void supplementUser(int n) {
		List<HttpHost> proxyListForRegister = XunProxy.getXunFreeProxy();
		while (userList.size() < n) {

			if (proxyListForRegister.isEmpty()) {
				proxyListForRegister = XunProxy.getXunFreeProxy();
			}

			User user = null;
			try {
				user = RegisterAccount.register(proxyListForRegister.remove(0));
			} catch (RegisterException e) {
				e.printStackTrace();
			}
			userList.add(user);
		}
	}

	/**
	 * 调用米扑代理接口，返回ip
	 *
	 * @return
	 */
	public List<Proxy> get() {

		return null;
	}


}

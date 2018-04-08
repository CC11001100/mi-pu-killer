package cc11001100.proxy.mipu.core;

import cc11001100.proxy.mipu.domain.Proxy;
import cc11001100.proxy.mipu.domain.User;
import cc11001100.proxy.mipu.exception.RegisterException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cc11001100.proxy.mipu.register.RegisterAccount.register;
import static cc11001100.proxy.mipu.register.XunProxy.getXunFreeProxy;
import static cc11001100.proxy.mipu.util.HttpUtil.getJson;
import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;
import static com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.readFileToString;

/**
 * @author CC11001100
 */
public class MiPuKiller {

	private static Logger logger = LogManager.getLogger(MiPuKiller.class);

	private static final Integer DEFAULT_USER_NUMBER = 3;
	private static final Integer MAX_EFFECT_HOURS = 5;

	private List<User> userList;
	private Integer userListIndex = 0;
	private Integer useHowManyUser = DEFAULT_USER_NUMBER;

	public MiPuKiller() {
		init();
	}

	private void init() {
		JSONObject config = readConfig();
		initUserList(config);
		registerSaveConfigHook(config);
	}

	private String getConfigFilePath() {
		String tempDirectory = System.getProperty("java.io.tmpdir");
		return tempDirectory + "mi_pu_killer\\config.json";
	}

	private JSONObject readConfig() {
		String configContent = "";
		try {
			configContent = readFileToString(new File(getConfigFilePath()), UTF_8);
		} catch (IOException e) {
			logger.warn("Not found config file {}", getConfigFilePath());
		}
		return parseObject(configContent);
	}

	private void initUserList(JSONObject config) {
		if (config == null) {
			// first, no config exists
			userList = new ArrayList<>();
		} else {
			// recovery
			userList = ofNullable(config.getJSONArray("users")).orElse(new JSONArray())
					.stream().map(userWrapper -> parseObject(userWrapper.toString(), User.class))
					.collect(toList());
		}

		if (userList.size() < useHowManyUser) {
			supplementUser(useHowManyUser);
		}
	}

	private void registerSaveConfigHook(JSONObject config) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			// save config
			JSONObject configToSave = ofNullable(config).orElse(new JSONObject());
			configToSave.put("users", userList);
			String configJsonString = toJSONString(configToSave, WriteMapNullValue, PrettyFormat);
			try {
				FileUtils.writeStringToFile(new File(getConfigFilePath()), configJsonString, UTF_8);
				logger.info("config file save success.");
			} catch (IOException e) {
				logger.info("save config file failed");
			}
		}));
	}

	private void supplementUser(int n) {
		List<HttpHost> proxyListForRegister = emptyList();
		while (userList.size() < n) {

			if (proxyListForRegister.isEmpty()) {
				logger.info("get xun proxy");
				proxyListForRegister = getXunFreeProxy();
				if (proxyListForRegister.isEmpty()) {
					throw new RegisterException("no proxy can use.");
				}
			}
			HttpHost proxy = getXunFreeProxy().remove(0);
			logger.info("use proxy {}:{} register", proxy.getHostName(), proxy.getPort());

			try {
				User user = register(proxy);
				userList.add(user);
				logger.info("register user {}", user.getName());
			} catch (RegisterException e) {
				logger.info("register exception, mesg={}", e.getMessage());
			}
		}
	}

	/**
	 * 调用米扑代理接口，返回ip
	 *
	 * @return
	 */
	public List<Proxy> get() {

		String url = "https://proxyapi.mimvp.com/api/fetchopen.php?orderid=%s&num=20&result_fields=1,2,10,3,4,5,6,7,8,9&result_format=json";

		while (true) {

			// 补充弹药
			if (userList.size() < useHowManyUser) {
				supplementUser(useHowManyUser);
			}

			userListIndex = (userListIndex + 1) % userList.size();
			User user = userList.get(userListIndex);

			// 检查当前取出的用户有效性，无效则移除
			if (!checkUser(user)) {
				userList.remove(user);
				continue;
			}

			JSONObject json = getJson("GET", String.format(url, user.getToken()));
			int code = json.getIntValue("code");

			if (code == 0) {
				// 调用成功
				return extractResult(json);
			} else if (code == 13) {
				// 调用太频繁
				logger.info("{} code 13, to fast, next.", user.getName());
			} else {
				// 不管，直接认为它死掉了
				logger.info("{} response={}, he is died.", user.getName(), json.toString());
				user.setDied(true);
			}
		}
	}

	private static boolean checkUser(User user) {
		// 过期时间检查
		LocalDateTime expireTime = LocalDateTime.now().plusHours(MAX_EFFECT_HOURS);
		if (expireTime.isAfter(user.getRegisterDate())) {
			user.setDied(true);
		}

		return user.getDied();
	}

	private static List<Proxy> extractResult(JSONObject json) {
		return json.getJSONArray("result").stream().map(xWrapper -> {
			JSONObject x = (JSONObject) xWrapper;
			Proxy proxy = new Proxy();

			proxy.setProtocolStatus(x.getString("protocol_status"));
			proxy.setTransferTime(x.getDouble("transfer_time"));
			proxy.setPingTime(x.getDouble("ping_time"));
			proxy.setCheckSuccessCount(x.getInteger("check_success_count"));
			proxy.setCheckTime(x.getString("check_dtime"));
			proxy.setHttpType(x.getString("http_type"));
			proxy.setAnonymous(x.getString("anonymous"));
			proxy.setIsp(x.getString("isp"));
			proxy.setCountry(x.getString("country"));

			String[] ipAndPort = x.getString("ip:port").split(":");
			proxy.setIp(ipAndPort[0]);
			proxy.setPort(Integer.parseInt(ipAndPort[1]));
			return proxy;
		}).collect(toList());
	}

}

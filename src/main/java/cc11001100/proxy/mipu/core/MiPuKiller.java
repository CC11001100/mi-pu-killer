package cc11001100.proxy.mipu.core;

import cc11001100.proxy.mipu.domain.Proxy;
import cc11001100.proxy.mipu.domain.User;
import cc11001100.proxy.mipu.exception.RegisterException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import static org.apache.commons.io.FileUtils.writeStringToFile;

/**
 * @author CC11001100
 */
public class MiPuKiller {

	private static Logger logger = LogManager.getLogger(MiPuKiller.class);

	/**
	 * 默认使用的用户数，因为返回的会有很多重复的，所以这个设置大了也没什么用
	 */
	private static final Integer DEFAULT_USER_NUMBER = 1;

	/**
	 * 每个账户注册之后5个小时内可以用来获取ip，超时则账户作废
	 */
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
		registerSaveConfigHook();
	}

	/**
	 * 配置文件默认保存在用户的临时目录
	 *
	 * @return
	 * @apiNote windows环境测试通过，其它环境未测试
	 */
	private String getConfigFilePath() {
		String tempDirectory = System.getProperty("java.io.tmpdir");
		return tempDirectory + "mi_pu_killer\\config.json";
	}

	/**
	 * 为了避免频繁的注册账号，用一个配置文件来保存当前注册的有效的账号
	 * 每次启动前优先读取使用配置文件中的账户，当不够时再去注册
	 *
	 * @return
	 */
	private JSONObject readConfig() {
		String configFilePath = getConfigFilePath();
		try {
			String configContent = readFileToString(new File(configFilePath), UTF_8);
			logger.info("recovery config from {}", configFilePath);
			return parseObject(configContent);
		} catch (IOException e) {
			logger.warn("Not found config file {}", getConfigFilePath());
			return null;
		}
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
			userList.forEach(user -> logger.info("read user {} from config", user.getName()));
		}

		// 如果配置文件中没有足够的账户，则注册新的账号直到达到要求
		if (userList.size() < useHowManyUser) {
			supplementUser(useHowManyUser);
		}
	}

	private void registerSaveConfigHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			JSONObject configToSave = new JSONObject();
			configToSave.put("users", userList);
			String configJsonString = toJSONString(configToSave, WriteMapNullValue, PrettyFormat);
			try {
				writeStringToFile(new File(getConfigFilePath()), configJsonString, UTF_8);
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
			}

			HttpHost proxy = null;
			if (!proxyListForRegister.isEmpty()) {
				proxy = proxyListForRegister.remove(0);
				logger.info("use proxy {}:{} register", proxy.getHostName(), proxy.getPort());
			}

			try {
				User user = register(proxy);
				userList.add(user);
				logger.info("register success, username={}", user.getName());
			} catch (RegisterException e) {
				logger.info("register exception, mesg={}", e.getMessage());
			}
		}
	}

	/**
	 * 默认使用的获取ip的方式
	 *
	 * @return
	 */
	public List<Proxy> get() {
		String url = "https://proxyapi.mimvp.com/api/fetchopen.php?orderid=%s&num=20&filter_hour=12&result_fields=1,2,10,3,4,5,6,7,8,9&result_format=json";
		return get(url);
	}

	/**
	 * 可以自定义参数来调用接口
	 *
	 * @return
	 */
	public List<Proxy> get(String url) {
		while (true) {

			// 补充弹药
			if (userList.size() < useHowManyUser) {
				supplementUser(useHowManyUser);
			}

			userListIndex = (userListIndex + 1) % userList.size();
			User user = userList.get(userListIndex);

			// 检查当前取出的用户有效性，无效则移除
			if (checkUser(user)) {
				userList.remove(user);
				continue;
			} else if (user.getLastGet().plusSeconds(10).isAfter(LocalDateTime.now())) {
				// 用户是有效的，同时上次调用时间与当前时间间隔超过10秒，调用频繁不允许
				continue;
			}

			JSONObject json = getJson("GET", String.format(url, user.getToken()));
			if(json==null){
				logger.info("get api response null");
				continue;
			}
			int code = json.getIntValue("code");
			user.setLastGet(LocalDateTime.now());

			if (code == 0) {
				// 调用成功
				return extractResult(json);
			} else if (code == 13) {
				// 调用太频繁
				logger.info("{} code 13, to fast.", user.getName());
			} else if (code == 14) {
				// 调用太频繁被封禁掉了,推荐一分钟调用一次...
				logger.info("code=14");
			} else {
				// 不管，直接认为它死掉了
				logger.info("{} response={}, he is died.", user.getName(), json.toString());
				user.setDied(true);
			}
		}
	}

	private static boolean checkUser(User user) {
		// 过期时间检查
		LocalDateTime expireTime = user.getRegisterDate().plusHours(MAX_EFFECT_HOURS);
		if (LocalDateTime.now().isAfter(expireTime)) {
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

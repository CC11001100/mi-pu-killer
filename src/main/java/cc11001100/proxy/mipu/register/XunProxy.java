package cc11001100.proxy.mipu.register;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static cc11001100.proxy.mipu.util.HttpUtil.getBytes;
import static cc11001100.proxy.mipu.util.HttpUtil.getJson;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * 讯代理公开的免费代理是免费代理中质量比较好的，因此采用它用来作为注册时的代理来躲避注册频繁封IP
 *
 * @author CC11001100
 */
public class XunProxy {

	private static Logger logger = LogManager.getLogger(XunProxy.class);

	public static List<HttpHost> getXunFreeProxy() {
		JSONObject json = getJson("GET", "http://www.xdaili.cn/ipagent/freeip/getFreeIps");
		if (json.getIntValue("ERRORCODE") != 0) {
			logger.error("Get Xun-Proxy free ip list failed. response={}", json.toString());
			return emptyList();
		}

		List<HttpHost> proxyList = json.getJSONObject("RESULT").getJSONArray("rows").stream().map(x -> {
			JSONObject y = (JSONObject) x;
			return new HttpHost(y.getString("ip"), y.getIntValue("port"));
		}).collect(toList());

		return check(proxyList);
	}

	private static List<HttpHost> check(List<HttpHost> proxyList) {
		int threadNum = Runtime.getRuntime().availableProcessors() * 3;
		ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

		ConcurrentHashMap<HttpHost, Object> placeholder = new ConcurrentHashMap<>();
		proxyList.forEach(x -> {
			executorService.execute(() -> {
				if (null != getBytes("GET", "http://www.baidu.com/", x, null, 1)){
					placeholder.put(x, 1);
				}
			});
		});
		try {
			executorService.shutdown();
			executorService.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return placeholder.entrySet().stream().map(Map.Entry::getKey).collect(toList());
	}

}

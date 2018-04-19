package cc11001100.proxy.mipu.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author CC11001100
 */
public class HttpUtil {

	private static final Logger logger = LogManager.getLogger(HttpUtil.class);

	/**
	 * 请求的最大失败重试次数，当达到此重试次数时直接返回null认为请求失败
	 */
	private static Integer MAX_RETRY = 5;

	/**
	 * 每次响应的cookie记下来，在之后的每次请求再带上，模拟浏览器的部分cookie功能
	 */
	private static Map<String, String> cookieMap = new HashMap<>();

	public static byte[] getBytes(String method, String url, HttpHost proxy, List<NameValuePair> paramsList, int times) {
		for (int i = 1; i <= times; i++) {
			Request request = null;

			// method
			if ("GET".equalsIgnoreCase(method)) {
				if (paramsList != null && !paramsList.isEmpty()) {
					url = url + "?" + paramsList.stream()
							.map(x -> x.getName() + "=" + x.getValue())
							.collect(Collectors.joining("&"));
				}
				request = Request.Get(url);
			} else if ("POST".equalsIgnoreCase(method)) {
				request = Request.Post(url);
				if (paramsList != null && !paramsList.isEmpty()) {
					request.bodyForm(paramsList);
				}
			} else if ("HEAD".equals(method)) {
				request = Request.Head(url);
			} else {
				throw new IllegalArgumentException(method);
			}

			// 代理
			if (proxy != null) {
				request.viaProxy(proxy);
			}

			// User-Agent先简单的写
			request.addHeader("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36");
			request.addHeader("Cookie", joinCookie());
			request.connectTimeout(1000 * 10).socketTimeout(1000 * 10);

			HttpResponse httpResponse = null;
			try {
				httpResponse = request.execute().returnResponse();
			} catch (IOException e) {
				logger.info("io exception, url={}, try_times={}", url, i);
				continue;
			}

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				logger.info("url={}, http status={}, try_times={}", url, statusCode, i);
				continue;
			}

			// 存储cookie
			Arrays.stream(httpResponse.getHeaders("Set-Cookie")).forEach(header -> {
				String[] kv = header.getValue().split(";")[0].split("=");
				cookieMap.put(kv[0], kv[1]);
			});

			try {
				return IOUtils.toByteArray(httpResponse.getEntity().getContent());
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	public static byte[] getBytes(String method, String url, HttpHost proxy, List<NameValuePair> paramsList) {
		return getBytes(method, url, proxy, paramsList, MAX_RETRY);
	}

	public static String getText(String method, String url, HttpHost proxy, List<NameValuePair> paramsList) {
		byte[] contentBytes = getBytes(method, url, proxy, paramsList);
		if (contentBytes == null) {
			return "";
		} else {
			return new String(contentBytes, UTF_8);
		}
	}

	public static JSONObject getJson(String method, String url, HttpHost proxy, List<NameValuePair> paramsList) {
		String contentText = getText(method, url, proxy, paramsList);
		return JSON.parseObject(contentText);
	}

	public static JSONObject getJson(String method, String url) {
		return getJson(method, url, null, null);
	}

	public static void clearCookie() {
		cookieMap.clear();
	}

	private static String joinCookie() {
		return cookieMap.entrySet().stream()
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("; "));
	}

	/**
	 * 用于测试代理的可用性
	 *
	 * @param proxy
	 * @return
	 */
	public static boolean test(HttpHost proxy) {
		String url = "http://www.baidu.com/";
		try {
			HttpResponse httpResponse = Request.Head(url).viaProxy(proxy)
					.connectTimeout(1000 * 5).socketTimeout(1000 * 30)
					.execute().returnResponse();
			return httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
		} catch (IOException ignored) {
		}
		return false;
	}

}
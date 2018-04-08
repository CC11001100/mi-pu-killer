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
	public static Integer MAX_RETRY = 5;
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
			} else {
				throw new IllegalArgumentException(method);
			}

			// 代理
			if (proxy != null) {
				request.viaProxy(proxy);
			}

			// User-Agent先简单的写
			request.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.119 Safari/537.36");
			request.addHeader("Cookie", joinCookie());
			request.connectTimeout(1000 * 10).socketTimeout(1000 * 10);

			try {
				HttpResponse httpResponse = request.execute().returnResponse();

				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					logger.info("url={}, http status={}, try_times={}", url, statusCode, i);
					continue;
				}

				Arrays.stream(httpResponse.getHeaders("Set-Cookie")).forEach(header -> {
					String[] kv = header.getValue().split(";")[0].split("=");
					cookieMap.put(kv[0], kv[1]);
				});

				return IOUtils.toByteArray(httpResponse.getEntity().getContent());
			} catch (IOException e) {
				logger.info("io exception, url={}, try_times={}", url, i);
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

}
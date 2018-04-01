package cc11001100.proxy.mipu.register;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 讯代理公开的免费代理是免费代理中质量比较好的，因此采用它用来作为注册时的代理来躲避注册频繁封IP
 *
 * @author CC11001100
 */
public class XunProxy {

	private static Logger logger = LogManager.getLogger(XunProxy.class);

	public static List<HttpHost> getXunFreeProxy() {
		try {
			String responseContent = Request.Get("http://www.xdaili.cn/ipagent/freeip/getFreeIps").execute().returnContent().toString();
			JSONObject json = JSON.parseObject(responseContent);

			if (json.getIntValue("ERRORCODE") != 0) {
				logger.error("Get Xun-Proxy free ip list failed.");
				return Collections.emptyList();
			}

			return json.getJSONObject("RESULT").getJSONArray("rows").stream().map(x -> {
				JSONObject y = (JSONObject) x;
				return new HttpHost(y.getString("ip"), y.getIntValue("port"));
			}).collect(toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

}

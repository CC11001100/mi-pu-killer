package cc11001100.proxy.mipu.register;

import cc11001100.proxy.mipu.domain.User;
import cc11001100.proxy.mipu.exception.LoginException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author CC11001100
 */
public class GetToken {

	private static final Logger logger = LogManager.getLogger(GetToken.class);

	private Map<String, String> cookieMap = new HashMap<>();

	private User user;

	public GetToken(User user) {
		this.user = user;
	}

	public String get() {
		login();
		return parseToken();
	}

	private void login() {
		String url = "https://proxy.mimvp.com/lib/user_login_check.php";
		try {
			HttpResponse response = Request.Post(url).bodyForm(
					new BasicNameValuePair("user_email", user.getName()),
					new BasicNameValuePair("user_pwd", user.getPasswd()),
					new BasicNameValuePair("remember", Integer.toString(new Random().nextInt(1))))
					.execute().returnResponse();
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new LoginException(user.toString());
			}
			Arrays.stream(response.getHeaders("Set-Cookie")).forEach(header -> {
				String[] kv = header.getValue().split(";")[0].split("=");
				cookieMap.put(kv[0], kv[1]);
			});
			logger.info(user.getName() + " login success, cookie=" + joinCookie());
		} catch (IOException e) {
			logger.info(user.getName() + " login failed");
		}
	}

	private String parseToken() {
		String url = "https://proxy.mimvp.com/usercenter/userinfo.php";
		try {
			String htmlContent = Request.Get(url).addHeader("Cookie", joinCookie()).execute().returnContent().toString();
			Matcher matcher = Pattern.compile("orderdetail.php\\?orderid=([0-9]+)").matcher(htmlContent);
			if (matcher.find()) {
				String token = matcher.group(1);
				logger.info(user.getName() + " get api token=" + token);
				return token;
			}
		} catch (IOException e) {
			logger.info(user.getName() + " get api token failed");
		}
		return null;
	}

	private String joinCookie() {
		return cookieMap.entrySet().stream()
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("; "));
	}

}

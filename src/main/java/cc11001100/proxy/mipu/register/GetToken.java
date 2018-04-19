package cc11001100.proxy.mipu.register;

import cc11001100.proxy.mipu.domain.User;
import cc11001100.proxy.mipu.exception.AccountException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc11001100.proxy.mipu.util.HttpUtil.*;

/**
 * @author CC11001100
 */
public class GetToken {

	private static final Logger logger = LogManager.getLogger(GetToken.class);
	private static final String LOGIN_URL = "https://proxy.mimvp.com/lib/user_login_check.php";
	private static final String USER_INFO_URL = "https://proxy.mimvp.com/usercenter/userinfo.php";

	private User user;
	private HttpHost proxy;

	public GetToken(String username, String passwd, HttpHost proxy) {
		user = new User(username, passwd);
		this.proxy = proxy;
	}

	public GetToken(String username, String passwd) {
		user = new User(username, passwd);
	}

	public GetToken(User user) {
		this.user = user;
	}

	public String get() {
		clearCookie();
		login();
		String token = parseToken();
		clearCookie();
		return token;
	}

	private void login() {
		List<NameValuePair> paramList = Arrays.asList(new BasicNameValuePair("user_email", user.getName()),
				new BasicNameValuePair("user_pwd", user.getPasswd()),
				new BasicNameValuePair("remember", Integer.toString(new Random().nextInt(1))));
		JSONObject json = getJson("POST", LOGIN_URL, proxy, paramList);
		if (json == null) {
			throw new AccountException("login response null");
		} else if (json.getIntValue("code") != 0) {
			throw new AccountException(json.toString());
		}
	}

	private String parseToken() {
		String htmlContent = getText("POST", USER_INFO_URL, proxy, null);
		Matcher matcher = Pattern.compile("orderdetail.php\\?orderid=([0-9]+)").matcher(htmlContent);
		if (matcher.find()) {
			String token = matcher.group(1);
			logger.info("{} get api token={}", user.getName(), token);
			return token;
		} else {
			logger.info("{} get api token failed", user.getName());
			return null;
		}
	}

}

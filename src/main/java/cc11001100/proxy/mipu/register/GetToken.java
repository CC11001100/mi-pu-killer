package cc11001100.proxy.mipu.register;

import cc11001100.proxy.mipu.domain.User;
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

import static cc11001100.proxy.mipu.util.HttpUtil.clearCookie;
import static cc11001100.proxy.mipu.util.HttpUtil.getText;

/**
 * @author CC11001100
 */
public class GetToken {

	private static final Logger logger = LogManager.getLogger(GetToken.class);
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
		login();
		String token = parseToken();
		clearCookie();
		return token;
	}

	private void login() {
		String url = "https://proxy.mimvp.com/lib/user_login_check.php";
		List<NameValuePair> paramList = Arrays.asList(new BasicNameValuePair("user_email", user.getName()),
				new BasicNameValuePair("user_pwd", user.getPasswd()),
				new BasicNameValuePair("remember", Integer.toString(new Random().nextInt(1))));
		getText("POST", url, proxy, paramList);
	}

	private String parseToken() {
		String url = "https://proxy.mimvp.com/usercenter/userinfo.php";
		String htmlContent = getText("POST", url, proxy, null);
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

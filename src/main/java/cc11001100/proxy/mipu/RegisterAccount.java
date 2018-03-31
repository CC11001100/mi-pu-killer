package cc11001100.proxy.mipu;

import cc11001100.ocr.OcrUtil;
import cc11001100.ocr.clean.SingleColorFilterClean;
import cc11001100.proxy.mipu.domain.User;
import cc11001100.proxy.mipu.exception.RegisterException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author CC11001100
 */
public class RegisterAccount {

	private static String[] email = {
		"qq.com",
		"163.com",
		"foo.com"
	};

	private static String passwdChar = "qwejzxcbnmWERXVBNM1234567890,.,.,.,.";

	private static OcrUtil ocrUtil;

	static {
		Map<Integer, String> dictionaryMap = new HashMap<>();
		dictionaryMap.put(959516722, "g");
		dictionaryMap.put(-975656737, "W");
		dictionaryMap.put(991772180, "7");
		dictionaryMap.put(1056940531, "H");
		dictionaryMap.put(-513393294, "j");
		dictionaryMap.put(364918742, "P");
		dictionaryMap.put(-1326696080, "s");
		dictionaryMap.put(-1492289902, "A");
		dictionaryMap.put(1079876477, "e");
		dictionaryMap.put(1919911499, "l");
		dictionaryMap.put(1844447189, "T");
		dictionaryMap.put(1296237030, "t");
		dictionaryMap.put(2118580974, "0");
		dictionaryMap.put(-1872809481, "e");
		dictionaryMap.put(-1357610564, "k");
		dictionaryMap.put(1966750992, "o");
		dictionaryMap.put(1071826642, "a");
		dictionaryMap.put(-2017078409, "U");
		dictionaryMap.put(-341284837, "v");
		dictionaryMap.put(-133374278, "Q");
		dictionaryMap.put(2098689583, "F");
		dictionaryMap.put(801504892, "1");
		dictionaryMap.put(2070135642, "r");
		dictionaryMap.put(738095943, "z");
		dictionaryMap.put(-2111995610, "s");
		dictionaryMap.put(-1773619830, "b");
		dictionaryMap.put(64500762, "K");
		dictionaryMap.put(1681842682, "d");
		dictionaryMap.put(-2027039899, "D");
		dictionaryMap.put(-2146329128, "2");
		dictionaryMap.put(-1611212225, "8");
		dictionaryMap.put(508038785, "G");
		dictionaryMap.put(1958376179, "N");
		dictionaryMap.put(-909604733, "c");
		dictionaryMap.put(147787394, "5");
		dictionaryMap.put(1159034971, "i");
		dictionaryMap.put(-1210164977, "M");
		dictionaryMap.put(-556811108, "b");
		dictionaryMap.put(1372788100, "z");
		dictionaryMap.put(2129309878, "p");
		dictionaryMap.put(1638077567, "x");
		dictionaryMap.put(-1409623513, "h");
		dictionaryMap.put(-1232357546, "j");
		dictionaryMap.put(1606699556, "q");
		dictionaryMap.put(2029187984, "3");
		dictionaryMap.put(-1132071961, "l");
		dictionaryMap.put(1192828412, "n");
		dictionaryMap.put(34655862, "c");
		dictionaryMap.put(-1534126490, "B");
		dictionaryMap.put(-1244124495, "w");
		dictionaryMap.put(998302826, "E");
		dictionaryMap.put(1159492848, "y");
		dictionaryMap.put(1800847780, "u");
		dictionaryMap.put(271015253, "X");
		dictionaryMap.put(-1908552626, "A");
		dictionaryMap.put(923756923, "4");
		dictionaryMap.put(-387010790, "m");
		dictionaryMap.put(1414897186, "6");
		dictionaryMap.put(-647204197, "V");
		dictionaryMap.put(-946835733, "o");
		dictionaryMap.put(-910000322, "R");
		dictionaryMap.put(1859720356, "9");
		dictionaryMap.put(-1018498505, "f");
		dictionaryMap.put(955124241, "Y");

		ocrUtil = new OcrUtil().setImageClean(new SingleColorFilterClean());
		ocrUtil.loadDictionaryMap(dictionaryMap);
	}

	public static User register() {
		String name = randomName();
		String passwd = randomPasswd();
		return register(name, passwd);
	}

	public static User register(String name, String passwd) {

		try {
			byte[] imgBytes = Request.Get("https://proxy.mimvp.com/common/ygrcode.php").execute().returnContent().asBytes();
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
			String rcode = ocrUtil.ocr(img);
			String responseContent = Request.Post("https://proxy.mimvp.com/lib/user_regist_check.php")
				.bodyForm(new BasicNameValuePair("user_email", name),
					new BasicNameValuePair("user_pwd", passwd),
					new BasicNameValuePair("user_rcode", rcode),
					new BasicNameValuePair("forurl", "login.php"))
				.execute().returnContent().toString();
			JSONObject json = JSON.parseObject(responseContent);
			if (json.getIntValue("code") != 0) {
				throw new RegisterException(responseContent);
			}

			return new User(name, passwd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new User();
	}

	private static String randomName() {
		Random random = new Random();
		StringBuilder name = new StringBuilder();
		name.append(Integer.toString(random.nextInt(999999999) + 1000000000));
		name.append("@").append(email[random.nextInt(email.length)]);
		return name.toString();
	}

	private static String randomPasswd() {
		Random random = new Random();
		StringBuilder passwd = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			passwd.append(passwdChar.charAt(random.nextInt(passwdChar.length())));
		}
		return passwd.toString();
	}

}

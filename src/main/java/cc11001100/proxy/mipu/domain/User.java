package cc11001100.proxy.mipu.domain;

import java.util.Date;

/**
 * @author CC11001100
 */
public class User {

	private String name;
	private String passwd;
	private String token;
	private Date registerDate;

	public User() {
	}

	public User(String name, String passwd) {
		this.name = name;
		this.passwd = passwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", passwd='" + passwd + '\'' +
				", token='" + token + '\'' +
				", registerDate=" + registerDate +
				'}';
	}
}

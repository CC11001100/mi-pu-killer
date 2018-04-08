package cc11001100.proxy.mipu.domain;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author CC11001100
 */
public class User {

	private String name;
	private String passwd;
	private String token;
	private LocalDateTime registerDate;
	private Boolean isDied;

	public User() {
	}

	public User(String name, String passwd) {
		this.name = name;
		this.passwd = passwd;
	}

	public User(String name, String passwd, String token, LocalDateTime registerDate) {
		this.name = name;
		this.passwd = passwd;
		this.token = token;
		this.registerDate = registerDate;
	}

	public Boolean getDied() {
		return isDied;
	}

	public void setDied(Boolean died) {
		isDied = died;
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

	public LocalDateTime getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(LocalDateTime registerDate) {
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

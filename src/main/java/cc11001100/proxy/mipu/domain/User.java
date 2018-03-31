package cc11001100.proxy.mipu.domain;

/**
 * @author CC11001100
 */
public class User {

	private String name;
	private String passwd;

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

	@Override
	public String toString() {
		return "User{" +
			"name='" + name + '\'' +
			", passwd='" + passwd + '\'' +
			'}';
	}
}

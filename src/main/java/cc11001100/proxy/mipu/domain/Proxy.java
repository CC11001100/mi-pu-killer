package cc11001100.proxy.mipu.domain;

/**
 * @author CC11001100
 */
public class Proxy {

	private String ip;
	private Integer port;
	private String httpType;
	private String anonymous;
	private String isp;
	private String country;
	private String checkTime;
	private String protocolStatus;
	private Double pingTime;
	private Double transferTime;
	private Integer checkSuccessCount;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getHttpType() {
		return httpType;
	}

	public void setHttpType(String httpType) {
		this.httpType = httpType;
	}

	public String getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(String anonymous) {
		this.anonymous = anonymous;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public String getProtocolStatus() {
		return protocolStatus;
	}

	public void setProtocolStatus(String protocolStatus) {
		this.protocolStatus = protocolStatus;
	}

	public Double getPingTime() {
		return pingTime;
	}

	public void setPingTime(Double pingTime) {
		this.pingTime = pingTime;
	}

	public Double getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(Double transferTime) {
		this.transferTime = transferTime;
	}

	public Integer getCheckSuccessCount() {
		return checkSuccessCount;
	}

	public void setCheckSuccessCount(Integer checkSuccessCount) {
		this.checkSuccessCount = checkSuccessCount;
	}
}

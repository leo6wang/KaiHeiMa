package com.kaiheima;

public class User {
	private String userId;
	private String pwd;
	private String nickName;
	private String sex;
    private	String head;
    private long lastModityTime;
    
	public long getLastModityTime() {
        return lastModityTime;
    }
    public void setLastModityTime(long lastModityTime) {
        this.lastModityTime = lastModityTime;
    }
    public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
}

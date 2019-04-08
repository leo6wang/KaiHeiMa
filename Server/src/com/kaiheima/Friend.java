package com.kaiheima;


public class Friend{
	private String friendID;       //好友ID
	private String friendName;     //好友名字
	private String head;           //好友头像路径
	private String headModifyTime; //头像的时间戳 
	private String sex;       
	private int type;		     //最后一条消息类型
	private String content;      //最后一条信息的内容
	private String time;         //最后一条信息的时间
	
	public Friend(){
		
	}
	
	public String getHeadModifyTime() {
        return headModifyTime;
    }

    public void setHeadModifyTime(String headModifyTime) {
        this.headModifyTime = headModifyTime;
    }

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getSex() {
		return sex;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getFriendID() {
		return friendID;
	}
	public void setFriendID(String friendID) {
		this.friendID = friendID;
	}
	public String getFriendName() {
		return friendName;
	}
	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}

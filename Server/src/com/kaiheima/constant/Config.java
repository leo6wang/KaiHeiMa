package com.kaiheima.constant;

public interface Config {
	public static final int MESSAGE_FROM=-2;
	public static final int MESSAGE_TO=-1;
	public static final int MESSAGE_TYPE_TXT=0;
	public static final int MESSAGE_TYPE_IMG=1;
	public static final int MESSAGE_TYPE_AUDIO=2;
	public static final int MESSAGE_TYPE_ADD_FRIEND=3;
	
	public static final int REQUEST_LOGIN=13;
	public static final int REQUEST_REGIST=14;
	public static final int REQUEST_UPDATE_HEAD=15;
	public static final int REQUEST_ADD_FRIEND=16;
	public static final int REQUEST_SEND_TXT=17;
	public static final int REQUEST_SEND_IMG=18;
	public static final int REQUEST_SEND_AUDIO=19;
	public static final int REQUEST_GET_OFFLINE_MSG=20;      //获取未发送的消息（暂存在服务器端）
	public static final int REQUEST_GET_FRIENDS=21;
	public static final int REQUEST_SEARCH_USER=22;
	public static final int REQUEST_EXIT=23;
	public static final int REQUEST_GET_HEAD=24;
	public static final int REQUEST_GET_USER=25;
	
	public static final int RESULT_LOGIN=100;
	public static final int RESULT_REGIST=101;
	public static final int RESULT_UPDATE_HEAD=102;
	public static final int RESULT_GET_OFFLINE_MSG=103;
	public static final int RESULT_GET_FRIENDS=104;
	public static final int RESULT_SEARCH_USER=105;
	public static final int RESULT_GET_HEAD=106;
	
	public static final int RECEIVE_TEXT=500;
	public static final int RECEIVE_AUDIO=501;
	public static final int RECEIVE_IMG=502;
	
	public static final int IMG_NEED_UPDATE=600;
	public static final int IMG_NO_UPDATE=601;
	
	
	public static final int LOGIN_SUCCESS=1000;
	public static final int LOGIN_FAILED=1001;
	public static final int RIGEST_SUCCESS=1002;
	public static final int RIGEST_FAILED=1003;
	public static final int SEARCH_USER_SUCCESS=1004;
	public static final int SEARCH_USER_FALSE=1005;
	public static final int USER_HAS_IMG=1006;
	public static final int USER_NOT_IMG=1007;
	public static final int ADD_FRIEND=1008;
	public static final int REGIST_HAS_IMG=1009;
	
	public static final int ADD_NEWS=1020;
	public static final int NEWS_NEED_UPDATE=1021;
	public static final int NEWS_NO_UPDATE=1022;
	public static final int RESULT_GET_NEWS=110;
	
}

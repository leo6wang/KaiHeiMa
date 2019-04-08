package com.kaiheima.net;

import com.kaiheima.DbUtil;
import com.kaiheima.FileUtil;
import com.kaiheima.Friend;
import com.kaiheima.LogUtil;
import com.kaiheima.Message;
import com.kaiheima.News;
import com.kaiheima.User;
import com.kaiheima.constant.Config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务器端进行消息转发的Task
 * @author hu
 *
 */
public class ForwardTask extends Task{
	static HashMap<String, Socket> map=new HashMap<String, Socket>();
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	DbUtil dbUtil;
	LogUtil log;
	private boolean onWork=true;
	
	public ForwardTask(Socket socket){
		this.socket=socket;
		log=new LogUtil();
		
		dbUtil=new DbUtil();
		
		try {
			dis=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dos=new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Task[] taskCore() throws Exception {
		return null;
	}

	@Override
	protected boolean useDb() {
		return false;
	}

	@Override
	protected boolean needExecuteImmediate() {
		return false;
	}

	@Override
	public String info() {
		return null;
	}
	
	/**
	 * 设置线程工作状态，true表示运行，false表示将关闭
	 * @param state
	 */
	private void setWorkState(boolean state){
		onWork=state;
	}
	
	 /**
	  * 任务执行入口
	  */
	public void run() {
		while(onWork){
			//读数据
			try{
				receiveMsg();
			}catch(Exception e){   //发生异常————通常是连接断开，则跳出循环，一个Task任务执行完毕
				e.printStackTrace();
				break;
			}
		}
		
		try{
			if(socket!=null)
				socket.close();
			if(dis!=null)
				dis.close();
			if(dos!=null)
				dos.close();
			
			socket=null;
			dis=null;
			dos=null;
		}catch (IOException e) {
			LogUtil.record("在线用户退出时发生异常");
		}
	}
	
	// 接收消息
	public void receiveMsg() throws IOException {
		// 读取请求类型，登录，注册，更新头像等等
		int requestType = dis.readInt();
		switch (requestType) {
		case Config.REQUEST_LOGIN:      //处理“登陆”请求
			handLogin();
			break;
		case Config.REQUEST_REGIST:     //处理“注册”请求
		    handRegist();
			break;
		case Config.REQUEST_ADD_FRIEND:  //处理“添加好友”请求
			handAddFriend();
			break;
		case Config.REQUEST_UPDATE_HEAD:  //处理“更新头像”请求
		    handUpdateHead();
			break;
		case Config.REQUEST_GET_HEAD:     //处理“获取头像”请求
		    handGetHead();
		    break;
		case Config.REQUEST_SEND_TXT:     //处理“发送文本消息”请求
			handSendText();
			break;
		case Config.REQUEST_SEND_IMG:     //处理“发送图片消息”请求
		    handSendImgOrAudio(Config.RECEIVE_IMG, Config.MESSAGE_TYPE_IMG);
			break;
		case Config.REQUEST_SEND_AUDIO:  //处理“发送语音消息”请求
		    handSendImgOrAudio(Config.RECEIVE_AUDIO, Config.MESSAGE_TYPE_AUDIO);
			break;
		case Config.REQUEST_GET_OFFLINE_MSG:   //处理“获取离线消息”请求
			handGetOfflineMsg();
			break;
		case Config.REQUEST_GET_FRIENDS:       //处理“获取好友列表”请求
			handGetFriends();
			break;
		case Config.REQUEST_SEARCH_USER:       //处理“查询用户信息”请求
			handSearchUser();
			break;
		case Config.REQUEST_GET_USER:
		    handGetUser();
		    break;
		case Config.REQUEST_EXIT:              //处理“退出程序”请求
			handExit();
			break;
		}
	}
	
	private void handGetUser() {
	    try {
            String userId=dis.readUTF();
            
            User user=dbUtil.searchUser(userId);
            dos.writeInt(Config.ADD_FRIEND);
            dos.writeUTF(user.getUserId());
            dos.writeUTF(user.getNickName());
            dos.writeUTF(user.getSex());
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取指定用户的头像
	private void handGetHead() {
	    try {
            String userId=dis.readUTF();
            
            User user=dbUtil.searchUser(userId);
            String headPath=user.getHead();
            String modifyTime=user.getLastModityTime()+"";
            
            if(headPath.length()!=0){
                dos.writeInt(Config.RESULT_GET_HEAD);
                dos.writeUTF(userId);
                dos.writeUTF(modifyTime);
                dos.flush();
                
                readFileSendData(headPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //处理更新头像功能
	private void handUpdateHead() {
        try {
            String userId=dis.readUTF();
            long lastModifyTime=dis.readLong();
            
            File file=FileUtil.createHeadFile(Integer.parseInt(userId));
            
            receiveDataWriteFile(file.getAbsolutePath());
            
            String headPath=file.getAbsolutePath().replace("\\","\\\\");
            dbUtil.updateHeadImg(userId, headPath, lastModifyTime);
        } catch (IOException e) {
            System.out.println("handUpdateHead() exception="+e.toString());
        }
    }

    private void handRegist() {
        try {
            System.out.println("handRegist(): 接收到注册用户请求");
            String nickName=dis.readUTF();
            String pwd=dis.readUTF();
            String sex=dis.readUTF();
            
            boolean isHasImg=dis.readBoolean();
            System.out.println("handRegist() nickName="+nickName+", sex="+sex);
            
            //查看数据库中是否存在这个用户————没必要进行这样的操作。允许存在昵称相同的用户，ID号是由系统提供的，ID号是唯一的辨别标识
            //获得数据库中ID号最大的用户ID
            int id=dbUtil.getMaxUserId();
            System.out.println("handRegist(): 新用户ID="+id);
            LogUtil.record("---------------------------------------");
            LogUtil.record("接收到注册用户请求：name="+nickName+" , sex="+sex+" , isHasImg="+isHasImg);
            System.out.println("handRegist(): 用户注册信息 name="+nickName+" , sex="+sex+" , isHasImg="+isHasImg);
            
            String headPath="";
            long lastModifyTime=1l;   //对于没有头像的用户，设置modifyTime字段为1l
            if(isHasImg==true){
                File file=FileUtil.createHeadFile(id);
                lastModifyTime=file.lastModified();
                
                receiveDataWriteFile(file.getAbsolutePath());
                
                headPath=file.getAbsolutePath().replace("\\", "\\\\");
            }
            
            boolean registResult=dbUtil.regist(nickName, pwd, sex, headPath, lastModifyTime);
            if(registResult==true){
                dos.writeInt(Config.RESULT_REGIST);
                dos.writeBoolean(true);                //注册成功
                dos.writeInt(id+1);                      //返回其ID
                dos.writeLong(lastModifyTime);
                
                LogUtil.record("注册成功！userId="+id+1);
            }else{
                dos.writeInt(Config.RESULT_REGIST);
                dos.writeBoolean(false);                //注册失败
                
                LogUtil.record("注册失败！");
            }
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void receiveDataWriteFile(String filePath) throws FileNotFoundException, IOException {
        DataOutputStream ddos=new DataOutputStream(new FileOutputStream(filePath));   //将语音或图片写入本地SD卡
        int length=0;
        int totalNum=0;
        byte[] buffer=new byte[2048];
        while((length=dis.readInt())!=0){
            length=dis.read(buffer, 0, length);
            totalNum+=length;
            ddos.write(buffer, 0, length);
            ddos.flush();
        }
        
        if(ddos!=null){
            try {
                ddos.close();
                ddos=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
//        Log.i(TAG, "handReceiveData() 接收 img.totalNum="+totalNum);
    }

    private void handExit() {
		try {
			String userId=dis.readUTF();
			LogUtil.record("---------------------------------------");
			LogUtil.record("在线用户"+userId+"请求退出登录");
			
			//结束线程
			setWorkState(false);
			
			LogUtil.record("用户"+userId+"退出前,共有"+map.size()+"个用户在线");
			//注意这里不是在转发消息，并不需要查询发送退出请求的用户是否在线。只要是同服务器有Socket连接就是在线的
			map.remove(userId);
			LogUtil.record("用户"+userId+"退出后,还有"+map.size()+"个用户在线");
			dbUtil.close();		//关闭到数据库的连接
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handAddFriend() {
		try {
			String selfId=dis.readUTF();
			String friendId=dis.readUTF();
			
			//向数据库中添加这对好友关系
		   boolean result=dbUtil.addFriend(selfId, friendId);
		   LogUtil.record("用户"+selfId+"发出添加用户"+friendId+"为好友的请求,服务器端插入这对好友关系："+result);
			//如果用户friendId在线，则向他发出添加好友的消息（在他的手机端存储这对好友关系）；若不在线，则将这对好友关系作为离线消息暂存在服务器端
			if(map.containsKey(friendId)){
				//从数据库中获取Id为selfId的用户的详细信息
				//A向服务器发出添加B为好友的请求，则服务器应将A的信息回馈给B，B用这个信息插入B的friend表
				User user=dbUtil.searchUser(selfId);
				if(user!=null){	 //存在该用户，其实这种担心是多余的，因为是客户端先查询了该用户的信息，才添加为好友的，所以添加好友请求的Id必定是存在的
					Socket socket=map.get(friendId);  
					DataOutputStream out=new DataOutputStream(socket.getOutputStream());
					out.writeInt(Config.ADD_FRIEND);
					out.writeUTF(user.getUserId());
					out.writeUTF(user.getNickName());
					out.writeUTF(user.getSex());
					out.flush();
					
					String headPath=user.getHead();
					File file=new File(headPath);
					if(file.exists()==true){
						out.writeInt(Config.USER_HAS_IMG);
						out.writeUTF(file.lastModified()+"");
						out.flush();
						
						//此处的代码同readFileSendData()相似
						DataInputStream ddis=new DataInputStream(new FileInputStream(headPath));
				        int length=0;
				        int totalNum=0;
				        byte[] buffer=new byte[1024];
				        
				        while((length=ddis.read(buffer))!=-1){
				            totalNum+=length;
				            out.writeInt(length);
				            out.write(buffer, 0, length);
				            out.flush();
				        }
				        
				        out.writeInt(0);
				        out.flush();
				        
				        if(ddis!=null){
				            ddis.close();
				            ddis=null;
				        }
					}else{
						out.writeInt(Config.USER_NOT_IMG);
						out.writeUTF(1l+"");
						out.flush();
					}
				}
			}else{
				//将这个添加好友请求作为离线消息存储
			    LogUtil.record("用户"+selfId+"发出添加用户"+friendId+"为好友的请求,"+friendId+"不在线，将这个添加好友请求作为离线消息存储");
			    dbUtil.saveMessage(selfId, friendId, Config.MESSAGE_TYPE_ADD_FRIEND, "", "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handSearchUser() {
		try {
			String userId=dis.readUTF();
			User user=dbUtil.searchUser(userId);
			if(user!=null){
				dos.writeInt(Config.RESULT_SEARCH_USER);
				dos.writeInt(Config.SEARCH_USER_SUCCESS);
				dos.writeUTF(user.getUserId());
				dos.writeUTF(user.getNickName());
				dos.writeUTF(user.getSex());
				String head=user.getHead();
				File file=new File(head);
				if(file.exists()==true){
					dos.writeInt(Config.USER_HAS_IMG);
					DataInputStream in=new DataInputStream(new FileInputStream(file));
					int length=in.available();
					byte[] data=new byte[length];
					int size=in.read(data);
					in.close();
					in=null;
					
					dos.writeInt(size);
					dos.write(data, 0, size);
				}else{
					dos.writeInt(Config.USER_NOT_IMG);
				}
				
				dos.flush();
			}else{
				dos.writeInt(Config.RESULT_SEARCH_USER);
				dos.writeInt(Config.SEARCH_USER_FALSE);
				dos.flush();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handLogin(){
		try {
		    System.out.println("handLogin() 处理登陆请求");
			String userId=dis.readUTF();
			System.out.println("handLogin() userId="+userId);
			String pwd=dis.readUTF();
			System.out.println("handLogin() pwd="+pwd);
			
			LogUtil.record("------------------------------------------");
			LogUtil.record("使用用户名="+userId+", 密码="+pwd+"  登录");
			User user=dbUtil.login(userId, pwd);
			if(user!=null){
			    System.out.println(userId+"登陆, sex="+user.getSex());
				dos.writeInt(Config.RESULT_LOGIN);
				dos.writeBoolean(true);
				dos.writeUTF(user.getUserId());
				dos.writeUTF(user.getNickName());
				dos.writeUTF(user.getSex());
				String head=user.getHead();
				System.out.println("handLogin() head="+head);
				if("".equals(head)){	//如果为""串，没有头像
					dos.writeInt(Config.USER_NOT_IMG);
					System.out.println("handLogin() 没有头像");
				}else{
					DataInputStream in=null;
					try{
						in=new DataInputStream(new FileInputStream(head));
					}catch(FileNotFoundException e){
						LogUtil.record("发生异常："+e.toString());
						e.printStackTrace();
					}
					if(in!=null){    //指定的文件没有找到，将会发生FileNotFoundException，是不能创建
					    dos.writeInt(Config.USER_HAS_IMG);
					    dos.writeLong(user.getLastModityTime());
						
						System.out.println("handLogin() 有头像");
					}else{
						dos.writeInt(Config.USER_NOT_IMG);
						System.out.println("handLogin() 没有找到头像文件");
					}
				}
				
				dos.flush();
								
				map.put(userId, socket);
				LogUtil.record("用户"+userId+"登录成功");
			}else{
				dos.writeInt(Config.RESULT_LOGIN);
				dos.writeBoolean(false);
				dos.flush();
				
				LogUtil.record("用户"+userId+"登录失败，userId="+userId+", pwd="+pwd);
			}			
		} catch (IOException e) {
			LogUtil.record("发生异常："+e.toString());
			e.printStackTrace();
		}
	}
	
	//处理文本消息
	public void handSendText(){
		try {
			String sendId=dis.readUTF();
			String receiveId=dis.readUTF();
			String time = dis.readUTF();
			String content=dis.readUTF();
			System.out.println("接收到客户端"+sendId+"发来的消息");
			log.record("------------------------------------------------------------------------");
			log.record("用户"+sendId+" 向用户"+receiveId+"发送文本消息='"+content+"'");
			//判断接收者是否在线
			if(map.containsKey(receiveId)){
				Socket socket=map.get(receiveId);
				log.record("服务器同消息发送者"+sendId+"连接的Socket="+map.get(sendId));
				log.record("服务器同消息接收者"+receiveId+"连接的Socket="+socket);
				DataOutputStream out=new DataOutputStream(socket.getOutputStream());
				out.writeInt(Config.RECEIVE_TEXT);
				out.writeUTF(sendId);
				out.writeUTF(receiveId);
				out.writeUTF(time);
				out.writeUTF(content);
				out.flush();
				
				log.record("用户"+receiveId+"在线，可以直接接收到消息，消息已转发给接收者"+receiveId);
			}else{
				//写入数据库
				dbUtil.saveMessage(sendId, receiveId, Config.MESSAGE_TYPE_TXT, time, content);
				log.record("用户"+receiveId+"不在线，先把消息暂存在服务器端");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	

	private void handSendImg() {
		try {
			String sendId=dis.readUTF();
			String receiveId=dis.readUTF();
			String time = dis.readUTF();
		
			log.record("用户"+sendId+" 向用户"+receiveId+"发送图片消息'");
			//判断接收者是否在线
			if(map.containsKey(receiveId)){
				Socket socket=map.get(receiveId);
				log.record("服务器同用户"+receiveId+"连接的Socket="+socket);
				DataOutputStream out=new DataOutputStream(socket.getOutputStream());
				out.writeInt(Config.RECEIVE_IMG);
				out.writeUTF(sendId);
				out.writeUTF(receiveId);
				out.writeUTF(time);
				out.flush();
				
				int length=0;
	            int totalNum=0;
	            byte[] buffer=new byte[2048];
	            while((length=dis.readInt())!=0){
	                length=dis.read(buffer, 0, length);
	                totalNum+=length;
	                
	                out.writeInt(length);
	                out.write(buffer, 0, length);
	                out.flush();
	            }
				
				out.writeInt(0);
				out.flush();
				
				System.out.println("img.totalNum="+totalNum);
				
				log.record("用户"+receiveId+"在线，可以直接接收到消息");
			}else{
				//写入数据库
				File file=FileUtil.createFile(sendId, Config.MESSAGE_TYPE_IMG);
				FileOutputStream ou=new FileOutputStream(file);
				
				int length=0;
                int totalNum=0;
                byte[] buffer=new byte[2048];
                while((length=dis.readInt())!=0){
                    length=dis.read(buffer, 0, length);
                    totalNum+=length;
                    ou.write(buffer, 0, length);
                    ou.flush();
                }
				ou.close();
				ou=null;
				
				System.out.println("img.totalNum="+totalNum);
				
				dbUtil.saveMessage(sendId, receiveId, Config.MESSAGE_TYPE_IMG, time, file.getAbsolutePath().replace("\\", "\\\\"));
				log.record("用户"+receiveId+"不在线，先把消息暂存在服务器端");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void handSendImgOrAudio(int requestType, int messageType) {
        try {
            String sendId=dis.readUTF();
            String receiveId=dis.readUTF();
            String time = dis.readUTF();
            
            if(messageType==Config.MESSAGE_TYPE_IMG){
                log.record("用户"+sendId+" 向用户"+receiveId+"发送图片消息'");
            }else{
                log.record("用户"+sendId+" 向用户"+receiveId+"发送语音消息'");
            }
            
            //判断接收者是否在线
            if(map.containsKey(receiveId)){
                Socket socket=map.get(receiveId);
                log.record("服务器同用户"+receiveId+"连接的Socket="+socket);
                DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                out.writeInt(requestType);
                out.writeUTF(sendId);
                out.writeUTF(receiveId);
                out.writeUTF(time);
                out.flush();
                
                int length=0;
                int totalNum=0;
                byte[] buffer=new byte[1024];
                while((length=dis.readInt())!=0){
                    length=dis.read(buffer, 0, length);
                    totalNum+=length;
                    
                    out.writeInt(length);
                    out.write(buffer, 0, length);
                    out.flush();
                }
                
                out.writeInt(0);
                out.flush();
                
                System.out.println("img.totalNum="+totalNum);
                
                log.record("用户"+receiveId+"在线，可以直接接收到消息");
            }else{
                //写入数据库
                File file=FileUtil.createFile(sendId, messageType);
                FileOutputStream ou=new FileOutputStream(file);
                
                int length=0;
                int totalNum=0;
                byte[] buffer=new byte[1024];
                while((length=dis.readInt())!=0){
                    length=dis.read(buffer, 0, length);
                    totalNum+=length;
                    ou.write(buffer, 0, length);
                    ou.flush();
                }
                ou.close();
                ou=null;
                
                System.out.println("img.totalNum="+totalNum);
                
                dbUtil.saveMessage(sendId, receiveId, messageType, time, file.getAbsolutePath().replace("\\", "\\\\"));
                log.record("用户"+receiveId+"不在线，先把消息暂存在服务器端");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
	
	//
	private void handSendAudio() {
		try {
			String sendId=dis.readUTF();
			String receiveId=dis.readUTF();
			String time = dis.readUTF();
			int length=dis.readInt();
			byte[] data=new byte[length];
			int realNum=dis.read(data);
			
			log.record("用户"+sendId+" 向用户"+receiveId+"发送语音消息'");
			//判断接收者是否在线
			if(map.containsKey(receiveId)){
				Socket socket=map.get(receiveId);
				log.record("服务器同用户"+receiveId+"连接的Socket="+socket);
				DataOutputStream out=new DataOutputStream(socket.getOutputStream());
				out.writeInt(Config.RECEIVE_AUDIO);
				out.writeUTF(sendId);
				out.writeUTF(receiveId);
				out.writeUTF(time);
				out.writeInt(realNum);
				out.write(data, 0, realNum);
				out.flush();
				
				log.record("用户"+receiveId+"在线，可以直接接收到消息");
			}else{
				//写入数据库
				File file=FileUtil.createFile(sendId, Config.MESSAGE_TYPE_AUDIO);
				FileOutputStream ou=new FileOutputStream(file);
				ou.write(data, 0, realNum);
				ou.flush();
				ou.close();
				ou=null;
				
				dbUtil.saveMessage(sendId, receiveId, Config.MESSAGE_TYPE_AUDIO, time, file.getAbsolutePath());
				log.record("用户"+receiveId+"不在线，先把消息暂存在服务器端");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	
	//处理”获取离线消息的请求“
	public void handGetOfflineMsg(){
		try {
			String selfId=dis.readUTF();
			ArrayList<Message> list=dbUtil.getMessages(selfId);
			int listSize=list.size();
			
			LogUtil.record("----------------------------------------------");
			LogUtil.record("用户"+selfId+"发来获取离线消息的请求,共找到与他相关的离线消息"+listSize+"条");
			if(list!=null && listSize>0){
				LogUtil.record("因服务器已发送离线消息给接收者，所以可以删除相关的消息");
				dbUtil.deleteMessages(selfId);
				Message msg=null;
				
				dos.writeInt(Config.RESULT_GET_OFFLINE_MSG);
				dos.writeInt(listSize);
				LogUtil.record("总共有条"+listSize+"离线消息");
				for(int i=0; i<listSize; i++){
					msg=list.get(i);
					int type=msg.getType();
					if(type==Config.MESSAGE_TYPE_TXT){
						dos.writeUTF(msg.getSendId());
						dos.writeUTF(msg.getReceiveId());
						dos.writeInt(msg.getType());
						dos.writeUTF(msg.getTime());
						dos.writeUTF(msg.getContent());
						dos.flush();
					}else {
						dos.writeUTF(msg.getSendId());
						dos.writeUTF(msg.getReceiveId());
						dos.writeInt(msg.getType());
						dos.writeUTF(msg.getTime());
						//获取音频文件的字节数组
						File file=new File(msg.getContent());
						DataInputStream in=new DataInputStream(new FileInputStream(file));
						int length=in.available();
						byte[] data=new byte[length];
						int size=in.read(data);
						dos.writeInt(size);
						dos.write(data, 0, size);
						dos.flush();
						
						in.close();
						in=null;
						
						//删除图片或语音文件
						file.delete();
					}
					
					LogUtil.record("发送第"+(i+1)+"离线条消息");
				}
			}else {
				System.out.println("服务器端没有有关用户"+selfId+"的未发消息");
			}
		} 
		catch (IOException e) {
			LogUtil.record("handGetOfflineMsg 发生异常："+e.toString());
			e.printStackTrace();
		}
	}
	
	public void handGetFriends(){
		try {
			String selfId=dis.readUTF();
			int length=dis.readInt();
			Map<String, String> map=new HashMap<String, String>();
			
			//循环读入客户端的好友列表<friendId, modifyTime>
			for(int i=0; i<length; i++){
			    String friendId=dis.readUTF();
			    String modifyTime=dis.readUTF();
			    map.put(friendId, modifyTime);
			}
			
			ArrayList<Friend> list=dbUtil.getFirends(selfId);
			dos.writeInt(Config.RESULT_GET_FRIENDS);
			int size=list.size();
			dos.writeInt(size);   //告诉客户端要接收多少数据
			for(int i=0; i<size; i++){
				Friend friend=list.get(i);
				String friendId=friend.getFriendID();
				String headPath=friend.getHead();
				String modifyTime=friend.getHeadModifyTime();
				
				//如果包含这个好友，则比较头像时间戳;不包含，则将服务器端的头像发给客户端
				if(map.containsKey(friendId)){
				    //时间戳不同，将服务器端的头像发给客户端
				    if(!map.get(friendId).equals(friend.getHeadModifyTime())){
				        dos.writeInt(Config.IMG_NEED_UPDATE);
				        dos.writeUTF(modifyTime);      //头像的时间戳
				        dos.flush();
				        readFileSendData(headPath);    //读取并发送头像
				    }else{
				      //时间戳相同，这样就不需要从服务器端把头像图片发送到客户端
				        dos.writeInt(Config.IMG_NO_UPDATE);
				        dos.flush();
				    }
				}else{  //客户端的数据库中还没有包含这个好友，所以将该用户的全部信息发给客户端
				    dos.writeInt(Config.ADD_FRIEND);
				    dos.writeUTF(friendId);
				    dos.writeUTF(friend.getFriendName());
				    dos.writeUTF(friend.getSex());
				   
				    if(headPath!=null && !headPath.equals("")){
				        dos.writeInt(Config.USER_HAS_IMG);
	                    dos.writeUTF(modifyTime);
	                    dos.flush();
				        //存在头像
				        readFileSendData(headPath);
				    }else{
				        dos.writeInt(Config.USER_NOT_IMG);
	                    dos.writeUTF(modifyTime);
	                    dos.flush();
				    }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handGetNews(){
		try {
			int length = dis.readInt();
			
			ArrayList<News> newslist = dbUtil.getNews();
			dos.writeInt(Config.RESULT_GET_NEWS);
			int size = newslist.size();
			dos.writeInt(size);   //告诉客户端要接收多少数据
			
			if(length == size){
				//新闻条数相同，这样就不需要从服务器端把新闻发送到客户端
		        dos.writeInt(Config.NEWS_NO_UPDATE);
		        dos.flush();
			}
			else{  //客户端的新闻条数不同，所以将全部新闻发给客户端
				dos.writeInt(Config.NEWS_NEED_UPDATE);
				for(int i=0; i<size; i++){
					News news = newslist.get(i);
					String newsTitle = news.getNewsTitle();
					String newsContent = news.getNewsContent();
					String newsImagePath = news.getNewsImage();
					
					dos.writeInt(Config.ADD_NEWS);
				    dos.writeUTF(newsTitle);
				    dos.writeUTF(newsContent);
				    dos.flush();
				    readFileSendData(newsImagePath);
				    
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 读取文件(图片、语音)，发送数据
     * @param filePath
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void readFileSendData(String filePath) throws FileNotFoundException, IOException {
        DataInputStream ddis=new DataInputStream(new FileInputStream(filePath));
        int length=0;
        int totalNum=0;
        byte[] buffer=new byte[1024];
        
        while((length=ddis.read(buffer))!=-1){
            totalNum+=length;
            dos.writeInt(length);
            dos.write(buffer, 0, length);
            dos.flush();
        }
        
        dos.writeInt(0);
        dos.flush();
        
        if(ddis!=null){
            ddis.close();
            ddis=null;
        }
    }
}

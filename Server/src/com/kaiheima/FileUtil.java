package com.kaiheima;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.kaiheima.constant.Config;

public class FileUtil {
	public static final String WORK_IMG_PATH="D:\\woliao\\Img";
	public static final String WORK_AUDIO_PATH="D:\\woliao\\Audio";
	public static final String WORK_HEAD_PATH="D:\\woliao\\Head";

	/**
	 * 根据fileType,创建普通的jpg或3gp文件来保存图片或语音
	 * @param selfId
	 * @param fileType
	 * @return
	 */
	public static File createFile(String selfId, int fileType) {
		String nowTime = TimeUtil.getAbsoluteTime();
		String filePath="";
		if(fileType==Config.MESSAGE_TYPE_IMG){
			filePath =WORK_IMG_PATH + selfId;
		}else{
			filePath =WORK_AUDIO_PATH + selfId;
		}
		
		File fileParent = new File(filePath);
		if (fileParent.exists() == false) {
			fileParent.mkdirs();
		}
		File file = null;
		if (fileType == Config.MESSAGE_TYPE_IMG) {
			file = new File(filePath + "\\" + nowTime + ".jpg");
		} else if (fileType == Config.MESSAGE_TYPE_AUDIO) {
			file = new File(filePath + "\\" + nowTime + ".3gp");
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	//根据用户ID,创建一个以该ID为文件名的jpg图片
	public static File createHeadFile(int userId){
        File fileParent = new File(WORK_HEAD_PATH);
        if (fileParent.exists() == false) {
            fileParent.mkdirs();
        }
        File file = null;
        file = new File(WORK_HEAD_PATH + "\\" + userId+ ".jpg");

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
	}
}

package com.hailan.HaiLanPrint.restfuls.bean;

import java.util.Date;

/**
 * 设备信息实体类
 * 
 * @author qinglong.huang
 * 
 */
public class Device {

	public int DeviceID;
	public String DeviceToken;
	public int Platform = 1;//默认为Android手机
	public String PlatformVersion;
	public String DeviceModel;
	public Date CreatedTime;

	public int getDeviceID() {
		return DeviceID;
	}

	public void setDeviceID(int deviceID) {
		DeviceID = deviceID;
	}

	public String getDeviceToken() {
		return DeviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		DeviceToken = deviceToken;
	}

	public int getPlatform() {
		return Platform;
	}

	public void setPlatform(int platform) {
		Platform = platform;
	}

	public String getPlatformVersion() {
		return PlatformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		PlatformVersion = platformVersion;
	}

	public String getDeviceModel() {
		return DeviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		DeviceModel = deviceModel;
	}

	public Date getCreatedTime() {
		return CreatedTime;
	}

	public void setCreatedTime(Date createdTime) {
		CreatedTime = createdTime;
	}

}

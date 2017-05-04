package com.hailan.HaiLanPrint.restfuls.bean;


import com.google.gson.JsonObject;

public class RequestDataForLogOnly {
	// / 当前使用的通信协议包的版本
	private String Version = "1.0";

	// / 文化/语言信息
	private String Culture;

	// / 业务码
	private int BusinessCode;

	// / 功能码
	private String FunctionName;

	// / 用户ID
	private int UserID;

	// / 设备ID
	private int DeviceID;

	// / 发起请求平台
	private int Platform;

	// / 通讯Token
	private String ServiceToken = "";

	// / 请求时间（现在与1970.1.1的时间差，单位秒）
	private long ActionTimeSpan;

	// / 请求参数
	private JsonObject QueryData;

	// / 请求签名
	private String Sign;

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getCulture() {
		return Culture;
	}

	public void setCulture(String culture) {
		Culture = culture;
	}

	public int getBusinessCode() {
		return BusinessCode;
	}

	public void setBusinessCode(int businessCode) {
		BusinessCode = businessCode;
	}

	public String getFunctionName() {
		return FunctionName;
	}

	public void setFunctionName(String functionName) {
		FunctionName = functionName;
	}

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	public int getDeviceID() {
		return DeviceID;
	}

	public void setDeviceID(int deviceID) {
		DeviceID = deviceID;
	}

	public int getPlatform() {
		return Platform;
	}

	public void setPlatform(int platform) {
		Platform = platform;
	}

	public String getServiceToken() {
		return ServiceToken;
	}

	public void setServiceToken(String serviceToken) {
		ServiceToken = serviceToken;
	}

	public long getActionTimeSpan() {
		return ActionTimeSpan;
	}

	public void setActionTimeSpan(long actionTimeSpan) {
		ActionTimeSpan = actionTimeSpan;
	}

	public JsonObject getQueryData() {
		return QueryData;
	}

	public void setQueryData(JsonObject queryData) {
		QueryData = queryData;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

}

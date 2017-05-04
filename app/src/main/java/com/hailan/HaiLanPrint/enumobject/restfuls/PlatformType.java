package com.hailan.HaiLanPrint.enumobject.restfuls;

public enum PlatformType {

	Unkown(0),
	ANDROID_PHONE(1),
	ANDROID_PAD(2),
	AndroidScreen(3);

	private int value;

	private PlatformType(int platform) {
		this.value = platform;
	}

	public int value() {
		return value;
	}

}

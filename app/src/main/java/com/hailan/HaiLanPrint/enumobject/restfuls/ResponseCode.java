package com.hailan.HaiLanPrint.enumobject.restfuls;

import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;

public enum ResponseCode {

    UnSpecific(-1000),
	Normal(0), 
	SystemError(0x1), 
	ApplicationError(0x2), 
	Warning(0x4), 
	VersonNotSpported(0x1 | 0x8), 
	LanguageNotSpported(0x4 | 0x10), 
	RequestCodeNotSupported(0x1 | 0x20), 
	LoadModuleFailed(0x1 | 0x40), 
	AppUnhandledException(0x1 | 0x2 | 0x80), 
	InvalidToken(0x100), 
	InvalidDevice(0x200), 
	NewNotify(0x400),    // 需要同步数据
	AppCustom0(0x80000), // 有审核数据
	AppCustom1(0x100000),
	AppCustom2(0x200000),  // 锁定
	AppCustom3(0x400000),
	AppCustom4(0x800000),
	AppCustom5(0x1000000),
	AppCustom6(0x2000000),
	AppCustom7(0x4000000),
	AppCustom8(0x8000000),
	AppCustom9(0x10000000);  // 版本更新

	private int value;

	private ResponseCode(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

    public static boolean isSuccess(ResponseData responseData) {
        return responseData.getCode() == Normal.getValue();
    }

    public static ResponseCode valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 0:
                return Normal;
            case 0x01:
                return SystemError;
            case 0x02:
                return ApplicationError;
            case 0x04:
                return Warning;
            case 0x1 | 0x8:
                return VersonNotSpported;
            case 0x4 | 0x10:
                return LanguageNotSpported;
            case 0x1 | 0x20:
                return RequestCodeNotSupported;
            case 0x1 | 0x40:
                return LoadModuleFailed;
            case 0x1 | 0x2 | 0x80:
                return AppUnhandledException;
            case 0x100:
                return InvalidToken;
            case 0x200:
                return InvalidDevice;
            case 0x400:
                return NewNotify;
            case 0x80000:
                return AppCustom0;
            case 0x100000:
                return AppCustom1;
            case 0x200000:
                return AppCustom2;
            case 0x400000:
                return AppCustom3;
            case 0x800000:
                return AppCustom4;
            case 0x1000000:
                return AppCustom5;
            case 0x2000000:
                return AppCustom6;
            case 0x4000000:
                return AppCustom7;
            case 0x8000000:
                return AppCustom8;
            case 0x10000000:
                return AppCustom9;
            default:
                return UnSpecific;
        }
    }

}

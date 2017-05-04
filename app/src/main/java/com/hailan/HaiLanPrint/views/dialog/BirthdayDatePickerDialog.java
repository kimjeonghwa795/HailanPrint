package com.hailan.HaiLanPrint.views.dialog;

import android.app.DatePickerDialog;
import android.content.Context;

import java.util.Date;

/**
 * 生日日期选择，不能超过当前时间
 * 
 * @author yoghourt
 * 
 */
public class BirthdayDatePickerDialog extends DatePickerDialog {

	public BirthdayDatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
		init();
	}

	public BirthdayDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		init();
	}

	private void init() {
		getDatePicker().setMaxDate(new Date().getTime());  // 生日不能超过今天
	}

}

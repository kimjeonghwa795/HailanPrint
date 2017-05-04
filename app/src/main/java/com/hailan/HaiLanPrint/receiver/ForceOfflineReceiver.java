package com.hailan.HaiLanPrint.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.service.ForceOfflineService;
import com.hailan.HaiLanPrint.utils.DeviceUtil;

public class ForceOfflineReceiver extends BroadcastReceiver {
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		intent = new Intent(context, ForceOfflineService.class);
		context.stopService(intent);
		showDialog(context, "");
	}

	private void showDialog(final Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.tips);
		builder.setMessage("您的账户已在另一个设备登录,请尝试重新登陆");

		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DeviceUtil.logoutUser(context);
			}
		});
		builder.setCancelable(false);
		AlertDialog alertDialog = builder.create();
		alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialog.show();
	}

}
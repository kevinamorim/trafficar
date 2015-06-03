package com.sdis.trafficar.helpers;

import android.app.AlertDialog;
import android.content.Context;

public class SimpleAlertDialog {
	
	public SimpleAlertDialog(Context context, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

}

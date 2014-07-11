package com.enlighten.lan_tic_tac_toe;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class Util {

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, 3000).show();
	}

	public static void showToastOnUiThread(final Activity activity,
			final String message) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Util.showToast(activity, message);
			}
		});
	};

}

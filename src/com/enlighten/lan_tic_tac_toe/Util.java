package com.enlighten.lan_tic_tac_toe;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

	public static void showConfirmationDialog(final Activity activity, String message,
			String title, final Runnable runnable) {
		Builder builder = new Builder(activity);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.runOnUiThread(runnable);
				dialog.cancel();
			}
		});
		builder.show();
	}

}

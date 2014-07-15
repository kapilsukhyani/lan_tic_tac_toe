package com.enlighten.lan_tic_tac_toe;

import android.os.Handler;
import android.text.TextUtils;

public class GameProtocol {

	public static final String USER_READY_COMMAND = "user_ready\r\n";
	public static final String MARK_SECTION_COMMAND = "mark <section_no>\r\n";
	public static final String USER_WON_COMMAND = "mark <section_no> user_won\r\n";
	public static final String SECTION_NO_PLACE_HOLDER = "<section_no>";

	public static void notifyListener(String command, Handler handler,
			final OnRemoteChangeListener listener) {

		Runnable runnable = null;
		if (!TextUtils.isEmpty(command)) {
			if (command.startsWith("user_ready")) {
				runnable = new Runnable() {

					@Override
					public void run() {
						listener.onRemoteReady();
					}
				};

			} else if (command.startsWith("mark")) {
				final int sectionNo = getSctionNoFromCommand(command);

				if (command.contains("user_won")) {
					runnable = new Runnable() {

						@Override
						public void run() {
							listener.onRemoteWon(sectionNo);
						}
					};
				} else {
					runnable = new Runnable() {

						@Override
						public void run() {
							listener.onRemoteMarked(sectionNo);
						}
					};
				}
			}

			handler.post(runnable);
		}

	}

	public static int getSctionNoFromCommand(String command) {
		if (!TextUtils.isEmpty(command) && command.startsWith("mark")) {
			int temp = "mark".length();
			final String sectionNo = command.substring(temp + 1, temp + 2);
			return Integer.valueOf(sectionNo);
		} else
			return -1;

	}

}

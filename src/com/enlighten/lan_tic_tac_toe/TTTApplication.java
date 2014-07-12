package com.enlighten.lan_tic_tac_toe;

import android.app.Application;

public class TTTApplication extends Application {

	public static final String TTT_SERVICE = "TTT_Service";
	public static final int TTT_SERVICE_PORT = 9999;

	public static enum UserType {
		FirstUser, SecondUser
	}

	@Override
	public void onCreate() {
		super.onCreate();
		NSDUtility.init(getApplicationContext());
	}

}

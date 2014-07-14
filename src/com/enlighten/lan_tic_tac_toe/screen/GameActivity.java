package com.enlighten.lan_tic_tac_toe.screen;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.enlighten.lan_tic_tac_toe.NSDUtility;
import com.enlighten.lan_tic_tac_toe.R;
import com.enlighten.lan_tic_tac_toe.TTTCommunicationChannel;
import com.enlighten.lan_tic_tac_toe.Util;

public class GameActivity extends Activity {

	public class ChannelReadyListener extends Handler {

		public static final int CHANNEL_READY = 1;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == CHANNEL_READY) {
				Util.showToast(GameActivity.this, "Channel is ready: got "
						+ msg.obj);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		findViewById(R.id.client).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {

					@Override
					public void run() {

						System.out.println("Client writing: ");
						TTTCommunicationChannel.sendCommand("client");

					}
				}).start();
			}
		});

		findViewById(R.id.server).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						System.out.println("server writing: ");
						TTTCommunicationChannel.sendCommand("server");

					}
				}).start();
			}
		});

		if (TTTCommunicationChannel
				.isCommunicationChannelReady(new ChannelReadyListener())) {
			Util.showToast(GameActivity.this, "Channel is ready");
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		NSDUtility.stop();
	}
}

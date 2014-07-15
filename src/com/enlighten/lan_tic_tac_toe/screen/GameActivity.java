package com.enlighten.lan_tic_tac_toe.screen;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.enlighten.lan_tic_tac_toe.NSDUtility;
import com.enlighten.lan_tic_tac_toe.R;
import com.enlighten.lan_tic_tac_toe.TTTCommunicationChannel;
import com.enlighten.lan_tic_tac_toe.Util;
import com.enlighten.lan_tic_tac_toe.view.TicTacToeBoard;

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

	private TicTacToeBoard ticTacToeBoard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		ticTacToeBoard = (TicTacToeBoard) findViewById(R.id.board);

		if (TTTCommunicationChannel.isCommunicationChannelReady(
				new ChannelReadyListener(), ticTacToeBoard)) {
			Util.showToast(GameActivity.this, "Channel is ready");
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		NSDUtility.stop();
		TTTCommunicationChannel.interrupt();
	}
}

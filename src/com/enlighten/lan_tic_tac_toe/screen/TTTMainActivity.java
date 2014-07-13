package com.enlighten.lan_tic_tac_toe.screen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.enlighten.lan_tic_tac_toe.NSDUtility;
import com.enlighten.lan_tic_tac_toe.R;
import com.enlighten.lan_tic_tac_toe.TTTApplication.UserType;
import com.enlighten.lan_tic_tac_toe.Util;

public class TTTMainActivity extends Activity implements View.OnClickListener {

	private Button startGame, joinGame;
	public boolean startedDiscovery = false;
	private SetupListener setupListener;
	private ProgressDialog progressDialog;

	public class SetupListener extends Handler {
		SetupListener(Looper looper) {
			super(looper);
		}

		public static final int SETUP_SUCCESSFUL = 0x1;
		public static final int SETUP_FAILED = 0x2;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressDialog.dismiss();
			if (msg.what == SETUP_SUCCESSFUL) {
				onSetupSucessful();
			} else if (msg.what == SETUP_FAILED) {
				onSetupFailed(msg.arg1);
			}

		}

		private void onSetupSucessful() {

			startActivity(new Intent(TTTMainActivity.this, GameActivity.class));

		}

		private void onSetupFailed(int errorCode) {
			Util.showToast(TTTMainActivity.this, "Setup failed " + errorCode);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tttmain);
		setupListener = new SetupListener(getMainLooper());
		startGame = (Button) findViewById(R.id.start_button);
		joinGame = (Button) findViewById(R.id.join_button);

		startGame.setOnClickListener(this);
		joinGame.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tttmain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.start_button:
			startGame();
			break;
		case R.id.join_button:
			joinGame();
			break;
		}

	}

	private void startGame() {
		start(UserType.FirstUser);
	}

	private void joinGame() {
		start(UserType.SecondUser);
	}

	private void start(UserType userType) {
		progressDialog = ProgressDialog.show(this, "Setting up network",
				"Please wait...");
		progressDialog.setCancelable(false);
		NSDUtility.start(userType, setupListener);
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	public void onBackPressed() {

	}
}

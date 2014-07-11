package com.enlighten.lan_tic_tac_toe.screen;

import android.app.Activity;
import android.net.nsd.NsdManager.DiscoveryListener;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.enlighten.lan_tic_tac_toe.NSDUtility;
import com.enlighten.lan_tic_tac_toe.R;
import com.enlighten.lan_tic_tac_toe.TTTApplication;
import com.enlighten.lan_tic_tac_toe.Util;

public class TTTMainActivity extends Activity implements View.OnClickListener {

	private Button startGame, joinGame;
	public boolean startedDiscovery = false;

	private class TTTRegistrationListener implements RegistrationListener {

		@Override
		public void onUnregistrationFailed(NsdServiceInfo serviceInfo,
				final int errorCode) {
			Util.showToastOnUiThread(TTTMainActivity.this,
					"Unregistration failed with error, " + errorCode);

		}

		@Override
		public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
			Util.showToastOnUiThread(TTTMainActivity.this,
					"Service unregistration succesful");

		}

		@Override
		public void onServiceRegistered(NsdServiceInfo serviceInfo) {
			Util.showToastOnUiThread(TTTMainActivity.this,
					"Service registration succesful");
			startedDiscovery = true;

		}

		@Override
		public void onRegistrationFailed(NsdServiceInfo serviceInfo,
				final int errorCode) {
			Util.showToastOnUiThread(TTTMainActivity.this,
					"Registratioin failed with error code " + errorCode);

		}
	};

	private class TTTDiscoveryListener implements DiscoveryListener {

		@Override
		public void onStopDiscoveryFailed(String serviceType, int errorCode) {
			Util.showToastOnUiThread(TTTMainActivity.this,
					"Stop discovery failed with error code, " + errorCode);
		}

		@Override
		public void onStartDiscoveryFailed(String serviceType, int errorCode) {
			Util.showToastOnUiThread(TTTMainActivity.this,
					"Discovery failed with error code, " + errorCode);
		}

		@Override
		public void onServiceLost(NsdServiceInfo serviceInfo) {
			Util.showToastOnUiThread(TTTMainActivity.this, "Service lost");
		}

		@Override
		public void onServiceFound(NsdServiceInfo serviceInfo) {
			System.out.println("found " + serviceInfo);
			if (serviceInfo.getServiceName().equals(TTTApplication.TTT_SERVICE)) {
				NSDUtility.stopDiscovery(TTTMainActivity.this, this);
				System.out.println("ip address " + serviceInfo.getHost());
			}

		}

		@Override
		public void onDiscoveryStopped(String serviceType) {
			Util.showToastOnUiThread(TTTMainActivity.this, "Discover stopped");
		}

		@Override
		public void onDiscoveryStarted(String serviceType) {
			Util.showToastOnUiThread(TTTMainActivity.this, "Discovery started");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tttmain);
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

	TTTRegistrationListener registrationListener = new TTTRegistrationListener();

	private void startGame() {
		NSDUtility.startAndRegisterService(this, registrationListener);
	}

	TTTDiscoveryListener discoveryListener = new TTTDiscoveryListener();

	private void joinGame() {
		NSDUtility.discoverService(TTTMainActivity.this, discoveryListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (startedDiscovery) {
			NSDUtility.unResgiterService(TTTMainActivity.this,
					registrationListener);
		}
	}
}

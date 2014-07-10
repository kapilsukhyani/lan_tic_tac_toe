package com.enlighten.lan_tic_tac_toe.screen;

import com.enlighten.lan_tic_tac_toe.R;
import com.enlighten.lan_tic_tac_toe.R.id;
import com.enlighten.lan_tic_tac_toe.R.layout;
import com.enlighten.lan_tic_tac_toe.R.menu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class TTTMainActivity extends Activity implements View.OnClickListener {

	private Button startGame, joinGame;

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

	private void startGame() {

	}

	private void joinGame() {
		// TODO Auto-generated method stub

	}
}

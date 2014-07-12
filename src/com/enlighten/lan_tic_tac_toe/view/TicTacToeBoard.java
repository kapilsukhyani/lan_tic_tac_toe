package com.enlighten.lan_tic_tac_toe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.enlighten.lan_tic_tac_toe.OnRemoteChangeListener;
import com.enlighten.lan_tic_tac_toe.R;
import com.enlighten.lan_tic_tac_toe.TTTApplication.UserType;

public class TicTacToeBoard extends TableLayout implements
		OnRemoteChangeListener {
	private boolean locked = false;
	private TicTacToeSection[] sections = new TicTacToeSection[9];

	private UserType localUsertype;

	public TicTacToeBoard(Context context) {
		super(context);
		init();
	}

	public TicTacToeBoard(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
	}

	public void init() {
		if (!isInEditMode()) {
			View board = ((LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE)).inflate(
					R.layout.tic_tac_toe_board, null);
			View row1 = board.findViewById(R.id.row1);
			View row2 = board.findViewById(R.id.row2);
			View row3 = board.findViewById(R.id.row3);
			// setLayoutParams(board.getLayoutParams());
			((TableLayout) board).removeAllViews();
			setOrientation(((TableLayout) board).getOrientation());
			setWeightSum(((TableLayout) board).getWeightSum());
			addView(row1);
			addView(row2);
			addView(row3);
			int index1, index2, index3;
			for (int i = 0; i < 3; i++) {
				index1 = 0 + i;
				index2 = 3 + i;
				index3 = 6 + i;
				sections[index1] = ((TicTacToeSection) ((TableRow) row1)
						.getChildAt(i));
				sections[index2] = ((TicTacToeSection) ((TableRow) row2)
						.getChildAt(i));
				sections[index3] = ((TicTacToeSection) ((TableRow) row3)
						.getChildAt(i));

				sections[index1].setBoard(this);
				sections[index2].setBoard(this);
				sections[index3].setBoard(this);

				sections[index1].setSectionIndex(index1);
				sections[index3].setSectionIndex(index2);
				sections[index2].setSectionIndex(index3);
			}

		}
	}

	// locks the board, does not allow editing
	public synchronized void lock() {
		locked = true;
	}

	public synchronized void unLock() {
		locked = false;
	}

	public synchronized boolean isLocked() {
		return locked;
	}

	/**
	 * Start the game if you are first player, it will lock the board till next
	 * user arrives
	 */
	public void startGame() {
		this.localUsertype = UserType.FirstUser;
		this.lock();
	}

	/**
	 * Join the game if you are second user, it will notify the first user that
	 * second user is ready, it will lock the board unless first user plays its
	 * turn
	 */
	public void joinGame() {
		this.localUsertype = UserType.SecondUser;
		this.lock();
		sendJoinedGameNotification();
	}

	private void sendJoinedGameNotification() {

	}

	@Override
	public void onRemoteWon(int sectionNo) {
		sections[sectionNo].onRemoteTap();
		Toast.makeText(getContext(), "Remote user won the game", 3000).show();

	}

	@Override
	public void onRemoteMarked(int sectionNo) {
		sections[sectionNo].onRemoteTap();
		this.unLock();

	}

	@Override
	public void onRemoteReady() {
		this.unLock();
	}

	/**
	 * Send mark command to remote user and locks the board
	 * 
	 * @param sectionNo
	 *            section got marked
	 */
	public void sectionMarked(int sectionNo) {
		this.lock();
		sendSectionMarkedNotification(sectionNo);
	}

	private void sendSectionMarkedNotification(int sectionNo) {

	}

}

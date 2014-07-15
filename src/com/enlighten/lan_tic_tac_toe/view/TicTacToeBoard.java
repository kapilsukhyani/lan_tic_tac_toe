package com.enlighten.lan_tic_tac_toe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.enlighten.lan_tic_tac_toe.GameProtocol;
import com.enlighten.lan_tic_tac_toe.OnRemoteChangeListener;
import com.enlighten.lan_tic_tac_toe.R;
import com.enlighten.lan_tic_tac_toe.TTTCommunicationChannel;
import com.enlighten.lan_tic_tac_toe.Util;

public class TicTacToeBoard extends TableLayout implements
		OnRemoteChangeListener {
	private boolean locked = false;
	private TicTacToeSection[] sections = new TicTacToeSection[9];

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
				sections[index2].setSectionIndex(index2);
				sections[index3].setSectionIndex(index3);
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

	@Override
	public void onRemoteWon(int sectionNo) {
		sections[sectionNo].onRemoteTap();
		Util.showToast(getContext(), "Remote user won the game");

	}

	@Override
	public void onRemoteMarked(int sectionNo) {
		sections[sectionNo].onRemoteTap();
		Util.showToast(getContext(), "Its your turn now");
		this.unLock();

	}

	@Override
	public void onRemoteReady() {
		this.unLock();
		Util.showToast(getContext(),
				"Board is unlocked as remote user joined the game");
	}

	/**
	 * Send mark command to remote user and locks the board
	 * 
	 * @param sectionNo
	 *            section got marked
	 */
	public void sectionMarked(int sectionNo) {
		this.lock();
		if (hasUserWon(sectionNo)) {
			Util.showToast(getContext(), "Congratulations, you won the game");
			sendUserWonNotification(sectionNo);
		} else {
			sendSectionMarkedNotification(sectionNo);
		}
	}

	private void sendUserWonNotification(int sectionNo) {
		String command = GameProtocol.USER_WON_COMMAND
				.replace(GameProtocol.SECTION_NO_PLACE_HOLDER,
						String.valueOf(sectionNo));
		TTTCommunicationChannel.sendCommand(command);
	}

	private void sendSectionMarkedNotification(int sectionNo) {
		String command = GameProtocol.MARK_SECTION_COMMAND
				.replace(GameProtocol.SECTION_NO_PLACE_HOLDER,
						String.valueOf(sectionNo));
		TTTCommunicationChannel.sendCommand(command);
	}

	private boolean hasUserWon(int sectionNo) {
		return (commonChekedTypeRow(sectionNo)
				|| commonCheckedTypeCol(sectionNo) || commonCheckedTypeDiagonal(sectionNo));
	}

	private boolean commonChekedTypeRow(int sectionNo) {
		int rowNo = sectionNo / 3;
		rowNo = rowNo + 2 * rowNo;
		boolean commonRow = true;
		for (int i = rowNo; i <= rowNo + 1; i++) {
			commonRow = commonRow
					&& sections[i].getSectionState().equals(
							sections[i + 1].getSectionState());
		}

		return commonRow;
	}

	private boolean commonCheckedTypeCol(int sectionNo) {
		int colNo = sectionNo % 3;
		boolean commonCol = true;
		for (int i = colNo; i <= colNo + 3; i += 3) {
			commonCol = commonCol
					&& sections[i].getSectionState().equals(
							sections[i + 3].getSectionState());
		}

		return commonCol;
	}

	private boolean commonCheckedTypeDiagonal(int sectionNo) {
		if (sectionNo != 0 && sectionNo != 4 && sectionNo != 7) {
			return false;
		} else {
			if (sections[0].getSectionState().equals(
					sections[4].getSectionState())
					&& sections[4].getSectionState().equals(
							sections[7].getSectionState())) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void onSentToRemoteSuccess(int sectionNo) {

		// only if it was a mark command
		if (sectionNo != -1) {

		}

	}

	@Override
	public void onSentToRemoteFailed(int sectionNo) {
		sections[sectionNo].onRemoteSentFailed();
	}

}

package com.enlighten.lan_tic_tac_toe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.enlighten.lan_tic_tac_toe.OnRemoteUserTap;
import com.enlighten.lan_tic_tac_toe.TTTApplication;
import com.enlighten.lan_tic_tac_toe.TTTApplication.UserType;

public class TicTacToeSection extends Button implements OnGestureListener,
		OnDoubleTapListener, OnTouchListener, OnRemoteUserTap {
	private GestureDetector mGestureDetector;
	private TicTacToeBoard board;
	private int sectionIndex;
	private final int FIRST_USER_IMAGE = android.R.color.holo_blue_dark,
			SECOND_USER_IMAGE = android.R.color.holo_green_dark,
			NEUTRAL_BACKGROUND = android.R.color.holo_purple;

	private int localSectionCheckedBackground, remoteSectionCheckedBackground;
	private SectionState localSectionCheckedState, remoteSectionCheckedState;

	public static enum SectionState {
		NEUTRAL, CHECKED_POSITIVE, CHECKED_NEGATIVE
	};

	private SectionState sectionState;

	public TicTacToeSection(Context context) {
		super(context);
		init();
	}

	public TicTacToeSection(Context context, AttributeSet set) {
		super(context, set);
		init();
	}

	public TicTacToeSection(Context context, AttributeSet set, int defaultStyle) {
		super(context, set, defaultStyle);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			setOnTouchListener(this);
			sectionState = SectionState.NEUTRAL;
			if (((TTTApplication) getContext().getApplicationContext())
					.getUserType().equals(UserType.FirstUser)) {
				localSectionCheckedState = SectionState.CHECKED_POSITIVE;
				remoteSectionCheckedState = SectionState.CHECKED_NEGATIVE;
				localSectionCheckedBackground = FIRST_USER_IMAGE;
				remoteSectionCheckedBackground = SECOND_USER_IMAGE;
			} else {
				localSectionCheckedState = SectionState.CHECKED_NEGATIVE;
				remoteSectionCheckedState = SectionState.CHECKED_POSITIVE;
				localSectionCheckedBackground = SECOND_USER_IMAGE;
				remoteSectionCheckedBackground = FIRST_USER_IMAGE;

			}
			mGestureDetector = new GestureDetector(getContext(), this);
		}
	}

	public void setBoard(TicTacToeBoard board) {
		this.board = board;
	}

	public void setSectionIndex(int sectionIndex) {
		this.sectionIndex = sectionIndex;
	}

	public SectionState getSectionState() {
		return sectionState;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Toast.makeText(getContext(), "double_tapped", 2000).show();
		if (!sectionState.equals(SectionState.NEUTRAL)) {
			Toast.makeText(getContext(), "The section is already marked", 2000)
					.show();
			return false;
		}

		changeSectionState(localSectionCheckedBackground,
				localSectionCheckedState);
		board.sectionMarked(sectionIndex);

		return true;
	}

	private void changeSectionState(int background, SectionState sectionState) {
		setBackgroundColor(getContext().getResources().getColor(background));
		this.sectionState = sectionState;
		invalidate();
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!board.isLocked()) {
			mGestureDetector.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public void onRemoteTap() {
		changeSectionState(remoteSectionCheckedBackground,
				remoteSectionCheckedState);
	}

	public void onRemoteSentFailed() {
		changeSectionState(NEUTRAL_BACKGROUND, SectionState.NEUTRAL);
	}
}

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

public class TicTacToeSection extends Button implements OnGestureListener,
		OnDoubleTapListener, OnTouchListener, OnRemoteUserTap {
	private int section_position_x;
	private int section_position_y;
	private GestureDetector mGestureDetector;
	private TicTacToeBoard board;
	private boolean checked = false;
	private int sectionIndex;

	public static enum SectionState {
		NEUTRAL, CHECKED
	};

	public static enum CheckedType {
		POSITIVE, NEGATIVE
	}

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
		setOnTouchListener(this);
		mGestureDetector = new GestureDetector(getContext(), this);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setBoard(TicTacToeBoard board) {
		this.board = board;
	}

	public void setSectionIndex(int sectionIndex) {
		this.sectionIndex = sectionIndex;
	}

	private void setState(SectionState state) {
		if (sectionState.NEUTRAL == state) {
			this.sectionState = state;
			invalidate();
		}
	}

	public int getSection_position_x() {
		return section_position_x;
	}

	public int getSection_position_y() {
		return section_position_y;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Toast.makeText(getContext(), "double_tapped", 2000).show();
		if (isChecked()) {
			Toast.makeText(getContext(), "The section is already marked", 2000)
					.show();
			return false;
		}
		board.sectionMarked(sectionIndex);
		return true;
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
	}
}

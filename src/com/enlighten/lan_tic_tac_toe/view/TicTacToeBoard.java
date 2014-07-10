package com.enlighten.lan_tic_tac_toe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class TicTacToeBoard extends ViewGroup implements
		GestureDetector.OnDoubleTapListener, OnTouchListener {
	private boolean locked = false;
	private GestureDetector gestureDetector;
	private TicTacToeSection[] sections = new TicTacToeSection[9];

	public TicTacToeBoard(Context context) {
		super(context);
		init();
	}

	public TicTacToeBoard(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
	}

	public TicTacToeBoard(Context context, AttributeSet attributeSet, int arg) {
		super(context, attributeSet, arg);
		init();
	}

	public void init() {
		setOnTouchListener(this);
		gestureDetector = new GestureDetector(getContext(), null);
		gestureDetector.setOnDoubleTapListener(this);
		int index = 1;
		int row = 0, col = 0;
		int rowCount = 3;
		for (TicTacToeSection section : sections) {
			if (index % rowCount == 0) {
				row++;
				col = 0;
			}
			section = new TicTacToeSection(getContext(), row, col);
			addView(section);

			index++;
			col++;

		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	// locks the board, does not allow editing
	public void lock() {
		locked = true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!locked) {
			this.gestureDetector.onTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

}

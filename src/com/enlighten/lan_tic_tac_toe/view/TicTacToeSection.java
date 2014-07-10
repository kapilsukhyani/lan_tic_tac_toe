package com.enlighten.lan_tic_tac_toe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class TicTacToeSection extends View {
	private int section_position_x;
	private int section_position_y;

	public static enum SectionState {
		NEUTRAL, CHECKED
	};
	
	public static enum CheckedType{
		POSITIVE,
		NEGATIVE
	}

	private SectionState sectionState;

	public TicTacToeSection(Context context, int position_x, int position_y) {
		super(context);
		init(position_x, position_y);
	}

	private void init(int position_x, int position_y) {
		this.section_position_x = position_x;
		this.section_position_y = position_y;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
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
}

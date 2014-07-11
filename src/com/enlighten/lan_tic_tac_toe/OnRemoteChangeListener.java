package com.enlighten.lan_tic_tac_toe;

public interface OnRemoteChangeListener {

	/**
	 * Callback to be called when remote user won with the mark specified by
	 * sectionNo
	 * 
	 * @param sectionNo
	 *            , specifies a section marked by remote user to win the game
	 */
	public void onRemoteWon(int sectionNo);

	/**
	 * Callback to be called when remote user marked a section
	 * 
	 * @param sectionNo
	 *            section number marked by remote user
	 */
	public void onRemoteMarked(int sectionNo);

	/**
	 * Callback to be called when remote user is ready and game can be started
	 */

	public void onRemoteReady();

}

package com.enlighten.lan_tic_tac_toe;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

import android.util.Log;

public class TTTCommunicationChannel {

	private static boolean initialized = false;
	private static SocketChannel socketChannel;
	private static Thread channelSelectionThread;
	private static final String TAG = TTTCommunicationChannel.class.getName();

	public static synchronized void initChannel(
			final SocketChannel socketChannel) {

		try {
			TTTCommunicationChannel.socketChannel = socketChannel;
			final int interestSet = SelectionKey.OP_READ
					| SelectionKey.OP_WRITE;
			socketChannel.configureBlocking(false);
			final Selector selector = Selector.open();
			final SelectionKey channelSelectionKey = socketChannel.register(
					selector, interestSet);

			channelSelectionThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (true && !Thread.interrupted()) {
							if (selector.select() > 0) {
								Set<SelectionKey> selectedKeys = selector
										.selectedKeys();

								for (SelectionKey selectionKey : selectedKeys) {
									if (selectionKey.interestOps() == interestSet
											&& selectionKey
													.equals(channelSelectionKey)) {
										onReadWrite();

									}
								}
							}
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});

			channelSelectionThread.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized boolean isCommunicationChannelInitialized() {
		return initialized;
	}

	public static void onReadWrite() {
		Log.d(TAG, "in read write");
	}

}

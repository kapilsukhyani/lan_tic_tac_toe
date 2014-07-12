package com.enlighten.lan_tic_tac_toe;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import android.util.Log;

public class TTTNSDService {

	private static final String TAG = TTTNSDService.class.getName();
	private static ServerSocketChannel serverSocketChannel;
	private static Thread selectorThread;

	public static void interrupService() {
		selectorThread.interrupt();
	}

	public static boolean initializeServcie() {
		try {
			Log.d(TAG, "Initializing service");
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(null);
			serverSocketChannel.configureBlocking(false);
			final Selector selector = Selector.open();
			final SelectionKey connectionAcceptSelectionKey = serverSocketChannel
					.register(selector, SelectionKey.OP_ACCEPT);

			selectorThread = new Thread(new Runnable() {

				@Override
				public void run() {

					while (true && !Thread.currentThread().isInterrupted()) {

						try {
							int noOfChannels = selector.select();

							if (noOfChannels <= 0)
								continue;

							Set<SelectionKey> selectedKeys = selector
									.selectedKeys();

							for (SelectionKey selectionKey : selectedKeys) {

								if (selectionKey.interestOps() == SelectionKey.OP_ACCEPT) {
									// got a new connection in server socket
									// channel
									Log.d(TAG, "Received new user connection");

									SocketChannel clientSocketChannel = ((ServerSocketChannel) selectionKey
											.channel()).accept();
									if (null != clientSocketChannel) {
										TTTCommunicationChannel
												.initChannel(clientSocketChannel);

									}

								}

							}

						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}
			});
			selectorThread.start();
			Log.d(TAG, "Service initialization completed");
			Log.d(TAG, "Service started on "
					+ serverSocketChannel.socket().getLocalPort());

		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	public static int getServicePort() {
		if (null != serverSocketChannel) {

			return serverSocketChannel.socket().getLocalPort();
		} else {
			return -1;
		}
	}

	private static final class TTTServiceRequestHandler implements Runnable {

		@Override
		public void run() {
			try {
				serverSocketChannel.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}

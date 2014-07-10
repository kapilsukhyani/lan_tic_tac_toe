package com.enlighten.lan_tic_tac_toe;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class TTTNSDService {

	private static ServerSocketChannel serverSocketChannel;
	private static Thread selectorThread;

	public static void interrupService() {
		selectorThread.interrupt();
	}

	public static boolean initializeServcie() {
		try {
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

									SocketChannel clientSocketChannel = ((ServerSocketChannel) selectionKey
											.channel()).accept();
									if (null != clientSocketChannel) {
										clientSocketChannel
												.configureBlocking(false);
										SelectionKey clientSocketSelectionKey = clientSocketChannel
												.register(
														selector,
														SelectionKey.OP_READ
																| selectionKey.OP_WRITE);

									}

								}

								else {
									// client socket is ready to be read or
									// written

									SocketChannel clientSocketChannel = (SocketChannel) selectionKey
											.channel();
									
									

								}

							}

						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}
			});
			selectorThread.start();

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

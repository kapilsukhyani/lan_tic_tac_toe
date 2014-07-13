package com.enlighten.lan_tic_tac_toe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

import android.os.Handler;
import android.os.Message;

import com.enlighten.lan_tic_tac_toe.screen.GameActivity.ChannelReadyListener;

public class TTTCommunicationChannel {

	private static boolean ready = false;
	private static SocketChannel socketChannel;
	private static Thread channelSelectionThread;
	private static Selector selector;
	private static SelectionKey channelSelectionKey;
	private static final int interestSet = SelectionKey.OP_READ
			| SelectionKey.OP_WRITE;
	private static final String TAG = TTTCommunicationChannel.class.getName();
	private static Handler readWriteReadyListener;

	public static void initChannel(final SocketChannel socketChannel) {

		try {
			TTTCommunicationChannel.socketChannel = socketChannel;

			socketChannel.configureBlocking(false);
			selector = Selector.open();
			channelSelectionKey = socketChannel.register(selector, interestSet);

			channelSelectionThread = new Thread(new ChannelSelectionRunnable());

			channelSelectionThread.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ChannelSelectionRunnable implements Runnable {

		@Override
		public void run() {

			try {
				while (true && !Thread.interrupted()) {
					if (selector.select() > 0) {
						Set<SelectionKey> selectedKeys = selector
								.selectedKeys();

						for (SelectionKey selectionKey : selectedKeys) {
							if (selectionKey.interestOps() == interestSet
									&& selectionKey.equals(channelSelectionKey)) {

								synchronized (TTTCommunicationChannel.class) {
									try {
										ready = true;
										notifyListener();
										TTTCommunicationChannel.class.wait();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									break;
								}

							}
						}

					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static synchronized boolean isCommunicationChannelReady(
			Handler readWriteReadyListener) {
		TTTCommunicationChannel.readWriteReadyListener = readWriteReadyListener;
		return ready;
	}

	private static void notifyListener() {

		if (null != readWriteReadyListener) {
			readWriteReadyListener
					.sendMessage(Message.obtain(readWriteReadyListener,
							ChannelReadyListener.CHANNEL_READY));
		}

	}

	/**
	 * Called by user to read the command when channel is ready, call
	 * isCommunicationChannelReady to be sure that channel is ready before
	 * calling this method
	 * 
	 * @return the command read
	 */
	public static String readCommand() {
		String result = null;
		try {
			ByteBuffer buf = ByteBuffer.allocate(48);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int bytesRead;
			bytesRead = socketChannel.read(buf);
			while (bytesRead > 0) {
				buf.flip(); // make buffer ready for read
				while (buf.hasRemaining()) {
					byteArrayOutputStream.write(buf.get()); // read 1 byte at a
															// time
				}

				buf.clear(); // make buffer ready for writing
				bytesRead = socketChannel.read(buf);
			}

			result = byteArrayOutputStream.toString();
			byteArrayOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Called by the user to write the command, call isCommunicationChannelReady
	 * to be sure that channel is ready before calling this method
	 * 
	 * @param command
	 * @return
	 */
	public static boolean writeCommand(String command) {

		boolean written = false;
		try {
			ByteBuffer buffer = ByteBuffer.allocate(command.getBytes().length);
			buffer.clear();
			buffer.put(command.getBytes());
			buffer.flip();

			while (buffer.hasRemaining()) {
				socketChannel.write(buffer);
			}
			buffer.clear();
			written = true;
			synchronized (TTTCommunicationChannel.class) {
				ready = false;
				TTTCommunicationChannel.class.notifyAll();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return written;
	}

}

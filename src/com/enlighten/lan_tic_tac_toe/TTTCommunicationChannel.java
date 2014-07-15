package com.enlighten.lan_tic_tac_toe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.os.Handler;
import android.util.Log;

public class TTTCommunicationChannel {

	private static boolean ready = false;
	private static SocketChannel socketChannel;
	private static Thread channelReadThread, channelWriteThread;

	private static Selector readSelector, writeSelector;
	private static SelectionKey channelReadSelectionKey,
			channelWriteSelectionKey;
	private static final String TAG = TTTCommunicationChannel.class.getName();
	private static Handler uiHandler;

	private static List<String> commandQueue = new ArrayList<String>();
	private static OnRemoteChangeListener remoteChangeListener;

	public static void sendCommand(String message) {
		synchronized (commandQueue) {
			commandQueue.add(message);
			commandQueue.notifyAll();
		}

	}

	public static void registerOnRemoteChangeListener(
			OnRemoteChangeListener remoteChangeListener) {
		TTTCommunicationChannel.remoteChangeListener = remoteChangeListener;
	}

	public static void initChannel(final SocketChannel socketChannel) {

		try {
			TTTCommunicationChannel.socketChannel = socketChannel;

			socketChannel.configureBlocking(false);
			readSelector = Selector.open();
			writeSelector = Selector.open();
			channelReadSelectionKey = socketChannel.register(readSelector,
					SelectionKey.OP_READ);
			channelWriteSelectionKey = socketChannel.register(writeSelector,
					SelectionKey.OP_WRITE);

			channelReadThread = new Thread(new ChannelReadRunnable());
			channelReadThread.start();
			channelWriteThread = new Thread(new ChannelWriteRunnable());
			channelWriteThread.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ChannelReadRunnable implements Runnable {

		@Override
		public void run() {

			try {

				while (true && !Thread.interrupted()) {
					if (readSelector.select() > 0
							|| readSelector.selectedKeys().size() > 0) {
						Set<SelectionKey> selectedKeys = readSelector
								.selectedKeys();

						for (SelectionKey selectionKey : selectedKeys) {
							if (selectionKey.isReadable()
									&& selectionKey
											.equals(channelReadSelectionKey)) {

								synchronized (TTTCommunicationChannel.class) {
									try {
										ready = true;
										notifyListener(readCommand());
										TTTCommunicationChannel.class.wait();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									break;
								}

							}

						}

					} else {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					readSelector.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	private static class ChannelWriteRunnable implements Runnable {
		@Override
		public void run() {
			try {
				while (true && !Thread.interrupted()) {

					// selector may return 0 even when socketchannel is ready to
					// be written because its state has not been changed since
					// last select
					if (writeSelector.select() > 0
							|| writeSelector.selectedKeys().size() > 0) {
						Set<SelectionKey> selectedKeys = writeSelector
								.selectedKeys();

						for (SelectionKey selectionKey : selectedKeys) {
							if (selectionKey.isWritable()
									&& selectionKey
											.equals(channelWriteSelectionKey)) {

								synchronized (commandQueue) {
									try {
										while (commandQueue.isEmpty()) {
											commandQueue.wait();
										}

										final String command = commandQueue
												.get(0);
										if (writeCommand(command)) {
											commandQueue.remove(command);

											postRunnableToUiHandler(new Runnable() {

												@Override
												public void run() {
													remoteChangeListener
															.onSentToRemoteSuccess(GameProtocol
																	.getSctionNoFromCommand(command));

												}
											});

											synchronized (TTTCommunicationChannel.class) {
												TTTCommunicationChannel.class
														.notifyAll();
											}
										} else {
											postRunnableToUiHandler(new Runnable() {

												@Override
												public void run() {
													remoteChangeListener
															.onSentToRemoteFailed(GameProtocol
																	.getSctionNoFromCommand(command));

												}
											});

										}

									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}

							}
						}

					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writeSelector.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static synchronized boolean isCommunicationChannelReady(
			Handler readWriteReadyListener, OnRemoteChangeListener remoteChangeListener) {
		TTTCommunicationChannel.uiHandler = readWriteReadyListener;
		TTTCommunicationChannel.remoteChangeListener =remoteChangeListener;
		return ready;
	}

	private static void notifyListener(String data) {

		if (null != uiHandler && null != remoteChangeListener) {
			/*
			 * readWriteReadyListener.sendMessage(Message.obtain(
			 * readWriteReadyListener, ChannelReadyListener.CHANNEL_READY,
			 * data));
			 */

			GameProtocol.notifyListener(data, uiHandler, remoteChangeListener);
		}

	}

	/**
	 * Called by user to read the command when channel is ready, call
	 * isCommunicationChannelReady to be sure that channel is ready before
	 * calling this method
	 * 
	 * @return the command read
	 */
	private static String readCommand() {
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
	private static boolean writeCommand(String command) {
		Log.d(TAG, "Writing command " + command);
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

		} catch (IOException e) {
			e.printStackTrace();
		}

		return written;
	}

	public static void interrupt() {
		commandQueue.removeAll(commandQueue);
		commandQueue = null;
		ready = false;
		remoteChangeListener = null;
		uiHandler = null;
		channelReadThread.interrupt();
		channelWriteThread.interrupt();
		try {
			socketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void postRunnableToUiHandler(Runnable runnable) {
		if (null != uiHandler && null != remoteChangeListener) {
			uiHandler.post(runnable);
		}
	}

}

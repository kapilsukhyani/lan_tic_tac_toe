package com.enlighten.lan_tic_tac_toe;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.DiscoveryListener;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdManager.ResolveListener;
import android.net.nsd.NsdServiceInfo;
import android.os.Message;
import android.util.Log;

import com.enlighten.lan_tic_tac_toe.TTTApplication.UserType;
import com.enlighten.lan_tic_tac_toe.screen.TTTMainActivity;
import com.enlighten.lan_tic_tac_toe.screen.TTTMainActivity.SetupListener;

public class NSDUtility {

	public static final int SERVICE_NOT_SARTED_ERROR_CODE = 12345;
	public static final int DISCOVERED_SERVICE_HOST_NULL_ERROR_CODE = 12346;
	public static final int NOT_ABLE_TO_CONNECT_TO_REMOTE_SSERVICE_ERROR_CODE = 12347;
	public static final String SERVICE_TYPE = "_http._tcp.";
	private static NsdServiceInfo nsdServiceInfo;
	private static SetupListener setupListener;
	private static NsdManager nsdManager;
	private static UserType userType;
	private static final String TAG = NSDUtility.class.getName();

	private static enum NSDStates {
		Registering, Registered, UnRegistered, Discovering, Discovered, Resolving, Resolved
	}

	private static NSDStates nsdState;

	private static ResolveListener resolveListener = new ResolveListener() {

		@Override
		public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
			Log.d(TAG, "Not able to resolve service, " + errorCode);
			setupListener.sendMessage(getSetupFailureMessage(errorCode));
		}

		@Override
		public void onServiceResolved(NsdServiceInfo serviceInfo) {
			Log.d(TAG, "Service resolved " + serviceInfo);
			Log.d(TAG, "service ipaddress " + serviceInfo.getHost() + "  port "
					+ serviceInfo.getPort());
			if (serviceInfo.getHost() != null) {
				SocketChannel socketChannel = connectToService(serviceInfo);
				if (null != socketChannel) {
					TTTCommunicationChannel.initChannel(socketChannel);
					setupListener.sendMessage(getSetupSuccessfulMessage());
				} else {
					setupListener
							.sendMessage(getSetupFailureMessage(NOT_ABLE_TO_CONNECT_TO_REMOTE_SSERVICE_ERROR_CODE));
				}

			} else {
				setupListener
						.sendMessage(getSetupFailureMessage(DISCOVERED_SERVICE_HOST_NULL_ERROR_CODE));
			}

		}

	};

	private static SocketChannel connectToService(NsdServiceInfo serviceInfo) {

		try {
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true);
			socketChannel.connect(new InetSocketAddress(serviceInfo.getHost(),
					serviceInfo.getPort()));
			sendInit(socketChannel);
			return socketChannel;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	private static void sendInit(final SocketChannel socketChannel) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String command = "Init";
				try {
					
					Log.d(TAG, "Sending init");
					ByteBuffer buffer = ByteBuffer.allocate(command.getBytes().length);
					buffer.clear();
					buffer.put(command.getBytes());
					buffer.flip();

					while (buffer.hasRemaining()) {
						socketChannel.write(buffer);
					}
					buffer.clear();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private static RegistrationListener registrationListener = new RegistrationListener() {

		@Override
		public void onUnregistrationFailed(NsdServiceInfo serviceInfo,
				final int errorCode) {
			Log.d(TAG, "Unregistration failed with error, " + errorCode);

		}

		@Override
		public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
			nsdState = NSDStates.UnRegistered;
			Log.d(TAG, "Service unregistration succesful");

		}

		@Override
		public void onServiceRegistered(NsdServiceInfo serviceInfo) {
			nsdState = NSDStates.Registered;
			Log.d(TAG, "Service registration succesful, service listening on "
					+ serviceInfo.getHost() + ":" + serviceInfo.getPort());
			Log.d(TAG, "service name " + serviceInfo.getServiceName());
			setupListener.sendMessage(getSetupSuccessfulMessage());

		}

		@Override
		public void onRegistrationFailed(NsdServiceInfo serviceInfo,
				final int errorCode) {
			Log.d(TAG, "Registratioin failed with error code " + errorCode);
			setupListener.sendMessage(getSetupFailureMessage(errorCode));

		}
	};

	private static DiscoveryListener discoveryListener = new DiscoveryListener() {

		@Override
		public void onStopDiscoveryFailed(String serviceType, int errorCode) {
			Log.d(TAG, "Stop discovery failed with error code, " + errorCode);
		}

		@Override
		public void onStartDiscoveryFailed(String serviceType, int errorCode) {
			Log.d(TAG, "Discovery failed with error code, " + errorCode);
			nsdState = NSDStates.Discovered;
			setupListener.sendMessage(getSetupFailureMessage(errorCode));
		}

		@Override
		public void onServiceLost(NsdServiceInfo serviceInfo) {
			nsdState = NSDStates.Discovered;
			Log.d(TAG, "Service lost");
		}

		@Override
		public void onServiceFound(NsdServiceInfo serviceInfo) {
			nsdState = NSDStates.Discovered;
			Log.d(TAG, "found " + serviceInfo);
			if (serviceInfo.getServiceName().equals(
					NSDUtility.nsdServiceInfo.getServiceName())) {
				NSDUtility.resolveService(resolveListener, serviceInfo);
			}
		}

		@Override
		public void onDiscoveryStarted(String serviceType) {
			nsdState = NSDStates.Discovering;
			Log.d(TAG, "Discovery started");
		}

		@Override
		public void onDiscoveryStopped(String serviceType) {
			Log.d(TAG, "Discover stopped");
		}

	};

	public static void init(Context context) {
		nsdManager = getNsdManager(context);
		nsdServiceInfo = new NsdServiceInfo();
		nsdServiceInfo.setServiceName(TTTApplication.TTT_SERVICE);
		nsdServiceInfo.setServiceType(SERVICE_TYPE);
	}

	public static void start(UserType userType, SetupListener setupListener) {
		NSDUtility.setupListener = setupListener;
		if (userType.equals(UserType.FirstUser)) {
			NSDUtility.userType = UserType.FirstUser;
			startAndRegisterService();
		} else if (userType.equals(UserType.SecondUser)) {
			NSDUtility.userType = UserType.SecondUser;
			discoverService(discoveryListener);
		}

	}

	public static void stop() {
		if (NSDUtility.userType.equals(UserType.FirstUser)) {
			if (nsdState == NSDStates.Registered) {
				NSDUtility.unResgiterService();
			}
		} else if (NSDUtility.userType.equals(UserType.SecondUser)) {

		}
	}

	public static void startAndRegisterService() {
		if (TTTNSDService.initializeServcie()) {
			nsdServiceInfo.setPort(TTTNSDService.getServicePort());
			nsdState = NSDStates.Registering;
			NSDUtility.nsdManager.registerService(nsdServiceInfo,
					NsdManager.PROTOCOL_DNS_SD, registrationListener);
		} else {
			setupListener
					.sendMessage(getSetupFailureMessage(SERVICE_NOT_SARTED_ERROR_CODE));
		}
	}

	private static Message getSetupFailureMessage(int serviceNotSartedErrorCode) {
		Message message = Message.obtain();
		message.what = TTTMainActivity.SetupListener.SETUP_FAILED;
		message.arg1 = serviceNotSartedErrorCode;
		return message;
	}

	private static Message getSetupSuccessfulMessage() {
		Message message = Message.obtain();
		message.what = TTTMainActivity.SetupListener.SETUP_SUCCESSFUL;
		return message;
	}

	public static void unResgiterService() {
		NSDUtility.nsdManager.unregisterService(registrationListener);
	}

	public static void discoverService(DiscoveryListener listener) {
		NSDUtility.nsdManager.discoverServices(SERVICE_TYPE,
				NsdManager.PROTOCOL_DNS_SD, listener);
	}

	public static void stopDiscovery(Context context, DiscoveryListener listener) {
		getNsdManager(context).stopServiceDiscovery(listener);
	}

	public static void resolveService(ResolveListener listener,
			NsdServiceInfo serviceInfo) {
		NSDUtility.nsdManager.resolveService(serviceInfo, listener);
	}

	private static NsdManager getNsdManager(Context context) {
		return (NsdManager) context.getSystemService(Context.NSD_SERVICE);
	}
}

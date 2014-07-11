package com.enlighten.lan_tic_tac_toe;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.DiscoveryListener;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdServiceInfo;

public class NSDUtility {

	public static final int SERVICE_NOT_SARTED_ERROR_CODE = 12345;
	public static final String SERVICE_TYPE = "_http._tcp.";

	public static void startAndRegisterService(Context context,
			RegistrationListener listener) {

		if (TTTNSDService.initializeServcie()) {

			NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
			nsdServiceInfo.setServiceName(TTTApplication.TTT_SERVICE);
			nsdServiceInfo.setPort(TTTNSDService.getServicePort());
			nsdServiceInfo.setServiceType(SERVICE_TYPE);
			NsdManager nsdManager = (NsdManager) context
					.getSystemService(Context.NSD_SERVICE);
			nsdManager.registerService(nsdServiceInfo,
					NsdManager.PROTOCOL_DNS_SD, listener);
		} else {
			listener.onRegistrationFailed(null, SERVICE_NOT_SARTED_ERROR_CODE);
		}
	}

	public static void discoverService(Context context,
			DiscoveryListener listener) {

		final NsdManager nsdManager = (NsdManager) context
				.getSystemService(Context.NSD_SERVICE);
		nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD,
				listener);
	}

	public static void unResgiterService(Context context,
			RegistrationListener listener) {
		final NsdManager nsdManager = (NsdManager) context
				.getSystemService(Context.NSD_SERVICE);
		nsdManager.unregisterService(listener);
	}

	public static void stopDiscovery(Context context, DiscoveryListener listener) {
		final NsdManager nsdManager = (NsdManager) context
				.getSystemService(Context.NSD_SERVICE);
		nsdManager.stopServiceDiscovery(listener);
	}

}

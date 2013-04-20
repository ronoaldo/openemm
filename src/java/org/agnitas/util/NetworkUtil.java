package org.agnitas.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class NetworkUtil {
	public static List<InetAddress> listLocalInetAddresses() throws SocketException {
		List<InetAddress> list = new Vector<InetAddress>();
		
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while( interfaces.hasMoreElements()) {
			listInetAddressesForInterface( list, interfaces.nextElement());
		}
		
		return list;
	}
	
	private static void listInetAddressesForInterface( List<InetAddress> list, NetworkInterface iface) {
		Enumeration<InetAddress> addresses = iface.getInetAddresses();
		
		while( addresses.hasMoreElements()) {
			list.add( addresses.nextElement());
		}
	}
}

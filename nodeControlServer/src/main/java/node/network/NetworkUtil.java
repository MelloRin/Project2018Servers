package node.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;

import node.NodeControlCore;

public class NetworkUtil
{
	public static final String DEFAULT_SUBNET = "192.168.0";
	
	public static final String PROP_broadcastPort = "broadcastPort";
	public static final String PROP_unicastPort = "unicastPort";
	public static final String PROP_networkInterface = "networkInterface";
	public static final String PROP_defaultAddr = "defaultAddr";
	
	private static String Subnet = null;
	private static InetAddress defaultAddr = null;
	private static InetAddress BROADCAST_IA = null;
	private static InetAddress ALL_IA = null;
	private static String nic = null;
	private static int broadcastPort;
	private static int unicastPort;
	
	static
	{
		String defaultInet = String.format("%s.%s",DEFAULT_SUBNET, NodeControlCore.getProp(PROP_defaultAddr));
		try
		{
			defaultAddr = InetAddress.getByName(defaultInet);
			ALL_IA = InetAddress.getByName("0.0.0.0");
			broadcastPort = Integer.parseInt(NodeControlCore.getProp(PROP_broadcastPort));
			unicastPort = Integer.parseInt(NodeControlCore.getProp(PROP_unicastPort));
			nic = NodeControlCore.getProp(PROP_networkInterface);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
	
	private static boolean isSetSubnet(String subnet)
	{
		if(Subnet == null || !Subnet.equals(subnet))
		{
			Subnet = subnet;
			return false;
		}
		return true;
	}
	
	public static InetAddress defaultAddr()
	{
		return defaultAddr;
	}
	
	public static InetAddress broadcastIA(String subnet)
	{
		if(!isSetSubnet(subnet) || BROADCAST_IA == null)
		{
			try
			{
				BROADCAST_IA = InetAddress.getByName(Subnet + ".255");
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
		}
		return BROADCAST_IA;
	}
	
	public static String getNIC()
	{
		return nic;
	}
	
	public static InetAddress allIA()
	{
		return ALL_IA;
	}
	
	public static int broadcastPort()
	{
		return broadcastPort;
	}
	
	public static int unicastPort()
	{
		return unicastPort;
	}
	
	public static NetworkInterface getNetworkInterface(String name)
	{
		Enumeration<NetworkInterface> nets = null;
		NetworkInterface findInterface = null;

		try
		{
			nets = NetworkInterface.getNetworkInterfaces();
		}
		catch (SocketException e)
		{
			NetworkManager.logger.log(Level.SEVERE, "네트워크 인터페이스 목록을 가져올 수 없습니다.", e);
		}
		if (nets == null)
			return null;

		StringBuffer netInfoBuf = new StringBuffer();
		netInfoBuf.append("네트워크 인터페이스 스캔\n");
		while (nets.hasMoreElements())
		{
			NetworkInterface net = nets.nextElement();
			try
			{
				if (!net.isUp())
					continue;
			}
			catch (SocketException e)
			{
				continue;
			}

			if (net.getName().equals(name))
			{
				findInterface = net;
				netInfoBuf.append("<SELECT>");
			}

			netInfoBuf.append("  Name:");
			netInfoBuf.append(net.getName());
			netInfoBuf.append(" Addr=>\n");

			int count = 0;
			Enumeration<InetAddress> addressItr = net.getInetAddresses();
			while (addressItr.hasMoreElements())
			{
				++count;
				InetAddress addr = addressItr.nextElement();
				netInfoBuf.append("    IP");
				netInfoBuf.append(count);
				netInfoBuf.append(": ");
				netInfoBuf.append(addr.getHostAddress());
				netInfoBuf.append("\n");
			}
			// netInfoBuf.deleteCharAt(netInfoBuf.length() - 1);

		}
		NetworkManager.logger.log(Level.INFO, netInfoBuf.toString());

		return findInterface;
	}
	
	public static Inet4Address getInterface4Addr(NetworkInterface network)
	{
		Enumeration<InetAddress> addrList = network.getInetAddresses();
		Inet4Address addr4 = null;
		InetAddress addr;
		while(addrList.hasMoreElements())
		{
			addr = addrList.nextElement();
			if(addr instanceof Inet4Address)
			{
				addr4 = (Inet4Address) addr;
				break;
			}
		}
		return addr4;
		
	}
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes, int end) {
	    StringBuffer buf = new StringBuffer();
	    for ( int j = 0; j < end; j++ ) {
	        int v = bytes[j] & 0xFF;
	        buf.append(hexArray[v >>> 4]);
	        buf.append(hexArray[v & 0x0F]);
	        buf.append(' ');
	    }
	    return buf.toString();
	}
}

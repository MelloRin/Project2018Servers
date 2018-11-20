package node.network.socketHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import node.NodeControlCore;
import node.bash.CommandExecutor;
import node.log.LogWriter;
import node.network.NetworkManager;
import node.network.NetworkUtil;
import node.network.packet.PacketUtil;

public class BroadcastHandler
{
	public static final Logger logger = LogWriter.createLogger(BroadcastHandler.class, "broadcastS");
	
	private static final String PROP_BroadcastIPstart = "broadcastIPstart";
	private static final String PROP_BroadcastIPend = "broadcastIPend";
	
	private static final String VNIC = "node_echo";
	
	private int ipStart;
	private int ipEnd;
	
	private int nowIP;
	
	private DatagramSocket socket;

	private boolean isWork;

	private BiConsumer<InetAddress, byte[]> receiveCallback;
	
	private Random random;
	
	public BroadcastHandler(BiConsumer<InetAddress, byte[]> receiveCallback)
	{
		this.receiveCallback = receiveCallback;
		
		this.socket = null;
		this.isWork = false;
		
		this.random = new Random();
	}
	
	public void start(InetAddress addr)
	{
		if(this.isWork) return;
		this.isWork = true;
		
		logger.log(Level.INFO, "브로드캐스트 송신기 로드");
		
		this.ipStart = Integer.parseInt(NodeControlCore.getProp(PROP_BroadcastIPstart));
		this.ipEnd = Integer.parseInt(NodeControlCore.getProp(PROP_BroadcastIPend));
		
		//this.nowIP = this.ipStart + this.random.nextInt(this.ipEnd - this.ipStart + 1);
		//this.ipJump();
		
		try
		{
			this.socket = new DatagramSocket(null);
			this.socket.bind(new InetSocketAddress(addr, NetworkUtil.broadcastPort()));
		}
		catch (SocketException e)
		{
			logger.log(Level.SEVERE, "바인딩 실패", e);
			return;
		}
		
	}
	
	public synchronized void sendMessage(byte[] data)
	{
		if(!this.isWork)
		{
			logger.log(Level.WARNING, "소켓 닫힘");
			return;
		}
		
		DatagramPacket packet = new DatagramPacket(data, data.length);
		packet.setAddress(NetworkUtil.broadcastIA(NetworkUtil.DEFAULT_SUBNET));
		packet.setPort(NetworkUtil.broadcastPort());
		try
		{
			this.socket.send(packet);
		}
		catch (IOException e)
		{
			logger.log(Level.SEVERE, "브로드캐스트 실패", e);
		}
	}
	
	/*public synchronized void ipJump()
	{
		int beforeIP = this.nowIP;
		this.nowIP = this.ipStart + this.random.nextInt(this.ipEnd - this.ipStart);
		if(this.nowIP >= beforeIP) ++this.nowIP;
		//IP이전꺼랑 안겹치게 랜덤 점프 하는 로직
		String nowAddr = String.format("%s.%d", NetworkUtil.DEFAULT_SUBNET, this.nowIP);
		String ipSetCommand = String.format("ifconfig %s:%s %s/24", NetworkUtil.getNIC(), VNIC, nowAddr);
		try
		{
			CommandExecutor.executeCommand(ipSetCommand, false);
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "가상NIC설정 실패", e);
			return;
		}
		try
		{
			if(this.socket != null && !this.socket.isClosed())
			{
				this.socket.close();
			}
			this.socket = new DatagramSocket(null);
			this.socket.bind(new InetSocketAddress(nowAddr, NetworkUtil.broadcastPort()));
			this.socket.setBroadcast(true);
		}
		catch (IllegalStateException | IOException e)
		{
			logger.log(Level.SEVERE, "소켓 열기 실패", e);
			return;
		}
	}*/

	public void stop()
	{
		if(!this.isWork) return;
		this.isWork = false;
		
		logger.log(Level.INFO, "브로드캐스트 송신기 종료");
		
		if(this.socket != null && !this.socket.isClosed())
		{
			this.socket.close();
		}
	}
}
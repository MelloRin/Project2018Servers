package node.network.socketHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import node.NodeControlCore;
import node.bash.CommandExecutor;
import node.log.LogWriter;
import node.network.NetworkManager;
import node.network.NetworkUtil;
import node.network.packet.PacketUtil;

public class IPJumpBroadcast
{
	public static final Logger logger = LogWriter.createLogger(IPJumpBroadcast.class, "broadcast");
	
	private static final String PROP_BroadcastIPstart = "broadcastIPstart";
	private static final String PROP_BroadcastIPend = "broadcastIPend";
	
	private static final String VNIC = "node_broadcast";
	
	private int ipStart;
	private int ipEnd;
	
	private int nowIP;
	
	private DatagramSocket socket;
	private int port;

	private boolean isWork;

	private BiConsumer<InetAddress, byte[]> receiveCallback;
	
	public IPJumpBroadcast(BiConsumer<InetAddress, byte[]> receiveCallback)
	{
		this.receiveCallback = receiveCallback;
		
		this.socket = null;
		this.isWork = false;
	}
	
	public void start()
	{
		if(this.isWork) return;
		this.isWork = true;
		
		this.ipStart = Integer.parseInt(NodeControlCore.getProp(PROP_BroadcastIPstart));
		this.ipEnd = Integer.parseInt(NodeControlCore.getProp(PROP_BroadcastIPend));
		
		this.nowIP = this.ipStart;

		logger.log(Level.INFO, "브로드캐스트 소켓 전송기 로드");
		this.port = Integer.parseInt(NodeControlCore.getProp(NetworkManager.PROP_INFOBROADCAST_PORT));

	}
	
	public synchronized void sendMessage(boolean jump, byte[] stream)
	{
		if(!this.isWork)
		{
			logger.log(Level.WARNING, "소켓 닫힘");
			return;
		}
		
		String nowAddr = String.format("%s.%d", NetworkUtil.DEFAULT_SUBNET, this.nowIP);
		
		if(jump)
		{
			++this.nowIP;
			if(this.nowIP > this.ipEnd)
			{
				this.nowIP = this.ipStart;
			}
			String ipSetCommand = String.format("ifconfig %s:%s %s/24", NetworkManager.getNIC(), VNIC, nowAddr);
			try
			{
				CommandExecutor.executeCommand(ipSetCommand);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "가상NIC설정 실패", e);
				return;
			}
			try
			{
				this.socket = new DatagramSocket(null);
				this.socket.bind(new InetSocketAddress(nowAddr, this.port));
				this.socket.setBroadcast(true);
			}
			catch (IllegalStateException | IOException e)
			{
				logger.log(Level.SEVERE, "소켓 열기 실패", e);
				return;
			}
		}
		
		DatagramPacket packet = new DatagramPacket(stream, stream.length);
		packet.setAddress(NetworkUtil.broadcastIA(NetworkUtil.DEFAULT_SUBNET));
		packet.setPort(this.port);
		logger.log(Level.INFO, "브로드케스트..");
		try
		{
			this.socket.send(packet);
		}
		catch (IOException e)
		{
			logger.log(Level.SEVERE, "브로드캐스트 실패", e);
		}
	}
	
	public void run()
	{
		/*logger.log(Level.INFO, "네트워크 수신 시작");
		byte[] packetBuffer = new byte[PacketUtil.HEADER_SIZE + PacketUtil.MAX_SIZE_KEY + PacketUtil.MAX_SIZE_DATA];
		DatagramPacket dgramPacket;
		
		while(this.isWork)
		{
			dgramPacket = new DatagramPacket(packetBuffer, packetBuffer.length);

			try
			{
				this.socket.receive(dgramPacket);
				byte[] copyBuf = Arrays.copyOf(packetBuffer, dgramPacket.getLength());
				this.receiveCallback.accept(dgramPacket.getAddress(), copyBuf);
				logger.log(Level.INFO, dgramPacket.getAddress().toString());
			}
			catch (IOException e)
			{
				
				logger.log(Level.SEVERE, "수신 실패", e);
			}
		}
		logger.log(Level.WARNING, "브로드캐스트 소켓 전송기 중지");*/
	}

	public void stop()
	{
		if(!this.isWork) return;
		this.isWork = false;
		
		if(this.socket != null && !this.socket.isClosed())
		{
			this.socket.close();
		}
		
	}
}
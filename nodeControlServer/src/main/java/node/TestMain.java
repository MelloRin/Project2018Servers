package node;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import com.savarese.rocksaw.net.RawSocket;

import node.fileIO.FileHandler;
import node.network.NetworkUtil;


public class TestMain
{
	
	
	public static void main(String[] args) throws Exception
	{
		NodeControlCore.init();
		
		System.out.println(System.getProperty("java.library.path"));
		
		DatagramSocket dgramSocket = new DatagramSocket();
		byte[] buffer= new byte[10000];
		while(true)
		{
			DatagramPacket dgramPacket = new DatagramPacket(buffer, buffer.length);
			dgramSocket.receive(dgramPacket);
			System.out.println(dgramPacket.getLength());
		}
		/*
		RawSocket rawSocket = new RawSocket();
		try
		{
			rawSocket.open(RawSocket.PF_INET, RawSocket.getProtocolByName("UDP"));
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] buffer = new byte[100000];
		while(true)
		{
			System.out.println("정상적으로 수신중입니다...");
			int readLen = 0;
			try
			{
				readLen = rawSocket.read(buffer);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(NetworkUtil.bytesToHex(buffer, readLen));
		}*/
		
		
	}
	

}

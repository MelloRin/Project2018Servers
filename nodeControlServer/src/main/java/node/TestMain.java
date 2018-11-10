package node;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import com.savarese.rocksaw.net.RawSocket;

import node.fileIO.FileHandler;


public class TestMain
{
	static
	{
		
		File rawSocketLib = FileHandler.getExtResourceFile("rawsocket");
		StringBuffer libPathBuffer = new StringBuffer();
		libPathBuffer.append(rawSocketLib.toString());
		libPathBuffer.append(":");
		libPathBuffer.append(System.getProperty("java.library.path"));
		
		System.setProperty("java.library.path", libPathBuffer.toString());
		Field sysPathsField = null;
		try
		{
			sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
			sysPathsField.setAccessible(true);
			sysPathsField.set(null, null);
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.loadLibrary("rocksaw");


	}
	public static void main(String[] args)
	{
		NodeControlCore.init();
		
		System.out.println(System.getProperty("java.library.path"));
		
		
		
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
			System.out.println(new String(buffer));
			System.out.println(bytesToHex(buffer, readLen));
		}
		
		
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

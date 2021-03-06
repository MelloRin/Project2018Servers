package node.network.protocol.encpacket;

import java.nio.ByteBuffer;
import java.security.Key;
import java.util.Arrays;

import node.security.RSAEncrypt;
import node.security.RSAKeyManager;
import node.security.RSAKeyUtill;

public class EncPacketBuilder 
{
	public static EncPacket buildEncPacket(byte[] rawPacket, Key publicKey)
	{
		if(rawPacket.length + EncPacketUtil.MAGIC_NO_PART.length <= EncPacketUtil.PAYLOAD_SIZE)	//패킷 분할이 필요없을경우.
		{
			try 
			{
				ByteBuffer buffer = ByteBuffer.allocate(EncPacketUtil.MAGIC_NO_PART.length + rawPacket.length);
				buffer.put(EncPacketUtil.MAGIC_NO_PART);
				buffer.put(rawPacket);
				byte[][] arr = new byte[][] {RSAEncrypt.incode(EncPacketUtil.convertByteBufferToByteArr(buffer), publicKey)};
				
				return new EncPacket(arr, 1);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		else									//패킷 분할이 필요할경우.
		{
			int partCount = rawPacket.length / EncPacketUtil.PAYLOAD_SIZE;
			if(rawPacket.length % EncPacketUtil.PAYLOAD_SIZE != 0)
				++partCount;
			
			byte[][] arr = new byte[partCount][];
			try 
			{
				ByteBuffer buffer = ByteBuffer.allocate(EncPacketUtil.PACKET_SIZE);
				
				for(int i = 0; i < partCount; ++i)
				{
					int copyStartIdx = i * EncPacketUtil.PAYLOAD_SIZE;
					int copyEndIdx = (i + 1) * EncPacketUtil.PAYLOAD_SIZE;

					buffer.put(EncPacketUtil.MAGIC_NO_PART);
					buffer.put(Arrays.copyOfRange(rawPacket, copyStartIdx, copyEndIdx));
					
					arr[i] = RSAEncrypt.incode(EncPacketUtil.convertByteBufferToByteArr(buffer), publicKey);
					buffer.clear();
				}
				return new EncPacket(arr, partCount);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return null;
			}
		}
	}
}
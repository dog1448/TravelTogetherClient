package mini.client.network.Services;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import mini.client.network.staticValues.ServerConnectionStaticValues;

public class NetService implements INetService
{
	private SocketChannel socketChannel;
	private SocketChannel socketFileChannel;
	private boolean isConnected = false;
	
	
	@Override
	public boolean getConnectState() {
//		System.out.println(isConnected);	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		return isConnected;
	}
	@Override
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	public NetService() 
	{
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() 
			{
				try {
					socketChannel = SocketChannel.open();
					socketChannel.configureBlocking(true);
					socketChannel.connect(new InetSocketAddress(ServerConnectionStaticValues.SERVER_INET_ADDRESS, ServerConnectionStaticValues.SERVER_PORT));
					
					System.out.println("[netservice] 소켓 연결");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					System.out.println("[netservice] 서버로 연결 성공");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					
					isConnected = true;
					
					
				} catch (IOException e) {
					System.out.println("NetService: socketChannel 생성 도중 에러-------------------------------");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					isConnected = false;
					if(socketChannel.isOpen())
						disconnectNetService();
					return;
				}
			}
		});
		thread.start();
	}
	
	@Override
	public void disconnectNetService()
	{
		try {
			if(socketChannel!=null && socketChannel.isOpen())
			{
				socketChannel.close();
				System.out.println("[NetService] 소켓 끊기");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				isConnected = false;
			}
		} catch (IOException e) {
			System.out.println("[NetService] 소켓 끊다가 에러----------------------------------------");	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			isConnected = false;
		}
	}
	
	@Override
	public void sendStringToServer(String protocol)
	{
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() 
			{
				try {
					Charset charset = Charset.forName("UTF-8");
					ByteBuffer byteBuffer = charset.encode(protocol);
					
					socketChannel.write(byteBuffer);
				} catch (IOException e) {
					System.out.println("[NetService] 서버 통신 불가----------------------------------------");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					disconnectNetService();
				}
			}
		});
		thread.start();
	}
	
	
	@Override
	public void sendFileToServer(File file) 
	{
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String fileProtocol = "9999:"+file.getName();
					byte[] protocolByte = fileProtocol.getBytes("UTF-8");
					ByteBuffer protocolBuffer = ByteBuffer.wrap(protocolByte);
					socketChannel.write(protocolBuffer);
					
					
					socketFileChannel = SocketChannel.open();
					socketFileChannel.configureBlocking(true);
					socketFileChannel.connect(new InetSocketAddress(ServerConnectionStaticValues.SERVER_INET_ADDRESS, ServerConnectionStaticValues.FILE_SERVER_PORT));
					System.out.println("[netservice] 파일서버 소켓 연결성공");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					
					
					String fileName=file.getName();
					System.out.println(fileName);
					
					ByteBuffer byteBuffer1 = null;
					Charset charset = Charset.forName("UTF-8");
					byteBuffer1 = charset.encode(fileName);
					socketFileChannel.write(byteBuffer1);
					
					
					FileChannel fileChannel = FileChannel.open(file.toPath());
					ByteBuffer byteBuffer = ByteBuffer.allocate(10000000);
					
					int bytesRead = fileChannel.read(byteBuffer);
					
					while(bytesRead != -1)
					{
						byteBuffer.flip();
						socketFileChannel.write(byteBuffer);
						byteBuffer.compact();
						bytesRead = fileChannel.read(byteBuffer);
					}
					
					fileChannel.close();
					socketFileChannel.close();
					
					
				} catch (UnsupportedEncodingException e) {
					System.out.println("서버로 파일 전송하다가 오류: "+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				} catch (IOException e) {
					System.out.println("서버로 파일 전송하다가 오류: "+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				}
			}
		});
		thread.start();
	}


	

}

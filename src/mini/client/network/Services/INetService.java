package mini.client.network.Services;

import java.io.File;
import java.nio.channels.SocketChannel;

public interface INetService 
{
	public boolean getConnectState();
	public SocketChannel getSocketChannel();
	public void disconnectNetService();
	public void sendStringToServer(String protocol);
	public void sendFileToServer(File file);
}

package guang.crawler.connector;

import guang.crawler.jsonServer.DataPacket;
import guang.crawler.util.StreamHelper;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @author yang
 */
public class JSONServerConnector
{
	
	private Socket	socket;
	
	public JSONServerConnector(String host, int port)
	        throws UnknownHostException, IOException
	{
		try
		{
			this.socket = new Socket(host, port);
		} catch (ConnectException e)
		{
			throw new IOException("can not connect to json server", e);
		}
		
	}
	
	public DataPacket read() throws IOException
	{
		return StreamHelper.readObject(this.socket.getInputStream(),
		        DataPacket.class);
	}
	
	public void send(DataPacket packet) throws IOException
	{
		StreamHelper.writeObject(this.socket.getOutputStream(), packet);
	}
	
	public void shutdown() throws IOException
	{
		if (this.socket != null)
		{
			this.socket.close();
		}
	}
	
}

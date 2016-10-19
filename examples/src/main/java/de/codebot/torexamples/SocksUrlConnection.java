package de.codebot.torexamples;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import de.codebot.tor.TorController;

/**
 * @see http://stackoverflow.com/q/5697371/4520565
 */
public class SocksUrlConnection {
	private Proxy proxy;

	public SocksUrlConnection(int socksPort){
		SocketAddress proxyAddr = new InetSocketAddress("127.0.0.1", socksPort);
        proxy = new Proxy(Proxy.Type.SOCKS, proxyAddr);
	}
	
	public String get(String url){
		URLConnection connection;
		try {
			connection = new URL(url).openConnection(proxy);
			InputStream response = connection.getInputStream();
			Scanner scanner = new Scanner(response);
			String responseBody = scanner.useDelimiter("\\A").next();
			scanner.close();
			return responseBody;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args){
		if(args.length != 1){
			System.err.println("Please set path to tor executable as first command line argument.");
			System.exit(1);
		}
		TorController tor = new TorController(args[0]);
		if(!tor.startUp()){
			System.err.println("Unable to start and connect to tor.");
			System.exit(1);
		}
		SocksUrlConnection client = new SocksUrlConnection(tor.getSocksPort());
		System.out.println("1. identity: " + client.get("http://api.ipify.org/?format=text"));
		
		if(!tor.changeIdentity()){
			System.err.println("Unable to change identity.");
			tor.shutDown();
			System.exit(1);
		}
		System.out.println("2. identity: " + client.get("https://api.ipify.org/?format=text"));
		
		if(!tor.shutDown()){
			System.err.println("Unable to shut down tor server.");
			System.exit(1);
		}
	}
}

package de.codebot.torexamples;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.codebot.tor.TorController;

public class SocksJSoupClient {
	private Proxy p;
	public SocksJSoupClient(int socksPort){
		SocketAddress proxyAddr = new InetSocketAddress("127.0.0.1", socksPort);
        p = new Proxy(Proxy.Type.SOCKS, proxyAddr);
	}
	
	/**
	 * @see https://jsoup.org/apidocs/org/jsoup/Connection.html#proxy-java.net.Proxy-
	 * @param url
	 * @return
	 */
	public String get(String url){
		Connection conn = Jsoup.connect(url).proxy(p);
		try {
			Document d = conn.get();
			return d.text();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
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
		SocksJSoupClient client = new SocksJSoupClient(tor.getSocksPort());
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

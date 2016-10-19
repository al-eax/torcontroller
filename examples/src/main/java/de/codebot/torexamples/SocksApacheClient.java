package de.codebot.torexamples;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import de.codebot.tor.TorController;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import javax.net.ssl.SSLContext;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class SocksApacheClient {

	/**
	 * @see http://stackoverflow.com/a/22960881/4520565
	 */
	class HTTPSConnectionFactory extends SSLConnectionSocketFactory {
		public HTTPSConnectionFactory(final SSLContext sslContext) {
			super(sslContext);
		}

		@Override
		public Socket createSocket(final HttpContext context) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}
	}

	/**
	 * @see http://stackoverflow.com/a/22960881/4520565
	 */
	class HTTPConnectionFactory extends PlainConnectionSocketFactory {
		@Override
		public Socket createSocket(final HttpContext context) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
			return new Socket(proxy);
		}
	}

	private HttpClient client;
	private HttpClientContext context;

	public SocksApacheClient(int port) {
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", new HTTPConnectionFactory())
				.register("https", new HTTPSConnectionFactory(SSLContexts.createSystemDefault())).build();

		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(reg);
		InetSocketAddress socksaddr = new InetSocketAddress("127.0.0.1", port);
		context = HttpClientContext.create();
		context.setAttribute("socks.address", socksaddr);
		client = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
	}

	public String get(String url) {
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(httpGet, context);
			HttpEntity ent = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(ent.getContent()));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Please set path to tor executable as first command line argument.");
			System.exit(1);
		}
		TorController tor = new TorController(args[0]);
		if (!tor.startUp()) {
			System.err.println("Unable to start and connect to tor.");
			System.exit(1);
		}
		SocksApacheClient client = new SocksApacheClient(tor.getSocksPort());
		System.out.println("1. identity: " + client.get("http://api.ipify.org/?format=text"));

		if (!tor.changeIdentity()) {
			System.err.println("Unable to change identity.");
			tor.shutDown();
			System.exit(1);
		}
		System.out.println("2. identity: " + client.get("https://api.ipify.org/?format=text"));

		if (!tor.shutDown()) {
			System.err.println("Unable to shut down tor server.");
			System.exit(1);
		}
	}

}
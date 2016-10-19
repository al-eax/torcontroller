package de.codebot.tor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TorClient {
	private int controlPort;
	private Socket controlSocket;
	private char[] password;
	private BufferedReader socketReader;
	private DataOutputStream socketWriter;
	protected List<String> log;
	private static final String LOCALHOST = "127.0.0.1";

	/**
	 * Create a new TorClient object
	 * 
	 * @param controlPort
	 *            Port to connect to tor control server
	 */
	public TorClient(int controlPort) {
		this(controlPort, null);
	}

	/**
	 * Create a new TorClient object
	 * 
	 * @param controlPort
	 *            controlPort Port to connect to tor control server
	 * @param password
	 *            Password for authentication
	 */
	public TorClient(int controlPort, char[] password) {
		this.controlPort = controlPort;
		this.password = password;
		log = new ArrayList<String>();
	}

	/**
	 * Creates a tcp connection to the local tor control server.
	 * 
	 * @return true if connection was successful.
	 */
	public boolean connect() {
		if (controlSocket == null)
			controlSocket = new Socket();
		try {
			controlSocket.connect(new InetSocketAddress(LOCALHOST, this.controlPort));
			socketWriter = new DataOutputStream(controlSocket.getOutputStream());
			socketReader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
			return controlSocket.isConnected();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Closes the tcp connection between this object and the tor control server.
	 * 
	 * @return true if the connection was closed successful.
	 */
	public boolean disconnect() {
		if (controlSocket == null)
			return false;
		try {
			controlSocket.close();
			return controlSocket.isClosed();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns the control port used to connect to the tor control server
	 * 
	 * @return
	 */
	public int getControlPort() {
		return this.controlPort;
	}

	/**
	 * Returns the used socks port which is used to tunnel connection through
	 * tor network.
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return this.controlSocket.isConnected();
	}

	/**
	 * Changes the identity from tor client
	 * 
	 * @return if the identity was successful changed
	 */
	public boolean changeIdentity() {
		String response = send("SIGNAL NEWNYM");
		return response != null && response.equals("250 OK");
	}

	/**
	 * Starts the authentication process
	 * 
	 * @return
	 */
	public boolean authenticate() {
		String response;
		if (this.password == null)
			response = send("AUTHENTICATE");
		else
			response = send("AUTHENTICATE \"" + String.valueOf(password) + "\"");
		return response != null && response.equals("250 OK");
	}

	/**
	 * Sends the QUIT command for server-side disconnection
	 * 
	 * @return True if the server closed the connection successful.
	 */
	public boolean quit() {
		String response = send("QUIT");
		return response != null && response.equals("250 closing connection");
	}

	/**
	 * Sends a line to tor control server and receives the answer.
	 * 
	 * @param msg
	 * @return
	 */
	private String send(String msg) {
		if (controlSocket == null || !controlSocket.isConnected())
			return null;
		try {
			socketWriter.write((msg + "\r\n").getBytes());
			socketWriter.flush();
			String line;
			line = socketReader.readLine();
			if (line != null)
				log.add(line);
			return line;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}

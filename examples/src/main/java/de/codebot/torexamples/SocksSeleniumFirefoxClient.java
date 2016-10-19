package de.codebot.torexamples;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import de.codebot.tor.TorController;

public class SocksSeleniumFirefoxClient {
	FirefoxDriver driver;

	/**
	 * @see http://stackoverflow.com/a/32451039/4520565
	 * @param socksPort
	 */
	public SocksSeleniumFirefoxClient(int socksPort) {
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("network.proxy.type", 1);
		profile.setPreference("network.proxy.socks", "127.0.0.1");
		profile.setPreference("network.proxy.socks_port", socksPort);
		this.driver = new FirefoxDriver(profile);
	}

	public String get(String url) {
		driver.get(url);
		return driver.getPageSource();
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
		SocksSeleniumFirefoxClient client = new SocksSeleniumFirefoxClient(tor.getSocksPort());
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

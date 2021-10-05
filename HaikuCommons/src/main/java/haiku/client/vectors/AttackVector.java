package haiku.client.vectors;

import java.util.HashMap;
import java.util.List;

public abstract class AttackVector {
	public abstract void start(String host, int port, HashMap<String, String> options);

	public abstract void stop();

	public abstract HashMap<String, String> getStatistics();

	public abstract void displayStatistics(List<HashMap<String, String>> hashMaps);
}

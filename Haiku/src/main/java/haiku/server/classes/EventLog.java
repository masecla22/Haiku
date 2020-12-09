package haiku.server.classes;

import java.util.ArrayList;
import java.util.List;

public class EventLog {
	private List<String> eventLog = new ArrayList<>();

	private static EventLog log;

	public EventLog() {
		log = this;
	}

	public List<String> getEventLog() {
		return eventLog;
	}

	public void addLog(String s) {
		this.eventLog.add(s);
	}

	public void printLogs() {
		eventLog.forEach(c -> System.out.println("[LOG] " + c));
	}

	public static EventLog getInstance() {
		return log;
	}

}

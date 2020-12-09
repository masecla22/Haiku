package haiku.server.pool;

import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import haiku.commons.packets.PacketDictionaryBuilder;
import haiku.server.classes.EventLog;
import metwork.network.classes.DuplexConnection;

public class ClientConnectionManager {
	private HashMap<UUID, DuplexConnection> activeConnections = new HashMap<>();
	private HashMap<UUID, Integer> lastPings = new HashMap<>();

	private ServerSocket socket;
	private AtomicBoolean running = new AtomicBoolean(false);
	private Thread thr = null;

	private int port = 9999;

	public void startListener() {

		startCleaner();
		this.thr = new Thread(() -> {
			this.running.set(true);
			try {
				this.socket = new ServerSocket(port);
				while (running.get()) {
					Socket sock = this.socket.accept();
					EventLog.getInstance().addLog("Opened a connection for " + sock.getInetAddress().toString());

					if (sock != null) {
						// Create metwork connection
						DuplexConnection conn = new DuplexConnection(sock, PacketDictionaryBuilder.getDictionary(),
								false);

						// Add connection to connection pool
						UUID toAlloc = UUID.randomUUID();
						this.activeConnections.put(toAlloc, conn);

						// Register listeners to connection
						conn.registerListener(new PingImplier(this, toAlloc));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		this.thr.start();
	}

	public void startCleaner() {
		final TimerTask x = new TimerTask() {
			@Override
			public void run() {
				Set<UUID> toRemove = new HashSet<>();

				for (UUID u : lastPings.keySet()) {
					int cr = lastPings.get(u);
					if (Instant.now().getEpochSecond() - cr > 20)
						toRemove.add(u);
				}

				toRemove.forEach(c -> {
					lastPings.remove(c);
					String addr = activeConnections.get(c).getTarget().getInetAddress().toString();
					activeConnections.get(c).fullyClose();
					activeConnections.remove(c);
					EventLog.getInstance().addLog("Disconnected " + addr);
				});
			}
		};
		new Timer().schedule(x, 0, 5000);
	}

	public HashMap<UUID, Integer> getLastPings() {
		return lastPings;
	}

	public Thread getThread() {
		return thr;
	}

	public HashMap<UUID, DuplexConnection> getActiveConnections() {
		return activeConnections;
	}

	public int getPort() {
		return port;
	}

	public void refreshConnection(UUID u) {
		this.lastPings.put(u, (int) Instant.now().getEpochSecond());
	}

	public void removeConnection(UUID u) {
		this.activeConnections.get(u).fullyClose();
		this.activeConnections.remove(u);
		this.lastPings.remove(u);
	}

	public void stopListener() {
		this.running.set(false); // Stop accepting new connections
		this.activeConnections.values().forEach(DuplexConnection::fullyClose);
		this.activeConnections.clear();
		try {
			this.thr.join(1000);
		} catch (InterruptedException e) {
		}
	}

	public boolean isRunning() {
		return this.running.get();
	}
}

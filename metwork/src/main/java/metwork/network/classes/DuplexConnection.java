package metwork.network.classes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import metwork.protocol.implementation.PacketDictionary;

public class DuplexConnection {

	private Socket target;
	private RegisteredPacketManager dictionary;
	private Queue<SerializablePacket> toSend = new ConcurrentLinkedQueue<>();
	private Thread sendingThread;
	private Thread receivingThread;

	private String hostname;
	private int port;

	private boolean attempt;
	private boolean closed;

	private List<PacketListener> listener = new ArrayList<>();

	public DuplexConnection(Socket target, RegisteredPacketManager dictionary, boolean attempt) {
		super();
		this.hostname = target.getInetAddress().toString().replace("/", "");
		this.port = target.getPort();
		this.attempt = attempt;

		this.target = target;
		this.dictionary = dictionary;
		this.sendingThread = new Thread(() -> {
			while (!target.isClosed()) {
				if (toSend.size() == 0) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					SerializablePacket pck = toSend.poll();
					try {
						actuallySendPacket(pck);
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		sendingThread.start();

		this.receivingThread = new Thread(() -> {
			while (!target.isClosed()) {
				try {
					SerializablePacket pck = this.getNextPacketInStream();
					if (pck == null) {
					} else {
						List<PacketListener> cr = new ArrayList<>();
						cr.addAll(listener);
						cr.forEach(c -> c.handlePacket(pck));
						cr.clear();
					}
				} catch (IOException e) {
					// Socket closed
					this.fullyClose();
				}
			}
		});

		receivingThread.start();
	}

	public void registerListener(PacketListener list) {
		list.initialize();
		listener.add(list);
	}

	public static DuplexConnection create(String hostname, int port, boolean attempt)
			throws UnknownHostException, IOException {
		Socket sc = new Socket(hostname, port);
		DuplexConnection dpl = new DuplexConnection(sc, PacketDictionary.createPacketManager(), attempt);
		dpl.hostname = hostname;
		dpl.port = port;
		return dpl;
	}

	public static DuplexConnection create(String hostname, int port, RegisteredPacketManager manager, boolean attempt)
			throws UnknownHostException, IOException {
		Socket sc = new Socket(hostname, port);
		DuplexConnection dpl = new DuplexConnection(sc, manager, attempt);
		dpl.hostname = hostname;
		dpl.port = port;
		return dpl;
	}

	public boolean isClosed() {
		return target.isClosed();
	}

	public void fullyClose() {
		this.closed = true;
		try {
			this.target.close();
		} catch (IOException e) {
		}
	}

	public Socket getTarget() {
		return target;
	}

	public RegisteredPacketManager getDictionary() {
		return dictionary;
	}

	public Queue<SerializablePacket> getToSend() {
		return toSend;
	}

	public void actuallySendPacket(SerializablePacket packet) throws IOException {
		if (this.closed)
			return;
		if (isClosed() && attempt) {
			this.target = new Socket(hostname, port);
			actuallySendPacket(packet);
			return;
		}
		try {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			packet.writeToStream(result);
			byte[] results = result.toByteArray();
			StreamUtil.writeInt(target.getOutputStream(), this.dictionary.getIDFor(packet));
			StreamUtil.writeInt(target.getOutputStream(), result.size());
			target.getOutputStream().write(results);
		} catch (Exception e) {
			this.closed = true;
		}
	}

	public void sendPacketWithFail(SerializablePacket packet) throws SocketException {
		if (closed)
			throw new SocketException("Connection closed");
		this.toSend.add(packet);
	}

	public void sendPacket(SerializablePacket packet) {
		this.toSend.add(packet);
	}

	protected SerializablePacket getNextPacketInStream() throws IOException {
		if (this.closed)
			return null;
		if (isClosed() && attempt) {
			this.target = new Socket(hostname, port);
			return getNextPacketInStream();
		}
		while (target.getInputStream().available() == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			continue;
		}
		int id = StreamUtil.readInt(target.getInputStream());
		int size = StreamUtil.readInt(target.getInputStream());
		if (size < 0)
			return null;
		SerializablePacket packet = this.dictionary.createEmptyPacket(id);
		if (packet == null)
			return null;

		byte[] data = new byte[size];

		ByteArrayOutputStream strm = new ByteArrayOutputStream();
		int read = 0;
		int bufferSize = 1024 * 8;
		while (strm.size() < size) {
			while (target.getInputStream().available() < size - read
					&& target.getInputStream().available() < bufferSize) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			byte[] buf = new byte[bufferSize];
			int chunk = target.getInputStream().read(buf, 0, Math.min(bufferSize, size - strm.size()));
			read += chunk;
			strm.write(buf);
		}

		data = strm.toByteArray();

		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		packet.readFromStream(stream);
		return packet;
	}

}

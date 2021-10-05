package haiku.commons.packets.instances;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

import metwork.network.classes.SerializablePacket;
import metwork.network.classes.StreamUtil;

public class StartAttackPacket extends SerializablePacket {
	private int vectorId;
	private UUID attackId;
	private String hostname;
	private int port;
	private HashMap<String, String> options = new HashMap<>();

	@Override
	public void readFromStream(InputStream stream) throws IOException {
		vectorId = StreamUtil.readInt(stream);
		attackId = UUID.fromString(StreamUtil.readString(stream));
		hostname = StreamUtil.readString(stream);
		port = StreamUtil.readInt(stream);

		int cont = StreamUtil.readInt(stream);
		for (int i = 0; i < cont; i++) {
			String key = StreamUtil.readString(stream);
			String val = StreamUtil.readString(stream);
			this.options.put(key, val);
		}
	}

	@Override
	public void writeToStream(OutputStream stream) throws IOException {
		StreamUtil.writeInt(stream, vectorId);
		StreamUtil.writeString(stream, attackId + "");
		StreamUtil.writeString(stream, hostname);
		StreamUtil.writeInt(stream, port);

		StreamUtil.writeInt(stream, options.size());
		options.forEach((c, v) -> {
			try {
				StreamUtil.writeString(stream, c);
				StreamUtil.writeString(stream, v);
			} catch (IOException e) {
			}
		});
	}

	public UUID getAttackId() {
		return attackId;
	}

	public String getHostname() {
		return hostname;
	}

	public HashMap<String, String> getOptions() {
		return options;
	}

	public int getPort() {
		return port;
	}

	public int getVectorId() {
		return vectorId;
	}

	public StartAttackPacket(int vectorId, UUID attackId, String hostname, int port) {
		super();
		this.vectorId = vectorId;
		this.attackId = attackId;
		this.hostname = hostname;
		this.port = port;
		this.options = new HashMap<>();
	}

	public StartAttackPacket() {
		super();
	}

}

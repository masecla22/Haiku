package haiku.commons.packets.instances;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

import metwork.network.classes.SerializablePacket;
import metwork.network.classes.StreamUtil;

public class StatisticsUpdatePacket extends SerializablePacket {
	private UUID attackId;
	private HashMap<String, String> statistics;

	public StatisticsUpdatePacket() {
		super();
	}

	public StatisticsUpdatePacket(UUID attackId, HashMap<String, String> statistics) {
		super();
		this.attackId = attackId;
		this.statistics = statistics;
	}

	public UUID getAttackId() {
		return attackId;
	}

	public HashMap<String, String> getStatistics() {
		return statistics;
	}

	@Override
	public void readFromStream(InputStream stream) throws IOException {
		this.attackId = UUID.fromString(StreamUtil.readString(stream));
		int optCount = StreamUtil.readInt(stream);
		for (int i = 0; i < optCount; i++) {
			String opt = StreamUtil.readString(stream);
			String val = StreamUtil.readString(stream);
			this.statistics.put(opt, val);
		}
	}

	@Override
	public void writeToStream(OutputStream stream) throws IOException {
		StreamUtil.writeString(stream, attackId + "");
		StreamUtil.writeInt(stream, statistics.size());
		statistics.forEach((c, v) -> {
			try {
				StreamUtil.writeString(stream, c);
				StreamUtil.writeString(stream, v);
			} catch (IOException e) {
			}
		});
	}

}

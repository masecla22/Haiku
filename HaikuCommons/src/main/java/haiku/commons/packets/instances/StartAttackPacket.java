package haiku.commons.packets.instances;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import metwork.network.classes.SerializablePacket;
import metwork.network.classes.StreamUtil;

public class StartAttackPacket extends SerializablePacket {
	private int vectorId;
	private UUID attackId;

	@Override
	public void readFromStream(InputStream stream) throws IOException {
		vectorId = StreamUtil.readInt(stream);
		attackId = UUID.fromString(StreamUtil.readString(stream));
	}

	@Override
	public void writeToStream(OutputStream stream) throws IOException {
		StreamUtil.writeInt(stream, vectorId);
		StreamUtil.writeString(stream, attackId + "");
	}

	public StartAttackPacket(int vectorId, UUID attackId) {
		super();
		this.vectorId = vectorId;
		this.attackId = attackId;
	}

	public StartAttackPacket() {
		super();
	}

}

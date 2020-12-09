package haiku.commons.packets.instances;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import metwork.network.classes.SerializablePacket;
import metwork.network.classes.StreamUtil;

public class StopAttackPacket extends SerializablePacket {
	private UUID attackId;

	@Override
	public void readFromStream(InputStream stream) throws IOException {
		attackId = UUID.fromString(StreamUtil.readString(stream));
	}

	@Override
	public void writeToStream(OutputStream stream) throws IOException {
		StreamUtil.writeString(stream, attackId + "");
	}

	public StopAttackPacket(UUID attackId) {
		super();
		this.attackId = attackId;
	}

	public StopAttackPacket() {
		super();
	}
}

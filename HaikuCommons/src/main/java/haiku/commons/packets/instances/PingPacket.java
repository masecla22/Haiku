package haiku.commons.packets.instances;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import metwork.network.classes.SerializablePacket;
import metwork.network.classes.StreamUtil;

public class PingPacket extends SerializablePacket {

	private int magicValue = 0;

	public PingPacket() {
	}

	@Override
	public void readFromStream(InputStream stream) throws IOException {
		this.magicValue = StreamUtil.readInt(stream);
	}

	@Override
	public void writeToStream(OutputStream stream) throws IOException {
		StreamUtil.writeInt(stream, 0xCacaBad);
	}

	public boolean verifyValidity() {
		return magicValue == 0xCacaBad;
	}
}

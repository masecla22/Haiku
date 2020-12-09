package metwork.network.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SerializablePacket {
	public abstract void readFromStream(InputStream stream) throws IOException;

	public abstract void writeToStream(OutputStream stream) throws IOException;

	public SerializablePacket() {

	}
}

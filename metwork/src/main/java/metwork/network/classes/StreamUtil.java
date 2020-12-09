package metwork.network.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StreamUtil {
	public static int readVarInt(InputStream str) throws IOException {
		int numRead = 0;
		int result = 0;
		byte read = 0;
		do {
			read = (byte) str.read();
			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));
			numRead++;
			if (numRead > 5) {
				throw new RuntimeException("VarInt is too big");
			}
		} while ((read & 0b10000000) != 0);

		return result;
	}

	public static long readVarLong(InputStream str) throws IOException {
		int numRead = 0;
		long result = 0;
		byte read;
		do {
			read = (byte) str.read();
			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));
			numRead++;
			if (numRead > 10) {
				throw new RuntimeException("VarLong is too big");
			}
		} while ((read & 0b10000000) != 0);

		return result;
	}

	public static void writeVarInt(OutputStream str, int value) throws IOException {
		do {
			byte temp = (byte) (value & 0b01111111);
			value >>>= 7;
			if (value != 0) {
				temp |= 0b10000000;
			}
			str.write(temp);
		} while (value != 0);
	}

	public static void writeVarLong(OutputStream str, long value) throws IOException {
		do {
			byte temp = (byte) (value & 0b01111111);
			value >>>= 7;
			if (value != 0) {
				temp |= 0b10000000;
			}
			str.write(temp);
		} while (value != 0);
	}

	public static void writeInt(OutputStream str, int i) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(i);
		str.write(bb.array());
	}

	public static int readInt(InputStream str) throws IOException {
		byte[] bfs = new byte[4];
		str.read(bfs);
		ByteBuffer bfr = ByteBuffer.wrap(bfs);
		bfr.order(ByteOrder.BIG_ENDIAN);
		return bfr.getInt();
	}

	public static void writeBool(OutputStream str, boolean i) throws IOException {
		if (i)
			str.write(0x01);
		else
			str.write(0x00);
	}

	public static boolean readBool(InputStream str) throws IOException {
		byte b = (byte) str.read();
		if (b == 0x00)
			return false;
		return true;
	}

	public static void writeShort(OutputStream str, short i) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.putShort(i);
		str.write(bb.array());
	}

	public static short readShort(InputStream str) throws IOException {
		byte[] bfs = new byte[2];
		str.read(bfs);
		ByteBuffer bfr = ByteBuffer.wrap(bfs);
		bfr.order(ByteOrder.BIG_ENDIAN);
		return bfr.getShort();
	}

	public static void writeString(OutputStream str, String i) throws IOException {
		writeInt(str, i.length());
		str.write(i.getBytes());
	}

	public static String readString(InputStream str) throws IOException {
		int len = readInt(str);
		byte[] b = new byte[len];
		str.read(b);
		return new String(b);
	}

}

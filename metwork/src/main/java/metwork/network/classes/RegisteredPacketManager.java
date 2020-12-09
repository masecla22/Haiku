package metwork.network.classes;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisteredPacketManager {
	private HashMap<Integer, Class<? extends SerializablePacket>> assignedIDs = new HashMap<>();

	public RegisteredPacketManager() {
		super();
	}

	public void registerPacketID(Class<? extends SerializablePacket> packet, int id) {
		assignedIDs.put(id, packet);
	}

	public SerializablePacket createEmptyPacket(int id) {
		try {
			return this.assignedIDs.get(id).getConstructor().newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	public int getIDFor(SerializablePacket packet) {
		AtomicInteger result = new AtomicInteger(-1);
		this.assignedIDs.forEach((id, pck) -> {
			if (pck.equals(packet.getClass()))
				result.set(id);
		});
		return result.get();
	}
}

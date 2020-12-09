package metwork.protocol.implementation;

import metwork.network.classes.RegisteredPacketManager;

public class PacketDictionary {
	public static RegisteredPacketManager createPacketManager() {
		RegisteredPacketManager result = new RegisteredPacketManager();
		
		return result;
	}
}

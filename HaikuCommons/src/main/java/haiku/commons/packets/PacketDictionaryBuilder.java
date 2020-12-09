package haiku.commons.packets;

import haiku.commons.packets.instances.PingPacket;
import metwork.network.classes.RegisteredPacketManager;
import metwork.protocol.implementation.PacketDictionary;

public class PacketDictionaryBuilder {
	public static RegisteredPacketManager getDictionary() {
		RegisteredPacketManager res = PacketDictionary.createPacketManager();
		res.registerPacketID(PingPacket.class, 1);
		
		return res;
	}
}

package haiku.commons.packets;

import haiku.commons.packets.instances.PingPacket;
import haiku.commons.packets.instances.StartAttackPacket;
import haiku.commons.packets.instances.StatisticsUpdatePacket;
import haiku.commons.packets.instances.StopAttackPacket;
import metwork.network.classes.RegisteredPacketManager;
import metwork.protocol.implementation.PacketDictionary;

public class PacketDictionaryBuilder {
	public static RegisteredPacketManager getDictionary() {
		RegisteredPacketManager res = PacketDictionary.createPacketManager();
		res.registerPacketID(PingPacket.class, 1);
		res.registerPacketID(StartAttackPacket.class, 2);
		res.registerPacketID(StatisticsUpdatePacket.class, 3);
		res.registerPacketID(StopAttackPacket.class, 4);

		return res;
	}
}

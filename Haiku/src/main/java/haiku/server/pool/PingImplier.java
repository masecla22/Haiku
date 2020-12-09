package haiku.server.pool;

import java.util.UUID;

import haiku.commons.packets.instances.PingPacket;
import metwork.network.classes.PacketListener;

public class PingImplier extends PacketListener {

	private ClientConnectionManager manager;
	private UUID u;

	public PingImplier(ClientConnectionManager manager, UUID u) {
		super();
		this.manager = manager;
		this.u = u;
	}

	@PacketHandler
	public void handlePings(PingPacket packet) {
		if (packet.verifyValidity()) {
			manager.refreshConnection(u);
		} else {
			manager.removeConnection(u);
		}
	}
}

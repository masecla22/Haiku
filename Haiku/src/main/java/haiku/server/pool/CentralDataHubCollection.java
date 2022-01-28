package haiku.server.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import haiku.commons.packets.instances.StatisticsUpdatePacket;
import metwork.network.classes.DuplexConnection;
import metwork.network.classes.PacketListener;

public class CentralDataHubCollection {
	private HashMap<UUID, List<HashMap<String, String>>> statistics = new HashMap<>();
	private OngoingAttacksManager manager;

	public CentralDataHubCollection(OngoingAttacksManager manager) {
		super();
		this.manager = manager;
	}

	public void registerListenerToConnection(DuplexConnection conn) {
		conn.registerListener(new PacketListener() {
			@PacketHandler
			public void onPacket(StatisticsUpdatePacket packet) {
				if (!statistics.containsKey(packet.getAttackId()))
					statistics.put(packet.getAttackId(), new ArrayList<>());
				statistics.get(packet.getAttackId()).add(packet.getStatistics());
			}
		});
	}

	public void showStats(UUID attackId) {
		List<HashMap<String, String>> stats = statistics.getOrDefault(attackId, new ArrayList<>());
		manager.getOngoingAttacks().get(attackId).displayStatistics(stats);
	}
}

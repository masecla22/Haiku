package haiku.server.pool;

import java.util.HashMap;
import java.util.UUID;

import haiku.client.vectors.AttackVector;
import haiku.client.vectors.VectorDictionary;
import haiku.commons.packets.instances.StartAttackPacket;
import haiku.commons.packets.instances.StopAttackPacket;
import haiku.server.classes.EventLog;

public class OngoingAttacksManager {

	private HashMap<UUID, AttackVector> ongoingAttacks = new HashMap<>();
	private ClientConnectionManager manager;
	private VectorDictionary dictionary;

	public OngoingAttacksManager(ClientConnectionManager manager) {
		super();
		this.manager = manager;
		this.dictionary = VectorDictionary.getInstance();
	}

	public void startNewAttack(Class<? extends AttackVector> clazz) {
		UUID attackId = UUID.randomUUID();
		int id = dictionary.getIdFor(clazz);
		AttackVector toSet = dictionary.newInstance(id);

		StartAttackPacket packet = new StartAttackPacket(id, attackId);
		// Broadcast attack start
		manager.getActiveConnections().values().forEach(c -> c.sendPacket(packet));

		String attackVector = toSet.getClass().getSimpleName();
		EventLog.getInstance().addLog("Started attack " + attackVector + " (" + attackId + ")");
		this.ongoingAttacks.put(attackId, toSet);
	}

	public void stopAttack(UUID attackId) {
		if (!ongoingAttacks.containsKey(attackId))
			return;
		StopAttackPacket stopping = new StopAttackPacket(attackId);
		String attackVector = this.ongoingAttacks.get(attackId).getClass().getSimpleName();
		manager.getActiveConnections().values().forEach(c -> c.sendPacket(stopping));
		EventLog.getInstance().addLog("Stopped attack " + attackVector + " (" + attackId + ")");
		this.ongoingAttacks.remove(attackId);
	}

}

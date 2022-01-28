package haiku.client.handlers;

import java.util.HashMap;
import java.util.UUID;

import haiku.client.vectors.AttackVector;
import haiku.client.vectors.VectorDictionary;

public class OngoingAttackManager {
	private HashMap<UUID, AttackVector> ongoingAttacks = new HashMap<>();
	private HashMap<UUID, Thread> thread = new HashMap<>();

	private VectorDictionary dictionary;

	public OngoingAttackManager(VectorDictionary dictionary) {
		super();
		this.dictionary = dictionary;
	}

	public HashMap<UUID, AttackVector> getTaskManager() {
		return ongoingAttacks;
	}

	public void startAttack(UUID u, String hostname, String port, int id, HashMap<String, String> options) {
		AttackVector vc = dictionary.newInstance(id);

		Thread t = new Thread(() -> {
			vc.start(hostname, Integer.parseInt(port), options);
		});

		this.thread.put(u, t);
		t.start();
		this.ongoingAttacks.put(u, vc);
	}

	public void stopAttack(UUID u) {

	}

}

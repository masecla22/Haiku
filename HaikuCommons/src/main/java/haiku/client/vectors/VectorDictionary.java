package haiku.client.vectors;

import java.util.HashMap;

import haiku.client.vectors.instances.ClassicDOS;

public class VectorDictionary {
	private HashMap<Integer, Class<? extends AttackVector>> vectors = new HashMap<>();

	public static VectorDictionary getInstance() {
		VectorDictionary res = new VectorDictionary();
		res.vectors.put(1, ClassicDOS.class);
		return res;
	}

	public AttackVector newInstance(int id) {
		try {
			return vectors.get(id).newInstance();
		} catch (Exception e) {
			return null;
		}
	}
}

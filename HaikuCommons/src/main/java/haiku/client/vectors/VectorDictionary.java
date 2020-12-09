package haiku.client.vectors;

import java.util.HashMap;
import java.util.Map.Entry;

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

	public int getIdFor(Class<? extends AttackVector> vector) {
		for (Entry<Integer, Class<? extends AttackVector>> crv : vectors.entrySet())
			if (crv.getValue().equals(vector))
				return crv.getKey();
		return -1;
	}

	public AttackVector newInstance(Class<? extends AttackVector> vector) {
		int id = getIdFor(vector);
		if (id != -1)
			return newInstance(id);
		return null;
	}
}

package metwork.network.classes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class PacketListener {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PacketHandler {
	}

	private HashMap<Class<? extends SerializablePacket>, List<Method>> handlers = new HashMap<>();

	protected void initialize() {
		for (Method m : this.getClass().getMethods()) {
			if (m.isAnnotationPresent(PacketHandler.class)) {
				@SuppressWarnings("unchecked")
				Class<? extends SerializablePacket> cls = (Class<? extends SerializablePacket>) m
						.getParameterTypes()[0];
				List<Method> mtds = handlers.getOrDefault(cls, new ArrayList<>());
				mtds.add(m);
				this.handlers.put(cls, mtds);
			}
		}
	}

	public void handlePacket(SerializablePacket pack) {
		for (Method m : handlers.getOrDefault(pack.getClass(), new ArrayList<>())) {
			try {
				m.setAccessible(true);
				m.invoke(this, pack);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

}

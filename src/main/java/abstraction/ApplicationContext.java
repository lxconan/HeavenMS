package abstraction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplicationContext {
    private Map<Class, Object> singleInstances = new HashMap<>();

    public void register(Class<?> clazz, Object instance) {
        Objects.requireNonNull(clazz);
        final Class<?> actualClazz = instance.getClass();
        if (!clazz.isAssignableFrom(actualClazz)) {
            final String message = String.format(
                "The classes you specified do not has extending or implementing relationship: %s, %s",
                clazz.getName(),
                actualClazz.getName());
            throw new IllegalArgumentException(message);
        }

        singleInstances.put(clazz, instance);
    }

    public <T> T getBean(Class<T> clazz) {
        if (singleInstances.containsKey(clazz)) {
            //noinspection unchecked
            return (T) singleInstances.get(clazz);
        }

        throw new IllegalArgumentException("The class you specified cannot be resolved: " + clazz.getName());
    }
}

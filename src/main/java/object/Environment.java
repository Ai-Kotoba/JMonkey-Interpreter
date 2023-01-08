package object;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Map<String, Object> store;
    final Environment outer;

    private Environment(Map<String, Object> store, Environment outer) {
        this.store = store;
        this.outer = outer;
    }

    public static Environment newEnvironment() {

        return new Environment(new HashMap<>(), null);
    }

    public static Environment newEnclosedEnvironment(Environment outer) {
        return new Environment(new HashMap<>(), outer);
    }

    public Object get(String name) {
        Object obj = store.get(name);
        if (obj == null && this.outer != null) {
            obj = this.outer.get(name);
        }
        return obj;
    }

    public void set(String name, Object value) {
        store.put(name, value);
    }
}

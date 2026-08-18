package semantic;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private final Map<String, String> symbols = new HashMap<>();

    public void declare(String name, String type) {
        symbols.put(name, type);
    }

    public boolean exists(String name) {
        return symbols.containsKey(name);
    }

    public String getType(String name) {
        return symbols.get(name);
    }
}

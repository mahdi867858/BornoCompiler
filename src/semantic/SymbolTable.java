package semantic;

import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable — Scoped symbol table।
 * প্রতিটি block এর নিজের scope আছে।
 * Child scope parent-এর variable দেখতে পারে।
 */
public class SymbolTable {

    private final Map<String, Type> symbols = new HashMap<>();
    private final SymbolTable parent; // null হলে global scope

    // Global scope (কোনো parent নেই)
    public SymbolTable() {
        this.parent = null;
    }

    // Child scope (parent আছে)
    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    /**
     * নতুন variable declare করো এই scope-এ।
     * একই scope-এ duplicate হলে error।
     */
    public void declare(String name, Type type) {
        if (symbols.containsKey(name)) {
            throw new RuntimeException(
                "Variable already declared in this scope: '" + name + "'"
            );
        }
        symbols.put(name, type);
    }

    /**
     * এই scope বা যেকোনো parent scope-এ exist করে কিনা।
     */
    public boolean exists(String name) {
        if (symbols.containsKey(name)) return true;
        if (parent != null) return parent.exists(name);
        return false;
    }

    /**
     * Type বের করো — নিজের scope-এ না থাকলে parent-এ খোঁজো।
     */
    public Type getType(String name) {
        if (symbols.containsKey(name)) return symbols.get(name);
        if (parent != null) return parent.getType(name);
        return null;
    }
}

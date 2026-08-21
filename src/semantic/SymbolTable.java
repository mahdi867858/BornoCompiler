package semantic;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SymbolTable — Scoped symbol table for Borno Compiler.
 * প্রতিটি block এর নিজস্ব scope থাকে।
 * Child scope parent-এর variable দেখতে পারে।
 */
public class SymbolTable {

    private final Map<String, Type> symbols = new LinkedHashMap<>();
    private final Map<String, Boolean> initialized = new LinkedHashMap<>();
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
            throw new RuntimeException("Duplicate variable declaration: '" + name + "'");
        }
        symbols.put(name, type);
        initialized.put(name, true);
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
     * শুধুমাত্র বর্তমান scope-এ exist করে কিনা।
     */
    public boolean existsInCurrentScope(String name) {
        return symbols.containsKey(name);
    }

    /**
     * Type বের করো — নিজের scope-এ না থাকলে parent-এ খোঁজো।
     */
    public Type getType(String name) {
        if (symbols.containsKey(name)) return symbols.get(name);
        if (parent != null) return parent.getType(name);
        return null;
    }

    /**
     * এই scope-এর সব symbols (read-only)।
     */
    public Map<String, Type> getSymbols() {
        return Collections.unmodifiableMap(symbols);
    }

    public SymbolTable getParent() {
        return parent;
    }
}

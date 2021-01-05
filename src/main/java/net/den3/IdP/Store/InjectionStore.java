package net.den3.IdP.Store;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InjectionStore {
    private static final InjectionStore SINGE = new InjectionStore();
    private final Map<String,Object> stores = new HashMap<>();

    public static InjectionStore get(){
        return SINGE;
    }

    public void inject(String name,Object store){
        stores.put(name,store);
    }

    public Optional<Object> get(String name){
        return Optional.ofNullable(stores.get(name));
    }
}

package net.den3.IdP.Util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {
    private final Map<String,Object> map = new HashMap<>();
    public static MapBuilder New(){
        return new MapBuilder();
    }

    public MapBuilder put(String key,Object obj){
        map.put(key,obj);
        return this;
    }

    public Map<String, Object> build(){
        return this.map;
    }
}

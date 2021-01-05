package net.den3.IdP.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

public class ParseJSON{

    public static Optional<Map<String,String>> convertToStringMap(String json){
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map;
        try {
            map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        if(map.size() == 0){
            return Optional.empty();
        }else{
            return Optional.of(map);
        }
    }

    public static Optional<Map<String,Object>> convertToMap(String json){
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map;
        try {
            map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        if(map.size() == 0){
            return Optional.empty();
        }else{
            return Optional.of(map);
        }
    }
}

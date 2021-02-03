package net.den3.IdP.Util;

import java.util.function.Function;

public class Errorable<A,B>{
    public B of(A value,B defaultValue, Function<A,B> f){
        try{
            return f.apply(value);
        }catch (Exception e){
            return defaultValue;
        }
    }

}

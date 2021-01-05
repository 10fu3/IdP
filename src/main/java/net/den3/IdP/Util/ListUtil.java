package net.den3.IdP.Util;

import java.util.List;

public class ListUtil<T> {
    public boolean hasElement(List<T> items,T item){
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).equals(item)){
                return true;
            }
        }
        return false;
    }
}

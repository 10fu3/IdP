package net.den3.IdP.Util;

public enum ContentsType {
    HTML("text/html; charset=utf-8"),
    JSON("application/json; charset=UTF-8");

    private final String value;
    ContentsType(String v){
        this.value = v;
    }

    public String get() {
        return value;
    }
}

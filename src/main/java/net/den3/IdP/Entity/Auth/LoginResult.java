package net.den3.IdP.Entity.Auth;

import net.den3.IdP.Entity.Account.IAccount;

public enum LoginResult {
    SUCCESS("SUCCESS"),
    ERROR_PARAMS("Invalid parameter"),
    ERROR_WRONGPASS("Wrong password"),
    ERROR_NOT_EXIST("Invalid name"),
    ERROR_FROZEN_ACCOUNT("Frozen account"),
    ERROR_EXCEPTION("Occurred exception");
    public IAccount account = null;
    private final String mes;

    LoginResult(String mes){
        this.mes = mes;
    }

    public String getMessage() {
        return mes;
    }
}

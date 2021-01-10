package net.den3.IdP.Entity.Auth;

import net.den3.IdP.Entity.Account.IAccount;

import java.util.Optional;

public enum LoginResult {
    SUCCESS("SUCCESS"),
    ERROR_PARAMS("Invalid parameter"),
    ERROR_WRONGPASS("Wrong password"),
    ERROR_NOT_EXIST("Invalid name"),
    ERROR_FROZEN_ACCOUNT("Frozen account"),
    ERROR_EXCEPTION("Occurred exception");
    public Optional<IAccount> account = Optional.empty();
    private final String mes;

    LoginResult(String mes){
        this.mes = mes;
    }

    public String getMessage() {
        return mes;
    }
}

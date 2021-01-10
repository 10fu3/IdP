package net.den3.IdP.Entity.Account;

public class AccountAttribute {
    //議論 public final or getter
    private final boolean admin;
    private final boolean frozen;

    public AccountAttribute(String uuid,boolean admin,boolean frozen){
        this.admin = admin;
        this.frozen = frozen;
    }

    public AccountAttribute(){
        this.admin = false;
        this.frozen = false;
    }

    public boolean isAdmin(){
        return admin;
    }

    public boolean isFrozen(){
        return frozen;
    }
}

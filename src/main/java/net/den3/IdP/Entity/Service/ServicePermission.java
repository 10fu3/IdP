package net.den3.IdP.Entity.Service;

import java.util.*;
import java.util.stream.Collectors;

public enum ServicePermission {
    READ_UUID("read_uuid"),
    EDIT_UUID("edit_uuid"),
    READ_MAIL("read_mail"),
    EDIT_MAIL("edit_mail"),
    READ_PROFILE("read_profile"),
    EDIT_PROFILE("edit_profile"),
    READ_LAST_LOGIN_TIME("read_last_login_time"),
    REMOVE_ACCOUNT("delete_self_account");

    public static final List<String> names = Collections.unmodifiableList(Arrays.stream(ServicePermission.values()).map(ServicePermission::getName).collect(Collectors.toList()));
    private final String name;
    ServicePermission(String perm){
        this.name = perm;
    }

    public String getName() {
        return name;
    }

    public static Optional<ServicePermission> getPermission(String name){
        for (int i = 0; i < ServicePermission.values().length; i++) {
            if(ServicePermission.values()[i].getName().equalsIgnoreCase(name)){
                return Optional.of(ServicePermission.values()[i]);
            }
        }
        return Optional.empty();
    }

    public static List<ServicePermission> convertFromScope(String scope){
        List<ServicePermission> perms = new ArrayList<>();
        if(scope.contains("openid")){
            perms.add(READ_UUID);
        }
        if(scope.contains("profile")){
            perms.add(READ_PROFILE);
        }
        if (scope.contains("mail")){
            perms.add(READ_MAIL);
        }
        return perms;
    }
}

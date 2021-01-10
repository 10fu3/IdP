package net.den3.IdP;

import net.den3.IdP.Router.URLTask;
import net.den3.IdP.Store.Account.AccountAttributeStore;
import net.den3.IdP.Store.Account.AccountStore;
import net.den3.IdP.Store.Account.TempAccountStore;
import net.den3.IdP.Store.Auth.AccessTokenStore;
import net.den3.IdP.Store.Auth.AuthFlowStore;
import net.den3.IdP.Store.Auth.LoginTokenStore;
import net.den3.IdP.Store.DBAccess;
import net.den3.IdP.Store.InMemoryRedis;
import net.den3.IdP.Store.InjectionStore;
import net.den3.IdP.Store.Service.ServiceStore;

public class Server {

    public static void start(){
        setupStore();
        setupRouting();
    }

    static void setupRouting(){
        URLTask.setupRouting();
    }

    static void setupStore(){
        InjectionStore store = InjectionStore.get();
        //マスターストア
        store.inject("rdbms",new DBAccess());
        store.inject("memory_db",new InMemoryRedis());

        //スレーブストア
        store.inject("account_attribute",new AccountAttributeStore());
        store.inject("account",new AccountStore());
        store.inject("service",new ServiceStore());
        store.inject("temp_account", new TempAccountStore());
        store.inject("auth_flow",new AuthFlowStore());
        store.inject("access_token",new AccessTokenStore());
        store.inject("login_token",new LoginTokenStore());
    }


}

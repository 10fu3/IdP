package net.den3.IdP.Logic;

import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Auth.LoginResult;
import net.den3.IdP.Security.HashGenerator;
import net.den3.IdP.Store.Account.IAccountStore;

import java.util.Map;
import java.util.Optional;

public class LoginAccount {
    /**
     * アカウントの存在確認とパスワード比較をする
     * @param json 送られてきたJSON メールアドレス/パスワードが含まれている前提
     * @return LoginResult
     */
    public static LoginResult containsAccount(Map<String,String> json){
        return authenticateAccount(json.get("pass"),containsStore(json.get("mail")));
    }

    private static LoginResult containsStore(String mail){
        Optional<IAccount> account = IAccountStore.getInstance().getAccountByMail(mail);
        LoginResult result;
        if(account.isPresent()){
            result = LoginResult.SUCCESS;
            result.account = account;
        }else{
            result = LoginResult.ERROR_NOT_EXIST;
        }
        return result;
    }

    /**
     * パスワードが一致するか確認する
     * @param pass 送られてきたパスワード
     * @param before containsStoreで検査した結果
     * @return LoginResult
     */
    private static LoginResult authenticateAccount(String pass, LoginResult before){
        LoginResult result = before;
        if(result != LoginResult.SUCCESS || !result.account.isPresent()){
            return before;
        }
        IAccount resultAccount = result.account.get();
        String generatePassword = HashGenerator.getSafetyPassword(pass,resultAccount.getMail());

        if (!resultAccount.getPasswordHash().equalsIgnoreCase(generatePassword)) {
            result = LoginResult.ERROR_WRONGPASS;
            result.account = Optional.empty();
        }

        if(resultAccount.getAttribute().isFrozen()){
            result = LoginResult.ERROR_FROZEN_ACCOUNT;
            result.account = Optional.empty();
        }

        return result;
    }
}

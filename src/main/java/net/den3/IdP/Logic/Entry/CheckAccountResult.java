package net.den3.IdP.Logic.Entry;

public enum CheckAccountResult {
    ERROR_MAIL("Invalid e-address"), //メールアドレスではない
    ERROR_PASSWORD_LENGTH("Need 8 characters or more"), //パスワードが基準
    ERROR_SAME("Already registered e-address"), //すでに登録されたメールアドレス
    ERROR_NOT_ALLOW_CHAR("Not allowed characters contain"), // { } , が含まれている
    SUCCESS("");//成功したのでメッセージはとくにない

    private final String text;

    CheckAccountResult(final String text) {
        this.text = text;
    }

    /**
     * メッセージの取得
     * @return 列挙体に振られたメッセージ
     */
    public String getString() {
        return this.text;
    }
}

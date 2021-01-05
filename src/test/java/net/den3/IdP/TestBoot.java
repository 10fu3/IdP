package net.den3.IdP;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestBoot {
    // テスト開始前に1回だけ実行される
    @BeforeAll
    static void beforeAll() {
        System.out.println("Test 開始");
    }

    // テスト開始後に1回だけ実行される
    @AfterAll
    static void afterAll() {
        System.out.println("Test 終了");
    }

    // テストメソッドは private や static メソッドにしてはいけない
    // 値を返してもいけないので戻り値は void にする
    @Test
    void DBTest() {

    }
}

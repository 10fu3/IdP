# IdP
認証認可の基盤

## docker || docker-compose
-   このプロジェクトの.gitをcloneする
- pom.ymlのあるディレクトリまで移動する
-   "maven clean package"を実行
-   "docker build -t IdP ."を実行
-   "docker-compose up" を実行

## 使用技術
- Java 8
- docker
- docker-compose
- maria-db (ほぼすべてのデータの永続化に使用)
- redis (ログイントークンの管理に使用)

## DBの構造
- account_repository アカウント情報を取り扱う

| 列名 | uuid | mail | pass | nick | icon | last_login_time | 
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | 
| プライマリーキー | ○ | - | - | - | - | - | - |
| データ種類 | VARCHAR | VARCHAR | VARCHAR | VARCHAR | VARCHAR | VARCHAR | |
| 役割 | 識別子 | メールアドレス | ハッシュ化したパスワード | ニックネーム | アイコン| 最終ログイン時刻 |

- temp_account_repository

 | 列名 | active_key | mail | pass | nick | registered_date | 
| :---: | :---: | :---: | :---: | :---: | :---: |
| プライマリーキー | ○ | - | - | - | - | - | 
| データ種類 | VARCHAR | VARCHAR | VARCHAR | VARCHAR | VARCHAR | VARCHAR | |
| 役割 | 識別子 | メールアドレス | ハッシュ化したパスワード | ニックネーム | 登録受付時刻 |

- service

## REST API

## 設計方針
- すべての処理はURLTaskを起点に処理が始まる
- データを扱う最小の単位はすべてEntity
- すべてのEntityはEntity固有のIDを持つ
- すべてのEntityはEntityパッケージの外では直接扱われない
- すべてのEntityはEntityパッケージの外で扱うために,インターフェースを経由してEntityから情報を得る
- すべてのEntityはGetterを持つインターフェースを実装している
- SetterにはBuilderを用いてアクセスする (外部連携サービスのシークレットキーの更新のみ例外でインターフェースにSetterが用意されている)
- すべてのStoreはStoreパッケージの外では直接扱われない
- すべてのStoreはCreate Update Read Delete の機能を定義したインターフェースを実装している
- すべてのStoreはStringをキーとするInjectionStoreに登録されている(実行時に登録しなければならない)
- - すべてのStoreのインターフェースにはgetInstanceが存在するが,すべてInjectionStoreからStoreを呼び出している
- DBに存在するデータはすべて文字列
- DBからデータを呼び起こし,Entityに変換するときに数値等に変換される
- SQLはコネクションオブジェクトもすべてOptionalで扱う

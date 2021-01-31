# IdP
認証認可の基盤 (Backend)

下のSPA化されたページ( https://github.com/10fu3/idpDashBoard )にAPIを提供すると同時に認証認可機能を提供/管理する

# メインの機能
<img width="561" alt="スクリーンショット 2021-01-29 5 07 04" src="https://user-images.githubusercontent.com/31952653/106192896-1eb29f00-61f0-11eb-90e2-d0bc93cc4736.png">

# ログイン画面
<img width="561" alt="スクリーンショット 2021-01-29 5 19 09" src="https://user-images.githubusercontent.com/31952653/106193945-861d1e80-61f1-11eb-85a0-36e4879e1dbd.png">


# トップページ
<img width="926" alt="スクリーンショット 2021-01-29 5 00 23" src="https://user-images.githubusercontent.com/31952653/106193000-499cf300-61f0-11eb-9596-76f5e5400303.png">

# アカウントの情報編集ページ
<img width="926" alt="スクリーンショット 2021-01-29 5 00 31" src="https://user-images.githubusercontent.com/31952653/106193056-5a4d6900-61f0-11eb-9ffb-a3fe7e1a64c2.png">

# 連携サービスの登録ページ
<img width="565" alt="スクリーンショット 2021-01-29 5 02 57" src="https://user-images.githubusercontent.com/31952653/106193269-a00a3180-61f0-11eb-9e13-c2f926d8f741.png">

# 登録した連携サービス一覧ページ
<img width="638" alt="スクリーンショット 2021-01-29 5 05 12" src="https://user-images.githubusercontent.com/31952653/106193338-b6b08880-61f0-11eb-8523-7c4ccc733e69.png">

# 登録した連携サービスの情報確認/編集ページ
<img width="561" alt="スクリーンショット 2021-01-29 5 05 57" src="https://user-images.githubusercontent.com/31952653/106193378-c203b400-61f0-11eb-9afa-98945379674d.png">

# 連携したサービス一覧と削除ページ
<img width="561" alt="スクリーンショット 2021-01-29 5 16 06" src="https://user-images.githubusercontent.com/31952653/106193713-39394800-61f1-11eb-9a02-a7ca1219f44b.png">


## docker || docker-compose
-   このプロジェクトの.gitをcloneする
- pom.ymlのあるディレクトリまで移動する
-   "maven clean package"を実行
-   "docker build -t IdP ."を実行
- docker-compose.ymlに必要なパラメーターを記述する
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

https://api.line.me の部分がlocalhostやグローバルIPになる以外下のページに準拠しています

https://developers.line.biz/ja/reference/line-login/#issue-access-token


## 設計方針
- Nullが存在しない
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

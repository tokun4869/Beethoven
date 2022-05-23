## フォルダ構成

- `src`: ソースコードの配置場所
- `bin`: コンパイル後のバイナリファイルの配置場所
- `lib`: ソースコード以外の必要ファイル（画像、音源など）の配置場所

## コンパイル手順

- cd "Beethoven-Client\srcの絶対パス"
- javac -encoding UTF-8 -d ..\bin -classpath ..\lib\mysql-connector-java-8.0.25.jar Server.java
- cd ..\bin
- java -classpath .;..\lib\mysql-connector-java-8.0.25.jar Server

## MySQLの設定手順

- create database beethoven;
- use beethoven;
- create table users (id varchar(20), password varchar(20), jpop int, edm int, jazz int, rock int);
- create table musics (composer varchar(20), title varchar(20), date date, genre varchar(4), file mediumtext, editable tinyint(1), bookmarks int);
- create table bookmarks (id varchar(20), composer varchar(20), title varchar(40));
- create table comments (composer varchar(20), title varchar(40), commenter varchar(20), comment varchar(200));
- create table logins (id varchar(20));
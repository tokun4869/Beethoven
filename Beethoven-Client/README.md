## フォルダ構成

- `src`: ソースコードの配置場所
- `bin`: コンパイル後のバイナリファイルの配置場所
- `lib`: ソースコード以外の必要ファイル（画像、音源など）の配置場所
- `song`: 作曲状態のファイルの配置場所

## コンパイル手順

- cd "Beethoven-Client\srcの絶対パス"
- javac -encoding UTF-8 -d ..\bin Client.java
- cd ..\bin
- java Client
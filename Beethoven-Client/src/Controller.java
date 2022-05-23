import java.io.*;
import java.util.*;

public class Controller {
    private final String sep = "#";
    private Client client;
    private View view;
    private Model model;
    private boolean playing;
    private boolean playmode = false;
    private boolean downloadmode = false;

    public Controller(){
        view = new View("beethoven-v.1.0.0", this);
        model = new Model(this);
        playing = false;
        playmode = false;
        downloadmode = false;
    }

    /* クライアントをセット */
    public void setClient(Client c){
        client = c;
    }

    /*　再生状態を取得する　*/
    public boolean getPlaying(){
        return playing;
    }

    public void processEvent(String cmd){
        String word[] = cmd.split(sep);

        switch(word[0]){
            case "name":
                model.setName(word[1]);
                view.setName(word[1]);
                break;

            case "login":
                if(!client.getLogin()){
                    if(word.length > 1){
                        client.setServerIP(word[1]);
                        view.drawLoginDisplay();
                    }
                    else{
                        view.msgDialog("IPアドレスを入力してください");
                    }
                }
                else{
                    view.msgDialog("既にログインしています");
                }
                break;

            case "signup":
                if(!client.getLogin()){
                    if(word.length > 1){
                        client.setServerIP(word[1]);
                        view.drawSignupDisplay();
                    }
                    else{
                        view.msgDialog("IPアドレスを入力してください");
                    }
                }
                else{
                    view.msgDialog("既にログインしています");
                }
                break;

            case "sendlogin":
                if (!client.getLogin()) {
                    if(client.makeSocket()){
                        client.send(cmd);
                    }
                } else {
                    view.msgDialog("既にログインしています");
                }
                break;

            case "sendsignup":
                if (!client.getLogin()) {
                    if(client.makeSocket()){
                        client.send(cmd);
                    }
                } else {
                    view.msgDialog("既にログインしています");
                }
                break;

            case "logout":
                if(client.getLogin()){
                    client.send("logout");
                }
                else{
                    view.msgDialog("まだログインしていません");
                }
                break;

            case "failconnect":
                view.msgDialog("サーバとの接続に失敗しました");
                break;

            case "listen":
                if(client.getLogin()){
                    client.send("request");
                }
                else{
                    view.msgDialog("ログインしてください");
                    view.drawSelectLoginDisplay();
                }
                break;

            case "make":
                model.make(word[1]);
                break;

            case "open":
                String dir = cmd.substring(5);
                model.open(dir);
                break;

            case "dirnotexist":
                view.setBackMode("project");
                view.drawComposeDisplay();
                break;

            case "direxist":
                view.msgDialog("既にそのファイル名は使われています");
                break;

            case "filenotfound":
                view.msgDialog("正規のpfファイルを選択してください");
                break;

            case "filefound":
                view.setBackMode("project");
                view.drawComposeDisplay();
                break;

            case "save":
                model.save();
                break;

            case "piano", "guitar", "bass", "drum", "synth":
                view.selInst(word[0]);
                break;

            case "play":
                if(!playing){
                    playing = true;
                    model.play();
                }
                break;

            case "pause":
                if(playing){
                    model.pause();
                    playing = false;
                }
                break;

            case "endplay":
                playing = false;
                break;

            case "testplay":
                String testplayer = word[1];
                for(int i = 2; i < word.length; i++){
                    testplayer += File.separator + word[i];
                }
                model.testPlay(testplayer);
                break;

            case "testpause":
                model.testPause();
                break;

            case "bar":
                int n = Integer.parseInt(word[1]);
                view.setNumber(n);
                model.setNumber(n);
                break;

            case "addbar":
                view.addBar();
                model.addBar();
                break;

            case "liblist":
                ArrayList<String> source = model.getSource();
                ArrayList<String> used = view.getUsedList();
                view.resetLib();
                for(int i = 0; i < source.size(); i++){
                    String[] s = source.get(i).split(sep);
                    String data = s[0] + sep + s[3];
                    boolean useflag = false;
                    if(s[1].equals(word[1])){
                        if(s[2].equals(word[2])){
                            for(int j = 0; j < used.size(); j++){
                                if(data.equals(used.get(j))){
                                    useflag = true;
                                    break;
                                }
                            }
                            if(!useflag){
                                view.addLib(s[0], s[1], s[2], s[3]);
                            }
                        }
                    }
                }
                view.drawSelectLoopDisplay();
                break;

            case "plus":
                model.addSource(word[1], word[2], word[3], word[4]);
                view.addSource(word[2], word[1], word[4]);
                break;

            case "volume":
                int volume = model.getVolume(Integer.parseInt(word[1]), word[2], Integer.parseInt(word[3]));
                if(volume != -1){
                    view.setVolume(volume);
                }
                else{
                    view.setVolume(255 / 2);
                }
                break;

            case "sendvolume":
                model.setVolume(Integer.parseInt(word[1]), word[2], Integer.parseInt(word[3]), Integer.parseInt(word[4]));
                view.setVolume(Integer.parseInt(word[4]));
                break;

            case "delete":
                model.delSource(word[2], Integer.parseInt(word[3]));
                view.delSource(word[2], Integer.parseInt(word[3]));
                break;

            case "search":
                client.send(cmd);
                break;

            case "listenplay":
                if(Boolean.parseBoolean(word[1])){
                    client.send("download" + sep + word[2] + sep + word[3]);
                }
                else{
                    client.send("play" + sep + word[2] + sep + word[3]);
                }
                break;

            case "bookmark":
                client.send(cmd);
                break;

            case "sendcomment":
                client.send(cmd);
                break;

            case "showcomment":
                view.setSongData(word[1], word[2]);
                client.send(cmd);
                break;

            case "upload":
                if(client.getLogin()){
                    view.drawUploadDisplay();
                }
                else{
                    view.msgDialog("ログインしてください");
                    view.drawSelectLoginDisplay();
                }
                break;
            
            case "update":
                client.webConnection();
                break;
            
            case "sendupload":  // 曲をアップロードする
                String contents;
                contents = System.lineSeparator() + model.upload(word[1]);
                client.send(cmd + contents);
                break;

            case "backcompose":
                model.pause();
                playing = false;
                model.reset();
                view.resetCompose();
                if(view.getBackMode().equals("project")){
                    view.drawProjectDisplay();
                }
                else if(view.getBackMode().equals("search")){
                    client.send("request");
                }
                break;

            case "backlisten":
                model.pause();
                model.reset();
                view.resetCompose();
                view.drawSearchDisplay();
                break;

            case "backsearch":
                client.send("exit");
                view.drawStartDisplay();
                break;
        }
    }
    
    public void processServerInput(String cmd) {
        String[] input = cmd.split(sep);
        switch (input[0]) {
            case "successregist":    // 新規登録成功
                view.msgDialog("新規登録に成功しました");
                client.setLogin(true);
                break;
            
            case "successlogin":    // ログイン成功
                view.msgDialog("ログインに成功しました");
                client.setLogin(true);
                break;
            
            case "successdelete":    // 削除成功
                view.msgDialog("削除に成功しました");
                break;
            
            case "successupload":
                view.msgDialog("アップロードに成功しました");
                break;

            case "failureregist":    // 新規登録失敗
                view.msgDialog("ログインに失敗しました");
                break;
            
            case "failurelogin":    // ログイン失敗
                view.msgDialog("ログインに失敗しました");
                break;
            
            case "failuredelete":    // 削除失敗
                view.msgDialog("削除に失敗しました");
                break;

            case "failureupload":
                view.msgDialog("アップロードに失敗しました");
                break;
            
            case "finlogout":
                client.closeSocket();
                view.msgDialog("ログアウトに成功しました");
                break;

            case "search":
                view.resetRankList();
                for(int i = 1; i < input.length; i++){
                    view.addRankList(input[i]);
                }
                break;

            case "mysong":    // ユーザ曲のリストを受信
                view.resetMyList();
                for(int i = 1; i < input.length; i++){
                    view.addMyList(input[i]);
                }
                break;
            
            case "recommend":    // おすすめ曲のリストを受信
                view.resetRecList();
                for(int i = 1; i < input.length; i++){
                    view.addRecList(input[i]);
                }
                break;

            case "ranking":    // ランキングのリストを受信
                view.resetRankList();
                for(int i = 1; i < input.length; i++){
                    view.addRankList(input[i]);
                }
                break;
            
            case "bookmarks":    // いいね曲のリストを受信
                view.resetFavList();
                for(int i = 1; i < input.length; i++){
                    view.addFavList(input[i]);
                }
                break;

            case "searchScreen":
                view.drawSearchDisplay();
                break;

            case "comment":
                view.resetComList();
                for(int i = 1; i < input.length; i++){
                    view.addComList(input[i]);
                }
                view.drawCommentDisplay();
                break;

            case "play":    // 曲の再生
                playmode = true;
                break;

            case "download":    //曲のダウンロード
                downloadmode = true;
                break;
        
            default:
                if(playmode){
                    if(cmd.equals("end")){
                        playmode = false;
                        view.drawListenDisplay();
                    }
                    else{
                        processEvent(cmd);
                    }
                }
                else if(downloadmode){
                    if(cmd.equals("end")){
                        downloadmode = false;
                        view.setBackMode("search");
                        view.drawComposeDisplay();
                        client.send("exit");
                    }
                    else{
                        processEvent(cmd);
                    }
                }
                break;
        }
    }
}
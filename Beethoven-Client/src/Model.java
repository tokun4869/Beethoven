/*　==================　*/
/*　ライブラリ　　　　　　*/
/*　==================　*/

import java.io.*;
import java.nio.file.*;
import java.nio.charset.MalformedInputException;
import java.util.*;
import javax.sound.sampled.*;



/*　==================　*/
/*　クラス定義　　　　　　*/
/*　==================　*/

public class Model{

    /*　==================　*/
    /*　メンバ変数　　　　　　*/
    /*　==================　*/

    private final static String sep = "#";
    private Controller ctrl;                                            //Controllerクラスの参照
    private String path;                                                //Beethoven-Clientの絶対パス
    private String name;                                                //曲名
    private ArrayList<String> source = new ArrayList<>();               //音源ライブラリ一覧
    private int number;                                                 //小節番号
    private ArrayList<Bars> bars = new ArrayList<>();                   //小節一覧
    private ArrayList<Timer> alarm = new ArrayList<>();                 //再生待機用Timer一覧
    private TestPlayer tp;                                              //音源試聴用クラス
    private static final int SAMPLE_RATE = 44100;                       //音源ライブラリのサンプリングレート



    /*　==================　*/
    /*　コンストラクタ　　　　*/
    /*　==================　*/

    public Model(Controller ctrl){
        this.ctrl = ctrl;       //Controllerクラスの参照を取得        
        path = getAbsPath();    //パスの取得        
        liblistUpdate();        //音源ライブラリの読み込み
    }


    
    /*　==================　*/
    /*　メソッド　　　　　　　*/
    /*　==================　*/

    /*　音源ライブラリ一覧を取得する　*/
    public ArrayList<String> getSource(){
        return source;
    }

    /*　小節を選択する　*/
    public void setNumber(int n){
        number = n; //小節番号をnにする
    }

    /*　小節を取得する　*/
    public int getNumber(){
        return number;
    }

    /*　曲名を与える　*/
    public void setName(String name){
        this.name = name;
    }

    /*　曲名を取得する　*/
    public String getName(){
        return name;
    }

    /*　プロジェクトの絶対パスを取得する　*/
    public String getAbsPath(){
        Path p1 = Paths.get("");
        Path p2 = p1.toAbsolutePath();
        String[] word;
        String path = "";
        if(File.separator.equals("\\")){
            word = p2.toString().split("\\\\");
        }
        else{
            word = p2.toString().split(File.separator);
        }
        
        for(int i = 0; i < word.length - 1; i++){
            path += word[i] + File.separator;
        }

        return path;
    }

    /*　小節一覧を取得する　*/
    public ArrayList<Bars> getBars(){
        return bars;
    }

    /*　音源を配置する　*/
    public void addSource(String libraryname, String inst, String genre, String filename){
        String source = path + "lib" + File.separator + "sound" + File.separator + libraryname + File.separator + inst + File.separator + genre + File.separator + filename;
        bars.get(number).addPlayer(inst, genre, source);
    }

    /*　音源を削除する　*/
    public void delSource(String inst, int playNo){
        int index = bars.get(number).getInstIndex(inst, playNo);
        bars.get(number).delPlayer(index);
    }

    /*　作曲状態を初期化する　*/
    public void reset(){
        while(bars.size() > 0){
            bars.remove(0);
        }
    }

    /*　音源格納・再生クラスを取得する　*/
    public Player getPlayer(int i, int j){
        return bars.get(i).player.get(j);
    }

    /*　曲の新規作成をする　*/
    public void make(String name){
        Path p = Paths.get(path + "song" + File.separator + name + ".pf");   //曲のディレクトリを与える

        if(Files.notExists(p)){
            ctrl.processEvent("dirnotexist");
            ctrl.processEvent("name" + sep + name);
            ctrl.processEvent("addbar");                                //小節の追加
            ctrl.processEvent("bar" + sep + 0);                         //小節番号の初期化
            save();
        }
        else{
            ctrl.processEvent("direxist");
        }
    }

    /*　作曲ファイルを保存する　*/
    public void save(){
        try{
            String songPath = "song" + File.separator + name + ".pf";
            File file = new File(path + songPath);    //プロジェクトファイルを開く
            FileWriter fw = new FileWriter(file);                                   //文字単位の書き込み用クラス
            BufferedWriter bw = new BufferedWriter(fw);                             //行単位の書き込みクラス

            bw.write("name" + sep + name);   //曲名を書き込む
            bw.newLine();
            for(int i = 0; i < bars.size(); i++){   //小節数を書き込む
                bw.write("addbar");
                bw.newLine();
            }

            for(int i = 0; i < bars.size(); i++){
                bw.write("bar" + sep + i);   //i番目の小節を選択する
                bw.newLine();
                for(int j = 0; j < bars.get(i).player.size(); j++){
                    String lib = getPlayer(i, j).getLib();
                    String inst = getPlayer(i, j).getInstrument();
                    String genre = getPlayer(i, j).getGenre();
                    String name = getPlayer(i, j).getName();
                    bw.write("plus" + sep + lib + sep + inst + sep + genre + sep + name);  //音源の配置情報を書き込む
                    bw.newLine();
                }
            }

            for(int i = 0; i < bars.size(); i++){
                for(int j = 0; j < bars.get(i).player.size(); j++){
                    String inst = getPlayer(i, j).getInstrument();
                    int volume = getPlayer(i, j).getVolume();
                    for(int k = 0; k < 4; k++){
                        if(bars.get(i).getInstIndex(inst, k) != -1){
                            bw.write("sendvolume" + sep + i + sep + inst + sep + k + sep + volume);  //音源の音量を書き込む
                            bw.newLine();
                        }
                    }
                }
            }

            bw.write("bar" + sep + number);  //選択している小節を書き込む
            bw.newLine();
            
            bw.write("end");

            bw.close(); //書き込みを終了する
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /*　アップロードする作曲データを取得する　*/
    public String upload(String songname){
        String file = "";
        file += "name" + sep + songname + System.lineSeparator();        //曲名を書き込む
        for(int i = 0; i < bars.size(); i++){   
            file += "addbar" + System.lineSeparator();              //小節数を書き込む
        }

        for(int i = 0; i < bars.size(); i++){
            file += "bar" + sep + i + System.lineSeparator();            //i番目の小節を選択する
            for(int j = 0; j < bars.get(i).player.size(); j++){
                String lib = getPlayer(i, j).getLib();
                String inst = getPlayer(i, j).getInstrument();
                String genre = getPlayer(i, j).getGenre();
                String name = getPlayer(i, j).getName();
                file += "plus" + sep + lib + sep + inst + sep + genre + sep + name + System.lineSeparator(); //音源の配置情報を書き込む
            }
        }

        for(int i = 0; i < bars.size(); i++){
            for(int j = 0; j < bars.get(i).player.size(); j++){
                String inst = getPlayer(i, j).getInstrument();
                int volume = getPlayer(i, j).getVolume();
                for(int k = 0; k < 4; k++){
                    if(bars.get(i).getInstIndex(inst, k) != -1){
                        file += "sendvolume" + sep + i + sep + inst + sep + k + sep + volume + System.lineSeparator();   //音源の音量を書き込む
                    }
                }
            }
        }

        file += "bar" + sep + number + System.lineSeparator();   //i番目の小節を選択する
        file += "end";  //i番目の小節を選択する

        return file;
    }

    /*　作曲ファイルを読み込む　*/
    public void open(String path){
        try{
            File f = new File(path);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String cmd;
            
            if(path.lastIndexOf(".pf") == -1){
                ctrl.processEvent("filenotfound");
            }
            else{
                ctrl.processEvent("filefound");
                while((cmd = br.readLine()) != null){
                    ctrl.processEvent(cmd);
                }
            }

            br.close();
        }
        catch(FileNotFoundException e){
            ctrl.processEvent("filenotfound");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /*　作曲ファイルをダウンロードする　*/
    public void download(String contents){
        try{
            String songPath = "song" + File.separator + name + ".pf";
            File file = new File(path + songPath);      //プロジェクトファイルを開く
            FileWriter fw = new FileWriter(file);       //文字単位の書き込み用クラス
            BufferedWriter bw = new BufferedWriter(fw); //行単位の書き込みクラス

            bw.write(contents); //ファイルを書き込む
            bw.close();         //書き込みを終了する
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /*　現在の小節から曲を再生する　*/
    public void play(){
        if(number < bars.size()){   //選択中の小節が存在するとき
            bars.get(number).play();    //選択中の小節の音源を再生する
        }
    }

    /*　現在の小節で曲を停止する　*/
    public void pause(){
        for(int i = 0; i < bars.size(); i++){   //全ての小節に処理を行う
            bars.get(i).pause();    //小節の音源を停止する
        }
    }

    /*　選択した音源を視聴する　*/
    public void testPlay(String name){
        testPause();
        tp = new TestPlayer(name);
        tp.play();
    }

    /*　選択した音源の視聴を停止する　*/
    public void testPause(){
        if(tp != null){
            tp.pause();
        }
    }

    /*　n個小節を追加する　*/
    public void addBar(){
        Bars newb = new Bars(); //新しい小節
        if(bars.size() > 0){    //既に小節がある
            bars.get(bars.size() - 1).setNext(newb);    //最後の小節の次の小節を、新しい小節にする
        }
        bars.add(newb); //新しい小節を小節一覧に追加する
    }

    /*　音源リストを更新する　*/
    public void liblistUpdate(){
        while(source.size() > 0){
            source.remove(0);
        }

        String folderPath = path + "lib" + File.separator + "sound";
        String libName;
        String instName;
        String genreName;
        String fileName;
        String newRecord;

        File folder = new File(folderPath);
        for(File library: folder.listFiles()){
            for(File inst: library.listFiles()){
                for(File genre: inst.listFiles()){
                    for(File file: genre.listFiles()){
                        libName = library.getName();
                        instName = inst.getName();
                        genreName = genre.getName();
                        fileName = file.getName();
                        newRecord = libName + sep + instName + sep + genreName + sep + fileName;
                        source.add(newRecord);
                    }
                }
            }
        }
    }

    /*　音源を再生できるようにする　*/
    public Clip createClip(File path){
        try(AudioInputStream ais = AudioSystem.getAudioInputStream(path)){
            AudioFormat af = ais.getFormat();
            DataLine.Info dataline = new DataLine.Info(Clip.class, af);
            Clip c = (Clip)AudioSystem.getLine(dataline);
            c.open(ais);

            return c;
        }
        catch(MalformedInputException e){
            e.printStackTrace();
        }
        catch(UnsupportedAudioFileException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(LineUnavailableException e){
            e.printStackTrace();
        }

        return null;
    }

    /*　音量調整する　*/
    public void setVolume(int barNo, String inst, int playNo, int volume){ 
        int count = 0;
        for(int i = 0; i < bars.get(barNo).player.size(); i++){
            if(bars.get(barNo).player.get(i).getInstrument().equals(inst)){
                if(count++ == playNo){
                    bars.get(barNo).player.get(i).setVolume(volume);
                    break;
                }
            }
        }
    }

    /*　現在の音量を取得する　*/
    public int getVolume(int barNo, String inst, int playNo){ 
        int count = 0;
        for(int i = 0; i < bars.get(barNo).player.size(); i++){
            if(bars.get(barNo).player.get(i).getInstrument().equals(inst)){
                if(count++ == playNo){
                    return bars.get(barNo).player.get(i).getVolume();
                }
            }
        }

        return -1;
    }



    /*　==================　*/
    /*　内部クラスBars　　　　*/
    /*　==================　*/

    class Bars{

        /*　==================　*/
        /*　メンバ変数　　　　　　*/
        /*　==================　*/

        ArrayList<Player> player = new ArrayList<>();   //音源を格納し再生するクラス
        Bars next = null;                               //次の小節
        Timer timer;                                    //次の小節を再生する時間を管理するクラス
        TimerTask task;                                 //時間になったときの処理



        /*　==================　*/
        /*　メソッド　　　　　　　*/
        /*　==================　*/

        /*　この小節を再生する　*/
        void play(){
            int delay = (int)((double)getSize() / SAMPLE_RATE * 1000);  //小節の再生時間を計算する

            for(int i = 0; i < player.size(); i++){ //すべての音源について
                player.get(i).play();                               //小節のi番目の音源を再生する
            }
            task = new TimerTask(){ //時間になったときの処理
                public void run(){
                    pause();    //小節を停止する
                    if(next != null){   //次の小節が存在するとき
                        if(delay > 0){
                            next.play();                                    //次の小節を再生する
                            ctrl.processEvent("bar" + sep + (number + 1));  //Controllerに命令を送る
                        }
                    }
                    else{
                        ctrl.processEvent("endplay");
                    }
                }
            };
            timer = new Timer();                                        //新しいTimerを与える
            timer.schedule(task, delay);                                //再生時間後にtaskに設定した処理を行うようにセット
            alarm.add(timer);                                           //再生待機中の一覧に加える
        }

        /*　この小節を停止する　*/
        void pause(){
            for(int i = 0; i < player.size(); i++){ //全ての音源について
                player.get(i).pause();                             //小節のi番目の音源を停止する
            }
            for(int i = 0; i < alarm.size(); i++){  //全ての再生待機しているTimerについて
                alarm.get(i).cancel();  //i番目のTimerを停止する
            }
        }

        /*　音源格納・再生クラスを追加する　*/
        void addPlayer(String instrument, String gerne, String path){
            Player newp = new Player(instrument, gerne, path);    //新しいインスタンスを作る
            player.add(newp);                               //新しいインスタンスを一覧に加える
        }

        /*　n番目の音源を削除する　*/
        void delPlayer(int n){
            player.remove(n);
        }

        /*　次の小節の参照を与える　*/
        void setNext(Bars b){
            next = b;
        }

        /*　小節中の音源で最長のもののサンプル長を取得する　*/
        int getSize(){
            int max = 0;    //最大サンプル長の初期化

            for(int i = 0; i < player.size(); i++){ //すべての音源について
                int buf = player.get(i).getSize();  //i番目の音源のサンプル長
                if(max < buf){  //i番目のサンプル長が最大サンプル長を越えるとき
                    max = buf;  //i番目のサンプル長を最大サンプル長にする
                }
            }

            return max;
        }

        /*　対応する楽器のn番目が存在するか確認する　*/
        int getInstIndex(String inst, int n){
            int count = 0;
            for(int i = 0; i < player.size(); i++){
                if(inst.equals(player.get(i).getInstrument())){
                    if(n == count){
                        return i;
                    }
                    count++;
                }
            }
            return -1;
        }
    }



    /*　==================　*/
    /*　内部クラスPlayer 　　*/
    /*　==================　*/

    class Player{

        /*　==================　*/
        /*　メンバ変数　　　　　　*/
        /*　==================　*/

        String lib;         //ライブラリ
        String instrument;  //楽器名
        String genre;       //ジャンル名
        String name;        //音源ファイル名
        String path;        //音源の絶対パス
        File file;          //音源ファイル
        Clip clip;          //再生用クラス



        /*　==================　*/
        /*　コンストラクタ　　　　*/
        /*　==================　*/

        Player(String instrument, String genre, String path){
            this.instrument = instrument;   //楽器名を与える
            this.genre = genre;             //ジャンル名を与える
            this.path = path;               //絶対パスを与える
            String[] word;
            if(File.separator.equals("\\")){
                word = path.split("\\\\");
            }
            else{
                word = path.split(File.separator);
            }
            lib = word[word.length - 4];
            name = word[word.length - 1];
            setSound(path);
        }



        /*　==================　*/
        /*　メソッド　　　　　　　*/
        /*　==================　*/

        /*　音源を再生する　*/
        public void play(){
            pause();
            clip.start();   //音源の再生を始める
        }

        /*　音源を停止する　*/
        public void pause(){
            clip.stop();                //再生を停止する
            clip.flush();               //残った再生データを削除する
            clip.setFramePosition(0);   //再生位置を先頭に設定する
        }

        /*　音源を設定する　*/
        public void setSound(String path){
            file = new File(path);      //音源ファイルを開く
            clip = createClip(file);    //音源を再生できるようにする
        }

        /*　ライブラリ名を取得する　*/
        public String getLib(){
            return lib;
        }

        /*　楽器名を取得する　*/
        public String getInstrument(){
            return instrument;
        }

        /*　ジャンル名を取得する　*/
        public String getGenre(){
            return genre;
        }

        /*　ファイル名を取得する　*/
        public String getName(){
            return name;
        }

        /*　絶対パスを取得する　*/
        public String getPath(){
            return path;
        }

        /*　クリップを取得する　*/
        public Clip getClip(){
            return clip;
        }

        /*　音源のサンプル長を取得する　*/
        public int getSize(){
            return clip.getFrameLength();
        }

        /*　音量を変更する　*/
        public void setVolume(int volume){
            FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue((float)(Math.log10(volume / 255.0 * 2) * 20));
        }

        /*　音量を取得する　*/
        public int getVolume(){
            FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            double linearScalar = (Math.pow(10, control.getValue() / 20.0));
            return (int)(linearScalar * 255 / 2);
        }
    }

    

    /*　==================　*/
    /*　内部クラスTestPlayer */
    /*　==================　*/

    class TestPlayer extends Thread{

        /*　==================　*/
        /*　メンバ変数           */
        /*　==================　*/

        String name;    //音源ファイル名
        File file;      //音源ファイル
        Clip clip;      //再生用クラス



        /*　==================　*/
        /*　コンストラクタ       */
        /*　==================　*/

        TestPlayer(String dir){
            String[] word;
            if(File.separator.equals("\\")){
                word = dir.split("\\\\");
            }
            else{
                word = dir.split(File.separator);
            }
            name = word[word.length - 1];                                                       //音源ファイル名                            
            file = new File(path + "lib" + File.separator + "sound" + File.separator + dir);    //音源ファイルを開く
            clip = createClip(file);                                                            //音源を再生できるようにする
        }

        /*　音源を再生する　*/
        void play(){
            pause();        //音源を停止する
            clip.start();   //音源の再生を始める
        }

        /*　音源を停止する　*/
        void pause(){
            clip.stop();                //再生を停止する
            clip.flush();               //残った再生データを削除する
            clip.setFramePosition(0);   //再生位置を先頭に設定する
        }
    }
}
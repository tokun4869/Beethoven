/*　==================　*/
/*　ライブラリ　　　　　　*/
/*　==================　*/

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;

/*　==================　*/
/*　クラス定義　　　　　　*/
/*　==================　*/

public class View implements ActionListener, ChangeListener, KeyListener, WindowListener{

    /*　==================　*/
    /*　メンバ変数　　　　　　*/
    /*　==================　*/

    final static String sep = "#"; 
    final static int INSTSIZE = 5;                                  //楽器の種類
    final static Color bgc = new Color(100, 0, 20);                 //背景色
    private Controller ctrl;                                        //Controllerクラスの参照
    private JFrame frame;                                           //画面表示
    private JFrame subFrame;                                        //サブウィンドウ
    private JTextField username;                                    //ユーザ名の入力欄
    private JPasswordField pass;                                    //パスワード入力欄
    private JPasswordField passCheck;                               //確認用パスワード入力欄
    private JTextField titleField;                                  //曲名を入力する欄
    private JComboBox<String> genreCmb;                             //ジャンルの選択肢
    private String[] genreData = {"EDM", "JPOP", "ROCK", "JAZZ"};   //ジャンルの選択肢の内容
    private ButtonGroup editable;                                   //編集制限を示すボタングループ
    private JTextField searchUser;                                  //作曲者のユーザ名の検索欄
    private JTextField searchTitle;                                 //曲名の検索欄
    private JTextField searchGenre;                                 //ジャンルの検索欄
    private JTextField commentArea;                                 //コメント入力欄
    private String serverIP = "";                                   //サーバIP
    private JTextField serverIPField;                               //サーバIP入力欄
    private ArrayList<BarPane> barPane = new ArrayList<>();         //小節単位のパネル
    private JPanel workPane;                                        //配置パネル
    private JButton plusBtn;                                        //小節追加ボタン
    private String backmode;                                        //作曲画面の戻るボタンの動作
    private JSlider volumeSlider;                                   //音量調整スライダー
    private JLabel volumeLabel;                                     //音量調整ラベル
    private int volume;                                             //現在操作している音量
    private String path;                                            //Beethoven-Clientの絶対パス
    private String name;                                            //曲名
    private int number;                                             //選択している小節番号
    private String inst, genre;                                     //楽器・ジャンル名
    private String selCmd;                                          //選択した音源の命令
    private String composer;                                        //選択した曲のタイトル
    private String title;                                           //選択した曲の作曲者
    private ArrayList<String> libList = new ArrayList<>();          //音源一覧
    private ArrayList<String> commentList = new ArrayList<>();      //コメント一覧
    private ArrayList<String> mySongList = new ArrayList<>();       //自分の曲の一覧
    private ArrayList<String> recSongList = new ArrayList<>();      //おすすめの曲の一覧
    private ArrayList<String> rankSongList = new ArrayList<>();     //ランキングの曲の一覧
    private ArrayList<String> favSongList = new ArrayList<>();      //いいねの曲の一覧
    private ImageIcon bg, logo, login, loginRollover, back, backRollover, piano, guitar, bass, drum, synth, play, pause, plus, playRollover, pauseRollover, playCirc, pauseCirc, commentCirc, playCircRollover, pauseCircRollover, commentCircRollover, select, save, upload, search, searchRollover, favorite, favoriteRollover, favoritePressed, favoritePressedRollover;    //画像アイコン
    


    /*　==================　*/
    /*　コンストラクタ　　　 */
    /*　==================　*/

    public View(String title, Controller ctrl){
        this.ctrl = ctrl;   //Controllerへの参照を取得する

        /*　ウインドウ表示　*/
        frame = new JFrame(title);                              //ウインドウを作る
        frame.setSize(1280, 720);                               //ウインドウの大きさを決める
        frame.setVisible(false);                                //ウインドウを可視化する
        frame.setResizable(false);                              //ウインドウの大きさを固定する
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //ウインドウを閉じたときにプログラムを終了するようにする
        subFrame = new JFrame();                                //サブウインドウを作る
        subFrame.setSize(640, 400);                             //サブウインドウの大きさを決める
        subFrame.setVisible(false);                             //サブウインドウを隠す
        subFrame.setResizable(false);                           //サブウインドウの大きさを固定する

        /*　絶対パスの取得　*/
        path = getAbsPath();

        /*　画像の読み込み　*/
        String iconpath = "lib" + File.separator + "icon" + File.separator;
        bg = new ImageIcon(path + iconpath + "background.png");                                       //背景
        logo = new ImageIcon(path + iconpath + "logo.png");                                           //タイトルロゴ 
        login = new ImageIcon(path + iconpath + "login.png");                                         //ログインボタン
        loginRollover = new ImageIcon(path + iconpath + "login_rollover.png");                        //ログインボタン（ロールオーバー）
        back = new ImageIcon(path + iconpath + "back.png");                                           //戻るボタン
        backRollover = new ImageIcon(path + iconpath + "back_rollover.png");                          //戻るボタン（ロールオーバー）
        piano = new ImageIcon(path + iconpath + "piano.png");                                         //ピアノアイコン
        guitar = new ImageIcon(path + iconpath + "guitar.png");                                       //ギターアイコン
        bass = new ImageIcon(path + iconpath + "bass.png");                                           //ベースアイコン
        drum = new ImageIcon(path + iconpath + "drum.png");                                           //ドラムアイコン
        synth = new ImageIcon(path + iconpath + "synth.png");                                         //メロディアイコン
        play = new ImageIcon(path + iconpath + "play.png");                                           //再生ボタン
        pause = new ImageIcon(path + iconpath + "pause.png");                                         //停止ボタン
        playRollover = new ImageIcon(path + iconpath + "play_rollover.png");                          //再生ボタン（ロールオーバー）
        pauseRollover = new ImageIcon(path + iconpath + "pause_rollover.png");                        //停止ボタン（ロールオーバー）
        playCirc = new ImageIcon(path + iconpath + "play_circ.png");                                  //再生円ボタン
        pauseCirc = new ImageIcon(path + iconpath + "pause_circ.png");                                //停止円ボタン
        commentCirc = new ImageIcon(path + iconpath + "comment_circ.png");                            //コメント円ボタン
        playCircRollover = new ImageIcon(path + iconpath + "play_circ_rollover.png");                 //再生円ボタン（ロールオーバー）
        pauseCircRollover = new ImageIcon(path + iconpath + "pause_circ_rollover.png");               //停止円ボタン（ロールオーバー）
        commentCircRollover = new ImageIcon(path + iconpath + "comment_circ_rollover.png");           //コメント円ボタン（ロールオーバー）
        plus = new ImageIcon(path + iconpath + "plus.png");                                           //小節追加ボタン
        select = new ImageIcon(path + iconpath + "select.png");                                       //選択ボタン
        save = new ImageIcon(path + iconpath + "save.png");                                           //保存ボタン
        upload = new ImageIcon(path + iconpath + "upload.png");                                       //共有ボタン
        search = new ImageIcon(path + iconpath + "search.png");                                       //検索ボタン
        searchRollover = new ImageIcon(path + iconpath + "search_rollover.png");                      //検索ボタン（ロールオーバー）
        favorite = new ImageIcon(path + iconpath + "favorite.png");                                   //いいねボタン
        favoriteRollover = new ImageIcon(path + iconpath + "favorite_rollover.png");                  //いいねボタン（ロールオーバー）
        favoritePressed = new ImageIcon(path + iconpath + "favorite_pressed.png");                    //いいねボタン（押した後）
        favoritePressedRollover = new ImageIcon(path + iconpath + "favorite_pressed_rollover.png");   //いいねボタン（押した後、ロールオーバー）

        /*　画面初期化　*/
        drawComposeDisplay();   //作曲画面の初期化をする
        drawStartDisplay();     //スタート画面を描画する
        frame.setVisible(true); //画面を表示する
        frame.validate();       //画面を更新する
    }



    /*　==================　*/
    /*　内部クラスBarPane　　*/
    /*　==================　*/

    class BarPane extends JPanel implements MouseListener{

        /*　==================　*/
        /*　メンバ変数　　　　　　*/
        /*　==================　*/

        JButton numBtn;                                     //小節を選択するボタン
        GridPane[] gridPane;                                //各楽器を配置するパネル配列：０＝ピアノ、１＝ギター、２＝ベース、３＝ドラム、４＝メロディ
        LineBorder border = new LineBorder(bgc, 2, true);   //枠線を管理するクラス
        ArrayList<String> usedList = new ArrayList<>();     //使用している音源一覧



        /*　==================　*/
        /*　コンストラクタ　　　　*/
        /*　==================　*/
        
        BarPane(View ref, int n){
            /*　レイアウトを決定する　*/
            setLayout(new GridLayout(INSTSIZE + 1, 1, 10, 10)); //パネルレイアウトを決定する

            /*　ボタンを作成する　*/
            numBtn = new JButton(Integer.toString(n));          //ボタンに小節番号を与える
            numBtn.setPreferredSize(new Dimension(100, 80));    //ボタンの大きさを決める
            numBtn.setContentAreaFilled(false);                 //ボタンの背景の塗りつぶしをなくす
            numBtn.setFocusPainted(false);                      //ボタンのクリック時の枠線をなくす
            numBtn.setBorder(border);                           //ボタンに枠線を与える
            numBtn.setActionCommand("bar" + sep + n);                //ボタンクリック時の命令文を与える
            numBtn.addActionListener(ref);                      //ボタンクリック時の命令送信先を与える
            numBtn.addMouseListener(this);                      //ボタンにマウスをロールオーバーしたときに処理を行えるようにする

            /*　パネルを作成する　*/
            gridPane = new GridPane[INSTSIZE];                      //パネルの配列を作る
            gridPane[0] = new GridPane(ref, n, "piano", piano);     //ピアノ用のパネルを作る
            gridPane[1] = new GridPane(ref, n, "guitar", guitar);   //ギター用のパネルを作る
            gridPane[2] = new GridPane(ref, n, "bass", bass);       //ベース用のパネルを作る
            gridPane[3] = new GridPane(ref, n, "drum", drum);       //ドラム用のパネルを作る
            gridPane[4] = new GridPane(ref, n, "synth", synth);   //メロディ用のパネルを作る

            /*　パネルにコンポーネントを追加する　*/
            add(numBtn);    //小節選択ボタンを全体パネルに加える
            border = new LineBorder(Color.DARK_GRAY, 1, true);  //枠線を作成する
            for(int i = 0; i < INSTSIZE; i++){
                gridPane[i].setBorder(border);  //楽器配置パネルに枠線を設定する
                add(gridPane[i]);               //楽器配置パネルを全体パネルに加える
            }
        }



        /*　==================　*/
        /*　メソッド　　　　　　　*/
        /*　==================　*/

        /*　マウスでクリック時の動作　*/
        public void mouseClicked(MouseEvent e) {
            
        }

        /*　マウスでクリック中の動作　*/
        public void mousePressed(MouseEvent e) {
            
        }

        /*　マウスでクリックを止めた時の動作　*/
        public void mouseReleased(MouseEvent e) {
            
        }

        /*　マウスで対象からポインタを外した時の動作　*/
        public void mouseExited(MouseEvent e) {
            numBtn.setBorder(new LineBorder(bgc, 2, true)); //通常時の描画にする
        }

        /*　マウスで対象にポインタを当てた時の動作　*/
        public void mouseEntered(MouseEvent e) {
            numBtn.setBorder(new LineBorder(bgc, 3, true)); //ロールオーバー時の描画にする
        }
    }



    /*　==================　*/
    /*　内部クラスGridPane 　*/
    /*　==================　*/

    class GridPane extends JPanel{

        /*　==================　*/
        /*　メンバ変数　　　　　　*/
        /*　==================　*/

        static final int GRIDSIZE = 2;                      //音源を配置できる縦横の上限値
        static final int RANGE = GRIDSIZE * GRIDSIZE;       //音源を配置できる上限値
        private InstBtn[] instBtn = new InstBtn[RANGE];     //配置した音源を表すボタン
        private int size;                                   //配置している音源の数



        /*　==================　*/
        /*　コンストラクタ　　　　*/
        /*　==================　*/

        GridPane(View ref, int num, String inst, ImageIcon icon){
            size = 0;                               //音源数を初期化する

            /* レイアウトを決定する */
            setLayout(new GridLayout(GRIDSIZE, GRIDSIZE, 5, 5));    //全体のレイアウトを決める
            setPreferredSize(new Dimension(100, 80));               //全体の大きさを決める
            setOpaque(false);                                       //背景色を透過する

            /* ボタンを初期化する */
            for(int i = 0; i < instBtn.length; i++){
                instBtn[i] = new InstBtn(ref, num, inst, i, icon);  //新しいボタンを与える
                instBtn[i].addActionListener(ref);                  //ボタン動作の送信先を決める
                add(instBtn[i]);                                    //パネルにボタンを加える
            }

            setBlank(0);    //配置を全て空白にする
        }



        /*　==================　*/
        /*　メソッド　　　　　　　*/
        /*　==================　*/

        /*　音源を追加する　*/
        public void setInst(){
            if(size < instBtn.length){
                instBtn[size].setInst();    //音源を配置する;
                if(size + 1 < instBtn.length){
                    setBlank(size + 1);     //以降のボタンを空白にする
                }
                size++;
            }
        }

        /*　空白を埋める　*/
        private void setBlank(int n){
            for(int i = n; i < instBtn.length; i++){
                instBtn[i].setBlank();
            }
        }

        /*　音源を削除する　*/
        public void delInst(int n){
            if(n < instBtn.length){
                getUsedList().remove(instBtn[n].getUsedId());    //使用中音源のリストから削除
                for(int i = 0; i < barPane.get(number).gridPane.length; i++){
                    for(int j = 0; j < instBtn.length; j++){
                        if(getGrid(i).instBtn[j].getUsedId() > instBtn[n].getUsedId()){
                            getGrid(i).instBtn[j].setUsedId(getGrid(i).instBtn[j].getUsedId() - 1); //以降のリストの繰り上げ
                        }
                    }
                }

                for(int k = n; k + 1 < instBtn.length; k++){
                    String word[] = instBtn[k + 1].getActionCommand().split(sep);
                    String cmd = "";

                    if(!word[0].equals("")){
                        for(int j = 0; j < word.length - 1; j++){
                            cmd += word[j] + sep;
                        }
                        cmd += Integer.parseInt(word[word.length - 1]);
                    }

                    instBtn[k].setActionCommand(cmd);
                    instBtn[k].setIcon(instBtn[k + 1].getIcon());
                }

                int count = 0;
                String[] msg;
                do{
                    msg = instBtn[count++].getActionCommand().split(sep); 
                }
                while(!msg[0].equals("") && count < instBtn.length);
                if(count == instBtn.length){
                    setBlank(instBtn.length - 1);
                }
                size--;
            }
        }
    }



    /*　==================　*/
    /*　内部クラスInstBtn 　*/
    /*　==================　*/

    class InstBtn extends JButton implements MouseListener{

        /*　==================　*/
        /*　メンバ変数　　　　　　*/
        /*　==================　*/

        private JPopupMenu popup;   //音源のポップアップメニュー
        private JMenuItem menu1;    //音量調整用メニュー
        private JMenuItem menu2;    //削除用メニュー
        private ImageIcon icon;     //音源を示す画像
        private String popupCmd;    //命令元を特定する文字列
        private String blackCmd;    //空の命令に対応する文字列
        private int usedId;         //usedListでのインデックス


        /*　==================　*/
        /*　コンストラクタ　　　　*/
        /*　==================　*/

        InstBtn(View ref, int number, String inst, int index, ImageIcon icon){
            super();

            /* メンバ変数を初期化する */
            this.icon = resizeIcon(icon, 35, 35);   //画像を与える
            popupCmd = number + sep + inst + sep + index;   //命令元を特定する文字列
            blackCmd = Integer.toString(number);

            /*　ポップアップメニューを初期化する　*/
            popup = new JPopupMenu();                       //ポップアップメニューを作る
            menu1 = new JMenuItem("音量");                  //音量調整用メニュー
            menu1.setActionCommand("volume" + sep + popupCmd);   //メニューに命令を与える
            menu1.addActionListener(ref);                   //メニュー動作の送信先を決める
            menu2 = new JMenuItem("削除");                  //削除用メニュー
            menu2.setActionCommand("delete" + sep + popupCmd);   //メニューに命令を与える
            menu2.addActionListener(ref);                   //メニュー動作の送信先を決める
            popup.add(menu1);                               //ポップアップメニューを追加する
            popup.add(menu2);                               //ポップアップメニューを追加する

            /* ボタンを初期化する */
            this.setContentAreaFilled(false); //背景を描画しない
            this.setFocusPainted(false);      //クリック時の文字列周りの枠を描画しない
            this.setBorderPainted(false);     //枠を描画しない
            this.addMouseListener(this);      //ボタン動作の送信先を決める
        }



        /*　==================　*/
        /*　メソッド　　　　　　　*/
        /*　==================　*/

        /*　音源を追加する　*/
        public void setInst(){
            setUsedId(getUsedList().size());
            this.setActionCommand("ctrlinst" + sep + popupCmd);  //ボタンに命令を与える
            this.setIcon(icon);                             //ボタンに画像を与える
        }

        /*　空白にする　*/
        public void setBlank(){
            this.setActionCommand("bar" + sep + blackCmd);   //ボタンの命令を空にする
            this.setIcon(null);                         //ボタンの画像を空にする
        }

        /*　使用中音源の一覧でのインデックスを与える　*/
        public void setUsedId(int n){
            usedId = n;
        }

        /*　使用中音源の一覧でのインデックスを取得する　*/
        public int getUsedId(){
            return usedId;
        }

        /*　ポップアップメニューを表示する　*/
        private void showPopup(MouseEvent e){
            popup.show(e.getComponent(), e.getX(), e.getY());
        }

        /*　マウスでクリック時の動作　*/
        public void mouseClicked(MouseEvent e) {
            selCmd = popupCmd;
        }

        /*　マウスでクリック中の動作　*/
        public void mousePressed(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON3){
                if(e.isPopupTrigger()){
                    ctrl.processEvent("bar" + sep + blackCmd);
                    selCmd = popupCmd;
                    showPopup(e);
                }
            }
        }

        /*　マウスでクリックを止めた時の動作　*/
        public void mouseReleased(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON3){
                if(e.isPopupTrigger()){
                    ctrl.processEvent("bar" + sep + blackCmd);
                    selCmd = popupCmd;
                    showPopup(e);
                }
            }
        }

        /*　マウスで対象からポインタを外した時の動作　*/
        public void mouseExited(MouseEvent e) {

        }

        /*　マウスで対象にポインタを当てた時の動作　*/
        public void mouseEntered(MouseEvent e) {
            
        }
    }



    /*　==================　*/
    /*　内部クラスLoopPane 　*/
    /*　==================　*/

    class LoopPane extends JPanel{

        /*　==================　*/
        /*　メンバ変数　　　　　　*/
        /*　==================　*/

        JLabel label;       //音源名
        JButton button1;    //再生ボタン
        JButton button2;    //停止ボタン
        JButton button3;    //決定ボタン
        String name;        //ファイル名



        /*　==================　*/
        /*　コンストラクタ　　　　*/
        /*　==================　*/

        LoopPane(View ref, String path){
            /*　画像を読み込む　*/
            ImageIcon icon1 = resizeIcon(playCirc, 30, 30);                     //再生ボタンの画像
            ImageIcon icon2 = resizeIcon(pauseCirc, 30, 30);                    //停止ボタンの画像
            ImageIcon icon1rollover = resizeIcon(playCircRollover, 30, 30);     //再生ボタンのロールオーバー時の画像
            ImageIcon icon2rollover = resizeIcon(pauseCircRollover, 30, 30);    //停止ボタンのロールオーバー時の画像

            /*　レイアウトを決定する　*/
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));   //レイアウトを決める
            setBackground(bgc);                                 //背景色を与える
            setMaximumSize(new Dimension(400, 50));           //大きさを決める
            setPreferredSize(new Dimension(400, 50));           //大きさを決める
            
            /*　ラベルを設定する　*/
            String[] word = path.split(sep);
            name = word[word.length - 1];
            label = new JLabel(name);                           //新しいラベルを作る
            label.setForeground(Color.WHITE);                   //文字色を与える
            label.setMaximumSize(new Dimension(200, 40));       //最大サイズを決める
            label.setPreferredSize(new Dimension(200, 40));     //大きさを決める
            label.setAlignmentY(Component.CENTER_ALIGNMENT);    //縦方向に中央揃えにする

            /*　再生ボタンを設定する　*/
            button1 = new JButton(icon1);                       //新しいボタンを作る
            button1.setRolloverIcon(icon1rollover);             //ロールオーバー時の画像を与える
            button1.setMaximumSize(new Dimension(40, 40));      //最大サイズを決める
            button1.setPreferredSize(new Dimension(40, 40));    //大きさを決める
            button1.setAlignmentY(Component.CENTER_ALIGNMENT);  //縦方向に中央揃えにする
            button1.setContentAreaFilled(false);                //背景を描画しない
            button1.setBorderPainted(false);                    //枠線を描画しない
            button1.setFocusPainted(false);                     //クリック時の文字列周りの枠を描画しない
            button1.setActionCommand("testplay" + sep + path);  //ボタンに命令を与える
            button1.addActionListener(ref);                     //ボタン動作の送信先を決める

            /*　停止ボタンを設定する　*/
            button2 = new JButton(icon2);                       //新しいボタンを作る
            button2.setRolloverIcon(icon2rollover);             //ロールオーバー時の画像を与える
            button2.setMaximumSize(new Dimension(40, 40));      //最大サイズを決める
            button2.setPreferredSize(new Dimension(40, 40));    //大きさを決める
            button2.setAlignmentY(Component.CENTER_ALIGNMENT);  //縦方向に中央揃えにする
            button2.setContentAreaFilled(false);                //背景を描画しない
            button2.setBorderPainted(false);                    //枠線を描画しない
            button2.setFocusPainted(false);                     //クリック時の文字列周りの枠を描画しない
            button2.setActionCommand("testpause");              //ボタンに命令を与える
            button2.addActionListener(ref);                     //ボタン動作の送信先を決める

            /*　決定ボタンを設定する　*/
            button3 = new JButton("決定");                      //新しいボタンを作る
            button3.setForeground(Color.WHITE);                 //文字色を決める
            button3.setMaximumSize(new Dimension(95, 40));      //最大サイズを決める
            button3.setPreferredSize(new Dimension(95, 40));    //大きさを決める
            button3.setAlignmentY(Component.CENTER_ALIGNMENT);  //縦方向に中央揃えにする
            button3.setContentAreaFilled(false);                //背景を描画しない
            button3.setFocusPainted(false);                     //クリック時の文字列周りの枠を描画しない
            button3.setActionCommand("plus" + sep + path);      //ボタンに命令を与える
            button3.addActionListener(ref);                     //ボタン動作の送信先を決める

            /*　パネルにコンポーネントを配置する　*/
            add(label);                                         //ラベルを配置する
            add(Box.createRigidArea(new Dimension(10, 0)));    //空白を配置する
            add(button1);                                       //再生ボタンを配置する
            add(Box.createRigidArea(new Dimension(5, 0)));     //空白を配置する
            add(button2);                                       //停止ボタンを配置する
            add(Box.createRigidArea(new Dimension(10, 0)));     //空白を配置する
            add(button3);                                       //決定ボタンを配置する
        }
    }



    /*　==================　*/
    /*　内部クラスSongData 　*/
    /*　==================　*/

    class SongData{

        /*　==================　*/
        /*　メンバ変数　　　　　　*/
        /*　==================　*/

        private String title;
        private String composer;
        private String genre;
        private int bookmarks;
        private String date;
        private boolean editable;



        /*　==================　*/
        /*　コンストラクタ　　　　*/
        /*　==================　*/

        SongData(String title, String composer, String genre, int bookmarks, String date, boolean editable){
            this.title = title;
            this.composer = composer;
            this.genre = genre;
            this.bookmarks = bookmarks;
            this.date = date;
            this.editable = editable;
        }



        /*　==================　*/
        /*　メソッド　　　　　　　*/
        /*　==================　*/

        public JPanel getPane(){
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));

            JLabel titleLbl = new JLabel(title);                //ラベルを作成する
            titleLbl.setForeground(Color.WHITE);                //字の色を白にする
            titleLbl.setAlignmentY(Component.CENTER_ALIGNMENT); //配置を中央揃えにする
            titleLbl.setHorizontalAlignment(JLabel.CENTER);     //文字を中央揃えにする
            titleLbl.setMaximumSize(new Dimension(120, 40));    //大きさの最大値を決める
            titleLbl.setPreferredSize(new Dimension(100, 40));  //大きさを決める

            JLabel composerLbl = new JLabel(composer);              //ラベルを作成する
            composerLbl.setForeground(Color.WHITE);                 //字の色を白にする
            composerLbl.setAlignmentY(Component.CENTER_ALIGNMENT);  //配置を中央揃えにする
            composerLbl.setHorizontalAlignment(JLabel.CENTER);      //文字を中央揃えにする
            composerLbl.setMaximumSize(new Dimension(120, 40));     //大きさの最大値を決める
            composerLbl.setPreferredSize(new Dimension(100, 40));   //大きさを決める

            JLabel genreLbl = new JLabel(genre);                //ラベルを作成する
            genreLbl.setForeground(Color.WHITE);                //字の色を白にする
            genreLbl.setHorizontalAlignment(JLabel.CENTER);     //文字を中央揃えにする
            genreLbl.setMaximumSize(new Dimension(50, 40));     //大きさの最大値を決める
            genreLbl.setPreferredSize(new Dimension(50, 40));   //大きさを決める

            JLabel bookmarksLbl = new JLabel(Integer.toString(bookmarks));  //ラベルを作成する
            bookmarksLbl.setForeground(Color.WHITE);                        //字の色を白にする
            bookmarksLbl.setAlignmentY(Component.CENTER_ALIGNMENT);         //配置を中央揃えにする
            bookmarksLbl.setHorizontalAlignment(JLabel.CENTER);             //文字を中央揃えにする
            bookmarksLbl.setMaximumSize(new Dimension(25, 40));             //大きさの最大値を決める
            bookmarksLbl.setPreferredSize(new Dimension(20, 40));           //大きさを決める

            JLabel dateLbl = new JLabel(date);                  //ラベルを作成する
            dateLbl.setForeground(Color.WHITE);                 //字の色を白にする
            dateLbl.setAlignmentY(Component.CENTER_ALIGNMENT);  //配置を中央揃えにする
            dateLbl.setHorizontalAlignment(JLabel.CENTER);      //文字を中央揃えにする
            dateLbl.setMaximumSize(new Dimension(100, 40));     //大きさの最大値を決める
            dateLbl.setPreferredSize(new Dimension(100, 40));   //大きさを決める

            p.add(titleLbl);
            p.add(Box.createRigidArea(new Dimension(5, 0)));
            p.add(composerLbl);
            p.add(Box.createRigidArea(new Dimension(5, 0)));
            p.add(genreLbl);
            p.add(Box.createRigidArea(new Dimension(5, 0)));
            p.add(bookmarksLbl);
            p.add(Box.createRigidArea(new Dimension(5, 0)));
            p.add(dateLbl);
            p.add(Box.createRigidArea(new Dimension(5, 0)));

            return p;
        }

        public String getTitle(){
            return title;
        }

        public String getComposer(){
            return composer;
        }

        public boolean getEditable(){
            return editable;
        }

        public String toString(){
            return composer + sep + title;
        }
    }



    /*　==================　*/
    /*　内部クラスListenPane 　*/
    /*　==================　*/

    class ListenPane extends JPanel{

        /*　==================　*/
        /*　メンバ変数　　　　　　*/
        /*　==================　*/

        SongData sd;        //曲情報
        JButton button1;    //再生ボタン
        JButton button2;    //いいねボタン
        JButton button3;    //コメントボタン
        boolean bookmark;   //いいねされているか



        /*　==================　*/
        /*　コンストラクタ　　　　*/
        /*　==================　*/

        ListenPane(View ref, String title, String composer, String genre, int bookmarks, String date, boolean editable, boolean bookmark){
            /*　画像を読み込む　*/
            ImageIcon icon1 = resizeIcon(playCirc, 20, 20);                         //再生ボタンの画像
            ImageIcon icon2 = resizeIcon(favorite, 20, 20);                         //いいねボタンの画像
            ImageIcon icon3 = resizeIcon(favoritePressed, 20, 20);                  //いいねボタンの画像（いいね済）
            ImageIcon icon4 = resizeIcon(commentCirc, 20, 20);                      //コメントボタンの画像
            ImageIcon icon1rollover = resizeIcon(playCircRollover, 20, 20);         //再生ボタンのロールオーバー時の画像
            ImageIcon icon2rollover = resizeIcon(favoriteRollover, 20, 20);         //いいねボタンのロールオーバー時の画像
            ImageIcon icon3rollover = resizeIcon(favoritePressedRollover, 20, 20);  //いいねボタンの画像（いいね済、ロールオーバー）
            ImageIcon icon4rollover = resizeIcon(commentCircRollover, 20, 20);      //停止ボタンのロールオーバー時の画像

            /*　レイアウトを決定する　*/
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));   //レイアウトを決める
            setBackground(bgc);                                 //背景色を与える
            setPreferredSize(new Dimension(580, 50));           //大きさを決める

            /*　ラベルを設定する　*/
            sd = new SongData(title, composer, genre, bookmarks, date, editable);
            JPanel p = sd.getPane();
            p.setMaximumSize(new Dimension(440, 40));       //最大サイズを決める
            p.setPreferredSize(new Dimension(440, 40));     //大きさを決める
            p.setAlignmentY(Component.CENTER_ALIGNMENT);    //縦方向に中央揃えにする
            p.setOpaque(false);                             //背景を描画しない

            /*　再生ボタンを設定する　*/
            button1 = new JButton(icon1);                                                                               //新しいボタンを作る
            button1.setRolloverIcon(icon1rollover);                                                                     //ロールオーバー時の画像を与える
            button1.setMaximumSize(new Dimension(40, 40));                                                              //最大サイズを決める
            button1.setPreferredSize(new Dimension(40, 40));                                                            //大きさを決める
            button1.setAlignmentY(Component.CENTER_ALIGNMENT);                                                          //縦方向に中央揃えにする
            button1.setContentAreaFilled(false);                                                                        //背景を描画しない
            button1.setBorderPainted(false);                                                                            //枠線を描画しない
            button1.setFocusPainted(false);                                                                             //クリック時の文字列周りの枠を描画しない
            button1.setActionCommand("listenplay" + sep + sd.getEditable() + sep + sd);  //ボタンに命令を与える
            button1.addActionListener(ref);                                                                             //ボタン動作の送信先を決める

            /*　いいねボタンを設定する　*/
            if(!bookmark){
                button2 = new JButton(icon2);                   //新しいボタンを作る
                button2.setRolloverIcon(icon2rollover);         //ロールオーバー時の画像を与える
            }
            else{
                button2 = new JButton(icon3);                   //新しいボタンを作る
                button2.setRolloverIcon(icon3rollover);         //ロールオーバー時の画像を与える
            }
            button2.setMaximumSize(new Dimension(40, 40));      //最大サイズを決める
            button2.setPreferredSize(new Dimension(40, 40));    //大きさを決める
            button2.setAlignmentY(Component.CENTER_ALIGNMENT);  //縦方向に中央揃えにする
            button2.setContentAreaFilled(false);                //背景を描画しない
            button2.setBorderPainted(false);                    //枠線を描画しない
            button2.setFocusPainted(false);                     //クリック時の文字列周りの枠を描画しない
            button2.setActionCommand("bookmark" + sep + sd);         //ボタンに命令を与える
            button2.addActionListener(ref);                     //ボタン動作の送信先を決める

            /*　コメントボタンを設定する　*/
            button3 = new JButton(icon4);                       //新しいボタンを作る
            button3.setRolloverIcon(icon4rollover);             //ロールオーバー時の画像を与える
            button3.setMaximumSize(new Dimension(40, 40));      //最大サイズを決める
            button3.setPreferredSize(new Dimension(40, 40));    //大きさを決める
            button3.setAlignmentY(Component.CENTER_ALIGNMENT);  //縦方向に中央揃えにする
            button3.setContentAreaFilled(false);                //背景を描画しない
            button3.setBorderPainted(false);                    //枠線を描画しない
            button3.setFocusPainted(false);                     //クリック時の文字列周りの枠を描画しない
            button3.setActionCommand("showcomment" + sep + sd); //ボタンに命令を与える
            button3.addActionListener(ref);                     //ボタン動作の送信先を決める

            /*　パネルにコンポーネントを配置する　*/
            add(p);                                             //パネルを配置する
            add(Box.createRigidArea(new Dimension(10, 0)));     //空白を配置する
            add(button1);                                       //再生ボタンを配置する
            add(Box.createRigidArea(new Dimension(5, 0)));      //空白を配置する
            add(button2);                                       //停止ボタンを配置する
            add(Box.createRigidArea(new Dimension(5, 0)));      //空白を配置する
            add(button3);                                       //決定ボタンを配置する
        }
    }



    /*　==================　*/
    /*　内部クラスCommentPane 　*/
    /*　==================　*/

    class CommentPane extends JPanel{

        /*　==================　*/
        /*　メンバ変数　　　　　　*/
        /*　==================　*/

        String userid;  //コメントしたユーザ名
        String comment; //コメント



        /*　==================　*/
        /*　コンストラクタ　　　　*/
        /*　==================　*/

        CommentPane(String userid, String comment){
            /*　メンバ変数を与える　*/
            this.userid = userid;
            this.comment = comment;

            /*　レイアウトを決定する　*/
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));   //レイアウトを決める
            setBackground(bgc);                                 //背景色を与える
            setMaximumSize(new Dimension(550, 30));             //大きさを決める
            setPreferredSize(new Dimension(550, 30));           //大きさを決める

            /*　ラベルを設定する　*/
            JLabel label1 = new JLabel(userid);                 //新しいラベルを作る
            label1.setForeground(Color.WHITE);                  //文字色を与える
            label1.setAlignmentY(Component.CENTER_ALIGNMENT);   //縦方向に中央揃えにする
            label1.setHorizontalAlignment(JLabel.RIGHT);        //文字を右揃えにする
            label1.setMaximumSize(new Dimension(130, 20));       //最大サイズを決める
            label1.setPreferredSize(new Dimension(130, 20));     //大きさを決める

            /*　ラベルを設定する　*/
            JLabel label2 = new JLabel(":");                    //新しいラベルを作る
            label2.setForeground(Color.WHITE);                  //文字色を与える
            label2.setAlignmentY(Component.CENTER_ALIGNMENT);   //縦方向に中央揃えにする
            label2.setHorizontalAlignment(JLabel.CENTER);       //文字を右揃えにする
            label2.setMaximumSize(new Dimension(20, 20));       //最大サイズを決める
            label2.setPreferredSize(new Dimension(20, 20));     //大きさを決める

            /*　ラベルを設定する　*/
            JLabel label3 = new JLabel(comment);                //新しいラベルを作る
            label3.setForeground(Color.WHITE);                  //文字色を与える
            label3.setAlignmentY(Component.CENTER_ALIGNMENT);   //縦方向に中央揃えにする
            label3.setHorizontalAlignment(JLabel.LEFT);         //文字を左揃えにする
            label3.setMaximumSize(new Dimension(400, 20));      //最大サイズを決める
            label3.setPreferredSize(new Dimension(400, 20));    //大きさを決める
            

            /*　パネルにコンポーネントを配置する　*/
            add(label1);                                    //ラベルを配置する
            add(label2);                                    //ラベルを配置する
            add(label3);                                    //ラベルを配置する
        }
    }



    /*　==================　*/
    /*　メソッド　　　　　　　*/
    /*　==================　*/

    /*　スタート画面を表示する　*/
    public void drawStartDisplay(){
        /*　画面のコンテナを取得する　*/
        Container contentPane = frame.getContentPane(); //メイン画面のコンテナを取得

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, frame.getWidth(), frame.getHeight());    //背景
        ImageIcon icon2 = resizeIcon(logo, 480, 200);   //ロゴ
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える
        
        /*　タイトルロゴを作成する　*/
	    JLabel label1 = new JLabel(icon2);                      //タイトルロゴを作成する
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        
        /*　compositionボタンを作成する　*/
        JButton button1 = new JButton("composition");           //ボタンを作成する
	    button1.setFont(new Font(Font.SERIF, Font.ITALIC, 25)); //フォントを設定する
        button1.setForeground(Color.WHITE);                     //字の色を白にする
	    button1.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button1.setMaximumSize(new Dimension(200, 60));         //大きさの最大値を決める
        button1.setPreferredSize(new Dimension(200, 60));       //大きさを決める
        button1.setContentAreaFilled(false);                    //背景を描画しない
        button1.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button1.setActionCommand("composition");                //ボタンに命令を与える
	    button1.addActionListener(this);                        //ボタン動作の送信先を決める
	    
        /*　listenボタンを作成する　*/
        JButton button2 = new JButton("listen");                //ボタンを作成する
	    button2.setFont(new Font(Font.SERIF, Font.ITALIC, 25)); //フォントを設定する
        button2.setForeground(Color.WHITE);                     //字の色を白にする
	    button2.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button2.setMaximumSize(new Dimension(200, 60));         //大きさの最大値を決める
        button2.setPreferredSize(new Dimension(200, 60));       //大きさを決める
        button2.setContentAreaFilled(false);                    //背景を描画しない
        button2.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button2.setActionCommand("listen");                     //ボタンに命令を与える
	    button2.addActionListener(this);                        //ボタン動作の送信先を決める
	    
        /*　loginボタンを作成する　*/
        JButton button3 = new JButton("login");                 //ボタンを作成する
	    button3.setFont(new Font(Font.SERIF, Font.ITALIC, 25)); //フォントを設定する
        button3.setForeground(Color.WHITE);                     //字の色を白にする
	    button3.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button3.setMaximumSize(new Dimension(200, 60));         //大きさの最大値を決める
        button3.setPreferredSize(new Dimension(200, 60));       //大きさを決める
        button3.setContentAreaFilled(false);                    //背景を描画しない
        button3.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button3.setActionCommand("selectlogin");                //ボタンに命令を与える
	    button3.addActionListener(this);                        //ボタン動作の送信先を決める

        /*　updateボタンを作成する　*/
        JButton button4 = new JButton("update");                //ボタンを作成する
	    button4.setFont(new Font(Font.SERIF, Font.ITALIC, 25)); //フォントを設定する
        button4.setForeground(Color.WHITE);                     //字の色を白にする
	    button4.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button4.setMaximumSize(new Dimension(200, 60));         //大きさの最大値を決める
        button4.setPreferredSize(new Dimension(200, 60));       //大きさを決める
        button4.setContentAreaFilled(false);                    //背景を描画しない
        button4.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button4.setActionCommand("update");                     //ボタンに命令を与える
	    button4.addActionListener(this);                        //ボタン動作の送信先を決める
	    
        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 80)));     //空白
	    backlbl.add(label1);                                        //ロゴ
        backlbl.add(Box.createRigidArea(new Dimension(0, 30)));     //空白
	    backlbl.add(button1);                                       //compositionボタン
        backlbl.add(Box.createRigidArea(new Dimension(0, 10)));     //空白
	    backlbl.add(button2);                                       //listenボタン
        backlbl.add(Box.createRigidArea(new Dimension(0, 10)));     //空白
	    backlbl.add(button3);                                       //loginボタン
        backlbl.add(Box.createRigidArea(new Dimension(0, 10)));     //空白
	    backlbl.add(button4);                                       //loginボタン

        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		frame.revalidate();         //画面を更新する
    }

    /*　プロジェクト作成画面を表示する　*/
    public void drawProjectDisplay() {
        /*　画面のコンテナを取得する　*/
        Container contentPane = frame.getContentPane(); //メイン画面のコンテナを取得

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, frame.getWidth(), frame.getHeight());        //背景
        ImageIcon icon2 = resizeIcon(back, 50, 50);         //背景
        ImageIcon icon3 = resizeIcon(backRollover, 50, 50); //背景
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　新規作成ボタンを作成する　*/
        JButton button1 = new JButton("新規作成");               //ボタンを作成する
        button1.setFont(new Font(Font.SERIF, Font.BOLD, 25));   //フォントを設定する
        button1.setForeground(Color.WHITE);                     //字の色を白にする
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button1.setMaximumSize(new Dimension(500, 80));         //大きさの最大値を決める
        button1.setPreferredSize(new Dimension(500, 80));       //大きさを決める
        button1.setContentAreaFilled(false);                    //背景を描画しない
        button1.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button1.setActionCommand("new");                        //ボタンに命令を与える
        button1.addActionListener(this);                        //ボタン動作の送信先を決める

        /*　開くボタンを作成する　*/
        JButton button2 = new JButton("既存のプロジェクトを開く");    //ボタンを作成する
        button2.setFont(new Font(Font.SERIF, Font.BOLD, 25));       //フォントを設定する
        button2.setForeground(Color.WHITE);                         //字の色を白にする
        button2.setAlignmentX(Component.CENTER_ALIGNMENT);          //配置を中央揃えにする
        button2.setMaximumSize(new Dimension(500, 80));             //大きさの最大値を決める
        button2.setPreferredSize(new Dimension(500, 80));           //大きさを決める
        button2.setContentAreaFilled(false);                        //背景を描画しない
        button2.setFocusPainted(false);                             //クリック時の枠線を描画しない
        button2.setActionCommand("open");                           //ボタンに命令を与える
        button2.addActionListener(this);                            //ボタン動作の送信先を決める

        /*　トップパネルを作成する　*/
        JPanel tp = new JPanel();                           //トップパネルを作成する
        tp.setLayout(new BoxLayout(tp, BoxLayout.X_AXIS));  //レイアウトを与える
        tp.setOpaque(false);                                //背景を描画しない

        /*　戻るボタンを作成する　*/
        JButton button3 = new JButton(icon2);               //ボタンを作成する
        button3.setRolloverIcon(icon3);                     //ロールオーバー時の画像を与える
        button3.setAlignmentX(Component.CENTER_ALIGNMENT);  //配置を中央揃えにする
        button3.setMaximumSize(new Dimension(50, 50));      //大きさの最大値を決める
        button3.setPreferredSize(new Dimension(50, 50));    //大きさを決める
        button3.setContentAreaFilled(false);                //背景を描画しない
        button3.setFocusPainted(false);                     //クリック時の枠線を描画しない
        button3.setBorderPainted(false);                    //枠線を描画しない
        button3.setActionCommand("backproject");            //ボタンに命令を与える
        button3.addActionListener(this);                    //ボタン動作の送信先を決める

        /*　コンポーネントを配置する　*/
        tp.add(Box.createRigidArea(new Dimension(600, 0)));         //空白
        tp.add(button3);                                            //戻るボタン
        backlbl.add(Box.createRigidArea(new Dimension(0, 150)));    //空白
        backlbl.add(tp);                                            //トップパネル
        backlbl.add(Box.createRigidArea(new Dimension(0, 50)));     //空白
        backlbl.add(button1);                                       //新規作成ボタン
        backlbl.add(Box.createRigidArea(new Dimension(0, 50)));     //空白
        backlbl.add(button2);                                       //開くボタン

        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);     //要素を配置する
		frame.revalidate();         //画面を更新する
    }

    /*　曲名を入力する画面を描画　*/
    public void drawNameMscDisplay(){
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane(); //メイン画面のコンテナを取得

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, subFrame.getWidth(), subFrame.getHeight());    //背景
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　曲名の入力欄を作成する　*/
        titleField = new JTextField("");                             //テキストフィールドを作成する
        titleField.setFont(new Font(Font.DIALOG, Font.BOLD, 30));   //フォントを設定する
        titleField.setAlignmentX(Component.CENTER_ALIGNMENT);        //配置を中央揃えにする
        titleField.setHorizontalAlignment(JTextField.CENTER);        //文字を中央揃えにする
        titleField.setMaximumSize(new Dimension(400, 60));           //大きさの最大値を決める
        titleField.setPreferredSize(new Dimension(400, 60));         //大きさを決める
        
	    /*　新規作成ボタンを作成する　*/
	    JButton button = new JButton("新規作成");                //ボタンを作成する
	    button.setFont(new Font(Font.SERIF, Font.BOLD, 25));   //フォントを設定する
        button.setForeground(Color.WHITE);                      //字の色を白にする
        button.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        button.setMaximumSize(new Dimension(200, 50));          //大きさの最大値を決める
        button.setPreferredSize(new Dimension(200, 50));        //大きさを決める
        button.setContentAreaFilled(false);                     //背景を描画しない
        button.setFocusPainted(false);                          //クリック時の枠線を描画しない
        button.setActionCommand("make");                        //ボタンに命令を与える
	    button.addActionListener(this);                         //ボタン動作の送信先を決める
        
        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(100, 100)));  //空白
        backlbl.add(titleField);                                         //曲名の入力欄
        backlbl.add(Box.createRigidArea(new Dimension(100, 50)));   //空白
        backlbl.add(button);                                        //新規作成ボタン

        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		subFrame.revalidate();      //サブ画面を更新する
        subFrame.setVisible(true);  //サブ画面を表示する
    }

    /*　作曲画面を描画　*/
    public void drawComposeDisplay() {
        final int INSTSIZE = 5;  //楽器の種類

        /*　画面のコンテナを取得する　*/
        Container contentPane = frame.getContentPane(); //メイン画面のコンテナを取得

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 1280, 720);            //背景
        ImageIcon icon2 = resizeIcon(play, 40, 40);             //再生ボタン
        ImageIcon icon3 = resizeIcon(playRollover, 40, 40);     //再生ボタン（ロールオーバー）
        ImageIcon icon4 = resizeIcon(pause, 40, 40);            //停止ボタン
        ImageIcon icon5 = resizeIcon(pauseRollover, 40, 40);    //停止ボタン（ロールオーバー）
        ImageIcon icon6 = resizeIcon(select, 100, 100);         //選択ボタン
        ImageIcon icon7 = resizeIcon(save, 100, 100);           //保存ボタン
        ImageIcon icon8 = resizeIcon(upload, 100, 100);         //アップロードボタン
        ImageIcon icon9 = resizeIcon(login, 50, 50);            //ログインボタン
        ImageIcon icon10 = resizeIcon(loginRollover, 50, 50);   //ログインボタン（ロールオーバー）
        ImageIcon icon11 = resizeIcon(back, 50, 50);            //戻るボタン
        ImageIcon icon12 = resizeIcon(backRollover, 50, 50);    //戻るボタン（ロールオーバー）

        /*　枠線を作成する　*/
        LineBorder border = new LineBorder(bgc, 2, true);   //枠線
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　作業スペースを作成する　*/
        JPanel panel1 = new JPanel();                       //作業パネルを作成する
        panel1.setAlignmentY(Component.TOP_ALIGNMENT);      //配置を上揃えにする
        panel1.setMaximumSize(new Dimension(1100, 570));    //大きさの最大値を決める
        panel1.setPreferredSize(new Dimension(1100, 570));  //大きさを決める

        /*　楽器パネルを作成する　*/
        JPanel panel2 = new JPanel();                               //楽器パネルを作成する
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));  //レイアウトを与える
        panel2.setOpaque(false);                                    //背景を描画しない

        /*　楽器名のラベルを作成する　*/
        JLabel[] labels = new JLabel[INSTSIZE];                             //ラベルの配列を作成する
        String[] labelTexts = {"Piano", "Guitar", "Bass", "Drum", "Synth"}; //楽器名を作成する
        panel2.add(Box.createRigidArea(new Dimension(0, 65)));              //空白を追加する
        for(int i = 0; i < INSTSIZE; i++){
            labels[i] = new JLabel(labelTexts[i]);                      //ラベルを作成する
            labels[i].setFont(new Font(Font.SERIF, Font.ITALIC, 15));     //フォントを設定する
            labels[i].setBorder(border);                                //枠線を与える
            labels[i].setBackground(bgc);                               //背景色を決める
            labels[i].setAlignmentX(Component.CENTER_ALIGNMENT);        //配置を中央揃えにする
            labels[i].setHorizontalAlignment(JLabel.CENTER);            //文字を中央揃えにする
            labels[i].setMaximumSize(new Dimension(100, 80));           //大きさの最大値を決める
            labels[i].setPreferredSize(new Dimension(100, 80));         //大きさを決める
            panel2.add(Box.createRigidArea(new Dimension(0, 10)));      //空白を追加する
            panel2.add(labels[i]);                                      //ラベルを追加する
        }

        /*　配置パネルを作成する　*/
        workPane = new JPanel();                                //配置パネルを作成する
        JScrollPane scrollPane = new JScrollPane(workPane);     //スクロールを設定する
        scrollPane.setPreferredSize(new Dimension(900, 560));   //大きさを決める

        /*　操作パネルを作成する　*/
        JPanel panel3 = new JPanel();                               //操作パネルを作成する
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));  //レイアウトを与える
        panel3.setPreferredSize(new Dimension(1000, 80));           //大きさを決める
        panel3.setOpaque(false);

        /*　メディアパネルを作成する　*/
        JPanel panel4 = new JPanel();                               //メディアパネルを作成する
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));  //レイアウトを与える
        panel4.setOpaque(false);                                    //背景を描画しない

        /*　下部パネルを作成する　*/
        JPanel panel5 = new JPanel();                               //下部パネルを作成する
        panel5.setLayout(new BoxLayout(panel5, BoxLayout.X_AXIS));  //レイアウトを与える
        panel5.setOpaque(false);                                    //背景を描画しない

        /*　中央パネルを作成する　*/
        JPanel panel6 = new JPanel();                               //中央パネルを作成する
        panel6.setLayout(new BoxLayout(panel6, BoxLayout.X_AXIS));  //レイアウトを与える
        panel6.setOpaque(false);                                    //背景を描画しない
        
        /*　再生ボタンを作成する　*/
        JButton button1 = new JButton(icon2);               //ボタンを作成する
        button1.setRolloverIcon(icon3);                     //ロールオーバー時の画像を与える
        button1.setAlignmentY(Component.CENTER_ALIGNMENT);  //配置を中央揃えにする
        button1.setMaximumSize(new Dimension(80, 80));      //大きさの最大値を決める
        button1.setPreferredSize(new Dimension(80, 80));    //大きさを決める
        button1.setBorderPainted(false);                    //枠線を描画しない
        button1.setContentAreaFilled(false);                //背景を描画しない
        button1.setFocusPainted(false);                     //クリック時の枠線を描画しない
        button1.setActionCommand("play");                   //ボタンに命令を与える
        button1.addActionListener(this);                    //ボタン動作の送信先を決める

        /*　停止ボタンを作成する　*/
        JButton button2 = new JButton(icon4);               //ボタンを作成する
        button2.setRolloverIcon(icon5);                     //ロールオーバー時の画像を与える
        button2.setAlignmentY(Component.CENTER_ALIGNMENT);  //配置を中央揃えにする
        button2.setMaximumSize(new Dimension(80, 80));      //大きさの最大値を決める
        button2.setPreferredSize(new Dimension(80, 80));    //大きさを決める
        button2.setBorderPainted(false);                    //枠線を描画しない
        button2.setContentAreaFilled(false);                //背景を描画しない
        button2.setFocusPainted(false);                     //クリック時の枠線を描画しない
        button2.setActionCommand("pause");                  //ボタンに命令を与える
        button2.addActionListener(this);                    //ボタン動作の送信先を決める

        /*　選択ボタンを作成する　*/
        JButton button3 = new JButton(icon6);                   //ボタンを作成する
        button3.setText("楽器を選ぶ");                           //文字列を与える
        button3.setFont(new Font(Font.SERIF, Font.BOLD, 15));   //フォントを設定する
        button3.setForeground(Color.WHITE);                     //字の色を白にする
        button3.setAlignmentY(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button3.setHorizontalTextPosition(JButton.CENTER);      //文字を中央揃えにする
        button3.setMaximumSize(new Dimension(300, 80));         //大きさの最大値を決める
        button3.setPreferredSize(new Dimension(300, 80));       //大きさを決める
        button3.setContentAreaFilled(false);                    //背景を描画しない
        button3.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button3.setActionCommand("select");                     //ボタンに命令を与える
        button3.addActionListener(this);                        //ボタン動作の送信先を決める

        /*　保存ボタンを作成する　*/
        JButton button4 = new JButton(icon7);                   //ボタンを作成する
        button4.setText("曲を保存");                             //文字列を与える
        button4.setFont(new Font(Font.SERIF, Font.BOLD, 15));   //フォントを設定する
        button4.setForeground(Color.WHITE);                     //字の色を白にする
        button4.setAlignmentY(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button4.setHorizontalTextPosition(JButton.CENTER);      //文字を中央揃えにする
        button4.setMaximumSize(new Dimension(300, 80));         //大きさの最大値を決める
        button4.setPreferredSize(new Dimension(300, 80));       //大きさを決める
        button4.setContentAreaFilled(false);                    //背景を描画しない
        button4.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button4.setActionCommand("save");                       //ボタンに命令を与える
        button4.addActionListener(this);                        //ボタン動作の送信先を決める

        /*　アップロードボタンを作成する　*/
        JButton button5 = new JButton(icon8);                   //ボタンを作成する
        button5.setText("曲をアップロード");                     //文字列を与える
        button5.setFont(new Font(Font.SERIF, Font.BOLD, 15));   //フォントを設定する
        button5.setForeground(Color.WHITE);                     //字の色を白にする
        button5.setAlignmentY(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button5.setHorizontalTextPosition(JButton.CENTER);      //文字を中央揃えにする
        button5.setMaximumSize(new Dimension(300, 80));         //大きさの最大値を決める
        button5.setPreferredSize(new Dimension(300, 80));       //大きさを決める
        button5.setContentAreaFilled(false);                    //背景を描画しない
        button5.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button5.setActionCommand("upload");                     //ボタンに命令を与える
        button5.addActionListener(this);                        //ボタン動作の送信先を決める

        /*　ログインボタンを作成する　*/
        JButton button6 = new JButton(icon9);               //ボタンを作成する
        button6.setRolloverIcon(icon10);                    //ロールオーバー時の画像を与える
        button6.setAlignmentY(Component.TOP_ALIGNMENT);     //配置を上揃えにする
        button6.setMaximumSize(new Dimension(50, 50));      //大きさの最大値を決める
        button6.setPreferredSize(new Dimension(50, 50));    //大きさを決める
        button6.setBorderPainted(false);                    //枠線を描画しない
        button6.setContentAreaFilled(false);                //背景を描画しない
        button6.setFocusPainted(false);                     //クリック時の枠線を描画しない
        button6.setActionCommand("selectlogin");            //ボタンに命令を与える
        button6.addActionListener(this);                    //ボタン動作の送信先を決める

        /*　戻るボタンを作成する　*/
        JButton button7 = new JButton(icon11);              //ボタンを作成する
        button7.setRolloverIcon(icon12);                    //ロールオーバー時の画像を与える
        button7.setAlignmentY(Component.TOP_ALIGNMENT);     //配置を上揃えにする
        button7.setMaximumSize(new Dimension(50, 50));      //大きさの最大値を決める
        button7.setBorderPainted(false);                    //枠線を描画しない
        button7.setContentAreaFilled(false);                //背景を描画しない
        button7.setFocusPainted(false);                     //クリック時の枠線を描画しない
        button7.setActionCommand("backcompose");            //ボタンに命令を与える
        button7.addActionListener(this);                    //ボタン動作の送信先を決める

        /*　小節追加ボタンを作成する　*/
        plusBtn = new JButton(plus);            //ボタンを作成する
        plusBtn.setBorderPainted(false);        //枠線を描画しない
        plusBtn.setContentAreaFilled(false);    //背景を描画しない
        plusBtn.setFocusPainted(false);         //クリック時の枠線を描画しない
        plusBtn.setActionCommand("addbar");     //ボタンに命令を与える
        plusBtn.addActionListener(this);        //ボタン動作の送信先を決める

        /*　配置パネルにコンポーネントを配置する　*/
        for(int i = 0; i < barPane.size(); i++){
            workPane.add(barPane.get(i));   //小節パネル
        }
        workPane.add(plusBtn);              //小節追加ボタン

        /*　作業パネルにコンポーネントを配置する　*/
        panel1.add(panel2);                                     //楽器パネル
        panel1.add(scrollPane);                                 //スクロールパネル

        /*　操作パネルにコンポーネントを配置する　*/
        panel3.add(button3);                                    //選択ボタン
        panel3.add(Box.createRigidArea(new Dimension(10,0)));   //空白
        panel3.add(button4);                                    //保存ボタン
        panel3.add(Box.createRigidArea(new Dimension(10,0)));   //空白
        panel3.add(button5);                                    //アップロードボタン

        /*　メディアパネルにコンポーネントを配置する　*/
        panel4.add(button1);                                    //再生ボタン
        panel4.add(Box.createRigidArea(new Dimension(10,0)));   //空白
        panel4.add(button2);                                    //停止ボタン

        /*　下部パネルにコンポーネントを配置する　*/
        panel5.add(panel3);                                     //操作パネル
        panel5.add(Box.createRigidArea(new Dimension(10,0)));   //空白
        panel5.add(panel4);                                     //メディアパネル

        /*　中央パネルにコンポーネントを配置する　*/
        panel6.add(panel1);                                     //作業パネル
        panel6.add(Box.createRigidArea(new Dimension(20,0)));   //空白
        panel6.add(button6);                                    //ログインボタン
        panel6.add(Box.createRigidArea(new Dimension(10,0)));   //空白
        panel6.add(button7);                                    //戻るボタン

        /*　背景にコンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0,10)));  //空白
        backlbl.add(panel6);                                    //作業パネル
        backlbl.add(Box.createRigidArea(new Dimension(0,10)));  //空白
        backlbl.add(panel5);                                    //下部パネル
        
        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		frame.revalidate();         //画面を更新する
    }

    /*　視聴画面を描画　*/
    public void drawListenDisplay() {
        /*　画面のコンテナを取得する　*/
        Container contentPane = frame.getContentPane(); //メイン画面のコンテナを取得

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 1280, 720);            //背景
        ImageIcon icon2 = resizeIcon(play, 40, 40);             //再生ボタン
        ImageIcon icon3 = resizeIcon(playRollover, 40, 40);     //再生ボタン（ロールオーバー）
        ImageIcon icon4 = resizeIcon(pause, 40, 40);            //停止ボタン
        ImageIcon icon5 = resizeIcon(pauseRollover, 40, 40);    //停止ボタン（ロールオーバー）
        ImageIcon icon6 = resizeIcon(back, 50, 50);            //戻るボタン
        ImageIcon icon7 = resizeIcon(backRollover, 50, 50);    //戻るボタン（ロールオーバー）
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　上部パネルを作成する　*/
        JPanel panel1 = new JPanel();                               //上部パネルを作成する
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));  //レイアウトを与える
        panel1.setAlignmentX(Component.CENTER_ALIGNMENT);           //配置を中央揃えにする
        panel1.setMaximumSize(new Dimension(1200, 100));            //大きさの最大値を決める
        panel1.setPreferredSize(new Dimension(1200, 100));          //大きさの最大値
        panel1.setOpaque(false);                                    //背景を描画しない

        /*　メディアパネルを作成する　*/
        JPanel panel2 = new JPanel();                               //メディアパネルを作成する
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));  //レイアウトを与える
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);           //配置を中央揃えにする
        panel2.setMaximumSize(new Dimension(500, 100));             //大きさの最大値を決める
        panel2.setPreferredSize(new Dimension(500, 100));           //大きさの最大値
        panel2.setOpaque(false);                                    //背景を描画しない
        
        /*　再生ボタンを作成する　*/
        JButton button1 = new JButton(icon2);               //ボタンを作成する
        button1.setRolloverIcon(icon3);                     //ロールオーバー時の画像を与える
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);  //配置を中央揃えにする
        button1.setAlignmentY(Component.CENTER_ALIGNMENT);  //配置を中央揃えにする
        button1.setMaximumSize(new Dimension(80, 80));      //大きさの最大値を決める
        button1.setPreferredSize(new Dimension(80, 80));    //大きさを決める
        button1.setBorderPainted(false);                    //枠線を描画しない
        button1.setContentAreaFilled(false);                //背景を描画しない
        button1.setFocusPainted(false);                     //クリック時の枠線を描画しない
        button1.setActionCommand("play");                   //ボタンに命令を与える
        button1.addActionListener(this);                    //ボタン動作の送信先を決める

        /*　停止ボタンを作成する　*/
        JButton button2 = new JButton(icon4);               //ボタンを作成する
        button2.setRolloverIcon(icon5);                     //ロールオーバー時の画像を与える
        button2.setAlignmentX(Component.CENTER_ALIGNMENT);  //配置を中央揃えにする
        button2.setAlignmentY(Component.CENTER_ALIGNMENT);  //配置を中央揃えにする
        button2.setMaximumSize(new Dimension(80, 80));      //大きさの最大値を決める
        button2.setPreferredSize(new Dimension(80, 80));    //大きさを決める
        button2.setBorderPainted(false);                    //枠線を描画しない
        button2.setContentAreaFilled(false);                //背景を描画しない
        button2.setFocusPainted(false);                     //クリック時の枠線を描画しない
        button2.setActionCommand("pause");                  //ボタンに命令を与える
        button2.addActionListener(this);                    //ボタン動作の送信先を決める

        /*　戻るボタンを作成する　*/
        JButton button3 = new JButton(icon6);               //ボタンを作成する
        button3.setRolloverIcon(icon7);                     //ロールオーバー時の画像を与える
        button3.setAlignmentY(Component.CENTER_ALIGNMENT);  //配置を上揃えにする
        button3.setMaximumSize(new Dimension(50, 50));      //大きさの最大値を決める
        button3.setBorderPainted(false);                    //枠線を描画しない
        button3.setContentAreaFilled(false);                //背景を描画しない
        button3.setFocusPainted(false);                     //クリック時の枠線を描画しない
        button3.setActionCommand("backlisten");             //ボタンに命令を与える
        button3.addActionListener(this);                    //ボタン動作の送信先を決める

        /*　上部パネルにコンポーネントを配置する　*/
        panel1.add(Box.createRigidArea(new Dimension(1070,0))); //空白
        panel1.add(button3);                                   //戻るボタン

        /*　メディアパネルにコンポーネントを配置する　*/
        panel2.add(Box.createRigidArea(new Dimension(200,0)));  //空白
        panel2.add(button1);                                    //再生ボタン
        panel2.add(Box.createRigidArea(new Dimension(10,0)));   //空白
        panel2.add(button2);                                    //停止ボタン
        panel2.add(Box.createRigidArea(new Dimension(200,0)));  //空白
        

        /*　背景にコンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0,20)));  //空白
        backlbl.add(panel1);                                    //上部パネル
        backlbl.add(Box.createRigidArea(new Dimension(0,150)));  //空白
        backlbl.add(panel2);                                    //メディアパネル
        
        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		frame.revalidate();         //画面を更新する
    }

    /*　音量調整画面を描画　*/
    public void drawVolumeDisplay(){
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane();  //サブ画面のコンテナを取得する
        contentPane.setName("音量を変更");                   //サブ画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 640, 400);    //背景
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　音量スライダーを作成する　*/
        volumeSlider = new JSlider(0, 255, getVolume());        //スライダーを作成する
        volumeSlider.setForeground(Color.WHITE);                //字の色を白にする
        volumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT); //配置を中央揃えにする
        volumeSlider.setMaximumSize(new Dimension(400, 80));    //大きさの最大値を決める
        volumeSlider.setPreferredSize(new Dimension(400, 80));  //大きさを決める
        volumeSlider.setMajorTickSpacing(50);                   //大目盛の間隔を決める
        volumeSlider.setMinorTickSpacing(5);                    //小目盛の間隔を決める
        volumeSlider.setOpaque(false);                          //背景を描画しない
        volumeSlider.setPaintTicks(true);                       //目盛を描画する
        volumeSlider.setPaintLabels(true);                      //目盛の数値を描画する
        volumeSlider.addChangeListener(this);                   //スライダー動作の送信先を決める
        
        /*　音量ラベルを作成する　*/
        volumeLabel = new JLabel(Integer.toString(getVolume()));    //ラベルを作成する
        volumeLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 20));  //フォントを設定する
        volumeLabel.setForeground(Color.WHITE);                     //字の色を白にする
        volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        volumeLabel.setHorizontalAlignment(JLabel.CENTER);          //文字を中央揃えにする
        volumeLabel.setMaximumSize(new Dimension(80, 80));          //大きさの最大値を決める
        volumeLabel.setPreferredSize(new Dimension(80, 80));        //大きさを決める

        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 70)));    //空白
        backlbl.add(volumeSlider);                                  //音量スライダー
        backlbl.add(Box.createRigidArea(new Dimension(0, 20)));     //空白
        backlbl.add(volumeLabel);                                   //音量ラベル

        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		subFrame.revalidate();      //画面を更新する
        subFrame.setVisible(true);  //サブ画面を表示する
    }

    /*　楽器選択画面を描画　*/
    public void drawSelectInstDisplay(){
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane();  //サブ画面のコンテナを取得する
        contentPane.setName("楽器を選択");                   //サブ画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 640, 400);         //背景
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　上段パネルを作成する　*/
        JPanel panel1 = new JPanel();                               //パネルを作成する
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));  //レイアウトを与える
        panel1.setOpaque(false);                                    //背景を描画しない

        /*　下段パネルを作成する　*/
        JPanel panel2 = new JPanel();                               //パネルを作成する
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));  //レイアウトを与える
        panel2.setOpaque(false);                                    //背景を描画しない
        
        /*　楽器ボタンを作成する　*/
        String[] insts = {"piano", "guitar", "bass", "drum", "synth"};  //楽器名
        JButton[] buttons = new JButton[5];                             //ボタンの配列を作成する
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(insts[i]);                     //ボタンを作成する
            buttons[i].setForeground(Color.WHITE);                  //字の色を白にする
            buttons[i].setMaximumSize(new Dimension(100, 100));     //大きさの最大値を決める
            buttons[i].setPreferredSize(new Dimension(100, 100));   //大きさを決める
            buttons[i].setContentAreaFilled(false);                 //背景を描画しない
            buttons[i].setFocusPainted(false);                      //クリック時の枠線を描画しない
            buttons[i].setActionCommand(insts[i]);                  //ボタンに命令を与える
            buttons[i].addActionListener(this);                     //ボタン動作の送信先を決める
        }

        /*　上段パネルにコンポーネントを配置する　*/
        panel1.add(buttons[0]);                                 //ピアノボタン
        panel1.add(Box.createRigidArea(new Dimension(20, 0)));  //空白
        panel1.add(buttons[1]);                                 //ギターボタン
        panel1.add(Box.createRigidArea(new Dimension(20, 0)));  //空白
        panel1.add(buttons[2]);                                 //ベースボタン

        /*　下段パネルにコンポーネントを配置する　*/
        panel2.add(buttons[3]);                                 //ドラムボタン
        panel2.add(Box.createRigidArea(new Dimension(20, 0)));  //空白
        panel2.add(buttons[4]);                                 //シンセボタン

        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 70))); //空白
        backlbl.add(panel1);                                    //上段パネル
        backlbl.add(Box.createRigidArea(new Dimension(0, 20))); //空白
        backlbl.add(panel2);                                    //下段パネル
        
        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		subFrame.revalidate();      //画面を更新する
        subFrame.setVisible(true);  //サブ画面を表示する
    }

    /*　ジャンル選択画面を描画　*/
    public void drawSelectGenreDisplay() {
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane();  //サブ画面のコンテナを取得する
        contentPane.setName("ジャンルを選択");               //サブ画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 640, 400);         //背景
        ImageIcon icon2 = resizeIcon(back, 40, 40);         //戻るボタン
        ImageIcon icon3 = resizeIcon(backRollover, 40, 40); //戻るボタンのロールオーバー時の画像
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　上部パネルを作成する　*/
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(460, 40));
        panel.setPreferredSize(new Dimension(460, 40));
        panel.setOpaque(false);

        /*　戻るボタンを作成する　*/
        JButton button1 = new JButton(icon2);                //ボタンを作成する
        button1.setRolloverIcon(icon3);                      //ロールオーバー時の画像を与える
        button1.setAlignmentY(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        button1.setMaximumSize(new Dimension(40, 40));       //大きさの最大値を決める
        button1.setPreferredSize(new Dimension(40, 40));     //大きさを決める
        button1.setContentAreaFilled(false);                 //背景を描画しない
        button1.setFocusPainted(false);                      //クリック時の枠線を描画しない
        button1.setBorderPainted(false);                     //枠線を描画しない
        button1.setActionCommand("backselectinst");          //ボタンに命令を与える
        button1.addActionListener(this);                     //ボタン動作の送信先を決める

        /*　上部パネルにコンポーネントを配置する　*/
        panel.add(Box.createRigidArea(new Dimension(420, 0)));
        panel.add(button1);

        /*　上段パネルを作成する　*/
        JPanel panel1 = new JPanel();                               //パネルを作成する
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));  //レイアウトを与える
        panel1.setOpaque(false);                                    //背景を描画しない

        /*　下段パネルを作成する　*/
        JPanel panel2 = new JPanel();                               //パネルを作成する
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));  //レイアウトを与える
        panel2.setOpaque(false);                                    //背景を描画しない
        
        /*　楽器ボタンを作成する　*/
        String[] insts = {"EDM", "JPOP", "ROCK", "JAZZ"};  //楽器名
        JButton[] buttons = new JButton[4];                //ボタンの配列を作成する
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(insts[i]);                     //ボタンを作成する
            buttons[i].setForeground(Color.WHITE);                  //字の色を白にする
            buttons[i].setMaximumSize(new Dimension(100, 100));     //大きさの最大値を決める
            buttons[i].setPreferredSize(new Dimension(100, 100));   //大きさを決める
            buttons[i].setContentAreaFilled(false);                 //背景を描画しない
            buttons[i].setFocusPainted(false);                      //クリック時の枠線を描画しない
            buttons[i].setActionCommand(insts[i]);                  //ボタンに命令を与える
            buttons[i].addActionListener(this);                     //ボタン動作の送信先を決める
        }

        /*　上段パネルにコンポーネントを配置する　*/
        panel1.add(buttons[0]);                                 //EDMボタン
        panel1.add(Box.createRigidArea(new Dimension(20, 0)));  //空白
        panel1.add(buttons[1]);                                 //JPOPボタン

        /*　下段パネルにコンポーネントを配置する　*/
        panel2.add(buttons[2]);                                 //ROCKボタン
        panel2.add(Box.createRigidArea(new Dimension(20, 0)));  //空白
        panel2.add(buttons[3]);                                 //JASSボタン

        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 30))); //空白
        backlbl.add(panel);                                     //上部パネル
        backlbl.add(Box.createRigidArea(new Dimension(0, 0))); //空白
        backlbl.add(panel1);                                    //上段パネル
        backlbl.add(Box.createRigidArea(new Dimension(0, 20))); //空白
        backlbl.add(panel2);                                    //下段パネル
        
        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		subFrame.revalidate();      //画面を更新する
        subFrame.setVisible(true);  //サブ画面を表示する
    }

    /*　音源試聴画面を描画　*/
    public void drawSelectLoopDisplay(){
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane();  //サブ画面のコンテナを取得する
        contentPane.setName("音源を選択");                   //メイン画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 640, 400);         //背景
        ImageIcon icon2 = resizeIcon(back, 40, 40);         //戻るボタン
        ImageIcon icon3 = resizeIcon(backRollover, 40, 40); //戻るボタンのロールオーバー時の画像
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　上部パネルを作成する　*/
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(460, 40));
        panel.setPreferredSize(new Dimension(460, 40));
        panel.setOpaque(false);

        /*　戻るボタンを作成する　*/
        JButton button1 = new JButton(icon2);                //ボタンを作成する
        button1.setRolloverIcon(icon3);                      //ロールオーバー時の画像を与える
        button1.setAlignmentY(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        button1.setMaximumSize(new Dimension(40, 40));       //大きさの最大値を決める
        button1.setPreferredSize(new Dimension(40, 40));     //大きさを決める
        button1.setContentAreaFilled(false);                 //背景を描画しない
        button1.setFocusPainted(false);                      //クリック時の枠線を描画しない
        button1.setBorderPainted(false);                     //枠線を描画しない
        button1.setActionCommand("backselectgenre");         //ボタンに命令を与える
        button1.addActionListener(this);                     //ボタン動作の送信先を決める

        /*　上部パネルにコンポーネントを配置する　*/
        panel.add(Box.createRigidArea(new Dimension(420, 0)));
        panel.add(button1);

        /*　音源パネルを作成する　*/
        JPanel panel1 = new JPanel();                               //パネルを作成する
        panel1.setBackground(bgc);                                  //背景色を決める
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));  //レイアウトを与える
        JScrollPane scroll = new JScrollPane(panel1);               //スクロールパネルを作成する
        scroll.setBackground(bgc);                                  //背景色を決める
        scroll.setMaximumSize(new Dimension(450, 230));             //大きさを決める
        scroll.setPreferredSize(new Dimension(450, 230));           //大きさを決める

        /*　音源パネルを作成する　*/
        ArrayList<LoopPane> lp = new ArrayList<>();             //音源パネルにリストを作成する
        for(int i = 0; i < libList.size(); i++){
            LoopPane newlp = new LoopPane(this, libList.get(i));//ファイル名を取得する
            newlp.setAlignmentX(Component.CENTER_ALIGNMENT);    //配置を中央揃えにする
            lp.add(newlp);                                      //リストにコンポーネントを配置する
        }

        /*　音源パネルにコンポーネントを配置する　*/
        for(int i = 0; i < lp.size(); i++){
            panel1.add(lp.get(i));
        }

        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 30)));
        backlbl.add(panel);
        backlbl.add(Box.createRigidArea(new Dimension(0, 10)));
        backlbl.add(scroll);
    
        /*　画面を描画する　*/
        contentPane.removeAll();            //画面を初期化する
        contentPane.add(backlbl);           //要素を配置する
        subFrame.addWindowListener(this);   //ウインドウ動作の送信先を決める
		subFrame.revalidate();              //画面を更新する
        subFrame.setVisible(true);          //サブ画面を表示する
    }

    /*　アップロード画面を描画　*/
    public void drawUploadDisplay(){
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane();  //サブ画面のコンテナを取得する
        contentPane.setName("曲の情報を入力");               //サブ画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 640, 400);    //背景
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　曲名の入力欄を作成する　*/
        JLabel label1 = new JLabel("曲名");                     //ラベルを作成する
        label1.setForeground(Color.WHITE);                      //字の色を白にする
        label1.setHorizontalAlignment(JLabel.CENTER);           //字を中央揃えにする
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        label1.setMaximumSize(new Dimension(100, 30));          //大きさの最大値を決める
        label1.setPreferredSize(new Dimension(100, 30));        //大きさを決める
        label1.setOpaque(false);                                //背景を描画しない
        titleField = new JTextField(name);                      //テキストフィールドを作成する
        titleField.setFont(new Font(Font.SERIF, Font.BOLD, 25));//フォントを設定する
        titleField.setHorizontalAlignment(JTextField.CENTER);   //字を中央揃えにする
        titleField.setAlignmentX(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        titleField.setMaximumSize(new Dimension(300, 50));      //大きさの最大値を決める
        titleField.setPreferredSize(new Dimension(300, 50));    //大きさを決める

        /*　ジャンルの入力欄を作成する　*/
        JLabel label2 = new JLabel("ジャンル");                //ラベルを作成する
        label2.setForeground(Color.WHITE);                    //字の色を白にする
        label2.setHorizontalAlignment(JLabel.CENTER);         //字を中央揃えにする
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);     //配置を中央揃えにする
        label2.setMaximumSize(new Dimension(100, 30));        //大きさの最大値を決める
        label2.setPreferredSize(new Dimension(100, 30));      //大きさを決める
        label2.setOpaque(false);                              //背景を描画しない
        genreCmb = new JComboBox<String>(genreData);          //選択肢を作る
        genreCmb.setAlignmentX(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        genreCmb.setMaximumSize(new Dimension(100, 40));      //大きさの最大値を決める
        genreCmb.setPreferredSize(new Dimension(100, 40));    //大きさを決める

        /*　編集選択パネルを作成する　*/
        JPanel panel1 = new JPanel();                               //パネルを作成する
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));  //レイアウトを与える
        panel1.setMaximumSize(new Dimension(200, 15));              //大きさの最大値を決める
        panel1.setPreferredSize(new Dimension(200, 15));            //大きさを決める
        panel1.setOpaque(false);                                    //背景を描画しない

        /*　編集可能ボタンを作成する　*/
        JRadioButton radio1 = new JRadioButton("編集可能", true);    //ラジオボタンを作成する
        radio1.setForeground(Color.WHITE);                          //字の色を白にする
        radio1.setAlignmentY(Component.CENTER_ALIGNMENT);           //配置を中央揃えにする
        radio1.setOpaque(false);                                    //背景を描画しない
        radio1.setActionCommand("1");                               //ボタンに命令を与える

        /*　編集不可ボタンを作成する　*/
        JRadioButton radio2 = new JRadioButton("編集不可"); //ラジオボタンを作成する
        radio2.setForeground(Color.WHITE);                  //字の色を白にする
        radio2.setAlignmentY(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        radio2.setOpaque(false);                            //背景を描画しない
        radio2.setActionCommand("0");                       //ボタンに命令を与える
        

        /*　ボタングループを作成する　*/
        editable = new ButtonGroup();   //ボタングループを作成する
        editable.add(radio1);           //編集可能ボタン
        editable.add(radio2);           //編集不可ボタン

        /*　編集選択パネルにコンポーネントを配置する　*/
        panel1.add(radio1);                                     //編集可能ボタン
        panel1.add(Box.createRigidArea(new Dimension(20, 0)));  //空白
        panel1.add(radio2);                                     //編集不可ボタン

        /*　アップロードボタンを作成する　*/
        JButton button = new JButton("アップロード");        //ボタンを作成する
        button.setForeground(Color.WHITE);                  //字の色を白にする
        button.setAlignmentX(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        button.setMaximumSize(new Dimension(200, 40));      //大きさの最大値を決める
        button.setPreferredSize(new Dimension(200, 40));    //大きさを決める
        button.setContentAreaFilled(false);                 //背景を描画しない
        button.setFocusPainted(false);                      //クリック時の枠線を描画しない
        button.setActionCommand("sendupload");              //ボタンに命令を与える
        button.addActionListener(this);                     //ボタン動作の送信先を決める

        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 50))); //空白
        backlbl.add(label1);                                    //曲名入力ラベル
        backlbl.add(titleField);                                //曲名入力欄
        backlbl.add(Box.createRigidArea(new Dimension(0, 10))); //空白
        backlbl.add(label2);                                    //ジャンル入力ラベル
        backlbl.add(genreCmb);                                  //ジャンル入力欄
        backlbl.add(Box.createRigidArea(new Dimension(0, 20))); //空白
        backlbl.add(panel1);                                    //編集選択パネル
        backlbl.add(Box.createRigidArea(new Dimension(0, 20))); //空白
        backlbl.add(button);                                    //アップロードボタン

        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		subFrame.revalidate();      //画面を更新する
        subFrame.setVisible(true);  //サブ画面を表示する
    }

    /*　曲の検索画面を描画　*/
    public void drawSearchDisplay(){        
        /*　画面のコンテナを取得する　*/
        Container contentPane = frame.getContentPane(); //メイン画面のコンテナを取得する
        contentPane.setName("曲の情報を入力");           //メイン画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 1280, 720);            //背景
        ImageIcon icon2 = resizeIcon(search, 50, 50);           //検索ボタン
        ImageIcon icon3 = resizeIcon(searchRollover, 50, 50);   //検索ボタン（ロールオーバー）
        ImageIcon icon4 = resizeIcon(back, 50, 50);             //戻るボタン
        ImageIcon icon5 = resizeIcon(backRollover, 50, 50);     //戻るボタン（ロールオーバー）
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　検索パネルを作成する　*/
        JPanel searchPane = new JPanel();                                   //パネルを作成する
        searchPane.setLayout(new BoxLayout(searchPane, BoxLayout.X_AXIS));  //レイアウトを与える
        searchPane.setOpaque(false);                                        //背景を描画しない

        /*　ユーザ名入力パネルを作成する　*/
        JPanel userPane = new JPanel();                                     //パネルを作成する
        userPane.setLayout(new BoxLayout(userPane, BoxLayout.Y_AXIS));      //レイアウトを与える
        userPane.setMaximumSize(new Dimension(400, 100));                   //大きさの最大値を決める
        userPane.setPreferredSize(new Dimension(400, 100));                 //大きさを決める
        userPane.setOpaque(false);                                          //背景を描画しない
        
        /*　タイトル入力パネルを作成する　*/
        JPanel titlePane = new JPanel();                                    //パネルを作成する
        titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.Y_AXIS));    //レイアウトを与える
        titlePane.setMaximumSize(new Dimension(400, 100));                  //大きさの最大値を決める
        titlePane.setPreferredSize(new Dimension(400, 100));                //大きさを決める
        titlePane.setOpaque(false);                                         //背景を描画しない

        /*　ジャンル入力パネルを作成する　*/
        JPanel genrePane = new JPanel();                                    //パネルを作成する
        genrePane.setLayout(new BoxLayout(genrePane, BoxLayout.Y_AXIS));    //レイアウトを与える
        genrePane.setMaximumSize(new Dimension(400, 100));                  //大きさの最大値を決める
        genrePane.setPreferredSize(new Dimension(400, 100));                //大きさを決める
        genrePane.setOpaque(false);                                         //背景を描画しない

        /*　ユーザ名検索欄を作る　*/
        JLabel userLabel = new JLabel("作曲者");                    //ラベルを作成する
        userLabel.setForeground(Color.WHITE);                       //字の色を白にする
        userLabel.setOpaque(false);                                 //背景を描画しない
        searchUser = new JTextField();                              //テキストフィールドを作成する
        searchUser.setBackground(Color.LIGHT_GRAY);                 //背景色を決める
        searchUser.setFont(new Font(Font.SERIF, Font.BOLD, 25));    //フォントを設定する
        searchUser.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        searchUser.setHorizontalAlignment(JTextField.CENTER);       //文字を中央揃えにする
        searchUser.setMaximumSize(new Dimension(400, 60));          //大きさの最大値を決める
        searchUser.setPreferredSize(new Dimension(400, 60));        //大きさを決める
        

        /*　タイトル検索欄を作る　*/
        JLabel titelLabel = new JLabel("曲名");                     //ラベルを作成する
        titelLabel.setForeground(Color.WHITE);                      //字の色を白にする
        titelLabel.setOpaque(false);                                //背景を描画しない
        searchTitle = new JTextField();                             //テキストフィールドを作成する
        searchTitle.setBackground(Color.LIGHT_GRAY);                //背景色を決める
        searchTitle.setFont(new Font(Font.SERIF, Font.BOLD, 25));   //フォントを設定する
        searchTitle.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        searchTitle.setHorizontalAlignment(JTextField.CENTER);      //文字を中央揃えにする
        searchTitle.setMaximumSize(new Dimension(400, 60));         //大きさの最大値を決める
        searchTitle.setPreferredSize(new Dimension(400, 60));       //大きさを決める

        /*　ジャンル検索欄を作る　*/
        JLabel genreLabel = new JLabel("ジャンル");                 //ラベルを作成する
        genreLabel.setForeground(Color.WHITE);                      //字の色を白にする
        genreLabel.setOpaque(false);                                //背景を描画しない
        searchGenre = new JTextField();                             //テキストフィールドを作成する
        searchGenre.setBackground(Color.LIGHT_GRAY);                //背景色を決める
        searchGenre.setFont(new Font(Font.SERIF, Font.BOLD, 25));   //フォントを設定する
        searchGenre.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        searchGenre.setHorizontalAlignment(JTextField.CENTER);      //文字を中央揃えにする
        searchGenre.setMaximumSize(new Dimension(400, 60));         //大きさの最大値を決める
        searchGenre.setPreferredSize(new Dimension(400, 60));       //大きさを決める
        
        /*　ユーザ名入力パネルにコンポーネントを配置する　*/
        userPane.add(userLabel);
        userPane.add(searchUser);

        /*　タイトル入力パネルにコンポーネントを配置する　*/
        titlePane.add(titelLabel);
        titlePane.add(searchTitle);

        /*　ジャンル入力パネルにコンポーネントを配置する　*/
        genrePane.add(genreLabel);
        genrePane.add(searchGenre);

        /*　検索ボタンを作成する　*/
        JButton searchButton = new JButton(icon2);              //ボタンを作成する
        searchButton.setRolloverIcon(icon3);                    //ロールオーバー時の画像を与える
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT); //配置を中央揃えにする
        searchButton.setMaximumSize(new Dimension(60, 60));     //大きさの最大値を決める
        searchButton.setPreferredSize(new Dimension(60, 60));   //大きさを決める
        searchButton.setContentAreaFilled(false);               //背景を描画しない
        searchButton.setFocusPainted(false);                    //クリック時の枠線を描画しない
        searchButton.setBorderPainted(false);                   //枠線を描画しない
        searchButton.setActionCommand("search");                //ボタンに命令を与える
        searchButton.addActionListener(this);                   //ボタン動作の送信先を決める

        /*　戻るボタンを作成する　*/
        JButton button = new JButton(icon4);                //ボタンを作成する
        button.setRolloverIcon(icon5);                      //ロールオーバー時の画像を与える
        button.setAlignmentX(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        button.setMaximumSize(new Dimension(60, 60));       //大きさの最大値を決める
        button.setPreferredSize(new Dimension(60, 60));     //大きさを決める
        button.setContentAreaFilled(false);                 //背景を描画しない
        button.setFocusPainted(false);                      //クリック時の枠線を描画しない
        button.setBorderPainted(false);                     //枠線を描画しない
        button.setActionCommand("backsearch");              //ボタンに命令を与える
        button.addActionListener(this);                     //ボタン動作の送信先を決める

        /*　検索パネルにコンポーネントを配置する　*/
        searchPane.add(Box.createRigidArea(new Dimension(100, 0))); //空白
        searchPane.add(userPane);                                   //ユーザ名入力パネル
        searchPane.add(titlePane);                                  //タイトル入力パネル
        searchPane.add(genrePane);                                  //ジャンル入力パネル
        searchPane.add(searchButton);                               //検索ボタン
        searchPane.add(button);                                     //検索ボタン
        searchPane.add(Box.createRigidArea(new Dimension(100, 0))); //空白

        LineBorder border = new LineBorder(bgc, 2);

        /*　ユーザ曲パネルを作成する　*/
        JPanel panel1 = new JPanel();                               //パネルを作成する
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));  //レイアウトを与える
        panel1.setMaximumSize(new Dimension(600, 240));             //大きさの最大値を決める
        panel1.setPreferredSize(new Dimension(600, 240));           //大きさを決める
        panel1.setBorder(border);                                   //枠線を決める

        /*　おすすめ曲パネルを作成する　*/
        JPanel panel2 = new JPanel();                               //パネルを作成する
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));  //レイアウトを与える
        panel2.setMaximumSize(new Dimension(600, 240));             //大きさの最大値を決める
        panel2.setPreferredSize(new Dimension(600, 240));           //大きさを決める
        panel2.setBorder(border);                                   //枠線を決める

        /*　ランキング曲パネルを作成する　*/
        JPanel panel3 = new JPanel();                               //パネルを作成する
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));  //レイアウトを与える
        panel3.setMaximumSize(new Dimension(600, 240));             //大きさの最大値を決める
        panel3.setPreferredSize(new Dimension(600, 240));           //大きさを決める
        panel3.setBorder(border);                                   //枠線を決める

        /*　いいね曲パネルを作成する　*/
        JPanel panel4 = new JPanel();                               //パネルを作成する
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));  //レイアウトを与える
        panel4.setMaximumSize(new Dimension(600, 240));             //大きさの最大値を決める
        panel4.setPreferredSize(new Dimension(600, 240));           //大きさを決める
        panel4.setBorder(border);                                   //枠線を決める

        /*　ユーザ曲一覧を作成する　*/
        ArrayList<ListenPane> lp1 = new ArrayList<>();                                                              //リストを作成する
        for(int i = 0; i < mySongList.size(); i++){             
            String[] word = mySongList.get(i).split("\\\\");                                                        //ユーザ曲の一覧を分解する
            String title = word[0];                                                                                 //曲名
            String composer = word[1];                                                                              //作曲者
            String genre = word[2];                                                                                 //ジャンル
            int bookmarks = Integer.parseInt(word[3]);                                                              //いいね数
            String date = word[4];                                                                                  //アップロード日時
            boolean editable = Boolean.parseBoolean(word[5]);                                                       //編集可能か
            boolean bookmark = favCheck(mySongList.get(i));                                                         //いいねしているか
            ListenPane newlp = new ListenPane(this, title, composer, genre, bookmarks, date, editable, bookmark);   //追加する曲のパネル
            newlp.setAlignmentX(Component.CENTER_ALIGNMENT);                                                        //配置を中央揃えにする
            lp1.add(newlp);                                                                                         //ユーザ曲一覧に追加する
        }

        /*　おすすめ曲一覧を作成する　*/
        ArrayList<ListenPane> lp2 = new ArrayList<>();                                                              //リストを作成する
        for(int i = 0; i < recSongList.size(); i++){
            String[] word = recSongList.get(i).split("\\\\");                                                       //おすすめ曲の一覧を分解する
            String title = word[0];                                                                                 //曲名
            String composer = word[1];                                                                              //作曲者
            String genre = word[2];                                                                                 //ジャンル
            int bookmarks = Integer.parseInt(word[3]);                                                              //いいね数
            String date = word[4];                                                                                  //アップロード日時
            boolean editable = Boolean.parseBoolean(word[5]);                                                       //編集可能か
            boolean bookmark = favCheck(recSongList.get(i));                                                         //いいねしているか
            ListenPane newlp = new ListenPane(this, title, composer, genre, bookmarks, date, editable, bookmark);   //追加する曲のパネル
            lp2.add(newlp);                                                                                         //おすすめ曲一覧に追加する
        }

        /*　ランキング曲一覧を作成する　*/
        ArrayList<ListenPane> lp3 = new ArrayList<>();                                                              //リストを作成する
        for(int i = 0; i < rankSongList.size(); i++){
            String[] word = rankSongList.get(i).split("\\\\");                                                      //ランキング曲の一覧を分解する
            String title = word[0];                                                                                 //曲名
            String composer = word[1];                                                                              //作曲者
            String genre = word[2];                                                                                 //ジャンル
            int bookmarks = Integer.parseInt(word[3]);                                                              //いいね数
            String date = word[4];                                                                                  //アップロード日時
            boolean editable = Boolean.parseBoolean(word[5]);                                                       //編集可能か
            boolean bookmark = favCheck(rankSongList.get(i));                                                       //いいねしているか
            ListenPane newlp = new ListenPane(this, title, composer, genre, bookmarks, date, editable, bookmark);   //追加する曲のパネル
            lp3.add(newlp);                                                                                         //ランキング曲一覧に追加する
        }

        /*　いいね曲一覧を作成する　*/
        ArrayList<ListenPane> lp4 = new ArrayList<>();                                                          //リストを作成する
        for(int i = 0; i < favSongList.size(); i++){
            String[] word = favSongList.get(i).split("\\\\");                                                   //ランキング曲の一覧を分解する
            String title = word[0];                                                                             //曲名
            String composer = word[1];                                                                          //作曲者
            String genre = word[2];                                                                             //ジャンル
            int bookmarks = Integer.parseInt(word[3]);                                                          //いいね数
            String date = word[4];                                                                              //アップロード日時
            boolean editable = Boolean.parseBoolean(word[5]);                                                   //編集可能か
            ListenPane newlp = new ListenPane(this, title, composer, genre, bookmarks, date, editable, true);   //追加する曲のパネル
            lp4.add(newlp);                                                                                     //ランキング曲一覧に追加する
        }

        /*　ユーザ曲ラベルを作成する　*/
        JLabel label1 = new JLabel("あなたの曲");                //ラベルを作成する
        label1.setFont(new Font(Font.SERIF, Font.BOLD, 25));    //フォントを設定する
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        label1.setHorizontalAlignment(JLabel.CENTER);           //文字を中央揃えにする

        /*　おすすめ曲ラベルを作成する　*/
        JLabel label2 = new JLabel("おすすめの曲");              //ラベルを作成する
        label2.setFont(new Font(Font.SERIF, Font.BOLD, 25));    //フォントを設定する
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        label2.setHorizontalAlignment(JLabel.CENTER);           //文字を中央揃えにする

        /*　ランキング曲ラベルを作成する　*/
        JLabel label3 = new JLabel("いいねランキング（検索欄）");   //ラベルを作成する
        label3.setFont(new Font(Font.SERIF, Font.BOLD, 25));      //フォントを設定する
        label3.setAlignmentX(Component.CENTER_ALIGNMENT);         //配置を中央揃えにする
        label3.setHorizontalAlignment(JLabel.CENTER);             //文字を中央揃えにする

        /*　いいね曲ラベルを作成する　*/
        JLabel label4 = new JLabel("いいねした曲");             //ラベルを作成する
        label4.setFont(new Font(Font.SERIF, Font.BOLD, 25));    //フォントを設定する
        label4.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        label4.setHorizontalAlignment(JLabel.CENTER);           //文字を中央揃えにする

        /*　ユーザ曲一覧パネルを作成する　*/
        JPanel songPane1 = new JPanel();                                    //パネルを作成する
        songPane1.setBackground(bgc);                                       //背景色を決める
        songPane1.setLayout(new BoxLayout(songPane1, BoxLayout.Y_AXIS));    //レイアウトを与える
        JScrollPane scroll1 = new JScrollPane(songPane1);                   //スクロールパネルを作成する
        scroll1.setMaximumSize(new Dimension(600, 200));                    //大きさの最大値を決める
        scroll1.setPreferredSize(new Dimension(600, 200));                  //大きさを決める

        /*　おすすめ曲一覧パネルを作成する　*/
        JPanel songPane2 = new JPanel();                                    //パネルを作成する
        songPane2.setBackground(bgc);                                       //背景色を決める
        songPane2.setLayout(new BoxLayout(songPane2, BoxLayout.Y_AXIS));    //レイアウトを与える
        JScrollPane scroll2 = new JScrollPane(songPane2);                   //スクロールパネルを作成する
        scroll2.setMaximumSize(new Dimension(600, 200));                    //大きさの最大値を決める
        scroll2.setPreferredSize(new Dimension(600, 200));                  //大きさを決める

        /*　ランキング曲一覧パネルを作成する　*/
        JPanel songPane3 = new JPanel();                                    //パネルを作成する
        songPane3.setBackground(bgc);                                       //背景色を決める
        songPane3.setLayout(new BoxLayout(songPane3, BoxLayout.Y_AXIS));    //レイアウトを与える
        JScrollPane scroll3 = new JScrollPane(songPane3);                   //スクロールパネルを作成する
        scroll3.setMaximumSize(new Dimension(600, 200));                    //大きさの最大値を決める
        scroll3.setPreferredSize(new Dimension(600, 200));                  //大きさを決める

        /*　いいね曲一覧パネルを作成する　*/
        JPanel songPane4 = new JPanel();                                    //パネルを作成する
        songPane4.setBackground(bgc);                                       //背景色を決める
        songPane4.setLayout(new BoxLayout(songPane4, BoxLayout.Y_AXIS));    //レイアウトを与える
        JScrollPane scroll4 = new JScrollPane(songPane4);                   //スクロールパネルを作成する
        scroll4.setMaximumSize(new Dimension(600, 200));                    //大きさの最大値を決める
        scroll4.setPreferredSize(new Dimension(600, 200));                  //大きさを決める

        /*　ユーザ曲一覧パネルにコンポーネントを追加する　*/
        for(int i = 0; i < lp1.size(); i++){
            songPane1.add(lp1.get(i));
        }

        /*　おすすめ曲一覧パネルにコンポーネントを追加する　*/
        for(int i = 0; i < lp2.size(); i++){
            songPane2.add(lp2.get(i));
        }

        /*　ランキング曲一覧パネルにコンポーネントを追加する　*/
        for(int i = 0; i < lp3.size(); i++){
            songPane3.add(lp3.get(i));
        }

        /*　いいね曲一覧パネルにコンポーネントを追加する　*/
        for(int i = 0; i < lp4.size(); i++){
            songPane4.add(lp4.get(i));
        }

        /*　ユーザ曲パネルにコンポーネントを追加する　*/
        panel1.add(Box.createRigidArea(new Dimension(0, 10)));
        panel1.add(label1);
        panel1.add(Box.createRigidArea(new Dimension(0, 10)));
        panel1.add(scroll1);

        /*　おすすめ曲パネルにコンポーネントを追加する　*/
        panel2.add(Box.createRigidArea(new Dimension(0, 10)));
        panel2.add(label2);
        panel2.add(Box.createRigidArea(new Dimension(0, 10)));
        panel2.add(scroll2);

        /*　ランキング曲パネルにコンポーネントを追加する　*/
        panel3.add(Box.createRigidArea(new Dimension(0, 10)));
        panel3.add(label3);
        panel3.add(Box.createRigidArea(new Dimension(0, 10)));
        panel3.add(scroll3);

        /*　いいね曲パネルにコンポーネントを追加する　*/
        panel4.add(Box.createRigidArea(new Dimension(0, 10)));
        panel4.add(label4);
        panel4.add(Box.createRigidArea(new Dimension(0, 10)));
        panel4.add(scroll4);

        /*　左パネルを作成する　*/
        JPanel holdPanel1 = new JPanel();                                   //パネルを作成する
        holdPanel1.setLayout(new BoxLayout(holdPanel1, BoxLayout.Y_AXIS));  //レイアウトを与える
        holdPanel1.setOpaque(false);                                        //背景を描画しない

        /*　右パネルを作成する　*/
        JPanel holdPanel2 = new JPanel();                                   //パネルを作成する
        holdPanel2.setLayout(new BoxLayout(holdPanel2, BoxLayout.Y_AXIS));  //レイアウトを与える
        holdPanel2.setOpaque(false);                                        //背景を描画しない

        /*　中央パネルを作成する  */
        JPanel holdPanel3 = new JPanel();                                   //パネルを作成する
        holdPanel3.setLayout(new BoxLayout(holdPanel3, BoxLayout.X_AXIS));  //レイアウトを与える
        holdPanel3.setOpaque(false);                                        //背景を描画しない

        /*　左パネルにコンポーネントを配置する　*/
        holdPanel1.add(panel1);                                     //ユーザ曲パネル
        holdPanel1.add(Box.createRigidArea(new Dimension(0, 20)));  //空白
        holdPanel1.add(panel2);                                     //おすすめ曲パネル

        /*　右パネルにコンポーネントを配置する　*/
        holdPanel2.add(panel3);                                     //ランキング曲パネル
        holdPanel2.add(Box.createRigidArea(new Dimension(0, 20)));  //空白
        holdPanel2.add(panel4);                                     //いいね曲パネル

        /*　中央パネルにコンポーネントを配置する　*/
        holdPanel3.add(holdPanel1);                                 //曲パネル
        holdPanel3.add(Box.createRigidArea(new Dimension(20, 0)));  //空白
        holdPanel3.add(holdPanel2);                                 //いいね曲パネル

        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 20))); //空白
        backlbl.add(searchPane);                                //検索パネル
        backlbl.add(holdPanel3);                                //中央パネル

        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		frame.revalidate();         //画面を更新する
    }

    public void drawCommentDisplay(){
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane();  //サブ画面のコンテナを取得する
        contentPane.setName("コメント");                     //サブ画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 640, 400);    //背景
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　コメントを表示する　*/
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.setBackground(bgc);
        JScrollPane scroll = new JScrollPane(panel1);
        scroll.setMaximumSize(new Dimension(590, 200));
        scroll.setPreferredSize(new Dimension(590, 200));

        /*　コメント一覧を作成する　*/
        ArrayList<CommentPane> cp = new ArrayList<>();              //リストを作成する
        for(int i = 0; i < commentList.size(); i++){
            String[] word = commentList.get(i).split("/");          //コメントの一覧を分解する
            String userid = word[0];                                //コメントを書いたユーザ名
            String comment = word[1];                               //コメント
            CommentPane newcp = new CommentPane(userid, comment);   //追加する曲のパネル
            newcp.setAlignmentX(Component.CENTER_ALIGNMENT);        //配置を中央揃えにする
            cp.add(newcp);                                          //コメント一覧に追加する
        }

        /*　コメント記入欄を作成する　*/
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        panel2.setBackground(bgc);
        JLabel label1 = new JLabel("コメント");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        label1.setAlignmentY(Component.CENTER_ALIGNMENT);
        label1.setHorizontalAlignment(JLabel.CENTER);
        label1.setMaximumSize(new Dimension(70, 50));
        label1.setPreferredSize(new Dimension(70, 50));
        commentArea = new JTextField();                              //テキストフィールドを作成する
        commentArea.setFont(new Font(Font.SERIF, Font.BOLD, 11));   //フォントを設定する
        commentArea.setMaximumSize(new Dimension(370, 20));         //大きさの最大値を決める
        commentArea.setPreferredSize(new Dimension(370, 20));       //大きさを決める
        commentArea.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        commentArea.addKeyListener(this);

        /*　コメント送信ボタンを作成する　*/
        JButton button = new JButton("送信");                //ボタンを作成する
        button.setForeground(Color.WHITE);                  //字の色を白にする
        button.setFont(new Font(Font.SERIF, Font.BOLD, 15));//フォントを設定する
        button.setAlignmentX(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        button.setMaximumSize(new Dimension(100, 50));      //大きさの最大値を決める
        button.setPreferredSize(new Dimension(100, 50));    //大きさを決める
        button.setContentAreaFilled(false);;                //背景を描画しない
        button.setActionCommand("sendcomment" + sep);       //ボタンに命令を与える
        button.addActionListener(this);                     //ボタン動作の送信先を決める
        
        /*　コメント一覧パネルにコンポーネントを追加する　*/
        panel1.add(Box.createRigidArea(new Dimension(0, 20)));
        for(int i = 0; i < cp.size(); i++){
            panel1.add(cp.get(i));
        }

        /*　コメント記入パネルにコンポーネントを配置する　*/
        panel2.add(Box.createRigidArea(new Dimension(5, 0)));
        panel2.add(label1);
        panel2.add(Box.createRigidArea(new Dimension(10, 0)));
        panel2.add(commentArea);
        panel2.add(Box.createRigidArea(new Dimension(5, 0)));

        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 10)));
        backlbl.add(scroll);
        backlbl.add(Box.createRigidArea(new Dimension(0, 10)));
        backlbl.add(panel2);
        backlbl.add(Box.createRigidArea(new Dimension(0, 10)));
        backlbl.add(button);
        backlbl.add(Box.createRigidArea(new Dimension(0, 10)));

        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		subFrame.revalidate();      //画面を更新する
        subFrame.setVisible(true);  //サブ画面を表示する
    }

    /*　ログイン，新規登録の選択画面を描画　*/
    public void drawSelectLoginDisplay(){    	
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane();  //サブ画面のコンテナを取得する
        contentPane.setName("アカウント管理");               //サブ画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 640, 400);    //背景
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　サーバIP入力欄を作成する　*/
        JLabel label1 = new JLabel("サーバIP");
        label1.setForeground(Color.WHITE);
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);
        label1.setHorizontalAlignment(JLabel.CENTER);
        serverIPField = new JTextField(serverIP);
        serverIPField.setFont(new Font(Font.SERIF, Font.BOLD, 20));   //フォントを設定する
        serverIPField.setAlignmentX(Component.CENTER_ALIGNMENT);
        serverIPField.setHorizontalAlignment(JTextField.CENTER);
        serverIPField.setMaximumSize(new Dimension(200, 30));
        serverIPField.setPreferredSize(new Dimension(200, 30));
        
        /*　ログインボタンを作成する　*/
	    JButton button1 = new JButton("ログイン");               //ボタンを作成する
	    button1.setFont(new Font(Font.SERIF, Font.BOLD, 20));   //フォントを設定する
        button1.setForeground(Color.WHITE);                     //字の色を白にする
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button1.setMaximumSize(new Dimension(200, 50));         //大きさの最大値を決める
        button1.setPreferredSize(new Dimension(200, 50));       //大きさを決める
        button1.setContentAreaFilled(false);                    //背景を描画しない
        button1.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button1.setActionCommand("login");                      //ボタンに命令を与える
	    button1.addActionListener(this);                        //ボタン動作の送信先を決める
	    
        /*　新規登録ボタンを作成する　*/
	    JButton button2 = new JButton("新規登録");               //ボタンを作成する
	    button2.setFont(new Font(Font.SERIF, Font.BOLD, 20));   //フォントを設定する
        button2.setForeground(Color.WHITE);                     //字の色を白にする
        button2.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button2.setMaximumSize(new Dimension(200, 50));         //大きさの最大値を決める
        button2.setPreferredSize(new Dimension(200, 50));       //大きさを決める
        button2.setContentAreaFilled(false);                    //背景を描画しない
        button2.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button2.setActionCommand("signup");                     //ボタンに命令を与える
	    button2.addActionListener(this);                        //ボタン動作の送信先を決める

        /*　新規登録ボタンを作成する　*/
	    JButton button3 = new JButton("ログアウト");             //ボタンを作成する
	    button3.setFont(new Font(Font.SERIF, Font.BOLD, 20));   //フォントを設定する
        button3.setForeground(Color.WHITE);                     //字の色を白にする
        button3.setAlignmentX(Component.CENTER_ALIGNMENT);      //配置を中央揃えにする
        button3.setMaximumSize(new Dimension(200, 50));         //大きさの最大値を決める
        button3.setPreferredSize(new Dimension(200, 50));       //大きさを決める
        button3.setContentAreaFilled(false);                    //背景を描画しない
        button3.setFocusPainted(false);                         //クリック時の枠線を描画しない
        button3.setActionCommand("logout");                     //ボタンに命令を与える
	    button3.addActionListener(this);                        //ボタン動作の送信先を決める
	    
        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 50)));    //空白
        backlbl.add(label1);
        backlbl.add(serverIPField);
        backlbl.add(Box.createRigidArea(new Dimension(0, 30)));    //空白
	    backlbl.add(button1);                                      //ログインボタン
        backlbl.add(Box.createRigidArea(new Dimension(0, 20)));    //空白
	    backlbl.add(button2);                                      //新規登録
        backlbl.add(Box.createRigidArea(new Dimension(0, 20)));    //空白
	    backlbl.add(button3);                                      //ログアウト
	    
	    /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		subFrame.revalidate();      //画面を更新する
        subFrame.setVisible(true);  //サブ画面を表示する
    }

    /*　ログイン画面を描画　*/
    public void drawLoginDisplay(){
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane();  //サブ画面のコンテナを取得する
        contentPane.setName("ログイン");                     //サブ画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 640, 400);         //背景
        ImageIcon icon2 = resizeIcon(back, 40, 40);         //戻るボタン
        ImageIcon icon3 = resizeIcon(backRollover, 40, 40); //戻るボタンのロールオーバー時の画像を与える
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　上部パネルを作成する　*/
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(460, 40));
        panel.setPreferredSize(new Dimension(460, 40));
        panel.setOpaque(false);

        /*　戻るボタンを作成する　*/
        JButton button1 = new JButton(icon2);                //ボタンを作成する
        button1.setRolloverIcon(icon3);                      //ロールオーバー時の画像を与える
        button1.setAlignmentY(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        button1.setMaximumSize(new Dimension(40, 40));       //大きさの最大値を決める
        button1.setPreferredSize(new Dimension(40, 40));     //大きさを決める
        button1.setContentAreaFilled(false);                 //背景を描画しない
        button1.setFocusPainted(false);                      //クリック時の枠線を描画しない
        button1.setBorderPainted(false);                     //枠線を描画しない
        button1.setActionCommand("backselectlogin");         //ボタンに命令を与える
        button1.addActionListener(this);                     //ボタン動作の送信先を決める

        /*　上部パネルにコンポーネントを配置する　*/
        panel.add(Box.createRigidArea(new Dimension(420, 0)));
        panel.add(button1);
        
        /*　ユーザ名入力欄を作成する　*/
	    JLabel label1 = new JLabel("アカウント名");              //ラベルを作成する
        label1.setFont(new Font(Font.SERIF, Font.BOLD, 20));    //フォントを設定する
        label1.setForeground(Color.WHITE);                      //字の色を白にする
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        label1.setHorizontalAlignment(JLabel.CENTER);           //文字を中央揃えにする
        label1.setMaximumSize(new Dimension(200, 30));          //大きさの最大値を決める
        label1.setPreferredSize(new Dimension(200, 30));        //大きさを決める
        username = new JTextField();                            //テキストフィールドを作成する
        username.setFont(new Font(Font.SERIF, Font.BOLD, 25));  //フォントを設定する
        username.setAlignmentX(Component.CENTER_ALIGNMENT);     //配置を中央揃えにする
        username.setHorizontalAlignment(JTextField.CENTER);     //文字を中央揃えにする
        username.setMaximumSize(new Dimension(400, 50));        //大きさの最大値を決める
        username.setPreferredSize(new Dimension(400, 50));      //大きさを決める
        
        /*　パスワード入力欄を作成する　*/
        JLabel label2 = new JLabel("パスワード");                //ラベルを作成する
        label2.setFont(new Font(Font.SERIF, Font.BOLD, 20));    //フォントを設定する
        label2.setForeground(Color.WHITE);                      //字の色を白にする
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        label2.setHorizontalAlignment(JLabel.CENTER);           //文字を中央揃えにする
        label2.setMaximumSize(new Dimension(200, 30));          //大きさの最大値を決める
        label2.setPreferredSize(new Dimension(200, 30));        //大きさを決める
        pass = new JPasswordField();                            //テキストフィールドを作成する
        pass.setFont(new Font(Font.SERIF, Font.BOLD, 25));      //フォントを設定する
        pass.setAlignmentX(Component.CENTER_ALIGNMENT);         //配置を中央揃えにする
        pass.setHorizontalAlignment(JPasswordField.CENTER);     //文字を中央揃えにする
        pass.setMaximumSize(new Dimension(400, 50));            //大きさの最大値を決める
        pass.setPreferredSize(new Dimension(400, 50));          //大きさを決める
        
        /*　ログインボタンを作成する　*/
	    JButton button2 = new JButton("ログイン");                //ボタンを作成する
	    button2.setFont(new Font(Font.SERIF, Font.BOLD, 20));    //フォントを設定する
        button2.setForeground(Color.WHITE);                      //字の色を白にする
        button2.setMaximumSize(new Dimension(200, 50));          //大きさの最大値を決める
        button2.setPreferredSize(new Dimension(200, 50));        //大きさを決める
        button2.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        button2.setContentAreaFilled(false);                     //背景を描画しない
        button2.setFocusPainted(false);                          //クリック時の枠線を描画しない
        button2.setActionCommand("sendlogin");                   //ボタンに命令を与える
	    button2.addActionListener(this);                         //ボタン動作の送信先を決める
	    
        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 30))); //空白
        backlbl.add(panel);                                     //上部パネル
        backlbl.add(Box.createRigidArea(new Dimension(0, 10))); //空白
        backlbl.add(label1);                                    //ユーザ名ラベル
	    backlbl.add(username);                                  //ユーザ名入力欄
        backlbl.add(Box.createRigidArea(new Dimension(0, 10))); //空白
        backlbl.add(label2);                                    //パスワードラベル
	    backlbl.add(pass);                                      //パスワード入力欄
        backlbl.add(Box.createRigidArea(new Dimension(0, 20))); //空白
        backlbl.add(button2);                                   //ログインボタン
        
        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		subFrame.revalidate();      //画面を更新する
        subFrame.setVisible(true);  //サブ画面を表示する
    }

    /*　新規登録画面を描画　*/
    public void drawSignupDisplay(){
        /*　画面のコンテナを取得する　*/
        Container contentPane = subFrame.getContentPane();  //サブ画面のコンテナを取得する
        contentPane.setName("新規登録");                     //サブ画面のタイトルを変更する

        /*　画像をリサイズする　*/
        ImageIcon icon1 = resizeIcon(bg, 640, 400);         //背景
        ImageIcon icon2 = resizeIcon(back, 40, 40);         //戻るボタン
        ImageIcon icon3 = resizeIcon(backRollover, 40, 40); //戻るボタンのロールオーバー時の画像を与える
        
        /*　背景を作成する　*/
        JLabel backlbl = new JLabel(icon1);                             //背景画像を作成する
        backlbl.setAlignmentX(Component.CENTER_ALIGNMENT);              //配置を中央揃えにする
        backlbl.setLayout(new BoxLayout(backlbl, BoxLayout.Y_AXIS));    //レイアウトを与える

        /*　上部パネルを作成する　*/
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(460, 40));
        panel.setPreferredSize(new Dimension(460, 40));
        panel.setOpaque(false);

        /*　戻るボタンを作成する　*/
        JButton button1 = new JButton(icon2);                //ボタンを作成する
        button1.setRolloverIcon(icon3);                      //ロールオーバー時の画像を与える
        button1.setAlignmentY(Component.CENTER_ALIGNMENT);   //配置を中央揃えにする
        button1.setMaximumSize(new Dimension(40, 40));       //大きさの最大値を決める
        button1.setPreferredSize(new Dimension(40, 40));     //大きさを決める
        button1.setContentAreaFilled(false);                 //背景を描画しない
        button1.setFocusPainted(false);                      //クリック時の枠線を描画しない
        button1.setBorderPainted(false);                     //枠線を描画しない
        button1.setActionCommand("backselectlogin");         //ボタンに命令を与える
        button1.addActionListener(this);                     //ボタン動作の送信先を決める

        /*　上部パネルにコンポーネントを配置する　*/
        panel.add(Box.createRigidArea(new Dimension(420, 0)));
        panel.add(button1);
        
        /*　ユーザ名入力欄を作成する　*/
	    JLabel label1 = new JLabel("アカウント名");              //ラベルを作成する
        label1.setFont(new Font(Font.SERIF, Font.BOLD, 15));    //フォントを設定する
        label1.setForeground(Color.WHITE);                      //字の色を白にする
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        label1.setHorizontalAlignment(JLabel.CENTER);           //文字を中央揃えにする
        label1.setMaximumSize(new Dimension(200, 20));          //大きさの最大値を決める
        label1.setPreferredSize(new Dimension(200, 20));        //大きさを決める
        username = new JTextField();                            //テキストフィールドを作成する
        username.setFont(new Font(Font.SERIF, Font.BOLD, 20));  //フォントを設定する
        username.setAlignmentX(Component.CENTER_ALIGNMENT);     //配置を中央揃えにする
        username.setHorizontalAlignment(JTextField.CENTER);     //文字を中央揃えにする
        username.setMaximumSize(new Dimension(400, 30));        //大きさの最大値を決める
        username.setPreferredSize(new Dimension(400, 30));      //大きさを決める
        
        /*　パスワード入力欄を作成する　*/
        JLabel label2 = new JLabel("パスワード");                //ラベルを作成する
        label2.setFont(new Font(Font.SERIF, Font.BOLD, 15));    //フォントを設定する
        label2.setForeground(Color.WHITE);                      //字の色を白にする
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        label2.setHorizontalAlignment(JLabel.CENTER);           //文字を中央揃えにする
        label2.setMaximumSize(new Dimension(200, 20));          //大きさの最大値を決める
        label2.setPreferredSize(new Dimension(200, 20));        //大きさを決める
        pass = new JPasswordField();                            //テキストフィールドを作成する
        pass.setFont(new Font(Font.SERIF, Font.BOLD, 20));      //フォントを設定する
        pass.setAlignmentX(Component.CENTER_ALIGNMENT);         //配置を中央揃えにする
        pass.setHorizontalAlignment(JPasswordField.CENTER);     //文字を中央揃えにする
        pass.setMaximumSize(new Dimension(400, 30));            //大きさの最大値を決める
        pass.setPreferredSize(new Dimension(400, 30));          //大きさを決める
        
        /*　パスワード確認入力欄を作成する　*/
        JLabel label3 = new JLabel("パスワード確認用");               //ラベルを作成する
        label3.setFont(new Font(Font.SERIF, Font.BOLD, 15));        //フォントを設定する
        label3.setForeground(Color.WHITE);                          //字の色を白にする
        label3.setAlignmentX(Component.CENTER_ALIGNMENT);           //配置を中央揃えにする
        label3.setHorizontalAlignment(JLabel.CENTER);               //文字を中央揃えにする
        label3.setMaximumSize(new Dimension(200, 20));          //大きさの最大値を決める
        label3.setPreferredSize(new Dimension(200, 20));        //大きさを決める
        passCheck = new JPasswordField();                           //テキストフィールドを作成する
        passCheck.setFont(new Font(Font.SERIF, Font.BOLD, 20));     //フォントを設定する
        passCheck.setAlignmentX(Component.CENTER_ALIGNMENT);        //配置を中央揃えにする
        passCheck.setHorizontalAlignment(JPasswordField.CENTER);    //文字を中央揃えにする
        passCheck.setMaximumSize(new Dimension(400, 30));           //大きさの最大値を決める
        passCheck.setPreferredSize(new Dimension(400, 30));         //大きさを決める

	    /*　新規登録ボタンを作成する　*/
	    JButton button = new JButton("新規登録");                //ボタンを作成する
	    button.setFont(new Font(Font.SERIF, Font.BOLD, 20));    //フォントを設定する
        button.setForeground(Color.WHITE);                      //字の色を白にする
        button.setMaximumSize(new Dimension(200, 50));          //大きさの最大値を決める
        button.setPreferredSize(new Dimension(200, 50));        //大きさを決める
        button.setAlignmentX(Component.CENTER_ALIGNMENT);       //配置を中央揃えにする
        button.setContentAreaFilled(false);                     //背景を描画しない
        button.setFocusPainted(false);                          //クリック時の枠線を描画しない
        button.setActionCommand("sendsignup");                  //ボタンに命令を与える
	    button.addActionListener(this);                         //ボタン動作の送信先を決める
	    
        /*　コンポーネントを配置する　*/
        backlbl.add(Box.createRigidArea(new Dimension(0, 30))); //空白
        backlbl.add(panel);                                     //上部パネル
        backlbl.add(label1);                                    //ユーザ名ラベル
	    backlbl.add(username);                                  //ユーザ名入力欄
        backlbl.add(Box.createRigidArea(new Dimension(0, 10))); //空白
        backlbl.add(label2);                                    //パスワードラベル
	    backlbl.add(pass);                                      //パスワード入力欄
        backlbl.add(Box.createRigidArea(new Dimension(0, 10))); //空白
        backlbl.add(label3);                                    //パスワード確認ラベル
	    backlbl.add(passCheck);                                 //パスワード確認入力欄
        backlbl.add(Box.createRigidArea(new Dimension(0, 30))); //空白
        backlbl.add(button);                                    //ログインボタン
        
        /*　画面を描画する　*/
        contentPane.removeAll();    //画面を初期化する
        contentPane.add(backlbl);   //要素を配置する
		subFrame.revalidate();      //画面を更新する
        subFrame.setVisible(true);  //サブ画面を表示する
    }

    /*　小節を追加する　*/
    public void addBar(){
        int n = barPane.size();
        int cc = workPane.getComponentCount();
        BarPane newbp = new BarPane(this, n);
        barPane.add(newbp);
        if(cc > 0){
            workPane.remove(cc - 1);
        }
        workPane.add(barPane.get(n));
        workPane.add(plusBtn);
        frame.revalidate();
    }

    /*　小節を選択する　*/
    public void setNumber(int n){
        number = n;
        for(int i = 0; i < barPane.size(); i++){
            barPane.get(i).setOpaque(false);
            barPane.get(i).setBackground(Color.WHITE);
        }
        barPane.get(n).setOpaque(true);
        barPane.get(n).setBackground(new Color(220, 255, 255));
    }

    /*　楽器名を対応した数字に変換する　*/
    public int getInstId(String inst){
        switch(inst){
            case "piano":
                return 0;
            case "guitar":
                return 1;
            case "bass":
                return 2;
            case "drum":
                return 3;
            case "synth":
                return 4;
        }

        return -1;
    }

    /*　対応した数字から楽器名に変換する　*/
    public String getInstName(int id){
        switch(id){
            case 0:
                return "piano";
            case 1:
                return "guitar";
            case 2:
                return "bass";
            case 3:
                return "drum";
            case 4:
                return "synth";
        }

        return null;
    }

    /*　楽器用パネルを取得する　*/
    public GridPane getGrid(String inst){
        int n = getInstId(inst);
        if(n > -1){
            return barPane.get(number).gridPane[n];
        }

        return null;
    }

    /*　楽器用パネルを取得する　*/
    public GridPane getGrid(int id){
        return barPane.get(number).gridPane[id];
    }

    /*　音源を追加する　*/
    public void addSource(String inst, String lib, String file){
        if(getGrid(inst) != null){
            getGrid(inst).setInst();
            getUsedList().add(lib + sep + file);
        }
    }

    /*　音源を削除する　*/
    public void delSource(String inst, int playNo){
        getGrid(inst).delInst(playNo);
    }

    /*　作曲画面を初期化する　*/
    public void resetCompose(){
        while(barPane.size() > 0){
            barPane.remove(0);
        }
    }

    /*　曲名を与える　*/
    public void setName(String name){
        this.name = name;
        frame.setTitle(this.name);
    }

    /*　楽器を決定する　*/
    public void setInst(String inst){
        this.inst = inst;
    }

    /*　音源ライブラリの一覧を初期化する　*/
    public void resetLib(){
        while(libList.size() > 0){
            libList.remove(0);
        }
    }

    /*　音源ライブラリの一覧を与える　*/
    public void addLib(String lib, String inst, String genre, String file){
        libList.add(lib + sep + inst + sep + genre + sep + file);
    }

    /*　使用している音源の一覧を取得する　*/
    public ArrayList<String> getUsedList(){
        return barPane.get(number).usedList;
    }

    /*　自分の曲の一覧に追加する　*/
    public void addMyList(String data){
        mySongList.add(data);
    }

    /*　自分の曲の一覧を取得する　*/
    public ArrayList<String> getMyList(){
        return mySongList;
    }

    /*　自分の曲の一覧を初期化する　*/
    public void resetMyList(){
        while(mySongList.size() > 0){
            mySongList.remove(0);
        }
    }

    /*　おすすめの曲の一覧に追加する　*/
    public void addRecList(String data){
        recSongList.add(data);
    }

    /*　おすすめの曲の一覧を取得する　*/
    public ArrayList<String> getRecList(){
        return recSongList;
    }

    /*　おすすめの曲の一覧を初期化する　*/
    public void resetRecList(){
        while(recSongList.size() > 0){
            recSongList.remove(0);
        }
    }

    /*　いいねの曲の一覧に追加する　*/
    public void addFavList(String data){
        favSongList.add(data);
    }

    /*　いいねの曲の一覧を取得する　*/
    public ArrayList<String> getFavList(){
        return favSongList;
    }

    /*　いいねの曲の一覧を初期化する　*/
    public void resetFavList(){
        while(favSongList.size() > 0){
            favSongList.remove(0);
        }
    }

    /*　いいねの曲の一覧に追加する　*/
    public void addRankList(String data){
        rankSongList.add(data);
    }

    /*　いいねの曲の一覧を取得する　*/
    public ArrayList<String> getRankList(){
        return rankSongList;
    }

    /*　いいねの曲の一覧を初期化する　*/
    public void resetRankList(){
        while(rankSongList.size() > 0){
            rankSongList.remove(0);
        }
    }

    /*　いいねした曲か確認する　*/
    public boolean favCheck(String sd){
        for(int i = 0; i < getFavList().size(); i++){
            if(sd.equals(getFavList().get(i))){
                return true;
            }
        }

        return false;
    }

    /*　コメントの一覧に追加する　*/
    public void addComList(String data){
        commentList.add(data);
    }

    /*　コメントの一覧を取得する　*/
    public ArrayList<String> getComList(){
        return commentList;
    }

    /*　コメントの一覧を初期化する　*/
    public void resetComList(){
        while(commentList.size() > 0){
            commentList.remove(0);
        }
    }

    /*　戻る先を与える　*/
    public void setBackMode(String mode){
        backmode = mode;
    }

    /*　戻る先を取得する　*/
    public String getBackMode(){
        return backmode;
    }

    /*　ユーザ名を取得する　*/
    public String getUsername(){
        return username.getText();
    }

    /*　パスワードを取得する　*/
    public String getPass(){
        return new String(pass.getPassword());
    }

    /*　確認用パスワードを取得する　*/
    public String getPassCheck(){
        return new String(passCheck.getPassword());
    }

    /*　選択した曲の作曲者と曲名を与える　*/
    public void setSongData(String composer, String title){
        this.composer = composer;
        this.title = title;
    }

    /*　選択した曲の作曲者と曲名を取得する　*/
    public String getSongData(){
        return composer + sep + title;
    }

    /*　検索情報を取得する　*/
    public String getSearchInfo(){
        String composer = "composer/" + searchUser.getText();
        String title = "title/" + searchTitle.getText();
        String genre = "genre/" + searchGenre.getText();
        return composer + sep + title + sep + genre;
    }

    /*　小節での楽器数を確認する　*/
    public boolean checkInstSize(String inst){
        GridPane gp = getGrid(inst);

        if(gp != null){
            if(gp.size < GridPane.RANGE){
                return true;
            }
        }

        return false;
    }

    /*　音量を与える　*/
    public void setVolume(int n){
        volume = n;
    }

    /*　音量を与える　*/
    public int getVolume(){
        return volume;
    }

    /*　メッセージダイアログを表示する　*/
    public void msgDialog(String msg){
        JOptionPane.showMessageDialog(frame, new JLabel(msg));
    }

    /*　楽器選択時の処理　*/
    public void selInst(String inst){
        if(checkInstSize(inst)){
            setInst(inst);
            drawSelectGenreDisplay();
        }else{
            msgDialog("ひとつの小節の同じ楽器には4つまでしか音源を入れられません");
        }
    }

    /*　アイコンの大きさを変更する　*/
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height){
        Image image = icon.getImage();
        Image scale = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scale);
    }

    /*　プロジェクトの絶対パスを取得する　*/
    public String getAbsPath(){
        Path p1 = Paths.get("");
        Path p2 = p1.toAbsolutePath();
        String[] word;
        if(File.separator.equals("\\")){
            word = p2.toString().split("\\\\");
        }
        else{
            word = p2.toString().split(File.separator);
        }
        String path = "";
        for(int i = 0; i < word.length - 1; i++){
            path += word[i] + File.separator;
        }
        return path;
    }

    /*　ボタン操作　*/
    public void actionPerformed(ActionEvent e){
        String cmd = e.getActionCommand();
        String[] word = cmd.split(sep);

        String un;
        String p;
        String pc;

        switch(word[0]){
            case "composition":
                drawProjectDisplay(); 
                break;
            case "listen":
                ctrl.processEvent(cmd);
                break;
            case "selectlogin":
                drawSelectLoginDisplay(); 
                break;
            case "update":
                ctrl.processEvent(cmd);
                break;
            case "login":
                serverIP = serverIPField.getText();
                ctrl.processEvent("login" + sep + serverIP);
                break;
            case "sendlogin":
                un = getUsername();
                p = getPass();
                if(un.isEmpty()){
                    msgDialog("ユーザ名を入力してください");
                }
                else if(p.isEmpty()){
                    msgDialog("パスワードを入力してください");
                }
                else{
                    ctrl.processEvent("sendlogin" + sep + un + sep + p);
                    subFrame.dispose();
                }
                break;
            case "signup":
                serverIP = serverIPField.getText();
                ctrl.processEvent("signup" + sep + serverIP);
                break;
            case "sendsignup":
                un = getUsername();
                p = getPass();
                pc = getPassCheck();
                if(un.isEmpty()){
                    msgDialog("ユーザ名を入力してください");
                }
                else if(p.isEmpty()){
                    msgDialog("パスワードを入力してください");
                }
                else if(pc.isEmpty()){
                    msgDialog("確認用パスワードを入力してください");
                    pass.setText("");
                }
                else if(!p.equals(pc)){
                    msgDialog("パスワードと確認用パスワードが一致しません");
                    pass.setText("");
                    passCheck.setText("");
                }
                else{
                    ctrl.processEvent("sendsignup" + sep + un + sep + p);
                    subFrame.dispose();
                }
                break;
            case "logout":
                subFrame.dispose();
                ctrl.processEvent(cmd);
                break;
            case "new":
                drawNameMscDisplay();
                break;
            case "open":
                FileDialog fd = new FileDialog(frame, "既存のプロジェクトを開く", FileDialog.LOAD);
                fd.setVisible(true);
                if(fd.getFile() != null){
                    ctrl.processEvent(cmd + sep + fd.getDirectory() + fd.getFile());
                }
                break;
            case "make":
                if(!titleField.getText().equals("")){
                    ctrl.processEvent(word[0] + sep + titleField.getText());
                    subFrame.dispose();
                }
                else{
                    msgDialog("曲名を入力してください");
                }
                break;
            case "bar":
                if(!ctrl.getPlaying()){
                    ctrl.processEvent(cmd);
                }
                break;
            case "addbar":
                ctrl.processEvent(cmd);
                break;
            case "ctrlinst":
                selCmd = word[1] + sep + word[2] + sep + word[3];
                if(!ctrl.getPlaying()){
                    ctrl.processEvent("bar" + sep + word[1]);
                }
                break;
            case "volume":
                ctrl.processEvent(cmd);
                drawVolumeDisplay();
                break;
            case "sendvolume":
                break;
            case "delete":
                ctrl.processEvent(cmd);
                break;
            case "select":
                drawSelectInstDisplay();
                break;
            case "save":
                ctrl.processEvent(cmd);
                break;
            case "upload":
                ctrl.processEvent(cmd);
                break;
            case "sendupload":
                if(titleField.getText().equals("")){
                    msgDialog("曲名を入力してください");
                }
                else{
                    ctrl.processEvent(word[0] + sep + titleField.getText() + sep + genreCmb.getSelectedItem() + sep + editable.getSelection().getActionCommand());
                    subFrame.dispose();
                }
                break;
            case "play", "pause":
                ctrl.processEvent(cmd);
                break;
            case "piano", "guitar", "bass", "drum", "synth":
                ctrl.processEvent(cmd);
                break;
            case "EDM", "JPOP", "ROCK", "JAZZ":
                genre = cmd;
                ctrl.processEvent("liblist" + sep + inst + sep + genre);
                break;
            case "testplay":
                ctrl.processEvent(cmd);
                break;
            case "testpause":
                ctrl.processEvent(cmd);
                break;
            case "plus":
                ctrl.processEvent(cmd);
                subFrame.dispose();
                break;
            case "search":
                ctrl.processEvent("search" + sep + getSearchInfo());
                break;
            case "listenplay":
                ctrl.processEvent(cmd);
                break;
            case "bookmark":
                ctrl.processEvent(cmd);
                break;
            case "showcomment":
                ctrl.processEvent(cmd);
                break;
            case "sendcomment":
                if(!commentArea.getText().equals("")){
                    ctrl.processEvent(cmd + getSongData() + sep + commentArea.getText());
                }
                break;
            case "backselectlogin":
                drawSelectLoginDisplay();
                break;
            case "backproject":
                drawStartDisplay();
                break;
            case "backselectinst":
                drawSelectInstDisplay();
                break;
            case "backselectgenre":
                ctrl.processEvent("testpause");
                drawSelectGenreDisplay();
                break;
            case "backcompose":
                ctrl.processEvent(cmd);
                break;
            case "backlisten":
                ctrl.processEvent(cmd);
                break;
            case "backsearch":
                ctrl.processEvent(cmd);
                break;
        }
    }

    public void stateChanged(ChangeEvent e){
        int volume = volumeSlider.getValue();
        String cmd = "sendvolume" + sep + selCmd + sep + volume;
        volumeLabel.setText(Integer.toString(volume));
        
        ctrl.processEvent(cmd);
    }

    public void keyReleased(KeyEvent e){
        if(commentArea.getText().length() > 30){
            commentArea.setText(commentArea.getText().substring(0, 30));
        }
    }

    public void keyTyped(KeyEvent e){
        if(commentArea.getText().length() > 30){
            commentArea.setText(commentArea.getText().substring(0, 30));
        }
    }

    public void keyPressed(KeyEvent e){
        if(commentArea.getText().length() > 30){
            commentArea.setText(commentArea.getText().substring(0, 30));
        }
    }

    public void windowOpened(WindowEvent e) {
        
    }

    public void windowClosing(WindowEvent e) {
        ctrl.processEvent("testpause");
    }

    public void windowClosed(WindowEvent e) {
        ctrl.processEvent("testpause");
    }

    public void windowIconified(WindowEvent e) {
        
    }

    public void windowDeiconified(WindowEvent e) {

    }

    public void windowActivated(WindowEvent e) {

    }

    public void windowDeactivated(WindowEvent e) {

    }
}
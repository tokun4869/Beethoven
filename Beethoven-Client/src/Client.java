import java.net.*;
import java.awt.Desktop;
import java.io.*;

import javax.swing.*;

public class Client implements Runnable {
    private String hostName = "localhost";  // IPアドレス(ホスト名)
    private int portNumber = 10000;         // ポート番号
    private Socket socket;                  // ソケット
    private BufferedReader input;           // 入力ストリーム
    private PrintStream output;             // 出力ストリーム
    private Controller ctrl;                // コントローラ
    private boolean isLogined;              // ログインの状態

    public Client(Controller c){
        ctrl = c;
        isLogined = false;
    }

    /* サーバから情報を受信する */
    @Override
    public void run() {
        String str;

        try {
            while ((str = input.readLine()) != null) {
                // コントローラに情報を渡す
                ctrl.processServerInput(str);
            }
        } catch (Exception e) {
            ctrl.processEvent("disconnect");
        }
    }

    /* サーバに接続 */
    public boolean makeSocket() {
        try {
            // ソケット作成
            socket = new Socket(hostName, portNumber);
            // 入出力ストリームを設定
            InputStream is = socket.getInputStream();
            input = new BufferedReader(new InputStreamReader(is));
            OutputStream os = socket.getOutputStream();
            output = new PrintStream(os);
            if(input == null || output == null){
                throw new Exception();
            }
            // サーバから情報を受信する
            Thread thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            ctrl.processEvent("failconnect");
            return false;
        }
        return true;
    }

    /* サーバと接続を切る */
    public void closeSocket() {
        try {
            socket.close();
            setLogin(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*　Google Driveに接続　*/
    public void webConnection(){
        Desktop desktop = Desktop.getDesktop();
        String uriString = "https://drive.google.com/drive/folders/1ebFZGQGfUyw01Z9eW6mRK6QZmUNe902n?usp=sharing";
        try{
            URI uri = new URI(uriString);
            desktop.browse(uri);
        }
        catch(URISyntaxException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /* サーバに送信 */
    public void send(String command) {
        System.out.println(command);
        output.println(command);
        output.flush();
    }

    /*　ログイン情報を与える　*/
    public void setLogin(boolean b){
        isLogined = b;
    }

    /*　ログイン情報を取得する　*/
    public boolean getLogin(){
        return isLogined;
    }

    /*　サーバIPを与える　*/
    public void setServerIP(String ip){
        hostName = ip;
    }

    /*　サーバIPを取得する　*/
    public String getServerIP(){
        return hostName;
    }



    /* main関数 */
    public static void main(String[] args){
        // ルックアンドフィールをクロスプラットフォームに設定
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Controller ctrl = new Controller();
        Client client = new Client(ctrl);
        ctrl.setClient(client);
    }
}
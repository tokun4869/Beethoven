/* ===================== */
/* ライブラリ　　　　　　  */
/* ===================== */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;

import javax.swing.*;



/* ===================== */
/* クラス　　　　　　　　　*/
/* ===================== */

public class Server {

	/* ===================== */
	/* メンバ変数　　　　　　  */
	/* ===================== */

	private final static String sep = "#";
	private int port; //ポート番号
	private Socket socket;
	private String sqlpassword;
	private Connection con = null;
	private PreparedStatement stmt = null;
	private String sql;
	private ServerSocket ss;
	private Administrator admin;


	/* ===================== */
	/* コンストラクタ　　　　  */
	/* ===================== */

	public Server(int port) { //待ち受けポートを引数とする
		this.port = port; //待ち受けポートを渡す
		sqlpassword = JOptionPane.showInputDialog(new JLabel("MySQLのパスワードを入力してください"));
		try{
			/* JDBCドライバのロード */
			Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			/* データベース接続 */
			String url = "jdbc:mysql://localhost:3306/beethoven";
			String user = "root";
			con = DriverManager.getConnection(url, user, sqlpassword);

			sql = "delete from logins";
			stmt = con.prepareStatement(sql);
			stmt.executeUpdate();
		}
		catch(Exception e){
			
		}
		admin = new Administrator("管理画面", con);
	}



	/* ===================== */
	/* メソッド　　　　　　　  */
	/* ===================== */

	/* クライアントと接続する */
	public void acceptClient(){ 
		try {
			System.out.println("サーバが起動しました．");
			ss = new ServerSocket(port); //サーバソケットを用意
			while (true) {
				socket = ss.accept(); //新規接続を受け付け
				ServerClient client = new ServerClient(socket, sqlpassword, con, admin);
				client.start();
			}
		}
		catch (Exception e) {
			admin.systemMessage("ソケット作成時にエラーが発生しました: " + e);
		}
	}

	/*　コネクションを取得する　*/
	public Connection getCon(){
		return con;
	}



	/* ===================== */
	/* 内部クラスServerClient */
	/* ===================== */

	class ServerClient extends Thread{

		/* ===================== */
		/* メンバ変数　　　　　　  */
		/* ===================== */

		private PrintWriter out;
		private InputStreamReader isr;
		private BufferedReader br;
		private String userId;
		private boolean state = false;
		private Connection con = null;
	    private PreparedStatement stmt = null;
	    private ResultSet rs = null;
	    private String[] input;
	    private String sql;
		private Administrator admin;



		/* ===================== */
		/* コンストラクタ　　　　  */
		/* ===================== */

		ServerClient(Socket socket, String password, Connection con, Administrator admin){
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				isr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(isr);
				this.con = con;
				this.admin = admin;
			}
			catch(Exception e){
				admin.systemMessage("サーバクライアント作成時にエラーが発生しました: " + e);
			}
		}



		/* ===================== */
		/* メソッド　　　　　　　  */
		/* ===================== */

		/*　クライアントからのメッセージを受信する　*/
		public void run(){
			try {
				String inputLine;
				while(true) {
					if((inputLine = br.readLine()) != null) {
						admin.systemMessage("[from " + userId + "]" + inputLine);
						input = inputLine.split(sep);
						switch(input[0]) {
							case "sendsignup": //新規登録
								state = registration();
								break;

							case "sendlogin": //ログイン
								state = login();
								break;

							case "sendupload": // アップロード
								if(state) {//ログイン状態の確認
									upload();
								}
								break;

							case "logout":
								logout();
								break;

							case "delete": //楽曲削除
								delete();
								break;

							case "request": // 検索画面
								searchScreen();
								break;						
						}
					}
				}
			}
			catch(IOException e) {
				admin.systemMessage("切断されました: " + e);
				logout();
			}
		}

		public boolean registration() {
			boolean flag = true;
			try {
				sql = "select id from users";
	            stmt = con.prepareStatement(sql);
	            rs = stmt.executeQuery();
				while (rs.next()) {//データベースからidを取得
	                String id = rs.getString("id");
	                if(input[1].equals(id)) {//同じIDが見つかった場合
	                	flag = false;
	                	break;
	                }
	            }

				if(flag) {
					userId = input[1];

					sql = "insert into logins values(?)";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, userId);
					stmt.executeUpdate();

					sql = "insert into users values(?,?,?,?,?,?)";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, userId);
					stmt.setString(2, input[2]);
					stmt.setInt(3, 0);
					stmt.setInt(4, 0);
					stmt.setInt(5, 0);
					stmt.setInt(6, 0);
					stmt.executeUpdate();
					out.println("successregist");//クライアントに成功を送信
					admin.systemMessage("[to " + userId + "]" + "succesregist");
				}
				else {
					out.println("failureregist");//クライアントに失敗を送信
					admin.systemMessage("[to " + userId + "]" + "failureregist");
				}
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
				flag = false;
				out.println("failureregist");//クライアントに失敗を送信
				admin.systemMessage("[to " + userId + "]" + "failureregist");
			}

			return flag;
		}

		public boolean login() {
			boolean flag = false;

			try {
				sql = "select * from users";
				stmt = con.prepareStatement(sql);
	            rs = stmt.executeQuery();
				while (rs.next()) {//データベースからidを取得
	                String id = rs.getString("id");
	                String pass = rs.getString("password");
	                if(input[1].equals(id) && input[2].equals(pass)) {//同じID、パスワードが見つかった場合
	                	flag = true;
	                	break;
	                }
	            }

				if(flag) {
					sql = "select * from logins";
					stmt = con.prepareStatement(sql);
		            rs = stmt.executeQuery();
		            while (rs.next()) {//データベースからidを取得
		                String id = rs.getString("id");
		                if(input[1].equals(id)) {//同じIDが見つかった場合
		                	flag = false;
		                	break;
		                }
		            }
				}

				if(flag) {
					userId = input[1];

					sql = "insert into logins values(?)";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, userId);
					stmt.executeUpdate();

					out.println("successlogin");//クライアントに成功を送信
					admin.systemMessage("[to " + userId + "]" + "successlogin");
				}
				else {
					out.println("failurelogin");//クライアントに失敗を送信
					admin.systemMessage("[to " + userId + "]" + "failurelogin");
				}
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
				out.println("failurelogin");//クライアントに失敗を送信
				admin.systemMessage("[to " + userId + "]" + "failurelogin");
			}

			return flag;
		}

		public void logout() {
			try {
				out.println("finlogout");
				admin.systemMessage("[to " + userId + "]" + "finlogout");

				sql = "delete from logins where id = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userId);
				stmt.executeUpdate();
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void upload() {//音楽のアップロード
			try {
				sql = "select * from musics where composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userId);//id
				stmt.setString(2, input[1]);//title
				rs = stmt.executeQuery();


				boolean editable = false;

				if(Integer.parseInt(input[3]) == 0) {
					editable = false;
				}
				else {
					editable = true;
				}

				String file = "";
				String text;
				while(!(text = br.readLine()).equals("end")) {
					file += text + "\n";
					admin.systemMessage("[from " + userId + "]" + text);
				}
				file += "end";
				admin.systemMessage("[from " + userId + "]" + "end");

				if(rs.next()) {
					sql = "update musics set date = ?, genre = ?, file = ?, editable = ?, bookmarks = ? where composer = ? and title = ?";
					stmt = con.prepareStatement(sql);
					stmt.setDate(1, new Date(System.currentTimeMillis()));//date
					stmt.setString(2, input[2]);//
					stmt.setString(3, file);
					stmt.setBoolean(4, editable);
					stmt.setInt(5, rs.getInt("bookmarks"));
					stmt.setString(6, userId);//id
					stmt.setString(7, input[1]);//title
					stmt.executeUpdate();
				}
				else {
					sql = "insert into musics values (?,?,?,?,?,?,0)";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, userId);//id
					stmt.setString(2, input[1]);//title
					stmt.setDate(3, new Date(System.currentTimeMillis()));//date
					stmt.setString(4, input[2]);//
					stmt.setString(5, file);
					stmt.setBoolean(6, editable);
					stmt.executeUpdate();
				}

				out.println("successupload");
				admin.systemMessage("[to " + userId + "]" + "successupload");
			}
			catch(IOException e){
				admin.systemMessage("切断されました: " + e);
				logout();
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
				out.println("failureupload");
				admin.systemMessage("[to " + userId + "]" + "failureupload");
			}
		}

		public boolean delete() {//音楽の削除
			boolean success = false;
			try {
				sql = "delete from musics where composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userId);
				stmt.setString(2, input[1]);
				int num = stmt.executeUpdate();
				if(num == 1) {
					success = true;
					out.println("successdelete");//クライアントに成功を送信
					admin.systemMessage("[to " + userId + "]" + "successdelete");
				}
				else {
					success = false;
					out.println("failuredelete");//クライアントに失敗を送信
					admin.systemMessage("[to " + userId + "]" + "failuredelete");
				}
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}

			return success;
		}

		public void searchScreen() {//楽曲の検索機能
			try {
				mysong();//ユーザ曲を送信
				recommend();//おすすめを送信
				ranking();//ランキングを送信
				sendBookmark();

				while(true) {
					String inputLine = br.readLine();
					input = inputLine.split(sep);
					admin.systemMessage("[from " + userId + "]" + input);
					switch(input[0]) {
						case "search": //検索
							search();
							break;

						case "play": //楽曲プレイ
							sendMusics(0);
							break;

						case "download": //楽曲ダウンロード
							sendMusics(1);
							break;

						case "sendcomment": //コメント
							sendComment();
							break;
						case "showcomment":
							showComment();
							break;
						case "bookmark": //お気に入り登録
							checkBookmark();
							break;
					}
					if(inputLine.equals("exit")) {//検索画面から離れる
						break;
					}
				}
			}
			catch(IOException e){
				admin.systemMessage("切断されました: " + e);
				logout();
			}
		}

		public void mysong() {
			try {
				sql = "select * from musics where composer = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1,userId);
				rs = stmt.executeQuery();

				String titleList = "";

				int i=0;
				while(rs.next() && i < 30) {
					String composer = rs.getString("composer");
					String title = rs.getString("title");
					Date date = rs.getDate("date");
					String genre = rs.getString("genre");
					boolean editable = rs.getBoolean("editable");
					int bookmarks = rs.getInt("bookmarks");

	    	        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	    	        String postedDate = format.format(date);

	    	        titleList = titleList + sep + title + "\\" + composer + "\\" + genre + "\\" + bookmarks + "\\" + postedDate + "\\" + editable;

	    	        i++;
				}
				out.println("mysong" + titleList);
				admin.systemMessage("[to " + userId + "]" + "mysong" + titleList);
				out.println("searchScreen");
				admin.systemMessage("[to " + userId + "]" + "searchScreen");
			}
			catch(SQLException e){
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void recommend() {
			try {
				sql = "select * from users where id = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1,userId);
				rs = stmt.executeQuery();

				if(rs.next()) {
					int jpop = rs.getInt("JPOP");
					int edm = rs.getInt("EDM");
					int jazz = rs.getInt("JAZZ");
					int rock = rs.getInt("ROCK");


					if(jpop>=edm && jpop>=jazz && jpop>=rock) {//jpop最大
						sql = "select * from musics where genre = 'JPOP' order by bookmarks desc";
					}else if(edm>=jazz && edm>=rock) {//edm最大
						sql = "select * from musics where genre = 'EDM' order by bookmarks desc";
					}else if(jazz>=rock) {//jazz最大
						sql = "select * from musics where genre = 'JAZZ' order by bookmarks desc";
					}else {//rock最大
						sql = "select * from musics where genre = 'ROCK' order by bookmarks desc";
					}

					stmt = con.prepareStatement(sql);
					rs = stmt.executeQuery();

					String titleList = "";

					int i=0;
					while(rs.next() && i < 30) {
						String composer = rs.getString("composer");
						String title = rs.getString("title");
						Date date = rs.getDate("date");
						String genre = rs.getString("genre");
						boolean editable = rs.getBoolean("editable");
						int bookmarks = rs.getInt("bookmarks");

		    	        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		    	        String postedDate = format.format(date);

		    	        titleList = titleList + sep + title + "\\" + composer + "\\" + genre + "\\" + bookmarks + "\\" + postedDate + "\\" + editable;

		    	        i++;
					}
					out.println("recommend" + titleList);
					admin.systemMessage("[to " + userId + "]" + "recommend" + titleList);
					out.println("searchScreen");
					admin.systemMessage("[to " + userId + "]" + "searchScreen");
				}
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void ranking() {
			try {
				sql = "select * from musics order by bookmarks desc";
				stmt = con.prepareStatement(sql);
				rs = stmt.executeQuery();

				String titleList = "";

				int i=0;
				while(rs.next() && i < 30) {
					String composer = rs.getString("composer");
					String title = rs.getString("title");
					Date date = rs.getDate("date");
					String genre = rs.getString("genre");
					boolean editable = rs.getBoolean("editable");
					int bookmarks = rs.getInt("bookmarks");

	    	        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	    	        String postedDate = format.format(date);

	    	        titleList = titleList + sep + title + "\\" + composer + "\\" + genre + "\\" + bookmarks + "\\" + postedDate + "\\" + editable;

	    	        i++;
				}
				out.println("ranking" + titleList);
				admin.systemMessage("[to " + userId + "]" + "ranking" + titleList);
				out.println("searchScreen");
				admin.systemMessage("[to " + userId + "]" + "searchScreen");
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void search() {
			try {
				String sw1, sw2, sw3;
				if(input[1].split("/").length == 2) {
					sw1 = input[1].split("/")[1];
				}
				else {
					sw1 = "";
				}
				if(input[2].split("/").length == 2) {
					sw2 = input[2].split("/")[1];
				}
				else {
					sw2 = "";
				}
				if(input[3].split("/").length == 2) {
					sw3 = input[3].split("/")[1];
				}
				else {
					sw3 = "";
				}
				if(!sw1.isEmpty() && !sw2.isEmpty() && !sw3.isEmpty()) {
					sql = "select * from musics where composer = ? and title = ? and genre = ?";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, sw1);
					stmt.setString(2, sw2);
					stmt.setString(3, sw3);
				}
				else if(!sw1.isEmpty() && !sw2.isEmpty()) {
					sql = "select * from musics where composer = ? and title = ?";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, sw1);
					stmt.setString(2, sw2);
				}
				else if(!sw1.isEmpty() && !sw3.isEmpty()) {
					sql = "select * from musics where composer = ? and genre = ?";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, sw1);
					stmt.setString(2, sw3);

				}
				else if(!sw2.isEmpty() && !sw3.isEmpty()) {
					sql = "select * from musics where title = ? and genre = ?";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, sw2);
					stmt.setString(2, sw3);

				}
				else if(!sw1.isEmpty()) {
					sql = "select * from musics where composer = ?";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, sw1);
				}
				else if(!sw2.isEmpty()) {
					sql = "select * from musics where title = ?";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, sw2);
				}
				else if(!sw3.isEmpty()) {
					sql = "select * from musics where genre = ?";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, sw3);
				}

				if(sw1.isEmpty() && sw2.isEmpty() && sw3.isEmpty()) {
					ranking();
				}
				else {
					rs = stmt.executeQuery();

					String titleList = "";

					while(rs.next()) {
						String composer = rs.getString("composer");
						String title = rs.getString("title");
						Date date = rs.getDate("date");
						String genre = rs.getString("genre");
						boolean editable = rs.getBoolean("editable");
						int bookmarks = rs.getInt("bookmarks");

		    	        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		    	        String postedDate = format.format(date);

		    	        titleList = titleList + sep + title + "\\" + composer + "\\" + genre + "\\" + bookmarks + "\\" + postedDate + "\\" + editable;
					}

					out.println("search" + titleList);
					admin.systemMessage("[to " + userId + "]" + "search" + titleList);
					out.println("searchScreen");
					admin.systemMessage("[to " + userId + "]" + "searchScreen");
				}
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void sendMusics(int i) {
			try {
				sql = "select * from musics where composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, input[1]);
				stmt.setString(2, input[2]);
				rs = stmt.executeQuery();

				String text = "";
				String genre="";
				boolean editable;
				if(rs.next()) {
					genre = rs.getString("genre");
					text = rs.getString("file");
					editable = rs.getBoolean("editable");
					if(editable == false) {
						i = 0;
					}else {
						i = 1;
					}
				}

				String[] word = text.split("\\n");

				if(i == 0) {
					out.println("play");
					admin.systemMessage("[to " + userId + "]" + "play");
					for(int j = 0; j < word.length; j++) {
						out.println(word[j]);
						admin.systemMessage("[to " + userId + "]" + word[j]);
					}
				}else {
					out.println("download");
					admin.systemMessage("[to " + userId + "]" + "download");
					for(int j = 0; j < word.length; j++) {
						out.println(word[j]);
						admin.systemMessage("[to " + userId + "]" + word[j]);
					}
				}

				sql = "select * from users where id = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userId);
				rs=stmt.executeQuery();
				int num=0;
				while(rs.next()) {
					num = rs.getInt(genre);
				}
				num++;

				sql = "update users set " + genre + " = ? where id = ?";
				stmt = con.prepareStatement(sql);
				stmt.setInt(1, num);
				stmt.setString(2, userId);
				stmt.executeUpdate();

			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void showComment() {
			try {
				sql = "select * from comments where composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1,  input[1]);
				stmt.setString(2, input[2]);
				rs = stmt.executeQuery();

				String commenter = "";
				String comment = "";

				String commentList = "comment";

				while(rs.next()) {
					commenter = rs.getString("commenter");
					comment = rs.getString("comment");

					commentList += sep + commenter + "/" + comment;
				}
				out.println(commentList);
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void sendComment() {
			try {
				sql = "insert into comments value(?,?,?,?)";
				stmt = con.prepareStatement(sql);
				stmt.setString(1,  input[1]);
				stmt.setString(2,  input[2]);
				stmt.setString(3, userId);
				stmt.setString(4, input[3]);
				stmt.executeUpdate();

				showComment();
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void checkBookmark() {
			try {
				sql = "select * from bookmarks where id = ? and composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userId);
				stmt.setString(2,  input[1]);
				stmt.setString(3, input[2]);
				rs = stmt.executeQuery();

				if(rs.next()) {
					removeBookmark();
				}
				else {
					addBookmark();
				}
				sendBookmark();
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void addBookmark() {
			try {
				sql = "select bookmarks from musics where composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1,  input[1]);
				stmt.setString(2, input[2]);
				rs = stmt.executeQuery();

				int num=0;
				if(rs.next()) {
					num = rs.getInt("bookmarks");
				}
				num++;

				sql = "update musics set bookmarks = ? where composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setInt(1, num);
				stmt.setString(2,  input[1]);
				stmt.setString(3, input[2]);
				stmt.executeUpdate();


				sql = "insert into bookmarks value(?,?,?)";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userId);
				stmt.setString(2, input[1]);
				stmt.setString(3, input[2]);
				stmt.executeUpdate();
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void removeBookmark() {
			try {
				sql = "select bookmarks from musics where composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1,  input[1]);
				stmt.setString(2, input[2]);
				rs = stmt.executeQuery();

				int num=0;
				if(rs.next()) {
					num = rs.getInt("bookmarks");
				}
				num--;

				sql = "update musics set bookmarks = ? where composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setInt(1, num);
				stmt.setString(2,  input[1]);
				stmt.setString(3, input[2]);
				stmt.executeUpdate();


				sql = "delete from bookmarks where id = ? and composer = ? and title = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userId);
				stmt.setString(2, input[1]);
				stmt.setString(3, input[2]);
				stmt.executeUpdate();
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}

		public void sendBookmark() {
			try {
				sql = "select * from bookmarks where id = ?";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, userId);
				rs = stmt.executeQuery();

				String composer="";
				String title ="";
				String titleList = "";
				while(rs.next()) {
					composer = rs.getString("composer");
					title = rs.getString("title");

					sql ="select * from musics where composer = ? and title = ?";
					stmt = con.prepareStatement(sql);
					stmt.setString(1, composer);
					stmt.setString(2, title);
					ResultSet temprs = stmt.executeQuery();

					if(temprs.next()) {
						Date date = temprs.getDate("date");
						String genre = temprs.getString("genre");
						boolean editable = temprs.getBoolean("editable");
						int bookmarks = temprs.getInt("bookmarks");

		    	        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		    	        String postedDate = format.format(date);

		    	        titleList = titleList + sep + title + "\\" + composer + "\\" + genre + "\\" + bookmarks + "\\" + postedDate + "\\" + editable;
					}
				}
				mysong();
				recommend();
				ranking();
				out.println("bookmarks" + titleList);
				admin.systemMessage("[to " + userId + "]" + "bookmarks" + titleList);
				out.println("searchScreen");
				admin.systemMessage("[to " + userId + "]" + "searchScreen");
			}
			catch(SQLException e) {
				admin.systemMessage("SQLの操作でエラーが発生しました: " + e);
			}
		}
	}

	class Administrator extends JFrame implements ActionListener{
		private Connection con = null;
	    private PreparedStatement stmt = null;
	    private ResultSet rs = null;
		private Color bg = new Color(100, 0, 20);	//背景色
		private JTextField text1;
		private JTextField text2;
		private JTextField text3;
		private JTextField text4;
		private JTextField text5;
		private JTextArea area;

		// コンストラクタ
		public Administrator(String title, Connection con){
			/* ウインドウ表示 */
			super(title);
			setSize(1280, 720);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.con = con;

			/* パネル */
			JPanel mp = new JPanel();
			mp.setLayout(new BoxLayout(mp, BoxLayout.X_AXIS));
			mp.setBackground(bg);
			add(mp);

			/* 左部パネルを作成する */
			JPanel panel1 = new JPanel();
			panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
			panel1.setBackground(bg);

    		/* 音源パック追加ラベルを作成する */
			JLabel label1 = new JLabel("音源パック追加");
			label1.setForeground(Color.WHITE);
			label1.setFont(new Font(Font.SERIF, Font.BOLD, 25));
			label1.setAlignmentX(Component.CENTER_ALIGNMENT);

			/* 接続ボタンを作成する */
			JButton button1 = new JButton("接続");
			button1.setForeground(Color.WHITE);
			button1.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			button1.setAlignmentX(Component.CENTER_ALIGNMENT);
			button1.setMaximumSize(new Dimension(70, 30));
			button1.setPreferredSize(new Dimension(70, 30));
			button1.setContentAreaFilled(false);
			button1.setFocusPainted(false);
			button1.setActionCommand("update");
			button1.addActionListener(this);

			/* 左部パネルにコンポーネントを配置する */
			panel1.add(label1);
			panel1.add(Box.createRigidArea(new Dimension(0, 200)));
			panel1.add(button1);

			/* 中部パネルを作成する */
			JPanel panel2 = new JPanel();
			panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
			panel2.setBackground(bg);

			/* 曲削除ラベルを作成する */
			JLabel label2 = new JLabel("楽曲の削除");
			label2.setForeground(Color.WHITE);
			label2.setFont(new Font(Font.SERIF, Font.BOLD, 25));
			label2.setAlignmentX(Component.CENTER_ALIGNMENT);

			/* 作曲者入力欄を作成する */
			JLabel label3 = new JLabel("作曲者");
			label3.setForeground(Color.WHITE);
			label3.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			label3.setAlignmentX(Component.CENTER_ALIGNMENT);
			text1 = new JTextField();
			text1.setMaximumSize(new Dimension(200, 30));
			text1.setPreferredSize(new Dimension(200, 30));
			text1.setHorizontalAlignment(JTextField.CENTER);

			/* 作曲者入力欄を作成する */
			JLabel label4 = new JLabel("曲名");
			label4.setForeground(Color.WHITE);
			label4.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			label4.setAlignmentX(Component.CENTER_ALIGNMENT);
			text2 = new JTextField();
			text2.setMaximumSize(new Dimension(200, 30));
			text2.setPreferredSize(new Dimension(200, 30));
			text2.setHorizontalAlignment(JTextField.CENTER);

			/* 検索ボタンを作成する */
			JButton button2 = new JButton("検索");
			button2.setForeground(Color.WHITE);
			button2.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			button2.setAlignmentX(Component.CENTER_ALIGNMENT);
			button2.setMaximumSize(new Dimension(70, 30));
			button2.setPreferredSize(new Dimension(70, 30));
			button2.setContentAreaFilled(false);
			button2.setFocusPainted(false);
			button2.setActionCommand("searchmusic");
			button2.addActionListener(this);

			/* 中部パネルにコンポーネントを配置する */
			panel2.add(label2);
			panel2.add(Box.createRigidArea(new Dimension(0, 30)));
			panel2.add(label3);
			panel2.add(text1);
			panel2.add(Box.createRigidArea(new Dimension(0, 30)));
			panel2.add(label4);
			panel2.add(text2);
			panel2.add(Box.createRigidArea(new Dimension(0, 40)));
			panel2.add(button2);

			/* 右部パネルを作成する */
			JPanel panel3 = new JPanel();
			panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
			panel3.setBackground(bg);

			/* コメント削除ラベルを作成する */
			JLabel label5 = new JLabel("コメントの削除");
			label5.setForeground(Color.WHITE);
			label5.setFont(new Font(Font.SERIF, Font.BOLD, 25));
			label5.setAlignmentX(Component.CENTER_ALIGNMENT);

			/* 作曲者入力欄を作成する */
			JLabel label6 = new JLabel("作曲者");
			label6.setForeground(Color.WHITE);
			label6.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			label6.setAlignmentX(Component.CENTER_ALIGNMENT);
			text3 = new JTextField();
			text3.setMaximumSize(new Dimension(200, 30));
			text3.setPreferredSize(new Dimension(200, 30));
			text3.setHorizontalAlignment(JTextField.CENTER);

			/* 作曲者入力欄を作成する */
			JLabel label7 = new JLabel("曲名");
			label7.setForeground(Color.WHITE);
			label7.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			label7.setAlignmentX(Component.CENTER_ALIGNMENT);
			text4 = new JTextField();
			text4.setMaximumSize(new Dimension(200, 30));
			text4.setPreferredSize(new Dimension(200, 30));
			text4.setHorizontalAlignment(JTextField.CENTER);

			/* コメント者入力欄を作成する */
			JLabel label8 = new JLabel("コメント者");
			label8.setForeground(Color.WHITE);
			label8.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			label8.setAlignmentX(Component.CENTER_ALIGNMENT);
			text5 = new JTextField();
			text5.setMaximumSize(new Dimension(200, 30));
			text5.setPreferredSize(new Dimension(200, 30));
			text5.setHorizontalAlignment(JTextField.CENTER);

			/* 検索ボタンを作成する */
			JButton button3 = new JButton("検索");
			button3.setForeground(Color.WHITE);
			button3.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			button3.setAlignmentX(Component.CENTER_ALIGNMENT);
			button3.setMaximumSize(new Dimension(70, 30));
			button3.setPreferredSize(new Dimension(70, 30));
			button3.setContentAreaFilled(false);
			button3.setFocusPainted(false);
			button3.setActionCommand("searchcomment");
			button3.addActionListener(this);

			/* 右部パネルにコンポーネントを配置する */
			panel3.add(label5);
			panel3.add(Box.createRigidArea(new Dimension(0, 10)));
			panel3.add(label6);
			panel3.add(text3);
			panel3.add(Box.createRigidArea(new Dimension(0, 10)));
			panel3.add(label7);
			panel3.add(text4);
			panel3.add(Box.createRigidArea(new Dimension(0, 10)));
			panel3.add(label8);
			panel3.add(text5);
			panel3.add(Box.createRigidArea(new Dimension(0, 25)));
			panel3.add(button3);

			/* コンソールパネルを作成する */
			JPanel panel4 = new JPanel();
			panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));
			panel4.setBackground(bg);

			/*　コンソールを作成する　*/
			JLabel label9 = new JLabel("ログ表示");
			label9.setForeground(Color.WHITE);
			label9.setFont(new Font(Font.SERIF, Font.BOLD, 25));
			label9.setAlignmentX(Component.CENTER_ALIGNMENT);
			area = new JTextArea();
			area.setLineWrap(true);
			JScrollPane scroll = new JScrollPane(area);
			scroll.setMaximumSize(new Dimension(400, 170));
			scroll.setPreferredSize(new Dimension(400, 170));

			/*　初期化ボタンを作成する　*/
			JButton button4 = new JButton("初期化");
			button4.setForeground(Color.WHITE);
			button4.setFont(new Font(Font.SERIF, Font.BOLD, 15));
			button4.setAlignmentX(Component.CENTER_ALIGNMENT);
			button4.setMaximumSize(new Dimension(100, 30));
			button4.setPreferredSize(new Dimension(100, 30));
			button4.setContentAreaFilled(false);
			button4.setFocusPainted(false);
			button4.setActionCommand("reset");
			button4.addActionListener(this);

			/* 中部パネルにコンポーネントを配置する */
			panel4.add(label9);
			panel4.add(Box.createRigidArea(new Dimension(0, 10)));
			panel4.add(scroll);
			panel4.add(Box.createRigidArea(new Dimension(0, 30)));
			panel4.add(button4);

			/*　コンポーネントを配置する　*/
			mp.add(Box.createRigidArea(new Dimension(50, 0)));
			mp.add(panel1);
			mp.add(Box.createRigidArea(new Dimension(50, 0)));
			mp.add(panel2);
			mp.add(Box.createRigidArea(new Dimension(50, 0)));
			mp.add(panel3);
			mp.add(Box.createRigidArea(new Dimension(50, 0)));
			mp.add(panel4);
			mp.add(Box.createRigidArea(new Dimension(50, 0)));

			setVisible(true);
		}

		public void systemMessage(String msg){
			area.setText(area.getText() + System.lineSeparator() + msg);
		}

	    public void actionPerformed(ActionEvent e) {
	    	String cmd = e.getActionCommand();
			String composer;
			String title;
			String commenter;

	    	switch(cmd) {
				case "update":
					Desktop desktop = Desktop.getDesktop();
					String uriString = "https://drive.google.com/drive/folders/1ebFZGQGfUyw01Z9eW6mRK6QZmUNe902n?usp=sharing";
					try{
						URI uri = new URI(uriString);
						desktop.browse(uri);
					}
					catch(URISyntaxException urise){
						systemMessage("URIの記法でエラーが発生しました: " + e);
					}
					catch(IOException ioe){
						systemMessage("ブラウザの接続でエラーが発生しました: " + e);
					}
					break;

				case "searchmusic":
					try{
						composer = text1.getText();
						title = text2.getText();
						text1.setText("");
						text2.setText("");
						if(!composer.isBlank() && !title.isBlank()){
							sql = "select * from musics where composer = ? and title = ?";
							stmt = con.prepareStatement(sql);
							stmt.setString(1, composer);
							stmt.setString(2, title);
							rs = stmt.executeQuery();

							if(rs.next()){
								int option = JOptionPane.showConfirmDialog(this, "該当の曲の削除を実行しますか？", "最終確認", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
								if(option == JOptionPane.YES_OPTION){
									sql = "delete from musics where composer = ? and title = ?";
									stmt = con.prepareStatement(sql);
									stmt.setString(1, composer);
									stmt.setString(2, title);
									stmt.executeUpdate();

									sql = "delete from bookmarks where composer = ? and title = ?";
									stmt = con.prepareStatement(sql);
									stmt.setString(1, composer);
									stmt.setString(2, title);
									stmt.executeUpdate();

									sql = "delete from comments where composer = ? and title = ?";
									stmt = con.prepareStatement(sql);
									stmt.setString(1, composer);
									stmt.setString(2, title);
									stmt.executeUpdate();

									JOptionPane.showMessageDialog(this, new JLabel("削除が完了しました"));
								}
							}
							else{
								JOptionPane.showMessageDialog(this, new JLabel("該当する曲はありません"));
							}
						}
						else{
							JOptionPane.showMessageDialog(this, new JLabel("作曲者と曲名を入力してください"));
						}
					}
					catch(SQLException sqle){
						sqle.printStackTrace();
					}
					break;

				case "searchcomment":
					try{
						composer = text3.getText();
						title = text4.getText();
						commenter = text5.getText();
						text3.setText("");
						text4.setText("");
						text5.setText("");
						if(!composer.isBlank() && !title.isBlank() && !commenter.isBlank()){
							sql = "select * from comments where composer = ? and title = ? and commenter = ?";
							stmt = con.prepareStatement(sql);
							stmt.setString(1, composer);
							stmt.setString(2, title);
							stmt.setString(3, commenter);
							rs = stmt.executeQuery();

							if(rs.next()){
								int option = JOptionPane.showConfirmDialog(this, "該当のコメントの削除を実行しますか？", "最終確認", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
								if(option == JOptionPane.YES_OPTION){
									sql = "delete from comments where composer = ? and title = ? and commenter = ?";
									stmt = con.prepareStatement(sql);
									stmt.setString(1, composer);
									stmt.setString(2, title);
									stmt.setString(3, commenter);
									stmt.executeUpdate();

									JOptionPane.showMessageDialog(this, new JLabel("削除が完了しました"));
								}
							}
							else{
								JOptionPane.showMessageDialog(this, new JLabel("該当するコメントはありません"));
							}
						}
						else{
							JOptionPane.showMessageDialog(this, new JLabel("作曲者と曲名とコメント者を入力してください"));
						}
					}
					catch(SQLException sqle){
						sqle.printStackTrace();
					}
					break;
				
				case "reset":
					int option = JOptionPane.showConfirmDialog(this, "ログの初期化を実行しますか？", "最終確認", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if(option == JOptionPane.YES_OPTION){
						area.setText("");
					}
					break;
	    	}
	    }
	}

	public static void main(String[] args){ //main
		Server server = new Server(10000); //待ち受けポート10000番でサーバオブジェクトを準備
		server.acceptClient(); //クライアント受け入れを開始
	}

}

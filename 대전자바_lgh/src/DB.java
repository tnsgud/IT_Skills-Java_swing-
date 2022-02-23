import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DB {

	static Connection con;
	static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true&allowPublicKeyRetrieval=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "셋팅 실패", "경고", 0);
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './Datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2021전국");
		execute("create database 2021전국 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant insert, select, update, delete on 2021전국.* to user@localhost");
		execute("set global local_infile=1");
		execute("use 2021전국");

		createT("user",
				"u_no int primary key not null auto_increment, u_name varchar(10), u_id varchar(20), u_pw varchar(20), u_img blob");
		createT("perform",
				"p_no int primary key not null auto_increment, pf_no varchar(10), p_name varchar(20), p_place varchar(20), p_price int, p_actor varchar(20), p_date date");
		createT("ticket",
				"t_no int primary key not null auto_increment, u_no int, p_no int, t_seat varchar(50), t_discount varchar(50), foreign key(u_no) references user(u_no), foreign key(p_no) references perform(p_no)");

		try {
			var ps = con.prepareStatement("update user set u_img=? where u_no=?");
			var rs = stmt.executeQuery("select u_no from user");
			while (rs.next()) {
				ps.setObject(1, new FileInputStream(new File("Datafiles/회원사진/" + rs.getInt(1) + ".jpg")));
				ps.setObject(2, rs.getInt(1));
				ps.execute();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JOptionPane.showMessageDialog(null, "셋팅 성공", "정보", 1);
	}
	
	public static void main(String[] args) {
		new DB();
	}

}

package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import tool.Tool;

public class DB implements Tool {
	public static Connection con;
	public static PreparedStatement ps;
	static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true&allowPublicKeyRetreival=true",
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
			e.printStackTrace();
			eMsg("셋팅 실패");
			System.exit(0);
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " lines terminated by '\r\n' "
				+ (t.equals("market") ? "" : "ignore 1 lines"));
	}

	public DB() {
		execute("drop database if exists 2022전국_3");
		execute("create database 2022전국_3 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on 2022전국_3. * to user@localhost");
		execute("set global local_infile=1");
		execute("use 2022전국_3");

		createT("user",
				"u_no int primary key not null auto_increment, u_id varchar(20), u_pw varchar(20), u_name varchar(20), u_birth date, u_money int, u_ox int, u_filter varchar(40), u_img longblob");
		createT("game",
				"g_no int primary key not null auto_increment, g_genre varchar(30), g_name varchar(50), g_age int, g_explan varchar(300), g_price int, g_sale int, g_gd int, g_ox int, g_img longblob");
		createT("cart",
				"c_no int primary key not null auto_increment, u_no int , g_no int, foreign key(u_no) references user(u_no), foreign key(g_no) references game(g_no)");
		createT("item", "i_no int primary key not null auto_increment, g_no int, i_name varchar(20), i_img longblob");
		createT("storage",
				"s_no int primary key not null auto_increment, u_no int, i_no int, foreign key(u_no) references user(u_no), foreign key(i_no) references item(i_no)");
		createT("market",
				"m_no int primary key not null auto_increment, u_no int, s_no int, m_price int, m_ox int, foreign key(u_no) references user(u_no), foreign key(s_no) references storage(s_no)");
		createT("deal",
				"d_no int primary key not null auto_increment, u_no int, m_no int, d_date date, foreign key(u_no) references user(u_no), foreign key(m_no) references market(m_no)");
		createT("library",
				"l_no int primary key not null auto_increment, u_no int, g_no int, l_price int, l_date date, foreign key(u_no) references user(u_no), foreign key(g_no) references game(g_no)");
		createT("review",
				"r_no int primary key not null auto_increment, u_no int, g_no int, r_score int, r_content varchar(150), foreign key(u_no) references user(u_no), foreign key(g_no) references game(g_no)");

		for (var file : new File("./datafiles/회원사진").listFiles()) {
			try {
				execute("update user set u_img = ? where u_no = ?", new FileInputStream(file),
						file.getName().split("\\.")[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				eMsg("셋팅 실패");
				System.exit(0);
			}
		}

		for (var file : new File("./datafiles/게임사진").listFiles()) {
			try {
				execute("update game set g_img = ? where g_no = ?", new FileInputStream(file),
						file.getName().split("\\.")[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				eMsg("셋팅 실패");
				System.exit(0);
			}
		}

		for (var file : new File("./datafiles/아이템사진").listFiles()) {
			try {
				execute("update item set i_img = ? where i_no = ?", new FileInputStream(file),
						file.getName().split("\\.")[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				eMsg("셋팅 실패");
				System.exit(0);
			}
		}
		
		createV();

		iMsg("셋팅 성공");
		System.exit(0);
	}

	public static void main(String[] args) {
		new DB();
	}
}

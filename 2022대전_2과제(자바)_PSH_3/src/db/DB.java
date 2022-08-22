package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			eMsg("셋팅 실패");
			System.exit(0);
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t
				+ " lines terminated by '\r\n' ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2022전국_2");
		execute("create database 2022전국_2 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on 2022전국_2. * to user@localhost");
		execute("set global local_infile=1");
		execute("use 2022전국_2");

		createT("base",
				"b_no int primary key not null auto_increment, division int, b_name varchar(15), b_ermperature int, b_note varchar(100), b_img longblob");
		createT("city", "c_no int primary key not null auto_increment, c_x int, c_y int, c_name varchar(15)");
		createT("town",
				"t_no int primary key not null auto_increment, c_no int, t_x int, t_y int, t_name varchar(15), foreign key(c_no) references city(c_no)");
		createT("user",
				"u_no int primary key not null auto_increment, u_name varchar(15), u_id varchar(15), u_pw int, u_birth varchar(15), division int, t_no int, foreign key(t_no) references town(t_no)");
		createT("farm",
				"f_no int primary key not null auto_increment, u_no int, b_no int, f_amount int, f_quantity int, foreign key(u_no) references user(u_no), foreign key(b_no) references base(b_no)");
		createT("purchase",
				"p_no int primary key not null auto_increment, u_no int, f_no int, p_date varchar(15), p_quantity int, foreign key(u_no) references user(u_no), foreign key(f_no) references farm(f_no)");
		createT("sale",
				"S_no int primary key not null auto_increment, u_no int, f_no int, s_date varchar(15), s_quantity int, foreign key(u_no) references user(u_no), foreign key(f_no) references farm(f_no)");
		createT("weather", "w_day varchar(15), w_temperature int, w_humidity int, w_state int");

		for (var file : new File("./datafiles/농산물").listFiles()) {
			try {
				execute("update base set b_img = ? where b_no = ?", new FileInputStream(file),
						file.getName().split("\\.")[0]);
			} catch (FileNotFoundException e) {
				eMsg("셋팅 실패");
				System.exit(0);
			}
		}

		iMsg("셋팅 성공");
		System.exit(0);
	}

	public static void main(String[] args) {
		new DB();
	}
}

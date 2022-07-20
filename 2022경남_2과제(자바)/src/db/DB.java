package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DB {
	public static Connection con;
	public static Statement stmt;
	public static PreparedStatement ps;
	
	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost/?serverTimezone=UTC&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (Exception e) {
			eMsg();
			System.exit(0);
		}
	}
	
	static void eMsg() {
		JOptionPane.showMessageDialog(null, "셋팅 실패", "경고", 0);
	}
	
	void execute(String sql) {
		System.out.println(sql);
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			eMsg();
			System.exit(0);
		}
	}
	
	void createT(String t, String c) {
		execute("create table "+t+"("+c+")");
		execute("load data local infile './Datafiles/"+t+".txt' into table "+t+" ignore 1 lines");
	}
	
	public DB() {
		execute("drop database if exists school");
		execute("drop user if exists user@localhost");
		execute("create database school");
		execute("create user user@localhost identified by '1234'");
		execute("set global local_infile=1");
		execute("grant select, update, insert, delete on school. * to user@localhost");
		execute("use school");

		createT("student",
				"st_no int primary key not null auto_increment, st_name varchar(10), st_id varchar(15), st_pw varchar(15), st_phone varchar(15), st_email varchar(50), st_school varchar(30), st_grade int, st_date date, st_point int");
		createT("teacher",
				"t_no varchar(4) primary key not null, t_name varchar(10), t_id varchar(15), t_pw varchar(15), t_phone varchar(15), t_email varchar(50), t_date date");
		createT("school", "s_no int primary key not null auto_increment, s_division int, s_name varchar(30)");
		createT("lecture",
				"l_no varchar(6) primary key not null, t_no varchar(4), b_code varchar(100), l_name varchar(150), l_day int, l_price int, foreign key(t_no) references teacher(t_no)");
		createT("book",
				"b_no int primary key not null auto_increment, b_code varchar(5), b_name varchar(200), b_price int");
		createT("payment",
				"p_no int primary key not null auto_increment, st_no int, l_no varchar(6), p_start date, p_end date, p_apply date, p_pay varchar(10), p_price int, p_state varchar(4), foreign key(st_no) references student(st_no), foreign key(l_no) references lecture(l_no)");
		createT("bookcase",
				"c_no int primary key not null auto_increment, st_no int, b_code varchar(5), c_date date, foreign key(st_no) references student(st_no)");
		
		JOptionPane.showMessageDialog(null, "셋팅 완료", "정보", 1);
		System.exit(0);
	}
	
	public static void main(String[] args) {
		new DB();
	}
}

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import tool.Tool;

public class DB implements Tool{
	public static Connection con;
	public static PreparedStatement ps;
	static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true&&allowPublicKeyRetreival=true",
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists airline");
		execute("create database airline default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on airline. * to user@localhost");
		execute("set global local_infile=1");
		execute("use airline");

		createT("member", "m_no int primary key not null auto_increment, m_id varchar(20), m_pw varchar(20), m_name1 varchar(20), m_name2 varchar(20), m_birth date, m_phone varchar(11), m_email varchar(3), m_sex int");
		createT("airport", "a_no int primary key not null auto_increment, a_code varchar(3), a_name varchar(30), a_latitude double, a_longitude double");
		createT("schedule", "s_no int primary key not null auto_increment, s_depart int, s_arrival int, s_time varchar(20), s_price int, foreign key(s_depart) references airport(a_no), foreign key(s_arrival) references airport(a_no)");
		createT("reservation", "r_no int primary key not null auto_increment, m_no int, s_no int, r_date date, r_price int, foreign key(m_no) references member(m_no), foreign key(s_no) references schedule(s_no)");
		createT("companion", "c_no int primary key not null auto_increment, r_no int, c_sex int, c_name varchar(20), c_birth date, c_seat varchar(5), c_division int, foreign key(r_no) references reservation(r_no)");
		createT("mileage", "mi_no int primary key not null auto_increment, m_no int, mi_income int, mi_expense int, foreign key(m_no) references member(m_no)");
		
		createV();
		
		System.exit(0);
	}

	public static void main(String[] args) {
		new DB();
	}
}

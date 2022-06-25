package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	public static Connection con;
	public static PreparedStatement ps;
	static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetreival=true&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		System.out.println(sql);
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void createT(String t, String c) {
		execute("create table `" + t + "`(" + c + ")");
		execute("load data local infile './Datafiles/" + t + ".txt' into table `" + t + "` ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists library");
		execute("create database library default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on libray. * to user@localhost");
		execute("set global local_infile=1");
		execute("use library");

		createT("member",
				"m_no int primary key not null auto_increment, m_name varchar(5), m_id varchar(20), m_pw varchar(20), m_tel varchar(20), m_address varchar(200)");
		createT("book",
				"b_no int primary key not null auto_increment, b_title varchar(50), b_gubun varchar(3), b_writer varchar(20), b_gun int, b_page int, b_date date, b_content varchar(250)");
		createT("borrow",
				"bo_no int primary key not null auto_increment, m_no int, b_no int, bo_num int, bo_sdate date, bo_endate date, bo_renew int, bo_reading int, foreign key(m_no) references member(m_no), foreign key(b_no) references book(b_no)");
		createT("return",
				"r_no int primary key not null auto_increment, bo_no int, r_date date, foreign key(bo_no) references borrow(bo_no)");
		createT("like",
				"l_no int primary key not null auto_increment, m_no int, b_no int, foreign key(m_no) references member(m_no), foreign key(b_no) references book(b_no)");
		createT("reserve",
				"rs_no int primary key not null auto_increment, m_no int, b_no int, rs_num int, rs_date date, rs_redate date, r_receive int, foreign key(m_no) references member(m_no), foreign key(b_no) references book(b_no)");

		execute("insert into borrow values(0, 1, 1, 1, '2022-06-06', '2022-06-13', 0, 10)");
		execute("insert into `return` values(0, 156, '2022-06-20')");
		
//		빌리는 중 => ""
//		반납 => "반납"
//		연체 => "연체"
//		연장 => "연장함"
	}

	public static void main(String[] args) {
		new DB();
	}
}

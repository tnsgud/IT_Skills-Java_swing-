package db;

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
	static Statement st;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetreival=true&allowLoadLocalInfile=true",
					"root", "1234");
			st = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			st.execute(sql);
		} catch (SQLException e) {
			eMsg("셋팅 실패");
			System.exit(0);
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2022지방_2");
		execute("create database 2022지방_2 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on 2022지방_2. * to user@localhost");
		execute("set global local_infile=1");
		execute("use 2022지방_2");

		createT("user",
				"u_no int primary key not null auto_increment, u_name varchar(10), u_id varchar(10), u_pw varchar(15), u_birth varchar(15), u_email varchar(30), u_gender int, u_graduate int, u_address varchar(100), u_img longblob");
		createT("company",
				"c_no int primary key not null auto_increment, c_name varchar(10), c_ceo varchar(10), c_address varchar(100), c_category varchar(15), c_employee int, c_img longblob, c_search int");
		createT("employment",
				"e_no int primary key not null auto_increment, c_no int, e_title varchar(30), e_pay int, e_people int, e_gender int, e_graduate int, foreign key(c_no) references company(c_no)");
		createT("applicant",
				"a_no int primary key not null auto_increment, e_no int, u_no int, a_apply int, foreign key(e_no) references employment(e_no), foreign key(u_no) references user(u_no)");

		var rs = rs("select * from user");
		for (var r : rs) {
			try {
				execute("update user set u_img=? where u_no=?",
						new FileInputStream("./datafiles/회원사진/" + r.get(0) + ".jpg"), r.get(0));
			} catch (FileNotFoundException e) {
				eMsg("셋팅 실패");
				System.exit(0);
			}
		}
		rs = rs("select * from company");
		for (var r : rs) {
			try {
				execute("update company set c_img=? where c_no=?",
						new FileInputStream("./datafiles/기업/" + r.get(1) + "1.jpg"), r.get(0));
			} catch (FileNotFoundException e) {
				eMsg("셋팅 실패");
				System.exit(0);
			}
		}
		
		iMsg("셋팅 성공");
	}

	public static void main(String[] args) {
		new DB();
	}
}

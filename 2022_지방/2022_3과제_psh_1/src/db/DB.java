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
			System.out.println(sql);
			st.execute(sql);
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
		execute("drop database if exists 2022지방_2");
		execute("create database 2022지방_2 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on 2022지방_2. * to user@localhost");
		execute("set global local_infile = 1");
		execute("use 2022지방_2");

		createT("company",
				"c_no int primary key not null auto_increment, c_name varchar(10), c_ceo varchar(10), c_address varchar(100), c_category varchar(15), c_employee int, c_img longblob, c_search int");
		createT("employment",
				"e_no int primary key not null auto_increment, c_no int, e_title varchar(30), e_pay int, e_people int, e_gender int, e_graduate int, foreign key(c_no) references company(c_no)");
		createT("user",
				"u_no int primary key not null auto_increment, u_name varchar(10), u_id varchar(10), u_pw varchar(10), u_birth varchar(15), u_email varchar(30), u_gender int, u_graduate int, u_address varchar(100), u_img longblob");
		createT("applicant",
				"a_no int primary key not null auto_increment, e_no int, u_no int , a_apply int, foreign key(e_no) references employment(e_no), foreign key(u_no) references user(u_no)");

		for (var file : new File("./datafiles/회원사진/").listFiles()) {
			try {
				execute("update user set u_img=? where u_no=?", new FileInputStream(file),
						file.getName().replaceAll("[\\D]", ""));
			} catch (FileNotFoundException | SQLException e) {
				eMsg("셋팅 실패");
				return;
			}
		}

		for (var file : new File("./datafiles/기업/").listFiles()) {
			try {
				if (file.getName().matches(".*[1].*")) {
					execute("update company set c_img=? where c_name = ?", new FileInputStream(file),
							file.getName().substring(0, file.getName().length() - 5));
				}
			} catch (FileNotFoundException | SQLException e) {
				eMsg("셋팅 실패");
				return;
			}
		}

		iMsg("셋팅 성공");
		System.exit(0);
	}

	public static void main(String[] args) {
		new DB();
	}
}

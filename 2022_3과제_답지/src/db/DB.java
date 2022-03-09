package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DB {
	public static Connection con;
	public static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetrieval=true&allowLoadLocalInfile=true",
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
			JOptionPane.showMessageDialog(null, "세팅실패", "경고", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	void table(String t, String... c) {
		execute("create table " + t + "(" + String.join(",", c) + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2022지방_2");
		execute("create database 2022지방_2 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, insert, delete, update on 2022지방_2.* to user@localhost");
		execute("set global local_infile = 1");
		execute("use 2022지방_2");

		table("company", "c_no int primary key not null auto_increment", "c_name varchar(10)", "c_ceo varchar(10)",
				"c_address varchar(100)", "c_category varchar(15)", "c_employee int", "c_img longblob", "c_search int");
		table("user", "u_no int primary key not null auto_increment", "u_name varchar(10)", "u_id varchar(10)",
				"u_pw varchar(15)", "u_birth varchar(15)", "u_email varchar(30)", "u_gender int", "u_graduate int",
				"u_address varchar(100)", "u_img longblob");
		table("employment", "e_no int primary key not null auto_increment", "c_no int", "e_title varchar(30)",
				"e_pay int", "e_people int", "e_gender int", "e_graduate int",
				"foreign key(c_no) references company(c_no)");
		table("applicant", "a_no int primary key not null auto_increment", "e_no int", "u_no int", "a_apply int",
				"foreign key(e_no) references employment(e_no)", "foreign key(u_no) references user(u_no)");

		for (var pic : new File("./datafiles/회원사진").listFiles()) {
			try {
				var pst = con.prepareStatement("update user set u_img = ? where u_no = ?");
				try {
					pst.setBinaryStream(1, new FileInputStream(pic));
					pst.setObject(2, pic.getName().replaceAll("[\\D]", ""));
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "세팅실패", "경고", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
				pst.execute();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "세팅실패", "경고", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}

		for (var pic : new File("./datafiles/기업").listFiles()) {
			if (pic.getName().matches(".*[1].*")) {
				try {
					var pst = con.prepareStatement("update company set c_img = ? where c_name like ?");
					try {
						pst.setBinaryStream(1, new FileInputStream(pic));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pst.setObject(2, "%" + pic.getName().substring(0, pic.getName().length() - 5) + "%");
					pst.execute();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, "세팅실패", "경고", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			}
		}

		JOptionPane.showMessageDialog(null, "세팅성공", "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String[] args) {
		new DB();
	}
}

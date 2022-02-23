package db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import tool.Tool;

public class DB implements Tool{
	public static Connection con;
	public static PreparedStatement ps;
	static Statement st;
	
	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetreival=true&allowLoadLocalInfile=true", "root", "1234");
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
			e.printStackTrace();
		}
	}
	
	void createT(String t,String c) {
		execute("create table "+t+"("+c+")");
		execute("load data local infile './Datafiles/"+t+".txt' into table "+t+" ignore 1 lines");
	}
	
	public DB() {
		execute("drop database if exists 2021전국");
		execute("create database 2021전국 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on 2021전국. * to user@localhost");
		execute("set global local_infile=1");
		execute("use 2021전국");
		
		createT("user", "u_no int primary key not null auto_increment, u_name varchar(10), u_id varchar(20), u_pw varchar(20), u_img blob");
		createT("perform", "p_no int primary key not null auto_increment, pf_no varchar(10), p_name varchar(20), p_place varchar(20), p_price int, p_actor varchar(20), p_date date");
		createT("ticket", "t_no int primary key not null auto_increment, u_no int, p_no int, t_seat varchar(50), t_discount varchar(50), foreign key(u_no) references user(u_no), foreign key(p_no) references perform(p_no)");
		
		var rs = rs("select u_no from user");
		try {
			while(rs.next()) {
				execute("update user set u_img=? where u_no=?", new FileInputStream("./Datafiles/회원사진/"+rs.getInt(1)+".jpg"), rs.getInt(1));
			}
		} catch (SQLException | FileNotFoundException e) {
			eMsg("셋팅 실패");
			e.printStackTrace();
		}
		
		iMsg("셋팅 성공");
	}
	
	public static void main(String[] args) {
		new DB();
	}
}

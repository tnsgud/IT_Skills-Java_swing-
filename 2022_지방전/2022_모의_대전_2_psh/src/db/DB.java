package db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import tool.Tool;
import view.BaseFrame;

public class DB implements Tool{
	static Connection con;
	static Statement stmt;
	static PreparedStatement ps;

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

	public static ArrayList<ArrayList<String>> getArray(String sql, Object... obj) {
		var array = new ArrayList<ArrayList<String>>();
		var rs = rs(sql, obj);
		int i = 0;
		try {
			while(rs.next()) {
				array.add(new ArrayList<String>());
				for (int j = 0; j < rs.getMetaData().getColumnCount(); j++) {
					array.get(i).add(rs.getString(j+1));
				}
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return array;
	}
	
	public static void execute(String sql, Object... obj) {
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				ps.setObject(i + 1, obj[i]);
			}
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ResultSet rs(String sql, Object... obj) {
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				ps.setObject(i + 1, obj[i]);
			}
			return ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getOne(String sql, Object... obj) {
		try {
			var rs = rs(sql, obj);
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}

		return "";
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
		execute("load data local infile './Datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2021전국");
		execute("create database 2021전국 default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, delete, insert on 2021전국. *to user@localhost");
		execute("set global local_infile=1");
		execute("use 2021전국");

		createT("user",
				"u_no int primary key not null auto_increment, u_name varchar(10), u_id varchar(20), u_pw varchar(20), u_img blob");
		createT("perform",
				"p_no int primary key not null auto_increment, pf_no varchar(10), p_name varchar(20), p_place varchar(20), p_price int, p_actor varchar(20), p_date date");
		createT("ticket",
				"t_no int primary key not null auto_increment, u_no int, p_no int, t_seat varchar(50), t_discount varchar(50), foreign key(u_no) references user(u_no), foreign key(p_no) references perform(p_no)");

		var rs = rs("select * from user");
		try {
			while (rs.next()) {
				execute("update user set u_img=? where u_no=?", new FileInputStream("./Datafiles/회원사진/"+rs.getString(1)+".jpg"), rs.getString(1));
			}
			
			iMsg("셋팅 완료");
		} catch (SQLException | FileNotFoundException e) {
			e.printStackTrace();
			eMsg("셋팅 실패");
		}
	}

	public static void main(String[] args) {
		new DB();
	}
}

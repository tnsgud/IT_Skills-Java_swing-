package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	public static Connection con;
	static Statement stmt;
	static PreparedStatement ps;

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

	public static ResultSet rs(String sql, Object... objs) {
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				ps.setObject(i + 1, objs[i]);
			}
			return ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static String getOne(String sql, Object... objs) {
		try {
			var rs = rs(sql, objs);
			if (rs.next()) {
				System.out.println(rs.getString(1));
				return rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static void execute(String sql, Object... objs) {
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				ps.setObject(i + 1, objs[i]);
			}
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static <T> T getModel(Class<T> cls, String sql, Object...objs) {
		try {
			T result = cls.getDeclaredConstructor().newInstance();
			var rs = rs(sql, objs);
			if(rs.next()) {
				setValue(result, rs);
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private static void setValue(Object result, ResultSet rs) throws SQLException, IllegalArgumentException, IllegalAccessException {
		var fleids = result.getClass().getFields();
		for (var f : fleids) {
			var data = rs.getString("u_"+f.getName());
			
			f.set(result, data);
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
		execute("drop database if exists adventure");
		execute("create database adventure default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on adventure. *to user@localhost");
		execute("set global local_infile=1");
		execute("use adventure");

		createT("user",
				"u_no int primary key not null auto_increment, u_name varchar(10), u_id varchar(15), u_pw varchar(10), u_height int, u_date date, u_age int, u_disable int");
		createT("ride",
				"r_no int primary key not null auto_increment, r_name varchar(15), r_floor varchar(2), r_max int, r_height varchar(15), r_old varchar(15), r_money int, r_disable int, r_explation varchar(150), r_img longBlob");
		createT("ticket",
				"t_no int primary key not null auto_increment, u_no int, t_date varchar(10), r_no int, t_magicpass int, foreign key(u_no) references user(u_no), foreign key(r_no) references ride(r_no)");

		var files = new File("./datafiles/이미지/").listFiles();
		for (var f : files) {
			try {
				execute("update ride set r_img=? where r_name=?", new FileInputStream(f),
						f.getName().replace(".jpg", ""));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new DB();
	}
}

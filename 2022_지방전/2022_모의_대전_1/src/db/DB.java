package db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import ui.BaseFrame;

public class DB {
	static Connection con;
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
		}
		return null;
	}

	public static void execute(String sql, Object... obj) {
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				ps.setObject(i + 1, obj[i]);
			}
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static <T> T getModel(Class<T> cls, String sql, Object...obj) {
		var rs = rs(sql, obj);
		
		try {
			var result = cls.getDeclaredConstructor().newInstance();
			if(rs.next()) {
				Arrays.stream(result.getClass().getFields()).forEach(f-> {
					try {
						f.set(result, rs.getString(f.getName()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> ArrayList<T> getModelList(Class<T> cls, String sql, Object...obj) {
		try {
			var result = new ArrayList<T>();
			var rs = rs(sql, obj);
			while(rs.next()) {
				var cons = cls.getDeclaredConstructor().newInstance();
				Arrays.stream(cons.getClass().getFields()).forEach(f-> {
					try {
						f.set(cons, rs.getString(f.getName()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				
				result.add(cons);
			}
			
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			BaseFrame.eMsg("셋팅 실패");
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './Datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists 2021전국");
		execute("create database 2021전국 default character set utf8");
		execute("drop user if exists user@localhsot");
		execute("create user user@localhsot identified by '1234'");
		execute("grant select, update, delete, insert on 2021전국. *to user@localhost");
		execute("set global local_infile=1");
		execute("use 2021전국");

		createT("user",
				"u_no int primary key not null auto_increment, u_name varchar(10), u_id varchar(20), u_pw varchar(20), u_img Blob");
		createT("perform",
				"p_no int primary key not null auto_increment, pf_no varchar(10), p_name varchar(20), p_place varchar(20), p_price int, p_actor varchar(20), p_date date");
		createT("ticket",
				"t_no int primary key not null auto_increment, u_no int, p_no int, t_seat varchar(50), t_discount varchar(50)");

		try {
			addImage();
		} catch (FileNotFoundException | SQLException e) {
			BaseFrame.eMsg("셋팅 실패");
			e.printStackTrace();
		}

		BaseFrame.iMsg("셋팅 성공");
	}

	private void addImage() throws FileNotFoundException, SQLException {
		var rs = rs("select * from user");
		while (rs.next()) {
			FileInputStream fis = new FileInputStream("./Datafiles/회원사진/" + rs.getString(1) + ".jpg");
			execute("update user set u_img=? where u_no=?", fis, rs.getInt(1));
		}
	}

	public static void main(String[] args) {
		new DB();
	}
}

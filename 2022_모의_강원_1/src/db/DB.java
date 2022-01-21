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
import java.util.ArrayList;
import java.util.HashMap;

public class DB {
	static Connection con;
	static Statement stmt;
	static PreparedStatement ps;
	static String cascade = "on update cascade on delete cascade";

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
		}
		return null;
	}

	public static ArrayList<ArrayList<String>> queryResult(String sql, Object... obj) {
		try {
			var rs = rs(sql, obj);
			var result = new ArrayList<ArrayList<String>>();
			while (rs.next()) {
				var row = new ArrayList<String>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					row.add(rs.getString(i + 1));
				}
				result.add(row);
			}
			
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
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
		execute("load data local infile './지급파일/" + t + ".csv' into table " + t
				+ " fields terminated by ',' lines terminated by '\r\n' ignore 1 lines");
	}

	public DB() throws SQLException, FileNotFoundException {
		execute("drop database if exists busticketbooking");
		execute("create database busticketbooking default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on busticketbooking. *to user@localhost");
		execute("set global local_infile = 1");
		execute("use busticketbooking");

		createT("user",
				"no int primary key not null auto_increment, id varchar(50), pwd varchar(50), name varchar(50), email varchar(50), point int");
		createT("location", "no int primary key not null auto_increment, name varchar(50)");
		createT("location2",
				"no int primary key not null auto_increment, name varchar(50), location_no int, foreign key(location_no) references location(no) "
						+ cascade);
		createT("schedule",
				"no int primary key not null auto_increment, departure_location2_no int, arrival_location2_no int, `date` datetime, elapsed_time time, foreign key(departure_location2_no) references location2(no) "
						+ cascade + ", foreign key(arrival_location2_no) references location2(no) " + cascade);
		createT("reservation",
				"no int primary key not null auto_increment, user_no int, schedule_no int, foreign key(user_no) references user(no) "
						+ cascade + ", foreign key(schedule_no) references schedule(no) " + cascade);
		createT("recommend",
				"no int primary key not null auto_increment, location_no int, foreign key(location_no) references location(no)"
						+ cascade);
		createT("recommend_info", "recommend_no int, title varchar(50), descrption varchar(1000), img longblob");

		var map = new HashMap<String, String>();
		map.put("부산", "busan");
		map.put("강원도", "gangwondo");
		map.put("광주", "gyeongju");
		map.put("전라남도", "Jeollanam-do");
		map.put("서울", "seoul");

		var rs = rs(
				"select title, l.name, ri.recommend_no from recommend_info ri, recommend r, location l where ri.recommend_no=r.no and r.location_no = l.no");
		while (rs.next()) {
			execute("update recommend_info set img =? where title=? and recommend_no=?",
					new FileInputStream(new File(
							"./지급파일/images/recommend/" + map.get(rs.getString(2)) + "/" + rs.getInt(1) + ".jpg")),
					rs.getInt(1), rs.getInt(3));
		}
	}

	public static void main(String[] args) {
		try {
			new DB();
		} catch (SQLException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

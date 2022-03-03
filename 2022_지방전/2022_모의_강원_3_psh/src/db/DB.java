package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.stream.Stream;

import tool.Tool;

public class DB implements Tool {
	public static Connection con;
	static Statement stmt;
	public static PreparedStatement ps;

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

	void createT(String t, String c) {
		var cols = Stream.of(c.split(",")).map(a -> a + (a.contains("foreign") ? " on update cascade on delete cascade"
				: a.equals("description") ? "" : " not null")).toArray(String[]::new);
		execute("create table " + t + "(" + String.join(",", cols) + ")");
		execute("load data local infile './지급파일/" + t + ".csv' into table " + t
				+ " fields terminated by ',' lines terminated by '\r\n' ignore 1 lines");
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DB() {
		execute("drop database if exists busticketbooking");
		execute("create database busticketbooking default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on busticketbooking. *to user@localhost");
		execute("set global local_infile = 1");
		execute("use busticketbooking");

		createT("user",
				"no int primary key auto_increment,id varchar(50),pwd varchar(50),name varchar(50),email varchar(50),point int");
		createT("location", "no int primary key auto_increment,name varchar(50)");
		createT("location2",
				"no int primary key auto_increment,name varchar(50),location_no int,foreign key(location_no) references location(no)");
		createT("schedule",
				"no int primary key auto_increment,departure_location2_no int,arrival_location2_no int, date datetime, elapsed_time time,foreign key(departure_location2_no) references location2(no),foreign key(arrival_location2_no) references location2(no)");
		createT("reservation",
				"no int primary key auto_increment,user_no int,schedule_no int,foreign key(user_no) references user(no),foreign key(schedule_no) references schedule(no)");
		createT("recommend",
				"no int primary key auto_increment,location_no int,foreign key(location_no) references location(no)");
		createT("recommend_info",
				"recommend_no int,title varchar(50),description varchar(1000),img longblob,foreign key(recommend_no) references recommend(no)");

		var map = new HashMap<String, String>();
		map.put("부산", "busan");
		map.put("강원도", "gangwondo");
		map.put("광주", "gyeongju");
		map.put("전라남도", "Jeollanam-do");
		map.put("서울", "seoul");

		try {
			var rs = rs(
					"select title, l.name, ri.recommend_no from recommend_info ri, recommend r, location l where ri.recommend_no = r.no and r.location_no = l.no");
			while (rs.next()) {
				execute("update recommend_info set img=? where title=? and recommend_no=?",
						new FileInputStream(new File("./지급파일/images/recommend/" + map.get(rs.getString(2)) + "/"
								+ rs.getString(1) + ".jpg")), rs.getInt(1), rs.getInt(3));
			}
		} catch (SQLException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		createV();
	}

	public static void main(String[] args) {
		new DB();
	}
}

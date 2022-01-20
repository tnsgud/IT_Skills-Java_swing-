package db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;

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

	public static void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println("failed ㅋㅋ");
			e.printStackTrace();
		}
	}

	public static void createT(String tname, String... col) {
		String sql = "create table " + tname + "(" + String.join(",", col) + ")";

		try {
			stmt.execute(sql);
			stmt.execute("load data local infile './지급파일/" + tname + ".csv' into table " + tname
					+ " fields terminated by ',' lines terminated by '\n' ignore 1 lines");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public DB() {
		execute("drop database if exists busticketbooking");
		execute("create database busticketbooking default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, insert, update, delete on busticketbooking.* to user@localhost");
		execute("set global local_infile=1");
		execute("use busticketbooking");

		createT("user", "no int primary key not null auto_increment", "id varchar(50) not null",
				"pwd varchar(50) not null", "name varchar(50) not null", "email varchar(50) not null",
				"point int not null");

		createT("location", "no int primary key not null auto_increment ", "name varchar(50) not null");

		createT("location2", "no int primary key not null auto_increment", "name varchar(50)", "location_no int",
				"foreign key(location_no) references location(no) on delete cascade on update cascade");

		createT("schedule", "no int primary key not null auto_increment", "departure_location2_no int not null",
				"arrival_location2_no int not null", "date datetime not null", "elapsed_time time not null",
				"foreign key(departure_location2_no) references location2(no)",
				"foreign key(arrival_location2_no) references location2(no) on delete cascade on update cascade");

		createT("reservation", "no int primary key not null auto_increment", "user_no int not null",
				"schedule_no int not null", "foreign key(user_no) references user(no) on delete cascade on update cascade",
				"foreign key(schedule_no) references schedule(no) on delete cascade on update cascade");

		createT("recommend", "no int primary key not null auto_increment, location_no int not null",
				"foreign key(location_no) references location(no)");

		createT("recommend_info", "recommend_no int not null", "title varchar(50) not null",
				"description varchar(1000)", "img longblob not null", "primary key(title, recommend_no)");

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("1", "busan");
		map.put("2", "gangwondo");
		map.put("3", "gyeongju");
		map.put("4", "Jeollanam-do");
		map.put("5", "seoul");

		try {
			var rs = stmt.executeQuery("select * from recommend_info");
			while (rs.next()) {
				var pst = con.prepareStatement("update recommend_info set img = ? where recommend_no = "
						+ rs.getString(1) + " and title = " + rs.getString(2));
				try {
					pst.setBinaryStream(1, new FileInputStream(
							"./지급파일/images/recommend/" + map.get(rs.getString(1)) + "/" + rs.getString(2) + ".jpg"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pst.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new DB();
	}
}

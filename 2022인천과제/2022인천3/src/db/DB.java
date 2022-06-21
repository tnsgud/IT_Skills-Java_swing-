package db;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

import tool.Tool;

public class DB implements Tool {
	public static Connection con;
	public static Statement stmt;
	public static PreparedStatement ps;
	static SystemTray tray = SystemTray.getSystemTray();
	static TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/covid.png"));

	static {
		try {
			tray.add(icon);
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetreival=true&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			emsg();
		}
	}

	void execute(String sql) {
		try {
			System.out.println(sql);
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			emsg();
		}
	}

	void createT(String t, String c) {
		c = String.join(",",
				Stream.of(c.split(","))
						.map(col -> col += col.contains("fore") ? " on delete cascade on update cascade" : "")
						.toArray(String[]::new));
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	static void emsg() {
		icon.displayMessage("DB 셋팅", "DB 셋팅 실패", MessageType.ERROR);
		System.exit(0);
	}

	public DB() {
		execute("drop database if exists covid");
		execute("create database covid default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on covid. * to user@localhost");
		execute("set global local_infile=1");
		execute("use covid");

		createT("building",
				"no int primary key not null auto_increment, name varchar(20), open time, close time, info text, type int, x int, y int, img longblob");
		createT("connection",
				"node1 int, node2 int, name text, foreign key(node1) references building(no), foreign key(node2) references building(no)");
		createT("vaccine", "no int primary key not null auto_increment, name varchar(20), price int");
		createT("user",
				"no int primary key not null auto_increment, name varchar(20), id varchar(10), pw varchar(20), phone varchar(30), birth date, building int, foreign key(building) references building(no)");
		createT("rate",
				"no int primary key not null auto_increment, building int, rate int, user int, review text, foreign key(building) references building(no), foreign key(user) references user(no)");
		createT("purchase",
				"no int primary key not null auto_increment, user int, date datetime, building int, vaccine int, shot int, foreign key(building) references building(no), foreign key(user) references user(no), foreign key(vaccine) references vaccine(no)");

		for (var rs : getRows("select no from building where type < 2")) {
			try {
				execute("update building set img=? where no=?",
						new FileInputStream("./datafiles/건물사진/" + rs.get(0) + ".jpg"), rs.get(0));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				emsg();
			}
		}

		icon.displayMessage("DB 셋팅", "DB 셋팅 성공", MessageType.INFO);
		System.exit(0);
	}

	public static void main(String[] args) {
		new DB();
	}
}

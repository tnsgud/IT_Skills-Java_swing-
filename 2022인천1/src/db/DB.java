package db;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
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
	public static Statement stmt;
	public static PreparedStatement ps;

	static SystemTray tray = SystemTray.getSystemTray();
	static TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/covid.png"));

	static {
		icon.setImageAutoSize(true);
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
			eMsg();
		}
	}

	static void eMsg() {
		icon.displayMessage("DB 셋팅", "DB 셋팅 성공", MessageType.ERROR);
		System.exit(0);
	}

	void execute(String sql) {
		try {
			System.out.println(sql);
			stmt.execute(sql);
		} catch (SQLException e) {
			eMsg();
			System.exit(0);
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists covid");
		execute("create database covid default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant insert, select, update, delete on covid. * to user@localhost");
		execute("set global local_infile=1");
		execute("use covid");

		createT("vaccine", "no int primary key not null auto_increment, name varchar(20), price int");
		createT("building",
				"no int primary key not null auto_increment, name varchar(20), open time, close time, info text, type int ,x int,y int, img longblob");
		createT("user",
				"no int primary key not null auto_increment, name varchar(20), id varchar(10), pw varchar(20), phone varchar(30), resident varchar(15), building int, foreign key(building) references building(no)");
		createT("connection",
				"node1 int, node2 int, name varchar(10), foreign key(node1) references building(no), foreign key(node2) references building(no)");
		createT("purchase",
				"no int primary key not null auto_increment, user int, date datetime, building int, vaccine int, shot int, foreign key(user) references user(no)");
		createT("rate",
				"no int primary key not null auto_increment, rate int, building int, user int, review text, foreign key(building) references building(no), foreign key(user) references user(no)");

		for (var rs : rs("select no from building where type = 0 or type = 1")) {
			try {
				execute("update building set img = ? where no=?",
						new FileInputStream(new File("./datafiles/건물사진/" + rs.get(0) + ".jpg")), rs.get(0));
			} catch (FileNotFoundException e) {
				eMsg();
			}
		}

		icon.displayMessage("DB 셋팅", "DB 셋팅 성공", MessageType.INFO);
	}

	public static void main(String[] args) {
		new DB();
	}
}

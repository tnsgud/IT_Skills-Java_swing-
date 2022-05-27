package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

public class DB {
	public static Connection con;
	public static Statement stmt;
	public static PreparedStatement ps;

	String cascade = " on delete cascade on update cascade";

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

	void execute(String sql) {
		try {
			System.out.println(sql);
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void createT(String t, String c) {
		c = String.join(",",
				Stream.of(c.split(",")).map(a -> a + (a.contains("foreign") ? cascade : "")).toArray(String[]::new));

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

		createT("vaccine", "no int primary key not null auto_increment, name varchar(20), prince int");
		createT("building",
				"no int primary key not null auto_increment, name varchar(20), open time, close time, info text, type int ,x int,y int, img longblob");
		createT("user",
				"no int primary key not null auto_increment, name varchar(20), id varchar(10), pw varchar(20), phone varchar(30), resident varchar(15), building int, foreign key(building) references building(no)");
		createT("connection",
				"node1 int, node2 int, foreign key(node1) references building(no), foreign key(node2) references building(no)");
		createT("purchase",
				"no int primary key not null auto_increment, user int, `when` datetime, building int, price int, vaccine int, foreign key(user) references user(no)");
		createT("infection",
				"no int primary key not null auto_increment, building varchar(10), date datetime, gender varchar(1), age int");
		createT("rate",
				"no int primary key not null auto_increment, building int, rate int, user int, review text, foreign key(building) references building(no), foreign key(user) references user(no)");
	}

	public static void main(String[] args) {
		new DB();
	}
}

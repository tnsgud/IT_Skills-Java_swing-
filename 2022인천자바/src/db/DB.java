package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	public static Connection con;
	public static Statement stmt;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetrieval=true&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void createT(String t, String c) {
		execute("create table " + t + "(" + c + ")");
		execute("load data local infile './지급자료/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists covid");
		execute("create database covid default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant insert,select,update,delete on covid.* to user@localhost");
		execute("set global local_infile=1");
		execute("use covid");

		createT("Region", "no int primary key not null auto_increment, name varchar(20)");
		createT("Point", "no int primary key not null auto_increment, x int, y int, type int");
		createT("User",
				"no int primary key not null auto_increment, name varchar(20), id varchar(15), pw varchar(15), region int, birth date, resident varchar(20), point int, vaccine boolean, foreign key(region) references Region(no), foreign key(point) references Point(no)");
		createT("Vaccine", "no int primary key not null auto_increment, name varchar(20), price int");
		createT("SearchLog", "no int primary key not null auto_increment, name varchar(20), count int");
		createT("Infection", "no int primary key not null auto_increment, `when` datetime, region int, foreign key (region) references Region(no)");
		createT("Institution",
				"no int primary key not null auto_increment, name varchar(50), point int, region int, vaccine int, open time, close time, type int, foreign key(point) references Point(no), foreign key(region) references Region(no), foreign key(vaccine) references Vaccine(no)");
		createT("Purchase",
				"no int primary key not null auto_increment, user int, `when` datetime, institution int, price int, foreign key(user) references User(no), foreign key(institution) references Institution(no)");
	}

	public static void main(String[] args) {
		new DB();
	}
}

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
		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		execute("drop database if exists covid");
		execute("create database covid default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant insert,select,update,delete on covid.* to user@localhost");
		execute("set global local_infile=1");
		execute("use covid");

		createT("Point", "no int primary key not null auto_increment, x int, y int");
		createT("User",
				"no int primary key not null auto_increment, name varchar(20), id varchar(15), pw varchar(15),  birth date, phone varchar(30), resident varchar(20), point int, vaccine boolean, foreign key(point) references Point(no)");
		createT("Vaccine", "no int primary key not null auto_increment, name varchar(20), price int");
		createT("Building",
				"no int primary key not null auto_increment, name varchar(50), point int, foreign key(point) references point(no)");
		createT("Building_Info", "no int primary key not null auto_increment, Vaccine int, Open time, Close time, Building int, foreign key(Vaccine) references Vaccine(no), foreign key(Building) references Building(no)");
		createT("Connection", "node1 int , node2 int, foreign key(node1) references Point(no), foreign key(node2) references Point(no)");
		createT("Purchase",
				"no int primary key not null auto_increment, user int, `when` datetime, building int, price int, foreign key(user) references User(no), foreign key(building) references building(no)");
	}

	public static void main(String[] args) {
		new DB();
	}
}

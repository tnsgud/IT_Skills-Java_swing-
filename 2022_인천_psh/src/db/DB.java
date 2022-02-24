package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	public static Connection con;
	public static PreparedStatement ps;
	static Statement st;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetreval=ture&allowLoadLocalInfile=true",
					"root", "1234");
			st = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void execute(String sql) {
		try {
			st.execute(sql);
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
		execute("drop database if exists covid");
		execute("create database covid default character set utf8");
		execute("drop user if exists user@localhost ");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, delete on cvoid. * to user@localhost");
		execute("set global local_infile=1");
		execute("use covid");

		createT("Point", "no int primary key not null auto_increment, x int, y int");
		createT("Vaccine", "no int primary key not null auto_increment,name varchar(50),price int");
		createT("User",
				"no int primary key not null auto_increment, name varchar(50),id varchar(10),pw varchar(20),region int,birth date,phone varchar(30),resident varchar(20),point int,foreign key(point) references point(no)");
		createT("Building", "no int primary key not null auto_increment,name varchar(20),point int,foreign key(point) references point(no)");
		createT("Building_Info", "no int primary key not null auto_increment,vaccine int,open time,close time,building int,foreign key(vaccine) references vaccine(no),foreign key(building) references building(no)");
		createT("Connection", "node1 int, node2 int, foreign key(node1) references point(no), foreign key(node2) references point(no)");
		createT("purchase", "no int primary key not null auto_increment,user int,`when` datetime,building int, foreign key(user) references user(no),foreign key(building) references building(no)");
	}

	public static void main(String[] args) {
		new DB();
	}
}
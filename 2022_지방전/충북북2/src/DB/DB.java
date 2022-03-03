package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	
	public static Connection con;
	public static Statement stmt;
	
	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost?serverTimezone=UTC&allowLoadLocalInfile=true&allowPublicKeyRetrieval=true", "root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void execute(String sql ) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void createT(String t, String c) {
		execute("create table "+t+"("+c+")");
		execute("load data local infile './지급자료/"+t+".txt' into table "+t+" ignore 1 lines");
	}
	
	public DB() {
		execute("drop database if exists Music");
		execute("create database Music default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, delete, update, insert on table Music. *to user@localhost ");
		execute("set global local_infile=1");
		execute("use Music");
		
		createT("region", "serial int primary key not null auto_increment, name varchar(10)");
		createT("category", "serial int primary key not null auto_increment, name varchar(10)");
		createT("artist", "serial int primary key not null auto_increment, name varchar(30), about text");
		createT("album", "serial int primary key not null auto_increment, name varchar(50), artist int, category int, `release` date, foreign key(artist) references artist(serial), foreign key(category) references category(serial)");
		createT("song", "serial int primary key not null auto_increment, name varchar(50), length time, album int, titlesong int, foreign key(album) references album(serial)");
		createT("user", "serial int primary key not null auto_increment, id varchar(10), pw varchar(10), name varchar(30), email varchar(50), region int, birth date, foreign key(region) references region(serial)");
		createT("playlist", "serial int primary key not null auto_increment, name varchar(50), user int, foreign key(user) references user(serial)");
		createT("songlist", "serial int primary key not null auto_increment, playlist int, song int, foreign key(playlist) references playlist(serial), foreign key(song) references song(serial)");
		createT("history", "serial int primary key not null auto_increment, song int, user int, date date, foreign key(song) references song(serial), foreign key(user) references user(serial)");
		createT("favorite", "serial int primary key not null auto_increment, user int, song int, foreign key(user) references user(serial), foreign key(song) references song(serial)");
		createT("community", "serial int primary key not null auto_increment, user int, artist int, rate int, content text, date date, foreign key(user) references user(serial), foreign key(artist) references artist(serial)");
	}
	
	public static void main(String[] args) {
		new DB();
	}
}

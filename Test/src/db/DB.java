package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tool.Tool;
import view.BaseFrame;

public class DB extends BaseFrame {
	DefaultTableModel m = model("테이블명,진행상황".split(","));
	JTable t = table(m);
	public static Connection con;
	public static PreparedStatement ps;
	static Statement stmt;
	HashMap<String, String> map = new HashMap<>();

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=Asia/Seoul&allowPublicKeyRetreival=true&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
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
		execute("create table " + t + "(" + c + ")");

		try {
			var reader = new BufferedReader(new FileReader("./datafiles/" + t + ".txt"));
			var str = reader.readLine();
			while ((str = reader.readLine()) != null) {
				System.out.println(str.split("\t").length);
//				System.out.println("insert " + t + " values(0, "
//						+ (Stream.generate(() -> "?").limit(c.split(",").length - 1).collect(Collectors.joining(",")))+")");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		execute("load data local infile './datafiles/" + t + ".txt' into table " + t + " ignore 1 lines");
	}

	public DB() {
		super("DB", 500, 500);

		execute("drop database if exists covid");
		execute("create database covid default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, update on covid. *to user@localhost");
		execute("set global local_infile = 1");
		execute("use covid");

		map.put("building",
				"no int primary key not null auto_increment, name text, open time, close time, info text, type int, x int, y int, img longblob");
		map.put("connection", "node1 int, node2 int, name text");

		setVisible(true);

		var futures = map.entrySet().stream().map(a -> CompletableFuture.supplyAsync(() -> {
			createT(a.getKey(), a.getValue());
			return "done";
		}).exceptionally(e -> {
			e.printStackTrace();
			return "error";
		})).collect(Collectors.toList());

		var results = futures.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList());

		System.out.println(results);

	}

	public static void main(String[] args) {
		new DB();
	}
}

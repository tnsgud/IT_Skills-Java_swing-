package db;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import view.BaseFrame;

public class DB extends BaseFrame {
	DefaultTableModel m = new DefaultTableModel(null, "테이블명,진행상황".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			var com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column != 1)
				return com;

			return (JComponent) value;
		};
	};
	JTable t = new JTable(m);
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
			e.printStackTrace();
		}
	}

	void createT(String t, String c) {
		c = Stream.of(c.split(",")).map(a -> a += a.contains("fore") ? " on delete cascade on update cascade" : "")
				.collect(Collectors.joining(","));
		System.out.println(c);
		execute("create table " + t + "(" + c + ")");
//		map.put(t, c);
	}

	public DB() {
		super("DB", 500, 500);
		setDefaultCloseOperation(3);
		
		add(new JScrollPane(t));

		t.getColumn("진행상황").setCellRenderer(r);

		execute("drop database if exists covid");
		execute("create database covid default character set utf8");
		execute("drop user if exists user@localhost");
		execute("create user user@localhost identified by '1234'");
		execute("grant select, update, insert, update on covid. *to user@localhost");
		execute("set global local_infile = 1");
		execute("use covid");

		map.put("building_test",
				"no int primary key not null auto_increment, name text, open time, close time, info text, type int, x int, y int, img longblob");
		map.put("connection", "node1 int, node2 int, name text");
		map.put("user",
				"no int primary key not null auto_increment, name varchar(20), id varchar(20), pw varchar(20), phone varchar(30), birth date, building int");
		map.put("vaccine", "no int primary key not null auto_increment, name varchar(20), price int");
		map.put("purchase",
				"no int primary key not null auto_increment, user int, date datetime, building int, vaccine int, shot int");
		map.put("rate", "no int primary key not null auto_increment, building int, rate int, user int, review text");
//		createT("building",
//				"no int primary key not null auto_increment, name text, open time, close time, info text, type int, x int, y int, img longblob");
//		createT("connection",
//				"node1 int, node2 int, name text, foreign key(node1) references building(no), foreign key(node2) references building(no)");
//		createT("user",
//				"no int primary key not null auto_increment, name varchar(20), id varchar(20), pw varchar(20), phone varchar(30), birth date, building int, foreign key(building) references building(no)");
//		createT("vaccine", "no int primary key not null auto_increment, name varchar(20), price int");
//		createT("purchase",
//				"no int primary key not null auto_increment, user int, date datetime, building int, vaccine int, shot int, foreign key(user) references user(no), foreign key(building) references building(no), foreign key(vaccine) references vaccine(no)");
//		createT("rate",
//				"no int primary key not null auto_increment, building int, rate int, user int, review text, foreign key(building) references building(no), foreign key(user) references user(no)");

		setVisible(true);

		var futures = map.entrySet().stream().map(a -> CompletableFuture.supplyAsync(() -> {
			try (BufferedReader reader = new BufferedReader(new FileReader("./datafiles/" + a.getKey() + ".txt"))) {
				var str = reader.readLine();
				var columeCount = str.split("\t").length;
				var colume = "?,".repeat(columeCount).substring(0, 2 * columeCount - 1);
				var rowCount = (int) Files.lines(new File("./datafiles/" + a.getKey() + ".txt").toPath()).count() - 1;
				var bar = new JProgressBar(0, 0, rowCount);

				execute("create table " + a.getKey() + "(" + a.getValue() + ")");

				bar.setStringPainted(true);
				m.addRow(new Object[] { a.getKey() + " Table", bar });

				while ((str = reader.readLine()) != null) {
					while (columeCount != str.split("\t").length) {
						str += " \t";
					}
					var ps = con.prepareStatement("insert " + a.getKey() + " values(" + colume + ")");
					for (int i = 0; i < columeCount; i++) {
						ps.setObject(i + 1, str.split("\t")[i]);
					}
					ps.execute();
					bar.setValue(bar.getValue() + 1);
					repaint();
					Thread.sleep(5);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "error";
			}
			return "done";
		}).exceptionally(e -> {
			return "error";
		})).collect(Collectors.toList());
		

//		var results = futures.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList());
//		System.out.println(results);
	}

	public static void main(String[] args) throws Exception {
		 new DB();
	}
}

package db;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import tool.Tool;
import view.BaseFrame;

public class DB extends BaseFrame {
	public static Connection con;
	public static PreparedStatement ps;
	static Statement stmt;

	JLabel lblLog;

	int arc;
	boolean isFail;

	static {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost?serverTimezone=UTC&allowPublicKeyRetreival=true&&allowLoadLocalInfile=true",
					"root", "1234");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void execute(String sql, String log, int w) {
		arc += w;

		System.out.println(sql);

		try {
			stmt.execute(sql);
			lblLog.setText(log);
		} catch (SQLException e) {
			isFail = true;
			lblLog.setForeground(Color.black);
			lblLog.setText(log + " Failed");
			e.printStackTrace();
		}

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		repaint();
		revalidate();
	}

	void createT(String t, String c) throws SQLException {
		c = Stream.of(c.split(",")).map(a -> a += a.contains("not null") || a.contains("fore") ? "" : " not null")
				.collect(Collectors.joining(","));

		execute("create table " + t + "(" + c + ")", t + " Table 생성", 10);
	}

	void loadData(String t) throws SQLException {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		execute("load data local infile './지급자료/" + t + ".csv' into table " + t
				+ " fields terminated by ',' lines terminated by '\r\n' ignore 1 lines", t + " Data Insert", 0);
	}

	public DB() {
		super("Database Setting", 400, 430);
		setDefaultCloseOperation(3);
		getContentPane().setBackground(Color.white);

		add(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(isFail ? Color.black : Tool.red);

				g2.fill(new Arc2D.Float(0, 0, getWidth(), getHeight(), 90, -arc, Arc2D.PIE));
				g2.setColor(Color.white);

				int w = 40;

				g2.fill(new Ellipse2D.Float(w, w, getWidth() - w * 2, getWidth() - w * 2));
			}
		});
		add(lblLog = lbl("", 0), "South");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		setVisible(true);

		new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				while (arc <= 360 && !isFail) {
					Thread.sleep(35);

					arc++;

					repaint();
					revalidate();
				}

				if (!isFail) {
					lblLog.setText("Setting Complete!");
				}

				return null;
			}
		}.execute();

		data();
	}

	private void data() {
		try {
			execute("drop database if exists 2022전국", "Database 삭제", 10);
			execute("drop user if exists user@localhost", "User 삭제", 10);
			execute("create database 2022전국 default character set utf8", "Database 생성", 10);
			execute("create user user@localhost identified by '1234'", "User 생성", 10);
			execute("grant select, insert, update, delete on 2022전국. * to user@localhost", "User 권한 지정", 10);

			stmt.execute("set global local_infile=1");
			stmt.execute("use 2022전국");

			createT("grade", "gr_no int primary key not null auto_increment, gr_name varchar(10), gr_criteria int");
			createT("user",
					"u_no int primary key not null auto_increment, u_id varchar(20), u_pw varchar(30), u_name varchar(30), u_birth date, u_gender int, gr_no int, foreign key(gr_no) references grade(gr_no)");
			createT("area", "a_no int primary key not null auto_increment, a_name varchar(15)");
			createT("theater",
					"t_no int primary key not null auto_increment, t_name varchar(30), a_no int, m_no varchar(200), foreign key(a_no) references area(a_no)");
			createT("genre", "g_no int primary key not null auto_increment,g_name varchar(10)");
			createT("movie",
					"m_no int primary key not null auto_increment, m_title varchar(50), m_synopsis text, g_no varchar(100), m_time int, m_open int, m_director varchar(20)");
			createT("comment",
					"c_no int primary key not null auto_increment, u_no int, m_no int, c_text text, c_rate int, foreign key(u_no) references user(u_no), foreign key(m_no) references movie(m_no)");
			createT("reservation",
					"r_no int primary key not null auto_increment, u_no int, m_no int, t_no int, r_date date, r_time varchar(10), r_seat varchar(200), r_price int, foreign key(u_no) references user(u_no), foreign key(m_no) references movie(m_no), foreign key(t_no) references theater(t_no)");

			for (var t : "grade,user,area,theater,genre,movie,comment,reservation".split(",")) {
				loadData(t);
			}

			stmt.execute("drop view if exists screening");
			stmt.execute(
					"create view screening as select m.m_no, count(*) >0 isScreening from theater t, movie m where find_in_set(m.m_no, replace(t.m_no, '.',',')) > 0 group by m.m_no");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new DB();
	}
}

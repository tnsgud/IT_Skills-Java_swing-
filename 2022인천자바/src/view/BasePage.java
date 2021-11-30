package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class BasePage extends JPanel {
	static Connection con = db.DB.con;
	static Statement stmt = db.DB.stmt;
	static MainFrame mf = new MainFrame();
	JPanel m; // masterPanel
	JPanel n, c, e, w, s;
	JPanel nn, nc, ne, nw, ns;
	JPanel en, ec, ee, ew, es;
	JPanel sn, sc, se, sw, ss;
	JPanel cn, cc, ce, cw, cs;
	JPanel wn, wc, we, ww, ws;

	static String uno, uname, uid, upw, uage, ulocal, ubirth, upoint, uphone;

	static{
		try {
			stmt.execute("use covid");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ResultSet rs(String sql) throws SQLException {
		var rs = stmt.executeQuery(sql);
		return rs;
	}

	public static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	public static void setLogin(int no) {

	}

	public static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보 ", JOptionPane.INFORMATION_MESSAGE);
	}

	public static ImageIcon getIcon(String path, int w, int h) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	public static ImageIcon getIcon(String path) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path));
	}

	static void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BasePage() {
		super(new BorderLayout());
		revalidate();
		repaint();
	}

	public static JLabel lbl(String t, int al, int s) {
		var l = new JLabel(t, al);
		l.setFont(new Font("맑은 고딕", Font.BOLD, s));
		return l;
	}

	public static JLabel lbl(String t, int al, int f, int s) {
		var l = new JLabel(t, al);
		l.setFont(new Font("맑은 고딕", f, s));
		return l;
	}

	public static String getOne(String sql) {
		try {
			var rs = rs(sql);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}
		} catch (SQLException e) {

			return null;
		}
	}

	public static Integer toInt(Object path) {
		return Integer.parseInt(path.toString());
	}

	public static JComponent sz(JComponent jc, int w, int h) {
		jc.setPreferredSize(new Dimension(w, h));
		return jc;
	}

	public static JLabel lbl(String t, int al) {
		return new JLabel(t, al);
	}

	public static JButton btn(String cap, ActionListener a) {
		var b = new JButton(cap);
		b.setContentAreaFilled(false);
		b.setOpaque(true);
		b.setForeground(Color.WHITE);
		b.setBackground(Color.ORANGE);
		b.addActionListener(a);
		return b;
	}

	public static JLabel hyplbl(String text, int al, int s, int f, Color col, MouseAdapter ma) {
		var l = new JLabel("<html><u>" + text, al);
		l.setFont(new Font("맑은 고딕", f, s));
		l.setForeground(col);
		l.addMouseListener(ma);
		return l;
	}

	@Deprecated
	static class sideBar {

	}

	public static JComponent setB(JComponent jc, Border b) {
		jc.setBorder(b);
		return jc;
	}
}

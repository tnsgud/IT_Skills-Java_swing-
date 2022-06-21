package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

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

	static String uno, upw, uphone, upoint, uname, uage;
	static String bpoint;

	static ResultSet rs;
	static int posDim[][];
	static HashMap<Integer, Object[]> facility;
	static HashMap<Integer, Object[]> residence;
	static HashMap<String, Integer> resMap;
	static HashMap<String, Integer> facMap;
	static int adjDim[][];
	static final int INF = 1000000;

	static {
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

	public static void datainit() throws SQLException {
		posDim = new int[334][2];

		rs = stmt.executeQuery("select * from Point");

		while (rs.next()) {
			posDim[rs.getInt(1)][0] = rs.getInt(2);
			posDim[rs.getInt(1)][1] = rs.getInt(3);
		}

		adjDim = new int[334][334];

		for (int i = 1; i < adjDim.length; i++) {
			for (int j = i + 1; j < adjDim[i].length; j++) {
				adjDim[i][j] = adjDim[j][i] = INF;
			}
		}

		rs = rs("select * from Connection");

		while (rs.next()) {
			adjDim[rs.getInt(1)][rs.getInt(2)] = (int) Point.distance(posDim[rs.getInt(1)][0], posDim[rs.getInt(1)][1],
					posDim[rs.getInt(2)][0], posDim[rs.getInt(2)][1]);
		}

		// 정보
		facility = new HashMap<>();
		residence = new HashMap<>();
		resMap = new HashMap<>();
		facMap = new HashMap<>();

		rs = rs("select * from building b left outer join building_Info bi on b.no = bi.building");
		while (rs.next()) {
			if (rs.getString(5) == null) {
				residence.put(rs.getInt(3), new Object[] { rs.getInt(1), rs.getString(2) });
				resMap.put(rs.getString(2), rs.getInt(3));
			} else {
				facility.put(rs.getInt(3), new Object[] { rs.getString(1), rs.getString(2), rs.getString(4),
						rs.getString(5), rs.getString(6) });
				facMap.put(rs.getString(2), rs.getInt(3));
			}
		}
	}

	public static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	public static void setLogin(int no) {

	}

	public static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
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
package tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import db.DB;
import view.MainFrame;

public interface Tool {
	default ResultSet rs(String sql, Object... obj) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			return DB.ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	default void execute(String sql, Object... obj) {
		try {
			DB.ps = DB.con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				DB.ps.setObject(i + 1, obj[i]);
			}
			DB.ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	default String getOne(String sql, Object... obj) {
		var rs = rs(sql, obj);
		try {
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	default <T extends JComponent> T sz(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	default void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "경고", JOptionPane.ERROR_MESSAGE);
	}

	default void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "정보", JOptionPane.INFORMATION_MESSAGE);
	}

	default JLabel img(String p, int w, int h) {
		return new JLabel(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./datafiles/" + p).getScaledInstance(w, h, Image.SCALE_SMOOTH)));
	}

	default JLabel img(String p) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/" + p)));
	}

	default JLabel lbl(String c, int a, int f, int sz, Color col, MouseAdapter mouse) {
		var l = new JLabel("<html><u>"+c+"</u></html>", a);
		l.setFont(new Font("맑은 고딕", f, sz));
		l.setForeground(col);
		l.addMouseListener(mouse);
		return l;
	}

	default JLabel lbl(String c, int a, int f, int sz) {
		var l = new JLabel(c, a);
		l.setFont(new Font("맑은 고딕", f, sz));
		return l;
	}

	default JLabel lbl(String c, int a, int sz) {
		return lbl(c, a, Font.BOLD, sz);
	}

	default JLabel lbl(String c, int a) {
		return lbl(c, a, 0, 12);
	}

	default JComponent border(JComponent c, Border b) {
		c.setBorder(b);
		return c;
	}
}

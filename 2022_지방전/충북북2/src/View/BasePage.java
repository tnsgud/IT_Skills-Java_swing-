package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import DB.DB;

public class BasePage extends JPanel {
	public static MainFrame mf = new MainFrame();
	public static Connection con = DB.con;
	public static Statement stmt = DB.stmt;
	public static int u_serial, u_region, top, value;
	public static String al_serial, s_serial, ar_serial, ar_name;
	public static Color myColor = new Color(50, 100, 255);
	static DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	static LinkedList<Integer> que = new LinkedList<Integer>() {
		public void add(int index, Integer element) {
			if (super.size() == 0) {
				top = element;
			}
			super.add(index, element);
		};
	};
	static Stack<Integer> cache = new Stack<>();
	static Timebar bar;
	static Timer barTimer;
	static DefaultTableModel que_m = songModel();
	static JTable que_t = songTable(que_m);
	static DefaultTableModel cur_m = songModel();
	static JTable cur_t = songTable(cur_m);
	static HashMap<Integer, Integer> songToalbum = new HashMap<>();
	static HashMap<Integer, Integer> albumToArtist = new HashMap<>();
	{
		cur_m.setRowCount(0);
		cur_m.addRow(new Object[] { "", "", "재생중이지 않음", "", "", "" });
	}
	static {
		try {
			var rs = stmt.executeQuery("select * from song");
			while (rs.next()) {
				songToalbum.put(rs.getInt(1), rs.getInt(4));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			var rs = stmt.executeQuery("select * from album");
			while (rs.next()) {
				albumToArtist.put(rs.getInt(1), rs.getInt("artist"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void curPlaying(int song, int length, String songName) {
		if (song == -1) {
			barTimer.stop();
			mf.album_img.setIcon(null);
			mf.state_lbl.setText("재생중이지 않음");
			return;
		}
		System.out.println(length);
		int album = songToalbum.get(song);
		mf.album_img.setIcon(
				new ImageIcon(Toolkit.getDefaultToolkit().getImage(MainFrame.IMG_PATH + "album/" + album + ".png")
						.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
		mf.state_lbl.setText(songName);
		bar.clear();
		bar.setMaxValue(length);
	}

	static void reFresh() {
		que_m.setRowCount(0);
		for (var v : que) {
			try {
				var rs = stmt.executeQuery(
						"SELECT if(s.titlesong = 1 , 1, 0) isTitle, s.name, if(s.serial in  ( select f.song from user u , favorite f where u.serial = f.user and u.serial = '"
								+ u_serial
								+ "'), true, false) isFavorite ,time_format(s.length, '%i:%S')  ,s.serial FROM song s where serial = "
								+ v + "");
				while (rs.next()) {
					que_m.addRow(new Object[] { rs.getInt(1) == 0 ? "" : "★", que_m.getRowCount() + 1, rs.getString(2),
							rs.getInt(3) == 0 ? "♡" : "♥", rs.getString(4), rs.getString(5) });
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	static void setCur(Object row[]) {
		cur_m.setRowCount(0);
		if (row == null) {

			cur_m.addRow(new Object[] { "", "", "재생중이지 않음", "", "", "" });
			curPlaying(-1, -1, "");
			return;
		}
		curPlaying(toInt(row[5]), LocalTime
				.of(0, toInt(row[4].toString().split(":")[0]), toInt(row[4].toString().split(":")[1])).toSecondOfDay(),
				row[2].toString());

		cur_m.addRow(new Object[] { "", "", row[2], row[3], row[4], row[5] });
	}

	static void next() {
		if (que.size() == 0) {
			setCur(null);
			mf.pause();
			bar.clear();
			bar.value = 1;
			bar.max = 100000000;
			System.out.println(value);
			return;
		}
		var song = que.poll();

		setCur(que_m.getDataVector().get(0).toArray());
		que_m.removeRow(0);
		reFresh();
		cache.add(song);
	}

	static void prev() {
		if (cache.isEmpty()) {
			setCur(null);
			return;
		}
		var song = cache.pop();
		System.out.println(song);
	}

	static {
		try {
			stmt.execute("use music");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void addSongRow(String sql, DefaultTableModel m) {
		m.setRowCount(0);
		try {
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {
				m.addRow(new Object[] { rs.getInt(1) == 0 ? "" : "★", rs.getRow(), rs.getString(2),
						rs.getInt(3) == 0 ? "♡" : "♥", rs.getString(4), rs.getString(5) });
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static DefaultTableModel songModel() {
		DefaultTableModel m = new DefaultTableModel(null, "chk,row,name,like,title,serial".split(",")) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};

		return m;
	}

	static JTable songTable(DefaultTableModel m) { // 클래스로 따로 빼놔야할것 같..

		JTable t = new JTable(m) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				JComponent v = (JComponent) super.prepareRenderer(renderer, row, column);
				if (row == super.getSelectedRow()) {
					v.setBackground(Color.WHITE.darker());
					v.setForeground(Color.WHITE);
				} else {
					v.setBackground(Color.black);
					v.setForeground(Color.WHITE);
				}

				v.setBorder(BorderFactory.createEmptyBorder());
				v.repaint();
				v.revalidate();
				return v;
			}

		};

		t.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JTable t = (JTable) e.getSource();
				Point p = e.getPoint();
				int row = t.rowAtPoint(p);
				if (row < 0 || row > t.getRowCount()) {
					t.clearSelection();
					return;
				}
				t.setRowSelectionInterval(row, row);
			}
		});

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				t.clearSelection();
				super.mouseExited(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					var song = toInt(t.getValueAt(t.getSelectedRow(), t.getColumnCount() - 1));
					que.add(song);
					iMsg("대기열에 추가되었습니다.");
				}
				super.mouseClicked(e);
			}
		});

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.setIntercellSpacing(new Dimension(0, 0));
		t.setBackground(Color.black);

		t.setForeground(Color.WHITE);
		t.setShowGrid(false);
		t.setGridColor(Color.BLACK);

		t.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;

				int row = t.getSelectedRow();
				int col = t.getSelectedColumn();

				if (t.getSelectedColumn() == 3) {
					if (t.getValueAt(row, col).equals("♥")) {
						t.setValueAt("♡", row, col);
						execute("delete from favorite where user =" + u_serial + " and song = "
								+ t.getValueAt(row, t.getColumnCount() - 1));

					} else {
						t.setValueAt("♥", row, col);
						execute("insert favorite values(0, " + u_serial + ","
								+ t.getValueAt(row, t.getColumnCount() - 1) + ")");
					}

					return;
				}

			}
		});

		t.setRowHeight(30);

		t.getColumnModel().getColumn(0).setMaxWidth(30);
		t.getColumnModel().getColumn(0).setMinWidth(30);
		t.getColumnModel().getColumn(1).setMaxWidth(30);
		t.getColumnModel().getColumn(2).setMinWidth(50);
		t.getColumnModel().getColumn(3).setMaxWidth(20);

		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		t.getColumnModel().getColumn(0).setCellRenderer(center);
		t.getColumnModel().getColumn(3).setCellRenderer(center);
		t.getColumnModel().getColumn(4).setCellRenderer(center);

		t.getColumnModel().getColumn(t.getColumnCount() - 2).setMinWidth(50);
		t.getColumnModel().getColumn(t.getColumnCount() - 2).setMaxWidth(50);
		t.getColumnModel().getColumn(t.getColumnCount() - 1).setMinWidth(0);
		t.getColumnModel().getColumn(t.getColumnCount() - 1).setMaxWidth(0);

		return t;
	}

	void addrow(String sql, DefaultTableModel m) {
		m.setRowCount(0);
		try {
			var rs = stmt.executeQuery(sql);
			Object row[] = new Object[m.getColumnCount()];
			while (rs.next()) {
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BasePage() {
		setLayout(new BorderLayout());
		execute("use music");
		setBackground(Color.black);

	}

	void addtoPlayList(int s_serial) {
		ArrayList<String> list = new ArrayList<String>();
		JComboBox<?> box;

		try {
			var rs = BasePage.stmt
					.executeQuery("select pl.name from playlist pl, user u where u.serial = pl.user and u.serial="
							+ BasePage.u_serial);
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		box = new JComboBox<>(list.toArray());
		int ynMsg = JOptionPane.showConfirmDialog(null, box, "플레이리스트에 추가", JOptionPane.YES_NO_OPTION);
		if (ynMsg == 0) {
			if (box.getSelectedIndex() == -1) {
				BasePage.eMsg("플레이리스트를 선택해주세요.");
				return;
			}

			BasePage.iMsg("추가되었습니다.");
			try {
				var rs = stmt.executeQuery(
						"select * from songlist where playlist = (select serial from playlist where name like '%"
								+ box.getSelectedItem() + "%' and user = " + u_serial + ") and song = '" + s_serial
								+ "'");
				if (rs.next()) {
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BasePage.execute("insert into songlist values(0, (select serial from playlist where name like '"
					+ box.getSelectedItem() + "' and user = " + u_serial + "), " + s_serial + ")");

		}
	}

	static JLabel imglbl(String path, int w, int h) {
		return new JLabel(
				new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH)));
	}

	static JLabel imglbl(String text, String path, int w, int h, MouseAdapter act) {
		JLabel lbl = new JLabel(
				new ImageIcon(Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(w, h, Image.SCALE_SMOOTH)));
		lbl.setText(text);
		lbl.setForeground(Color.white);
		lbl.addMouseListener(act);
		lbl.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		lbl.setFont(new Font("맑은 고딕", 0, 15));
		lbl.setHorizontalAlignment(JLabel.LEFT);
		return lbl;
	}

	static JLabel imglbl(String path) {
		return new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(path)));
	}

	public static int toInt(Object p) {
		return Integer.parseInt(p.toString());
	}

	public static void execute(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JLabel lbl(String c, int a, int style, int size) {
		JLabel l = new JLabel(c, a);
		l.setForeground(Color.white);
		l.setFont(new Font("맑은 고딕", style, size));
		return l;
	}

	public static JLabel lbl(String c, int a, int style, int size, MouseListener m) {
		JLabel l = new JLabel(c, a);
		l.setForeground(Color.white);
		l.setFont(new Font("맑은 고딕", style, size));
		l.addMouseListener(m);
		return l;
	}

	public static JLabel lbl(String c, int a, int size) {
		JLabel l = new JLabel(c, a);
		l.setForeground(Color.white);
		l.setFont(new Font("맑은 고딕", 0, size));
		return l;
	}

	public static JButton btn(String cap, ActionListener a) {
		JButton b = new JButton(cap);
		b.setBackground(Color.white);
		b.addActionListener(a);
		return b;
	}

	public static JTable table(DefaultTableModel m) {
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		JTable t = new JTable(m);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		return t;
	}

	static class Timebar extends JLabel {

		public double max = 1000, value;

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			// getPercentage of value and change it to curval
			double percent = (value / max); // 원비 -> 해당 width의 비율로
			g2.setColor(Color.ORANGE);
			g2.fillRect(0, 0, (int) (getWidth() * percent), getHeight());
			repaint();
			super.paint(g);
		}

		void clear() {
			max = 1000;
			BasePage.value = 0;
		}

		void setMaxValue(double max) {
			this.max = max;
		}

		void setValue(double value) {
			if (value > max)
				next();
			this.value = value;
		}
	}

	public static DefaultTableModel model(String col[]) {
		DefaultTableModel m = new DefaultTableModel(null, col) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return m;
	}

	public static void iMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void eMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg, "", JOptionPane.ERROR_MESSAGE);
	}

	public static <T extends JComponent> T size(T c, int w, int h) {
		c.setPreferredSize(new Dimension(w, h));
		return c;
	}

	public static JTextField txt(int size) {
		JTextField t = new JTextField(size);
		t.setBackground(Color.gray);
		return t;
	}
}

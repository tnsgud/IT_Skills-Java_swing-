package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import db.DB;
import model.Perform;
import ui.BaseFrame.Before;

public class Search extends BaseFrame {
	JMenuBar bar;
	JMenu menu;
	JMenuItem[] items = new JMenuItem[4];
	String[] icap = "전체,뮤지컬,오페라,콘서트".split(",");
	DefaultTableModel m = model("p_no,공연날짜,공연명,공연가격".split(","));
	JTable t = table(m);
	JScrollPane scr = new JScrollPane(t);
	JTextField txt = new JTextField(15);
	String key = "", type = "";
	Object[] params = { "2021-10-06", "%" + key + "%", "%" + type + "%" };
	West w;
	String sql = "select p_no, p_date, p_name, format(p_price, '#,##0') from perform where p_date>=? and p_name like ? and pf_no like ? order by p_date asc, p_price desc";

	public Search() {
		super("검색", 800, 650);
		
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		
		add(n = new JPanel(new FlowLayout(2)), "North");
		add(w = new West(), "West");
		add(c = new JPanel(new BorderLayout()));

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		c.setBorder(new EmptyBorder(5, 5, 5, 5));

		setJMenuBar(bar = new JMenuBar());
		bar.add(menu = new JMenu("분류"));
		for (int i = 0; i < icap.length; i++) {
			menu.add(items[i] = new JMenuItem(icap[i]));
		}

		event();
		addRow(m, sql, params);

		n.add(lbl("공연명 : ", 0));
		n.add(txt);
		n.add(btn("검색", a -> {
			key = txt.getText();
			try {
				var rs = DB.rs(sql, params);
				if (rs.next()) {
					addRow(m, sql, params);
				} else {
					eMsg("검색 결과가 없습니다.");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		c.add(lbl("현재 예매 가능 공연", 4), "North");
		c.add(scr);

		scr.setBorder(new LineBorder(Color.black));
		setVisible(true);
	}

	private void event() {
		for (int i = 0; i < items.length; i++) {
			items[i].addActionListener(a -> {
				w.tit.setText(a.getActionCommand());
				if (a.getActionCommand().equals(icap[0])) {
					type = "";
				} else if (a.getActionCommand().equals(icap[1])) {
					type = "M";
				} else if (a.getActionCommand().equals(icap[2])) {
					type = "O";
				} else {
					type = "C";
				}
			});
		}
		
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				perform = DB.getModel(Perform.class, "select * from perform where p_no=?", ((JTable)e.getSource()).getValueAt(t.getSelectedRow(), 0));
				new Reserve().addWindowListener(new Before(Search.this));
			}
		});
	}

	class West extends JPanel {
		int curidx;
		JPanel c, s, s_n, s_c;
		JLabel prev = lbl("◀", 0), next = lbl("▶", 0), first = lbl("《처음으로", 0), last = lbl("마직막으로》", 0), tit, lbls[];
		ArrayList<Perform> performs = new ArrayList<Perform>();

		public West() {
			setLayout(new BorderLayout(5, 5));
			setBorder(new EmptyBorder(100, 5, 100, 5));

			this.event();

			add(tit = lbl("분류:전체", 4), "North");
			add(prev, "West");
			add(this.c = new JPanel(new GridLayout(2, 2, 5, 5)));
			add(next, "East");
			add(this.s = new JPanel(new BorderLayout()), "South");
			s.add(s_n = new JPanel(), "North");
			s.add(s_c = new JPanel());

			curidx = 0;
			addImage();
		}

		private void addImage() {
			c.removeAll();
			s_n.removeAll();

			performs = DB.getModelList(Perform.class,
					"select * from perform where '2021-10-06' <= p_date and pf_no like ? and p_name like ? group by p_name order by p_date asc, p_price desc",
					"%" + type + "%", "%" + key + "%");

			for (int i = curidx * 4; i <= curidx * 4 + 3; i++) {
				var l = lbl("", 0);
				if (i < performs.size()) {
					l.setIcon(img("공연사진/" + performs.get(i).pf_no, 150, 150));
					l.setName(i + "");
					l.setToolTipText(performs.get(i).p_name + "/" + performs.get(i).p_date);
				} else {
					l.setName("-1");
				}

				l.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var n = ((JLabel) e.getSource()).getName();
						if (n.contentEquals("-1")) {
							eMsg("공연정보가 없습니다.");
							return;
						}

						BaseFrame.perform = performs.get(toInt(n));
						new Reserve().addWindowListener(new Before(Search.this));
					}
				});

				l.setBorder(new LineBorder(Color.black));
				c.add(sz(l, 150, 150));
			}

			lbls = new JLabel[performs.size() % 4 == 0 ? performs.size() : (performs.size() / 4) + 1];
			for (int i = 0; i < lbls.length; i++) {
				lbls[i] = lbl("●", 0);
				s_n.add(lbls[i]);
			}
			lbls[curidx].setForeground(Color.red);

			repaint();
			revalidate();
		}

		void event() {
			prev.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (curidx != 0) {
						curidx--;
						addImage();
					}
				}
			});
			next.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (curidx != (performs.size() / 4)) {
						curidx++;
						addImage();
					}
				}
			});
			first.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					curidx = 0;
					addImage();
				}
			});
			last.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					curidx = performs.size() / 4;
					addImage();
				}
			});
		}
	}

	public static void main(String[] args) {
		new Search();
	}
}
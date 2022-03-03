import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Search extends BaseFrame {

	JPanel c_w;

	JMenuBar bar = new JMenuBar();
	JMenu menu = new JMenu("분류");
	JMenuItem item[] = { new JMenuItem("전체"), new JMenuItem("뮤지컬"), new JMenuItem("오페라"), new JMenuItem("콘서트") };

	JTextField txt;

	DefaultTableModel m = model("공연날짜,공연명,공연가격,pno".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);

	String type = "";

	public Search() {
		super("검색", 600, 600);

		this.setJMenuBar(bar);
		bar.add(menu);
		for (int i = 0; i < item.length; i++) {
			menu.add(item[i]);
			if (i == 0) {
				menu.addSeparator();
			}

			item[i].addActionListener(e -> {
				if (e.getActionCommand().equals("뮤지컬")) {
					type = "M";
				} else if (e.getActionCommand().equals("오페라")) {
					type = "O";
				} else if (e.getActionCommand().equals("콘서트")) {
					type = "C";
				} else {
					type = "";
				}

				search();
			});
		}

		this.add(n = new JPanel(new FlowLayout(2)), "North");
		this.add(c = new JPanel(new BorderLayout()));

		var c_e = new JPanel(new BorderLayout());

		n.add(new JLabel("공연명 : "));
		n.add(txt = new JTextField(15));
		n.add(btn("검색", e -> search()));

		c.add(c_w = new JPanel(new BorderLayout()), "West");
		c.add(c_e, "East");

		c_w.add(new West());
		c_e.add(new JLabel("현재 예매 가능 공연", 4), "North");
		c_e.add(sz(jsc, 250, 400));

		search();

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				pno = t.getValueAt(t.getSelectedRow(), 3).toString();
				new Reserve().addWindowListener(new Before(Search.this));
			};
		});

		t.getColumnModel().getColumn(3).setMinWidth(0);
		t.getColumnModel().getColumn(3).setMaxWidth(0);

		this.setVisible(true);
	}

	class West extends JPanel {

		JPanel n, c, s;
		CardLayout card;

		JLabel type;
		JLabel prev, next, first, last;
		ArrayList<JLabel> imglist = new ArrayList<JLabel>();

		int pageCnt = 1, pageIdx = 1;

		public West() {
			super(new BorderLayout());

			this.add(n = new JPanel(new FlowLayout(2, 30, 10)), "North");
			this.add(c = new JPanel(new BorderLayout()));
			this.add(s = new JPanel(new FlowLayout()), "South");

			var c_c = new JPanel(card = new CardLayout());
			var c_s = new JPanel(new FlowLayout(1, 5, 5));

			c.add(prev = lbl("◀", 0, 30), "West");
			c.add(c_c);
			c.add(c_s, "South");
			c.add(next = lbl("▶", 0, 30), "East");

			if (Search.this.type == "M") {
				n.add(type = new JLabel("분휴 : 뮤지컬"));
			} else if (Search.this.type == "O") {
				n.add(type = new JLabel("분류 : 오페라"));
			} else if (Search.this.type == "C") {
				n.add(type = new JLabel("분류 : 콘서트"));
			} else {
				n.add(type = new JLabel("분류 : 전체"));
			}

			s.add(first = new JLabel("≪처음으로"));
			s.add(last = new JLabel("마지막으로≫"));

			String sql = "select * from perform where pf_no like '%" + Search.this.type
					+ "%' and p_date >= '2021-10-06' and p_name like '%" + txt.getText()
					+ "%' group by p_name order by p_date, p_price desc";
			try {
				var rs = stmt.executeQuery(sql);
				while (rs.next()) {
					var img = new JLabel(icon(rs.getString(2), 120, 120));
					imglist.add(img);
					img.setBorder(new LineBorder(Color.BLACK));
					img.setName(rs.getString(1));
					img.setToolTipText(rs.getString(3) + "/" + rs.getString("p_date"));

					img.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							pno = ((JLabel) e.getSource()).getName();
							new Reserve().addWindowListener(new Before(Search.this));
						}
					});
				}

				if (imglist.size() == 0) {
					eMsg("공연정보가 없습니다.");

					var tmp = new JPanel(new GridLayout(2, 2, 5, 5));
					for (int i = 0; i < 4; i++) {
						var lbl = new JLabel();

						lbl.setBorder(new LineBorder(Color.BLACK));
						tmp.add(lbl);
						c_c.add(tmp);

						lbl.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								eMsg("공연정보가 없습니다.");
								return;
							}
						});

						sz(lbl, 120, 120);
					}

					n.setBorder(new EmptyBorder(90, 0, 0, 0));
					c.setBorder(new EmptyBorder(0, 10, 0, 10));
					s.setBorder(new EmptyBorder(0, 0, 90, 0));
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (imglist.size() % 4 != 0) {
				for (int i = imglist.size() % 4; i < 4; i++) {
					var lbl = new JLabel();

					lbl.setBorder(new LineBorder(Color.BLACK));
					imglist.add(lbl);

					lbl.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							eMsg("공연정보가 없습니다.");
							return;
						}
					});

					sz(lbl, 120, 120);
				}
			}

			pageCnt = imglist.size() / 4;

			int cnt = 0;
			var tmp = new JPanel(new GridLayout(2, 2, 5, 5));
			for (int i = 1; i <= pageCnt; i++) {
				for (int j = cnt; j < cnt + 4; j++) {
					tmp.add(imglist.get(j));
				}

				cnt += 4;
				c_c.add(tmp, i + "");
				tmp = new JPanel(new GridLayout(2, 2, 5, 5));
			}

			var lbl = new JLabel[pageCnt];

			for (int i = 0; i < lbl.length; i++) {
				c_s.add(lbl[i] = new JLabel("●", 0));
			}

			lbl[0].setForeground(Color.RED);

			prev.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (pageIdx - 1 == 0) {
						return;
					} else {
						pageIdx--;
					}

					for (int i = 0; i < lbl.length; i++) {
						lbl[i].setForeground(Color.BLACK);
					}

					lbl[pageIdx - 1].setForeground(Color.RED);
					card.show(c_c, pageIdx + "");
				}
			});

			next.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (pageIdx - 1 == pageCnt - 1) {
						return;
					} else {
						pageIdx++;
					}

					for (int i = 0; i < lbl.length; i++) {
						lbl[i].setForeground(Color.BLACK);
					}

					lbl[pageIdx - 1].setForeground(Color.RED);
					card.show(c_c, pageIdx + "");
				}
			});

			first.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					pageIdx = 1;

					for (int i = 0; i < lbl.length; i++) {
						lbl[i].setForeground(Color.BLACK);
					}

					lbl[pageIdx - 1].setForeground(Color.RED);

					card.show(c_c, pageIdx + "");
				}
			});

			last.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					pageIdx = pageCnt;

					for (int i = 0; i < lbl.length; i++) {
						lbl[i].setForeground(Color.BLACK);
					}

					lbl[pageIdx - 1].setForeground(Color.RED);

					card.show(c_c, pageIdx + "");
				}
			});

			n.setBorder(new EmptyBorder(90, 0, 0, 0));
			c.setBorder(new EmptyBorder(0, 10, 0, 10));
			s.setBorder(new EmptyBorder(0, 0, 90, 0));
		}
	}

	void search() {
		c_w.removeAll();

		c_w.add(new West());
		addRow(m,
				"select p_date,p_name,format(p_price, '#,##0'),p_no from perform where pf_no like '%" + type
						+ "%' and p_name like '%" + txt.getText()
						+ "%' and p_date >= '2021-10-06' order by p_date, p_price desc");

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new Search();
	}
}

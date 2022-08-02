package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class StorageAndMarketPage extends BasePage {
	DefaultTableModel m1 = model("아이템,게임명,아이템명,s_no,i_no".split(","));
	JTable t = table(m1);
	JTextField txt = new JTextField();
	JLabel lblExp;
	JPanel main = new JPanel(new GridLayout(1, 0, 40, 20));

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var l = super.lbl(c, a, st, sz);
		l.setForeground(Color.black);
		return l;
	}

	public static void main(String[] args) {
		new LoginFrame();
	}

	public StorageAndMarketPage(String tit) {
		super(tit);

		add(lblExp = lbl("", 4, 20), "North");
		add(main);

		main.add(w = new JPanel(new BorderLayout()));
		main.add(c = new JPanel(new BorderLayout()));

		storage();
		if (tit.equals("장터")) {
			search();
		} else {
			itemSets();
		}

		mf.repaint();

		main.setBorder(new EmptyBorder(30, 30, 30, 30));
		w.setBorder(new LineBorder(Color.black));
		c.setBorder(new LineBorder(Color.black));
		setBackground(Color.white);
		setOpaque(true);
	}

	void storage() {
		w.removeAll();

		w.add(lbl("보관함", 0, 25), "North");
		w.add(new JScrollPane(t));
		w.add(ws = new JPanel(new FlowLayout(2)), "South");

		ws.add(btn("등록하기", a -> {

			var dia = new MarketDialog("장터등록", toInt(t.getValueAt(t.getSelectedRow(), 4)));
			dia.s_no = toInt(t.getValueAt(t.getSelectedRow(), 3));
			dia.setVisible(true);
		}));

		var col = "아이템,게임명,아이템명,s_no,i_no".split(",");
		var wid = new int[] { 80, 150, 150, 0, 0 };
		for (int i = 0; i < col.length; i++) {
			t.getColumn(col[i]).setMinWidth(wid[i]);
			t.getColumn(col[i]).setMaxWidth(wid[i]);
		}
		t.setRowHeight(80);

		t.setRowHeight(50);

		m1.setRowCount(0);
		for (var rs : getRows(
				"select i_img, g_name, i_name, s.s_no, i.i_no from v2, storage s, game g, item i where v2.s_no = s.s_no and g.g_no = v2.g_no and i.i_no=s.i_no and v2.u_no = ?",
				user.get(0))) {
			rs.set(0, new JLabel(getIcon(rs.get(0), 50, 50)));
			m1.addRow(rs.toArray());
		}

		lblExp.setText("경험치 : " + u_exp + "[등급 : " + g_gd[u_gd] + "]");

		w.repaint();
		w.revalidate();
	}

	void itemSets() {
		c.removeAll();
		c.setLayout(new BorderLayout());

		c.add(lbl("아이템 세트", 0, 20), "North");
		c.add(new JScrollPane(cc = new JPanel()));
		cc.setLayout(new BoxLayout(cc, BoxLayout.PAGE_AXIS));
		
		for (var rs : getRows("select g_no from item group by g_no")) {
			var g_no = toInt(rs.get(0));
			var cnt = 0;

			var tmp = new JPanel(new GridLayout(1, 0));

			tmp.setBorder(new TitledBorder(getOne("select g_name from game where g_no = ?", g_no)));
			for (var r : getRows("select i_no, i_img from item where g_no = ?", g_no)) {
				var img = getIcon(r.get(1), 80, 80).getImage();

				if (getOne("select * from storage s, v2 where s.s_no = v2.s_no and v2.u_no = ? and s.i_no = ?",
						user.get(0), r.get(0)).isEmpty()) {
					img = GrayFilter.createDisabledImage(img);
				}

				tmp.add(new JLabel(new ImageIcon(img)));

				cnt = getOne("select * from v2, storage s where v2.s_no = s.s_no and v2.u_no = ? and i_no= ?",
						user.get(0), r.get(0)).isEmpty() ? cnt : cnt + 1;
			}

			tmp.setBackground(cnt == 3 ? Color.yellow : Color.white);

			cc.add(tmp);
		}

		c.repaint();
		c.revalidate();
	}

	void search() {
		c.removeAll();

		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(new JScrollPane(cc = new JPanel()));
		cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));

		cn.add(lbl("검색", 2), "West");
		cn.add(txt);
		cn.add(btn("검색", a -> {
			cc.removeAll();

			for (var rs : getRows(
					"select i_img, g_name, i_name, format(m_price, '#,##0'), m.u_no, m_no, i.i_no from market m, storage s, item i, game g where m.s_no = s.s_no and s.i_no = i.i_no and i.g_no = g.g_no and m.m_ox = 0 and (i_name like ? or g_name like ?)",
					"%" + txt.getText() + "%", "%" + txt.getText() + "%")) {
				var tmp = new JPanel(new BorderLayout(5, 5));
				var tmp_c = new JPanel(new GridLayout(0, 1));
				var cap = "게임명,아이템명,가격".split(",");
				var flag = rs.get(4).equals(user.get(0));
				var pop = new JPopupMenu();

				for (var c : "구매하기,판매취소".split(",")) {
					var i = new JMenuItem(c);

					i.addActionListener(e -> {
						if (c.equals("구매하기")) {
							var dia = new MarketDialog("장터구매", toInt(rs.get(6)), rs.get(3).toString());
							dia.m_no = toInt(rs.get(5));
							dia.setVisible(true);
						} else {
							iMsg("판매취소가 완료되었습니다.");
							execute("delete from market where m_no =?", rs.get(5));
							createV();
							storage();
							((JButton) a.getSource()).doClick();
						}
					});

					if (c.equals("구매하기")) {
						i.setEnabled(!flag);
					} else {
						i.setEnabled(flag);
					}

					pop.add(i);
				}

				for (int i = 0; i < cap.length; i++) {
					var lbl = lbl(cap[i] + " : " + rs.get(i + 1), 2, 15);
					lbl.setForeground(flag ? Color.blue : Color.black);
					tmp_c.add(lbl);
				}

				tmp.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
				tmp.setComponentPopupMenu(pop);

				tmp.setOpaque(false);
				tmp_c.setOpaque(false);

				tmp.add(new JLabel(getIcon(rs.get(0), 90, 90)), "West");
				tmp.add(tmp_c);

				cc.add(sz(tmp, 300, 100));
			}

			repaint();
			revalidate();
		}), "East");

		c.repaint();
		c.revalidate();
	}
}

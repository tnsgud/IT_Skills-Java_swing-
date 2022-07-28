package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class StoragePage extends BasePage {
	DefaultTableModel m = model("s_no,아이템,게임명,아이템명,i_no".split(","));
	JTable t = table(m);

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var lbl = super.lbl(c, a, st, sz);
		lbl.setForeground(Color.black);
		return lbl;
	}

	public StoragePage() {
		super("보관함");

		setBackground(Color.white);
		setLayout(new GridLayout(1, 0, 100, 0));
		setOpaque(true);

		add(w = new JPanel(new BorderLayout()), "West");
		add(c = new JPanel(new BorderLayout()));

		w.add(lbl("보관함", 0, 20), "North");
		w.add(new JScrollPane(t));
		w.add(ws = new JPanel(new FlowLayout(2)), "South");

		ws.add(btn("등록하기", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("등록할 아이템을 선택하세요.");
				return;
			}

			var dia = new MarketDialog("장터등록", toInt(t.getValueAt(t.getSelectedRow(), 4)));
			dia.s_no = toInt(t.getValueAt(t.getSelectedRow(), 0));
			dia.setVisible(true);
		}));

		var col = "아이템,게임명,아이템명,s_no,i_no".split(",");
		var wid = new int[] { 80, 150, 150, 0, 0 };
		for (int i = 0; i < col.length; i++) {
			t.getColumn(col[i]).setMinWidth(wid[i]);
			t.getColumn(col[i]).setMaxWidth(wid[i]);
		}
		t.setRowHeight(80);

		c.add(lbl("아이템 세트", 0, 20), "North");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 1))));

		for (var rs : getRows("select g_no from item group by g_no")) {
			var g_no = toInt(rs.get(0));
			var cnt = 0;

			var tmp = new JPanel(new GridLayout(1, 0));

			tmp.setBorder(new TitledBorder(getOne("select g_name from game where g_no = ?", g_no)));
			for (var r : getRows("select i_no, i_img from item where g_no = ?", g_no)) {
				var img = getIcon(r.get(1), 80, 80).getImage();
				
				if(getOne("select * from storage s, v2 where s.s_no = v2.s_no and v2.u_no = ? and s.i_no = ?", user.get(0),
						r.get(0)).isEmpty()) {
					img = GrayFilter.createDisabledImage(img);
				}
				
				tmp.add(new JLabel(new ImageIcon(img)));

				cnt = getOne("select * from v2, storage s where v2.s_no = s.s_no and v2.u_no = ? and i_no= ?",
						user.get(0), r.get(0)).isEmpty() ? cnt : cnt + 1;
			}

			tmp.setBackground(cnt == 3 ? Color.yellow : Color.white);

			cc.add(tmp);
		}

		addRow();

		w.setBorder(new LineBorder(Color.black));
		c.setBorder(new LineBorder(Color.black));
	}

	void addRow() {
		m.setRowCount(0);
		for (var rs : getRows(
				"select s.s_no, i_img, g_name, i_name, i.i_no from v2, storage s, game g, item i where v2.s_no = s.s_no and g.g_no = v2.g_no and i.i_no=s.i_no and v2.u_no = ?",
				user.get(0))) {
			rs.set(1, new JLabel(getIcon(rs.get(1), 80, 80)));
			m.addRow(rs.toArray());
		}
	}

	public static void main(String[] args) {
		new LoginFrame();
	}
}

package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class StoragePage extends BasePage {
	DefaultTableModel m = model("아이템,게임명,아이템명,s_no,i_no".split(","));
	JTable t = table(m);

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var l = super.lbl(c, a, st, sz);
		l.setForeground(Color.black);
		return l;
	}

	public StoragePage() {
		super("보관함");

		setLayout(new BorderLayout(20, 10));

		add(lbl("경험치:" + u_exp + "[등급 : " + g_gd[u_gd] + "]", 4, 15), "North");
		add(w = new JPanel(new BorderLayout()), "West");
		add(c = new JPanel());

		w.add(lbl("보관함", 0, 25), "North");
		w.add(new JScrollPane(t));
		w.add(ws = new JPanel(new FlowLayout(2)), "South");

		ws.add(btn("등록하기", a -> {
		}));

		var col = "아이템,게임명,아이템명,s_no,i_no".split(",");
		var wid = new int[] { 80, 150, 150, 0, 0 };
		for (int i = 0; i < wid.length; i++) {
			t.getColumn(col[i]).setMinWidth(wid[i]);
			t.getColumn(col[i]).setMaxWidth(wid[i]);
		}
		t.setRowHeight(80);

		addRow();

		mf.repaint();
		setOpaque(true);
		setBackground(Color.white);
	}

	private void addRow() {
		m.setRowCount(0);
		for (var rs : getRows(
				"select i_img, g_name, i_name, s.s_no, i.i_no from v2, storage s, game g, item i where v2.s_no = s.s_no and g.g_no =v2.g_no and i.i_no = s.i_no and v2.u_no = ?",
				user.get(0))) {
			rs.set(0, new JLabel(getIcon(rs.get(0), 80, 80)));
			m.addRow(rs.toArray());
		}
	}
}

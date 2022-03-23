package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Search extends BaseFrame {
	DefaultTableModel m = model("지역".split(" "));
	JTable t = table(m, "");
	JComboBox com;
	JTextField txt = new JTextField(15);

	public Search() {
		super("검색", 800, 500);

		add(lblH("방탈출 카페 검색", 2, 1, 30), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));

		c.add(cn = new JPanel(new FlowLayout(4)), "North");
		c.add(sz(new JScrollPane(t), 100, 0), "West");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 3, 5, 5))));

		cn.add(lbl("장르", 0, 12));
		cn.add(com = new JComboBox("전체".split(" ")));
		cn.add(lbl("테마", 0, 12));
		cn.add(txt);
		cn.add(btn("검색", a -> load()));

		m.addRow(new Object[] { "전체" });
		var rs = rs("select a_name from area");
		for (var r : rs) {
			m.addRow(r.toArray());
		}

		t.setRowSelectionInterval(0, 0);

		rs = rs("select g_name from genre");
		for (var r : rs) {
			com.addItem(r.get(0));
		}

		load();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void load() {
		cc.removeAll();

		String cri1 = "", cri2 = "";
		System.out.println(t.getSelectedRow());
		if (t.getSelectedRow() > 0) {
			cri1 = " and a_name='" + t.getValueAt(t.getSelectedRow(), 0) + "'";
		}

		if (com.getSelectedIndex() != 0) {
			cri2 = " and g.g_no=" + com.getSelectedIndex();
		}

		var rs = rs(
				"select c.c_no, c_name from cafe c, theme t, genre g, area a where a.a_no=c.a_no and g.g_no=t.g_no and concat(',', c.t_no, ',') like concat('%,', t.t_no, ',%') and t_name like ? "
						+ cri1 + cri2 + " group by c.c_no",
				"%" + txt.getText() + "%");
		if (rs.isEmpty()) {
			eMsg("검색 결과가 없습니다.");
			com.setSelectedIndex(0);
			t.setRowSelectionInterval(0, 0);
			txt.setText("");
			load();
			return;
		}

		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(new JLabel(img("지점/" + r.get(1).toString().split(" ")[0] + ".jpg", 200, 100)));
			p.add(lbl(r.get(1) + "", 0, 15), "South");
			p.setName(r.get(0) + "");
			p.setBorder(new LineBorder(Color.black));
			p.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					System.out.println(1);
					if (e.getClickCount() == 2) {
						cno = ((JPanel) e.getSource()).getName();
						new Intro().addWindowListener(new Before(Search.this));
					}
				}
			});
			cc.add(p);
		}

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new Search();
	}
}

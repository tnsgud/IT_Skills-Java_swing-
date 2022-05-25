package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Search extends BaseFrame {
	JComboBox com = new JComboBox<>(("전체," + String.join(",", rs("select g_name from genre").stream()
			.flatMap(a -> a.stream()).map(String::valueOf).toArray(String[]::new))).split(","));
	JTextField txt = new JTextField(10);
	DefaultTableModel m = model("지역".split(" "));
	JTable t = table(m, "");

	public Search() {
		super("검색", 800, 500);

		add(lblH("방탈출 카페 검색", 2, 0, 30), "North");
		add(c = new JPanel(new BorderLayout()));

		c.add(cn = new JPanel(new FlowLayout(2)), "North");
		c.add(sz(new JScrollPane(t), 100, 0), "West");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 3, 5, 5))));

		cn.add(lbl("장르", 0));
		cn.add(com);
		cn.add(lbl("테마", 0));
		cn.add(txt);
		cn.add(btn("검색", a -> search()));

		var rs = rs("select a_name from area");
		m.addRow(new Object[] {"전국"});
		for (var r : rs) {
			m.addRow(r.toArray());
		}

		t.setRowSelectionInterval(0, 0);

		search();

		setVisible(true);
	}

	void search() {
		cc.removeAll();

		String c1 = "", c2 = "";

		if (t.getSelectedRow() > 0) {
			c1 = " and a_no = " + t.getSelectedRow();
		}

		if (com.getSelectedIndex() != 0) {
			c2 = " and g_no = " + com.getSelectedIndex();
		}

		var rs = rs(
				"select c.c_no, c_name from cafe c, theme t where concat(',', c.t_no, ',') like concat('%,' , t.t_no,',%') and t_name like ?"
						+ c1 + c2 + " group by c.c_no",
				"%" + txt.getText() + "%");
		if (rs.isEmpty()) {
			eMsg("검색 결과가 없습니다.");
			com.setSelectedIndex(0);
			t.setRowSelectionInterval(0, 0);
			txt.setText("");
			search();
			return;
		}

		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(new JLabel(img("지점/" + r.get(1).toString().split(" ")[0] + ".jpg", 200, 150)));
			p.add(lbl(r.get(1) + "", 0), "South");
			p.setName(r.get(0) + "");
			p.setBorder(new LineBorder(Color.black));
			p.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2) {
						cno = p.getName();
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

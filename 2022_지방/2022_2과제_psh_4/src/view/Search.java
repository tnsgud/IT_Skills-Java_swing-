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
	DefaultTableModel m = model("지역".split(","));
	JTable t = table(m, "");
	JComboBox<String> com;
	JTextField txt = new JTextField(15);

	public Search() {
		super("검색", 800, 500);

		add(lblH("방탈출 카페 검색", 2, 0, 25), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(cn = new JPanel(new FlowLayout(2)), "North");
		c.add(sz(new JScrollPane(t), 100, 0), "West");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 3, 5, 5))));

		cn.add(lbl("장르", 2));
		cn.add(com = new JComboBox<>());
		cn.add(lbl("테마", 2));
		cn.add(txt);
		cn.add(btn("검색", a -> search()));
		com.addItem("전체");
		for (var r : rs("select g_name from genre")) {
			com.addItem(r.get(0) + "");
		}

		m.addRow(new Object[] { "전체" });
		for (var r : rs("select a_name from area")) {
			m.addRow(r.toArray());
		}

		t.setRowSelectionInterval(0, 0);

		search();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setVisible(true);
	}

	private void search() {
		cc.removeAll();
		var rs = rs(
				"select c_no, c_name from cafe c, theme t where concat(',', c.t_no, ',') like concat('%,',t.t_no,',%') and t_name like ?"
						+ (t.getSelectedRow() < 1 ? "" : " and a_no=" + t.getSelectedRow())
						+ (com.getSelectedIndex() == 0 ? "" : " and g_no=" + com.getSelectedIndex()) + " group by c.c_no",
				"%" + txt.getText() + "%");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout());
			p.add(new JLabel(img("지점/" + r.get(1).toString().split(" ")[0] + ".jpg", 200, 100)));
			p.add(lbl(r.get(1) + "", 0), "South");

			p.setName(r.get(0) + "");
			p.setBorder(new LineBorder(Color.black));
			
			p.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if(e.getClickCount() == 2) {
						cno = ((JPanel)e.getSource()).getName();
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

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
	JTextField txt;
	String sql = "";

	public Search() {
		super("검색", 800, 500);

		addRow(m, getResult("select a_name from area"));
		m.insertRow(0, new Object[] { "전국" });
		t.setRowSelectionInterval(0, 0);
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(t.getSelectedRow() == -1) {
					return;
				}
				
				search();
			}
		});
		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		add(lblH("방탈출 카페 검색", 2, 35), "North");
		add(c = new JPanel(new BorderLayout(10, 10)));

		c.add(cn = new JPanel(new FlowLayout(2)), "North");
		c.add(sz(new JScrollPane(t), 80, 0), "West");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 3, 5, 5))));

		cn.add(lbl("장르", 2));
		cn.add(com = new JComboBox<>(
				getResult("select g_name from genre").stream().flatMap(a -> a.stream()).toArray(String[]::new)));
		cn.add(lbl("테마", 2));
		cn.add(txt = new JTextField(15));
		cn.add(btn("검색", a -> search()));

		com.insertItemAt("전체", 0);
		com.setSelectedIndex(0);

		search();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	void search() {
		cc.removeAll();

		var rs = getResult(
				"select c_no, c_name from cafe c, theme t where concat(',', c.t_no, ',') like concat('%,', t.t_no, ',%') and t.t_name like ?"
						+ (com.getSelectedIndex() == 0 ? "" : " and g_no=" + com.getSelectedIndex())
						+ (t.getSelectedRow() < 1 ? "" : " and c.a_no=" + t.getSelectedRow()) + " group by c.c_no",
				"%" + txt.getText() + "%");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout(5, 5));
			p.add(new JLabel(img("지점/" + r.get(1).toString().split(" ")[0] + ".jpg", 200, 100)));
			p.add(lbl(r.get(1) + "", 0), "South");

			p.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2) {
						BaseFrame.cno = ((JPanel) e.getSource()).getName();
						new Introduce().addWindowListener(new Before(Search.this));
					}
				}
			});

			p.setName(r.get(0) + "");
			p.setBorder(new LineBorder(Color.black));

			cc.add(p);
		}

		cc.repaint();
		cc.revalidate();
	}

	public static void main(String[] args) {

		new Search();
	}
}

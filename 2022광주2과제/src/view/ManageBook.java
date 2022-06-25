package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ManageBook extends BaseFrame {
	JComboBox<String> com = new JComboBox<>("전체,총류,철학,종교,사회과학,자연과학,기술과학,예술,언어,문학,역사".split(","));
	JTextField txt = new JTextField();
	DefaultTableModel m = new DefaultTableModel(null, "No,Title,Divisio,Writer,Num,Page,Date".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	String[] gubun = "총류,철학,종교,사회과학,자연과학,기술과학,예술,언어,문학,역사".split(",");
	JTable t = new JTable(m);

	public ManageBook() {
		super("Manage Book", 900, 500);

		var lbl = lbl("Manage Book", 0, 35);
		lbl.setOpaque(true);
		lbl.setForeground(Color.white);
		lbl.setBackground(Color.black);

		add(lbl, "North");
		add(c = new JPanel(new BorderLayout(5, 5)));

		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(new JScrollPane(t));
		c.add(ce = new JPanel(new BorderLayout(5, 5)), "East");

		{
			var tmp = new JPanel(new GridLayout(1, 0));

			tmp.add(lbl("Division", 0));
			tmp.add(com);
			tmp.add(lbl("Search", 0));
			tmp.add(txt);
			tmp.add(btn("검새", a -> {
			}));

			cn.add(tmp, "West");
		}

		{
			var tmp = new JPanel(new FlowLayout(2, 5, 5));

			tmp.add(btn("Book Register", a -> {
			}));
			tmp.add(btn("Close", a -> {
			}));

			cn.add(tmp, "East");
		}

		{
			
		}

		var rs = getRows("select b_title, b_gubun, b_writer, b_gun, b_page, b_date from book order by b_date asc");
		for (var r : rs) {
			r.add(0, rs.indexOf(r) + 1);
			r.set(2, gubun[toInt(r.get(2)) / 100]);
			m.addRow(r.toArray());
		}

		t.setRowSelectionInterval(0, 0);
		
		setVisible(true);
	}

	public static void main(String[] args) {
		new ManageBook().setVisible(true);
	}
}

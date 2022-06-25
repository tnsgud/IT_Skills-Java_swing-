package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Like extends BaseFrame {
	DefaultTableModel m = new DefaultTableModel(null, ",No,Title,Writer,Possible,State".split(",")) {
		public java.lang.Class<?> getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		};
	};
	JTable t = new JTable(m);

	ArrayList<ArrayList<Object>> rs;

	public Like() {
		super("Like", 600, 350);

		rs = getRows("select b_title, b_writer, b_gun, b_gubun from `like` l inner join book b on l.b_no = b.b_no where l.m_no = ?", user.get(0));
		
		for (var r : rs) {
			var row = new ArrayList<>();
			
			r.add(0, rs.indexOf(r)+1);
			
			row.addAll(r);
			row.add(0, false);
			
			m.addRow(row.toArray());
		}

		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(2)), "South");

//		n.add(lbl("You can borrow "++, ABORT))

		setVisible(true);
	}

	public static void main(String[] args) {
		new Like();
	}
}

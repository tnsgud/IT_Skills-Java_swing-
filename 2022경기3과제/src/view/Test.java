package view;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Test extends BaseFrame {
	DefaultTableModel m = new DefaultTableModel(null, "a,b,c,d".split(",")) {
		public java.lang.Class<?> getColumnClass(int columnIndex) {
			return JComponent.class;
		};
	};
	DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
		public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			return (JLabel) value;
		};
	};
	JTable t = new JTable(m);

	public Test() {
		super("", 500, 500);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}
		
		add(t);
		
		m.addRow(new Object[] {lbl("<html><u>hello", 0), lbl("<html><strike>hello", 0)});
	}

	public static void main(String[] args) {
		new Test().setVisible(true);
		;
	}
}

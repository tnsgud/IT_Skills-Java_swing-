package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class Reserve extends BaseFrame {
	DefaultTableModel m = new DefaultTableModel(null, "no,출발지,도착지,출발시간,도착시간, ".split(",")) {
		public boolean isCellEditable(int row, int column) {
			System.out.println(column);
			return column == 5;
		};
	};
	JTable t = table(m);
	ArrayList<Integer> sno;

	public Reserve(ArrayList<Integer> sno) {
		super(1200, 500);
		this.sno = sno;
		setLayout(new BorderLayout(10, 10));

		add(lbl("예매", 2, 35), "North");
		add(new JScrollPane(t));

		t.getColumnModel().getColumn(5).setCellEditor(new Btn());
		t.getColumnModel().getColumn(5).setCellRenderer(new Btn());

		data();

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);
		sno.forEach(no -> {
			var rs = rs(
					"select concat(l11name, ' ', l12name), concat(l21name, ' ', l22name), time_format(s.date, '%H:%m:%I'), time_format(addtime(s.date,  s.elapsed_time), '%H:%m%i') from v1, schedule s where v1.sno = s.no and sno = ?",
					no);
			try {
				if (rs.next()) {
					var row = new Object[m.getColumnCount()];
					for (int i = 0; i < rs.getMetaData().getColumnCount()+1; i++) {
						if (i == 0) {
							row[i] = rs.getRow();
						} else {
							row[i] = rs.getString(i);
						}
					}
					row[5] = new Btn();
					m.addRow(row);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	class Btn implements TableCellEditor, TableCellRenderer {
		JButton btn;

		public Btn() {
			btn = new JButton("예매");
			btn.addActionListener(a -> {
				JOptionPane.showConfirmDialog(null, "tlqkf", "안내", JOptionPane.YES_NO_OPTION);
			});
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return false;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean stopCellEditing() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void cancelCellEditing() {
			// TODO Auto-generated method stub

		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			// TODO Auto-generated method stub

		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return btn;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			return btn;
		}

	}

	public static void main(String[] args) {
		uno = 1;
		new UserMain();
	}
}

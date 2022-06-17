package view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Building extends BasePage {

	DefaultTableModel m = new DefaultTableModel(null, "이름,종류,설명,시작시간,종료시간,사진,no".split(",")) {
		public java.lang.Class<?> getColumnClass(int columnIndex) {
			return JComponent.class;
		};

		public boolean isCellEditable(int row, int column) {
			return column != 1;
		};
	};
	DefaultTableCellRenderer d = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (value instanceof JComponent) {
				return (JComponent) value;
			} else {
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};
	};
	JTable t = new JTable(m);
	ArrayList<Integer> rows = new ArrayList<>();
	HashMap<Integer, File> files = new HashMap<>();

	public Building() {
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(4)), "South");

		s.add(btn("저장", a -> {
			for (var row : rows) {
				var data = new ArrayList<>();
				for (int i = 0; i < t.getColumnCount(); i++) {
					if (i == 1 || i == 5) {
						continue;
					}

					data.add(t.getValueAt(row, i));
				}

				execute("update building set name=?, info=?, open=?, close=? where no=?", data.toArray());

				if (files.containsKey(row)) {
					try {
						execute("update building set img=? where no=?", new FileInputStream(files.get(row)),
								data.get(data.size() - 1));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}));

		d.setHorizontalAlignment(0);
		t.setSelectionMode(0);

		t.setRowHeight(80);
		t.setDefaultRenderer(JComponent.class, d);

		var col = "no,사진".split(",");
		var width = new int[] { 0, 120 };
		for (int i = 0; i < width.length; i++) {
			t.getColumn(col[i]).setMinWidth(width[i]);
			t.getColumn(col[i]).setMaxWidth(width[i]);
		}

		col = "이름,설명".split(",");
		for (int j = 0; j < width.length; j++) {
			t.getColumn(col[j]).setCellEditor(new DefaultCellEditor(new JTextField()));
		}

		col = "시작시간,종료시간".split(",");
		for (int k = 0; k < width.length; k++) {
			t.getColumn(col[k]).setCellEditor(new Spin(k + 3));
		}

		for (var rs : getRows(
				"select name, type, info, time_format(open, '%H:%i'), time_format(close,'%H:%i'), img, no from building where type < 3")) {
			rs.set(1, "진료소,병원,거주지".split(",")[toInt(rs.get(1))]);
			rs.set(5, new JLabel(img(rs.get(5), 120, 80)));
			m.addRow(rs.toArray());
		}

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 1 || t.getSelectedRow() == -1 || t.getSelectedColumn() != 5) {
					return;
				}

				var jfc = new JFileChooser();
				jfc.resetChoosableFileFilters();
				jfc.addChoosableFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "JPG files";
					}

					@Override
					public boolean accept(File f) {
						return f.getName().endsWith("jpg") || f.isDirectory();
					}
				});

				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					var file = jfc.getSelectedFile();
					t.setValueAt(new JLabel(img(file.getAbsolutePath(), 120, 80)), t.getSelectedRow(), 5);
					files.put(t.getSelectedRow(), file);
				}
			}
		});

		t.getModel().addTableModelListener(e -> {
			if (e.getType() == 0) {
				rows.add(t.getSelectedRow());
			}
		});

		setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	class Spin extends DefaultCellEditor {
		LocalTime date = LocalTime.of(4, 30);
		String[] open_times, close_times;
		JSpinner spinner;
		JSpinner.DefaultEditor editor;

		public Spin(int column) {
			super(new JTextField());
			open_times = Stream.generate(() -> {
				date = date.plusMinutes(30);
				return date + "";
			}).limit(10).toArray(String[]::new);
			date = LocalTime.of(18, 30);
			close_times = Stream.generate(() -> {
				date = date.plusMinutes(30);
				return date + "";
			}).limit(10).toArray(String[]::new);
			spinner = new JSpinner(new SpinnerListModel(column == 3 ? open_times : close_times));
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			if (table.getValueAt(row, 1).toString().equals("거주지")) {
				eMsg("거주지는 수정이 불가합니다.");
				return null;
			}

			spinner.setValue(t.getValueAt(row, column));
			return spinner;
		}

		@Override
		public Object getCellEditorValue() {
			return spinner.getValue();
		}
	}

	public static void main(String[] args) {
		mf.swapPage(new AdminPage());
		mf.setVisible(true);
	}
}
package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import db.DB;
import tool.Tool;

public class UserManage extends JPanel implements Tool {
	JTextField txt;
	JPopupMenu pop = new JPopupMenu();
	JMenuItem item = new JMenuItem("예매 조회");
	DefaultTableModel m = new DefaultTableModel(null, "순번,아이디,비밀번호,성명,이메일,포인트,예매수".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return column != 0;
		};
	};
	JTable t = table(m);
	String key = "%%";
	boolean isChange = false;

	JPanel n, s;

	public UserManage() {
		ui();
		data();
		event();
	}

	private void event() {
		m.addTableModelListener(a -> {
			isChange = true;
		});

		item.addActionListener(a -> {
			new Booking(toInt(t.getValueAt(t.getSelectedRow(), 0)));
		});

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() != -1 && e.getButton() == 3) {
					pop.show(t, e.getX(), e.getY());
				}
			}
		});
	}

	private void data() {
		m.setRowCount(0);
		var rs = DB.rs("select * from user where name like ?", key);
		try {
			while (rs.next()) {
				var row = new Object[t.getColumnCount()];
				for (int i = 0; i < row.length-1; i++) {
					row[i] = rs.getString(i + 1);
				}
				row[6] = DB.getOne("select count(*) from reservation where user_no=?", row[0]);
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		var n_e = new JPanel(new FlowLayout(2));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(2)), "South");

		n.add(lbl("사용자 관리", 2, 35), "West");
		n.add(n_e, "East");

		n_e.add(txt = new JHintField(12, "성명"));
		n_e.add(btn("사용자 조회", a -> {
			key = txt.getText().isEmpty() ? "%%" : "%" + txt.getText() + "%";
			data();
		}));

		for (var cap : "저장,삭제".split(",")) {
			s.add(btn(cap, a -> {
				if (!isChange || t.getSelectedRow() == -1) {
					return;
				}

				if (a.getActionCommand().contentEquals("저장")) {
					for (int i = 0; i < t.getRowCount(); i++) {
						var data = new ArrayList<Object>();
						for (int j = 1; j < t.getColumnCount() - 1; j++) {
							data.add(t.getValueAt(i, j));
						}

						data.add(i + 1);
						DB.execute("update user set id=?,pwd=?,name=?,email=?,point=? where no=?", data);
					}

					iMsg("수정내용을 저장 완료했습니다.");
					isChange = false;
				}else {
					DB.execute("delete from user where no=?", t.getValueAt(t.getSelectedRow(), 0));
					data();
					iMsg("삭제를 완료하였습니다.");
				}
			}));
		}

		setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}

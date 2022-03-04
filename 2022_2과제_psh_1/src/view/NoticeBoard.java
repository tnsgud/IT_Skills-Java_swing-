package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class NoticeBoard extends BaseFrame {
	JLabel pagelbl, datelbl;
	DefaultTableModel m = model("번호,제목,아이디,등록일,조회".split(","));
	JTable t = blueTable(m, getOne("select u_id from user where u_no=?", uno));
	JButton prev, next, modify;
	JComboBox<String> com = new JComboBox<>();
	JTextField searchtxt = new JTextField(7), titletxt = new JTextField(10);
	JTextArea area = new JTextArea();
	ArrayList<Object[]> nno = new ArrayList<>();
	int cur = 1, max = 5;

	public NoticeBoard() {
		super("게시판", 600, 450);

		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				var isMe = getOne("select u_id from user where u_no=?", uno)
						.equals(t.getValueAt(t.getSelectedRow(), 2));

				if (e.isVisible()) {
					setSize(600, 450);
				} else {
					if (!isMe) {
						t.setValueAt(toInt(t.getValueAt(t.getSelectedRow(), 4)) + 1, t.getSelectedRow(), 4);
						execute("update notice set n_viewcount = ? where n_no=?", t.getValueAt(t.getSelectedRow(), 4),
								t.getValueAt(t.getSelectedRow(), 0));
					}
					setSize(900, 450);
				}
				e.setVisible(!e.isVisible());

				titletxt.setText(t.getValueAt(t.getSelectedRow(), 1) + "");
				area.setText(getOne("select n_content from notice where n_no=?", t.getValueAt(t.getSelectedRow(), 0)));

				modify.setVisible(isMe);
				titletxt.setEnabled(isMe);
				area.setEnabled(isMe);
			}
		});
	}

	private void data() {
		var rs = rs(
				"select n_no, n_title, u_id, n_date, n_viewcount from notice n, user u where n.u_no= u.u_no and (u.u_no=? or n_open = 1)",
				uno);
		try {
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					row[i] = rs.getString(i + 1);
				}
				nno.add(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		max = nno.size() % 10 == 0 ? nno.size() / 10 : nno.size() / 10 + 1;

		changeTable();
	}

	void changeTable() {
		pagelbl.setText(cur + "/" + max);
		m.setRowCount(0);
		var idx = (cur - 1) * 10;
		for (int i = idx; i < (idx + 10 < nno.size() ? idx + 10 : nno.size()); i++) {
			m.addRow(nno.get(i));
		}
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		var nw = new JPanel(new FlowLayout(1));
		var ne = new JPanel(new FlowLayout(2));
		var en = new JPanel(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(sz(e = new JPanel(new BorderLayout(5, 5)), 300, 0), "East");

		n.add(nw, "West");
		n.add(ne, "East");

		e.add(en, "North");
		e.add(area);
		e.add(datelbl = lbl("", 4), "South");

		nw.add(lbl("페이지 정보: ", 2));
		nw.add(pagelbl = lbl(cur + "/" + max, 2));
		nw.add(prev = btn("◀", a -> {
			cur--;
			pagelbl.setText(cur + "/" + max);
			next.setEnabled(true);
			changeTable();

			if (cur == 1) {
				prev.setEnabled(false);
				return;
			}
		}));
		nw.add(next = btn("▶", a -> {
			cur++;
			
			prev.setEnabled(true);
			changeTable();

			if (cur == max) {
				next.setEnabled(false);
				return;
			}
		}));

		ne.add(lbl("분류 : ", 2));
		ne.add(com);
		ne.add(searchtxt);
		ne.add(btn("검색", a -> {
			nno.clear();
			var rs = rs(
					"select n_no, n_title, u_id, n_date, n_viewcount from notice n, user u where n.u_no= u.u_no and "
							+ (com.getSelectedIndex() == 0 ? "n_title" : "u.u_id") + " like ?",
					"%" + searchtxt.getText() + "%");
			try {
				while (rs.next()) {
					var row = new Object[m.getColumnCount()];
					for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
						row[i] = rs.getString(i + 1);
					}
					nno.add(row);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			max = nno.size() % 10 == 0 ? nno.size() / 10 : nno.size() / 10 + 1;

			changeTable();
		}));
		ne.add(btn("게시물 작성", a -> {
			new Registration().addWindowListener(new Before(this));
		}));

		en.add(lblB("제목:", 2, 15), "West");
		en.add(titletxt);
		en.add(modify = btn("수정", a -> {
			iMsg("수정이 완료되었습니다.");
			execute("update notice set n_content=? where n_no=?", area.getText(), t.getValueAt(t.getSelectedRow(), 0));
			setSize(600, 450);
			e.setVisible(false);
		}), "East");

		t.getColumnModel().getColumn(0).setMaxWidth(80);
		t.getColumnModel().getColumn(4).setMaxWidth(80);
		t.getColumnModel().getColumn(1).setMinWidth(180);

		for (var c : "제목,아이디".split(",")) {
			com.addItem(c);
		}

		area.setLineWrap(true);

		t.setRowHeight(30);
		e.setVisible(false);
		prev.setEnabled(false);
		modify.setVisible(false);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}

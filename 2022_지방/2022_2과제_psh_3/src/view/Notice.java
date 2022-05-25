package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Notice extends BaseFrame {
	DefaultTableModel m = model("번호,제목,아이디,등록일,조회".split(","));
	JTable t = table(m, rs("select u_id from user where u_no=?", uno).get(0).get(0) + "");
	ArrayList<ArrayList<Object>> rs;
	JTextField txt = new JTextField(7), titletxt = new JTextField();
	JTextArea area = new JTextArea();
	JButton prev, next, modify;
	JLabel datelbl, pagelbl;
	int cur = 1, max = 0;
	JComboBox<String> com;

	public Notice() {
		super("게시판", 600, 450);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(sz(e = new JPanel(new BorderLayout(5, 5)), 300, 0), "East");

		n.add(nw = new JPanel(new FlowLayout(1)), "West");
		n.add(ne = new JPanel(new FlowLayout(3)), "East");

		e.add(en = new JPanel(new BorderLayout(5, 5)), "North");
		e.add(area);
		e.add(datelbl = lbl("", 4), "South");

		nw.add(lbl("패이지 정보:", 2));
		nw.add(pagelbl = lbl("", 2));
		nw.add(prev = btn("◀", a -> {
			cur--;
			next.setEnabled(true);
			changeTable();
			if (cur == 1) {
				prev.setEnabled(false);
			}
		}));
		nw.add(next = btn("▶", a -> {
			cur++;
			prev.setEnabled(true);
			changeTable();
			if (cur == max) {
				next.setEnabled(false);
			}
		}));

		ne.add(lbl("분류:", 2));
		ne.add(com = new JComboBox<>("제목,아이디".split(",")));
		ne.add(txt);
		ne.add(btn("검색", a -> {
			data();
		}));
		ne.add(btn("게시물 작석", a -> {
			new NoticeSign().addWindowListener(new Before(this));
		}));

		en.add(lbl("제목:", 2, 15), "West");
		en.add(titletxt);
		en.add(modify = btn("수정", a -> {
			iMsg("수정이 완료되었습니다.");
			execute("update notice set n_content =?, n_title=? where n_no=?", area.getText(), titletxt.getText(),
					t.getValueAt(t.getSelectedRow(), 0));
			setSize(600, 450);
			e.setVisible(false);
		}), "East");

		for (var c : "번호,조회".split(",")) {
			t.getColumn(c).setMinWidth(80);
			t.getColumn(c).setMaxWidth(80);
		}

		area.setLineWrap(true);
		t.setRowHeight(30);
		prev.setEnabled(false);
		e.setVisible(false);

		data();

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e1) {
				var isMe = rs("select u_id from user where u_no=?", uno).get(0).get(0)
						.equals(t.getValueAt(t.getSelectedRow(), 2));
				setSize(e.isVisible() ? 600 : 900, 450);
				e.setVisible(!e.isVisible());

				if (!isMe && e.isVisible()) {
					t.setValueAt(toInt(t.getValueAt(t.getSelectedRow(), 4)) + 1, t.getSelectedRow(), 4);
					execute("update notice set n_viewcount=? where n_no=?", t.getValueAt(t.getSelectedRow(), 4),
							t.getValueAt(t.getSelectedRow(), 0));
				}

				titletxt.setText(t.getValueAt(t.getSelectedRow(), 1) + "");
				area.setText(rs("select n_content from notice where n_no=?", t.getValueAt(t.getSelectedRow(), 0)).get(0)
						.get(0) + "");
				datelbl.setText("작성일:" + t.getValueAt(t.getSelectedRow(), 3));
				modify.setVisible(isMe);
				titletxt.setEnabled(isMe);
				area.setEnabled(isMe);
			}
		});

		setVisible(true);
	}

	private void data() {
		rs = rs("select n_no, n_title, u_id, n_date, n_viewcount from notice n, user u where u.u_no=n.u_no and "
				+ (com.getSelectedIndex() == 0 ? "n_title" : "u_id") + " like ? and (n_open= 1  or u.u_no=?)",
				"%" + txt.getText() + "%", uno);

		max = rs.size() % 10 == 0 ? rs.size() / 10 : rs.size() / 10 + 1;

		changeTable();
	}

	private void changeTable() {
		pagelbl.setText(cur + "/" + max);
		m.setRowCount(0);
		var idx = (cur - 1) * 10;
		for (int i = idx; i < (idx + 10 < rs.size() ? idx + 10 : rs.size()); i++) {
			m.addRow(rs.get(i).toArray());
		}
	}

	public static void main(String[] args) {
		uno = 1;
		new Notice();
	}
}

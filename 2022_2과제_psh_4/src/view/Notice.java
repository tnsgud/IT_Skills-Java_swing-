package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.MouseAdapter;
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
	JTable t = table(m, user.get(1) + "");
	JComboBox<String> com = new JComboBox<>("제목,아이디".split(","));
	JButton prev, next, mod;
	JLabel pagelbl = lbl("", 2), datelbl = lbl("", 4);
	int cur = 1, max = 0;
	ArrayList<ArrayList<Object>> rs;
	JTextField txt = new JTextField(7), titletxt = new JTextField();
	JTextArea area = new JTextArea();

	public Notice() {
		super("게시판", 600, 400);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(sz(e = new JPanel(new BorderLayout()), 300, 0), "East");

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		nw.add(pagelbl = lbl("페이지 정보 : " + cur + "/" + max, 2));
		nw.add(prev = btn("◀", a -> {
			cur--;
			change();
			next.setEnabled(true);
			if (cur == 1) {
				prev.setEnabled(false);
			}
		}));
		nw.add(next = btn("▶", a -> {
			cur++;
			change();
			prev.setEnabled(true);
			if (cur == max) {
				next.setEnabled(false);
			}
		}));
		prev.setEnabled(false);

		ne.add(lbl("분류 :", 2));
		ne.add(com);
		ne.add(txt);
		ne.add(btn("검색", a -> data()));
		ne.add(btn("게시물 작성", a -> {
			new Posting().addWindowListener(new Before(this));
		}));

		e.add(en = new JPanel(new BorderLayout()), "North");
		e.add(area);
		en.add(lbl("제목:", 2), "West");
		en.add(titletxt);
		en.add(mod = btn("수정", a -> {
			iMsg("수정이 완료되었습니다.");
			execute("update notice set n_title=?, n_content=? where n_no=?", titletxt.getText(), area.getText(),
					t.getValueAt(t.getSelectedRow(), 0));
			
		}), "East");

		area.setLineWrap(true);

		e.setVisible(false);

		data();

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e1) {
				if (t.getSelectedRow() == -1)
					return;
				var ty = user.get(1).equals(t.getValueAt(t.getSelectedRow(), 2));
				e.setVisible(!e.isVisible());
				setSize(e.isVisible() ? 900 : 600, 400);
				if (e.isVisible()) {
					mod.setVisible(ty);
					titletxt.setEnabled(ty);
					area.setEnabled(ty);
					titletxt.setText(t.getValueAt(t.getSelectedRow(), 1) + "");
					area.setText(rs("select n_content from notice where n_no=?", t.getValueAt(t.getSelectedRow(), 0))
							.get(0).get(0) + "");
					datelbl.setText("작성일:" + t.getValueAt(t.getSelectedRow(), 3));
					t.setValueAt(toInt(t.getValueAt(t.getSelectedRow(), 4)) + 1, t.getSelectedRow(), 4);
					execute("update notice set n_viewcount=n_viewcount+1 where n_no=?",
							t.getValueAt(t.getSelectedRow(), 0));
				}
			};
		});

		setVisible(true);
	}

	private void data() {
		rs = rs("select n_no, n_title, u_id, n_date, n_viewcount from notice n, user u where u.u_no=n.u_no and (u.u_no = ? or n_open=1) and "
				+ (com.getSelectedIndex() == 0 ? "n_title" : "u_id") + " like ?", user.get(0),
				"%" + txt.getText() + "%");
		max = rs.size() % 10 == 0 ? rs.size() / 10 : rs.size() / 10 + 1;
		change();
	}

	void change() {
		m.setRowCount(0);
		pagelbl.setText("페이지 정보 : " + cur + "/" + max);
		var idx = (cur - 1) * 10;
		for (int i = idx; i < (idx + 10 < rs.size() ? idx + 10 : rs.size()); i++) {
			m.addRow(rs.get(i).toArray());
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}

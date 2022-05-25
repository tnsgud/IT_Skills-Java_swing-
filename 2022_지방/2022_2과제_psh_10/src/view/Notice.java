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
	ArrayList<ArrayList<Object>> rs;
	JButton prev, next, save;
	JLabel pagelbl, datelbl;
	JTextField txt = new JTextField(8), title = new JTextField(10);
	JComboBox<String> com = new JComboBox<>("제목,아이디".split(","));
	DefaultTableModel m = model("번호,제목,아이디,등록일,조회".split(","));
	JTable t = table(m, user.get(1) + "");
	JTextArea area = new JTextArea();
	int cur, max;

	public Notice() {
		super("게시판", 700, 400);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(sz(e = new JPanel(new BorderLayout()), 300, 0), "East");

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");
		nw.add(pagelbl = lbl("", 2, 15));
		nw.add(prev = btn("◀", a -> {
			cur--;

			change();
		}));
		nw.add(next = btn("▶", a -> {
			cur++;

			change();
		}));
		ne.add(lbl("분류:", 2, 12));
		ne.add(com);
		ne.add(txt);
		ne.add(btn("검색", a -> {
			data();
		}));
		ne.add(btn("게시물 작성", a -> {
			new Posting(this).addWindowListener(new Before(this));
		}));

		e.add(en = new JPanel(new BorderLayout()), "North");
		e.add(area);
		e.add(datelbl = lbl("", 4, 15), "South");
		en.add(lbl("제목:", 0, 12), "West");
		en.add(title);
		en.add(save = btn("수정", a -> {
			iMsg("수정이 완료되었습니다.");

			execute("update notice set n_title=?, n_content=? where n_no=?", title.getText(), area.getText(),
					t.getValueAt(t.getSelectedRow(), 0));
			t.setValueAt(title.getText(), t.getSelectedRow(), 1);
		}), "East");

		area.setLineWrap(true);

		e.setVisible(false);

		t.setRowHeight(30);
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e1) {
				e.setVisible(!e.isVisible());
				setSize(e.isVisible() ? 900 : 700, 400);

				var isMe = t.getValueAt(t.getSelectedRow(), 2).equals(user.get(1));

				var content = rs("select n_content from notice where n_no=?", t.getValueAt(t.getSelectedRow(), 0))
						.get(0).get(0) + "";
				title.setEnabled(isMe);
				area.setEnabled(isMe);
				save.setVisible(isMe);

				if (!isMe && e.isVisible()) {
					execute("update notice set n_viewcount=n_viewcount+1 where n_no=?",
							t.getValueAt(t.getSelectedRow(), 0));
					t.setValueAt(toInt(t.getValueAt(t.getSelectedRow(), 4)) + 1, t.getSelectedRow(), 4);
				}

				title.setText(t.getValueAt(t.getSelectedRow(), 1) + "");
				area.setText(content);
				datelbl.setText("작성일 : " + t.getValueAt(t.getSelectedRow(), 3));

				repaint();
				revalidate();
			}
		});

		data();

		setVisible(true);
	}

	void data() {
		cur = 1;
		rs = rs("select n_no, n_title, u_id, n_date, n_viewcount from notice n, user u where u.u_no=n.u_no and (u.u_no=? or n_open=1) and "
				+ (com.getSelectedIndex() == 0 ? "n_title" : "u_id") + " like ?", user.get(0),
				"%" + txt.getText() + "%");
		max = rs.size() % 10 == 0 ? rs.size() / 10 : (rs.size() / 10) + 1;

		change();
	}

	private void change() {
		m.setRowCount(0);
		prev.setEnabled(cur != 1);
		next.setEnabled(max != cur);
		pagelbl.setText("페이지 정보 : " + cur + "/" + max);
		var idx = (cur - 1) * 10;
		for (int i = idx; i < idx + 10; i++) {
			m.addRow(rs.get(i).toArray());
		}
	}

	public static void main(String[] args) {
		new Notice();
	}
}

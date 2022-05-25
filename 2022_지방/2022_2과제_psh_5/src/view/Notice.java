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
	JTable t = table(m, user.get(1) + "");
	JLabel page = new JLabel(), date;
	JButton prev, next, save;
	ArrayList<ArrayList<Object>> rs;
	JComboBox com = new JComboBox<>("제목,아이디".split(","));
	JTextField txt = new JTextField(7), title = new JTextField(15);
	JTextArea area = new JTextArea();
	int cur = 1, max;

	public Notice() {
		super("게시판", 600, 400);

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(e = new JPanel(new BorderLayout()), "East");

		e.setVisible(false);

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(ne = new JPanel(new FlowLayout(4)), "East");

		nw.add(lbl("페이지 정보 : ", 2));
		nw.add(page = lbl(cur + "/" + max, 2));
		nw.add(prev = btn("◀", a -> {
			cur--;
			changeTable();
			next.setEnabled(true);
			if (cur == 1) {
				prev.setEnabled(false);
			}
		}));
		nw.add(next = btn("▶", a -> {
			cur++;
			changeTable();
			prev.setEnabled(true);
			if (cur == max) {
				next.setEnabled(false);
			}
		}));

		ne.add(lbl("분류:", 0));
		ne.add(com);
		ne.add(txt);
		ne.add(btn("검색", a -> search()));
		ne.add(btn("게시물 작성", a -> {
			new Posting().addWindowListener(new Before(this));
		}));

		e.add(en = new JPanel(new BorderLayout()), "North");
		e.add(area);
		e.add(date = lbl("", 4), "South");
		en.add(lbl("제목 :", 2), "West");
		en.add(title);
		en.add(save = btn("수정", a -> {
			execute("update notice set n_title=?, n_content=? where n_no=?", title.getText(), area.getText(),
					t.getValueAt(t.getSelectedRow(), 0));
			iMsg("수정이 완료되었습니다.");
		}), "East");

		area.setLineWrap(true);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e1) {
				e.setVisible(!e.isVisible());

				var isMe = t.getValueAt(t.getSelectedRow(), 2).equals(user.get(1));

				var rs = rs("select n_title, n_content, n_date from notice where n_no=?",
						t.getValueAt(t.getSelectedRow(), 0)).get(0);
				title.setText(rs.get(0) + "");
				area.setText(rs.get(1) + "");
				date.setText("작성일 : " + rs.get(2));

				setSize(e.isVisible() ? 900 : 600, 400);
				save.setVisible(isMe);
				title.setEnabled(isMe);
				area.setEnabled(isMe);

				if (!isMe && e.isVisible()) {
					t.setValueAt(toInt(t.getValueAt(t.getSelectedRow(), 4)) + 1, t.getSelectedRow(), 4);
					execute("update notice set n_viewcount=n_viewcount+1 where n_no=?",
							t.getValueAt(t.getSelectedRow(), 0));
				}

				repaint();
				revalidate();
			}
		});

		prev.setEnabled(false);

		search();

		setVisible(true);
	}

	private void search() {
		rs = rs("select n_no, n_title, u_id, n_date, n_viewcount from notice n, user u where n.u_no=u.u_no and (n_open=1 or u.u_no=?) and "
				+ (com.getSelectedIndex() == 0 ? "n_title " : "u_id ") + "like ?", user.get(0),
				"%" + txt.getText() + "%");
		cur = 1;
		max = rs.size() % 10 == 0 ? rs.size() / 10 : rs.size() / 10 + 1;
		changeTable();
	}

	private void changeTable() {
		m.setRowCount(0);
		page.setText(cur + "/" + max);
		prev.setEnabled(cur != 1);
		next.setEnabled(cur != max);
		var idx = (cur - 1) * 10;
		for (int i = idx; i < idx + 10; i++) {
			m.addRow(rs.get(i).toArray());
		}
	}

	public static void main(String[] args) {
		new Notice();
	}
}

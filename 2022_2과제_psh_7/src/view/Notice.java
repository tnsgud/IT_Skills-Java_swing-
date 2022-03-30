package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.DefaultTableModel;

public class Notice extends BaseFrame {
	int cur = 1, max;
	JComboBox com = new JComboBox<>("제목,아이디".split(","));
	JTextField txt = new JTextField(7), title = new JTextField(15);
	DefaultTableModel m = model("번호,제목,아이디,등록일,조회".split(","));
	JTable t = table(m, user.get(1) + "");
	JButton prev, next, save;
	ArrayList<ArrayList<Object>> rs;
	JLabel pagelbl, datelbl;
	JTextArea area = new JTextArea();

	public Notice() {
		super("게시판", 700, 500);

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(sz(e = new JPanel(new BorderLayout()), 200, 0), "East");

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(ne = new JPanel(new FlowLayout(4)), "East");

		nw.add(pagelbl = lbl("", 2));
		nw.add(prev = btn("◀", a -> {
			cur--;

			change();
		}));
		nw.add(next = btn("▶", a -> {
			cur++;

			change();
		}));

		ne.add(lbl("분류:", 2));
		ne.add(com);
		ne.add(txt);
		ne.add(btn("검색", a -> data()));
		ne.add(btn("게시물 작성", a -> {
			new Posting().addWindowListener(new Before(this));
		}));

		e.add(en = new JPanel(new BorderLayout()), "North");
		e.add(area);
		e.add(datelbl = lbl("작성일 : ", 4, 12), "South");

		en.add(lbl("제목:", 2, 15), "West");
		en.add(title);
		en.add(save = btn("수정", a -> {
			t.setValueAt(title.getText(), t.getSelectedRow(), 1);
			execute("update notice set n_title=?, n_content=? where n_no=?", title.getText(), area.getText(),
					t.getValueAt(t.getSelectedRow(), 0));
			iMsg("수정이 완료되었습니다.");
		}), "East");

		data();

		area.setLineWrap(true);
		e.setVisible(false);

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e1) {
				var isMe = t.getValueAt(t.getSelectedRow(), 2).equals(user.get(1));

				e.setVisible(!e.isVisible());

				setSize(e.isVisible() ? 900 : 700, 500);

				if (!isMe) {
					t.setValueAt(toInt(t.getValueAt(t.getSelectedRow(), 4)) + 1, t.getSelectedRow(), 4);
					execute("update notice set n_viewcount=n_viewcount+1 where n_no=?",
							t.getValueAt(t.getSelectedRow(), 0));
				}

				title.setEnabled(isMe);
				area.setEnabled(isMe);
				save.setVisible(isMe);

				title.setText(t.getValueAt(t.getSelectedRow(), 1) + "");
				area.setText(rs("select n_content from notice where n_no=?", t.getValueAt(t.getSelectedRow(), 0)).get(0)
						.get(0) + "");
				datelbl.setText("작성일 : " + t.getValueAt(t.getSelectedRow(), 3));
			};
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void data() {
		rs = rs("select n_no, n_title, u_id, n_date, n_viewcount from notice n, user u where u.u_no =n.u_no and (u.u_no=? or n_open=1) and "
				+ (com.getSelectedIndex() == 0 ? "n_title" : "u_id") + " like ?", user.get(0),
				"%" + txt.getText() + "%");
		max = rs.size() % 10 == 0 ? rs.size() / 10 : (rs.size() / 10) + 1;
		cur = 1;
		change();
	}

	private void change() {
		m.setRowCount(0);

		pagelbl.setText("페이지 정보 :" + cur + "/" + max);

		prev.setEnabled(cur != 1);
		next.setEnabled(max != cur);

		var idx = (cur - 1) * 10;
		for (int i = idx; i < idx + 10; i++) {
			m.addRow(rs.get(i).toArray());
		}
	}

	public static void main(String[] args) {
		new Notice();
	}
}

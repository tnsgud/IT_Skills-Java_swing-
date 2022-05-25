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
import javax.swing.table.DefaultTableModel;

public class Notice extends BaseFrame {
	int cur, max;
	JLabel pagelbl, datelbl;
	ArrayList<ArrayList<Object>> rs;
	DefaultTableModel m = model("번호,제목,아이디,등록일,조회".split(","));
	JTable t = table(m, user.get(1) + "");
	JButton prev, next, save;
	JComboBox com = new JComboBox<>("제목,아이디".split(","));
	JTextField txt, title;
	JTextArea area;

	public Notice() {
		super("게시판", 700, 400);

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(sz(e = new JPanel(new BorderLayout()), 300, 0), "East");

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		nw.add(pagelbl = lbl("", 2, 12));
		nw.add(prev = btn("◀", a -> {
			cur--;
			change();
		}));
		nw.add(next = btn("▶", a -> {
			cur++;
			change();
		}));

		ne.add(lbl("분류 : ", 2));
		ne.add(com);
		ne.add(txt = new JTextField(10));
		ne.add(btn("검색", a -> data()));
		ne.add(btn("게시물 작성", a -> new Posting().addWindowListener(new Before(this))));

		e.add(en = new JPanel(new BorderLayout()), "North");
		e.add(area = new JTextArea());
		e.add(datelbl = lbl("", 4, 12), "South");
		en.add(lbl("제목 :", 2, 15), "West");
		en.add(title = new JTextField(10));
		en.add(save = btn("수정", a -> {
			iMsg("수정이 완료되었습니다.");
			t.setValueAt(title.getText(), t.getSelectedRow(), 1);
			execute("update notice set n_content=?, n_title =? where n_no=?", area.getText(), title.getText(),
					t.getValueAt(t.getSelectedRow(), 0));
		}), "East");

		t.setRowHeight(25);

		data();

		e.setVisible(false);

		t.addMouseListener(new MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e1) {
				var isMe = t.getValueAt(t.getSelectedRow(), 2).equals(user.get(1));

				e.setVisible(!e.isVisible());
				setSize(e.isVisible() ? 900 : 700, 400);

				if (e.isVisible() && !isMe) {
					t.setValueAt(toInt(t.getValueAt(t.getSelectedRow(), 4)) + 1, t.getSelectedRow(), 4);
					execute("update notice set n_viewcount=n_viewcount+1 where n_no=?",
							t.getValueAt(t.getSelectedRow(), 0));
				}

				title.setEnabled(isMe);
				area.setEnabled(isMe);
				save.setVisible(isMe);

				title.setText(t.getValueAt(t.getSelectedRow(), 1) + "");
				datelbl.setText("작성일 : " + t.getValueAt(t.getSelectedRow(), 3));
				area.setText(rs("select n_content from notice where n_no=?", t.getValueAt(t.getSelectedRow(), 0)).get(0)
						.get(0) + "");
			};
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		area.setLineWrap(true);

		setVisible(true);
	}

	private void data() {
		rs = rs("select n_no, n_title, u_id, n_date, n_viewcount from notice n, user u where u.u_no =n.u_no and (n_open = 1 or u.u_no=1) and "
				+ (com.getSelectedIndex() == 0 ? "n_title" : "u_id") + " like ?", "%" + txt.getText() + "%");
		max = rs.size() % 10 == 0 ? rs.size() / 10 : (rs.size() / 10) + 1;
		cur = 1;
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
		new Main();
	}

}

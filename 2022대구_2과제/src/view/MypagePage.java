package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MypagePage extends BasePage {
	String cols[] = "포스터,제목,일시,인원,가격,리뷰,r_no,m_no".split(",");
	JLabel lblImg;
	DefaultTableModel m = model(cols);
	JTable t = table(m);
	DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			var com = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (column == 3) {
				com.setToolTipText(getOne("select r_seat from reservation where r_no = ? and u_no = ?",
						m.getValueAt(row, 6), BaseFrame.user.get(0)));
			}

			return value instanceof JLabel ? (JLabel) value : com;
		}
	};

	public MypagePage() {
		ui();
		data();
		event();
	}

	private void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;

				var pop = new JPopupMenu();
				var i1 = new JMenuItem("리뷰 작성");
				var i2 = new JMenuItem("삭제");

				i1.addActionListener(a -> new CommentDialog(toInt(t.getValueAt(t.getSelectedRow(), 7)), MypagePage.this)
						.setVisible(true));
				i2.addActionListener(a -> {
					var date = LocalDateTime.parse(t.getValueAt(t.getSelectedRow(), 2).toString(),
							DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
					
					System.out.println(LocalDateTime.now());
					System.out.println(date);

					if (date.isBefore(LocalDateTime.now())) {
						eMsg("이미 상영된 영화입니다.");
						return;
					}

					var c_no = getOne("select c_no from comment where m_no=? and u_no=?",
							t.getValueAt(t.getSelectedRow(), 7), BaseFrame.user.get(0));

					iMsg("삭제가 완료되었습니다.");
					
					execute("delete from reservation where r_no = ?", t.getValueAt(t.getSelectedRow(), 6));
					execute("delete from comment where c_no = ?", c_no);

					data();
				});

				if (e.getButton() == 3) {
					if (t.getValueAt(t.getSelectedRow(), 5).toString().equals("-")) {
						pop.add(i1);
					}

					pop.add(i2);
					pop.show(t, e.getX(), e.getY());
				}

			}
		});
	}

	void data() {
		m.setRowCount(0);
		var rs = getRows(
				"select m.m_no, m_title, concat(date_format(r_date, '%Y년 %m월 %d일'), ' ', time_format(r_time, '%H시 %i분')) as r_datetime, r_seat, r_price, ifnull(c_text, '-'), r.r_no, m.m_no from movie m, reservation r left outer join comment c on r.u_no = c.u_no and c.m_no = r.m_no where r.m_no = m.m_no and r.u_no = ? order by r_date asc, r_time asc",
				BaseFrame.user.get(0));
		for (var r : rs) {
			r.set(0, new JLabel(getIcon("./지급자료/image/movie/" + r.get(0) + ".jpg", 60, 80)));
			r.set(3, r.get(3).toString().split("\\.").length + " 명");
			m.addRow(r.toArray());
		}
	}

	private void ui() {
		setLayout(new BorderLayout(0, 20));

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(scroll(t));
		add(s = new JPanel(), "South");

		n.add(lblImg = lblRoundImg(getIcon("./지급자료/image/user/" + BaseFrame.user.get(0) + ".jpg", 100, 100), 100, 100));
		n.add(nc = new JPanel(new GridLayout(0, 1, 5, 5)));

		nc.add(lbl(BaseFrame.user.get(3).toString(), 2, 20));
		nc.add(lbl(BaseFrame.user.get(4) + " " + ",남,여".split(",")[toInt(BaseFrame.user.get(5))], 2));
		nc.add(lbl(getOne("select gr_name from grade where gr_no = ?", BaseFrame.user.get(6)), 2));

		var wid = new int[] { 80, 220, 220, 120, 120 };
		for (int i = 0; i < cols.length - 3; i++) {
			t.getColumn(cols[i]).setMinWidth(wid[i]);
			t.getColumn(cols[i]).setMaxWidth(wid[i]);
		}

		for (var c : "r_no,m_no".split(",")) {
			t.getColumn(c).setMinWidth(0);
			t.getColumn(c).setMaxWidth(0);
		}

		t.setRowHeight(80);

		r.setHorizontalAlignment(0);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}

		setBorder(new EmptyBorder(50, 80, 50, 80));
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}

package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	DefaultTableModel m = model("번호,기업명,모집정보,시급,모집정원,최종학력,성별,합격여부,ano".split(","));
	JTable t = table(m);
	String apply[] = "심사중,합격,불합격".split(",");

	public MyPage() {
		super("마이페이지", 650, 400);

		ui();
		data();
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 3 || t.getSelectedRow() == -1) {
					return;
				}
				
				if(t.getValueAt(t.getSelectedRow(), 7).equals("불합격")) {
					var pop = new JPopupMenu();
					var item = new JMenuItem("삭제");
					pop.add(item);
					item.addActionListener(a->{
						try {
							execute("delete from applicant where a_no=?", t.getValueAt(t.getSelectedRow(), 8));
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						data();
					});
					pop.show(t, e.getX(), e.getY());
				}
			}
		});

		setVisible(true);
	}

	private void data() {
		var rs = getResults(
				"select 1, c_name, e_title, format(e_pay, '#,##0'), e_people, e_graduate, e_gender, a_apply, a_no from applicant a inner join employment e on a.e_no = e.e_no inner join company c on c.c_no=e.c_no where u_no=?",
				uno);
		for (int i = 0; i < rs.size(); i++) {
			rs.get(i).set(0, i + 1);
			rs.get(i).set(5, graduate[toInt(rs.get(i).get(5))]);
			rs.get(i).set(6, gender[toInt(rs.get(i).get(6)) - 1]);
			rs.get(i).set(7, apply[toInt(rs.get(i).get(7))]);
		}

		addRow(rs, m);
	}

	private void ui() {
		add(lblH("Mypage", 0, 1, 30), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(cn = new JPanel(new GridLayout(0, 1)), "North");
		c.add(cc = new JPanel(new BorderLayout()));

		s.add(btn("PDF 인쇄", a -> {
			try {
				t.print();
			} catch (PrinterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		cn.add(lblH("성명 : " + uname, 2, 1, 25));
		cn.add(lblH("성별 : " + ugender, 2, 1, 15));

		cc.add(lblH("최종학력 : " + ugraduate, 2, 1, 15));
		cc.add(new JScrollPane(t));

		var width = new int[] { 60, 100, 120, 75, 60, 100, 50, 70 };
		for (int i = 0; i < width.length; i++) {
			t.getColumnModel().getColumn(i).setMinWidth(width[i]);
			t.getColumnModel().getColumn(i).setMaxWidth(width[i]);
		}

		t.getColumn("ano").setMinWidth(0);
		t.getColumn("ano").setMaxWidth(0);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public static void main(String[] args) {
		uno = "1";
		new MyPage();
	}
}

package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {

	DefaultTableModel m = crt_model("번호,기업명,모집정보,시급,모집정원,최종학력,성별,합격여부,ano".split(","));
	JTable t = crt_table(m);
	String apply[] = "심사중,합격,불합격".split(",");
	JScrollPane p;

	public MyPage() {
		super("Mypage", 1000, 400);
		add(crt_lbl("Mypage", JLabel.CENTER, "HY헤드라인M", Font.BOLD, 30), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		c.add(cn = new JPanel(new GridLayout(0, 1)), "North");
		c.add(cc = new JPanel(new BorderLayout()));

		cn.add(crt_lbl("성명:" + uname, JLabel.LEFT, "HY헤드라인M", Font.BOLD, 25));
		cn.add(crt_lbl("성별:" + ugender, JLabel.LEFT, "HY헤드라인M", Font.BOLD, 15));

		cc.add(crt_lbl("최종학:" + ugraduate, JLabel.LEFT, "HY헤드라인M", Font.BOLD, 15), "North");
		cc.add(p = new JScrollPane(t));

		s.add(crt_evt_btn("PDF 인쇄", a -> {
			try {
				t.print();
			} catch (PrinterException e1) {
				e1.printStackTrace();
			}
		}));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;

				if (t.getValueAt(t.getSelectedRow(), 7).equals("불합격")) {
					JPopupMenu m = new JPopupMenu();
					var item = new JMenuItem("삭제");
					m.add(item);
					item.addActionListener(a -> {
						setValues("delete from applicant where a_no = ? ", t.getValueAt(t.getSelectedRow(), 8));
						data();
					});
					m.show(t, e.getX(), e.getY());
				}
				super.mousePressed(e);
			}
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		data();

		t.getColumn("ano").setMinWidth(0);
		t.getColumn("ano").setMaxWidth(0);

		t.getColumn("번호").setMaxWidth(30);
		t.getColumn("번호").setMinWidth(30);

		t.getColumn("기업명").setMinWidth(120);
		t.getColumn("기업명").setMaxWidth(120);

		t.getColumn("시급").setMinWidth(80);
		t.getColumn("시급").setMaxWidth(80);

		t.getColumn("모집정원").setMinWidth(60);
		t.getColumn("모집정원").setMaxWidth(60);

		t.getColumn("성별").setMinWidth(50);
		t.getColumn("성별").setMaxWidth(50);

		t.getColumn("합격여부").setMinWidth(50);
		t.getColumn("합격여부").setMaxWidth(50);

		t.getColumn("최종학력").setMinWidth(100);
		t.getColumn("최종학력").setMaxWidth(100);

		setVisible(true);
	}

	void data() {
		var rs = getResults(
				"select 1, c_name, e_title, format(e_pay,'#,##0'), e_people, e_graduate, e_gender, a_apply, a_no from applicant a inner join employment e on a.e_no = e.e_no inner join company c on c.c_no = e.c_no where u_no = ?",
				uno);

		for (int i = 0; i < rs.size(); i++) {
			rs.get(i).set(0, i + 1);
			rs.get(i).set(5, graduate[toInt(rs.get(i).get(5))]);
			rs.get(i).set(6, gender[toInt(rs.get(i).get(6)) - 1]);
			rs.get(i).set(7, apply[toInt(rs.get(i).get(7))]);
		}
		addRow(rs, m);
	}

}

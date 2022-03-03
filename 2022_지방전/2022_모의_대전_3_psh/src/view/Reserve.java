package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Reserve extends BaseFrame {
	String[] cap = "장소,출연,가격,날짜".split(",");
	DefaultTableModel m = model("pno,날짜,여유좌석".split(","));
	JTable t = table(m);
	JScrollPane scr = new JScrollPane(t);
	ArrayList<String> date = new ArrayList<String>();

	public Reserve() {
		super("예매", 600, 300);

		data();
		ui();

		setVisible(true);
	}

	private void data() {
		var rs = rs(
				"select p.p_no, date_format(p_date, '%m. %d. ') as date, t_seat from perform p, ticket t where p.p_no=t.p_no and p_name = ?",
				getOne("select p_name from perform where p_no=?", pno));
		var p_no = 0;
		var date = "";
		var t_seat = 60;
		
		try {
			while(rs.next()) {
				if(date.equals("")) {
					date = rs.getString(2);
					p_no = rs.getInt(1);
				}
				
				if(!date.equals(rs.getString(2))) {
					m.addRow(new Object[] {p_no, date, t_seat});
					t_seat = 60;
					date = rs.getString(2);
					p_no = rs.getInt(1);
				}
				
				t_seat -= rs.getString(3).split(",").length;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		var c_c = new JPanel(new GridLayout(0, 1));

		add(lbl(getOne("select p_name from perform where p_no-?", pno), 2, 35), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		add(sz(scr, 150, 150), "East");
		add(s = new JPanel(new FlowLayout(2)), "South");

		c.add(img("공연사진/" + getOne("select pf_no from perform where p_no=?", pno) + ".jpg", 150, 150), "West");
		c.add(c_c);

		scr.setBorder(new LineBorder(Color.black));
		c.setBorder(new LineBorder(Color.black));

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		var dt = new DefaultTableCellRenderer();
		dt.setHorizontalAlignment(SwingConstants.LEFT);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dt);
		}

		s.add(sz(btn("예매하기", a -> {
			if(!isLogin) {
				var an = JOptionPane.showConfirmDialog(null, "회원만이 가능한 서비스 입니다.\n로그인 하시겠습니까?", "로그인", JOptionPane.YES_NO_OPTION);
				if(an == JOptionPane.YES_OPTION) {
					new Login().addWindowListener(new Before(Reserve.this));
				}
				
				return;
			}
			
			var d = t.getValueAt(t.getSelectedRow(), 1).toString().split(". ");
			var date = LocalDate.of(2021, toInt(d[0]), toInt(d[1]));
			if(date.toEpochDay() < LocalDate.parse("2021-10-06").toEpochDay()) {
				eMsg("종료된 공연입니다.");
				return;
			}
			
			pno = toInt(t.getValueAt(t.getSelectedRow(), 0));
			new Stage().addWindowListener(new Before(Reserve.this));
		}), 150, 25));

		
		for (int i = 0; i < t.getRowCount(); i++) {
			if (t.getValueAt(i, 0).equals(pno)) {
				t.setRowSelectionInterval(0, i);
				break;
			}
		}

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				addlbl(c_c);
			}
		});

		addlbl(c_c);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

	}

	private void addlbl(JPanel p) {
		p.removeAll();

		var rs = rs("select p_place, p_actor, format(p_price, '#,##0'), p_date from perform where p_no=?",
				t.getValueAt(t.getSelectedRow(), 0));

		try {
			if (rs.next()) {
				for (int i = 0; i < cap.length; i++) {
					var t = new JPanel(new FlowLayout(0));
					t.add(lbl(cap[i] + ":" + rs.getString(i + 1), 2, 15));
					p.add(t);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		repaint();
		revalidate();
	}
}

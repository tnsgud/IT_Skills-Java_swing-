package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import db.DB;
import model.Perform;
import ui.BaseFrame.Before;

public class Reserve extends BaseFrame {
	String[] cap = "장소,출연,가격,날짜".split(",");
	DefaultTableModel m = model("p_no,날짜,여유좌석".split(","));
	JTable t = table(m);
	JScrollPane scr = new JScrollPane(t);
	ArrayList<String> datas = new ArrayList<String>();

	public Reserve() {
		super("예매", 600, 300);

		dtcr.setHorizontalAlignment(SwingConstants.LEFT);

		setLayout(new BorderLayout(10, 10));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		var c_c = new JPanel(new GridLayout(0, 1));
		var s = new JPanel(new FlowLayout(2));

		add(lbl(perform.p_name, 2, 25), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		add(sz(scr, 150, 150), "East");
		add(s, "South");

		scr.setBorder(new LineBorder(Color.black));
		c.setBorder(new LineBorder(Color.black));
		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		c.add(new JLabel(img("공연사진/" + perform.pf_no, 150, 150)), "West");
		c.add(c_c);

		try {
			var rs = DB.rs(
					"select p.p_no, date_format(p.p_date, '%m. %d.') date, t_seat from perform p, ticket t where p.p_no=t.p_no and p_name like ?",
					"%" + perform.p_name + "%");
			var p_no = 0;
			var date = "";
			var t_seat = 60;
			while (rs.next()) {
				if (date.equals("")) {
					date = rs.getString(2);
					p_no = rs.getInt(1);
				}
				
				if(!date.equals(rs.getString(2))) {
					m.addRow(new Object[] {p_no, date, t_seat});
					t_seat = 60;
					date = rs.getString(2);
					p_no = rs.getInt(1);
				}
				
				t_seat -=rs.getString(3).split(",").length;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		s.add(sz(btn("예매하기", a-> {
			if(user == null) {
				var y = JOptionPane.showConfirmDialog(null, "회원만 가능한 서비스입니다.\n로그인하시겠습니까?", "로그인", JOptionPane.YES_NO_OPTION);
				if(y == 0) {
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
			
			perform= DB.getModel(Perform.class, "select * from perform where p_no=?", t.getValueAt(t.getSelectedRow(), 0));
			new Stage().addWindowListener(new Before(Reserve.this));
		}), 150, 25));
		
		for (int i = 0; i < t.getRowCount(); i++) {
			if(t.getValueAt(i, 0).equals(perform.p_no)) {
				t.setRowSelectionInterval(i, 0);
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
		
		setVisible(true);
	}
	
	private void addlbl(JPanel p) {
		p.removeAll();
		try {
			var rs=  DB.rs("select p_place, p_actor, format(p_price, '#,##0'), p_date from perform where p_no=?", t.getValueAt(t.getSelectedRow(), 0));
			if(rs.next()) {
				for (int i = 0; i < cap.length; i++) {
					var t = new JPanel(new FlowLayout());
					t.add(lbl(cap[i]+":"+rs.getString(i+1), 2));
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

	public static void main(String[] args) {
		DB.execute("use 2021전국");
		perform = DB.getModel(Perform.class, "select * from perform where p_no=?", 1);
		new Reserve();
	}
}

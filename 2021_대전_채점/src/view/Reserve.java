package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Reserve extends BaseFrame {
	String[] cap = "장소,출연,가격,날짜".split(","), f = "place,actor,price,date".split(",");
	DefaultTableModel m = model("pno,날짜,여유좌석".split(","));
	JTable t = table(m);
	JScrollPane scr = new JScrollPane(t);
	JLabel lbl[] = new JLabel[cap.length];

	public Reserve() {
		super("예매", 500, 250);

		data();
		ui();

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				pno = toInt(t.getValueAt(t.getSelectedRow(), 0));
				for (int i = 0; i < lbl.length; i++) {
					lbl[i].setText(getOne("select " + (i == 2 ? "format(p_" + f[i] + ", '#,##0')" : "p_" + f[i])
							+ " from perform where p_no=?", pno));
				}
			}
		});

		setVisible(true);
	}

	private void data() {
		var rs = rs(
				"select p.p_no, date_format(p_date, '%m. %d. ') as date, t_seat from perform p, ticket t where p.p_no=t.p_no and p_name=?",
				getOne("select p_name from perform where p_no=?", pno));
		var p_no = 0;
		var date = "";
		var t_seat = 60;

		try {
			while (rs.next()) {
				if (date.equals("")) {
					date = rs.getString(2);
					p_no = rs.getInt(1);
				}

				if (!date.equals(rs.getString(2))) {
					m.addRow(new Object[] { p_no, date, t_seat });
					t_seat = 60;
					date = "";
				}

				t_seat -= rs.getString(3).split(",").length;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		var c_c = new JPanel(new GridLayout(0, 1));

		setLayout(new BorderLayout(10, 10));

		add(lbl(getOne("select p_name from perform where p_no=?", pno), 2, 25), "North");
		add(c = new JPanel(new BorderLayout(10, 10)));
		add(e = new JPanel(new BorderLayout(10, 10)), "East");
		add(s = new JPanel(new BorderLayout(10, 10)), "South");

		c.add(img("공연사진/" + getOne("select pf_no from perform where p_no=?", pno) + ".jpg", 130, 130), "West");
		c.add(c_c);

		e.add(sz(scr, 150, 100));
		s.add(btn("예매하기", a -> {
			if (!isLogin) {
				var an = JOptionPane.showConfirmDialog(null, "회원만이 가능한 서비스 입니다.\n로그인 하시겠습니까?", "로그인",
						JOptionPane.YES_NO_OPTION);
				if (an == JOptionPane.YES_OPTION) {
					new Login().addWindowListener(new Before(Reserve.this));
				}
				
				return;
			}
			
			new Stage();
		}), "East");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i] + ":", 0, 15), 50, 20));
			p.add(lbl[i] = lbl(getOne("select " + (i == 2 ? "format(p_" + f[i] + ", '#,##0')" : "p_" + f[i])
					+ " from perform where p_no=?", pno), 0, 15));
			c_c.add(p);
		}

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		var dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.LEFT);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}

		for (int j = 0; j < t.getRowCount(); j++) {
			if (t.getValueAt(j, 0).equals(pno)) {
				t.setRowSelectionInterval(0, j);
				break;
			}
		}

		c.setBorder(new LineBorder(Color.black));
		scr.setBorder(new LineBorder(Color.black));
	}
}

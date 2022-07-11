package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Reservation extends BasePage {
	int curidx;
	String cols[] = "번호,날짜,출발지,도착지,결제금액".split(",");
	ArrayList<ArrayList<Object>> data;
	DefaultTableModel m = new DefaultTableModel(null, cols);
	JTable t = new JTable(m);
	JLabel prev, next;
	JLabel ticket = new JLabel(
			new ImageIcon(Toolkit.getDefaultToolkit().getImage("./datafiles/티켓.png").getScaledInstance(350, 150, 4)) {
				public synchronized void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
					super.paintIcon(c, g, x, y);

					if (data == null || data.isEmpty()) {
						return;
					}

					var list = data.get(curidx);
					var g2 = (Graphics2D) g;

					g2.setColor(Color.black);
					g2.setFont(new Font("맑은 고딕", 1, 30));

					g2.drawString(list.get(0).toString() + " → " + list.get(1), 10, 60);

					g2.setFont(new Font("맑은 고딕", 1, 15));
					g2.drawString(list.get(2).toString() + " ㆍ " + list.get(3).toString().toUpperCase(), 15, 120);
				};
			});

	public Reservation() {
		BaseFrame.user = getRows("select * from member").get(0);

		setLayout(new GridBagLayout());

		add(c = new JPanel(new BorderLayout()));

		ticket.setBorder(new LineBorder(Color.black));

		c.add(sz(new JScrollPane(t), 300, 200), "North");
		c.add(prev = lbl("◀", 0), "West");
		c.add(ticket);
		c.add(next = lbl("▶", 0), "East");

		var wid = new int[] { 50, 120, 80, 100, 100 };
		for (int i = 0; i < cols.length; i++) {
			t.getColumn(cols[i]).setMinWidth(wid[i]);
			t.getColumn(cols[i]).setMaxWidth(wid[i]);
		}

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1) {
					return;
				}

				if (e.getButton() == 1) {
					var date = LocalDate.parse(t.getValueAt(t.getSelectedRow(), 1).toString().split(" ")[0]);

					if (date.isBefore(now)) {
						eMsg("이미 탑승이 완료되어 티켓을 볼 수 없습니다.");
						return;
					}
					
					showTicket();
				} else if (e.getButton() == 3) {
					var pop = new JPopupMenu();
					var item = new JMenuItem("예약 취소");

					item.addActionListener(a -> {
						execute("delete from reservation where r_no = ?", t.getValueAt(t.getSelectedRow(), 0));
						addRow();
					});

					pop.add(item);

					pop.show(t, e.getX(), e.getY());
				}
			}
		});

		prev.setVisible(false);
		ticket.setVisible(false);
		next.setVisible(false);

		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (curidx == 0) {
					return;
				}

				curidx--;

				ticket.repaint();
				ticket.revalidate();
			}
		});

		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (curidx == data.size() - 1) {
					return;
				}

				curidx++;

				ticket.repaint();
				ticket.revalidate();
			}
		});

		addRow();

		setVisible(true);
	}

	private void showTicket() {
		prev.setVisible(true);
		ticket.setVisible(true);
		next.setVisible(true);

		data = getRows(
				"select a1.a_code, a2.a_code, m_name2, c_seat from reservation r, schedule s, member m, companion c, airport a1, airport a2 where r.s_no = s.s_no and c.r_no = r.r_no and s.s_depart = a1.a_no and s.s_arrival = a2.a_no and r.m_no = m.m_no and r.r_no = ? and m.m_no = ?",
				t.getValueAt(t.getSelectedRow(), 0), BaseFrame.user.get(0));
		
		repaint();
	}

	private void addRow() {
		m.setRowCount(0);
		for (var rs : getRows(
				"select r_no, concat(r_date, ' ', s_time), a1.a_name, a2.a_name, format(r_price, '#,##0') from reservation r, schedule s, airport a1, airport a2 where r.s_no = s.s_no and s.s_depart = a1.a_no and s.s_arrival = a2.a_no and  m_no = ? order by r_date asc",
				BaseFrame.user.get(0))) {
			m.addRow(rs.toArray());
		}
	}

	public static void main(String[] args) {
		new Reservation();
	}
}

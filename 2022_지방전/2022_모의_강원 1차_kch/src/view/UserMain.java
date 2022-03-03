package view;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

public class UserMain extends BaseFrame {

	JHintField dep, arrv, date;
	LocalDate ld = LocalDate.now();
	JPopupMenu p1, p2;
	public UserMain() {
		super("버스 예매", 1150, 600);
		setLayout(new GridLayout(0, 1));
		try {
			dataInit();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		var row1 = new JLabel(getIcon("./지급파일/images/main.jpg"));
		var row2 = new JPanel(new BorderLayout());

		// row1
		row1.setLayout(new BorderLayout());
		var row1_n = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		row1.add(row1_n, "North");
		row1_n.add(themebtn(this));

		for (var cap : "계정,예매,로그아웃".split(",")) {
			row1_n.add(btn(cap, a -> {
				switch (a.getActionCommand()) {
				case "계정":
					var value = JOptionPane.showInputDialog(this, "비밀번호를 입력해주세요.", "입력", JOptionPane.QUESTION_MESSAGE);
					if (value == null)
						return;

					if (value.isEmpty()) {
						eMsg("공란을 확인해주세요.");
						return;
					}

					if (value.equals(upwd))
						new Account().setVisible(true);

					break;
				case "예매":
					new Ticket().setVisible(true);
					break;
				default:
					dispose();
				}
			}));
		}

		var row1_c = new JPanel(new BorderLayout());
		var row1_cc = new JPanel(new BorderLayout());
		var row1_ccc = new JPanel(new FlowLayout(FlowLayout.LEFT));

		row1.add(row1_c);
		row1_c.add(row1_cc);
		row1_cc.setBorder(new EmptyBorder(20, 20, 20, 20));
		row1_cc.add(lbl("예매", JLabel.LEFT, 20), "North");
		row1_cc.add(row1_ccc);
		row1_ccc.add(dep = new JHintField("출발지", 20));
		row1_ccc.add(btn("<html>←<br>→", a -> {
			if (dep.toString().isEmpty() || arrv.toString().isEmpty())
				return;
			var tmp = dep.toString();
			dep.setText(arrv + "");
			arrv.setText(tmp);

		}));
	
		
		row1_ccc.add(arrv = new JHintField("도착지", 20));
		row1_ccc.add(date = new JHintField("날짜", 20));
		
		dep.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3) {
					p1.show(dep, e.getX(), e.getY());
				}
			};
		});
		
		arrv.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3) {
					p2.show(arrv, e.getX(), e.getY());
				}
			};
		});
		row1_ccc.add(sz(btn("조회", a -> {
			if (dep.toString().isEmpty() || arrv.toString().isEmpty() || date.toString().isEmpty()) {
				eMsg("출발지, 도착지, 날짜 중 공란이 있습니다.");
				return;
			}

			var ar = arrv.toString().split(" ")[1];
			var d = dep.toString().split(" ")[1];
			try {

				String sql = "select no, departure_location2_no, arrival_location2_no, date, elapsed_time, time(date) ,time(date_add(date, interval elapsed_time hour_second))  from schedule where departure_location2_no = '"
						+ Arrays.asList(loc2).indexOf(d) + "' and arrival_location2_no = '"
						+ Arrays.asList(loc2).indexOf(ar) + "' and date(date) = '" + date + "'";
				var rs = stmt.executeQuery(sql);

				if (rs.next()) {
					new Reserve(sql).setVisible(true);
				} else {
					eMsg("예매 가능한 일정이 없습니다.");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}), 150, 30));

		add(row1);
		row1_n.setOpaque(false);
		row1_c.setOpaque(false);
		row1_c.setBorder(new EmptyBorder(60, 100, 60, 100));

		p1 = locPopup(dep, 0, 0, (int) dep.getPreferredSize().getWidth(), 300);
		p2 = locPopup(arrv, 0, 0, (int) arrv.getPreferredSize().getWidth(), 300);
		datePopup(date);
		// row2
		row2.add(lbl("추천 여행지", JLabel.LEFT, 20), "North");
		var row2_c = new JPanel(new GridLayout(1, 0, 20, 20));
		try {
			var rs = stmt.executeQuery(
					"SELECT location_no, ri.img, ri.recommend_no FROM recommend r inner join recommend_info ri on r.no = ri.recommend_no where title = 1");
			while (rs.next()) {
				var temp = new JPanel(new BorderLayout());
				var img = new JLabel(
						new ImageIcon(Toolkit.getDefaultToolkit().createImage(rs.getBinaryStream(2).readAllBytes())
								.getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
				if (row2_c.getComponents().length % 2 != 0)
					temp.setBorder(new EmptyBorder(20, 0, 0, 0));
				else
					temp.setBorder(new EmptyBorder(0, 0, 20, 0));

				img.setBorder(new TitledBorder(loc1[rs.getInt(1)]));

				JPopupMenu menu = new JPopupMenu();
				var item1 = new JMenuItem("상세설명");
				var item2 = new JMenuItem("예매");
				final var rno = rs.getInt(3);
				final var lno = rs.getInt(1);
				item1.addActionListener(a -> {
					new Detail(rno).setVisible(true);
				});
				item2.addActionListener(a -> {
					if (dep.toString().isEmpty())
						ShownlocPopup(dep, lno);
					else
						ShownlocPopup(arrv, lno);
				});
				menu.add(item1);
				menu.add(item2);
				temp.add(img);
				img.setComponentPopupMenu(menu);
				row2_c.add(temp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		row2.add(row2_c);
		add(row2);
		row2.setBorder(new EmptyBorder(20, 20, 20, 20));
		setVisible(true);
	}

	void datePopup(JTextComponent jt) {
		JPopupMenu menu = new JPopupMenu();
		menu.setPreferredSize(new Dimension((int) jt.getPreferredSize().getWidth(), 100));

		JLabel year = new JLabel(ld.getYear() + "", JLabel.CENTER),
				month = new JLabel(ld.getMonthValue() + "", JLabel.CENTER),
				day = new JLabel(ld.getDayOfMonth() + "", JLabel.CENTER);

		JPanel yp;
		JPanel mp;
		JPanel dp;

		menu.setLayout(new GridLayout(1, 0));
		menu.add(yp = new JPanel(new BorderLayout()));
		menu.add(mp = new JPanel(new BorderLayout()));
		menu.add(dp = new JPanel(new BorderLayout()));

		yp.add(btn("▲", a -> {
			ld = ld.plusYears(1);
			year.setText(ld.getYear() + "");
			month.setText(ld.getMonthValue() + "");
			day.setText(ld.getDayOfMonth() + "");
		}), "North");

		mp.add(btn("▲", a -> {
			ld = ld.plusMonths(1);
			year.setText(ld.getYear() + "");
			month.setText(ld.getMonthValue() + "");
			day.setText(ld.getDayOfMonth() + "");
		}), "North");
		dp.add(btn("▲", a -> {
			ld = ld.plusDays(1);
			year.setText(ld.getYear() + "");
			month.setText(ld.getMonthValue() + "");
			day.setText(ld.getDayOfMonth() + "");
		}), "North");

		yp.add(year);
		mp.add(month);
		dp.add(day);

		yp.add(btn("▼", a -> {
			ld = ld.plusYears(-1);
			year.setText(ld.getYear() + "");
			month.setText(ld.getMonthValue() + "");
			day.setText(ld.getDayOfMonth() + "");
		}), "South");
		mp.add(btn("▼", a -> {
			ld = ld.plusMonths(-1);
			year.setText(ld.getYear() + "");
			month.setText(ld.getMonthValue() + "");
			day.setText(ld.getDayOfMonth() + "");
		}), "South");
		dp.add(btn("▼", a -> {
			ld = ld.plusDays(-1);
			year.setText(ld.getYear() + "");
			month.setText(ld.getMonthValue() + "");
			day.setText(ld.getDayOfMonth() + "");
		}), "South");

		for (var i : menu.getComponents()) {
			for (var j : ((JComponent) i).getComponents()) {
				j.setForeground(new Button().getForeground());
				j.setBackground(new Button().getBackground());
			}
		}

		jt.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					menu.show(jt, 0, 20);
				}
				super.mousePressed(e);
			}
		});

		menu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				ld = LocalDate.now();
				year.setText(ld.getYear() + "");
				month.setText(ld.getMonthValue() + "");
				day.setText(ld.getDayOfMonth() + "");

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				jt.setText(year.getText() + "-" + month.getText() + "-" + day.getText());

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public static void main(String[] args) {
		upwd = "1";
		uno = "1";
		new UserMain();
	}

}

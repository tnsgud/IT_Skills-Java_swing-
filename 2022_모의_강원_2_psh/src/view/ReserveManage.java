package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import db.DB;
import tool.Tool;

public class ReserveManage extends JPanel implements Tool {
	JPanel chart;
	DefaultTableModel m = model("sno,rno,순번,예매자,출발지,도착지,출발날짜,도착시간".split(","));
	JTable t = table(m);
	JComboBox<String> box;
	HashSet<Integer> rows = new HashSet<Integer>();
	ArrayList<ArrayList<String>> area = new ArrayList<ArrayList<String>>();
	boolean mod = true, isChange = false;

	public ReserveManage() {
		ui();
		data();
	}

	private void data() {
		m.setRowCount(0);
		var rs = DB.rs(
				"select s.no, r.no u.name, concat(v1.l11name, ' ', v1.l21name), concat(v1.l11name, ' ', v1.l21name), s.date,time_format(addtime(s.date,  s.elapsed_time), '%H:%i:%s') from v1, schedule s, reservaton r where r.scheduel_no=s.no and s.no=v1.sno order by r.no");
		try {
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));
		var n = new JPanel(new FlowLayout(0));
		var s = new JPanel(new FlowLayout(2));

		add(chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.drawString("<가장 예매가 많은 일정 TOP 6>",
						getWidth() / 2 - getFontMetrics(getFont()).stringWidth("<가장 예매가 많은 일정 TOP 6>") / 2, 20);

				if (mod) {
					g2.drawLine(50, 50, 50, 281);
					for (int i = 0; i < 5; i++) {
						g2.drawLine(50, 50 + (i * 58), 600, 50 + (i * 58));
					}

					var p = new Polygon();
					area = DB.toArrayList(
							"select *, count(scheduel_no) from reservation group by schedule_no order by count(schedule_no) desc, scheduel_no limit 6");

					int max = toInt(area.get(0).get(3));

					for (int i = 0; i < area.size(); i++) {
						if (i != 5) {
							g2.drawString((max - i * 2) + "", 30, 50 + (i * 58));
						}

						g2.drawString(area.get(i).get(2), 50 + (i * 108), 300);

						if (max >= 18) {
							p.addPoint(50 + (i * 110), 50 + ((max - toInt(area.get(i).get(3))) * 12));
						} else {
							p.addPoint(50 + (i * 110), 50 + ((max - toInt(area.get(i).get(3))) * 30));
						}
					}

					p.addPoint(600, 282);
					p.addPoint(50, 282);
					g2.setColor(new Color(0, 125, 255));
					g2.fillPolygon(p);
				} else {
					var num = new int[6];
					var po = new int[6][2];
					var pg = new Polygon[] { new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(),
							new Polygon() };

					for (int i = 0; i < 5; i++) {
						for (int j = 0; j < pg.length; j++) {
							pg[i].addPoint((int) (380 + (100 - i * 20) * Math.cos(j * Math.PI / 3 + (Math.PI / 6))),
									(int) (200 + (100 - i * 20 * Math.sin(j * Math.PI / 3 + (Math.PI / 6)))));
						}

						g2.drawPolygon(pg[i]);
					}

					for (int i = 0; i < pg.length; i++) {
						po[i][0] = (int) (375 + 110 * Math.cos(i * Math.PI / 3 - (Math.PI / 2)));
						po[i][1] = (int) (200 + 110 * Math.sin(i * Math.PI / 3 - (Math.PI / 2)));
					}

					var rs = DB.rs(
							"select *, count(schedule_no) as cnt from reservation group by schedule_no order by cnt desc, scheduel_no asc limit 6");
					int i = 0, max = 0;
					try {
						while (rs.next()) {
							if (i == 0) {
								max = rs.getInt(4);
							}

							if (i < 5) {
								g2.drawString(max - 2 * i + "", 370, 100 + (i * 20));
							}

							g2.drawString(rs.getString(3), po[i][0], po[i][1]);
							num[i] = rs.getInt(4);
							i++;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					g2.setColor(new Color(0, 125, 255));
					var p = new Polygon();

					for (int j = 0; j < num.length; j++) {
						p.addPoint(
								(int) (380 + (100 - (num[0] - num[i]) / 2.0 * 20)
										* Math.cos(i * Math.PI / 3 - (Math.PI / 2))),
								(int) (200 + (100 - (num[0] - num[i]) / 2.0 * 20)
										* Math.sin(i * Math.PI / 3 - (Math.PI / 2))));
					}
					g2.drawPolygon(p);
				}
			}
		}, "North");
		n.setOpaque(false);

		chart.add(n, "North");

		n.add(lbl("에매 관리", 2, 20));
		n.add(box = new JComboBox<String>(new DefaultComboBoxModel<String>("2차원 영역형,방사형".split(","))));
		add(new JScrollPane(t));
		add(s, "South");

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);
		t.getColumnModel().getColumn(1).setMinWidth(0);
		t.getColumnModel().getColumn(1).setMaxWidth(0);

		box.addItemListener(i -> {
			mod = i.getItem().equals("2차원 영역형");
			repaint();
		});

		for (var cap : "저장,취소".split(",")) {
			s.add(btn(cap, a -> {
				if (a.getActionCommand().contentEquals("저장")) {
					rows.forEach(row -> {
						var dp = toInt(DB.getOne("select l21no from v1 where l11name=? and l21name=?",
								t.getValueAt(row, 2).toString().split(" ")));
						var ar = toInt(DB.getOne("select l22no from v1 where l12name=? and l22name=?",
								t.getValueAt(row, 3).toString().split(" ")));

						DB.execute("update schedule set departure_location2_no=?, arrival_location2_no=? where no=?",
								dp, ar, toInt(t.getValueAt(row, 0)));

						iMsg("수정내용을 저장 완료하였습니다.");
						DB.createV();

						data();
					});
				} else {
					DB.execute("delete from reservation where no=?", toInt(t.getValueAt(t.getSelectedRow(), 1)));

					iMsg("삭제를 완료하였습니다.");
					DB.createV();

					data();
				}
			}));

		}
	}
}

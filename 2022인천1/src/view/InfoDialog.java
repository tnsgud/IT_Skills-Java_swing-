package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class InfoDialog extends BaseDialog {

	ArrayList<Object> building;
	JPanel reserve, info;
	JLabel star[] = new JLabel[5];
	CardLayout card;
	JTextArea area;

	int uno = toInt(BasePage.user.get("no"));

	public InfoDialog(ArrayList<Object> building) {
		super(400, 400);

		this.building = building;

		setContentPane(new JPanel(card = new CardLayout()));
		add(reserve = new JPanel(new BorderLayout(5, 5)), "reserve");
		add(info = new JPanel(new BorderLayout(5, 5)), "info");

		reserveUI();
		infoUI();

		card.show(getContentPane(), "info");
	}

	private void infoUI() {
		info.removeAll();

		var t = rs("select * from rate where user =? and building =?", uno, building.get(0));
		var rate = t.isEmpty() ? new ArrayList<>() : t.get(0);

		info.setBorder(new EmptyBorder(5, 5, 5, 5));

		var c = new JPanel(new BorderLayout());
		var s = new JPanel(new GridLayout(1, 0, 5, 5));
		var cn = new JPanel();
		var cc = new JPanel(new BorderLayout(5, 5));
		var cs = new JPanel(new GridLayout(0, 1));
		var ratelbl = lbl((rate.isEmpty() ? "0" : rate.get(1)) + "/5", 2, 20);

		cn.setLayout(new BoxLayout(cn, BoxLayout.Y_AXIS));

		info.add(lbl(building.get(1) + "", 0, 15), "North");
		info.add(new JScrollPane(c));
		info.add(s, "South");

		c.add(cn, "North");
		c.add(cc);
		c.add(cs, "South");

		cn.add(lbl("사진", 2, 20));
		if (building.get(8) != null) {
			cn.add(new JLabel(img(building.get(8), 360, 180)));
		}
		cn.add(lbl("정보", 2, 20));
		cn.add(lbl("<html>" + building.get(4), 2));
		cn.add(lbl("후기 작성", 2, 20));

		if (toInt(building.get(5)) < 2) {
			var tmp = new JPanel(new FlowLayout(0));
			for (int i = 0; i < 5; i++) {
				tmp.add(star[i] = lbl("★", 2, 20));
				star[i].setName(i + 1 + "");
				star[i].setForeground(
						rate.isEmpty() ? Color.gray : toInt(rate.get(1)) >= i + 1 ? Color.red : Color.gray);
				star[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						int i = toInt(((JLabel) e.getSource()).getName());
						if (e.getButton() == 1) {
							for (int j = 0; j < 5; j++) {
								star[j].setForeground(Color.LIGHT_GRAY);
							}

							for (int k = 0; k < i; k++) {
								star[k].setForeground(Color.RED.brighter());
							}

							ratelbl.setText(i + "/5");
						}
					}
				});
			}
			tmp.add(ratelbl);
			cc.add(tmp, "North");
		}

		cc.add(area = new JTextArea(6, 10));
		area.setText(rate.isEmpty() ? "" : rate.get(4) + "");

		var tmp = new JPanel(new FlowLayout(2));
		for (var cap : (rate.isEmpty() ? "후기 작성하기," : "후기 수정하기,삭제").split(",")) {
			tmp.add(btn(cap, a -> {
				if (cap.contains("작성하기")) {
					execute("insert rate values(0, ?, ?, ?, ?)", ratelbl.getText().split("/")[0], building.get(0), uno,
							area.getText());
					iMsg("작성이 완료되었습니다.");
				} else if (cap.equals("삭제")) {
					execute("delete from rate where no=?", rate.get(0));
					iMsg("삭제되었습니다.");
				} else {
					execute("update rate set rate=?, review=? where no =?", ratelbl.getText().split("/")[0],
							area.getText(), rate.get(0));
					iMsg("수정되었습니다.");
				}

				infoUI();
				return;
			}));
		}

		cc.add(tmp, "South");

		cs.setLayout(new BoxLayout(cs, BoxLayout.Y_AXIS));

		var l1 = lbl("<html>전체<font color='#007BFF'> "
				+ getOne("select count(*) from rate where building = ?", building.get(0)), 2, 15);
		var l2 = lbl("평점: " + building.get(9), 2, 20);
		l1.setAlignmentX(Component.LEFT_ALIGNMENT);
		l2.setAlignmentX(Component.LEFT_ALIGNMENT);
		cs.add(l1);
		cs.add(l2);

		for (var rs : rs("select r.*, u.name from rate r, user u where r.user = u.no and r.building=?",
				building.get(0))) {
			var tmep = new JPanel(new GridLayout(0, 1));
			var tmepn = new JPanel(new FlowLayout(0));

			for (int j = 0; j < 5; j++) {
				var l = lbl("★", 0);
				l.setForeground(j + 1 <= toInt(rs.get(1)) ? Color.red : Color.LIGHT_GRAY);
				tmepn.add(l);
			}

			tmep.add(tmepn, "North");
			tmep.add(lbl(rs.get(4) + "", 2));
			tmep.add(lbl(rs.get(5) + "", 2));

			tmep.setAlignmentX(Component.LEFT_ALIGNMENT);
			tmep.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

			cs.add(tmep);
		}

		for (var cap : "예약하기,닫기".split(",")) {
			s.add(btn(cap, a -> {
				if (cap.equals("예약하기")) {
					card.show(getContentPane(), "reserve");
				} else {
					dispose();
				}
			}));
		}

		c.setBorder(new EmptyBorder(5, 5, 5, 5));

		repaint();
		revalidate();
	}

	private void reserveUI() {
		var c = new JPanel(new GridLayout(0, 1, 5, 5));
		var s = new JPanel(new GridLayout(1, 0, 5, 5));
		var cap = "이름,전화번호".split(",");
		var txt = new JTextField[2];
		var datetxt = new JTextField(15);
		var tmp = new JPanel(new BorderLayout());
		var tmp2 = new JPanel(new GridLayout(1, 0, 5, 5));
		JComboBox<String> vaccine, hour, min;

		reserve.add(c);
		reserve.add(s, "South");

		for (int i = 0; i < cap.length; i++) {
			c.add(lbl(cap[i], 2, 20));
			c.add(txt[i] = new JTextField(15));
			txt[i].setText(
					getOne("select " + (i == 0 ? "name" : "phone") + " from user where no=?", BasePage.user.get("no")));
			txt[i].setEnabled(false);
		}

		c.add(lbl("백신", 2, 20));
		c.add(vaccine = new JComboBox<>("아스트라제네카,얀센,화이자,모더나".split(",")));

		c.add(lbl("예약 날짜 및 시간", 2, 20));
		c.add(tmp);

		tmp.add(datetxt);
		tmp.add(tmp2, "East");

		tmp2.add(hour = new JComboBox<>());
		tmp2.add(min = new JComboBox<>());

		for (int i = toInt(getOne("select hour(open) from building where no=?", building.get(0))) + 1; i < toInt(
				getOne("select hour(close) from building where no=?", building.get(0))); i++) {
			hour.addItem(String.format("%02d", i));
		}

		for (int i = 0; i < 60; i++) {
			min.addItem(String.format("%02d", i + 1));
		}

		datetxt.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new DatePicker(datetxt, LocalDate.now()).show(datetxt, datetxt.getX(), datetxt.getY() + 40);
			}
		});

		datetxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				e.consume();
			}
		});

		for (var ca : "뒤로가기,예약하기".split(",")) {
			s.add(btn(ca, a -> {
				if (ca.contains("뒤로")) {
					card.show(getContentPane(), "info");
				} else {
					if (datetxt.getText().isEmpty()) {
						eMsg("날짜를 선택해주세요.");
						return;
					}

					var ldt = LocalDateTime.of(
							LocalDate.parse(datetxt.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
							LocalTime.of(toInt(hour.getSelectedItem()), toInt(min.getSelectedItem())));

					execute("insert purchase values(0,?,?,?,?,?)", BasePage.user.get("no"), ldt, building.get(0), 0,
							vaccine.getSelectedIndex() + 1);
					var lbl = lbl("예약이 완료되었습니다.", 2);
					var evtlbl = hyplbl("길 찾으러 가기", 2, 0, 13, Color.blue, () -> {
						JOptionPane.getRootFrame().dispose();
						var sp = (SearchPage) BasePage.mf.getContentPane().getComponent(0);
						sp.searchPanel.togglePath.setSelected(true);
						sp.searchPanel.arrv.setText(building.get(1) + "");
						sp.searchPanel.arrv.setName(building.get(0) + "");
						sp.pathFind();
					});
					
					var temp = new JPanel(new GridLayout(0, 1));
					temp.add(lbl);
					temp.add(evtlbl);
					
					JOptionPane.showMessageDialog(this, temp, "확인", JOptionPane.INFORMATION_MESSAGE);
					dispose();
				}
			}));
		}

		reserve.setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public static void main(String[] args) {
		BasePage.mf.swap(new SearchPage());
		BasePage.mf.setVisible(true);
	}
}
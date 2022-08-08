package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class InfoDialog extends BaseDialog {
	ArrayList<Object> arr;
	SearchPage page;
	CardLayout card;
	JPanel info, reserve;
	JButton btn[] = new JButton[2];
	JTextField txt[] = new JTextField[3];
	JComboBox<String> com = new JComboBox<>(
			getRows("select name from vaccine").stream().map(a -> a.get(0).toString()).toArray(String[]::new)),
			comTime = new JComboBox<>();

	public InfoDialog(ArrayList<Object> arr) {
		super(400, 400);
		this.arr = arr;
		page = (SearchPage) BasePage.mf.getContentPane().getComponent(0);
		setLayout(new BorderLayout(5, 5));

		add(lbl(arr.get(1).toString(), 0, 20), "North");
		add(c = new JPanel(card = new CardLayout()));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		c.add(new JScrollPane(info = new JPanel(new BorderLayout())), "info");
		c.add(reserve = new JPanel(new GridLayout(0, 1, 5, 5)), "reserve");

		info();
		reserve();

		var cap = "닫기,예약하기".split(",");
		for (int i = 0; i < cap.length; i++) {
			s.add(btn[i] = btn(cap[i], a -> {
				if (a.getActionCommand().equals("닫기")) {
					dispose();
				} else if (a.getActionCommand().equals("예약하기")) {
					if (reserve.isVisible()) {
						var shot = toInt(getOne("select count(*) from purchase where user = ?", BasePage.user.get(0)));
						if (shot == 4) {
							eMsg("이미 모든 접종을 완료하셨습니다.");
							return;
						}

						iMsg("예약이 완료되었습니다.");
						dispose();
						execute("insert purchase values(0, ?, ?, ?, ?, ?)", BasePage.user.get(0),
								txt[2].getText() + " " + comTime.getSelectedItem(), arr.get(0),
								com.getSelectedIndex() + 1, shot + 1);
					} else {
						card.show(c, "reserve");
						btn[0].setText("뒤로가기");
					}
				} else {
					card.show(c, "info");
					btn[0].setText("닫기");
				}
			}));
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		info.setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	private void reserve() {
		var cap = "이름,전화번호,백신,예약 날짜 및 시간".split(",");
		for (int i = 0; i < cap.length; i++) {
			reserve.add(lbl(cap[i], 0, 20));

			if (i == 3) {
				var tmp = new JPanel(new BorderLayout());
				tmp.add(txt[2] = new JTextField());
				tmp.add(comTime, "East");

				reserve.add(tmp);
			} else if (i == 2) {
				reserve.add(com);
			} else {
				reserve.add(txt[i] = new JTextField());
			}
		}

		var sTime = LocalTime.parse(arr.get(2).toString(), DateTimeFormatter.ofPattern("HH:mm:ss"));
		var eTime = LocalTime.parse(arr.get(3).toString(), DateTimeFormatter.ofPattern("HH:mm:ss"));

		for (var t = sTime; t.isBefore(eTime); t = t.plusMinutes(30)) {
			comTime.addItem(t.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
			if (!t.isBefore(LocalTime.of(23, 30)))
				break;
		}
	}

	private void info() {
		info.removeAll();

		var area = new JTextArea(2, 5);
		var lblRate = lbl("0/5", 2, 20);
		var stars = new JLabel[5];
		var tmp1 = new JPanel(new FlowLayout(0, 0, 0));
		var tmp2 = new JPanel(new FlowLayout(2));
		var rate = getRows("select * from rate where user = ? and building = ?", BasePage.user.get(0), arr.get(0));
		var rs = getRows("select rate, review, u.name from rate r, user u where r.user = u.no and r.building = ?",
				arr.get(0));

		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

		info.add(lbl("사진", 0, 20));
		info.add(new JLabel(getIcon(arr.get(8), 350, 180)));
		info.add(lbl("정보", 0, 20));
		info.add(lbl("<html>" + arr.get(4), 2));
		info.add(lbl("후기 작성", 0, 20));
		info.add(tmp1);
		info.add(sz(area, 350, 60));
		info.add(tmp2);
		info.add(lbl("<html>전체<font color=rgb(0,123,255)>" + rs.size(), 2, 15));
		info.add(lbl("평점 " + getOne("select round(avg(rate), 1) from rate where building = ?", arr.get(0)), 2, 20));

		for (int i = 0; i < stars.length; i++) {
			final int j = i;
			tmp1.add(stars[i] = lbl("★", 2, 20));
			stars[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() != 1)
						return;

					Stream.of(stars).forEach(s -> s.setForeground(Color.LIGHT_GRAY));
					for (int j2 = 0; j2 <= j; j2++) {
						stars[j2].setForeground(Color.red);
					}
					lblRate.setText(j + 1 + "/5");
				}
			});
			stars[i].setForeground(Color.LIGHT_GRAY);
		}
		tmp1.add(lblRate);

		tmp2.add(btn(rate.isEmpty() ? "후기 작성하기" : "후기 수정하기", a -> {
			if (toInt(lblRate.getText().split("/")[0]) == 0) {
				eMsg("별점을 1점 이상 선택하세요.");
				return;
			}

			iMsg((rate.isEmpty() ? "작성" : "수정") + "이 완료되었습니다.");

			if (rate.isEmpty()) {
				execute("insert rate values(0, ?, ?, ?, ?)", arr.get(0), lblRate.getText().split("/")[0],
						BasePage.user.get(0), area.getText());
			} else {
				execute("update rate set rate = ?, review = ? where no = ?", lblRate.getText().split("/")[0],
						area.getText(), getOne("select no from rate where user = ? and building = ?",
								BasePage.user.get(0), arr.get(0)));
			}

			if (!page.west.txtSearch.getText().isEmpty()) {
				page.west.search();
			}

			info();
		}));

		if (!rate.isEmpty()) {
			for (int i = 0; i < toInt(rate.get(0).get(2)); i++) {
				stars[i].setForeground(Color.red);
			}
			lblRate.setText(rate.get(0).get(2) + "/5");
			area.setText(rate.get(0).get(4).toString());
			tmp2.add(btn("삭제", a -> {
				execute("delete from rate where no = ?", rate.get(0).get(0));
				info();
			}));
		}

		for (var r : rs) {
			var temp1 = new JPanel(new GridLayout(0, 1));
			var temp2 = new JPanel(new FlowLayout(0, 0, 0));

			temp1.add(temp2);
			temp1.add(lbl("<html>" + r.get(1), 2, 15));
			temp1.add(lbl(r.get(2).toString(), 2, 15));

			for (int i = 0; i < 5; i++) {
				var l = lbl("★", 0);
				l.setForeground(i < toInt(r.get(0)) ? Color.red : Color.lightGray);
				temp2.add(l);
			}

			temp1.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

			info.add(temp1);
		}

		area.setBorder(new LineBorder(Color.black));

		for (var com : info.getComponents()) {
			((JComponent) com).setAlignmentX(Component.LEFT_ALIGNMENT);
		}

		info.repaint();
		info.revalidate();
	}
}

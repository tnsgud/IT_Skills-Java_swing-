package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class InfoDialog extends BaseDialog {
	ArrayList<Object> r;
	JPanel info = new JPanel(), reserve = new JPanel(new GridLayout(0, 1, 5, 5));
	JScrollPane scr;
	JLabel stars[] = new JLabel[5];
	CardLayout card;
	JTextArea area;

	JTextField txt[] = new JTextField[3];
	JComboBox<String> vac = new JComboBox<>(
			getRows("select name from vaccine").stream().flatMap(a -> a.stream()).toArray(String[]::new)),
			time = new JComboBox<String>();

	public InfoDialog(ArrayList<Object> r) {
		super(500, 500);
		this.r = r;

		setLayout(new BorderLayout(5, 5));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		BasePage.user = getRows("select * from user").get(0);

		add(lbl(r.get(1).toString(), 0, 25), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		c.add(scr = new JScrollPane(info));

		info();
		reserve();

		for (var cap : "닫기,예약하기".split(",")) {
			s.add(btn(cap, a -> {
				if (a.getActionCommand().equals("닫기")) {
					dispose();
				} else if (a.getActionCommand().equals("뒤로가기")) {
					c.remove(reserve);
					c.add(info);

					repaint();
					revalidate();
				} else {
					if (scr.isVisible()) {
						c.removeAll();
						c.add(reserve);

						repaint();
						revalidate();
					} else {
						if (txt[2].getText().isEmpty()) {
							eMsg("날짜를 선택해주세요.");
							return;
						}

						var shot = toInt(getOne("select count(*) from purchase where user = ?", BasePage.user.get(0)));

						if (shot == 4) {
							eMsg("이미 모든 접종을 완료하셨습니다.");
							return;
						}

						iMsg("예약이 완료되었습니다.");
						var date = LocalDateTime.of(LocalDate.parse(txt[2].getText()),
								LocalTime.parse(time.getSelectedItem().toString()));
						execute("insert purchase values(0, ?, ?, ?, ?, ?", BasePage.user.get(0), date, r.get(0),
								vac.getSelectedIndex() + 1, shot + 1);
						 
						dispose();
					}
				}
			}));
		}
	}

	private void reserve() {
		var cap = "이름,전화번호,백신,예약 날짜 및 시간".split(",");

		for (int i = 0, idx = 0; i < cap.length; i++) {
			reserve.add(lbl(cap[i], 2, 20));

			if (i == 3) {
				var tmp = new JPanel(new BorderLayout(5, 5));

				tmp.add(txt[idx] = new JTextField());
				tmp.add(time, "East");

				var start = LocalTime.parse(r.get(2).toString());
				var end = LocalTime.parse(r.get(3).toString()).minusHours(1);

				while (start.isBefore(end)) {
					time.addItem(start.toString());
					start = start.plusMinutes(30);
				}
				time.addItem(end.toString());

				reserve.add(tmp);
			} else if (i == 2) {
				reserve.add(vac);
			} else {
				reserve.add(txt[idx++] = new JTextField());
			}
		}

		txt[0].setText(BasePage.user.get(1).toString());
		txt[1].setText(BasePage.user.get(4).toString());
	}

	private void info() {
		info.removeAll();

		var n = new JPanel(new FlowLayout(0, 0, 0));
		var c = new JPanel(new BorderLayout());
		var s = new JPanel(new GridLayout(0, 1, 5, 5));
		var cs = new JPanel(new FlowLayout(2));

		var reviews = getRows("select rate, review, name from rate r, user u where r.user = u.no and r.building = ?",
				r.get(0));
		var optional = reviews.stream().filter(review -> review.get(2).equals(BasePage.user.get(1))).findFirst();
		var myReview = optional.isPresent() ? optional.get() : new ArrayList<>();
		var ratelbl = lbl("0/5", 2, 20);

		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.setBorder(new EmptyBorder(5, 5, 5, 5));

		info.add(lbl("정보", 2, 20));
		info.add(new JLabel(getIcon(r.get(8), 450, 350)));
		info.add(lbl("정보", 2, 20));
		info.add(lbl(r.get(4) + "", 2));
		info.add(lbl("후기 작성", 2, 20));
		info.add(n);
		info.add(c);
		info.add(lbl("<html>전체<font color=rgb(0,123,255)>" + reviews.size(), 2, 15));
		info.add(lbl("평점 " + String.format("%.1f",
				(double) toInt(getOne("select ifnull(round(avg(rate), 0), 0) from rate where building = ?", r.get(0)))),
				2, 20));
		info.add(s);

		for (int i = 0; i < stars.length; i++) {
			n.add(stars[i] = lbl("★", 2, 15));
			stars[i].setName(i + 1 + "");
			stars[i].setForeground(Color.lightGray);
			stars[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (var start : stars) {
						start.setForeground(Color.LIGHT_GRAY);
					}

					var lbl = ((JLabel) e.getSource());
					for (int j = 0; j < toInt(lbl.getName()); j++) {
						stars[j].setForeground(Color.red);
					}

					ratelbl.setText(lbl.getName() + "/5");
				}
			});
		}

		n.add(ratelbl);

		c.add(sz(area = new JTextArea(), 1, 100));
		c.add(cs, "South");
		cs.add(btn("후기 작성하기", a -> {
			int rate = 0;

			for (var star : stars) {
				rate = star.getForeground() == Color.red ? rate + 1 : rate;
			}

			if (rate == 0) {
				eMsg("별점을 1점 이상 선택하세요.");
				return;
			}

			execute("insert rate values(0, ?, ?, ?, ?)", r.get(0), rate, BasePage.user.get(0), area.getText());

			info();
		}));

		for (var review : reviews) {
			var tmp = new JPanel(new BorderLayout(5, 5));
			var tmp_n = new JPanel(new FlowLayout(0));
			var stars = new JLabel[5];

			tmp.add(tmp_n, "North");
			tmp.add(lbl("<html>" + review.get(1), 2, 13));
			tmp.add(lbl(review.get(2) + "", 2, 13), "South");

			for (int i = 0; i < stars.length; i++) {
				tmp_n.add(stars[i] = lbl("★", 2));
				stars[i].setForeground(i <= toInt(review.get(0)) - 1 ? Color.red : Color.LIGHT_GRAY);
			}

			tmp.setBorder(new MatteBorder(2, 0, 0, 0, Color.LIGHT_GRAY));

			s.add(tmp);
		}

		n.setAlignmentX(Component.LEFT_ALIGNMENT);
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		s.setAlignmentX(Component.LEFT_ALIGNMENT);

		area.setBorder(new LineBorder(Color.black));

		if (!myReview.isEmpty()) {
			for (int i = 0; i < toInt(myReview.get(0)); i++) {
				stars[i].setForeground(Color.red);
			}

			ratelbl.setText(myReview.get(0) + "/5");

			area.setText(myReview.get(1).toString());

			var btn = (JButton) cs.getComponent(0);
			btn.setText("후기 수정하기");
			cs.add(btn("삭제", a -> {
				execute("delete from rate where building = ? and user = ?", r.get(0), BasePage.user.get(0));

				info();
			}));
		}

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		BasePage.mf.swapPage(new SearchPage());
		BasePage.mf.setVisible(true);
	}
}

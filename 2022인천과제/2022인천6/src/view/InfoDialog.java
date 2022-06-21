package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class InfoDialog extends BaseDialog {
	JPanel s;
	JPanel main, info, reserve;
	JComboBox<String> vaccine, time;
	CardLayout card;

	ArrayList<Object> r;
	int uno;
	SearchPage searchPage;
	
	public InfoDialog(ArrayList<Object> building) {
		super(400, 400);
		
		this.r = building;
	}

	public InfoDialog(ArrayList<Object> building, int uno, SearchPage searchPage) {
		super(400, 400);
		
		this.r = building;
		this.uno = uno;
//		this.searchPage = searchPage;
		
		
		setLayout(new BorderLayout());
		setContentPane(main = new JPanel(card = new CardLayout()));
		main.add(info = new JPanel(new BorderLayout(5, 5)), "info");
		main.add(reserve = new JPanel(new BorderLayout(5, 5)), "reserve");
		infoUI();
		reUI();
		card.show(main, "info");
	}

	void infoUI() {
		info.removeAll();

		var c = new JPanel(new BorderLayout());
		var s = new JPanel(new GridLayout(1, 0, 5, 5));

		var cn = new JPanel();
		var cc = new JPanel(new FlowLayout(0));
		var cs = new JPanel(new BorderLayout());

		var csn = new JPanel(new BorderLayout());
		var csc = new JPanel(new GridLayout(0, 1));

		info.add(lbl(r.get(1).toString(), 0, 25), "North");
		info.add(new JScrollPane(c));
		info.add(s, "South");

		cn.setLayout(new BoxLayout(cn, BoxLayout.Y_AXIS));
		c.add(cn, "North");
		c.add(cc);
		c.add(cs, "South");

		s.add(btn("닫기", e -> dispose()));
		s.add(btn("예약하기", e -> card.show(main, "reserve")));

		cn.add(lbl("사진", 2, 15));
		cn.add(new JLabel(getIcon(r.get(8), 370, 150)));
		cn.add(lbl("정보", 2, 15));
		cn.add(lbl(r.get(4).toString(), 2, 13));
		cn.add(lbl("후기 작성", 2, 15));
		cs.add(csn, "North");
		cs.add(csc);

		var tmp1 = new JPanel(new FlowLayout(0));
		var tmp2 = new JPanel(new FlowLayout(2));
		var area = new JTextArea(6, 10);
		var stars = new JLabel[5];
		var rate = lbl("0/5", 2, 15);

		for (int i = 0; i < stars.length; i++) {
			tmp1.add(stars[i] = lbl("★", 0, 15));
			stars[i].setForeground(Color.LIGHT_GRAY);
			int idx = i + 1;
			stars[i].addMouseListener(new MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					for (int j = 0; j < stars.length; j++) {
						stars[j].setForeground(Color.LIGHT_GRAY);
					}

					for (int k = 0; k < idx; k++) {
						stars[k].setForeground(Color.red);
					}
					rate.setText(idx + "/" + 5);
				};
			});
		}
		tmp1.add(rate);
		cc.add(tmp1);
		csn.add(area);
		csn.add(tmp2, "South");

		var rateNo = getOne("select no from rate where building=" + r.get(0) + " and user=" + uno);
		if (!rateNo.isEmpty()) {
			for (int i = 0; i < toInt(getOne("select rate from rate where no=" + rateNo)); i++) {
				stars[i].setForeground(Color.red);
			}

			rate.setText(getOne("select rate from rate where no=" + rateNo) + "/" + 5);
			area.setText(getOne("select review from rate where no=" + rateNo));
		}
		tmp2.add(btn(rateNo.isEmpty() ? "후기 작성하기" : "후기 수정하기", e -> {
			if (e.getActionCommand().equals("후기 작성하기")) {
				if (rate.getText().split("/")[0].equals("0")) {
					eMsg("별점을 1점 이상 선택하세요.");
					return;
				}

				execute("insert rate values(0,?,?,?,?)", r.get(0), rate.getText().split("/")[0], uno, area.getText());
			} else {
				iMsg("수정이 완료되었습니다.");
				execute("update rate set rate=?, review=? where no=?", rate.getText().split("/")[0], area.getText(),
						rateNo);
			}

			infoUI();
		}));
		tmp2.add(btn("삭제", e -> {
			execute("delete from rate where no=?", rateNo);
			infoUI();
		})).setVisible(!rateNo.isEmpty());

		csc.add(lbl("<html>전체" + getOne("select count(*) from rate where building=" + r.get(0)), 2, 13));
		csc.add(lbl("평점 " + r.get(9), 2, 15));
		for (var r : getRows("select r.rate, r.review, u.name from rate r, user u where r.user = u.no and r.building=?",
				this.r.get(0))) {
			var tmp = new JPanel(new BorderLayout());
			var tmpn = new JPanel(new FlowLayout(0));
			var tmpc = new JPanel(new FlowLayout(0));
			var tmps = new JPanel(new FlowLayout(0));
			var rates = new JLabel[5];

			tmp.add(tmpn, "North");
			tmp.add(tmpc);
			tmp.add(tmps, "South");

			for (int i = 0; i < rates.length; i++) {
				tmpn.add(rates[i] = new JLabel("★"));
				rates[i].setForeground(Color.LIGHT_GRAY);
			}

			for (int i = 0; i < toInt(r.get(0)); i++) {
				rates[i].setForeground(Color.red);
			}

			tmpc.add(new JLabel(r.get(1).toString()));
			tmps.add(new JLabel(r.get(2).toString()));
			tmpn.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

			csc.add(tmp);
		}

		csc.setBorder(new EmptyBorder(0, 10, 0, 10));
		area.setBorder(new LineBorder(Color.black));
		area.setLineWrap(true);

		repaint();
		revalidate();
	}

	void reUI() {
		reserve.removeAll();

		var c = new JPanel(new GridLayout(0, 1));
		var s = new JPanel(new GridLayout(1, 0, 5, 5));

		reserve.add(c);
		reserve.add(s, "South");

		var str = "이름,전화번호,백신,예약 날짜 및 시간".split(",");
		JTextField txt[] = { new JTextField(), new JTextField(), new JTextField() };

		for (int i = 0; i < str.length; i++) {
			c.add(lbl(str[i], 2, 15));
			if (i == 2) {
				c.add(vaccine = new JComboBox<String>());
			} else if (i == 3) {
				var tmp1 = new JPanel(new BorderLayout(5, 5));
				var tmp2 = new JPanel(new GridLayout(1, 0, 5, 5));
				tmp1.add(txt[2]);
				tmp1.add(tmp2, "East");
				tmp2.add(time = new JComboBox<String>());
				c.add(tmp1);
			} else {
				c.add(txt[i]);
			}
		}
		s.add(btn("뒤로가기", e -> card.show(main, "info")));
		s.add(btn("얘약하기", e -> {
			if (txt[2].getText().isEmpty()) {
				eMsg("날짜를 선택해주세요.");
				return;
			}

			if (toInt(getOne("select count(*) from purchase where user=" + uno)) == 4) {
				eMsg("이미 모든 접증을 완료하셨습니다.");
				return;
			}

			execute("insert purchase values(0,?,?,?,?,?)", uno,
					LocalDateTime.parse(txt[2].getText() + " " + time.getSelectedItem(),
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
					r.get(0), getOne("select no from vaccine where name='" + vaccine.getSelectedItem() + "'"),
					getOne("select count(*) from purchase where user=" + uno) + 1);
			iMsg("예약이 완료되었습니다.");
			dispose();
		}));

		var start = LocalTime.parse(r.get(2).toString(), DateTimeFormatter.ofPattern("HH:mm:ss"));
		var end = LocalTime.parse(r.get(3).toString(), DateTimeFormatter.ofPattern("HH:mm:ss"));

		for (var i = end; i.isBefore(start); i = i.plusMinutes(30)) {
			time.addItem(i.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		}

		for (var r : getRows("select name from vaccine")) {
			vaccine.addItem(r.get(0).toString());
		}

		txt[0].setText(getOne("select name from user where no=" + uno));
		txt[1].setText(getOne("select phone from user where no=" + uno));

		txt[0].setEditable(false);
		txt[1].setEditable(false);

		repaint();
		revalidate();
	}

}

package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class InfoDialog2 extends BaseDialog {

	JButton btn1, btn2, btn3, btn4;
	JScrollPane pane;
	JTextArea area;
	JLabel star[] = new JLabel[5];
	JLabel ratelbl;

	JPanel cs;
	ArrayList<Object> building;
	JPanel info, master, reserv;
	CardLayout card;

	int uno = toInt(BasePage.user.get("no"));

	public InfoDialog2(ArrayList<Object> building) {
		super(400, 400);
		this.building = building;
		setModal(true);
		setSize(400, 400);
		setLocationRelativeTo(BasePage.mf);
		setDefaultCloseOperation(2);
		setUndecorated(true);
		setContentPane(master = new JPanel(card = new CardLayout()));
		master.add(reserv = new JPanel(new BorderLayout(5, 5)), "reserv");
		master.add(info = new JPanel(new BorderLayout(5, 5)), "info");
		info_ui();
		reserve_ui();
		card.show(master, "info");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	void reserve_ui() {
		var c = new JPanel(new GridLayout(0, 1, 5, 5));
		var s = new JPanel(new GridLayout(1, 0, 5, 5));
		reserv.add(c);
		reserv.add(s, "South");

		var txt = new JTextField[] { new JTextField(15), new JTextField(15), new JTextField(15) };

		c.add(lbl("이름", JLabel.LEFT, 20));
		c.add(txt[0]);
		c.add(lbl("전화번호", JLabel.LEFT, 20));
		c.add(txt[1]);

		JComboBox<String> vaccine, hour, minute;

		c.add(lbl("백신", JLabel.LEFT, 20));
		c.add(vaccine = new JComboBox<String>("아스트라제네카,얀센,화이자,모더나".split(",")));

		var tmp = new JPanel(new BorderLayout(5, 5));
		var tmp2 = new JPanel(new GridLayout(1, 0, 5, 5));
		c.add(lbl("예약 날짜 및 시간", JLabel.LEFT, 20));
		c.add(tmp);
		tmp.add(tmp2, "East");
		tmp2.add(hour = new JComboBox<String>());
		tmp2.add(minute = new JComboBox<String>());

		tmp.add(txt[2]);

		txt[2].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new DatePicker(txt[2], LocalDate.now()).show(txt[2], txt[2].getX(), txt[2].getY() + 40);
			}
		});

		txt[0].setEditable(false);
		txt[1].setEditable(false);

		txt[0].setText(getOne("select name from user where no = " + uno));
		txt[1].setText(getOne("select phone from user where no = " + uno));

		for (int i = toInt(getOne("select hour(open) from building where no = '" + building.get(0) + "'"))
				+ 1; i < toInt(getOne("select hour(close) from building where no = '" + building.get(0) + "'")); i++) {
			hour.addItem(String.format("%02d", i));
		}

		txt[2].addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				e.consume();
			}
		});

		for (int i = 0; i < 60; i += 5)
			minute.addItem(String.format("%02d", i));

		s.add(btn("뒤로가기", a -> card.show(master, "info")));
		s.add(btn("예약하기", a -> {
			if (txt[2].getText().isEmpty()) {
				eMsg("날짜를 선택해주세요.");
				return;
			}

			var ldt = LocalDateTime.of(LocalDate.parse(txt[2].getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
					LocalTime.of(toInt(hour.getSelectedItem() + ""), toInt(minute.getSelectedItem() + "")));

			execute("insert purchase values(0,?,?,?,?,?)", uno, ldt, building.get(0), 0,
					vaccine.getSelectedIndex() + 1);
			var lbl = lbl("예약이 완료되었습니다.", JLabel.LEFT);
			var evtlbl = hyplbl("길 찾으러 가기", JLabel.LEFT, 13, Font.TYPE1_FONT, Color.BLACK, () -> {
				JOptionPane.getRootFrame().dispose();
				var sp = (SearchPage) BasePage.mf.getContentPane().getComponent(0);
				sp.searchPanel.togglePath.setSelected(true);
				sp.searchPanel.arrv.setText(building.get(1) + "");
				sp.searchPanel.arrv.setName(building.get(0) + "");
				sp.pathFind();
			});

			JPanel temp = new JPanel(new GridLayout(0, 1));
			temp.add(lbl);
			temp.add(evtlbl);

			JOptionPane.showMessageDialog(this, temp, "확인", JOptionPane.INFORMATION_MESSAGE);
			dispose();
		}));

	}

	void info_ui() {
		info.removeAll();

		var rate = rs("select * from rate where user = ? and building = ?", uno, building.get(0));

		var c = new JPanel(new BorderLayout());
		var s = new JPanel(new GridLayout(1, 0, 5, 5));
		var cn = new JPanel();

		cn.setLayout(new BoxLayout(cn, BoxLayout.Y_AXIS));
		var cc = new JPanel(new BorderLayout());
		var ccn = new JPanel(new BorderLayout());
		var ccs = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		var ccnc = new JPanel(new FlowLayout(FlowLayout.LEFT));

		info.add(lbl(building.get(1) + "", JLabel.CENTER, 20), "North");
		info.add(pane = new JScrollPane(c));
		info.add(s, "South");

		c.add(cn, "North");
		c.add(cc);

		cn.add(lbl("사진", JLabel.LEFT, 20));
		if (building.get(8) != null)
			cn.add(new JLabel(img(building.get(8), 360, 180)));

		cn.add(lbl("정보", JLabel.LEFT, 20));
		cn.add(lbl("<html>" + building.get(4) + "", JLabel.LEFT));
		if (toInt(building.get(5)) == 1 || toInt(building.get(5)) == 0) {
			cc.add(ccn, "North");
			ccn.add(lbl("후기 작성", JLabel.LEFT, 20), "North");
			ccn.add(ccnc);

			for (int i = 0; i < 5; i++) {
				ccnc.add(star[i] = lbl("★", JLabel.CENTER, 20));
				star[i].setForeground(Color.LIGHT_GRAY);
				final int j = i + 1;
				star[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == 1) {

							for (int i = 0; i < 5; i++)
								star[i].setForeground(Color.LIGHT_GRAY);

							for (int k = 0; k < j; k++) {
								star[k].setForeground(Color.RED.brighter());
							}
							ratelbl.setText(j + "/" + 5);
						}
					}
				});

			}

			ccnc.add(ratelbl = lbl("0/5", JLabel.CENTER, 20));

			ccn.add(area = new JTextArea(6, 10), "South");
			cc.add(ccs, "South");
			ccs.add(btn3 = btn(rate.isEmpty() ? "후기 작성하기" : "후기 수정하기", a -> {
				if (a.getActionCommand().equals("후기 작성하기")) {
					execute("insert rate values(0, ?, ?, ?,?)", building.get(0), ratelbl.getText().split("/")[0], uno,
							area.getText());
					iMsg("작성이 완료되었습니다.");
				} else {
					execute("update rate set rate = ?, review = ? where no = ?", ratelbl.getText().split("/")[0],
							area.getText(), rate.get(0).get(0));
					iMsg("수정이 완료되었습니다.");
				}
				info_ui();
				var sp = (SearchPage) BasePage.mf.getContentPane().getComponent(0);
				sp.searchPanel.search();
			}));

			ccs.add(btn4 = btn("삭제", a -> {
				execute("delete from rate where no = ?", rate.get(0).get(0));
				info_ui();
				return;
			}));

			btn4.setVisible(!rate.isEmpty());

			c.add(cs = new JPanel(), "South");

			cs.setLayout(new BoxLayout(cs, BoxLayout.Y_AXIS));

			var lbl1 = lbl(
					"<html>전체<font color = '#007BFF'> "
							+ rs("select count(*) from rate where building = ?", building.get(0)).get(0).get(0),
					JLabel.LEFT, 15);
			var lbl2 = lbl("평점:" + building.get(9), JLabel.LEFT, 20);
			lbl1.setAlignmentX(Component.LEFT_ALIGNMENT);// 0.0
			lbl2.setAlignmentX(Component.LEFT_ALIGNMENT);// 0.0
			cs.add(lbl1);
			cs.add(lbl2);
			s.add(btn1 = btn("예약하기", a -> {
				card.show(master, "reserv");
			}));

			for (var r : rs("select r.*,  u.name from rate r, user u where r.building = ? and r.user = u.no",
					building.get(0))) {

				var temp = new JPanel(new BorderLayout());
				var tempn = new JPanel(new FlowLayout(FlowLayout.LEFT));
				var tempc = new JPanel(new FlowLayout(FlowLayout.LEFT));
				var temps = new JPanel(new FlowLayout(FlowLayout.LEFT));
				var star = new JLabel[5];

				temp.add(tempn, "North");
				temp.add(tempc);
				temp.add(temps, "South");

				for (int i = 0; i < 5; i++) {
					tempn.add(star[i] = new JLabel("★", JLabel.CENTER));
					star[i].setBackground(Color.LIGHT_GRAY);
				}

				for (int i = 0; i < toInt(r.get(2)); i++) {
					star[i].setForeground(Color.RED.brighter());
				}

				temp.setBorder(new MatteBorder(1, 0, 0, 0, Color.lightGray));
				tempc.add(lbl(r.get(4) + "", JLabel.LEFT));
				temps.add(lbl(r.get(5) + "", JLabel.CENTER));
				temp.setAlignmentX(Component.LEFT_ALIGNMENT);
				cs.add(temp);
			}

			if (!rate.isEmpty()) {
				ratelbl.setText(rate.get(0).get(2) + "/5");
				for (int i = 0; i < toInt(rate.get(0).get(2)); i++)
					star[i].setForeground(Color.RED.brighter());
				area.setText(rate.get(0).get(4) + "");
			}

		}
		s.add(btn2 = btn("닫기", a -> dispose()));
		c.setBorder(new EmptyBorder(20, 5, 5, 5));
		revalidate();
		repaint();
	}
}
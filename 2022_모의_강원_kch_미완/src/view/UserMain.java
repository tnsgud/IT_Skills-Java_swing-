 package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class UserMain extends BaseFrame {

	JTextField start = new JHintField("�����", 15);
	JTextField end = new JHintField("������", 15);
	JTextField date = new JHintField("��¥", 15);

	LocalDate today = LocalDate.now();

	public UserMain() {
		super("��������", 1000, 500);
		try {
			dataInit();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setLayout(new GridLayout(0, 1));
		var c1 = new JLabel(getIcon("./��������/images/main.jpg", getWidth(), 250));
		c1.setLayout(new BorderLayout());
		var c2 = new JPanel(new BorderLayout());
		var c1n = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		var c1c = new JPanel(new BorderLayout());
		var c2c = new JPanel(new GridLayout(1, 0));

		add(c1);
		add(c2);
		c1.add(c1n, "North");
		c1.add(c1c);
		c2.add(lbl("��õ ������", JLabel.LEFT, 20), "North");
		c2.add(c2c);

		c1n.add(themeButton());
		for (var bcap : "����,����,�α׾ƿ�".split(",")) {
			c1n.add(btn(bcap, a -> {
				if (a.getActionCommand().equals("�α׾ƿ�")) {
					dispose();
				} else if (a.getActionCommand().equals("����")) {
					new Lookup().setVisible(true);
				} else {
					var pw = getOne("select * from user where no = " + uno);
					var input = JOptionPane.showInputDialog(null, "��й�ȣ�� �Է����ּ���", "�Է�", JOptionPane.QUESTION_MESSAGE);
					if (input != null) {
						if (!input.isEmpty()) {
							if (input.equals(pw)) {
								new Account().setVisible(true);
							}
						}
					}
				}
			}));
		}

		c1c.add(c = new JPanel(new BorderLayout()));

		c.add(lbl("����", JLabel.LEFT, 15), "North");
		c.add(cc = new JPanel());
		cc.setLayout(new BoxLayout(cc, BoxLayout.X_AXIS));

		cc.add(start);
		cc.add(sz(btn("<html>��<br>��", a -> {
			if (start.toString().isEmpty() || end.toString().isEmpty()) {
				return;
			}

			var tmp = start.toString();
			start.setText(end.toString());
			end.setText(tmp);
			start.setForeground(Color.BLACK);
			end.setForeground(Color.BLACK);
		}), 50, 30));
		cc.add(end);
		cc.add(date);
		cc.add(sz(btn("��ȸ", a -> {
			if (start.toString().isEmpty() || end.toString().isEmpty()) {
				eMsg("�����,������,��¥ �� ������ �ֽ��ϴ�.");
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from schedule where date(date) = '" + date.toString()
						+ "' and departure_location2_no = " + loc2List.indexOf(start.toString().split(" ")[1]
								+ " and arrival_location2_no = " + loc2List.indexOf(end.toString().split(" ")[1])));
				if (rs.next()) {
					new Purchase().addWindowListener(new Before(this));
				} else {
					eMsg("���� ������ �����̾����ϴ�.");
					return;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		}), 100, 50));

		date.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					var pop = datepop(date);
					pop.setPreferredSize(new Dimension(date.getWidth(), 80));
					pop.show(date, 0, 60);
				}
				super.mousePressed(e);
			}
		});
		c1c.setBorder(new EmptyBorder(50, 50, 50, 50));

		try {
			var rs = stmt.executeQuery(
					"select title, l.name, r.no from recommend_info ri inner join recommend r on ri.recommend_no = r.no inner join location l on r.location_no = l.no and title = 1 order by r.no asc");
			while (rs.next()) {
				var tmp = new JPanel(new GridBagLayout());
				JLabel img = new JLabel(
						getIcon("./��������/images/recommend/" + hashMap.get(rs.getString(2)) + "/" + rs.getInt(1) + ".jpg",
								120, 120));
				var popup = new JPopupMenu();
				for (var bcap : "�󼼼���,����".split(",")) {
					var item = new JMenuItem(bcap);
					item.setName(rs.getInt("r.no") + "");
					item.addActionListener(a -> {
						if (a.getActionCommand().equals("����")) {
							new Lookup().setVisible(true);
						} else {
							new Detail(toInt(((JMenuItem) a.getSource()).getName())).setVisible(true);
						}
					});
					popup.add(item);
				}

				img.setComponentPopupMenu(popup);
				tmp.add(sz(img, 130, 130));
				if (rs.getRow() % 2 == 0) {
					tmp.setBorder(new EmptyBorder(50, 0, 0, 0));
				}
				c2c.add(tmp);
				img.setBorder(new TitledBorder(new LineBorder(Color.BLACK), rs.getString(2)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		start.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					var pop = locationPopup(start);
					pop.setPreferredSize(new Dimension(start.getWidth(), 200));
					pop.show(start, 0, start.getHeight());
					super.mousePressed(e);
				}
			}
		});

		end.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					var pop = locationPopup(end);
					pop.setPreferredSize(new Dimension(end.getWidth(), 200));
					pop.show(end, 0, start.getHeight());
					super.mousePressed(e);
				}
			}
		});

		c1n.setOpaque(false);
		c1c.setOpaque(false);
		cc.setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	JPopupMenu datepop(JTextField text) {
		var datepop = new JPopupMenu();
		var item = new JMenuItem();
		
		datepop.add(item);
		JLabel lbl1 = lbl(today.getYear() + "", JLabel.CENTER), lbl2 = lbl(today.getMonthValue() + "", JLabel.CENTER),
				lbl3 = lbl(today.getDayOfMonth() + "", JLabel.CENTER);
		JPanel p1, p2, p3;
		item.setLayout(new GridLayout(1, 0));
		item.add(p1 = new JPanel(new BorderLayout()));
		item.add(p2 = new JPanel(new BorderLayout()));
		item.add(p3 = new JPanel(new BorderLayout()));
		p1.add(btn("��", a -> {
			today = today.plusYears(1);
			lbl1.setText(today.getYear() + "");
			lbl2.setText(today.getMonthValue() + "");
			lbl3.setText(today.getDayOfMonth() + "");
		}), "North");
		p1.add(lbl1);
		p1.add(btn("��", a -> {
			today = today.plusYears(-1);
			lbl1.setText(today.getYear() + "");
			lbl2.setText(today.getMonthValue() + "");
			lbl3.setText(today.getDayOfMonth() + "");
		}), "South");

		p2.add(btn("��", a -> {
			today = today.plusMonths(1);
			lbl1.setText(today.getYear() + "");
			lbl2.setText(today.getMonthValue() + "");
			lbl3.setText(today.getDayOfMonth() + "");

		}), "North");
		p2.add(lbl2);
		p2.add(btn("��", a -> {
			today = today.plusMonths(-1);
			lbl1.setText(today.getYear() + "");
			lbl2.setText(today.getMonthValue() + "");
			lbl3.setText(today.getDayOfMonth() + "");
		}), "South");

		p3.add(btn("��", a -> {
			today = today.plusDays(1);
			lbl1.setText(today.getYear() + "");
			lbl2.setText(today.getMonthValue() + "");
			lbl3.setText(today.getDayOfMonth() + "");
		}), "North");
		p3.add(lbl3);
		p3.add(btn("��", a -> {
			today = today.plusDays(-1);
			lbl1.setText(today.getYear() + "");
			lbl2.setText(today.getMonthValue() + "");
			lbl3.setText(today.getDayOfMonth() + "");
		}), "South");

		for(var p : item.getComponents())
		{
			for(var i : ((JComponent)p).getComponents())
			{
				if(i instanceof JButton)
				{
					i.setBackground(new JScrollBar().getBackground());
					i.setForeground(new JComboBox<>().getForeground());					
				}
			}
		}
		
		datepop.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				today = LocalDate.now();
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				text.setText(today + "");
				text.setForeground(Color.BLACK);
				today = LocalDate.now();
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				text.setText(today + "");
				text.setForeground(Color.BLACK);
			}
		});
		return datepop;
	}

	public static void main(String[] args) {
		uno = "1";
		new UserMain();
	}
}

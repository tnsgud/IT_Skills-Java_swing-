package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Main extends BaseFrame {
	JLabel lblLogin, lbl[] = new JLabel[6], icon[] = new JLabel[6];
	ArrayList<JLabel> list = new ArrayList<>();
	SwingWorker worker;

	public Main() {
		super("메인", 800, 600);
		setDefaultCloseOperation(3);
		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = sz(new JPanel(null), 0, 300));
		add(s = new JPanel(new GridLayout(0, 3)), "South");

		n.add(hylbl("<html><font color='green'>농수산물판매관리", 2, 25), "West");
		n.add(lblLogin = hylbl("로그인을 먼저 해주세요.", 4), "East");

		var cap = "로그인,회원가입,거래내역,농산물관리,농산물검색,시도별분석".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel();

			tmp.add(icon[i] = new JLabel(getIcon("./datafiles/메인이미지/" + cap[i] + ".jpg", 80, 80)));
			tmp.add(lbl[i] = lbl(cap[i], 0, 0, 15));

			icon[i].setName(cap[i]);

			icon[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JLabel) e.getSource();

					if (!me.isEnabled())
						return;

					switch (me.getName()) {
					case "로그인":
						new Login().addWindowListener(new Before(Main.this));
						break;
					case "회원가입":
						new Sign().addWindowListener(new Before(Main.this));
						break;
					case "거래내역":
						new TradeHistory().addWindowListener(new Before(Main.this));
						break;
					case "농산물관리":
						new BaseMange().addWindowListener(new Before(Main.this));
						break;
					case "농산물검색":
						new Search().addWindowListener(new Before(Main.this));
						break;
					case "시도별분석":
						new Map().addWindowListener(new Before(Main.this));
						break;
					case "로그아웃":
						iMsg("로그아웃 되었습니다.");
						logout();
						break;
					}
				}
			});

			tmp.setBorder(new LineBorder(Color.LIGHT_GRAY));

			s.add(tmp);
		}

		if (user == null) {
			logout();
		} else {
			login();
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void animation() {
		c.removeAll();
		list.clear();

		if (worker != null) {
			worker.cancel(true);
		}

		var div = user == null ? "" : "and u.division = " + user.get(5);
		var sql = "select b.b_no, b.b_name, b.b_note, b.b_img, sum(s.s_quantity) cnt, sum(f.f_amount * s.s_quantity) price from  base b, farm f, sale s, user u where s.f_no = f.f_no and f.b_no = b.b_no and f.u_no = u.u_no "
				+ div + " group by b.b_no order by cnt desc, price desc, b.b_no limit 5";
		var rs = getRows(sql);

		for (var r : rs) {
			var img = new JLabel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);

					var g2 = (Graphics2D) g;
					var grad = new GradientPaint(0, 0, new Color(0, 255, 255, 50), getWidth(), getHeight(),
							new Color(0, 0, 255, 50));
					var img = new ImageIcon((byte[]) r.get(3));

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g2.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
					g2.setPaint(grad);
					g2.fillRect(0, 0, getWidth(), getHeight());

					g2.setColor(Color.white);
					g2.setFont(new Font("HY헤드라인M", 0, 25));
					g2.drawString(r.get(1) + "[" + (rs.indexOf(r) + 1) + "위]", 30, getHeight() - 60);
				}
			};

			c.add(img).setBounds(list.size() * 800, 0, 800, 320);
			list.add(img);
		}

		int min = -800, max = list.get(list.size() - 1).getX();

		list.get(list.size() - 1).setBounds(min, 0, 800, 320);

		worker = new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				while (true) {
					for (var img : list) {
						img.setLocation(img.getX() - 5, img.getY());

						if (img.getX() <= min) {
							img.setLocation(max, img.getY());

							Thread.sleep(3000);
						}
					}

					Thread.sleep(1);
				}
			}
		};
		worker.execute();

		c.repaint();
		c.revalidate();
	}

	void login() {
		lblLogin.setText(user.get(1) + "님 환영합니다.");

		for (int i = 0; i < lbl.length; i++) {
			icon[i].setEnabled(i != 1);
		}

		lbl[0].setText("로그아웃");
		icon[0].setName("로그아웃");

		animation();
	}

	void logout() {
		user = null;
		lblLogin.setText("로그인을 먼저 해주세요.");

		for (int i = 0; i < lbl.length; i++) {
			icon[i].setEnabled(i < 2);
		}

		lbl[0].setText("로그인");
		icon[0].setName("로그인");

		animation();
	}

	public static void main(String[] args) {
		new Main();
	}

}

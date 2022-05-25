package view;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Admin extends BaseFrame {
	public Admin() {
		super("관리자", 600, 500);

		add(c = new JPanel(new GridLayout(0, 5, 5, 5)));
		add(s = new JPanel(), "South");

		var rs = rs("select * from company");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout()) {
				float a = 0.1f;

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);

					var g2 = (Graphics2D) g;
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
				}
			};
			var l = new JLabel(img("기업/" + r.get(1) + "1.jpg", 100, 100));
			p.setName(r.get(0) + "");
			p.setBorder(new LineBorder(Color.black));
			p.setToolTipText(r.get(1) + "");
			p.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					cno = toInt(p.getName());
					new Company("수정").addWindowListener(new Before(Admin.this));
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					p.a = 1f;

					repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					p.a = 0.1f;

					repaint();
				}
			});
			p.add(l);
			c.add(p);
		}

		for (var c : "채용 정보,지원자 목록,공고 등록,지원자 분석,닫기".split(",")) {
			s.add(btn(c, a -> {
				if (a.getActionCommand().equals("채용 정보")) {
					new AdminJobs().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("지원자 목록")) {
					new Applicant().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("공고 등록")) {
					new Posting().addWindowListener(new Before(this));
				} else if (a.getActionCommand().equals("지원자 분석")) {
					new Chart().addWindowListener(new Before(this));
				} else {
					dispose();
				}
			}));
		}

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				new Main();
			}
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	public static void main(String[] args) {
		new Admin();
	}
}

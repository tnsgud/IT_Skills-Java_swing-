package view;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Admin extends BaseFrame {
	public Admin() {
		super("관리자 메인", 500, 700);

		add(c = new JPanel(new GridLayout(0, 5, 5, 5)));
		add(s = new JPanel(), "South");

		var rs = rs("select c_no, c_name from company");
		for (var r : rs) {
			var p = new JPanel(new BorderLayout()) {
				float op = 0.1f;

				@Override
				protected void paintComponent(Graphics g) {
					var g2 = (Graphics2D) g;
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, op));
				}
			};
			p.setBorder(new LineBorder(Color.black));
			p.setName(r.get(0) + "");
			p.setToolTipText(r.get(1) + "");
			p.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					p.op = 1f;

					repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					p.op = 0.1f;
				}

				@Override
				public void mousePressed(MouseEvent e) {
					cno = toInt(((JPanel) e.getSource()).getName());
					new Company("수정");
				}
			});
			p.add(new JLabel(img("기업/" + r.get(1) + "1.jpg", 100, 100)));
			c.add(p);
		}

		var cap = "채용 정보,지원자 목록,공고 등록,지원자 분석,닫기".split(",");
		for (int i = 0; i < cap.length; i++) {
			s.add(btn(cap[i], a -> {
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
					new Main();
				}
			}));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Admin();
	}
}

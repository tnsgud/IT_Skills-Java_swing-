package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Admin extends BaseFrame {

	String cap[] = "채용 정보,지원자 목록,공고 등록,지원자 분석,닫기".split(",");

	public Admin() {
		super("관리자 메인", 500, 500);

		add(c = new JPanel(new GridLayout(5, 5, 5, 5)));
		add(s = new JPanel(new FlowLayout(FlowLayout.CENTER)), "South");

		load();

		for (int i = 0; i < cap.length; i++) {
			s.add(crt_evt_btn(cap[i], a -> {
				if (a.getActionCommand().equals("채용 정보")) {
					new AdminJobs().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("지원자 목록")) {
					new Applicant().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("공고 등록")) {
					new Posting().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("지원자 분석")) {
					new Chart().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("닫기")) {
					dispose();
					new Main();
				}
			}));
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Admin();
	}

	void load() {
		c.removeAll();
		var rs = getResults("select c_no, c_img, c_name from company");

		for (var r : rs) {
			var lbl = new JLabel(toIcon(r.get(1), 80, 80)) {
				float alpha = 0.1f;

				@Override
				protected void paintComponent(Graphics g) {
					var g2 = (Graphics2D) g;
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
					super.paintComponent(g);
				}
			};
			lbl.setToolTipText(r.get(2).toString());

			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					lbl.alpha = 1f;
					lbl.repaint();
					super.mouseEntered(e);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					lbl.alpha = 0.1f;
					lbl.repaint();
					super.mouseExited(e);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					new Company(r.get(0) + "", Admin.this).addWindowListener(new before(Admin.this));
					super.mousePressed(e);
				}
			});

			lbl.setBorder(new LineBorder(Color.BLACK));

			c.add(lbl);
		}

	}
}

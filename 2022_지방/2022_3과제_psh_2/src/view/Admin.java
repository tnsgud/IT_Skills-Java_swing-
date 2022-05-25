package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Admin extends BaseFrame {
	public Admin() {
		super("관리자 메인", 500, 500);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(c = new JPanel(new GridLayout(0, 5, 5, 5)));
		add(s = new JPanel(), "South");

		load();
		
		for (var c : "채용 정보,지원자 목록,공고 등록,지원자 분석,닫기".split(",")) {
			s.add(btn(c, a->{
				if(a.getActionCommand().equals("채용 정보")) {
					new AdminJobs().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals("지원자 목록")) {
					new Applicant().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals("공고 등록")) {
					new Posting().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals("지원자 분석")) {
					new Chart().addWindowListener(new Before(this));
				}else {
					dispose();
				}
			}));
		}
	}
	
	void load() {
		var rs = rs("select c_name from company");
		for (var r : rs) {
			var l = new JLabel(img("기업/" + (r.get(0).toString()) + "1.jpg", 80, 80)) {
				float al = 0.1f;

				@Override
				protected void paintComponent(Graphics g) {
					var g2 = (Graphics2D) g;
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, al));
					super.paintComponent(g);
				}
			};
			l.setToolTipText(r.get(0) + "");
			l.setBorder(new LineBorder(Color.black));
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					l.al = 1f;
					l.repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					l.al = 0.1f;
					l.repaint();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					new Company(r.get(0) + "", Admin.this).addWindowListener(new Before(Admin.this));
				}
			});
			c.add(l);
		}
	}

	public static void main(String[] args) {
		new Admin();
	}
}

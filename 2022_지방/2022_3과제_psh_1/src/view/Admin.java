package view;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Admin extends BaseFrame {
	String cap[] = "채용정보,지원자 목록,공고 등록,지원자 분석,닫기".split(",");

	public Admin() {
		super("관리자 메인", 500, 500);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(c = new JPanel(new GridLayout(0, 5, 5, 5)));
		add(s = new JPanel());

		load();
		
		for (var c : cap) {
			s.add(btn(c, a->{
				if(a.getActionCommand().equals(cap[0])) {
					new AdminJobs().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals(cap[1])) {
					new Applicant().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals(cap[2])) {
					new Posting().addWindowListener(new Before(this));
				}else if(a.getActionCommand().equals(cap[3])) {
					new Chart().addWindowListener(new Before(this));
				}else {
					dispose();
				}
			}));
		}
	}

	void load() {
		c.removeAll();
		var rs = getResults("select c_no, c_img, c_name from company");
		for (var r : rs) {
			var lbl = new JLabel(img(r.get(1), 80, 80)) {
				float alpha = 0.1f;

				@Override
				protected void paintComponent(Graphics g) {
					var g2 = (Graphics2D) g;
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
					super.paintComponent(g);
				}
			};
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					lbl.alpha = 1f;
					lbl.repaint();
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					lbl.alpha = 0.1f;
					lbl.repaint();
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					new Company(r.get(0)+"", Admin.this);
				}
			});
			lbl.setToolTipText(r.get(2) + "");
			c.add(lbl);
		}
		c.repaint();
		c.revalidate();
	}

	public static void main(String[] args) {
		new Admin();
	}
}

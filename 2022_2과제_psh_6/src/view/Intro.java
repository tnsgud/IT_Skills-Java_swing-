package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class Intro extends BaseFrame {
	JScrollPane scr;
	ArrayList<GrayImage> imgs = new ArrayList<>();
	JPanel m1, m2;

	public Intro() {
		super("지점소개", 800, 600);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(sz(scr = new JScrollPane(nw = new JPanel(new FlowLayout(0))), 150, 70), "West");
		n.add(btn("예약하기", a -> {
			new Reserve().addWindowListener(new Before(this));
		}), "East");

		var rs = rs(
				"select t.t_no, t_name from cafe c, theme t where concat(',', c.t_no, ',') like concat('%,', t.t_no, ',%') and c_no=?",
				cno);
		for (var r : rs) {
			var img = new GrayImage("./Datafiles/테마/" + r.get(0) + ".jpg", 40, 40);
			img.setName(r.get(0) + "");
			img.setToolTipText(r.get(1) + "");
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					imgs.forEach(i -> i.isSelected = false);
					img.isSelected = true;

					tno = toInt(img.getName());
					load();
				}
			});
			nw.add(sz(img, 40, 40));
			imgs.add(img);
		}

		var l = lblH(rs("select c_name from cafe where c_no=?", cno).get(0).get(0) + "", 2, 0, 25);
		c.add(l, "North");
		c.add(cc = new JPanel(new GridLayout(1, 0, 5, 5)));

		imgs.get(0).isSelected = true;
		tno = toInt(imgs.get(0).getName());
		load();

		cc.setBorder(new EmptyBorder(5, 5, 5, 5));
		scr.getVerticalScrollBar().setVisible(imgs.size() > 3);
		l.setForeground(Color.orange);

		setVisible(true);
	}

	private void load() {
		cc.removeAll();

		var info = rs(
				"select t_name, t_explan, g_name, t_personnel, t_time, format(c_price, '#,##0') from cafe c, theme t, genre g where g.g_no=t.g_no and t.t_no=? and c.c_no=?",
				tno, cno).get(0);

		cc.add(m1 = new JPanel(new BorderLayout()));
		cc.add(m2 = new JPanel(new BorderLayout()));
		m1.add(new JLabel(img("테마/" + tno + ".jpg", 400, 500)));

		var l1 = lblH(info.get(0) + "", 2, 0, 15);
		var l2 = lblH("<html>" + info.get(1) + "", 2, 0, 15);
		var txt = "<html>";
		var cap = "장르,최대 인원,시간,가격".split(",");
		for (int i = 0; i < cap.length; i++) {
			txt += cap[i] + " : " + info.get(i + 2) + (i == 0 ? "" : i == 1 ? "명" : i == 2 ? "분" : "원") + "<br/>";
		}
		var l3 = lblH(txt, 2, 0, 13);

		m2.add(l1, "North");
		m2.add(l2);
		m2.add(l3, "South");
		Stream.of(c, cc, m1, m2).forEach(p -> p.setBackground(Color.black));
		Stream.of(l1, l2, l3).forEach(l->l.setForeground(Color.white));
		
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		cno = "A-003";
		new Intro();
	}
}

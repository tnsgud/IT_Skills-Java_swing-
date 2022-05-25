package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Introduce extends BaseFrame {
	ArrayList<GrayImage> imgs = new ArrayList<>();
	JLabel img;
	JScrollPane scr;
	int idx = 0;

	public Introduce() {
		super("지점소개", 800, 700);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(ne = new JPanel(), "East");
		n.add(sz(scr = new JScrollPane(nw = new JPanel(new FlowLayout())), 160, 75), "West");

		var rs = getResult("select * from cafe where c_no=?", cno).get(0);
		for (var tno : rs.get(2).toString().split(",")) {
			var img = new GrayImage("./Datafiles/테마/" + tno + ".jpg", 45, 45);
			img.setName(tno);
			img.setBorder(new LineBorder(Color.black));
			img.setToolTipText(getResult("select t_name from theme where t_no=?", toInt(tno)).get(0).get(0) + "");
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					imgs.forEach(i -> i.isSelected = false);
					var s = ((GrayImage) e.getSource());
					s.isSelected = true;
					setInfo(s.getName());

					repaint();
					revalidate();
				}
			});
			nw.add(sz(img, 45, 45));
			imgs.add(img);
		}

		ne.add(btn("예약하기", a -> {
			imgs.forEach(l -> {
				if (l.isSelected) {
					tno = toInt(l.getName());
				}
			});
			
			new Reserve().addWindowListener(new Before(this));
		}));

		var tit = lbl(rs.get(1) + "", 2, 25);

		c.add(tit, "North");
		c.add(img = new JLabel(), "West");
		c.add(cc = new JPanel(new BorderLayout()));

		imgs.get(idx).isSelected = true;
		setInfo(imgs.get(idx).getName());

		c.setBackground(Color.black);
		tit.setForeground(Color.orange);
		c.setBorder(new EmptyBorder(5, 5, 5, 5));
		scr.getHorizontalScrollBar().setVisible(imgs.size() > 3);
	}

	private void setInfo(String tno) {
		cc.removeAll();
		img.setIcon(img("테마/" + tno + ".jpg", 400, 550));

		var rs = getResult(
				"select t_name , t_explan, g_name, t_personnel, t_time from theme t, genre g where g.g_no=t.g_no and t_no=?",
				tno).get(0);

		var l1 = lbl(rs.get(0) + "", 2, 25);
		var l2 = lbl("<html>" + rs.get(1) + "</html>", 2, 25);
		var txt = "<html>";
		var idx = 3;
		for (var c : "장르,최대 인원,시간".split(",")) {
			txt += c + " : " + rs.get(idx) + (idx == 5 ? "명" : idx == 6 ? "분" : "") + "<br/>";
		}
		txt += "가격 : " + format.format(toInt(getResult("select c_price from cafe where c_no=?", cno).get(0).get(0)))
				+ "원</html>";
		var l3 = lbl(txt, 2, 15);

		cc.add(l1, "North");
		cc.add(l2);
		cc.add(l3, "South");

		Stream.of(l1, l2, l3).forEach(l -> {
			l.setOpaque(true);
			l.setBackground(Color.black);
			l.setForeground(Color.white);
		});

		cc.repaint();
		cc.revalidate();
	}

	public static void main(String[] args) {
		cno = "A-001";
		new Introduce();
	}
}

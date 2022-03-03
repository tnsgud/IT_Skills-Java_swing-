import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MyPage extends BaseFrame {
	JPanel c, cn, cw, cc;
	JButton reBtn;
	String ureident = "040229-3123456";

	public MyPage() {
		ui();

		setVisible(true);
	}

	private void ui() {
		add(c = new JPanel(new BorderLayout(10, 10)));
		c.add(sz(cn = new JPanel(new BorderLayout(5, 5)), 550, 300), "North");
		{
			var t = new JPanel(new GridLayout(1, 0, 10, 10));
			t.add(cw = new JPanel(new BorderLayout(5, 5)));
			t.add(cc = new JPanel(new BorderLayout(5, 5)));
			c.add(t);
		}

		cn.add(lbl("프로필", JLabel.LEFT, 25), "North");
		cn.add(lbl("이미지", JLabel.LEFT, 30), "West");
		cn.add(lbl("이름 : 박순형", JLabel.LEFT, 25));

		{
			var t = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			t.add(btn("수정", a -> {
				new EditProfile(MyPage.this);
			}));
			cn.add(t, "South");
		}

		cw.add(lbl("내 경로", JLabel.LEFT, 25), "North");
		{
			var t = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			t.add(btn("조회", a -> {
				new MyRoute(MyPage.this);
			}));
			cw.add(t);
		}

		cc.add(lbl("백신 내역", JLabel.LEFT, 25), "North");
		{
			var t = new JPanel(new FlowLayout(FlowLayout.LEFT));
			t.add(reBtn = btn("예약", a -> {
			}));
		}

//		예약버튼 enable

		cn.setBorder(new CompoundBorder(new LineBorder(Color.orange, 5), new EmptyBorder(10, 10, 10, 10)));
		cw.setBorder(new CompoundBorder(new LineBorder(Color.orange, 5), new EmptyBorder(10, 10, 10, 10)));
		cc.setBorder(new CompoundBorder(new LineBorder(Color.orange, 5), new EmptyBorder(10, 10, 10, 10)));
	}

	public static void main(String[] args) {
		new MyPage();
	}
}

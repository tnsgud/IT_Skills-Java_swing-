package view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DateFormatter;

import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;

public class MyPage extends BasePage {

	JButton profileEdit;
	JButton personalEdit;
	JButton localEdit;
	JButton aboutEdit;

	JLabel img = new JLabel(getIcon("./지급자료/유저사진/" + uno + ".jpg", 150, 150));

	String cap = "백신내역", str_arr[] = new String[2];
	LocalDate date_arr[] = new LocalDate[2];

	public MyPage() {
		super();
		ui();
	}

	void ui() {
		setLayout(new GridBagLayout());

		add(c = new JPanel(new BorderLayout(10, 10)));

		c.add(sz(cn = new JPanel(new BorderLayout(5, 5)), 550, 200), "North");
		c.add(cc = new JPanel(new GridLayout(1, 0, 10, 10)));

		cn.add(lbl("프로필", JLabel.LEFT, 25), "North");

		{
			var t = new JPanel(new BorderLayout());
			t.add(btn("수정", a -> {
				new EditProfile(MyPage.this);
			}), "South");

			var pw = upw.substring(0, 2);
			for (int i = 0; i < upw.length() - 2; i++) {
				pw += "*";
			}
			System.out.println(ulocal.replaceAll("\r\n", ""));
			cn.add(img, "West");
			cn.add(sz(lbl(
					"<html><pre><font face='맑은 고딕'>이름\t" + uname + "<br>비밀번호\t" + pw + "<br>전화번호\t" + uphone
							+ "<br>위치\t" + (ulocal.replaceAll("\r\n", "")) + "-" + upoint + "</font></pre>",
					JLabel.LEFT, 20), 250, 300));
			cn.add(sz(t, 120, 80), "East");
		}

		{
			var t = new JPanel(new BorderLayout(5, 5));
			var ts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			var btn = btn("수정", a -> {
				if (a.getActionCommand().contentEquals("수정")) {
					new EditProfile(MyPage.this);
				} else {
					BasePage.mf.swapPage(new ReservePage());
				}
			});
			ts.add(btn);
			t.add(lbl(cap, JLabel.LEFT), "North");

			try {

				var rs = rs(
						"select date_format(`when`, '%Y년 %m월 %d일'), date_format(`when`, '%Y-%m-%d') from purchase where user="
								+ uno + " order by no asc");

				for (int i = 0; i < str_arr.length; i++) {
					str_arr[i] = "미접종";
					date_arr[i] = LocalDate.of(2000, 01, 01);
				}

				while (rs.next()) {
					str_arr[rs.getRow() - 1] = rs.getString(1);
					date_arr[rs.getRow() - 1] = LocalDate.parse(rs.getString(2));
				}

				btn.setEnabled(false);

				if (str_arr[0].contentEquals("미접종")) {
					btn.setEnabled(true);
				} else if (str_arr[1].contentEquals("미접종") && LocalDate.now().isAfter(date_arr[0].plusDays(21))) {
					btn.setEnabled(true);
				} else if (str_arr[1].contentEquals("미접종")) {
					str_arr[1] += "(" + (date_arr[0].plusDays(21).getDayOfYear() - LocalDate.now().getDayOfYear())
							+ "후 접종가능)";
				}

				var c = "<html><font face='맑은 고딕'>1차 : " + str_arr[0] + "<br>2차 : " + str_arr[1] + "</font>";
				t.add(lbl(c, JLabel.LEFT));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			btn.setText("예약");
			t.add(ts, "South");

			t.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));

			cc.add(t);
		}

		cn.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
	}

	public static void main(String[] args) {
		BasePage.mf.swapPage(new LoginPage());
		BasePage.mf.setVisible(true);
	}
}

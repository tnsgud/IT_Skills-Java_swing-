package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SignPage extends BasePage {
	JComboBox<String> rCombo;
	JTextField id, pw, pwchk, birth, phone, rrfn, rrln, name;

	String cap1[] = "비밀번호,이름,거주지,주민번호 앞자리".split(",");
	String cap2[] = "비밀번호 확인,전화번호,,주민번호 뒷자리".split(",");

	public SignPage() {
		try {
			datainit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		init();
	}

	void init() {
		add(m = new JPanel(new GridBagLayout()));
		m.add(sz(c = new JPanel(new GridLayout(0, 1, 5, 5)), 250, 350));
		setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

		JComponent jc1[] = { pw = new JPasswordField(), name = new JTextField(), rCombo = new JComboBox<>(),
				rrfn = new JTextField() };
		JComponent jc2[] = { pwchk = new JPasswordField(), phone = new JTextField(), null, rrln = new JTextField() };
		for (var k : residence.keySet()) {
			rCombo.addItem(residence.get(k)[1].toString());
		}

		var n = new JPanel(new GridLayout(1, 0, 5, 5));
		var c = new JPanel(new GridLayout(1, 0, 5, 5));

		n.add(lbl("아이디", 2));
		c.add(id = new JTextField());

		this.c.add(n);
		this.c.add(c);

		for (int i = 0; i < cap1.length; i++) {
			var tmp_n = new JPanel(new GridLayout(1, 0, 5, 5));
			var tmp_c = new JPanel(new GridLayout(1, 0, 5, 5));
			tmp_n.add(lbl(cap1[i], 2));
			tmp_n.add(lbl(cap2[i], 2));
			this.c.add(tmp_n);
			tmp_c.add(jc1[i]);
			if (jc2[i] != null)
				tmp_c.add(jc2[i]);
			this.c.add(tmp_c);
		}

		this.c.add(btn("회원가입", a -> {
			if (id.getText().isEmpty() || pw.getText().isEmpty() || pwchk.getText().isEmpty()
					|| rCombo.getSelectedIndex() == -1 || phone.getText().isEmpty() || rrfn.getText().isEmpty()
					|| rrln.getText().isEmpty()) {
				eMsg("빈칸이 있습니다.");
				return;
			}

			try {
				var rs = rs("select * from user where phone = '" + phone.getText() + "'");
				if (rs.next()) {
					eMsg("전화번호 겹칩니다.");
					return;
				}

				var rs2 = rs("select * from user where resident = '" + rrfn.getText() + "-" + rrln.getText() + "'");
				if (rs2.next()) {
					eMsg("주민번호가 겹칩니다.");
					return;
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (!pw.getText().equals(pwchk.getText())) {
				eMsg("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
				return;
			}

			if (!(pw.getText().matches(".*[0-9].*") && pw.getText().matches(".*[a-zA-Z].*")
					&& pw.getText().matches(".*[\\W].*"))) {
				eMsg("비밀번호 형식이 일치하지 않습니다.");
				return;
			}

			if ((rrfn.getText().length() != 6) || (rrln.getText().length() != 8)) {
				eMsg("주민번호 길이를 맞추세요.");
				return;
			}

			if (!rrfn.getText().matches(".*[0-9].*") || !rrln.getText().matches(".*[0-9].*")) {
				eMsg("주민번호는 숫자로 입력해주세요.");
				return;
			}

			String resident = rrfn.getText() + "-" + rrln.getText();
			String birth = "", front = "";
			if (toInt(rrln.getText().substring(0, 1)) == 3 || toInt(rrln.getText().substring(0, 1)) == 4) {
				front = "20";
			} else if (toInt(rrln.getText().substring(0, 1)) == 1 || toInt(rrln.getText().substring(0, 1)) == 2) {
				front = "19";
			} else {
				eMsg("주민번호 뒷자리를 확인해주세요.");
				return;
			}

			iMsg("회원가입이 완료되었습니다.");
			birth = front + rrfn.getText().substring(0, 2) + "-" + rrfn.getText().substring(2, 4) + "-"
					+ rrfn.getText().substring(4, 6);
			execute("insert into user values(0, '" + name.getText() + "','" + id.getText() + "','" + pw.getText()
					+ "','" + birth + "','" + phone.getText() + "','" + resident + "','"
					+ resMap.get(rCombo.getSelectedItem()) + "',0)");
			System.out.println("insert into user values(0, '" + name.getText() + "','" + id.getText() + "','"
					+ pw.getText() + "','" + birth + "','" + phone.getText() + "','" + resident + "','"
					+ resMap.get(rCombo.getSelectedItem()) + "',0)");
			mf.swapPage(new LoginPage());
		}), "South");
		this.c.add(hyplbl("이미 계정이 있으십니까?", JLabel.LEFT, 13, Font.PLAIN, Color.ORANGE, new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mf.swapPage(new LoginPage());
				super.mousePressed(e);
			}
		}));

		this.c.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}
}

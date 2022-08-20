package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Find extends BaseFrame {
	static Find find = new Find();
	JRadioButton rad[] = Stream.of("휴대폰 번호,이메일주소".split(",")).map(c -> new JRadioButton(c + "로 찾기"))
			.toArray(JRadioButton[]::new);
	JComboBox comNum;
	JTextField txtName = txt("한글 또는 영문으로 입력해주세요.", 20), txtEmail = txt("이메일 주소를 입력해주세요.", 20),
			txtID = txt("아이디를 입력해주세요.", 20), txtNum1, txtNum2;
	JLabel lblTItle;
	String sql = "select u_id, u_pw from user where u_%s=? and u_%s=?";
	JButton btn;
	JPanel phoneP, emailP;

	public Find() {
		super("", 500, 350);

		add(lblTItle = lbl("아이디 찾기", 2, 35), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(), "South");

		c.add(cn = new JPanel(new FlowLayout(0)), "North");
		c.add(cc = new JPanel(new FlowLayout(0)));

		s.add(btnBlack("취소", a -> dispose()));
		s.add(btn = btn("다음", a -> {
			var flag = lblTItle.getText().contains("아이디");

			if (rad[0].isSelected()) {
				for (var t : new JTextField[] { flag ? txtName : txtID, txtNum1, txtNum2 }) {
					if (t.getText().isEmpty()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}
			} else {
				for (var t : new JTextField[] { flag ? txtName : txtID, txtEmail }) {
					if (t.getText().isEmpty()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}
			}

			var phone = comNum.getSelectedItem() + "-" + txtNum1.getText() + "-" + txtNum2.getText();
			var email = txtEmail.getText();

			if (rad[0].isSelected() && !phone.matches("^\\d{3}-\\d{4}-\\d{4}$")) {
				eMsg("전화번호 형식이 일치하지 않습니다.");
				return;
			}

			if (rad[1].isSelected() && !email.matches("^.{3,}@.{2,}\\..{2,}$")) {
				eMsg("이메일 형식이 일치하지 않습니다.");
				return;
			}

			var rs = getRows(String.format(sql, flag ? "name" : "id", rad[0].isSelected() ? "phone" : "email"),
					txtName.isVisible() ? txtName.getText() : txtID.getText(), rad[0].isSelected() ? phone : email);

			if (rs.isEmpty()) {
				eMsg("일치하는 정보가 없습니다.");
				return;
			}

			iMsg("회원님의 " + (flag ? "아이디" : "비밀번호") + "는 '" + rs.get(0).get(flag ? 0 : 1) + "'입니다.");
			dispose();
		}));

		for (var r : rad) {
			cn.add(r);
		}

		cc.add(lbl("아이디", 2, 15));
		cc.add(txtName);
		cc.add(txtID);

		phoneP = new JPanel(new FlowLayout(0));

		phoneP.add(lbl("휴대폰 번호", 0, 15), "West");

		phoneP.add(comNum = new JComboBox("선택,010,011,016,017,018,019".split(",")));
		phoneP.add(txtNum1 = new JTextField(10));
		phoneP.add(lbl("-", 0));
		phoneP.add(txtNum2 = new JTextField(10));

		emailP = new JPanel(new FlowLayout(0));

		emailP.add(lbl("이메일 주소", 2, 15));
		emailP.add(txtEmail);

		c.add(phoneP, "South");

		rad[0].setSelected(true);

		rad[0].addActionListener(a -> {
			c.remove(emailP);
			c.add(phoneP, "South");

			c.repaint();
			c.revalidate();
		});
		rad[1].addActionListener(a -> {
			c.remove(phoneP);
			c.add(emailP, "South");

			c.repaint();
			c.revalidate();
		});

		var bg = new ButtonGroup();
		Stream.of(rad).forEach(b -> bg.add(b));
	}

	static Find ID() {
		find.lblTItle.setText("아이디 찾기");
		((JLabel) find.cc.getComponent(0)).setText("이름");
		find.txtID.setVisible(false);
		find.txtName.setVisible(true);

		find.comNum.setSelectedIndex(-1);
		for (var t : new JTextField[] { find.txtEmail, find.txtName, find.txtID, find.txtNum1, find.txtNum2 }) {
			t.setText("");
		}

		find.setVisible(true);
		return find;
	}

	static Find PW() {
		find.lblTItle.setText("비밀번호 찾기");
		((JLabel) find.cc.getComponent(0)).setText("아이디");
		find.txtID.setVisible(true);
		find.txtName.setVisible(false);

		find.comNum.setSelectedIndex(-1);
		for (var t : new JTextField[] { find.txtEmail, find.txtName, find.txtID, find.txtNum1, find.txtNum2 }) {
			t.setText("");
		}

		find.setVisible(true);
		return find;
	}

	public static void main(String[] args) {
		new Login();
	}
}

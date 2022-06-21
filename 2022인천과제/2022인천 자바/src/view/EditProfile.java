package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditProfile extends BaseDialog {
	String cap[] = "이름,비밀번호,전화번호".split(",");
	JTextField txt[] = new JTextField[cap.length];
	JButton btn;
	JComboBox<String> locCombo;

	String infos[] = new String[4];

	public EditProfile(BasePage bp) {
		super("정보 수정", 300, 250);

		ui();
		setInfo();

		setVisible(true);
	}

	private void setInfo() {
		try {
			var rs = BasePage.stmt.executeQuery(
					"select u.name, u.pw, u.phone, b.name from user u, building b where u.point = b.point");
			if (rs.next()) {
				infos[0] = rs.getString(1);
				infos[1] = rs.getString(2);
				infos[2] = rs.getString(3);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < cap.length; i++) {
			txt[i].setText(infos[i]);
			txt[i].getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					btn.setEnabled(true);
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					btn.setEnabled(true);
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					btn.setEnabled(true);
				}
			});
		}
	}

	private void ui() {
		setLayout(new GridLayout(0, 1, 10, 10));
		for (int i = 0; i < cap.length + 1; i++) {
			var t = new JPanel(new FlowLayout());
			if (i == 3) {
				t.add(BasePage.sz(BasePage.lbl("거주지", JLabel.LEFT, 15), 100, 25));
				t.add(BasePage.sz(
						locCombo = new JComboBox<String>(
								new DefaultComboBoxModel<>("이클립스 타워,틴슬 타워,알타 st,인테그리티 웨이,라스라구나 0604".split(","))),
						135, 25));
			} else {
				t.add(BasePage.sz(BasePage.lbl(cap[i], JLabel.LEFT, 15), 100, 25));
				t.add(txt[i] = new JTextField(12));
			}
			add(t);
		}

		add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
			@Override
			public void updateUI() {
				super.updateUI();
				add(btn = BasePage.btn("수정", a -> {
					for (int i = 0; i < txt.length; i++) {
						if (txt[i].getText().isEmpty()) {
							BasePage.eMsg("누락된 항목이 있습니다.");
							return;
						}
					}

					BasePage.iMsg("정보가 수정되었습니다.");
					BasePage.execute("update user set name='" + txt[0].getText() + "', pw='" + txt[1].getText()
							+ "', phone='" + txt[2].getText() + "' point=" + locCombo.getSelectedItem() + 304);
					dispose();
				}));
			}
		}, "South");

		btn.setEnabled(false);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}
package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditProfile extends BaseDialog {

	String cap[] = "이름,비밀번호,전화번호,위치".split(",");
	JTextField txt[] = new JTextField[cap.length];
	JButton btn;

	public EditProfile(BasePage bp) {
		super("정보 수정", 300, 250);

		ui();
		setInfo();

		setVisible(true);
	}

	private void setInfo() {
		var c = "박순형,user01!,010-2776-5930,서구 6-3".split(",");
		for (int i = 0; i < c.length; i++) {
			txt[i].setText(c[i]);
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
		for (int i = 0; i < cap.length; i++) {
			var t = new JPanel(new FlowLayout());
			t.add(BasePage.sz(BasePage.lbl(cap[i], JLabel.LEFT, 15), 100, 25));
			t.add(txt[i] = new JTextField(12));
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
							+ "', phone='" + txt[2].getText() + "' point=" + txt[3].getText());
					dispose();
				}));
			}
		}, "South");

		btn.setEnabled(false);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}

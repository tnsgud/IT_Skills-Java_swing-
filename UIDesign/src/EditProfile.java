import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditProfile extends BaseDialog {

	String cap[] = "이름,비밀번호,전화번호,위치".split(",");
	JTextField txt[] = new JTextField[cap.length];
	boolean isChanged = false;
	JButton btn;

	public EditProfile(BaseFrame bf) {
		super(bf, "정보수정", 300, 250);
		setLayout(new GridLayout(0, 1, 5, 5));

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
		for (int i = 0; i < cap.length; i++) {
			var t = new JPanel(new FlowLayout());
			t.add(sz(lbl(cap[i], JLabel.LEFT, 15), 100, 25));
			t.add(txt[i] = new JTextField(12));
			add(t);
		}

		add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
			@Override
			public void updateUI() {
				super.updateUI();
				add(btn = btn("수정", a -> {
					for (int i = 0; i < txt.length; i++) {
						if (txt[i].getText().isEmpty()) {
							eMsg("누락된 항목이 있습니다.");
							return;
						}
					}
					
					iMsg("정보가 수정되었습니다.");
				}));
			}
		}, "South");
		
		btn.setEnabled(false);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}

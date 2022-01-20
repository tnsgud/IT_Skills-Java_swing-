package view;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import db.DB;

public class Account extends BaseDialog {
	String[] cap = "id,pwd,name,email,point".split(",");
	JTextField[] txt = new JTextField[cap.length];

	public Account(JFrame jf) {
		super(jf, "계정", 300, 600);

		ui();
		data();

		setVisible(true);
	}

	private void data() {
		try {
			var rs = DB.rs("select * from user where no=?", BaseFrame.no);
			if(rs.next()) {
				for (int i = 0; i < txt.length; i++) {
					txt[i].setText(rs.getString(i+2));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new GridBagLayout());

		var root = new JPanel(new BorderLayout());
		var c = new JPanel(new GridLayout(0, 1, 20, 20));

		add(root);
		root.add(BaseFrame.lbl("계정", 2, 35), "North");
		root.add(c);

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(BaseFrame.sz(BaseFrame.lbl(cap[i], 2), 30, 20));
			p.add(txt[i] = new JTextField(20));
			txt[i].setEnabled(!(i == 0 || i == 4));
			c.add(p);
		}

		var p = new JPanel(new GridLayout(1, 0, 10, 10));
		for (var s : "수정,취소".split(",")) {
			p.add(BaseFrame.btn(s, a -> {
				if (a.getActionCommand().equals("수정")) {
					for (var t : txt) {
						if (t.getText().equals(t.getName()) || t.getText().isEmpty()) {
							BaseFrame.eMsg("공란을 확인해주세요.");
							return;
						}
					}

					if (!txt[1].getText().matches(".*[\\W].*")) {
						BaseFrame.eMsg("특수문자를 포함해주세요.");
						return;
					}

					DB.execute("update user set pwd = ?, name=?, email=? where no=?", txt[1].getText(),
							txt[2].getText(), txt[3].getText(), BaseFrame.no);
				} else {
					dispose();
				}
			}));
		}

		add(root);

	}

	public static void main(String[] args) {
		BaseFrame.no = 1;
		var jf = new JFrame();
		jf.setSize(500, 500);
		new Account(jf);
	}
}

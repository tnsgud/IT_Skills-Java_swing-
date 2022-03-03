package view;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import db.DB;
import tool.Tool;

public class Account extends BaseDialog implements Tool {
	String[] cap = "id,pwd,name,email,point".split(",");
	JTextField[] txt = new JTextField[cap.length];

	public Account() {
		super("", 250, 350);

		ui();
		data();

		setVisible(true);
	}

	private void data() {
		var rs = DB.rs("select id, pwd, name, email, point from user where no=?", BaseFrame.no);
		try {
			if(rs.next()) {
				for (int i = 0; i < txt.length; i++) {
					txt[i].setText(rs.getString(i+1));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new GridLayout(0, 1, 10, 10));

		add(lbl("계정", 2, 25));
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0, 0, 0));
			p.add(sz(lbl(cap[i], 2), 40, 20));
			p.add(txt[i] = new JTextField(15));
			txt[i].setEnabled(i>0 && i<4);
			add(p);
		}
		add(s = new JPanel(new GridLayout(1, 0, 10, 10)));
		for (var c : "수정,취소".split(",")) {
			s.add(btn(c, a -> {
				if(a.getActionCommand().equals("수정")) {
					for (var t : txt) {
						if(t.getText().isEmpty()) {
							eMsg("공란을 확인해주세요.");
							return;
						}
					}
					
					if(!txt[1].getText().matches(".*[\\W].*")) {
						eMsg("특수문자를 포함해주세요.");
						return;
					}
					
					DB.execute("update user set pwd=?, name=?, email=? where no=?", txt[1].getText(), txt[2].getText(), txt[3].getText(), BaseFrame.no);
					dispose();
				}else {
					dispose();
				}
			}));
		}
		
		((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 10, 20, 10));
	}

	public static void main(String[] args) {
		BaseFrame.no = 1;
		new Account();
	}
}

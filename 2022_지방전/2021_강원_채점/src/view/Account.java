package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class Account extends BaseDialog {
	String[] cap = "id,pwd,name,email,point".split(",");
	JTextField txt[] = new JTextField[cap.length];

	public Account() {
		super("계정", 300, 450);

		setLayout(new GridLayout(0, 1, 5, 5));
		add(lbl("계정", 2, 25));
		var rs = rs("select * from user where no=?", BaseFrame.uno);
		try {
			if (rs.next()) {
				for (int i = 0; i < rs.getMetaData().getColumnCount()-1; i++) {
					var p = new JPanel(new BorderLayout(5, 5));
					p.add(sz(lbl(cap[i], 0), 50, 20), "West");
					p.add(txt[i] = new JTextField(15));
					txt[i].setText(rs.getString(i+2));
					txt[i].setEnabled(i > 0 && i < 4);
					add(p);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)));

		for (var c : "수정,취소".split(",")) {
			s.add(btn(c, a -> {
				if(a.getActionCommand().equals("취소")) {
					dispose();
				}else {
					for (int i = 1; i < 4; i++) {
						if(txt[i].getText().isEmpty()) {
							eMsg("공란을 확인해주세요.");
							return;
						}
					}
					
					if(!txt[1].getText().matches(".*[\\W].*")) {
						eMsg("특수문자를 포함해주세요.");
						return;
					}
					
					execute("update user set pwd=? , name=?, email=? where no=?", txt[1].getText(), txt[2].getText(), txt[3].getText(),BaseFrame.uno);
					dispose();
				}
			}));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		BaseFrame.uno = 1;
		new Account();
	}
}

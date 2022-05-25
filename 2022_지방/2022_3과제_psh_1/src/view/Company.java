package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Company extends BaseFrame {
	Admin admin;
	JLabel img;
	JButton btn;
	File f;
	JTextField txt[] = new JTextField[5];
	String cno;

	public Company(String cno) {
		super("회사", 400, 400);
		this.cno = cno;

		ui();

		setVisible(true);
	}

	private void ui() {
		add(c = new JPanel(new BorderLayout(10, 10)));
		c.add(sz(img = new JLabel(), 200, 200), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));

		var rs = getResults("select * from company where c_no=?", cno);
		for (var r : rs) {
			r.set(4, String.join(",",
					Arrays.stream(r.get(4).toString().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));
		}

		var cap = "기업명,대표자,주소,직종,직원수".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());
			tmp.add(sz(lbl(cap[i], 2), 60, 0), "West");
			tmp.add(txt[i] = new JTextField());
			txt[i].setText(rs.get(0).get(i + 1) + "");
			txt[i].setEnabled(false);
			cc.add(tmp);
		}
		c.add(btn = btn("닫기", a -> {
			if (a.getActionCommand().equals("닫기")) {
				dispose();
			} else {
				for (var t : txt) {
					if (t.getText().isEmpty()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}

				try {
					execute("update company set c_ceo=?, c_address=? where c_no=?", txt[1].getText(), txt[2].getText(),
							cno);
					if (f != null) {
						execute("update company set c_img=? where c_no=?", new FileInputStream(f), cno);
						Files.copy(f.toPath(), new File("./datafiles/기업/" + txt[0].getText() + ".jpg").toPath(),
								StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (SQLException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				iMsg("수정이 완료되었습니다.");
				this.admin.load();
				dispose();
			}
		}), "South");

		img.setIcon(img(rs.get(0).get(5), 200, 200));
		img.setBorder(new LineBorder(Color.BLACK));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public Company(String cno, Admin admin) {
		this(cno);
		this.admin = admin;
		btn.setText("수정");
		txt[1].setEnabled(false);
		txt[2].setEnabled(false);
		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var jfc = new JFileChooser("./datafiles/기업");
				if (jfc.showOpenDialog(Company.this) == JFileChooser.APPROVE_OPTION) {
					f = jfc.getSelectedFile();
					img.setIcon(img("기업/" + f.getName(), 200, 200));
				}
			}
		});
	}
}

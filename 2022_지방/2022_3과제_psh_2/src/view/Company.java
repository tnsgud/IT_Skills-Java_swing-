package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Company extends BaseFrame {
	JLabel img;
	File f;
	String cno;
	JButton btn;
	JTextField txt[] = new JTextField[5];
	Admin admin;
	ArrayList<Object> rs;

	public Company(String cno) {
		super("기업정보상세", 300, 500);
		this.cno = cno;
		ui();

		setVisible(true);
	}

	private void ui() {
		rs = rs("select * from company where c_no=?", cno).get(0);

		add(sz(img = new JLabel(img("기업/" + rs.get(1) + "1.jpg", 300, 200)), 300, 200), "North");
		add(c = new JPanel(new GridLayout(0, 1, 10, 10)));
		add(btn = btn("닫기", a -> {
			if (a.getActionCommand().equals("닫기")) {
				dispose();
			} else {
				for (var t : txt) {
					if (t.getText().isEmpty()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}

				iMsg("수정이 완료되엇습니다.");
				try {
					execute("update company set c_img=?, c_ceo=?, c_address=? where = c_no=?", new FileInputStream(f),
							txt[1].getText(), txt[2].getText(), cno);
					Files.copy(f.toPath(), new File("./datafiles/기업/" + txt + "1.jpg").toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				admin.load();
				dispose();
			}
		}), "South");

		var cap = "기업명,대표자,주소,직종,직원수".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(1));
			p.add(sz(lbl(cap[i], 2), 80, 20));
			p.add(txt[i] = new JTextField(15));
			txt[i].setText(i == 3 ? category[toInt(rs.get(i + 1))] : rs.get(i + 1) + "");
			txt[i].setEnabled(false);
			c.add(p);
		}

		img.setBorder(new LineBorder(Color.black));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public Company(String cno, Admin admin) {
		this(cno);
		this.admin = admin;

		setTitle("기업정보수정");
		f = new File("./datafiles/기업/" + rs.get(1) + "1.jpg");
		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var jfc = new JFileChooser("./datafiles/회원사진");
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					f = jfc.getSelectedFile();
					img.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath())
							.getScaledInstance(300, 200, Image.SCALE_SMOOTH)));
				}
			}
		});

		txt[1].setEnabled(false);
		txt[2].setEnabled(false);
	}

	public static void main(String[] args) {
		new Company("14");
	}
}

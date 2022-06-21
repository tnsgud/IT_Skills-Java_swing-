package view;

import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AdminPage extends BasePage {
	public AdminPage() {
		setLayout(new GridBagLayout());

		add(c = new JPanel(new GridLayout(1, 0, 5, 5)));

		for (var cap : "도로 수정,건물 연결 수정".split(",")) {
			c.add(sz(btn(cap, a -> {
				if (cap.equals("도루 수정")) {
					new MapEdit().setVisible(true);
				} else {
					var input = JOptionPane.showInputDialog(null, "건물 번호 또는 건물의 이름을 정확히 입력하세요.");
					if (input.isEmpty()) {
						eMsg("존재하지 않습니다.");
					}
					var no = getOne("select no from building where name =? or no=?", input, input);

					new MapEdit(no).setVisible(true);
				}
			}), 150, 150));
		}
	}

	public static void main(String[] args) {
		mf.swap(new AdminPage());
		mf.setVisible(true);
	}
}

package view;

import java.awt.Color;

import javax.swing.JTabbedPane;

public class AdminMain extends BaseFrame {
	JTabbedPane tab;

	public AdminMain() {
		super(1200, 600);

		ui();

		setVisible(true);
	}

	private void ui() {
		add(tab = new JTabbedPane(JTabbedPane.LEFT));

		tab.add("사용자 관리", new UserManage());
		tab.add("추천 여행지 관리", new Recommend());
		tab.add("일정 관리", new Schedule());
		tab.add("예매 관리", new ReserveManage());
		tab.add("테마", null);
		tab.add("로그아웃", null);

		setTheme(this);
		tab.setBackgroundAt(4, theme ? Color.DARK_GRAY : Color.white);
		tab.setForegroundAt(4, theme ? Color.white : Color.DARK_GRAY);

		tab.addChangeListener(a -> {
			if (tab.getSelectedIndex() == 4) {
				theme = !theme;
				setTheme(this);

				var source = (JTabbedPane) a.getSource();

				source.setBackground(BaseFrame.theme ? Color.darkGray : Color.white);
				source.setForeground(BaseFrame.theme ? Color.white : Color.BLACK);

				repaint();
				revalidate();

				for (int i = 0; i < tab.getTabCount(); i++) {
					if (i != 4) {
						tab.setBackground(theme ? Color.white : Color.DARK_GRAY);
						tab.setForeground(theme ? Color.DARK_GRAY : Color.white);
					}
				}

				tab.setBackgroundAt(4, theme ? Color.DARK_GRAY : Color.white);
				tab.setForegroundAt(4, theme ? Color.white : Color.DARK_GRAY);
				tab.setSelectedIndex(0);
			}else if(tab.getSelectedIndex() == 5) {
				dispose();
			}
		});
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new AdminMain();
	}
}

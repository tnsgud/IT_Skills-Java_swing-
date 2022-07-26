package view;

import javax.swing.JLabel;

public class GameInfoPage extends BasePage {
	JLabel lblGenre;
	
	public GameInfoPage() {
		super("게임정보");
	}
	
	public GameInfoPage(int gNo) {
		this();
	}
}

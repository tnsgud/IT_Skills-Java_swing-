package view;

import java.util.ArrayList;

public class EditTheater extends BaseDialog {
	TheaterManageFrame frame;
	ArrayList<Object> rs;

	public EditTheater(TheaterManageFrame frame, ArrayList<Object> rs) {
		super("극장 편집", 500, 500);

		this.frame = frame;
		this.rs = rs;

		setVisible(true);
	}
}

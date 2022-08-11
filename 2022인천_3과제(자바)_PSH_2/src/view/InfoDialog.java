package view;

import java.util.ArrayList;

public class InfoDialog extends BaseDialog {
	ArrayList<Object> info;

	public InfoDialog(ArrayList<Object> info) {
		super(500, 500);
		this.info = info;
	}
}

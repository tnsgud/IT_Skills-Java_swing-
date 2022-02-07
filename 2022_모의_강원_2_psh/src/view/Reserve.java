package view;

import java.util.ArrayList;

import tool.Tool;

public class Reserve extends BaseDialog implements Tool {

	ArrayList<Integer> sno = new ArrayList<Integer>();

	public Reserve(ArrayList<Integer> sno) {
		super("", 100, 100);
		this.sno = sno;

		setVisible(true);
	}
}

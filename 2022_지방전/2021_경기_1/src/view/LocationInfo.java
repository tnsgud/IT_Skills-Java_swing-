package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

public class LocationInfo extends BaseFrame {
	JTabbedPane tab = new JTabbedPane(2);
	String fl[] = "1F,2F,3F,4F,외부".split(",");
	JLabel fl_lbl[] = new JLabel[fl.length];
	JComboBox<String> com = new JComboBox<String>();
	HashMap<String, ArrayList<String>> ride = new HashMap<String, ArrayList<String>>();
	IconLabel icon[][];
	IconLabel prev;
	
	int point[][][];
	Timer blink;
	String chr[] = "로티,로티".split(",");
	Random rnd = new Random();
	
	public LocationInfo() {
		super("위치정보", 500, 500);
		
		data();
		ui();
		event();
	}

	private void event() {
		// TODO Auto-generated method stub
		
	}

	private void ui() {
		// TODO Auto-generated method stub
		
	}

	private void data() {
		// TODO Auto-generated method stub
		
	}

}

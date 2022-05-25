package view;

public class Reserve extends BaseFrame {
	public Reserve() {
		super("예약", 500, 500);
		
		add(lblH("이것은 예약이다", 0, 1, 35));
		
		setVisible(true);
	}

	public static void main(String[] args) {
		new Reserve();
	}
}

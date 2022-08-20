package view;

import java.util.ArrayList;

public class MovieInfoDialog extends BaseDialog {
	public MovieInfoDialog(ArrayList<Object> movie) {
		super(movie.get(1).toString(), 500, 500);
	}
}

package infoViewer.view;

import java.util.Observable;

import javax.swing.JPanel;

public class Observer extends JPanel implements java.util.Observer {

	@Override
	public void update(Observable o, Object arg) {

	}
	
	protected String getFileName(String path) {

		char[] charPath = path.toCharArray();
		int i = 0, start = 0, end = path.length() - 4;

		while (i < path.length()) {

			if ((int) charPath[i] == 92)
				start = i;
			i++;
		}

		return path.substring(start + 1, end);
	}
}
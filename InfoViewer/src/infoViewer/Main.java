package infoViewer;

import javax.swing.SwingUtilities;

import infoViewer.view.AppGUI;

public class Main {

	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				AppGUI gui = AppGUI.getInstance();
			}
		});
	}
}

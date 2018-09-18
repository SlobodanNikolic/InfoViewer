package infoViewer.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.TabableView;

import infoViewer.view.AppGUI;
import infoViewer.view.database.DBFileView;
import infoViewer.view.files.FileView;

public class CloseAll extends AbstractAction {

	private ArrayList<FileView> fileViews, toDelete;
	private ArrayList<DBFileView> dbFileViews;

	public CloseAll() {

		putValue(NAME, "Close all");
		putValue(SMALL_ICON, new ImageIcon("src/resources/icons/Close all.png"));
		putValue(SHORT_DESCRIPTION, "Close all files.");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if (AppGUI.getInstance().getTabbedPane().getComponentCount() == 0) {

			JOptionPane.showMessageDialog(null, "No files to close!");

		} else {

			((Close) ActionManager.getInstance().getAction("close")).setCloseAll(true);

			closeDBFileViews();
			closeFileViews();

			((Close) ActionManager.getInstance().getAction("close")).setCloseAll(false);
		}
	}

	public void closeFileViews() {

		// FILE_VIEWS

		int index = 0;

		toDelete = new ArrayList();
		fileViews = AppGUI.getInstance().getFileViews();
		int size = AppGUI.getInstance().getFileViews().size();

		for (int i = 0; i < fileViews.size(); i++) {

			FileView fileView = fileViews.get(i);

			fileView.setCanPassOn(true);

			System.out.println("*CloseAll: actionPerformed(): (fileView) " + fileView.getName());
			AppGUI.getInstance().setCurrentFileView(fileView);
			System.out.println("Close ALL: Ulazi u close...");

			try {

				ActionManager.getInstance().getAction("close").actionPerformed(null);

				System.out.println("USAO U PASS ON TRIAL!");

				if (fileViews.get(index + 1).isNew()) {
					if (fileViews.get(index).getCanPassOn()) {

						System.out.println("CLOSE_ALL: Path trenutnog fajla: " + fileViews.get(index).getPath());
						fileView.passOnPath(fileViews.get(index + 1), fileView.getPath());
					}

					System.out.println("PROSAO PASS ON TRIAL!");
				}

			} catch (Exception e) {
			}
			index++;
		}

		System.out.println("**CloseAll: actionPerformed(): (toDelete) " + toDelete);
		fileViews.removeAll(toDelete);
		toDelete.clear();

		System.out.println("***CloseAll: actionPerformed(): (fileViews) " + AppGUI.getInstance().getFileViews());
		if (!fileViews.isEmpty() && AppGUI.getInstance().getTabbedPane().getSelectedComponent() instanceof FileView)
			AppGUI.getInstance()
					.setCurrentFileView((FileView) AppGUI.getInstance().getTabbedPane().getSelectedComponent());

		JTextArea[] dateSize = AppGUI.getInstance().getDateSize();
		for (JTextArea textArea : dateSize)
			textArea.setForeground(Color.gray);

		dateSize[0].setText("Date of creation");
		dateSize[1].setText("Size of the file");

		if (fileViews.size() == 0)
			AppGUI.getInstance().setInfoFieldsDefault();
	}

	public void closeDBFileViews() {

		// DB_FILE_VIEWS

		dbFileViews = AppGUI.getInstance().getDBFileViews();

		for (DBFileView view : dbFileViews)
			AppGUI.getInstance().getTabbedPane().remove(view);

		dbFileViews.clear();
	}

	public ArrayList<FileView> getToDelete() {

		return toDelete;
	}
}

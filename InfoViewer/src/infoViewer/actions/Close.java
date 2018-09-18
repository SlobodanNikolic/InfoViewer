package infoViewer.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import infoViewer.view.AppGUI;
import infoViewer.view.Observer;
import infoViewer.view.database.DBFileView;
import infoViewer.view.files.FileView;

public class Close extends AbstractAction {

	private FileView fileView;
	private DBFileView dbFileView;
	private boolean closeAll = false;

	public Close() {

		putValue(NAME, "Close");
		putValue(SMALL_ICON, new ImageIcon("src/resources/icons/Close.png"));
		putValue(SHORT_DESCRIPTION, "Close file.");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		int optionPane = 0;

		if (AppGUI.getInstance().getTabbedPane().getSelectedComponent() instanceof FileView)
			fileView = (FileView) AppGUI.getInstance().getTabbedPane().getSelectedComponent();
		else if (AppGUI.getInstance().getTabbedPane().getSelectedComponent() instanceof DBFileView)
			dbFileView = (DBFileView) AppGUI.getInstance().getTabbedPane().getSelectedComponent();
		else
			System.out.println("Close - tabbedPane.getSelected je null!!!");

		if (fileView != null) {

			System.out.println("_Close: (THE FILE IS NOT NULL!)");

			if (((FileView) fileView).isEdited() && !fileView.getSaveFailed()) {

				System.out.println("_Close: (hasTheFileBeenEdited): " + ((FileView) fileView).isEdited());

				if (((FileView) fileView).isNew()) {

					System.out.println("_Close: (isTheFileNew): " + ((FileView) fileView).isNew());

					String[] options = { "Yes", "No", "Cancel" };

					int answer = JOptionPane.showOptionDialog(null,
							"Would you like to save the new file (" + fileView.getName() + ")?", "Message", 0,
							JOptionPane.QUESTION_MESSAGE, null, options, 1);

					if (answer == 0) {

						try {

							AppGUI.getInstance().setCurrentFileView((FileView) fileView);
							ActionManager.getInstance().getAction("save").actionPerformed(null);

						} catch (Exception e) {

							System.out.println("Close: INICIJALNI SAVE NIJE USPIO! (ide se na backup)");
							JOptionPane.showMessageDialog(null, "The (tree)path has not been specified.", "Warning",
									JOptionPane.QUESTION_MESSAGE);
							((FileView) fileView).setCanClose(false);
							((FileView) fileView).setCanPassOn(false);
							return;
						}
					}

					else if (answer == 2)
						return;

					if (((FileView) fileView).getCanClose()) {
						AppGUI.getInstance().getTabbedPane().remove(fileView);
						if (closeAll)
							((CloseAll) ActionManager.getInstance().getAction("closeAll")).getToDelete()
									.add((FileView) fileView);
					}
					// Ako fajl nije nov...
				} else {

					optionPane = JOptionPane.showConfirmDialog(AppGUI.getInstance(),
							"Da li ste imali izmene koje biste zeleli da sacuvate? (" + fileView.getName() + ".txt)");

					if (optionPane == JOptionPane.NO_OPTION) {

						AppGUI.getInstance().getTabbedPane()
								.remove(AppGUI.getInstance().getTabbedPane().getSelectedComponent());
						// AppGUI.getInstance().getFileViews().remove(fileView);
						if (closeAll)
							((CloseAll) ActionManager.getInstance().getAction("closeAll")).getToDelete()
									.add((FileView) fileView);

					} else if (optionPane == JOptionPane.YES_OPTION) {

						((FileView) fileView).saveFile(((FileView) fileView).getTextArea().getText());
						AppGUI.getInstance().getTabbedPane().remove(fileView);

						if (closeAll)
							((CloseAll) ActionManager.getInstance().getAction("closeAll")).getToDelete()
									.add((FileView) fileView);

					} else {

						System.out.println("*Close - actionPefromed: (fileView_CLOSE) " + fileView.getName());
					}
				}
				// Ako fajl nije editovan...
			} else {

				AppGUI.getInstance().getTabbedPane().remove(fileView);
				if (closeAll)
					((CloseAll) ActionManager.getInstance().getAction("closeAll")).getToDelete()
							.add((FileView) fileView);
			}
			if (!closeAll)
				AppGUI.getInstance().getFileViews().remove(fileView);

			if (AppGUI.getInstance().getFileViews().size() == 0)
				AppGUI.getInstance().setInfoFieldsDefault();
			
			fileView.setSaveFailed(false);
		}

		if (dbFileView != null) {

			AppGUI.getInstance().getTabbedPane().remove(dbFileView);
			AppGUI.getInstance().getDBFileViews().remove(dbFileView);
		}

		if (fileView == null && dbFileView == null)
			JOptionPane.showMessageDialog(null, "No files to close.");
	}

	public void setCloseAll(boolean closeAll) {

		this.closeAll = closeAll;
	}

	public boolean getCloseAll() {

		return closeAll;
	}
}

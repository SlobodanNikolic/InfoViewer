package infoViewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import infoViewer.view.AppGUI;
import infoViewer.view.files.FileView;

public class Save extends AbstractAction {

	private static String backupPath;

	private boolean cantSaveThere = true;

	public Save() {

		putValue(NAME, "Save");
		putValue(SMALL_ICON, new ImageIcon("src/resources/icons/Save.png"));
		putValue(SHORT_DESCRIPTION, "Save file.");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
	}

	public void actionPerformed(ActionEvent arg0) {

		FileView fileView = (FileView) AppGUI.getInstance().getCurrentFileView();

		if (fileView != null) {
			if (fileView.isEdited()) {

				if (!fileView.checkPath()) {

					if (AppGUI.getInstance().getPathField().getText() != null) {
						if (!AppGUI.getInstance().getPathField().getText().equals("Path of the selected file")
								&& !AppGUI.getInstance().getPathField().getText().trim().equals("")) {
							if (JOptionPane.showConfirmDialog(null, "Would you like to save to {"
									+ AppGUI.getInstance().getPathField().getText() + "}") == 0) {

								String newPath = AppGUI.getInstance().getPathField().getText() + "\\"
										+ fileView.getName() + ".txt";

								fileView.setPath(newPath);

								if (fileView.checkPath()) {

									fileView.saveFile(fileView.getTextArea().getText());
									return;
								}

							} else {
								JOptionPane.showMessageDialog(null, "The (tree)path has not been specified.", "Warning",
										JOptionPane.ERROR_MESSAGE);
								cantSaveThere = false;
							}
						} else {
							JOptionPane.showMessageDialog(null, "The (tree)path has not been specified.", "Warning",
									JOptionPane.ERROR_MESSAGE);
							cantSaveThere = false;
						}
					} else {
						JOptionPane.showMessageDialog(null, "The (tree)path has not been specified.", "Warning",
								JOptionPane.ERROR_MESSAGE);
						cantSaveThere = false;
					}

					System.out.println("Save: Prvi path propao, ide se na backup!");

					if (backupPath == null) {

						if (cantSaveThere)
							JOptionPane.showMessageDialog(null, "You can't save there!");
						return;
					}

					fileView.setPath(backupPath);

					if (fileView.checkPath()) {

						fileView.saveFile(fileView.getTextArea().getText());
						System.out.println("SAVE (CheckPath after save): " + fileView.checkPath());
					}

					else {

						JOptionPane.showMessageDialog(null, "The (tree)path has not been specified.", "Warning",
								JOptionPane.ERROR_MESSAGE);
						fileView.setCanPassOn(false);
						fileView.setCanClose(false);
						fileView.setSaveFailed(true);
					}
				}

				else {

					System.out.println("Save: Prvi path nije propao, glasi: " + fileView.getPath());
					fileView.saveFile(fileView.getTextArea().getText());
					System.out.println("SAVE (CheckPath after save): " + fileView.checkPath());
				}
			} else {
				JOptionPane.showMessageDialog(null, "There are no changes to save.");
			}
		} else {
			JOptionPane.showMessageDialog(null, "No files to save.");
		}

		cantSaveThere = true;
	}

	public static String getBackupPath() {

		return backupPath;
	}

	public static void setBackupPath(String path) {

		backupPath = path;
	}
}

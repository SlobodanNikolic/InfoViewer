package infoViewer.view.files;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import infoViewer.actions.ActionManager;
import infoViewer.actions.Save;
import infoViewer.model.file.FileModel;
import infoViewer.view.AppGUI;
import infoViewer.view.Observer;
import net.miginfocom.swing.MigLayout;

public class FileView extends Observer {

	private FileModel fileModel;
	private String path = null;
	private JTextArea textArea;

	private boolean isEdited, isNew;
	private boolean canClose = true;
	private boolean canPassOn = false;
	private boolean saveFailed = false;

	private TreePath treePath;
	private static TreePath treePathBackup;

	public FileView() {

		fileModel = new FileModel();
		fileModel.addObserver(this);

		setLayout(new BorderLayout());

		textArea = new JTextArea();
		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {

				isEdited = true;
				// setCanClose(false);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				isEdited = true;
				// setCanClose(false);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				// ... cu ne radi za plain text...
			}
		});

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createTitledBorder(""));
		add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		for (int i = 0; i < 3; i++)
			panel.add(new JLabel(""), "wrap");
		add(panel, BorderLayout.SOUTH);
	}

	public JTextArea getTextArea() {

		return textArea;
	}

	public FileModel getModel() {

		return fileModel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		textArea.setText(fileModel.getText());
		textArea.setCaretPosition(0);
	}

	public void saveFile(String content) {

		try {

			if (isNew == true) {

				treePath = AppGUI.getInstance().getFileTree().getSelectionModel().getLeadSelectionPath();

				if (treePath == null)
					treePath = treePathBackup;
				else
					setTreePathBackup(treePath);

				if (!(new File(treePath.getLastPathComponent().toString())).isDirectory())
					treePath = treePath.getParentPath();

				String[] itemsContained = new File(treePath.getLastPathComponent().toString()).list();

				for (int i = 0; i < itemsContained.length; i++) {

					if ((getName() + ".txt").equals(itemsContained[i])) {

						int choice = JOptionPane.showConfirmDialog(null, "Do you want to overwrite?");

						if (choice == 1) {

							String newName = JOptionPane.showInputDialog(null, "Please enter the new name.");
							String noSpaces = newName;

							if (!(newName == null))
								newName = newName.replace(" ", "");

							while (newName == null || newName.equals("")) {
								newName = JOptionPane.showInputDialog(null, "Please enter the new name.", "New name");
								noSpaces = newName;
								if (!(newName == null))
									newName = newName.replace(" ", "");
							}

							int index = AppGUI.getInstance().getTabbedPane().indexOfComponent(this);
							AppGUI.getInstance().getTabbedPane().remove(this);
							setName(noSpaces);
							// AppGUI.getInstance().setCurrentFileView(this);
							AppGUI.getInstance().getFileViews().add(this);
							AppGUI.getInstance().getTabbedPane().add(this, index);
							AppGUI.getInstance().getTabbedPane().setSelectedComponent(this);
						}

						else if (choice == 0) {

						} else {

							canClose = false;
							return;
						}
					}
				}

				File fileNew = new File(path);
				fileNew.setWritable(true);

				try {
					fileNew.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				setCanClose(true);

				/*
				 * ROW - Indeks poslednjeg selektovanog foldera (kao cvora) u
				 * stablu
				 */
				// int row =
				// AppGUI.getInstance().getTree().getRowForPath(treePath);

				DefaultMutableTreeNode root = new DefaultMutableTreeNode();
				DefaultTreeModel treeModel = new DefaultTreeModel(root);
				FileSystemView fileSystemView = FileSystemView.getFileSystemView();

				// Grananje

				File[] roots = fileSystemView.getRoots();

				for (File fileSystemRoot : roots) {

					DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
					root.add(node);
					File[] files = fileSystemView.getFiles(fileSystemRoot, true);

					for (File file : files)
						if (file.isDirectory())
							node.add(new DefaultMutableTreeNode(file));
				}

				AppGUI.getInstance().getFileTree().setModel(treeModel);
				AppGUI.getInstance().getFileTree().expandRow(0);
			}

			if (path != null && !path.equals("")) {

				System.out.println("FILE_VIEW - SaveFile: Path to RAF: " + path);

				RandomAccessFile raf = new RandomAccessFile(path, "rw");
				raf.writeBytes(content);
				raf.setLength(content.length());
				raf.close();

				JOptionPane.showMessageDialog(null, "The file has been saved! ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isEdited() {

		return isEdited;
	}

	public void setIsEdited(boolean isEdited) {

		this.isEdited = isEdited;
	}

	public boolean isNew() {

		return isNew;
	}

	public void setIsNew(boolean isNew) {

		this.isNew = isNew;
	}

	public void setCanClose(boolean canClose) {

		this.canClose = canClose;
	}

	public boolean getCanClose() {

		return canClose;
	}

	public void setPath(String path) {

		if (path == null) {

			JOptionPane.showMessageDialog(null, "The selected treepath is null.");
		}
		this.path = path;
		fileModel.setName(getFileName(path));
		setName(getFileName(path));
	}

	public void passOnPath(Observer fileView, String path) {

		System.out.println("OLD PATH: " + path); // 4 = .txt
		String newPath = path.substring(0, path.length() - getName().length() - 4);
		newPath += fileView.getName() + ".txt";
		System.out.println("NEW PATH: " + newPath);
		((FileView) fileView).setPath(newPath);
	}

	public String getPath() {

		return path;
	}

	@Override
	public String toString() {

		return getModel().getName();
	}

	public boolean checkPath() {

		try {

			new File(path);
			canPassOn = true;

			return true;

		} catch (Exception e) {

			System.out.println("Usao u catch!");

			canPassOn = false;
			return false;
		}
	}

	public boolean getCanPassOn() {

		return canPassOn;
	}

	public void setCanPassOn(boolean canNotCAN) {

		canClose = canNotCAN;
	}

	public void setTreePathBackup(TreePath backup) {

		treePathBackup = backup;
	}

	public boolean getSaveFailed() {

		return saveFailed;
	}

	public void setSaveFailed(boolean saveFailed) {

		this.saveFailed = saveFailed;
	}
}

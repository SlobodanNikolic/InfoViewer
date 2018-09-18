package infoViewer.actions;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import infoViewer.view.AppGUI;

public class About extends AbstractAction {

	public About() {

		putValue(NAME, "About");
		putValue(SMALL_ICON, new ImageIcon("src/resources/icons/About.png"));
		putValue(SHORT_DESCRIPTION, "About team");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
	}

	public void actionPerformed(ActionEvent arg0) {

		JDialog dialog = new JDialog(AppGUI.getInstance(), "About");
		ImageIcon image1 = new ImageIcon("src/resources/icons/204.3a.jpg");
		ImageIcon image2 = new ImageIcon("src/resources/icons/204.3b.jpg");
		JPanel labels = new JPanel();

		JLabel label1 = new JLabel(
				"<html>&nbsp;&nbsp;&nbsp;Uros Tanaskovic<br><br>&nbsp;&nbsp;&nbsp;RN 96/15<br><br>&nbsp;&nbsp;&nbsp;utanaskovic14@raf.edu.rs<br><br></html>",
				image1, JLabel.CENTER);

		label1.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		JLabel label2 = new JLabel(
				"<html>&nbsp;&nbsp;&nbsp;Stefan Toljic<br><br>&nbsp;&nbsp;&nbsp;RN 92/15<br><br>&nbsp;&nbsp;&nbsp;stoljic14@raf.edu.rs<br><br></html>",
				image2, JLabel.CENTER);

		labels.setLayout(new GridLayout());
		labels.add(label1);
		labels.add(label2);

		dialog.setResizable(false);
		dialog.setSize(AppGUI.WIDTH * 3 / 5, AppGUI.HEIGHT / 3);
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(AppGUI.getInstance());
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.add(labels, BorderLayout.CENTER);
		dialog.validate();
	}
}

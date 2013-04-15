package org.kenneh.scripts.hydrachopper;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.powerbot.game.api.Manifest;

public class HCGui {
	
	private JFrame guiFrame;
	private JPanel guiPanel;
	private JButton startButton;
	private JComboBox<Tree> mySelection;
	private DefaultComboBoxModel<Tree> myModel;
	private Image[] treePictures = new Image[5];
	private JLabel imageLabel;
	private Tree myTree;
	
	public void show(boolean t) {
		guiFrame.setVisible(t);
	}
	
	public Tree getMySelection() {
		return myTree;
	}
	
	public JFrame getFrame() {
		return guiFrame;
	}
	
	public void initImages() throws MalformedURLException, IOException {
		treePictures[0] = ImageIO.read(new URL("http://puu.sh/2A4xz"));
		treePictures[1] = ImageIO.read(new URL("http://puu.sh/2A4AN"));
	}

	public void initGUI() {
		try {
			initImages();
		} catch (IOException e) {
			e.printStackTrace();
		}
		guiFrame = new JFrame(getClass().getAnnotation(Manifest.class).name());
		guiPanel = new JPanel();
		guiPanel.setLayout(new BorderLayout());
		myModel = new DefaultComboBoxModel<Tree>();
		mySelection = new JComboBox<Tree>(myModel);
		for(Tree t : Tree.values()) {
			myModel.addElement(t);
		}
		startButton = new JButton("Start Script");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				myTree = (Tree) mySelection.getSelectedItem();
				guiFrame.setVisible(false);
			}
			
		});
		imageLabel = new JLabel(new ImageIcon(treePictures[mySelection.getSelectedIndex()].getScaledInstance(200, 200, 5)));
		mySelection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Updating");
				imageLabel.setIcon(new ImageIcon(treePictures[mySelection.getSelectedIndex()].getScaledInstance(200, 200, 5)));
				imageLabel.repaint();
			}
			
		});
		guiPanel.add(startButton, BorderLayout.SOUTH);
		guiPanel.add(mySelection, BorderLayout.NORTH);
		guiPanel.add(imageLabel, BorderLayout.CENTER);
		guiFrame.add(guiPanel);
		guiFrame.setLocationRelativeTo(null);
		guiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		guiFrame.pack();
		guiFrame.setVisible(true);
	}
	
}

package org.kenneh.core.graphics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Logger {

	public static void main(String[] args) {
		Logger logger = new Logger();
		logger.display();
		log("started");
	}

	public void display() {
		if(frame != null) {
			frame.setVisible(true);
		}
		log("GUI Initiated!");
	}
	
	public Logger() {
		init();
	}

	public void dispose() {
		if(frame != null && frame.isVisible()) {
			frame.dispose();
		}
	}

	public void setTitle(String title) {
		if(frame != null) {
			frame.setTitle(title);
		}
	}

	public static String getTime(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static void log() {
		textArea1.append(System.getProperty("line.separator"));
		textArea1.scrollRectToVisible(new Rectangle(0, textArea1.getHeight() - 2, 1, 1));
	}

	public static void log(String o) {
		try {
			textArea1.append("[" + getTime("hh:mm:ss z") + "] " + o + System.getProperty("line.separator"));
			textArea1.scrollRectToVisible(new Rectangle(0, textArea1.getHeight() - 2, 1, 1));
			System.out.println(o);
		} catch(Exception ignored) {}
	}

	public void init() {

		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Kenneth LaCombe
		frame = new JFrame("Console");
		scrollPane1 = new JScrollPane();
		textArea1 = new JTextArea();

		//======== this ========
		Container contentPane = frame.getContentPane();

		//======== scrollPane1 ========
		{

			//---- textArea1 ----
			textArea1.setFont(new Font("Calibri", Font.PLAIN, 14));
			textArea1.setWrapStyleWord(true);
			textArea1.setBackground(Color.black);
			textArea1.setForeground(new Color(51, 255, 0));
			textArea1.setEditable(false);
			textArea1.setLineWrap(true);
			scrollPane1.setViewportView(textArea1);
		}


		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
				.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
				);
		contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
				.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
				);
		frame.setSize(780, 245);
		frame.setLocationRelativeTo(frame.getOwner());

		// JFormDesigner - End of component initialization  //GEN-END:initComponents


	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Kenneth LaCombe
	private JScrollPane scrollPane1;
	private static JTextArea textArea1;
	private JFrame frame;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

}

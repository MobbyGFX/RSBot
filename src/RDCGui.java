import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.TitledBorder;


public class RDCGui {
	
	private JFrame frame;
	private JComboBox<Food> selection;
	private JButton start;
	private JLabel logo;
	private JPasswordField pf;
	
	private JCheckBox urns;

	public void show() {
		frame.setVisible(true);
	}
	
	public void startActionListener(final ActionEvent e) {
		RougesDenCooker.useUrns = urns.isSelected();
		System.out.println("Using urns: "+ RougesDenCooker.useUrns);
		
		RougesDenCooker.food = (Food) selection.getSelectedItem();
		System.out.println("Cooking: " + RougesDenCooker.food);
		
		RougesDenCooker.pin = pf.getPassword();

		RougesDenCooker.start = true;
		
		frame.setVisible(false);
	}
	
	public RDCGui() {
		final JPanel center = new JPanel();
		center.setLayout(new GridBagLayout());
		
		final JPanel pinPanel = new JPanel();
		pinPanel.setLayout(new BorderLayout());
		pinPanel.setBorder(new TitledBorder("Bank pin"));
		
		frame = new JFrame("Kenneh's Rogues Den Cooker");
		frame.setMinimumSize(new Dimension(400, 250));
		frame.getContentPane().setLayout(new BorderLayout());

		logo = new JLabel("Rogues Den Cooker");
		logo.setHorizontalAlignment(JLabel.CENTER);
		logo.setFont(new Font("Calibri", Font.BOLD, 16));

		final DefaultComboBoxModel<Food> model = new DefaultComboBoxModel<Food>();
		for(Food f : Food.values()) {
			model.addElement(f);
		}
		
		selection = new JComboBox<Food>(model);
		selection.setFont(new Font("Calibri", Font.PLAIN, 13));
		
		urns = new JCheckBox("Use cooking urns");
		urns.setFont(new Font("Calibri", Font.PLAIN, 14));
		
		start = new JButton("Start Cooking");
		start.setFont(new Font("Calibri", Font.PLAIN, 15));
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				startActionListener(arg0);
			}
			
		});
		
		pf = new JPasswordField();

		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0; c.gridy = 0;
		center.add(selection, c);
		c.gridx = 1; c.gridy = 0;
		center.add(urns, c);
		c.gridx = 0; c.gridy = 1;
		center.add(pinPanel, c);
		pinPanel.add(new JLabel("<html>This is not required, it's here <br> simply due to the fact that <br> the default one doesn't work.</html>"), BorderLayout.NORTH);
		pinPanel.add(pf, BorderLayout.SOUTH);
		
		
		frame.getContentPane().add(logo, BorderLayout.NORTH);
		frame.getContentPane().add(center, BorderLayout.CENTER);
		frame.getContentPane().add(start, BorderLayout.SOUTH);
		
		frame.pack();
	}
	
	public static void main(String[] args) {
		RDCGui gui = new RDCGui();
		gui.show();
	}

}

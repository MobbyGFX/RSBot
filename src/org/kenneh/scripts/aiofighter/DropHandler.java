package org.kenneh.scripts.aiofighter;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.kenneh.scripts.aiofighter.nodes.PriceChecker;


@SuppressWarnings("serial")
public class DropHandler extends JFrame {

	public static void main(String[] args) {
		DropHandler dh = new DropHandler();
		dh.setVisible(true);
	}

	static String[] names = {"Name", "Amount", "Value", "Total"};

	static DefaultTableModel model2 = new DefaultTableModel(null, names);
	
	public void init() {
		setLayout(new BorderLayout());
		setTitle("Drop log");
		scrollPane1 = new JScrollPane();
		table1 = new JTable(model2);
		scrollPane1.setViewportView(table1);
		add(scrollPane1, BorderLayout.CENTER);;
		setSize(450, 300);
	}

	public static void populate(int id, int amount) {
		String name = PriceChecker.namelist.get(id);
		for(int i = 0; i < model2.getRowCount(); i++) {
			if(name.equals(model2.getValueAt(i, 0))) {
				int currAmount = Integer.parseInt(String.valueOf(model2.getValueAt(i,1)));
				int newAmount = currAmount + (int)amount;
				model2.setValueAt(newAmount, i, 1);
				int newTotal = newAmount * PriceChecker.lootlist.get(id);
				model2.setValueAt(newTotal, i, 3);
				return;
			}
		}
		model2.addRow(new String[] {
				PriceChecker.namelist.get(id),
				String.valueOf(amount),
				String.valueOf(PriceChecker.lootlist.get(id)),
				String.valueOf(PriceChecker.lootlist.get(id) * (int)amount)
		});
	}

	public DropHandler() {
	}

	private JScrollPane scrollPane1;
	private JTable table1;
}

package org.kenneh.scripts.aiofighter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.kenneh.core.api.Misc;
import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.constants.Constants;
import org.kenneh.scripts.aiofighter.nodes.Alch;
import org.kenneh.scripts.aiofighter.nodes.AttackOneOf;
import org.kenneh.scripts.aiofighter.nodes.BarrowsCheck;
import org.kenneh.scripts.aiofighter.nodes.BuryBones;
import org.kenneh.scripts.aiofighter.nodes.EatFood;
import org.kenneh.scripts.aiofighter.nodes.EnableRun;
import org.kenneh.scripts.aiofighter.nodes.EndOnSlayerTask;
import org.kenneh.scripts.aiofighter.nodes.Expandbar;
import org.kenneh.scripts.aiofighter.nodes.FightEntity;
import org.kenneh.scripts.aiofighter.nodes.GemBag;
import org.kenneh.scripts.aiofighter.nodes.LootHandler;
import org.kenneh.scripts.aiofighter.nodes.Potions;
import org.kenneh.scripts.aiofighter.nodes.RejuvFailsafe;
import org.kenneh.scripts.aiofighter.nodes.SpinTicket;
import org.kenneh.scripts.aiofighter.nodes.SummoningHandler;
import org.kenneh.scripts.aiofighter.nodes.Teleport;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.input.Mouse.Speed;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.NPC;



/**
 * @author Kenneth LaCombe
 */
@SuppressWarnings("serial")
public class FighterGUI extends JPanel {

	public void populateActionEvent(ActionEvent e) {
		model.clear();
		for (String i : NPC_LIST()) {
			model.addElement(i);
		}
		list1.repaint();
	}

	public int getFoodId(String name) {
		int[] ids = {315, 333, 329, 379, 373, 7946, 385, 15266, 15272};
		String[] names = {"Shrimp", "Trout", "Salmon", "Tuna", "Lobster", "Monkfish", "Shark", "Cavefish", "Rocktail"};
		for(int i = 0; i < names.length; i++) {
			if(names[i].equals(name)) {
				return ids[i];
			}
		}
		return -1;
	}

	public FighterGUI() {

		initComponents();
		try {
			load();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(JCheckBox box : checkBox) {
			box.repaint();
		}
	}

	public void addToArraylist() {
		if(checkBox[0].isSelected()) Settings.setLootIds(12158);
		if(checkBox[1].isSelected()) Settings.setLootIds(12159);
		if(checkBox[2].isSelected()) Settings.setLootIds(12160);
		if(checkBox[3].isSelected()) Settings.setLootIds(12163);
		if(checkBox[4].isSelected()) Settings.setLootIds(452);
		if(checkBox[5].isSelected()) Settings.setLootIds(574);
		if(checkBox[6].isSelected()) Settings.setLootIds(9342);
		if(checkBox[7].isSelected()) Settings.setLootIds(5289);
		if(checkBox[8].isSelected()) Settings.setLootIds(570);
		if(checkBox[9].isSelected()) Settings.setLootIds(1392);
		if(checkBox[10].isSelected()) Settings.setLootIds(3001);
		if(checkBox[11].isSelected()) Settings.setLootIds(2364);
		if(checkBox[12].isSelected()) Settings.setLootIds(384);
		if(checkBox[13].isSelected()) Settings.setLootIds(1215);
		if(checkBox[14].isSelected()) Settings.setLootIds(1216);
		if(checkBox[15].isSelected()) Settings.setLootIds(450);
		if(checkBox[16].isSelected()) Settings.setLootIds(20667);
		if(checkBox[17].isSelected()) Settings.setLootIds(2362);
		if(checkBox[18].isSelected()) Settings.setLootIds(270);
		if(checkBox[19].isSelected()) Settings.setLootIds(5304);
		if(checkBox[20].isSelected()) Settings.setLootIds(1201);
		if(checkBox[21].isSelected()) Settings.setLootIds(2366);
		if(checkBox[22].isSelected()) Settings.setLootIds(1149);
		if(checkBox[23].isSelected()) Settings.setLootIds(892);
		if(checkBox[24].isSelected()) Settings.setLootIds(7937);
		if(checkBox[25].isSelected()) Settings.setLootIds(454);
		if(checkBox[26].isSelected()) Settings.setLootIds(258);
		if(checkBox[27].isSelected()) Settings.setLootIds(2999);
		if(checkBox[28].isSelected()) Settings.setLootIds(6686);
		if(checkBox[29].isSelected()) Settings.setLootIds(5315);
		if(checkBox[30].isSelected()) Settings.setLootIds(5316);
		if(checkBox[31].isSelected()) Settings.setLootIds(1516);
		if(checkBox[32].isSelected()) Settings.setLootIds(24154);
		if(checkBox[33].isSelected()) Settings.setLootIds(18778);
		if(checkBox[34].isSelected()) Settings.setLootIds(Constants.CHAMPION_SCROLLS); 
	}

	public void loadButtonActionEvent(ActionEvent e) {
		editorPane1.setText("");
		if(formattedTextField1.getText() != null) {
			getSettings(formattedTextField1.getText());
		}
	}

	public final static DefaultListModel<String> model = new DefaultListModel<String>();

	public static String[] NPC_LIST() {
		long start = System.nanoTime();
		ArrayList<String> list = new ArrayList<String>();
		NPC[] all = NPCs.getLoaded(new Filter<NPC>() {
			@Override
			public boolean accept(NPC arg0) {
				if(arg0 != null && arg0.getName().equals("Mound") && MonsterKiller.isInArea(arg0)) return true;
				return arg0 != null && arg0.getLevel() != 0 && MonsterKiller.isInArea(arg0);
			}
		});
		for (NPC i : all) {
			String s = i.getName() + "(" + i.getLevel() + ") - "+ i.getId();
			if (!list.contains(s)) {
				list.add(s);
			}
		}
		String[] temp = new String[list.size()];
		for(int i = 0; i < list.size(); ++i) {
			temp[i] = list.get(i);
		}
		Arrays.sort(temp);
		System.out.println("Generated in: " + (System.nanoTime() - start) + " nanos");
		return temp;
	}

	public static JFrame getFrame() {
		return frame;
	}

	public void load() throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(Environment.getStorageDirectory() + "/config.properties"));
		String boxes = props.getProperty("checkboxes");
		for(int i = 0; i < checkBox.length; i++) {
			checkBox[i].setSelected((Integer.parseInt(String.valueOf(boxes.charAt(i))) == 1));
		}
		editorPane1.setText(props.getProperty("customloot"));
		comboBox1.setSelectedIndex(Integer.parseInt(props.getProperty("foodindex")));
		spinner1.setValue(Integer.parseInt(props.getProperty("radius")));
		spinner2.setValue(Integer.parseInt(props.getProperty("overx")));
		editorPane2.setText(props.getProperty("alchs"));
		abilDelay.setValue(Integer.parseInt(props.getProperty("abildelay")));
		mouseBox.setSelectedIndex(Integer.parseInt(props.getProperty("mouse")));
		fastCamera.setSelected(props.getProperty("fcamera").equals("true") ? true : false );
		quickPrayer.setSelected(props.getProperty("qprayer").equals("true") ? true : false );
		buryBones.setSelected(props.getProperty("burybones").equals("true") ? true : false );
		waitLoot.setSelected(props.getProperty("waitloot").equals("true") ? true : false);
		popupCheckbox.setSelected(props.getProperty("popups").equals("true") ? true : false );
		lootClues.setSelected(props.getProperty("clues").equals("true") ? true : false);
	}
	
	public static boolean useQuickPrayer = false;
	public JCheckBox quickPrayer;
	
	public static boolean bury = false;
	private JCheckBox buryBones;
	
	private JCheckBox lootClues;

	public void save() throws Exception {
		Properties prop = new Properties();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < checkBox.length; i++) {
			sb.append(checkBox[i].isSelected() ? "1" : "0");
		}
		prop.setProperty("checkboxes", sb.toString());
		prop.setProperty("customloot", editorPane1.getText());
		prop.setProperty("foodindex", String.valueOf(comboBox1.getSelectedIndex()));
		prop.setProperty("radius", String.valueOf(spinner1.getValue()));
		prop.setProperty("overx", String.valueOf(spinner2.getValue()));
		prop.setProperty("alchs", editorPane2.getText());
		prop.setProperty("mouse", String.valueOf(mouseBox.getSelectedIndex()));
		prop.setProperty("abildelay", String.valueOf(abilDelay.getValue()));
		prop.setProperty("fcamera", String.valueOf(useFastCamera));
		prop.setProperty("qprayer", String.valueOf(quickPrayer.isSelected()));
		prop.setProperty("burybones", String.valueOf(buryBones.isSelected()));
		prop.setProperty("waitloot", String.valueOf(waitLoot.isSelected()));
		prop.setProperty("popups", String.valueOf(popupCheckbox.isSelected()));
		prop.setProperty("clues", String.valueOf(lootClues.isSelected()));
		prop.store(new FileOutputStream(Environment.getStorageDirectory()+ "/config.properties"), null);
		System.out.println("Settings saved!");
	}

	public void getSettings(String url2) {
		try {
			if(url2.length() == 0) {
				editorPane1.setText("Invalid url..");
				return;
			}
			URL url = new URL(url2);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String htmlText;
			while ((htmlText = in.readLine()) != null) {
				editorPane1.setText(htmlText);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startActionEvent(ActionEvent e) {
		addToArraylist();
		if(parseCustomLoot(editorPane1.getText()) != null ) {
			Settings.setLootIds(parseCustomLoot(editorPane1.getText()));
		}
		if(parseCustomLoot(editorPane2.getText()) != null) {
			Settings.setAlchIds(parseCustomLoot(editorPane2.getText()));
		}

		Settings.setRadius(getRadius());
		Settings.setStartTile(Players.getLocal().getLocation());
		Settings.setLootValue(lootOverX());

		Settings.setFoodId(getFoodId((String)comboBox1.getSelectedItem()));
		for (String i : list1.getSelectedValuesList()) {
			String name = i;
			String format = name.split("- ")[1];
			Settings.setMonsterIds(Integer.parseInt(format));
		}
		
		frame.setVisible(false);
		
		if(Alch.hasRunes()) {
			MonsterKiller.provide(new Alch());
		}

		final Node[] nodes = {new EatFood(), new Teleport(), new Expandbar(),
				new EnableRun(), new BuryBones(), new SpinTicket(), new BarrowsCheck(),
				new AttackOneOf(), new FightEntity(), new LootHandler(),
				new RejuvFailsafe()
		};
		MonsterKiller.provide(nodes);
		if(Misc.contains(Constants.SUMMONING_RESTORE)) {
			MonsterKiller.provide(new SummoningHandler());
		}
		if(GemBag.hasGemBag()) {
			MonsterKiller.provide(new GemBag());
		}

		if(Misc.contains(Constants.ATTACK) || Misc.contains(Constants.STRENGTH) || Misc.contains(Constants.DEFENSE)
				|| Misc.contains(Constants.RANGE)|| Misc.contains(Constants.MAGIC)) {
			MonsterKiller.provide(new Potions());
		}

		if(Inventory.getItem(4155) != null) {
			MonsterKiller.provide(new EndOnSlayerTask());
		}
		
		Settings.setMouseSpeed((Speed) mouseBox.getSelectedItem());

		MonsterKiller.setSpeed(Settings.getMouseSpeed());
		
		Settings.setLootClues(lootClues.isSelected());
		
		useFastCamera = fastCamera.isSelected();
		abilityDelay = abilDelay.getValue();
		useQuickPrayer = quickPrayer.isSelected();
		bury = buryBones.isSelected();
		waitForLoot = waitLoot.isSelected();
		showPopups = popupCheckbox.isSelected();
		
		if(Settings.lootClueScrolls()) { 
			Settings.setLootIds(Constants.CLUE_SCROLLS);
		}
		
		Logger.log("Looting clues: " + Settings.lootClueScrolls());
		Logger.log("Mouse speed: " + Settings.getMouseSpeed());
		Logger.log("Camera speed: "+ (FighterGUI.useFastCamera? "fast":"slow"));
		Logger.log("Ability delay: " + FighterGUI.abilityDelay);
		Logger.log("Quick prayer: " + FighterGUI.useQuickPrayer);
		Logger.log("Food id: " + Settings.getFoodId());
		Logger.log("Looting items over " + Settings.getLootValue() + " gp!");
		
		Misc.showMessage("Kenneh's AIO Fighter", "Script started!", MonsterKiller.img);
		
		Logger.log("AIOFighter Initialized..");
		
		try {
			save();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
//	public static Speed speed = Speed.VERY_FAST;
	public static boolean useFastCamera = true;
	public static int abilityDelay = 250;
	public static boolean waitForLoot = false;
	public static boolean showPopups = false;
	
	
	private JCheckBox popupCheckbox;
	private JComboBox<Speed> mouseBox;
	private DefaultComboBoxModel<Speed> mouseBoxModel;
	private JSlider abilDelay;
	private JCheckBox fastCamera;
	
	private JCheckBox waitLoot;

	public int[] parseCustomLoot(String text) {
		try {
			final String[] temp = text.split(",");
			final int[] data = new int[temp.length];
			for(int i = 0; i < temp.length; i++) {
				data[i] = Integer.parseInt(temp[i].trim());
			}
			System.out.println(data.length);
			return data;
		} catch(Exception a) {
		}
		return null;
	}

	public static int lootOverX() {
		int value = (int) spinner2.getValue();
		return value == 0 ? Integer.MAX_VALUE : value;
	}

	public static int getRadius() {
		return (int)spinner1.getValue();
	}

	public static boolean radiusIsNull() {
		return spinner1 == null;
	}

	public void initExtraPanel() {
		final JPanel panel = new JPanel();
		final JLabel label = new JLabel();
		final JLabel label2 = new JLabel();
		final JLabel label3 = new JLabel();
		buryBones = new JCheckBox("Bury bones");
		waitLoot = new JCheckBox("Wait for loot");
		label3.setText("Have quick prayers in ActionBar slot 0!");
		label2.setText("Choose the default mouse speed!");
		label.setText("Choose the ability delay (The higher the value, the longer it will wait inbetween ability usage!)");
		mouseBoxModel = new DefaultComboBoxModel<Speed>();
		mouseBox = new JComboBox<Speed>(mouseBoxModel);
		for(Speed s : Speed.values()) {
			mouseBoxModel.addElement(s);
		}
		lootClues = new JCheckBox("Loot clue scrolls");
		popupCheckbox = new JCheckBox("Show popup notifications");
		quickPrayer = new JCheckBox("Enable quick prayer usage");
		fastCamera = new JCheckBox("Use fast camera movements");
		abilDelay = new JSlider();
		abilDelay.setValue(abilityDelay);
		abilDelay.setMajorTickSpacing(250);
		abilDelay.setMaximum(5000);
		abilDelay.setPaintTicks(true);
		panel.setLayout(new GridBagLayout());
		abilDelay.setPreferredSize(new Dimension(600, 10));
		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.ipady = 20;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(label, c);
		c.gridx = 0;
		c.gridy = 1;
		panel.add(abilDelay, c);
		c.gridx = 0;
		c.gridy = 2;
		panel.add(label2, c);
		c.gridx = 0;
		c.gridy = 3;
		c.ipady = 0;
		panel.add(mouseBox, c);
		c.ipady = 20;
		c.gridx = 0;
		c.gridy = 4;
		panel.add(fastCamera, c);
		c.gridx = 0;
		c.gridy = 5;
		c.ipady = 0;
		panel.add(label3, c);
		c.gridx = 0;
		c.gridy = 6;
		panel.add(quickPrayer, c);
		c.gridx = 0;
		c.gridy = 7;
		panel.add(buryBones, c);
		c.gridx = 0;
		c.gridy = 8;
		panel.add(waitLoot, c);
		c.gridx = 0;
		c.gridy = 9;
		panel.add(popupCheckbox, c);
		c.gridx = 0;
		c.gridy = 10;
		panel.add(lootClues, c);
		tabbedPane1.addTab("SETTINGS", panel);
	}

	private void initComponents() {
		try {
			checkBox = new JCheckBox[35];

			for(int i = 0; i < 35; i++) {
				checkBox[i] = new JCheckBox();
			}

			frame = new JFrame();
			tabbedPane1 = new JTabbedPane();
			panel1 = new JPanel();
			scrollPane1 = new JScrollPane();
			list1 = new JList<String>(model);
			spinner1 = new JSpinner();
			label1 = new JLabel();
			label2 = new JLabel();
			button2 = new JButton();
			comboBox1 = new JComboBox<String>();
			label4 = new JLabel();
			panel2 = new JPanel();
			label5 = new JLabel();
			layeredPane1 = new JLayeredPane();
			label6 = new JLabel();
			layeredPane2 = new JLayeredPane();
			label7 = new JLabel();
			layeredPane3 = new JLayeredPane();
			label8 = new JLabel();
			layeredPane4 = new JLayeredPane();
			label9 = new JLabel();
			formattedTextField1 = new JFormattedTextField();
			button3 = new JButton();
			layeredPane5 = new JLayeredPane();
			label10 = new JLabel();
			spinner2 = new JSpinner();
			layeredPane6 = new JLayeredPane();
			label11 = new JLabel();
			scrollPane2 = new JScrollPane();
			editorPane1 = new JEditorPane();
			panel3 = new JPanel();
			label12 = new JLabel();
			scrollPane3 = new JScrollPane();
			editorPane2 = new JEditorPane();
			button1 = new JButton();
			label3 = new JLabel();

			// COMBO BOX STUFF //

			comboBox1.setModel(new DefaultComboBoxModel<>(new String[] {
					"Shrimp", "Trout", "Salmon", "Tuna", "Lobster",
					"Swordfish", "Monkfish", "Shark", "Cavefish",
			"Rocktail" }));

			button2.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					populateActionEvent(arg0);
				}

			});

			//======== this ========
			setBackground(Color.white);

			//======== tabbedPane1 ========
			{
				tabbedPane1.setBackground(Color.white);

				//======== panel1 ========
				{

					//======== scrollPane1 ========
					{
						scrollPane1.setViewportView(list1);
					}

					//---- spinner1 ----
					spinner1.setModel(new SpinnerNumberModel(1, 1, null, 1));

					//---- label1 ----
					label1.setText("Hold control while clicking to select multiple npcs.");

					//---- label2 ----
					label2.setText("Fight Radius");

					//---- button2 ----
					button2.setText("Refresh NPC list");

					//---- label4 ----
					label4.setText("Select your food");
					label4.setHorizontalAlignment(SwingConstants.CENTER);

					GroupLayout panel1Layout = new GroupLayout(panel1);
					panel1.setLayout(panel1Layout);
					panel1Layout.setHorizontalGroup(
							panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup()
									.addContainerGap()
									.addGroup(panel1Layout.createParallelGroup()
											.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
											.addGroup(panel1Layout.createSequentialGroup()
													.addComponent(label1)
													.addGap(0, 0, Short.MAX_VALUE))
													.addGroup(panel1Layout.createSequentialGroup()
															.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
																	.addComponent(spinner1, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
																	.addComponent(label2))
																	.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
																	.addComponent(button2)
																	.addGap(153, 153, 153)
																	.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
																			.addComponent(comboBox1, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
																			.addComponent(label4, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))))
																			.addContainerGap())
							);
					panel1Layout.setVerticalGroup(
							panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup()
									.addGap(13, 13, 13)
									.addComponent(label1)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
									.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(label2)
											.addComponent(label4))
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
													.addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(button2))
													.addContainerGap())
							);
				}
				tabbedPane1.addTab("FIGHTING", panel1);


				//======== panel2 ========
				{

					//---- label5 ----
					label5.setText("Select the following options to loot");

					//======== layeredPane1 ========
					{
						layeredPane1.setBorder(new EtchedBorder());

						//---- label6 ----
						label6.setText("Charms");
						label6.setHorizontalAlignment(SwingConstants.CENTER);
						layeredPane1.add(label6, JLayeredPane.DEFAULT_LAYER);
						label6.setBounds(0, 5, 145, label6.getPreferredSize().height);

						//---- checkBox1 ----
						checkBox[0].setText("Gold");
						layeredPane1.add(checkBox[0], JLayeredPane.DEFAULT_LAYER);
						checkBox[0].setBounds(5, 25, 50, checkBox[0].getPreferredSize().height);

						//---- checkBox2 ----
						checkBox[1].setText("Green");
						layeredPane1.add(checkBox[1], JLayeredPane.DEFAULT_LAYER);
						checkBox[1].setBounds(75, 25, 65, checkBox[1].getPreferredSize().height);

						//---- checkBox3 ----
						checkBox[2].setText("Crimson");
						layeredPane1.add(checkBox[2], JLayeredPane.DEFAULT_LAYER);
						checkBox[2].setBounds(5, 50, 70, checkBox[2].getPreferredSize().height);

						//---- checkBox4 ----
						checkBox[3].setText("Blue");
						layeredPane1.add(checkBox[3], JLayeredPane.DEFAULT_LAYER);
						checkBox[3].setBounds(new Rectangle(new Point(75, 50), checkBox[3].getPreferredSize()));
					}

					//======== layeredPane2 ========
					{
						layeredPane2.setBorder(new EtchedBorder());

						//---- label7 ----
						label7.setText("Rare drop table");
						label7.setHorizontalAlignment(SwingConstants.CENTER);
						layeredPane2.add(label7, JLayeredPane.DEFAULT_LAYER);
						label7.setBounds(-5, 0, 605, label7.getPreferredSize().height);

						//---- checkBox5 ----
						checkBox[4].setText("Runite ore(noted)");
						layeredPane2.add(checkBox[4], JLayeredPane.DEFAULT_LAYER);
						checkBox[4].setBounds(5, 20, 115, checkBox[4].getPreferredSize().height);

						//---- checkBox6 ----
						checkBox[5].setText("Air orb(noted)");
						layeredPane2.add(checkBox[5], JLayeredPane.DEFAULT_LAYER);
						checkBox[5].setBounds(5, 45, 110, checkBox[5].getPreferredSize().height);

						//---- checkBox7 ----
						checkBox[6].setText("Onyx bolts");
						layeredPane2.add(checkBox[6], JLayeredPane.DEFAULT_LAYER);
						checkBox[6].setBounds(5, 70, 80, checkBox[6].getPreferredSize().height);

						//---- checkBox8 ----
						checkBox[7].setText("Palm tree seed");
						layeredPane2.add(checkBox[7], JLayeredPane.DEFAULT_LAYER);
						checkBox[7].setBounds(5, 95, 105, checkBox[7].getPreferredSize().height);

						//---- checkBox9 ----
						checkBox[8].setText("Fire orb(noted)");
						layeredPane2.add(checkBox[8], JLayeredPane.DEFAULT_LAYER);
						checkBox[8].setBounds(5, 120, 105, checkBox[8].getPreferredSize().height);

						//---- checkBox10 ----
						checkBox[9].setText("Battlestaff(noted)");
						layeredPane2.add(checkBox[9], JLayeredPane.DEFAULT_LAYER);
						checkBox[9].setBounds(5, 145, 115, checkBox[9].getPreferredSize().height);

						//---- checkBox11 ----
						checkBox[10].setText("Clean snapdragon(noted)");
						layeredPane2.add(checkBox[10], JLayeredPane.DEFAULT_LAYER);
						checkBox[10].setBounds(120, 45, 150, checkBox[10].getPreferredSize().height);

						//---- checkBox12 ----
						checkBox[11].setText("Rune bar(noted)");
						layeredPane2.add(checkBox[11], JLayeredPane.DEFAULT_LAYER);
						checkBox[11].setBounds(120, 70, 105, checkBox[11].getPreferredSize().height);

						//---- checkBox13 ----
						checkBox[12].setText("Raw shark(noted)");
						layeredPane2.add(checkBox[12], JLayeredPane.DEFAULT_LAYER);
						checkBox[12].setBounds(120, 95, 120, checkBox[12].getPreferredSize().height);

						//---- checkBox14 ----
						checkBox[13].setText("Dragon dagger");
						layeredPane2.add(checkBox[13], JLayeredPane.DEFAULT_LAYER);
						checkBox[13].setBounds(120, 120, 110, checkBox[13].getPreferredSize().height);

						//---- checkBox15 ----
						checkBox[14].setText("Dragon dagger(noted)");
						layeredPane2.add(checkBox[14], JLayeredPane.DEFAULT_LAYER);
						checkBox[14].setBounds(275, 20, 135, checkBox[14].getPreferredSize().height);

						//---- checkBox16 ----
						checkBox[15].setText("Adamantite ore(noted)");
						layeredPane2.add(checkBox[15], JLayeredPane.DEFAULT_LAYER);
						checkBox[15].setBounds(275, 45, 135, checkBox[15].getPreferredSize().height);

						//---- checkBox17 ----
						checkBox[16].setText("Vecna skull");
						layeredPane2.add(checkBox[16], JLayeredPane.DEFAULT_LAYER);
						checkBox[16].setBounds(275, 70, 90, checkBox[16].getPreferredSize().height);

						//---- checkBox18 ----
						checkBox[17].setText("Adamant bar(noted)");
						layeredPane2.add(checkBox[17], JLayeredPane.DEFAULT_LAYER);
						checkBox[17].setBounds(275, 95, 125, checkBox[17].getPreferredSize().height);

						//---- checkBox19 ----
						checkBox[18].setText("Clean torstol(noted)");
						layeredPane2.add(checkBox[18], JLayeredPane.DEFAULT_LAYER);
						checkBox[18].setBounds(275, 120, 135, checkBox[18].getPreferredSize().height);

						//---- checkBox20 ----
						checkBox[19].setText("Torstol seed");
						layeredPane2.add(checkBox[19], JLayeredPane.DEFAULT_LAYER);
						checkBox[19].setBounds(410, 20, 90, checkBox[19].getPreferredSize().height);

						//---- checkBox21 ----
						checkBox[20].setText("Rune kiteshield");
						layeredPane2.add(checkBox[20], JLayeredPane.DEFAULT_LAYER);
						checkBox[20].setBounds(410, 45, 115, checkBox[20].getPreferredSize().height);

						//---- checkBox22 ----
						checkBox[21].setText("Shield left half");
						layeredPane2.add(checkBox[21], JLayeredPane.DEFAULT_LAYER);
						checkBox[21].setBounds(410, 70, 110, checkBox[21].getPreferredSize().height);

						//---- checkBox23 ----
						checkBox[22].setText("Dragon helm");
						layeredPane2.add(checkBox[22], JLayeredPane.DEFAULT_LAYER);
						checkBox[22].setBounds(410, 95, 100, checkBox[22].getPreferredSize().height);

						//---- checkBox24 ----
						checkBox[23].setText("Rune arrow");
						layeredPane2.add(checkBox[23], JLayeredPane.DEFAULT_LAYER);
						checkBox[23].setBounds(410, 120, 90, checkBox[23].getPreferredSize().height);

						//---- checkBox25 ----
						checkBox[24].setText("Pure essence(noted)");
						layeredPane2.add(checkBox[24], JLayeredPane.DEFAULT_LAYER);
						checkBox[24].setBounds(120, 20, 130, checkBox[24].getPreferredSize().height);

						//---- checkBox26 ----
						checkBox[25].setText("Coal(noted)");
						layeredPane2.add(checkBox[25], JLayeredPane.DEFAULT_LAYER);
						checkBox[25].setBounds(5, 170, 100, checkBox[25].getPreferredSize().height);

						//---- checkBox27 ----
						checkBox[26].setText("Clean ranarr(noted)");
						layeredPane2.add(checkBox[26], JLayeredPane.DEFAULT_LAYER);
						checkBox[26].setBounds(275, 145, 125, checkBox[26].getPreferredSize().height);

						//---- checkBox28 ----
						checkBox[27].setText("Clean toadflax(noted)");
						layeredPane2.add(checkBox[27], JLayeredPane.DEFAULT_LAYER);
						checkBox[27].setBounds(120, 145, 140, checkBox[27].getPreferredSize().height);

						//---- checkBox29 ----
						checkBox[28].setText("Saradomin brew (4)(noted)");
						layeredPane2.add(checkBox[28], JLayeredPane.DEFAULT_LAYER);
						checkBox[28].setBounds(120, 170, 155, checkBox[28].getPreferredSize().height);

						//---- checkBox30 ----
						checkBox[29].setText("Yew logs(noted)");
						layeredPane2.add(checkBox[29], JLayeredPane.DEFAULT_LAYER);
						checkBox[29].setBounds(275, 170, 120, checkBox[29].getPreferredSize().height);

						//---- checkBox31 ----
						checkBox[30].setText("Yew seed");
						layeredPane2.add(checkBox[30], JLayeredPane.DEFAULT_LAYER);
						checkBox[30].setBounds(410, 145, 80, checkBox[30].getPreferredSize().height);

						//---- checkBox32 ----
						checkBox[31].setText("Magic seed");
						layeredPane2.add(checkBox[31], JLayeredPane.DEFAULT_LAYER);
						checkBox[31].setBounds(410, 170, 85, checkBox[31].getPreferredSize().height);
					}

					//======== layeredPane3 ========
					{
						layeredPane3.setBorder(new EtchedBorder());

						//---- label8 ----
						label8.setText("Miscellaneous");
						label8.setHorizontalAlignment(SwingConstants.CENTER);
						layeredPane3.add(label8, JLayeredPane.DEFAULT_LAYER);
						label8.setBounds(0, 5, 105, label8.getPreferredSize().height);

						//---- checkBox33 ----
						checkBox[32].setText("Spin Tickets");
						layeredPane3.add(checkBox[32], JLayeredPane.DEFAULT_LAYER);
						checkBox[32].setBounds(5, 25, 90, checkBox[32].getPreferredSize().height);

						//---- checkBox34 ----
						checkBox[33].setText("Effigys");
						layeredPane3.add(checkBox[33], JLayeredPane.DEFAULT_LAYER);
						checkBox[33].setBounds(5, 50, 75, checkBox[33].getPreferredSize().height);

						checkBox[34].setText("Champion scrolls");
						layeredPane3.add(checkBox[34], JLayeredPane.DEFAULT_LAYER);
						checkBox[34].setBounds(5, 75, 95, checkBox[33].getPreferredSize().height);
					}

					//======== layeredPane4 ========
					{
						layeredPane4.setBorder(new EtchedBorder());

						//---- label9 ----
						label9.setText("Import list from URL..");
						layeredPane4.add(label9, JLayeredPane.DEFAULT_LAYER);
						label9.setBounds(5, 5, label9.getPreferredSize().width, 19);
						layeredPane4.add(formattedTextField1, JLayeredPane.DEFAULT_LAYER);
						formattedTextField1.setBounds(115, 5, 155, formattedTextField1.getPreferredSize().height);

						//---- button3 ----
						button3.setText("Load");
						button3.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								loadButtonActionEvent(arg0);
							}

						});
						layeredPane4.add(button3, JLayeredPane.DEFAULT_LAYER);
						button3.setBounds(275, 5, 55, button3.getPreferredSize().height);
					}

					//======== layeredPane5 ========
					{
						layeredPane5.setBorder(new EtchedBorder());

						//---- label10 ----
						label10.setText("Loot over x");
						label10.setHorizontalAlignment(SwingConstants.CENTER);
						layeredPane5.add(label10, JLayeredPane.DEFAULT_LAYER);
						label10.setBounds(5, 5, 90, label10.getPreferredSize().height);

						//---- spinner2 ----
						spinner2.setModel(new SpinnerNumberModel(0, 0, null, 1));
						layeredPane5.add(spinner2, JLayeredPane.DEFAULT_LAYER);
						spinner2.setBounds(5, 50, 90, spinner2.getPreferredSize().height);
					}

					//======== layeredPane6 ========
					{
						layeredPane6.setBorder(new EtchedBorder());

						//---- label11 ----
						label11.setText("Enter custom loot ids here..");
						label11.setHorizontalAlignment(SwingConstants.CENTER);
						layeredPane6.add(label11, JLayeredPane.DEFAULT_LAYER);
						label11.setBounds(5, 5, 220, label11.getPreferredSize().height);

						//======== scrollPane2 ========
						{
							scrollPane2.setViewportView(editorPane1);
						}
						layeredPane6.add(scrollPane2, JLayeredPane.DEFAULT_LAYER);
						scrollPane2.setBounds(5, 20, 220, 50);
					}

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(
							panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
									.addContainerGap()
									.addGroup(panel2Layout.createParallelGroup()
											.addGroup(panel2Layout.createSequentialGroup()
													.addComponent(layeredPane1, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
													.addComponent(layeredPane3, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
													.addGroup(panel2Layout.createParallelGroup()
															.addGroup(panel2Layout.createSequentialGroup()
																	.addComponent(layeredPane5, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
																	.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																	.addComponent(layeredPane6, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
																	.addComponent(layeredPane4, GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)))
																	.addGroup(panel2Layout.createSequentialGroup()
																			.addComponent(label5)
																			.addGap(0, 0, Short.MAX_VALUE))
																			.addComponent(layeredPane2, GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE))
																			.addContainerGap())
							);
					panel2Layout.setVerticalGroup(
							panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(label5)
									.addGap(18, 18, 18)
									.addComponent(layeredPane2, GroupLayout.PREFERRED_SIZE, 198, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
											.addGroup(panel2Layout.createSequentialGroup()
													.addComponent(layeredPane4, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
													.addGroup(panel2Layout.createParallelGroup()
															.addComponent(layeredPane5)
															.addGroup(panel2Layout.createSequentialGroup()
																	.addGap(0, 0, Short.MAX_VALUE)
																	.addComponent(layeredPane6, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE))))
																	.addComponent(layeredPane1, GroupLayout.Alignment.LEADING)
																	.addComponent(layeredPane3, GroupLayout.Alignment.LEADING))
																	.addContainerGap(6, Short.MAX_VALUE))
							);
				}
				tabbedPane1.addTab("LOOTING", panel2);


				//======== panel3 ========
				{

					//---- label12 ----
					label12.setText("Enter the ids of the items you want to alch in the box seperated by a comma..");

					//======== scrollPane3 ========
					{
						scrollPane3.setViewportView(editorPane2);
					}

					GroupLayout panel3Layout = new GroupLayout(panel3);
					panel3.setLayout(panel3Layout);
					panel3Layout.setHorizontalGroup(
							panel3Layout.createParallelGroup()
							.addGroup(panel3Layout.createSequentialGroup()
									.addContainerGap()
									.addGroup(panel3Layout.createParallelGroup()
											.addGroup(panel3Layout.createSequentialGroup()
													.addComponent(label12)
													.addGap(0, 0, Short.MAX_VALUE))
													.addComponent(scrollPane3))
													.addContainerGap())
							);
					panel3Layout.setVerticalGroup(
							panel3Layout.createParallelGroup()
							.addGroup(panel3Layout.createSequentialGroup()
									.addContainerGap()
									.addComponent(label12)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
									.addContainerGap())
							);
				}
				tabbedPane1.addTab("ALCHING", panel3);

			}

			//---- button1 ----
			button1.setText("Start Script");

			button1.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					startActionEvent(arg0);
				}

			});

			//---- label3 ----
			label3.setText("Thank you for using my AIO Fighter script, feel free to donate!");

			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);
			layout.setHorizontalGroup(
					layout.createParallelGroup()
					.addComponent(tabbedPane1, GroupLayout.Alignment.TRAILING)
					.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(label3)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(button1, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
					);
			layout.setVerticalGroup(
					layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
							.addComponent(tabbedPane1, GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(button1)
									.addComponent(label3))
									.addGap(7, 7, 7))
					);
			initExtraPanel();
			frame.add(this);
			frame.pack();
			frame.setVisible(true);
		} catch(Exception a) {
			a.printStackTrace();
		}
	}
	public static JCheckBox[] checkBox;
	public static JFrame frame;
	private JTabbedPane tabbedPane1;
	private JPanel panel1;
	private JScrollPane scrollPane1;
	private JList<String> list1;
	private static JSpinner spinner1;
	private JLabel label1;
	private JLabel label2;
	private JButton button2;
	private JComboBox<String> comboBox1;
	private JLabel label4;
	private JPanel panel2;
	private JLabel label5;
	private JLayeredPane layeredPane1;
	private JLabel label6;
	private JLayeredPane layeredPane2;
	private JLabel label7;
	private JLayeredPane layeredPane3;
	private JLabel label8;
	private JLayeredPane layeredPane4;
	private JLabel label9;
	private JFormattedTextField formattedTextField1;
	private JButton button3;
	private JLayeredPane layeredPane5;
	private JLabel label10;
	private static JSpinner spinner2;
	private JLayeredPane layeredPane6;
	private JLabel label11;
	private JScrollPane scrollPane2;
	private JEditorPane editorPane1;
	private JPanel panel3;
	private JLabel label12;
	private JScrollPane scrollPane3;
	private JEditorPane editorPane2;
	private JButton button1;
	private JLabel label3;
}

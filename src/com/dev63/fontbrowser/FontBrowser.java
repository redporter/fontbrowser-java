/*
 *  Dev63.com confidential. DO NOT DISTRIBUTE.
 *  Createad: 13 Apr 2025 14:25:06
 *  Author:  nemanja
 */
package com.dev63.fontbrowser;

import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;


public class FontBrowser extends JFrame{

	public static void main(String[] args) throws Exception {
		FontBrowser b  = new FontBrowser();
		b.setVisible(true);
	}

	private JList<String> familyC;
	private JComboBox<Integer> sizeC;
	private JTextArea text;
	private JTabbedPane center;
	
	private String family;
	private int size;
	
	private JToggleButton italic;
	private JToggleButton bold;
	private JToggleButton previewBtn;
	
	private Font mainF = new Font("Nosifer", Font.PLAIN, 14);
	private Color mainC = new Color(200, 20, 20, 255);

	private boolean preview = false;
	private JTextField searchField;
	private String[] allFonts;
	
	public FontBrowser() throws Exception {
		setTitle("Font Browser");
		setSize(1024, 768);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		InputStream stream = FontBrowser.class.getResourceAsStream("Nosifer-Regular.ttf");
		ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));		
		
		mainF = new Font("Nosifer", Font.PLAIN, 14);
		mainC = new Color(200, 20, 20, 255);
		
		JPanel leftO = new JPanel(new BorderLayout());
		JPanel left = new JPanel(new GridLayout(0, 1, 5, 5));
		

		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
		allFonts = fonts;
		if(fonts.length < 1){
			JOptionPane.showMessageDialog(left, "Strage", 
					"No fonts found on system ?!?", JOptionPane.WARNING_MESSAGE);
			System.exit(1); 
		}
		family = fonts[0];
		
		familyC = new JList<>(fonts);
		leftO.add(new JScrollPane(familyC), BorderLayout.CENTER);
		familyC.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()){
					return;
				}
				
				int index = familyC.getSelectedIndex();
				if(index < 0){
					return;
				}
				
				updateFont(familyC.getModel().getElementAt(index), -1); 
			}
		});
		familyC.setCellRenderer(new ListRenderer());
		familyC.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){
					preview = !preview;
					if (previewBtn != null) {
						previewBtn.setSelected(preview);
					}
					familyC.repaint();
				}
			}
		});
		familyC.setToolTipText("Double click to enbable/disable preview in list."); 
		
		Integer[] sizes = new Integer[]{8, 9, 10, 11, 12, 13, 14, 15, 16, 18, 
			20, 22, 24, 26, 30, 36, 42, 48, 54, 60};
		size = 14;
		
		JPanel sizePanel = new JPanel(new GridLayout(1, 2, 5, 5));
		sizePanel.setOpaque(false);
		JLabel label = new JLabel("Size:");
		restyle(label); 
		sizePanel.add(label);
		sizeC = new JComboBox<>(sizes);
		sizeC.setSelectedItem(14); 		
		sizeC.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() != ItemEvent.SELECTED){
					return;
				}
				
				updateFont(null, (Integer) e.getItem());
			}
		});
		sizePanel.add(sizeC);
		restyle(sizeC);
		left.add(sizePanel);
		
		center = new JTabbedPane();
		add(center, BorderLayout.CENTER);
		
		text = new JTextArea();
		text.setText("Quick brown fox jumps over lazy dog.");
		center.addTab("Text", new JScrollPane(text));
		center.addTab("Buttons", new JScrollPane(createButtonsPanel()));
		center.addTab("Inputs", new JScrollPane(createInputsPanel()));
		center.addTab("Selection", new JScrollPane(createSelectionPanel()));
		center.addTab("Data Views", createDataViewsPanel());
		center.addTab("Menus", createMenusPanel());
		center.addTab("Feedback & Info", new JScrollPane(createFeedbackPanel()));
		
		
		leftO.add(left, BorderLayout.NORTH);
		add(leftO, BorderLayout.WEST);
		
		JPanel stylePanel = new JPanel(new GridLayout(1, 3, 5, 5));
		stylePanel.setOpaque(false);
		
		italic = new JToggleButton("Italic");
		stylePanel.add(italic);
		italic.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFont(null, -1);
			}
		});
		restyle(italic);
		
		bold = new JToggleButton("Bold");
		stylePanel.add(bold);
		bold.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFont(null, -1);
			}
		});
		bold.setFont(mainF);
		restyle(bold);
		
		previewBtn = new JToggleButton("Preview");
		stylePanel.add(previewBtn);
		previewBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				preview = previewBtn.isSelected();
				familyC.repaint();
			}
		});
		restyle(previewBtn);
		
		left.add(stylePanel);
		
		JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
		searchPanel.setOpaque(false);
		JLabel searchLabel = new JLabel("Search:");
		restyle(searchLabel);
		searchField = new JTextField();
		restyle(searchField);
		searchPanel.add(searchLabel, BorderLayout.WEST);
		searchPanel.add(searchField, BorderLayout.CENTER);
		leftO.add(searchPanel, BorderLayout.SOUTH);
		
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filterFonts();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				filterFonts();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				filterFonts();
			}
		});
		
		updateFont(null, -1); 
	}
	
	private void restyle(Component c){
		c.setFont(mainF);
		c.setForeground(mainC); 
	}
	
	private void updateFont(String family, int size) {
		if(family != null){
			this.family = family;
		}
		
		if(size > 0){
			this.size = size;
		}
		
		int style = Font.PLAIN;
		if(italic.isSelected()){
			style |= Font.ITALIC;
		}
		if(bold.isSelected()){
			style |= Font.BOLD;
		}
		
		Font f = new Font(this.family, style, this.size);
		System.out.println(this.family + " / " + style + " / " + size);
		if (center != null) {
			updateComponentFonts(center, f);
		} else {
			text.setFont(f);
		}
	}

	private void updateComponentFonts(Component comp, Font font) {
		comp.setFont(font);
		if (comp instanceof JTable) {
			JTable table = (JTable) comp;
			if (table.getTableHeader() != null) {
				table.getTableHeader().setFont(font);
			}
		}
		if (comp instanceof JComponent) {
			JComponent jc = (JComponent) comp;
			if (jc.getBorder() instanceof javax.swing.border.TitledBorder) {
				((javax.swing.border.TitledBorder) jc.getBorder()).setTitleFont(font);
			}
		}
		if (comp instanceof JMenu) {
			JMenu menu = (JMenu) comp;
			for (Component child : menu.getMenuComponents()) {
				updateComponentFonts(child, font);
			}
		}
		if (comp instanceof Container) {
			for (Component child : ((Container) comp).getComponents()) {
				updateComponentFonts(child, font);
			}
		}
	}

	private void filterFonts() {
		String query = searchField.getText().trim().toLowerCase();
		java.util.List<String> filtered = new java.util.ArrayList<>();
		for (String font : allFonts) {
			if (query.isEmpty() || font.toLowerCase().contains(query)) {
				filtered.add(font);
			}
		}
		String selected = familyC.getSelectedValue();
		familyC.setListData(filtered.toArray(new String[0]));
		if (selected != null && filtered.contains(selected)) {
			familyC.setSelectedValue(selected, true);
		} else if (!filtered.isEmpty()) {
			familyC.setSelectedIndex(0);
		}
	}

	private JPanel createButtonsPanel() {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

		JButton btnActive = new JButton("Standard Button");
		JButton btnDisabled = new JButton("Disabled Button");
		btnDisabled.setEnabled(false);

		JToggleButton btnToggle = new JToggleButton("Selected Toggle Button");
		btnToggle.setSelected(true);
		JToggleButton btnToggleUn = new JToggleButton("Unselected Toggle Button");

		JCheckBox cbChecked = new JCheckBox("Active Checkbox (Checked)");
		cbChecked.setSelected(true);
		JCheckBox cbUnchecked = new JCheckBox("Active Checkbox (Unchecked)");

		JRadioButton rbA = new JRadioButton("Radio Option A");
		rbA.setSelected(true);
		JRadioButton rbB = new JRadioButton("Radio Option B");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbA);
		bg.add(rbB);

		panel.add(btnActive);
		panel.add(btnDisabled);
		panel.add(btnToggle);
		panel.add(btnToggleUn);
		panel.add(cbChecked);
		panel.add(cbUnchecked);
		panel.add(rbA);
		panel.add(rbB);

		wrapper.add(panel, BorderLayout.NORTH);
		return wrapper;
	}

	private JPanel createInputsPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		JLabel lblText = new JLabel("Text Field:");
		JTextField txtField = new JTextField("The quick brown fox jumps over the lazy dog.");
		JLabel lblPass = new JLabel("Password Field:");
		JPasswordField txtPass = new JPasswordField("secretpassword");
		JLabel lblArea = new JLabel("Text Area (Wrapped):");
		JTextArea txtArea = new JTextArea("This is a multi-line JTextArea.\nIt has wrap-around text enabled so it flows nicely.");
		txtArea.setLineWrap(true);
		txtArea.setWrapStyleWord(true);
		txtArea.setRows(4);
		JScrollPane areaScroll = new JScrollPane(txtArea);

		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; panel.add(lblText, gbc);
		gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; panel.add(txtField, gbc);

		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; panel.add(lblPass, gbc);
		gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; panel.add(txtPass, gbc);

		gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.NORTH; panel.add(lblArea, gbc);
		gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH; panel.add(areaScroll, gbc);

		return panel;
	}

	private JPanel createSelectionPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.weightx = 1.0;

		JLabel lblCombo = new JLabel("ComboBox Selection:");
		String[] options = {"Option 1 (Active)", "Option 2 (Selected)", "Option 3", "Option 4"};
		JComboBox<String> combo = new JComboBox<>(options);
		combo.setSelectedIndex(1);

		JLabel lblList = new JLabel("JList (Scrollable):");
		String[] listItems = {"Alpha Item", "Beta Item", "Gamma Item", "Delta Item", "Epsilon Item"};
		JList<String> list = new JList<>(listItems);
		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setPreferredSize(new Dimension(150, 80));

		JLabel lblSpinner = new JLabel("JSpinner (Number):");
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(42, 0, 100, 1));

		JLabel lblSlider = new JLabel("JSlider (Ticks):");
		JSlider slider = new JSlider(0, 100, 75);
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		gbc.gridx = 0; gbc.gridy = 0; panel.add(lblCombo, gbc);
		gbc.gridx = 1; gbc.gridy = 0; panel.add(combo, gbc);

		gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTH; panel.add(lblList, gbc);
		gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5; panel.add(listScroll, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0.0;

		gbc.gridx = 0; gbc.gridy = 2; panel.add(lblSpinner, gbc);
		gbc.gridx = 1; gbc.gridy = 2; panel.add(spinner, gbc);

		gbc.gridx = 0; gbc.gridy = 3; panel.add(lblSlider, gbc);
		gbc.gridx = 1; gbc.gridy = 3; panel.add(slider, gbc);

		return panel;
	}

	private JPanel createDataViewsPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
		panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// Table Preview
		String[] columns = {"ID", "License Type", "Cost"};
		Object[][] rowData = {
			{"101", "Developer Seat", "$199.00"},
			{"102", "Team Edition", "$899.00"},
			{"103", "Enterprise Tier", "$2,499.00"}
		};
		JTable table = new JTable(rowData, columns);
		table.setFillsViewportHeight(true);
		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setBorder(BorderFactory.createTitledBorder("JTable Preview"));

		// Tree Preview
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Workspace");
		DefaultMutableTreeNode src = new DefaultMutableTreeNode("src");
		src.add(new DefaultMutableTreeNode("FontBrowser.java"));
		DefaultMutableTreeNode res = new DefaultMutableTreeNode("resources");
		res.add(new DefaultMutableTreeNode("Nosifer-Regular.ttf"));
		res.add(new DefaultMutableTreeNode("OFL.txt"));
		root.add(src);
		root.add(res);
		JTree tree = new JTree(root);
		// Expand the entire tree
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		JScrollPane treeScroll = new JScrollPane(tree);
		treeScroll.setBorder(BorderFactory.createTitledBorder("JTree Preview"));

		panel.add(tableScroll);
		panel.add(treeScroll);

		return panel;
	}

	private JPanel createFeedbackPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1.0;

		JLabel lblProgress = new JLabel("JProgressBar (70%):");
		JProgressBar progress = new JProgressBar(0, 100);
		progress.setValue(70);
		progress.setStringPainted(true);

		JLabel lblInfo = new JLabel("Plain Information Label");
		JLabel lblWarning = new JLabel("Warning Label (Italic Style Demo)");
		lblWarning.setFont(lblWarning.getFont().deriveFont(Font.ITALIC));

		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);

		gbc.gridx = 0; gbc.gridy = 0; panel.add(lblProgress, gbc);
		gbc.gridx = 1; gbc.gridy = 0; panel.add(progress, gbc);

		gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; panel.add(separator, gbc);
		gbc.gridwidth = 1;

		gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Labels:"), gbc);
		gbc.gridx = 1; gbc.gridy = 2; panel.add(lblInfo, gbc);

		gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Styled:"), gbc);
		gbc.gridx = 1; gbc.gridy = 3; panel.add(lblWarning, gbc);

		// Add empty filler to push elements up
		gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weighty = 1.0;
		panel.add(Box.createVerticalGlue(), gbc);

		return panel;
	}

	private JPanel createMenusPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JMenuBar menuBar = new JMenuBar();

		// Center log area to show interaction
		JTextArea menuLog = new JTextArea("Click menu items to see events here...\n");
		menuLog.setEditable(false);
		menuLog.setLineWrap(true);
		menuLog.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(menuLog);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Menu Event Log"));

		// ActionListener for all menu items
		ActionListener menuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuLog.append("Triggered Action: " + e.getActionCommand() + "\n");
			}
		};

		// 1. File Menu
		JMenu fileMenu = new JMenu("File");

		JMenuItem newFile = new JMenuItem("New");
		newFile.addActionListener(menuListener);
		fileMenu.add(newFile);

		JMenuItem openFile = new JMenuItem("Open...");
		openFile.addActionListener(menuListener);
		fileMenu.add(openFile);

		fileMenu.addSeparator();

		// Submenu inside File
		JMenu exportSubMenu = new JMenu("Export As");

		JMenuItem exportPdf = new JMenuItem("PDF Document");
		exportPdf.addActionListener(menuListener);
		exportSubMenu.add(exportPdf);

		JMenuItem exportPng = new JMenuItem("PNG Image");
		exportPng.addActionListener(menuListener);
		exportSubMenu.add(exportPng);

		fileMenu.add(exportSubMenu);

		fileMenu.addSeparator();

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);

		menuBar.add(fileMenu);

		// 2. Edit Menu
		JMenu editMenu = new JMenu("Edit");

		JMenuItem cutItem = new JMenuItem("Cut");
		cutItem.addActionListener(menuListener);
		editMenu.add(cutItem);

		JMenuItem copyItem = new JMenuItem("Copy");
		copyItem.addActionListener(menuListener);
		editMenu.add(copyItem);

		JMenuItem pasteItem = new JMenuItem("Paste");
		pasteItem.addActionListener(menuListener);
		editMenu.add(pasteItem);

		editMenu.addSeparator();

		// Submenu inside Edit
		JMenu formatSubMenu = new JMenu("Format Options");

		JMenu caseSubMenu = new JMenu("Change Case");
		JMenuItem upperItem = new JMenuItem("UPPERCASE");
		upperItem.addActionListener(menuListener);
		JMenuItem lowerItem = new JMenuItem("lowercase");
		lowerItem.addActionListener(menuListener);
		caseSubMenu.add(upperItem);
		caseSubMenu.add(lowerItem);
		formatSubMenu.add(caseSubMenu);

		editMenu.add(formatSubMenu);

		menuBar.add(editMenu);

		// 3. View Menu
		JMenu viewMenu = new JMenu("View");

		JCheckBoxMenuItem showGrid = new JCheckBoxMenuItem("Show Gridlines");
		showGrid.setSelected(true);
		showGrid.addActionListener(menuListener);
		viewMenu.add(showGrid);

		JRadioButtonMenuItem lightMode = new JRadioButtonMenuItem("Light Theme");
		JRadioButtonMenuItem darkMode = new JRadioButtonMenuItem("Dark Theme");
		ButtonGroup themeGroup = new ButtonGroup();
		themeGroup.add(lightMode);
		themeGroup.add(darkMode);
		lightMode.setSelected(true);
		lightMode.addActionListener(menuListener);
		darkMode.addActionListener(menuListener);
		viewMenu.add(lightMode);
		viewMenu.add(darkMode);

		menuBar.add(viewMenu);

		// Add JMenuBar to North of panel
		panel.add(menuBar, BorderLayout.NORTH);

		// Add Log to Center of panel
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}
	
	
	private class ListRenderer extends DefaultListCellRenderer{

		private Map<String, Font> loaded = new HashMap<>();
		private Border border = new MatteBorder(0, 0, 1, 0, Color.BLACK);
		
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if(preview){
				String name = (String) value;

				Font f = loaded.get(name);
				if(f == null){
					f = new Font(name, Font.PLAIN, 14);
					loaded.put(name, f);
				}

				c.setFont(f); 
			}else{
				restyle(c); 
			}
			
			setBorder(border);
			return c;
		}
		
	}
	
	
}

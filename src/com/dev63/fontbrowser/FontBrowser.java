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


public class FontBrowser extends JFrame{

	public static void main(String[] args) throws Exception {
		FontBrowser b  = new FontBrowser();
		b.setVisible(true);
	}

	private JList<String> familyC;
	private JComboBox<Integer> sizeC;
	private JTextArea text;
	
	private String family;
	private int size;
	
	private JToggleButton italic;
	private JToggleButton bold;
	
	private Font mainF = new Font("Nosifer", Font.PLAIN, 14);
	private Color mainC = new Color(200, 20, 20, 255);

	private boolean preview = false;
	
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
		JPanel left = new JPanel(new GridLayout(0, 2, 5, 5));
		

		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
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
					familyC.repaint();
				}
			}
		});
		familyC.setToolTipText("Double click to enbable/disable preview in list."); 
		
		Integer[] sizes = new Integer[]{8, 9, 10, 11, 12, 13, 14, 15, 16, 18, 
			20, 22, 24, 26, 30, 36, 42, 48, 54, 60};
		size = 14;
		
		JLabel label = new JLabel("Size:");
		restyle(label); 
		left.add(label);
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
		left.add(sizeC);
		restyle(sizeC);
		
		JTabbedPane center = new JTabbedPane();
		add(center, BorderLayout.CENTER);
		
		text = new JTextArea();
		text.setText("Quick brown fox jumps over lazy dog.");
		center.addTab("Text", new JScrollPane(text));
		
		
		leftO.add(left, BorderLayout.NORTH);
		add(leftO, BorderLayout.WEST);
		
		italic = new JToggleButton("Italic");
		left.add(italic);
		italic.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFont(null, -1);
			}
		});
		restyle(italic);
		
		bold = new JToggleButton("Bold");
		left.add(bold);
		bold.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFont(null, -1);
			}
		});
		bold.setFont(mainF);
		restyle(bold);
		
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
		text.setFont(f); 
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

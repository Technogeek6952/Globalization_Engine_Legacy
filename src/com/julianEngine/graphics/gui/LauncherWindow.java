package com.julianEngine.graphics.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.julianEngine.Engine2D;
import com.julianEngine.utility.Log;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.AbstractListModel;
import java.awt.Font;
import java.awt.Panel;

public class LauncherWindow extends JFrame {

	private JPanel contentPane;
	private static LauncherWindow instance;
	Thread waitingThread;
	private List<JCheckBox> plugins = new ArrayList<JCheckBox>();
	
	/**
	 * Create the frame.
	 */
	public LauncherWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.EAST);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		JPanel playBtnPanel = new JPanel();
		buttonPanel.add(playBtnPanel);
		playBtnPanel.setLayout(new BoxLayout(playBtnPanel, BoxLayout.X_AXIS));
		
		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (waitingThread!=null) 
					synchronized(waitingThread) {
						waitingThread.notify();
					}
			}
		});
		playBtnPanel.add(btnPlay);
		
		JPanel optionsBtnPanel = new JPanel();
		buttonPanel.add(optionsBtnPanel);
		optionsBtnPanel.setLayout(new BoxLayout(optionsBtnPanel, BoxLayout.X_AXIS));
		
		JButton btnNewButton = new JButton("Options");
		optionsBtnPanel.add(btnNewButton);
		
		Component verticalGlue = Box.createVerticalGlue();
		buttonPanel.add(verticalGlue);
		
		JPanel exitBtnPanel = new JPanel();
		buttonPanel.add(exitBtnPanel);
		exitBtnPanel.setLayout(new BoxLayout(exitBtnPanel, BoxLayout.X_AXIS));
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		exitBtnPanel.add(btnExit);
		
		JPanel pluginsPanel = new JPanel();
		contentPane.add(pluginsPanel, BorderLayout.CENTER);
		pluginsPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel pluginsLabel = new JLabel("Plugins:");
		pluginsLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		pluginsPanel.add(pluginsLabel, BorderLayout.NORTH);
		
		JList<JCheckBox> pluginsList = new JList<JCheckBox>();
		pluginsList.setModel(new AbstractListModel<JCheckBox>() {
			private static final long serialVersionUID = -7667998764307478932L;
			
			public int getSize() {
				return plugins.size();
			}
			public JCheckBox getElementAt(int index) {
				return plugins.get(index);
			}
		});
		pluginsList.setCellRenderer(new ListCellRenderer<JCheckBox>(){
			@Override
			public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index,
					boolean isSelected, boolean cellHasFocus) {
				if (isSelected){
					value.setBackground(pluginsList.getSelectionBackground());
					value.setForeground(pluginsList.getSelectionForeground());
				}else{
					value.setBackground(Color.WHITE);
					value.setForeground(Color.BLACK);
				}
				
				return value;
			}
		});
		pluginsList.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				int index = pluginsList.locationToIndex(e.getPoint());
				if (index != -1 && pluginsList.getCellBounds(index, index).contains(e.getPoint())){
					if (plugins.get(index).isEnabled()){
						plugins.get(index).setSelected(!plugins.get(index).isSelected());
					}
				}
				pluginsList.repaint();
			}
		});
		pluginsPanel.add(pluginsList);
		
		JPanel reorderPanel = new JPanel();
		pluginsPanel.add(reorderPanel, BorderLayout.SOUTH);
		
		JButton btnUp = new JButton("Up");
		reorderPanel.add(btnUp);
		
		JButton btnDown = new JButton("Down");
		reorderPanel.add(btnDown);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
	}
	
	public List<String> launchEngine(){
		List<String> pluginsToLoad = new ArrayList<String>();
		this.setVisible(true);
		
		while (true){
			try {
				waitingThread = Thread.currentThread();
				
				File dataDir = new File(System.getProperty("user.dir"), "./Data"); //points to /Data directory
				
				//Get an array of files in the data directory that end in .jdm (Julian Data Master)
				File[] masterFiles = dataDir.listFiles(new FileFilter(){
					public boolean accept(File file){
						return file.getPath().toLowerCase().endsWith(".jdm");
					}
				});
				
				//Get an array of files in the data directory that end in .jdp (Julian Data Plugin)
				File[] dataFiles = dataDir.listFiles(new FileFilter(){
					public boolean accept(File file){
						return file.getPath().toLowerCase().endsWith(".jdp");
					}
				});
				
				if (masterFiles.length > 0){
					JCheckBox pluginBox = new JCheckBox();
					pluginBox.setSelected(true);
					pluginBox.setText(masterFiles[0].getName());
					pluginBox.setEnabled(false);
					plugins.add(pluginBox);
				}
				
				for (File file:dataFiles){
					JCheckBox pluginBox = new JCheckBox();
					pluginBox.setSelected(true);
					pluginBox.setText(file.getName());
					plugins.add(pluginBox);
				}
				
				synchronized (waitingThread){
					Thread.currentThread().wait();
				}
				
				for (JCheckBox plugin:plugins){
					if (plugin.isSelected()){
						pluginsToLoad.add(plugin.getText());
					}
				}
				
				break;
			} catch (InterruptedException e) {
				Log.warn("Thread interrupted while waiting for user input...");
			}
		}
		
		this.setVisible(false);
		return pluginsToLoad;
	}
	
	public static LauncherWindow getInstance(){
		if (instance==null)
			instance = new LauncherWindow();
		return instance;
	}
}

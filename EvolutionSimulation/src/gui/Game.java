package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import simulation.Environment;
import simulation.GameLoop;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

public class Game {


	private final int UPDATE_TIME = 50;
	private final int FOOD_REGENERATION_RATE = 50;
	private TerrainPanel panel;
	private JTextField txtNumberFood;
	private JLabel lblNrSpeciesText;
	private Timer timer;
	private JLabel lblTime;
	private JLabel lblAvgSizeText;
	private JLabel lblAvgSpeedText;
	private JLabel lblAvgScentText;
	private JLabel lblAvgAgeText;
	private JLabel lblEnergyConsumptionText;
	private Environment environment;
	private GameLoop gameloop;
	private OptionData options;
	private JFrame f;
	private boolean runGUI;
	
	public Game(OptionData data, boolean runGUI) {
		this.options = data;
		this.runGUI = runGUI;
		if(runGUI) {
			initGUI();
		}
	}
	
	private void initGUI() {
		this.environment = new Environment(this.options);
		panel = new TerrainPanel(950,950, this.environment);
		SwingUtilities.isEventDispatchThread();
		f = new JFrame("Terrain");
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel gamePannel = new JPanel();
		f.getContentPane().add(gamePannel, BorderLayout.EAST);
		GridBagLayout gbl_gamePannel = new GridBagLayout();
		gamePannel.setLayout(gbl_gamePannel);
		
// the panel containing the good stuff
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridheight = 14;
		gbc_panel.gridy = 0;
		gbc_panel.gridx = 0;
		gamePannel.add(panel, gbc_panel);
		
//menu bar
		JMenuBar menuBar = new JMenuBar();
		f.setJMenuBar(menuBar);
		
		JMenu evolutionMenu = new JMenu("Evolution game");
		menuBar.add(evolutionMenu);
		
		JMenuItem mntmNew = new JMenuItem("New...");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		evolutionMenu.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Restart...");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});
		evolutionMenu.add(mntmOpen);
		
		JLabel lblStatistics = new JLabel("Statistics:");
		GridBagConstraints gbc_lblStatistics = new GridBagConstraints();
		gbc_lblStatistics.gridwidth = 2;
		gbc_lblStatistics.insets = new Insets(5, 5, 5, 0);
		gbc_lblStatistics.gridx = 1;
		gbc_lblStatistics.gridy = 0;
		gamePannel.add(lblStatistics, gbc_lblStatistics);

		JLabel lblNrSpecies = new JLabel("Number of individuals:");
		lblNrSpecies.setOpaque(true);
		lblNrSpecies.setBackground(Color.RED);
		GridBagConstraints gbc_lblNrSpecies = new GridBagConstraints();
		gbc_lblNrSpecies.anchor = GridBagConstraints.WEST;
		gbc_lblNrSpecies.insets = new Insets(5, 5, 5, 5);
		gbc_lblNrSpecies.gridx = 1;
		gbc_lblNrSpecies.gridy = 1;
		gamePannel.add(lblNrSpecies, gbc_lblNrSpecies);
		
		lblNrSpeciesText = new JLabel("");
		GridBagConstraints gbc_lblNrSpeciesText = new GridBagConstraints();
		gbc_lblNrSpeciesText.anchor = GridBagConstraints.WEST;
		gbc_lblNrSpeciesText.weightx = 1.0;
		gbc_lblNrSpeciesText.insets = new Insets(5, 5, 5, 0);
		gbc_lblNrSpeciesText.gridx = 2;
		gbc_lblNrSpeciesText.gridy = 1;
		gamePannel.add(lblNrSpeciesText, gbc_lblNrSpeciesText);
		
		JLabel lblAvgSpeed = new JLabel("Average speed:");
		lblAvgSpeed.setOpaque(true);
		lblAvgSpeed.setBackground(Color.DARK_GRAY);
		GridBagConstraints gbc_lblAvgSpeed = new GridBagConstraints();
		gbc_lblAvgSpeed.anchor = GridBagConstraints.WEST;
		gbc_lblAvgSpeed.insets = new Insets(5, 5, 5, 5);
		gbc_lblAvgSpeed.gridx = 1;
		gbc_lblAvgSpeed.gridy = 2;
		gamePannel.add(lblAvgSpeed, gbc_lblAvgSpeed);
		
		lblAvgSpeedText = new JLabel("");
		GridBagConstraints gbc_lblAvgSpeedText = new GridBagConstraints();
		gbc_lblAvgSpeedText.anchor = GridBagConstraints.WEST;
		gbc_lblAvgSpeedText.weightx = 1.0;
		gbc_lblAvgSpeedText.insets = new Insets(5, 5, 5, 0);
		gbc_lblAvgSpeedText.gridx = 2;
		gbc_lblAvgSpeedText.gridy = 2;
		gamePannel.add(lblAvgSpeedText, gbc_lblAvgSpeedText);
		
		JLabel lblAvgSize = new JLabel("Average size:");
		lblAvgSize.setOpaque(true);
		lblAvgSize.setBackground(Color.BLUE);
		GridBagConstraints gbc_lblAvgSize = new GridBagConstraints();
		gbc_lblAvgSize.anchor = GridBagConstraints.WEST;
		gbc_lblAvgSize.insets = new Insets(5, 5, 5, 5);
		gbc_lblAvgSize.gridx = 1;
		gbc_lblAvgSize.gridy = 3;
		gamePannel.add(lblAvgSize, gbc_lblAvgSize);
		
		lblAvgSizeText = new JLabel("");
		GridBagConstraints gbc_lblAvgSizeText = new GridBagConstraints();
		gbc_lblAvgSizeText.anchor = GridBagConstraints.WEST;
		gbc_lblAvgSizeText.weightx = 1.0;
		gbc_lblAvgSizeText.insets = new Insets(5, 5, 5, 0);
		gbc_lblAvgSizeText.gridx = 2;
		gbc_lblAvgSizeText.gridy = 3;
		gamePannel.add(lblAvgSizeText, gbc_lblAvgSizeText);
		
		JLabel lblAvgAge = new JLabel("Average max age:");
		lblAvgAge.setOpaque(true);
		lblAvgAge.setBackground(Color.YELLOW);
		GridBagConstraints gbc_lblAvgAge = new GridBagConstraints();
		gbc_lblAvgAge.anchor = GridBagConstraints.WEST;
		gbc_lblAvgAge.insets = new Insets(5, 5, 5, 5);
		gbc_lblAvgAge.gridx = 1;
		gbc_lblAvgAge.gridy = 5;
		gamePannel.add(lblAvgAge, gbc_lblAvgAge);
		
		lblAvgAgeText = new JLabel("");
		GridBagConstraints gbc_lblAvgAgeText = new GridBagConstraints();
		gbc_lblAvgAgeText.anchor = GridBagConstraints.WEST;
		gbc_lblAvgAgeText.insets = new Insets(5, 5, 5, 0);
		gbc_lblAvgAgeText.gridx = 2;
		gbc_lblAvgAgeText.gridy = 5;
		gamePannel.add(lblAvgAgeText, gbc_lblAvgAgeText);
		
		JLabel lblAvgScent = new JLabel("Average scent range:");
		lblAvgScent.setOpaque(true);
		lblAvgScent.setBackground(Color.GRAY);
		GridBagConstraints gbc_lblAvgScent = new GridBagConstraints();
		gbc_lblAvgScent.anchor = GridBagConstraints.WEST;
		gbc_lblAvgScent.insets = new Insets(5, 5, 5, 5);
		gbc_lblAvgScent.gridx = 1;
		gbc_lblAvgScent.gridy = 6;
		gamePannel.add(lblAvgScent, gbc_lblAvgScent);
		
		lblAvgScentText = new JLabel("");
		GridBagConstraints gbc_lblAvgScentText = new GridBagConstraints();
		gbc_lblAvgScentText.anchor = GridBagConstraints.WEST;
		gbc_lblAvgScentText.insets = new Insets(5, 5, 5, 0);
		gbc_lblAvgScentText.gridx = 2;
		gbc_lblAvgScentText.gridy = 6;
		gamePannel.add(lblAvgScentText, gbc_lblAvgScentText);
		
		JLabel lblEnergyConsumption = new JLabel("Energy consumption:");
		lblEnergyConsumption.setOpaque(true);
		lblEnergyConsumption.setBackground(Color.CYAN);
		GridBagConstraints gbc_lblEnergyConsumption = new GridBagConstraints();
		gbc_lblEnergyConsumption.anchor = GridBagConstraints.WEST;
		gbc_lblEnergyConsumption.insets = new Insets(5, 5, 5, 5);
		gbc_lblEnergyConsumption.gridx = 1;
		gbc_lblEnergyConsumption.gridy = 7;
		gamePannel.add(lblEnergyConsumption, gbc_lblEnergyConsumption);
		
		lblEnergyConsumptionText = new JLabel("");
		GridBagConstraints gbc_lblEnergyConsumptionText = new GridBagConstraints();
		gbc_lblEnergyConsumptionText.anchor = GridBagConstraints.WEST;
		gbc_lblEnergyConsumptionText.insets = new Insets(5, 5, 5, 0);
		gbc_lblEnergyConsumptionText.gridx = 2;
		gbc_lblEnergyConsumptionText.gridy = 7;
		gamePannel.add(lblEnergyConsumptionText, gbc_lblEnergyConsumptionText);
		
		JButton btnShowGraph = new JButton("Show Graph");
		btnShowGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new GraphBuilder(gameloop.getData().getTimeArray(), gameloop.getData().getDataArray()
						,1000, 800, new String [] {"Time", ""}, false);
			}

	
		});
		GridBagConstraints gbc_btnShowGraph = new GridBagConstraints();
		gbc_btnShowGraph.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnShowGraph.insets = new Insets(5, 5, 5, 5);
		gbc_btnShowGraph.gridx = 2;
		gbc_btnShowGraph.gridy = 11;
		gamePannel.add(btnShowGraph, gbc_btnShowGraph);
		
		JLabel lblNrFood = new JLabel("Regen of food:");
		GridBagConstraints gbc_lblNrFood = new GridBagConstraints();
		gbc_lblNrFood.anchor = GridBagConstraints.WEST;
		gbc_lblNrFood.insets = new Insets(5, 5, 5, 5);
		gbc_lblNrFood.gridx = 1;
		gbc_lblNrFood.gridy = 12;
		gamePannel.add(lblNrFood, gbc_lblNrFood);
		
		txtNumberFood = new JTextField();
		txtNumberFood.setText(FOOD_REGENERATION_RATE + "");
		GridBagConstraints gbc_txtNumberFood = new GridBagConstraints();
		gbc_txtNumberFood.anchor = GridBagConstraints.WEST;
		gbc_txtNumberFood.weightx = 1.0;
		gbc_txtNumberFood.insets = new Insets(0, 0, 5, 0);
		gbc_txtNumberFood.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNumberFood.gridx = 2;
		gbc_txtNumberFood.gridy = 12;
		gamePannel.add(txtNumberFood, gbc_txtNumberFood);
		txtNumberFood.setColumns(10);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startTimer();
			}
		});
		
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnStart.insets = new Insets(5, 5, 5, 5);
		gbc_btnStart.gridx = 1;
		gbc_btnStart.gridy = 10;
		gamePannel.add(btnStart, gbc_btnStart);
		
		JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTimer();
			}
		});
		GridBagConstraints gbc_btnPause = new GridBagConstraints();
		gbc_btnPause.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPause.insets = new Insets(5, 5, 5, 0);
		gbc_btnPause.gridx = 2;
		gbc_btnPause.gridy = 10;
		gamePannel.add(btnPause, gbc_btnPause);
		
		JLabel lblTimeElapsed = new JLabel("Time elapsed:");
		GridBagConstraints gbc_lblTimeElapsed = new GridBagConstraints();
		gbc_lblTimeElapsed.anchor = GridBagConstraints.WEST;
		gbc_lblTimeElapsed.weightx = 1.0;
		gbc_lblTimeElapsed.insets = new Insets(5, 5, 5, 5);
		gbc_lblTimeElapsed.gridx = 1;
		gbc_lblTimeElapsed.gridy = 8;
		gamePannel.add(lblTimeElapsed, gbc_lblTimeElapsed);
		
		lblTime = new JLabel("");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.anchor = GridBagConstraints.WEST;
		gbc_lblTime.insets = new Insets(5, 5, 5, 0);
		gbc_lblTime.weightx = 1.0;
		gbc_lblTime.gridx = 2;
		gbc_lblTime.gridy = 8;
		gamePannel.add(lblTime, gbc_lblTime);
		
		f.pack();
		f.setVisible(true);
	}
	
	private void startTimer() {
		if (timer == null) {
			if(runGUI) {
				lblNrSpeciesText.setText(panel.getEnvironment().getNrSpecies() + "");
			}
			this.gameloop = new GameLoop(panel,txtNumberFood, this);
			timer = new Timer(UPDATE_TIME, gameloop);
			timer.start();
		}
		else {
			timer.start();
		}
	}
	
	private void stopTimer() {
		timer.stop();
	}
	
	private void newGame() {
		// TODO Auto-generated method stub
		
	}
	
	//broken needs fixing
	private void restartGame() {
		if (timer != null) {
			timer.stop();
			f.removeAll();
			initGUI();
			panel.repaint();
			lblNrSpeciesText.setText(panel.getEnvironment().getNrSpecies() + "");
			this.gameloop = new GameLoop(panel,txtNumberFood, this);
			timer.restart();
			}
	}
	
	public void updateLabels(String [] textArray) {
		this.lblNrSpeciesText.setText(textArray[0]);
		this.lblAvgSpeedText.setText(textArray[1]);
		this.lblAvgSizeText.setText(textArray[2]);
		this.lblAvgAgeText.setText(textArray[3]);
		this.lblAvgScentText.setText(textArray[4]);
		this.lblEnergyConsumptionText.setText(textArray[5]);
		this.lblTime.setText(textArray[6]);
	}
	
	public Environment getEnvironment() {
		return environment;
	}

	public GameLoop getGameloop() {
		return gameloop;
	}
}


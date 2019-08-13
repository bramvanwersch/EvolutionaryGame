package guilessRuns;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import gui.SidePanelGui;
import gui.TerrainPanel;
import simulation.Environment;
import simulation.Population;
import simulation.PopulationData;

public class BlankGameLoop implements ActionListener {
	private Environment environment;
	private int timeElapsed;
	private int foodRegenTxt;
	private boolean isSimulationFinished;
	private int updateTime;
	private Integer runCount;
	private PopulationData popData[];
	
	public BlankGameLoop(int txtFoodRegen, Environment enviroment, int updateTime) {
		this.environment = enviroment;
		this.foodRegenTxt = txtFoodRegen;
		this.popData = new PopulationData[environment.getPopulations().size()];
		this.timeElapsed = 0;
		this.isSimulationFinished = false;
		this.updateTime = updateTime;
		this.popData = new PopulationData[environment.getPopulations().size()];
		for (int i = 0; i < environment.getPopulations().size(); i ++) {
			this.popData[i] = new PopulationData();
		}
		environment.moveSpecies();
		runCount = 0;
		
		
	}
	
	/**
	 * Function that is invoked every time this class recieves an update. This is the main function to keep 
	 * the game running
	 */
	public void actionPerformed(ActionEvent e) {
		timeElapsed += updateTime;
		environment.nextTimeStep();
		environment.createFood(foodRegenTxt);
		addPopData();
		if (timeElapsed % 1000 == 0) {
			System.out.println("Age step taken");
			System.out.println(this.environment.getPopulations().size());
			if (timeElapsed != 0) {
				environment.addAge();
			}
			addPopData();
			if (checkIfSoleSurvivor(e)) {
				System.out.println("Dying is not an option");
				this.isSimulationFinished = true;
				runCount += 1 ;
			
			}
		}
	}
	
	/**
	 * Function that is evoked every second to record data points for every stat of the species and time.
	 */
	private void addPopData() {
		for (int i = 0; i < environment.getPopulations().size(); i ++) {
			Population sp = environment.getPopulations().get(i);
			popData[i].setAvgSpeed(sp.getSpeedStats()[0]);
			popData[i].setAvgSize(sp.getMaxSizeStats()[0]);
			popData[i].setAvgAge(sp.getMaxAgeStats()[0]);
			popData[i].setAvgScent(sp.getScentStats()[0]);
			popData[i].setAvgEnergyCost(sp.getEnergyConsumptionStats()[0]);
			popData[i].setTime(timeElapsed/1000);
			popData[i].setNrSpecies(sp.getNrSpecies());
		}
	}
	
	/**
	 * Function that will check if there are species alive. If there is one species alive the timer stops.
	 * Because these GUIless games are used to make a inference about good starting values.
	 * @param e: actionevent variable to stop the timer invoked in the Game class
	 * @return boolean telling if the game should be continued or stopped.
	 */
	private boolean checkIfSoleSurvivor(ActionEvent e) {
		Integer countDeadPopulation = getDeadPopulation();

		if (countDeadPopulation>= 1) {
			System.out.println("Timer stopped");
			Timer t  = (Timer) e.getSource();
			t.stop();
			return true;
		}
		return false;
	}
	
	
	
	public Integer getDeadPopulation() {
		Integer countDeadPopulation = 0;
		ArrayList<Population> pops = this.environment.getPopulations();
		for(int i = 0; i < pops.size() ; i++ ) {
			if(pops.get(i).getNrSpecies()==0) {
				countDeadPopulation += 1;
			}
		}
		
		return countDeadPopulation;
	}

	public PopulationData[] getData() {
		return popData;
	}
	public String getRunCountString() {
		return runCount.toString();
	}
	
	public boolean isSimulationFinished() {
		return isSimulationFinished;
	}


	
	

}
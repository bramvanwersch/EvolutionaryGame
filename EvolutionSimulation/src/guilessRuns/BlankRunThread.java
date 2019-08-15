package guilessRuns;



import java.awt.Color;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import gui.OptionData;
import simulation.Environment;
import simulation.Population;

public class BlankRunThread {
	private Timer timer;
	private Environment environment;
	private BlankGameLoop blankLoop;
	private OptionData optionData;
	public BlankRunThread() {
		this.optionData = makeOptionData();;
		environment = new Environment(optionData);
		blankLoop = new BlankGameLoop(50, environment, 10);
		timer = new Timer(10, blankLoop);
	//	Population population = getSoleSurvivor();
	//	double[] stats = population.getMaxAgeStats();
	//	for (int i =0 ; i < stats.length; i++ ) {
	//		System.out.println(stats[i]);
	//	}
		
	}
	
	



	public BlankGameLoop getBlankGameLoop() {
		return blankLoop;
	}
	
	
	
	public void startTimer() {
		timer.start();
	}
	public void stopTimer() {
		timer.stop();
	}
	private Population getSoleSurvivor() {
		ArrayList<Population> populations = environment.getPopulations();
		Population rightPopulation = null;
		for(int i =0; i<populations.size(); i++) {
			if(populations.get(i).getNrSpecies()>0) {
				rightPopulation = populations.get(i);
			}
		}
		return rightPopulation;
	}
	private OptionData makeOptionData() {
		OptionData optionData = new OptionData();
		optionData.setFoodEnergy(100);
		optionData.setFoodSize(5);
		
		optionData.addColorsList(new Color(66,66,66));
		optionData.addEatSizeFactorsList(1);
		optionData.addMaxAgesList(5);
		optionData.addNamesList("Brams");
		optionData.addNoIndividualsList(1);
		optionData.addScentRangesList(10);
		optionData.addSizesList(50);
		optionData.addSpeedsList(10);
		optionData.addTypeList("Herbivore");
		
		optionData.addColorsList(new Color(66,66,66));
		optionData.addEatSizeFactorsList(0);
		optionData.addMaxAgesList(8);
		optionData.addNamesList("Wytzeus");
		optionData.addNoIndividualsList(1);
		optionData.addScentRangesList(10);
		optionData.addSizesList(50);
		optionData.addSpeedsList(10);
		optionData.addTypeList("Carnivore");
		return optionData;
	}
	
	
//	private void writeSoleSurvivor(Population survivor) {
//		FileWriter filewriter = new FileWriter("DataDocument.txt");
//	}
	
}



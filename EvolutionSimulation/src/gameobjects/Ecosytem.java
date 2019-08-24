package gameobjects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import environment.Environment;
import user_input.OptionData;

public class Ecosytem {
	private int plantEnergy;
	private int plantSize;
	private ArrayList<Population> populations;
	private ArrayList<Plant> plantList;
	private int[] popOrderSeed;
	private PopulationData averagePopData;
	private Environment environment;

	public Ecosytem(OptionData options, Environment environment) {
		this.plantList = new ArrayList<Plant>();
		this.populations = new ArrayList<Population>();
		this.plantEnergy = options.getPlantEnergy();
		this.plantSize = options.getPlantSize();
		this.popOrderSeed = createPopOrderSeed(options.getNoIndividuals().length);
		this.averagePopData = new PopulationData();
		this.averagePopData.setReduce(true);
		this.environment = environment;
		createAnimalPopulations(options.getNoIndividuals().length, options.getColors(), options.getTypes(), options.getNames());
		createSpecies(options.getNoIndividuals(), options.getSizes(), options.getSpeeds(), options.getMaxAges(), 
				options.getScentRanges(), options.getEatSizeFactors());
		//TODO: Make sure that this has a proper feedback mechanism.
		createPlants(50);
	}
	
	/**
	 * Container function for invoking methods that need to be updated every frame for each species in a population
	 */
	public void nextTimeStep() {

		checkAliveSpecies();
		checkAge();
		moveSpecies();
		eatPlants();
		
		eatTimeCheck();
		eatSpecies();
		checkCanMultiply();
		shuffleLists();
	}
	
// methods that need checking every frame.

	/**
	 * Method that creates an array that is the lenght of the popultion list that helps rendomize the selection of
	 * populations by functions This has to be done this way to make sure populations are chosen at random but are not 
	 * actualy shuffled which leads to problems in the data collection.
	 * @return
	 */
	private int[] createPopOrderSeed(int numberOfPopulations) {
		int[] numberArray = new int[numberOfPopulations];
		for (int i = 0; i < numberOfPopulations; i++ ) {
			numberArray[i] = i;
		}
		return shufflePopOrderSeed(numberArray);
	}

	/**
	 * Function for invoking the checkAliveSpecies for every species in a population.
	 */
	public void checkAliveSpecies() {
		for (int loc : popOrderSeed) {
			Population sp =  populations.get(loc);
			sp.checkAliveSpecies();
		}
	}
	
	/**
	 * Function for moving all the species. First is checked if a species is in range of the scent of another species if this is the case
	 * scentmovement is used to move. Otherwise normal movement will be used to move.
	 */
	public void moveSpecies() {
		for (int loc : popOrderSeed) {
			Population sp =  populations.get(loc);
			for (int i = 0; i < sp.getNrSpecies(); i++) {
				Species s = sp.getSpecies(i);
				Species closestCarnivore = null;
				Species closestHerbivore = null;
				if (sp.getType().equals("Herbivore")){
					closestCarnivore = checkHerbivoreScent(s);
					if (closestCarnivore != null) {
						s.scentMovement(closestCarnivore.getxLoc(), closestCarnivore.getyLoc());
					}
					else {
						s.move();
					}
				}
				else if (sp.getType().equals("Carnivore")){
					closestHerbivore = checkCarnivoreScent(s);
					if (closestHerbivore != null) {
						s.scentMovement(closestHerbivore.getxLoc(), closestHerbivore.getyLoc());
					}
					else {
						s.move();
					}
				}
				else {
					s.move();
				}
			}
		}	
	}
	
	/**
	 * Function to check for the closest carnivore that is bigger then the herbivore so scent movement can
	 * be used to move away from it. 
	 * @param s1: the herbivore.
	 * @return the closest carnivore or null if no carnivore is in range of the scent.
	 */
	public Species checkHerbivoreScent(Species s1) {
		Species closestCarnivore = null;
		double lowestC = s1.getScentRange();
		for (int i = 0; i < getAllCarnivores().size(); i++) {
			Species s2 = getAllCarnivores().get(i);
			//getting slope of triangle using pythagoras.
			if (Math.sqrt(Math.pow(s1.getxLoc() - s2.getxLoc(), 2) + Math.pow(s1.getyLoc() - s2.getyLoc(), 2)) 
					< lowestC && s2.getSize() > s1.getEatSizeFactor()* s1.getSize()) {
				closestCarnivore = s2;
				lowestC = Math.sqrt(Math.pow(s1.getxLoc() - s2.getxLoc(), 2) + Math.pow(s1.getyLoc() - s2.getyLoc(), 2)); 
			}
		}
		return closestCarnivore;
	}
	
	/**
	 * Function to check for the closest herbivore that is smaller then the carnivore so scent movement can
	 * be used to move towards it. 
	 * @param s1: the carnivore.
	 * @return the closest herbivore or null if no herbivore is in range of the scent.
	 */
	public Species checkCarnivoreScent(Species s1) {
		Species closestHerbivore = null;
		double lowestC = s1.getScentRange();
		for (int i = 0; i < getAllHerbivores().size(); i++) {
			Species s2 = getAllHerbivores().get(i);
			//getting slope of triangle using pythagoras.
			if (Math.sqrt(Math.pow(s1.getxLoc() - s2.getxLoc(), 2) + Math.pow(s1.getyLoc() - s2.getyLoc(), 2)) 
					< lowestC && s2.getSize() < s1.getEatSizeFactor()* s1.getSize()) {
				closestHerbivore = s2;
				lowestC = Math.sqrt(Math.pow(s1.getxLoc() - s2.getxLoc(), 2) + Math.pow(s1.getyLoc() - s2.getyLoc(), 2)); 
			}
		}
		return closestHerbivore;
	}
	
	/**
	 * Function for plant eaters to check if there bounding box is on top of a food object. If this is the 
	 * case the food is consumed and the species gets energy
	 */
	public void eatPlants() {
		for (int i = 0; i < getAllHerbivores().size() + getAllOmnivores().size(); i++) {
			Species s = getAllPlantEaters().get(i);
			for(int j = getNrPlant() - 1; j >= 0; j--) {
				Plant f = getPlant(j);
				if (s.foodEaten(f.getxLoc(), f.getyLoc(), f.getSize(), f.getEnergy())) {
					removePlant(j);
				}
			}
		}	
	}
	
	/**
	 * Function for meat eaters to figure out if theire bounding box is on top of a herbivore. If this is
	 * the case the herbivore will be removed.
	 */
	public void eatSpecies() {
		for(int i = 0; i < getAllCarnivores().size() + getAllOmnivores().size(); i++) {
			for (int loc : popOrderSeed) {
				Population sp =  populations.get(loc);
				if (sp.getType().equals("Herbivore")) {
					for(int j = sp.getNrSpecies() - 1; j >= 0; j--){
						Species s1 = getAllMeatEaters().get(i);
						Species s2 = sp.getSpecies(j);
						if (s1.getSize() > s2.getSize() * s1.getEatSizeFactor()) {
							if (s1.foodEaten(s2.getxLoc(), s2.getyLoc(), s2.getSize(), s2.getEnergy())) {
								sp.removeSpecies(j);
								if (i != 0) {
									i--;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void eatTimeCheck() {
		for (Species s : getAllCarnivores()) {
			s.eatTimeCheck();
		}
	}
	
	/**
	 * Function that invokes a function for every population that checks if species are eligible for 
	 * multiplication
	 */
	public void checkCanMultiply() {
		for (int loc : popOrderSeed) {
			Population sp =  populations.get(loc);
			sp.checkCanMultiply();
		}
	}
	
	/**
	 * Function for checking is species are older then theire max age. If this is the case the species will
	 * die and be removed. Otherwise the age of the species is increased.
	 * Note: this method is only invoked once every second.
	 */
	public void addAge() {
		for (int loc : popOrderSeed) {
			Population sp =  populations.get(loc);
			for (int i = 0; i < sp.getNrSpecies(); i++) {
				Species s = sp.getSpecies(i);
				s.addAge();
			}
		}
	}
	
	public void checkAge() {
		for (int loc : popOrderSeed) {
			Population sp =  populations.get(loc);
			for (int i = 0; i < sp.getNrSpecies(); i++) {
				Species s = sp.getSpecies(i);
				if (s.getAge() >= s.getMaxAge()) {
					sp.removeSpecies(i);
				}
			}
		}
	}
	
	/**
	 * Function that shuffles the species and food list making sure that checks that are biased by list order
	 * are less biased. Besided that a seed that is a list of integer locations is shuffled making sure the popultions
	 * are looped trough in a random order but dont change order
	 */
	public void shuffleLists() {
		for (Population sp: populations ) {
			sp.shuffleSpeciesList();
		}
		Collections.shuffle(plantList);
		this.popOrderSeed = shufflePopOrderSeed(popOrderSeed);
	}
	
	private int[] shufflePopOrderSeed(int[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--){
			int index = rnd.nextInt(i + 1);
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
		return ar;
	}

	private void createAnimalPopulations(int nrPopulations, Color[] colors, String[] type, String[] names) {
		for (int i = 0; i < nrPopulations; i++) {
			AnimalPopulation p = new AnimalPopulation(colors[i], type[i], names[i]);
			populations.add(p);
		}
	}

//methods for innitialy creating species that are specified.
	public void createSpecies(int[] nrSpecies, int[] size, int[] speed, int[] maxAge, int[] scentRange
			, double[] eatSizeFactor) {
		for (int i = 0; i <populations.size(); i++) {
			Population p = populations.get(i);
			for (int j = 0; j < nrSpecies[i]; j++) {
				Species s = null;
				if (p.getType().equals("Carnivore")) {
					if (p.getNrSpecies() == 0) {
						s = new Carnivore(size[i], speed[i], maxAge[i], scentRange[i], eatSizeFactor[i]);
						p.addSpeciesData(s, -1);
					}
				}
				else if (p.getType().equals("Herbivore")) {
					if (p.getNrSpecies() == 0) {
						s = new Herbivore(size[i], speed[i], maxAge[i], scentRange[i], eatSizeFactor[i]);
						p.addSpeciesData(s, -1);
					}
				}
				else if(p.getType().equals("Omnivore")) {
					if (p.getNrSpecies() == 0) {
						s = new Omnivore(size[i], speed[i], maxAge[i], scentRange[i], eatSizeFactor[i]);
						p.addSpeciesData(s, -1);
					}
				}
				if (s == null){
					p.multiplySpecies(p.getNrSpecies()-1, false);
				}
				else if (!checkSpeciesPlacement(s)) {
					j--;
				}
				else{
					p.addSpecies(s);
				}
			}
		}
	}
	
	private boolean checkSpeciesPlacement(Species spec) {
		for (int loc : popOrderSeed) {
			Population sp =  populations.get(loc);
			for (int i = 0; i < sp.getNrSpecies(); i++ ) {
				Species s = sp.getSpecies(i);
				//check if the central point of the species just created is witin another species or not. if so move it.
				if (s.getxLoc() - s.getSize() < spec.getxLoc() && s.getxLoc() +s.getSize() > spec.getxLoc() &&
					s.getyLoc() - s.getSize() < spec.getyLoc() && s.getyLoc() +s.getSize() > spec.getyLoc()) {
					return false;
				}
			}
		}
		return true;
	}

// methods for food managing methods.
	public void createPlants(int nrPLants) {
		for (int i = 0; i < nrPLants; i++) {
			plantList.add(new Plant(plantEnergy, plantSize));
		}	
	}
	
	public int getNrPlant() {
		return plantList.size();
	}
	
	public Plant getPlant(int index) {
		return plantList.get(index);
	}
	
	public void removePlant(int index) {
		plantList.remove(index);
	}

// methods for getting certain collections of species from populations.
	private ArrayList<Species> getAllSpecies() {
		ArrayList<Species> specList = getAllCarnivores();
		specList.addAll(getAllOmnivores());
		specList.addAll(getAllHerbivores());
		return specList;
	}
	
	private ArrayList<Species> getAllMeatEaters() {
		ArrayList<Species> meatList = getAllCarnivores();
		meatList.addAll(getAllOmnivores());
		return meatList;
	}
	
	private ArrayList<Species> getAllPlantEaters() {
		ArrayList<Species> greenList = getAllHerbivores();
		greenList.addAll(getAllOmnivores());
		return greenList;
	}
	
	private ArrayList<Species> getAllCarnivores() {
		ArrayList<Species> specList = new ArrayList<Species>();
		for (int loc : popOrderSeed) {
			Population sp =  populations.get(loc);
			if (sp.getType().equals("Carnivore")) {
				for (int i = 0; i < sp.getNrSpecies(); i++) {
					specList.add(sp.getSpecies(i));
				}
			}
		}
		return specList;
	}
	
	private ArrayList<Species> getAllOmnivores() {
		ArrayList<Species> specList = new ArrayList<Species>();
		for (int loc : popOrderSeed) {
			Population sp =  populations.get(loc);
			if (sp.getType().equals("Omnivore")) {
				for (int i = 0; i < sp.getNrSpecies(); i++) {
					specList.add(sp.getSpecies(i));
				}
			}
		}
		return specList;
	}
	
	private ArrayList<Species> getAllHerbivores() {
		ArrayList<Species> specList = new ArrayList<Species>();
		for (int loc : popOrderSeed) {
			Population sp =  populations.get(loc);
			if (sp.getType().equals("Herbivore")) {
				for (int i = 0; i < sp.getNrSpecies(); i++) {
					specList.add(sp.getSpecies(i));
				}
			}
		}
		return specList;
	}

// metthods for getting any amount of species
	public int getNrHerbivores() {
		return getAllHerbivores().size();
	}

	public int getNrCarnivores() {
		return getAllCarnivores().size();
	}

	public int getNrOmnivores() {
		return getAllOmnivores().size();
	}

	public int getNrSpecies() {
		int count = 0;
		for (Population sp: populations ) {
			count += sp.getNrSpecies();
		}
		return count;
	}
	
	public int getAllDeadSpecies() {
		int count = 0;
		for (Population sp: populations ) {
			count += sp.getDiedSpecies();
		}
		return count;
	}
	
	public ArrayList<Population> getPopulations() {
		return populations;
	}
	
	private ArrayList<Population> getLivingPopulations() {
		ArrayList<Population> livingPopulations = new ArrayList<Population>();
		for (Population sp: populations) {
			if (sp.getNrSpecies() > 0) {
				livingPopulations.add(sp);
			}
		}
		return livingPopulations;
	}
	
	public Population getMaxNrSpeciesPop() {
		Population maxPopulation = populations.get(0);
		for (int i = 1; i < populations.size(); i++) {
			if (populations.get(i).getNrSpecies() > maxPopulation.getNrSpecies()) {
				maxPopulation = populations.get(i);
			}
		}
		return maxPopulation;
	}
	public Population getMinNrSpeciesPop() {
		Population minPopulation = populations.get(0);
		for (int i = 1; i < populations.size(); i++) {
			if (populations.get(i).getNrSpecies() < minPopulation.getNrSpecies()) {
				minPopulation = populations.get(i);
			}
		}
		return minPopulation;
	}
	
	public PopulationData[] getAllPopData() {
		PopulationData[] popDataArray = new PopulationData[populations.size()];
		for (int i = 0; i < populations.size(); i++) {
			popDataArray[i] = populations.get(i).getPopData();
		}
		return popDataArray;
	}
	
	public PopulationData getAveragePopData() {
		return this.averagePopData;
	}
	
	public Environment getEnvironment() {
		return this.environment;
	}

	
/*
 * Data saving functions	
 */
	public void saveAveragePopulationsStatsData(int timeElapsed) {
		double[][] averageStats = getAveragePopulationStats();
		averagePopData.setNrHerbivores(getNrHerbivores());
		averagePopData.setNrOmnivores(getNrOmnivores());
		averagePopData.setNrCarnivores(getNrCarnivores());
		averagePopData.setSpeedStats(averageStats[0]);
		averagePopData.setSizeStats(averageStats[1]);
		averagePopData.setAgeStats(averageStats[2]);
		averagePopData.setScentStats(averageStats[3]);
		averagePopData.setEnergyCostStats(averageStats[4]);
		averagePopData.setTime(timeElapsed/1000);
	}
	
	public double[][] getAveragePopulationStats() {
		double[][] valArray = new double[5][getLivingPopulations().size()];
		
		for (int i = 0; i < getLivingPopulations().size(); i++) {
			double[][] attributes = getLivingPopulations().get(i).getStats();
			for (int j = 0; j < attributes.length; j++) {
				valArray[j][i] = attributes[j][0];
			}
		}
		double[][] finalArray = new double[5][];
		for (int k = 0; k < finalArray.length; k++) {
			double[] attribute = valArray[k];	
			int[] minMax = calcMinMax(attribute);
			finalArray[k] = new double[]{calcAvgAttribute(attribute), minMax[0], minMax[1]};
		}
		return finalArray;
	}
	

	public double calcAvgAttribute(double[] attrArray) {
		double total = 0;
		for(double arr : attrArray){
			total += arr;
		}
		return total/new Double(attrArray.length);
	}
	
	private int[] calcMinMax(double[] attrArray) {
		int[] minMax = {(int) attrArray[0],(int) attrArray[0]};
		for(int i = 0; i < attrArray.length; i++){
			if (attrArray[i] < minMax[0]) {
				minMax[0] = (int)attrArray[i];
			}
			else if (attrArray[i] > minMax[1]) {
				minMax[1] = (int)attrArray[i];
			}
		}
		return minMax;
	}
}
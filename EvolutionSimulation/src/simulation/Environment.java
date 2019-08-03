package simulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import gui.OptionData;

public class Environment {
	private int DEFAULT_FOOD_E = 100;
	private int DEFAULT_FOOD_SIZE = 5;
	private double EAT_SIZE_FACTOR = 1;
	private ArrayList<Population> populations;
	private ArrayList<Food> foodList;
	
	/**
	 * Class that will manage species populations. It contains methods that affect all species and directs underlying population
	 * classes to manage their respective species.
	 * @param nrSpecies
	 * @param size
	 * @param speed
	 * @param maxAge
	 * @param colors
	 * @param type
	 * @param nrFood
	 */
	public Environment(OptionData options) {
		this.foodList = new ArrayList<Food>();
		this.populations = new ArrayList<Population>();
		createPopulations(options.getNoIndividuals().length, options.getColors(), options.getTypes());
		createSpecies(options.getNoIndividuals(), options.getSizes(), options.getSpeeds(), options.getMaxAges(), 
				options.getNames(), options.getEatSizeFactors());
		//TODO: fill in
		createFood(50);
	}
	
	/**
	 * Container function for invoking methods that need to be updated every frame.
	 */
	public void nextTimeStep() {
		checkAliveSpecies();
		moveSpecies();
		eatFood();
		eatSpecies();
		checkCanMultiply();
		shuffleLists();
	}
	
// methods that need checking every frame.
	/**
	 * Function for invoking the checkAliveSpecies for every population.
	 */
	public void checkAliveSpecies() {
		for (Population sp: populations ) {
			sp.checkAliveSpecies();
		}
	}
	
	/**
	 * Function for moving all the species. First is checked if a species is in range of the scent of another species if this is the case
	 * scentmovement is used to move. Otherwise normal movement will be used to move.
	 */
	public void moveSpecies() {
		for (Population sp: populations ) {
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
					< lowestC && s2.getSize() > EAT_SIZE_FACTOR* s1.getSize()) {
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
					< lowestC && s2.getSize() < EAT_SIZE_FACTOR* s1.getSize()) {
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
	public void eatFood() {
		for (int i = 0; i < getAllHerbivores().size() + getAllOmnivores().size(); i++) {
			Species s = getAllPlantEaters().get(i);
			for(int j = getNrFood() - 1; j >= 0; j--) {
				Food f = getFood(j);
				if (s.foodEaten(f.getxLoc(), f.getyLoc(), f.getSize(), f.getEnergy())) {
					removeFood(j);
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
			for (Population sp: populations ) {
				if (sp.getType().equals("Herbivore")) {
					for(int j = sp.getNrSpecies() - 1; j >= 0; j--){
						Species s1 = getAllMeatEaters().get(i);
						Species s2 = sp.getSpecies(j);
						if (s1.getSize() > s2.getSize() * EAT_SIZE_FACTOR) {
							if (s1.checkCanEat(s2.getxLoc(), s2.getyLoc(), s2.getSize(), s2.getEnergy())) {
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
	
	/**
	 * Function that invokes a function for every population that checks if species are eligible for 
	 * multiplication
	 */
	public void checkCanMultiply() {
		for (Population sp: populations ) {
			sp.checkCanMultiply();
		}
	}
	
	/**
	 * Function for checking is species are older then theire max age. If this is the case the species will
	 * die and be removed. Otherwise the age of the species is increased.
	 * Note: this method is only invoked once every second.
	 */
	public void addCheckAge() {
		for (Population sp: populations ) {
			for (int i = 0; i < sp.getNrSpecies(); i++) {
				Species s = sp.getSpecies(i);
				if (s.getAge() <= s.getMaxAge()) {
					s.addRepTime();
					s.addAge();
				}
				else {
					sp.removeSpecies(i);
				}
			}
		}
	}
	
	/**
	 * Function to invoke the shuffle list function for each population. And to shuffle the foodlist.
	 * This is important to make sure that checks that loop trough lists are not biased towards the objects
	 * at the top of the lists.
	 */
	public void shuffleLists() {
		for (Population sp: populations ) {
			sp.shuffleSpeciesList();
		}
		Collections.shuffle(foodList);	
	}

	private void createPopulations(int nrPopulations, Color[] colors, String[] type) {
		for (int i = 0; i < nrPopulations; i++) {
			Population p = new Population(colors[i], type[i]);
			populations.add(p);
		}
	}

//methods for innitialy creating species that are specified.
	public void createSpecies(int[] nrSpecies, int[] size, int[] speed, int[] maxAge, String[] names, double[] eatSizeFactor) {
		for (int i = 0; i <populations.size(); i++) {
			Population p = populations.get(i);
			for (int j = 0; j < nrSpecies[i]; j++) {
				Species s = null;
				if (p.getType().equals("Carnivore")) {
					if (p.getNrSpecies() == 0) {
						s = new Carnivore(size[i], speed[i], maxAge[i], names[i], eatSizeFactor[i]);
						p.addSpeciesData(s, -1);
					}
				}
				else if (p.getType().equals("Herbivore")) {
					if (p.getNrSpecies() == 0) {
						s = new Herbivore(size[i], speed[i], maxAge[i], names[i], eatSizeFactor[i]);
						p.addSpeciesData(s, -1);
					}
				}
				else if(p.getType().equals("Omnivore")) {
					if (p.getNrSpecies() == 0) {
						s = new Omnivore(size[i], speed[i], maxAge[i], names[i], eatSizeFactor[i]);
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
		for (Population sp: populations ) {
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
	public void createFood(int nrFood) {
		for (int i = 0; i < nrFood; i++) {
			foodList.add(new Food(DEFAULT_FOOD_E, DEFAULT_FOOD_SIZE));
		}	
	}
	
	public int getNrFood() {
		return foodList.size();
	}
	
	public Food getFood(int index) {
		return foodList.get(index);
	}
	
	public void removeFood(int index) {
		foodList.remove(index);
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
		for (Population sp: populations ) {
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
		for (Population sp: populations ) {
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
		for (Population sp: populations ) {
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
	
/*
 * Methods for data class. These methods calculate max, min and average values for all species for a 
 * certain statistic. Probably is a better way of doing this.
 */
	public double[] getSpeedStats() {
		double[] valArray = new double[getNrSpecies()];
		for (int i = 0; i < getNrSpecies(); i++) {
			valArray[i] = getAllSpecies().get(i).getSpeed();
		}
		int[] minMax = calcMinMax(valArray);
		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
	}
	
	public double[] getSizeStats() {
		double[] valArray = new double[getNrSpecies()];
		for (int i = 0; i < getNrSpecies(); i++) {
			valArray[i] = getAllSpecies().get(i).getSize();
		}
		int[] minMax = calcMinMax(valArray);
		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
	}
	
	public double[] getMaxAgeStats() {
		double[] valArray = new double[getNrSpecies()];
		for (int i = 0; i < getNrSpecies(); i++) {
			valArray[i] = getAllSpecies().get(i).getMaxAge();
		}
		int[] minMax = calcMinMax(valArray);
		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
	}
	
	public double[] getScentStats() {
		double[] valArray = new double [getNrSpecies()];
		for (int i = 0; i < getNrSpecies(); i++) {
			valArray[i] = getAllSpecies().get(i).getScentRange() - getAllSpecies().get(i).getSize();
		}
		int[] minMax = calcMinMax(valArray);
		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
	}
	
	public double[] getEnergyConsumptionStats() {
		double[] valArray = new double[getNrSpecies()];
		for (int i = 0; i < getNrSpecies(); i++) {
			valArray[i] = getAllSpecies().get(i).getEnergyConsumption();
		}
		int[] minMax = calcMinMax(valArray);
		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
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
		for(int i = 0; i < getNrSpecies(); i++){
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

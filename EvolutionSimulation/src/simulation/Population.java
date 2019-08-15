package simulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import genome.Gene;
import genome.Genome;
import genome.PanGenome;

public class Population {
	private final double MUTATION_CHANCE = 0.005;
	private ArrayList<Species> speciesList;
	private int diedSpecies;
	private Color color;
	private String type;
	private PanGenome panGenome;
	private String speciesData;
	private String name;
	
	public Population(Color color, String type, String name) {
		this.speciesList = new ArrayList<Species>();
		this.diedSpecies = 0;
		this.color = color;
		this.type = type;
		this.speciesData = "";
		this.name = name;

		this.panGenome = new PanGenome(this.type +"Data");
	}
	
	public void addSpecies(Species s) {
		this.speciesList.add(s);
	}
	
	public int getNrSpecies() {
		return speciesList.size();
	}
	
	public Species getSpecies(int index) {
		return speciesList.get(index);
	}
	
	public void checkCanMultiply() {
		for (int i = 0; i < getNrSpecies(); i++) {
			if (getSpecies(i).isCanMultiply()) {
				multiplySpecies(i, true);
			}
		}
	}
	
	public void multiplySpecies(int index, boolean mutation) {
		Species s = speciesList.get(index);
		Genome genome = new Genome(s.getGenome().getPerfectGenes(), s.getGenome().getDNACode());
		int energy = s.getEnergy();
		if (mutation) {
			genome.mutateGenome(MUTATION_CHANCE);
			energy = s.halfEnergy();
		}
		genome.setGeneValues();
		Species sCopy = null;
		if (this.type.equals("Carnivore")) {
			if (genome.isSpeciesSurvivable()) {
				sCopy = new Carnivore(s.getxLoc(), s.getyLoc(),energy, genome, speciesList.size() + diedSpecies +1
						, s.getEatSizeFactor());
			}
		}
		else if (this.type.equals("Herbivore")) {
			if (genome.isSpeciesSurvivable()) {
				sCopy = new Herbivore(s.getxLoc(), s.getyLoc(),energy, genome, speciesList.size() + diedSpecies +1
						, s.getEatSizeFactor());
			}
		}
		else if (this.type.equals("Omnivore")) {
			if (genome.isSpeciesSurvivable()) {
				sCopy = new Omnivore(s.getxLoc(), s.getyLoc(),energy, genome, speciesList.size() + diedSpecies +1
						, s.getEatSizeFactor());
			}
		}
		if(sCopy != null) {
			speciesList.add(sCopy);
			addSpeciesData(sCopy, s.getNumber());
		}
	}

	public void removeSpecies(int index) {	
		speciesList.remove(index);
		diedSpecies += 1;
	}
	
	public int getDiedSpecies() {
		return diedSpecies;
	}

	public void shuffleSpeciesList() {
		Collections.shuffle(speciesList);
	}

	public Color getColor() {
		return this.color;
	}

	public String getType() {
		return this.type;
	}
	public void addSpeciesData(Species s, int prevNumber) {
		if (speciesData.isEmpty()) {
			for (String key : s.getGenome().getPerfectGenes().keySet()) {
				speciesData += "<"+ key + "\n"+s.getGenome().DnaToAa(s.getGenome().getPerfectGenes().get(key).getSequence())+"\n";
			}
		}
		speciesData += ">" +s.getNumber() +"<--"+ prevNumber + "\n" +s.getGenome().DnaToAa(s.getGenome().getDNACode())+"\n";
		if (speciesData.length() > 100000) {
			panGenome.writeSpeciesInfo(speciesData);
			speciesData = " ";
		}
	}

	public void checkAliveSpecies() {
		for (int i = 0; i < getNrSpecies(); i++) {
			if (getSpecies(i).getEnergy() <= 0) {
				removeSpecies(i);
			}
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	/*
 * Methods for data class. These methods calculate max, min and average values for all species for a 
 * certain statistic. Probably is a better way of doing this.
 */
	public double[][] getStats() {
		double[][] valArray = new double[5][getNrSpecies()];
		for (int i = 0; i < getNrSpecies(); i++) {
			double[] attributes = speciesList.get(i).getAttributeData();
			for (int j = 0; j < attributes.length; j++) {
				valArray[j][i] = attributes[j];
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
	
//	
//	public double[] getSpeedStats() {
//		double[] valArray = new double[getNrSpecies()];
//		for (int i = 0; i < getNrSpecies(); i++) {
//			valArray[i] = speciesList.get(i).getSpeed();
//		}
//		int[] minMax = calcMinMax(valArray);
//		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
//	}
//	
//	public double[] getMaxSizeStats() {
//		double[] valArray = new double[getNrSpecies()];
//		for (int i = 0; i < getNrSpecies(); i++) {
//			valArray[i] = speciesList.get(i).getMaxSize();
//		}
//		int[] minMax = calcMinMax(valArray);
//		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
//	}
//	
//	public double[] getMaxAgeStats() {
//		double[] valArray = new double[getNrSpecies()];
//		for (int i = 0; i < getNrSpecies(); i++) {
//			valArray[i] = speciesList.get(i).getMaxAge();
//		}
//		int[] minMax = calcMinMax(valArray);
//		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
//	}
//	
//	public double[] getScentStats() {
//		double[] valArray = new double [getNrSpecies()];
//		for (int i = 0; i < getNrSpecies(); i++) {
//			valArray[i] = speciesList.get(i).getScentRange() - speciesList.get(i).getSize();
//		}
//		int[] minMax = calcMinMax(valArray);
//		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
//	}
//	
//	public double[] getEnergyConsumptionStats() {
//		double[] valArray = new double[getNrSpecies()];
//		for (int i = 0; i < getNrSpecies(); i++) {
//			valArray[i] = speciesList.get(i).getEnergyConsumption();
//		}
//		int[] minMax = calcMinMax(valArray);
//		return new double[]{calcAvgAttribute(valArray), minMax[0], minMax[1]};
//	}
	
	public double calcAvgAttribute(double[] attrArray) {
		double total = 0;
		if (attrArray.length == 0) {
			return 0;
		}
		for(double arr : attrArray){
			total += arr;
		}
		return total/new Double(attrArray.length);
	}
	
	private int[] calcMinMax(double[] attrArray) {
		if (attrArray.length == 0) {
			return new int [] {0,0};
		}
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

package simulation;

import genome.Genome;

public class Carnivore extends Species{
	private String[] geneNames = {"size","speed","maxAge","scentRange"};
	private int chaseTime;
	private int MAX_CHASE_TIME = 2000;
	private final int MINIMUM_REP_TIME = 5;

	//innitial constructor
	public Carnivore(int size, int speed, int maxAge) {
		super(size, speed, maxAge);
		this.chaseTime = 0;
	}
	
	//inheriting constructor
	public Carnivore(int x, int y,int energy, Genome genome, int number) {
		super(x, y, energy, genome, number);
		this.chaseTime = 0;
	}
	
	public double getEnergyConsumption() {
		int r = getSize() / 2;
		double contentSurface = (1.33* Math.PI * Math.pow(r, 3)) /(4 * Math.PI * Math.pow(r, 2));
		return (Math.pow(1.25, contentSurface) - 1)* 0.3* getSpeed() + 0.125*(getScentRange() - getSize()) + getAge();
	}

	/**
	 * Function that will check if a eatable species is completely in the bounding box of the carnivore.
	 * If this is the case true is returned and the energy is added to the energy of the carnivore. This means
	 * that the species got eaten and will be removed from the game. The chase time will be set to almost
	 * max ensuring that the carnivore will rest for a while before attackign again.
	 * @param x the x coordinate of the eatable species
	 * @param y the y coordinate of the eatable species
	 * @param size the sSize the size of the eatable species
	 * @param sEnergy the energy of the eatable species
	 * @return boolean that tells if the species can be eaten or not.
	 */
	public boolean checkCanEat(int x, int y, int sSize, int sEnergy) {
		if (getxLoc() - 0.5 * getSize() < x && getxLoc() + 0.5 * getSize() - 0.5 * sSize > x 
				&& getyLoc() - 0.5 * getSize()  < y && getyLoc() + 0.5 * getSize() - 0.5 * sSize > y) {
			changeEnergy(sEnergy);
			this.chaseTime = MAX_CHASE_TIME -1;
			return true;
		}
		return false;
	}
	
	public void useScentToMove(int ix, int iy) {
		double y  = (double) iy;
		double x = (double) ix;
		if (getEnergy() > 0 && this.chaseTime < this.MAX_CHASE_TIME) {
			this.chaseTime += 50;
			double slopeLength = Math.sqrt(Math.pow(x - getxLoc(), 2) + Math.pow(y - getyLoc(), 2));
			//direction that is straigh away from the target
			changeXLoc((x - getxLoc())/ slopeLength *getSpeed());
			changeYLoc((y - getyLoc())/ slopeLength * getSpeed());
			double fd = Math.atan2((y - getyLoc())/ slopeLength, (x - getxLoc())/ slopeLength);
			double min = (fd - 0.25 * Math.PI);
			double max = (fd + 0.25 * Math.PI);
			setFacingDirection((Math.random() * (max - min)) + min);
			changeEnergy(-1*getEnergyConsumption());
		}
		else {
			move();
			if (this.chaseTime - 5 < this.MAX_CHASE_TIME) {
				this.chaseTime = 0;
			}
			else {
				this.chaseTime -= 5;
			}
		}
	}
	
	public String[] getGeneNames() {
		return this.geneNames;
	}
	
	public int getRepTime() { 
		return this.MINIMUM_REP_TIME;
	}
	
	/**
	 * Function that returns the size of an organism towards its maximum value generated by the genome depending on how old
	 * the organism is. The formula describing it looks like a michaels menten formula. But the start
	 * value is set to 25 procent of the maximum value.
	 */
	public int getSize() {
		return (int) (((getGenome().getGeneValue("size") - 0.5 * getGenome().getGeneValue("size")) * getAge()) /
				(getAge() + 5) + 0.5 * getGenome().getGeneValue("size"));
	}

}

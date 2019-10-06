package tests;

import java.awt.Color;
import java.util.Arrays;

import junit.framework.TestCase;
import user_input.OptionData;
import user_input.PopulationSettings;
import gameobjects.Ecosystem;

public class TestEcosystem extends TestCase {
	
	public OptionData createOptionData() {
		OptionData p = new OptionData();
		p.addPopulationSettings(new PopulationSettings("Plant", "a name", 5, 1, 1, 1, 1, Color.GREEN, 1.0));
		p.addPopulationSettings(new PopulationSettings("Carnivore", "a name", 5, 1, 1, 1, 1, Color.GREEN, 1.0));
		p.setPlantEnergy(100);
		p.setPlantSize(5);
		return p;
	}
	
	public void testCreatePopOrderSeed() {
		OptionData d = createOptionData();
		Ecosystem t = new Ecosystem(d);
		assertEquals(Arrays.toString(new int[] {0,1}), Arrays.toString(t.createPopOrderSeed(d.getPopulationSettingSize())));
	}
	
//	public void test
}
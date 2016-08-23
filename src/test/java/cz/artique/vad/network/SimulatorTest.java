package cz.artique.vad.network;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
@Ignore
public class SimulatorTest {

	private static final String SCENARIO = "src/test/resources/cz/artique/vad/scenario/";

	@DataPoints("jsons")
	public static String[] jsons = new String[] { SCENARIO + "s1.json" };

	@Theory
	public void testSimulation(@FromDataPoints("jsons") String json) throws FileNotFoundException {
		Simulator.main(new String[] { json });
	}
}

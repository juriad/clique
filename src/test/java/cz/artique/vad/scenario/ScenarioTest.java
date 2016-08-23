package cz.artique.vad.scenario;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
@Ignore
public class ScenarioTest {

	@DataPoints("jsons")
	public static String[] jsons = new String[] { "s1.json", "s2.json" };

	@Theory
	public void testFromFile(@FromDataPoints("jsons") String json) {
		InputStream s = this.getClass().getResourceAsStream(json);
		Scenario scenario = Scenario.fromFile(new InputStreamReader(s));
		System.out.println(scenario.getActions());
	}
}

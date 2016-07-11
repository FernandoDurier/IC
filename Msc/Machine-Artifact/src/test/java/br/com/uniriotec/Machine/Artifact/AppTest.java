package br.com.uniriotec.Machine.Artifact;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import br.com.uniriotec.process.model.ProcessModel;


/**
 * Unit test for simple App.
 */
public class AppTest {
	private IProcessModelFacade processModelFacade;
	
	@Before
	public void initializeTest(){
		processModelFacade = new ProcessMachineArtifactFacade();
	}
	
	@Test
	public void testProcessModelParser() throws CreateProcessModelException{
		URL is = ClassLoader.getSystemResource("Hotel_Service_English.json");
		ProcessModel pm = processModelFacade.readProcessModelFromJson(is.getPath());
		
		assertNotNull(pm);
	}
}

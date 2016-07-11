package br.com.uniriotec.Machine.Artifact;

import br.com.uniriotec.process.model.ProcessModel;

/***
 * Concrete implementation for the {@linkplain IProcessModelFacade}. <br>
 * The facade trigger the process for creating a new {@linkplain ProcessModel}, 
 * it delegates the creation task for the {@linkplain IProcessModelFactory}.
 * 
 * @author Raphael Rodrigues
 * @version 1.0.20140903
 */
public class ProcessMachineArtifactFacade implements IProcessModelFacade {
	private IProcessModelFactory processModelFactory;
	
	public ProcessMachineArtifactFacade(){
		processModelFactory = new ProcessModelFactory();
	}
	
	public ProcessModel readProcessModelFromJson(String filePath) throws CreateProcessModelException {
		return processModelFactory.createProcessModelFromJson(filePath);
	}

	public void readProcessModelFromXml(String filePath) throws CreateProcessModelException {
		processModelFactory.createProcessModelFromXml(filePath);
	}
}

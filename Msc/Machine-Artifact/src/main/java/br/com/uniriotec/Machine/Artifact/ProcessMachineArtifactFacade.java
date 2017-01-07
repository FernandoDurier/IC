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
                
                String resposta = " ";
                String port = "C:\\Users\\Cliente\\Desktop\\bolsa ic 2\\unirio-workspace\\Msc\\Machine-Artifact\\src\\test\\resources\\Hotel_Service_Portuguese.json";
                
                String eng = "C:\\Users\\Cliente\\Desktop\\bolsa ic 2\\unirio-workspace\\Msc\\Machine-Artifact\\src\\test\\resources\\Hotel_Service_English.json";
                
                String esp = "C:\\Users\\Cliente\\Desktop\\bolsa ic 2\\unirio-workspace\\Msc\\Machine-Artifact\\src\\test\\resources\\Hotel_Service_Spanish.json";
		
                if(filePath.equals("Hotel_Service_Portuguese.json")){
                resposta = port;
                }else if(filePath.equals("Hotel_Service_English.json")){
                resposta = eng;
                }
                else if(filePath.equals("Hotel_Service_Spanish.json")){
                resposta = esp;
                }
                
                return processModelFactory.createProcessModelFromJson(resposta);
	}

	public void readProcessModelFromXml(String filePath) throws CreateProcessModelException {
		processModelFactory.createProcessModelFromXml(filePath);
	}
}

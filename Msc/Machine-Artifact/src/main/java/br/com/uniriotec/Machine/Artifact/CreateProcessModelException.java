package br.com.uniriotec.Machine.Artifact;

/***
 * Exception that wraps problems that occurred while trying to create a 
 * {@linkplain ProcessModel} from a given file in a machine artifact format.
 * 
 * @author Raphael Rodrigues
 * @version 1.0.140903
 */
public class CreateProcessModelException extends Exception {
	private static final long serialVersionUID = 7094404806910545990L;
	
	public CreateProcessModelException(String message){
		super(message);
	}
	
	public CreateProcessModelException(String message, Throwable t){
		super(message, t);
	}
}

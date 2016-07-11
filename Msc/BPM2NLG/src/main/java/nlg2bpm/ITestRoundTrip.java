package nlg2bpm;

import general.language.common.realizer.RealizedText;
import br.com.uniriotec.process.model.ProcessModel;

public interface ITestRoundTrip {
	void runRoundTrip(RealizedText realizedText, ProcessModel process);
}

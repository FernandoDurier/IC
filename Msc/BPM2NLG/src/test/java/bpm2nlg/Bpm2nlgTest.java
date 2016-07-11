package bpm2nlg;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import general.language.common.realizer.RealizedText;
import nlg2bpm.ITestRoundTrip;

import org.junit.Before;
import org.junit.Test;



/***
 * Class developed for the executing tests with the pipeline. 
 * 
 * @author Raphael
 */
public class Bpm2nlgTest {
	private BpmnlgMain bpm2nlg;
	private ITestRoundTrip concreteRoundTripTest;
	
	@Before
	@SuppressWarnings("deprecation")
	public void initializeTest(){
		this.concreteRoundTripTest = new TestRoundTrip();
		this.bpm2nlg = new BpmnlgMain(concreteRoundTripTest);
	}
	
	@Test
	public void runPortuguesePipeline_Trival(){
		String expected = "O processo comeca quando o Professor elabora a prova. Entao, o aluno realiza a prova. Em seguida, o professor corrige a prova. Subsequentemente, o professor entrega a nota para a secretaria. Entao, a secretaria cadastra a nota no historico do aluno. Finalmente, o processo é terminado.";
		String pathToModel = ClassLoader.getSystemResource("JSON/I_Pt-v1.json").getPath();
		try {
			RealizedText generatedText = bpm2nlg.convertToModelToText(pathToModel);
			System.out.println(generatedText.getFormattedText());
			assertTrue(generatedText.isEquivalentTo(expected));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void runPortuguesePipeline_IntermediateWithSimpleXorGateway(){
		String pathToModel = ClassLoader.getSystemResource("JSON/II_Pt-XOR-v1.json").getPath();
		try {
			RealizedText generatedText = bpm2nlg.convertToModelToText(pathToModel);
			System.out.println(generatedText.getFormattedText());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void runPortuguesePipeline_IntermediateWithXorGateway(){
		String pathToModel = ClassLoader.getSystemResource("JSON/II_Pt-XOR-v2.json").getPath();
		try {
			RealizedText generatedText = bpm2nlg.convertToModelToText(pathToModel);
			System.out.println(generatedText.getFormattedText());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void runPortuguesePipeline_IntermediateWithAndGateway(){
		String expected = "O processo comeca quando o Secretario recebe o pedido de compra. Em seguida, os seguintes caminhos são executados. O Gerente atualiza a planilha. O Secretario atualiza o cadastro. Em seguida, o gerente finaliza o pedido. Finalmente, o processo é terminado.";
		String pathToModel = ClassLoader.getSystemResource("JSON/II_Pt-AND-v1.json").getPath();
		try {
			RealizedText generatedText = bpm2nlg.convertToModelToText(pathToModel);
			System.out.println(generatedText.getFormattedText());
			assertTrue(generatedText.isEquivalentTo(expected));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void runPortuguesePipeline_IntermediateWithAndGatewayV2(){
		String pathToModel = ClassLoader.getSystemResource("JSON/II_Pt-AND-v2.json").getPath();
		try {
			RealizedText generatedText = bpm2nlg.convertToModelToText(pathToModel);
			System.out.println(generatedText.getFormattedText());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void runPortuguesePipeline_IntermediateWithAndGatewayV3(){
		String pathToModel = ClassLoader.getSystemResource("JSON/II_Pt-AND-v3.json").getPath();
		try {
			RealizedText generatedText = bpm2nlg.convertToModelToText(pathToModel);
			System.out.println(generatedText.getFormattedText());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void runPortuguesePipeline_AdvancedHotel(){
		String expected = "O processo comeca quando o Gerente servico de quarto recebe o pedido. Entao, o processo é dividido em 3 ramificacoes paralelas. O Gerente servico de quarto entrega o pedido para o barman. Em seguida, os seguintes caminhos são executados. O Barman pega o vinho da adega. O Barman prepara as bebida alcoolica. O Gerente servico de quarto entrega o pedido para o garcom. Subsequentemente, o garcom prepara a nota. O Gerente servico de quarto submete o pedido para a cozinha. Entao, a cozinha prepara a comida. O Garcom entrega a para o quarto do hospede. Em seguida, o garcom retorna a para o servico de quarto. Subsequentemente, o garcom debita a da conta do hospede. Finalmente, o processo é terminado.";
		String pathToModel = ClassLoader.getSystemResource("JSON/III_Pt-Hotel-v1.json").getPath();
		try {
			RealizedText generatedText = bpm2nlg.convertToModelToText(pathToModel);
			System.out.println(generatedText.getFormattedText());
			assertTrue(generatedText.isEquivalentTo(expected));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
//	
//	@Test
//	public void runEnglishPipeline_Trival(){
//		String pathToModel = ClassLoader.getSystemResource("Process_Trivial.json").getPath();
//		try {
//			RealizedText generatedText = bpm2nlg.convertToModelToText(pathToModel);
//			System.out.println(generatedText.getFormattedText());
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
}

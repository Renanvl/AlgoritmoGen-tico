import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GATSP {

	private static final String endereco = "Instancias GA";

	private static Integer numeroIndividuos = 0;

	public static void main(String[] args) {
		try {
			String nomeArquivo ;
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			nomeArquivo = "si1032.tsp";/* input.readLine(); */
			BufferedReader arquivoLeitura = new BufferedReader(new FileReader(endereco + "\\" + nomeArquivo));
			String linha = arquivoLeitura.readLine();
			String SomaLinhas = "";
			ArrayList<Integer> elementosMatriz = new ArrayList<Integer>();
			int contLinha = 1;
			
			System.out.println("Insira o nome do arquivo de dados (ex.: dados.txt):");
			
			numeroIndividuos = identificarTamanhoMatriz(nomeArquivo);
			while (linha != null) {
				String[] elemento = linha.trim().split(" ");
				if (contLinha > 7) {
					for (int x = 0; x < elemento.length; x++) {
						if (elemento[x].equals("EOF")) {
							break;
						}
						elementosMatriz.add(Integer.parseInt(elemento[x]));
					}
				}
			//	System.out.println(linha);
				contLinha++;
				linha = arquivoLeitura.readLine();
			}
			int[] melhorCaminho;
			ManipuladorMatriz mm = new ManipuladorMatriz(numeroIndividuos);
			AlgoritmoGenetico ga = AlgoritmoGenetico.getInstance();
			melhorCaminho = ga.tsp(mm.montarMatriz(elementosMatriz,nomeArquivo));
			int distaanciaTotal = mm.somaDist(melhorCaminho,mm.montarMatriz(elementosMatriz,nomeArquivo));
			System.out.print("Melhor Caminho: ");
	        for (int i = 0; i < melhorCaminho.length; i++) {
	            System.out.print(melhorCaminho[i] + "-");
	        }
	        System.out.println(melhorCaminho[0]);
			
	        System.out.println(distaanciaTotal);
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Integer identificarTamanhoMatriz(String nomeArquivo) {
		Integer tamanho = 0;
		String tamanhoString = "";
		char elementoString;
		for (int i = 2; i <= nomeArquivo.length(); i++) {
			elementoString = nomeArquivo.charAt(i);
			if (elementoString == '.') {
				break;
			}
			tamanhoString += nomeArquivo.charAt(i);
		}
		tamanho = Integer.parseInt(tamanhoString);
		return tamanho;
	}

	public ArrayList<Integer> tsp(int[][] matriz) {
		ArrayList<Integer> melhoresElementos = new ArrayList<Integer>();
		return melhoresElementos;
	}

	public Integer getNumeroIndividuos() {
		return numeroIndividuos;
	}

	public static void setNumeroIndividuos(Integer numeroIndividuos) {
		GATSP.numeroIndividuos = numeroIndividuos;
	}

}

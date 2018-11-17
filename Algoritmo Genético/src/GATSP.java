import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GATSP {

	private static final String endereco = "D:\\Documentos\\Instancias GA";

	private static Integer n = 0;
 

	public static void main(String[] args) {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			// System.out.println("Insira o endereço da pagina: ");
			// private static final String endereco = "C:\\Users\\yago\\Desktop\\tgw162";
			// String endereco = br.readLine();
			System.out.println("Insira o nome do arquivo de dados (ex.: dados.txt):");

			String nomeArquivo;

			nomeArquivo = "gr17.tsp";/* input.readLine(); */
			n = identificarTamanhoMatriz(nomeArquivo);
			// while ((new File(endereco+"\\"+nomeArquivo)).exists()) {
			// System.out.println("Arquivo nÃ£o encontrado!\nInsira um novo nome para o
			// arquivo: ");
			// nomeArquivo = br.readLine();
			// }
			BufferedReader arquivoLeitura = new BufferedReader(new FileReader(endereco + "\\" + nomeArquivo));

			String linha = arquivoLeitura.readLine();
			String SomaLinhas = "";
			ArrayList<Integer> elementosMatriz = new ArrayList<Integer>();
			int contLinha = 1, i = 0, j = 0;
			while (linha != null) {
				String[] elemento = linha.trim().split(" ");
				if (contLinha > 7) {
					for (int x = 0; x < elemento.length; x++) {
						if(elemento[x].equals("EOF")) {
							break;
						}
						elementosMatriz.add(Integer.parseInt(elemento[x]));
					}
				}
				System.out.println(linha);
				contLinha++;
				linha = arquivoLeitura.readLine();
			}
			Integer[][] matrizDist = new Integer[n][n];
			for(int k=0;k < elementosMatriz.size();k++){
				    matrizDist[i][j] = elementosMatriz.get(k);
				    matrizDist[j][i] = elementosMatriz.get(k);
				    j++;
				    if(elementosMatriz.get(k)==0) {
				    	i++;
				    	j=0;
				    }
				    if( i==n-2 && j==n-1) {
				    	break;
				    }
			}

			for(int a=0; a<n; a++){
				System.out.println();
				for(int b=0; b<n; b++){
						
						System.out.print(matrizDist[a][b]+" ");
				}			
			}		
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		// System.out.println(tamanho);
		return tamanho;
	}
	
	public ArrayList<Integer>  tsp(int[][] matriz ) {
		ArrayList<Integer> melhoresElementos = new ArrayList<Integer>();
		
		return melhoresElementos;
	}

}

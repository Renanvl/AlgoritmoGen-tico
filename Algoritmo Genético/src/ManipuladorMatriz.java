import java.util.ArrayList;

public class ManipuladorMatriz {

	public ManipuladorMatriz(Integer n) {
		this.n = n;
	}

	private Integer n = 0;
	private Integer[][] matrizDist;

	public Integer identificarTamanhoMatriz(String nomeArquivo) {
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

	public static String identificarTipoMatriz(String nomeArquivo) {
		String tipo = "";
		if (nomeArquivo.substring(0, 2).equals("gr")) {
			tipo = "LOWER";
		} else {
			tipo = "UPPER";
		}
		return tipo;
	}

	public Integer somaDist(int[] melhorCaminho, Integer[][] matrizResultado) {
		int distanciaTotal = 0;
		for (int i = 0; i <= melhorCaminho.length - 1; i++) {
			if (i == melhorCaminho.length - 1) {
				distanciaTotal += matrizResultado[melhorCaminho[i]][0];
				break;
			}
			distanciaTotal += matrizResultado[melhorCaminho[i]][melhorCaminho[i + 1]];
		}
		return distanciaTotal;
	}

	public Integer[][] montarMatriz(ArrayList<Integer> elementosMatriz, String nomeArquivo) {
		String tipo = identificarTipoMatriz(nomeArquivo);
		int i = 0, j = 0;
		Integer[][] matrizDist = new Integer[n][n];
		if (tipo.equals("LOWER")) {
			for (int k = 0; k < elementosMatriz.size(); k++) {
				matrizDist[i][j] = elementosMatriz.get(k);
				matrizDist[j][i] = elementosMatriz.get(k);
				j++;
				if (elementosMatriz.get(k) == 0) {
					i++;
					j = 0;
				}
				if (i == n - 2 && j == n - 1) {
					break;
				}
			}
		} else if (tipo.equals("UPPER")) {
			for (int k = 0; k < elementosMatriz.size(); k++) {
				if (elementosMatriz.get(k) == 0 && k != 0) {
					i++;
					j = i;
				}
				if (j == n) {
					break;
				}
				matrizDist[i][j] = elementosMatriz.get(k);
				matrizDist[j][i] = elementosMatriz.get(k);
				j++;

			}
		}

		for (int a = 0; a < n; a++) {
			//System.out.println();
			for (int b = 0; b < n; b++) {

			//	System.out.print(matrizDist[a][b] + " ");
			}
		}
		return matrizDist;
	}

}

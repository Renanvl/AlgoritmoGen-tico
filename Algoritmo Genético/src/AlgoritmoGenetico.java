
import java.util.Arrays;
import java.util.Random;

/**
 * GeneticAlgorithm
 *
 * @author: onlylemi
 */
public class AlgoritmoGenetico {
	GATSP gatsp = new GATSP(); 
	private static long tempoInicial;
 
    
    private float probabilidadeDeCrossOver = 0.9f; 
    private float probabilidadeDeMutacao = 0.01f; 
    private int tamanhoPopulacao = gatsp.getNumeroIndividuos();


	private int numeroDeMutacoes = 0; 
    private int atualGeracao = 0; 
    private int maxGeracao = 1000; 

    private int pointNum;
    private int[][] populacao; 
    private Integer[][] dist; 

    private int[] melhorIndividuo; 
    private float melhorDist; 
    private int atualMelhorPosicao;
    private float atualMelhorDist; 

    private float[] valores; 
    private float[] valorCabivel; 
    private float[] roleta;

    private boolean BooleanProxGeracaoAuto = false;

    public static AlgoritmoGenetico getInstance() {
        return AlgoritmoGeneticoH.instance;
    }

    private static class AlgoritmoGeneticoH {
        private static AlgoritmoGenetico instance = new AlgoritmoGenetico();
    }


    public int[] tsp(Integer[][] matriz) {
    	tempoInicial = System.currentTimeMillis();
        this.dist = matriz;
        pointNum = matriz.length;
        init();

        if (BooleanProxGeracaoAuto) {
            int i = 0;
            while (i++ < maxGeracao) {
                proxGeracao();
            }
        }
        BooleanProxGeracaoAuto = false;
        long tempoFinal   = System.currentTimeMillis();
		System.out.println("\nTempo de execução: "+ -1* (tempoInicial - tempoFinal)+" milisegundos\n");
        return getMelhorIndividuo();
    }


    private void init() {
        setNumeroDeMutacoes(0);
        setAtualGeracao(0);
        melhorIndividuo = null;
        melhorDist = 0;
        atualMelhorPosicao = 0;
        atualMelhorDist = 0;

        valores = new float[tamanhoPopulacao];
        valorCabivel = new float[tamanhoPopulacao];
        roleta = new float[tamanhoPopulacao];
        populacao = new int[tamanhoPopulacao][pointNum];

        for (int i = 0; i < tamanhoPopulacao; i++) {
            populacao[i] = individuoRandom(pointNum);
        }
        calcularMelhorIndividuo();
    }

  
    public int[] proxGeracao() {
        setAtualGeracao(getAtualGeracao() + 1);
        selecao();
        crossover();
        mutacao();
        calcularMelhorIndividuo();
        return getMelhorIndividuo();
    }

    
    private void selecao() {
        int[][] parentes = new int[tamanhoPopulacao][pointNum];

        int initnum = 4;
        parentes[0] = populacao[atualMelhorPosicao];
        parentes[1] = exchangeMutate(melhorIndividuo.clone()); 
        parentes[2] = inserirMutacoes(melhorIndividuo.clone()); 
        parentes[3] = melhorIndividuo.clone(); 

        setRoleta();
        for (int i = initnum; i < tamanhoPopulacao; i++) {
            parentes[i] = populacao[sair((int) Math.random())];
        }
        populacao = parentes;
    }

 
    private void setRoleta() {
       
        for (int i = 0; i < valores.length; i++) {
            valorCabivel[i] = 1.0f / valores[i]; 
        }

       
        float sum = 0;
        for (int i = 0; i < valorCabivel.length; i++) {
            sum += valorCabivel[i];
        }
        for (int i = 0; i < roleta.length; i++) {
            roleta[i] = valorCabivel[i] / sum;
        }
        for (int i = 1; i < roleta.length; i++) {
            roleta[i] += roleta[i - 1];
        }
    }

    private int sair(int ran) {
        for (int i = 0; i < roleta.length; i++) {
            if (ran <= roleta[i]) {
                return i;
            }
        }
        return 0;
    }

    private int[] exchangeMutate(int[] seq) {
        setNumeroDeMutacoes(getNumeroDeMutacoes() + 1);
        int m, n;
        do {
            m = random(seq.length - 2);
            n = random(seq.length);
        } while (m >= n);

        int j = (n - m + 1) >> 1;
        for (int i = 0; i < j; i++) {
            int tmp = seq[m + i];
            seq[m + i] = seq[n - i];
            seq[n - i] = tmp;
        }
        return seq;
    }

    private int[] inserirMutacoes(int[] seq) {
        setNumeroDeMutacoes(getNumeroDeMutacoes() + 1);
        int m, n;
        do {
            m = random(seq.length >> 1);
            n = random(seq.length);
        } while (m >= n);

        int[] s1 = Arrays.copyOfRange(seq, 0, m);
        int[] s2 = Arrays.copyOfRange(seq, m, n);

        for (int i = 0; i < m; i++) {
            seq[i + n - m] = s1[i];
        }
        for (int i = 0; i < n - m; i++) {
            seq[i] = s2[i];
        }
        return seq;
    }


    private void crossover() {
        int[] queue = new int[tamanhoPopulacao];
        int num = 0;
        for (int i = 0; i < tamanhoPopulacao; i++) {
            if (Math.random() < probabilidadeDeCrossOver) {
                queue[num] = i;
                num++;
            }
        }
        queue = Arrays.copyOfRange(queue, 0, num);
        queue = embaralhar(queue);
        for (int i = 0; i < num - 1; i += 2) {
            doCrossover(queue[i], queue[i + 1]);
        }
    }

    private static final int ANTERIOR = 0;
    private static final int PROXIMO = 1;

    private void doCrossover(int x, int y) {
        populacao[x] = getChild(x, y, ANTERIOR);
        populacao[y] = getChild(x, y, PROXIMO);
    }

   
    private int[] getChild(int x, int y, int pos) {
        int[] solution = new int[pointNum];
        int[] px = populacao[x].clone();
        int[] py = populacao[y].clone();

        int dx = 0, dy = 0;
        int c = px[random(px.length)];
        solution[0] = c;

        for (int i = 1; i < pointNum; i++) {
            int posX = indexOf(px, c);
            int posY = indexOf(py, c);

            if (pos == ANTERIOR) {
                dx = px[(posX + px.length - 1) % px.length];
                dy = py[(posY + py.length - 1) % py.length];
            } else if (pos == PROXIMO) {
                dx = px[(posX + px.length + 1) % px.length];
                dy = py[(posY + py.length + 1) % py.length];
            }

            for (int j = posX; j < px.length - 1; j++) {
                px[j] = px[j + 1];
            }
            px = Arrays.copyOfRange(px, 0, px.length - 1);
            for (int j = posY; j < py.length - 1; j++) {
                py[j] = py[j + 1];
            }
            py = Arrays.copyOfRange(py, 0, py.length - 1);

            c = dist[c][dx] < dist[c][dy] ? dx : dy;

            solution[i] = c;
        }
        return solution;
    }

    private void mutacao() {
        for (int i = 0; i < tamanhoPopulacao; i++) {
            if (Math.random() < probabilidadeDeMutacao) {
                if (Math.random() > 0.5) {
                    populacao[i] = inserirMutacoes(populacao[i]);
                } else {
                    populacao[i] = exchangeMutate(populacao[i]);
                }
                i--;
            }
        }
    }


    private void calcularMelhorIndividuo() {
        for (int i = 0; i < populacao.length; i++) {
            valores[i] = calcularDistIndividual(populacao[i]);
        }
        calcularMelhorDistAtual();
        if (melhorDist == 0 || melhorDist > atualMelhorDist) {
            melhorDist = atualMelhorDist;
            melhorIndividuo = populacao[atualMelhorPosicao].clone();
        }
    }


    private float calcularDistIndividual(int[] individuo) {
        float sum = dist[individuo[0]][individuo[individuo.length - 1]];
        for (int i = 1; i < individuo.length; i++) {
            sum += dist[individuo[i]][individuo[i - 1]];
        }
        return sum;
    }


    public void calcularMelhorDistAtual() {
        atualMelhorDist = valores[0];
        for (int i = 1; i < tamanhoPopulacao; i++) {
            if (valores[i] < atualMelhorDist) {
                atualMelhorDist = valores[i];
                atualMelhorPosicao = i;
            }
        }
    }



    private int[] individuoRandom(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }

        return embaralhar(a);
    }


    private int[] embaralhar(int[] a) {
        for (int i = 0; i < a.length; i++) {
            int p = random(a.length);
            int tmp = a[i];
            a[i] = a[p];
            a[p] = tmp;
        }
        return a;
    }

    private static Random rd;

    private int random(int n) {
        Random ran = rd;
        if (ran == null) {
            ran = new Random();
        }
        return ran.nextInt(n);
    }

    private int indexOf(int[] a, int index) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == index) {
                return i;
            }
        }
        return 0;
    }

    public int[] getMelhorIndividuo() {
        int[] best = new int[melhorIndividuo.length];
        int pos = indexOf(melhorIndividuo, 0);

        for (int i = 0; i < best.length; i++) {
            best[i] = melhorIndividuo[(i + pos) % melhorIndividuo.length];
        }
        return best;
    }


	public int getNumeroDeMutacoes() {
		return numeroDeMutacoes;
	}


	public void setNumeroDeMutacoes(int numeroDeMutacoes) {
		this.numeroDeMutacoes = numeroDeMutacoes;
	}


	public int getAtualGeracao() {
		return atualGeracao;
	}


	public void setAtualGeracao(int atualGeracao) {
		this.atualGeracao = atualGeracao;
	}

}

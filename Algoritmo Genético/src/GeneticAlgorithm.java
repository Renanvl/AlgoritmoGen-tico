
import java.util.Arrays;
import java.util.Random;

/**
 * GeneticAlgorithm
 *
 * @author: onlylemi
 */
public class GeneticAlgorithm {
	GATSP gatsp = new GATSP(); 
	private static long startTime;
	
    private static final float DEFAULT_CROSSOVER_PROBABILITY = 0.9f; 
    private static final float DEFAULT_MUTATION_PROBABILITY = 0.01f; 
 
    
    private float crossoverProbability = DEFAULT_CROSSOVER_PROBABILITY; 
    private float mutationProbability = DEFAULT_MUTATION_PROBABILITY; 
    private int tamanhoPopulacao = gatsp.getNumeroIndividuos();

    private int mutationTimes = 0; 
    private int currentGeneration = 0; 
    private int maxGeneration = 1000; 

    private int pointNum;
    private int[][] population; 
    private Integer[][] dist; 

    private int[] bestIndivial; 
    private float bestDist; 
    private int currentBestPosition;
    private float currentBestDist; 

    private float[] values; 
    private float[] fitnessValues; 
    private float[] roulette;

    private boolean isAutoNextGeneration = false;

    public static GeneticAlgorithm getInstance() {
        return GeneticAlgorithmHolder.instance;
    }

    private static class GeneticAlgorithmHolder {
        private static GeneticAlgorithm instance = new GeneticAlgorithm();
    }


    public int[] tsp(Integer[][] matrix) {
    	startTime = System.currentTimeMillis();
        this.dist = matrix;
        pointNum = matrix.length;
        init();

        if (isAutoNextGeneration) {
            int i = 0;
            while (i++ < maxGeneration) {
                nextGeneration();
            }
        }
        isAutoNextGeneration = false;
        long endTime   = System.currentTimeMillis();
		System.out.println("\nTempo de execução: "+ -1* (startTime - endTime)+" milisegundos\n");
        return getBestIndivial();
    }


    private void init() {
        mutationTimes = 0;
        currentGeneration = 0;
        bestIndivial = null;
        bestDist = 0;
        currentBestPosition = 0;
        currentBestDist = 0;

        values = new float[tamanhoPopulacao];
        fitnessValues = new float[tamanhoPopulacao];
        roulette = new float[tamanhoPopulacao];
        population = new int[tamanhoPopulacao][pointNum];

        for (int i = 0; i < tamanhoPopulacao; i++) {
            population[i] = randomIndivial(pointNum);
        }
        evaluateBestIndivial();
    }

    /**
     * ä¸‹ä¸€ä»£
     */
    public int[] nextGeneration() {
        currentGeneration++;
        selection();
        crossover();
        mutation();
        evaluateBestIndivial();
        return getBestIndivial();
    }

    /**
     * é€‰æ‹©
     */
    private void selection() {
        int[][] parents = new int[tamanhoPopulacao][pointNum];

        int initnum = 4;
        parents[0] = population[currentBestPosition];
        parents[1] = exchangeMutate(bestIndivial.clone()); 
        parents[2] = insertMutate(bestIndivial.clone()); 
        parents[3] = bestIndivial.clone(); 

        setRoulette();
        for (int i = initnum; i < tamanhoPopulacao; i++) {
            parents[i] = population[wheelOut((int) Math.random())];
        }
        population = parents;
    }

    /**
     *
     */
    private void setRoulette() {
        //calculate all the fitness
        for (int i = 0; i < values.length; i++) {
            fitnessValues[i] = 1.0f / values[i]; 
        }

        //set the roulette
        float sum = 0;
        for (int i = 0; i < fitnessValues.length; i++) {
            sum += fitnessValues[i];
        }
        for (int i = 0; i < roulette.length; i++) {
            roulette[i] = fitnessValues[i] / sum;
        }
        for (int i = 1; i < roulette.length; i++) {
            roulette[i] += roulette[i - 1];
        }
    }

    private int wheelOut(int ran) {
        for (int i = 0; i < roulette.length; i++) {
            if (ran <= roulette[i]) {
                return i;
            }
        }
        return 0;
    }

    private int[] exchangeMutate(int[] seq) {
        mutationTimes++;
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

    private int[] insertMutate(int[] seq) {
        mutationTimes++;
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
            if (Math.random() < crossoverProbability) {
                queue[num] = i;
                num++;
            }
        }
        queue = Arrays.copyOfRange(queue, 0, num);
        queue = shuffle(queue);
        for (int i = 0; i < num - 1; i += 2) {
            doCrossover(queue[i], queue[i + 1]);
        }
    }

    private static final int PREVIOUS = 0;
    private static final int NEXT = 1;

    private void doCrossover(int x, int y) {
        population[x] = getChild(x, y, PREVIOUS);
        population[y] = getChild(x, y, NEXT);
    }

   
    private int[] getChild(int x, int y, int pos) {
        int[] solution = new int[pointNum];
        int[] px = population[x].clone();
        int[] py = population[y].clone();

        int dx = 0, dy = 0;
        int c = px[random(px.length)];
        solution[0] = c;

        for (int i = 1; i < pointNum; i++) {
            int posX = indexOf(px, c);
            int posY = indexOf(py, c);

            if (pos == PREVIOUS) {
                dx = px[(posX + px.length - 1) % px.length];
                dy = py[(posY + py.length - 1) % py.length];
            } else if (pos == NEXT) {
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

    private void mutation() {
        for (int i = 0; i < tamanhoPopulacao; i++) {
            if (Math.random() < mutationProbability) {
                if (Math.random() > 0.5) {
                    population[i] = insertMutate(population[i]);
                } else {
                    population[i] = exchangeMutate(population[i]);
                }
                i--;
            }
        }
    }


    private void evaluateBestIndivial() {
        for (int i = 0; i < population.length; i++) {
            values[i] = calculateIndivialDist(population[i]);
        }
        evaluateBestCurrentDist();
        if (bestDist == 0 || bestDist > currentBestDist) {
            bestDist = currentBestDist;
            bestIndivial = population[currentBestPosition].clone();
        }
    }


    private float calculateIndivialDist(int[] indivial) {
        float sum = dist[indivial[0]][indivial[indivial.length - 1]];
        for (int i = 1; i < indivial.length; i++) {
            sum += dist[indivial[i]][indivial[i - 1]];
        }
        return sum;
    }


    public void evaluateBestCurrentDist() {
        currentBestDist = values[0];
        for (int i = 1; i < tamanhoPopulacao; i++) {
            if (values[i] < currentBestDist) {
                currentBestDist = values[i];
                currentBestPosition = i;
            }
        }
    }



    private int[] randomIndivial(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }

        return shuffle(a);
    }


    private int[] shuffle(int[] a) {
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

    private int[] concatAllArray(int[] first, int[]... rest) {
        int totalLength = first.length;
        for (int[] array : rest) {
            totalLength += array.length;
        }
        int[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (int[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private int indexOf(int[] a, int index) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == index) {
                return i;
            }
        }
        return 0;
    }

    public int[] getBestIndivial() {
        int[] best = new int[bestIndivial.length];
        int pos = indexOf(bestIndivial, 0);

        for (int i = 0; i < best.length; i++) {
            best[i] = bestIndivial[(i + pos) % bestIndivial.length];
        }
        return best;
    }

    public float getBestDist() {
        return bestDist;
    }

    public void setMaxGeneration(int maxGeneration) {
        this.maxGeneration = maxGeneration;
    }

    public void setAutoNextGeneration(boolean autoNextGeneration) {
        isAutoNextGeneration = autoNextGeneration;
    }

    public int getMutationTimes() {
        return mutationTimes;
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }
}

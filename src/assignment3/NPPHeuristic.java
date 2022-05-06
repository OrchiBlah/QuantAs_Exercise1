package assignment3;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

public class NPPHeuristic {


    public static void nppsolve(int[] openingcost, int[] capacity, double[][] connectioncost){

        int number_areas = connectioncost.length;
        int number_locations = connectioncost[0].length;

        // setup matrix and other needed stuff
        int[][] currentconnection = new int[number_areas][number_locations];

        int[] openlocation = new int[number_locations];

        Boolean[] uniqueconnection = new Boolean[number_areas];
        for(int i = 0; i < number_areas; i++){
            uniqueconnection[i] = false;
        }

        double[] candidates;
        double areasum;
        int minj;

        // loop until all areas have one connection -> uniqueconnection consists of only 'true' entries
        while (List.of(uniqueconnection).contains(false)){

            candidates = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};

            for (int j = 0; j < number_locations; j++){

                // if this holds, the location is already opened, i.e., we can skip it
                if(openlocation[j] == 1){
                    continue;
                }

                // connectioncost is identical for all areas so we can skip grabbing all entries
                areasum = connectioncost[0][j] * number_areas;
                candidates[j] = areasum / capacity[j] + openingcost[j];

            }

            minj = j_index(candidates);

            Triple<int[][], int[], Boolean[]> res = includej(minj, capacity, currentconnection, openlocation, uniqueconnection).get(0);

            currentconnection = res.getLeft();
            openlocation = res.getMiddle();
            uniqueconnection = res.getRight();

        }

        double optcost = multiplyConnectionCosts(currentconnection, connectioncost) + multiplyOpeningCosts(openlocation, openingcost);
        System.out.println("Heuristic result: "+ optcost+'\n');
        System.out.println("Optimal connections:"+'\n');
        for ( int i = 0; i < number_areas; i++){
            System.out.println(Arrays.toString(currentconnection[i]));
        }
        System.out.println('\n'+"Open locations: ");
        System.out.println('\n'+Arrays.toString(openlocation));

    }

    // setup area connections to the minimum location
    public static List<Triple<int[][], int[], Boolean[]>> includej(int minj, int[] capacity, int[][] currentconnection, int[] openlocation, Boolean[] uniqueconnection){

        int locapacity = capacity[minj];
        openlocation[minj] = 1;
        int i = 0;

        // connect as many areas to the minimal location as possible -> max capacity is capacity[j]
        while (locapacity > 0 && i <= currentconnection.length-1){

            if (!uniqueconnection[i]){
                uniqueconnection[i] = true;
                currentconnection[i][minj] = 1;
                locapacity -= 1;
            }
            i += 1;
        }

        List<Triple<int[][], int[], Boolean[]>> res = new LinkedList<>();
        res.add(Triple.of(currentconnection, openlocation, uniqueconnection));

        return res;
    }

    // finds index of the location with the minimal value
    public static int j_index(double[] candidates){

        int index = 0;

        for ( int i = 0; i < candidates.length; i++){
            if (candidates[i] <= candidates[index]){
                index = i;
            }
        }

        return index;
    }

    public static double multiplyConnectionCosts(int[][] currentconnection, double[][] connectioncost){

        double sum = 0;

        for (int i = 0; i < currentconnection.length; i++){
            for (int j = 0; j < currentconnection[0].length; j++){
                sum += currentconnection[i][j] * connectioncost[i][j];
            }
        }

        return sum;
    }

    public static double multiplyOpeningCosts(int[] openlocation, int[] openingcost){

        double sum = 0;

        for (int j = 0; j < openlocation.length; j++){
            sum += openlocation[j] * openingcost[j];
        }

        return sum;

    }
}

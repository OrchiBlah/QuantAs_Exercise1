/* Examplary solution of assignment 3 for the first exercise sheet
 */

package assignment3;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class Assignment3 {

    // setup values of the optimization problem
    private final int[] openingcost = {5, 2, 12, 6, 20};
    private final int[] capacity = {2, 3, 5, 3, 8};
    private final double[][] connectioncost = new double[9][5];

    public Assignment3(){

        double[] rowcontent = {1, 2, 0.7, 1.2, 0.8};

        for(int i = 0; i < 9; i++){
            connectioncost[i] = rowcontent;
        }
    }

    // 0 == optimal, 1 == relaxation, 2 == heuristic
    private void calcSolution(){

        try{

            IloCplex model = new IloCplex();
            NPPSolver.nppsolve(openingcost, capacity, connectioncost, 0, model);
            model.clearModel();
            NPPSolver.nppsolve(openingcost, capacity, connectioncost, 1, model);
            NPPHeuristic.nppsolve(openingcost, capacity, connectioncost);

        } catch (IloException e){
            e.printStackTrace();
        }
    }

    public static void main ( String[] args ){

        Assignment3 a3 = new Assignment3();
        a3.calcSolution();
    }

}
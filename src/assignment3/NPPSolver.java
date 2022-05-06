package assignment3;

import ilog.concert.*;
import ilog.cplex.IloCplex;

public class NPPSolver {

    public NPPSolver(){}

    public static void nppsolve (int[] openingcost, int[] capacity, double[][] connectioncost, int type, IloCplex model){

        int number_areas = connectioncost.length;
        int number_locations = connectioncost[0].length;

        try{
            // initialize variables

            // boolean variable creation for different types (0 == optimal, 1 == relaxation)
            IloNumVar[][] u = new IloNumVar[number_areas][number_locations];
            IloNumVar[] r;

            if(type == 0){
                for ( int i = 0; i < number_areas; i++){
                    u[i] = model.boolVarArray(number_locations);
                }
                r = model.boolVarArray(number_locations);
            } else{
                for ( int i = 0; i < number_areas; i++){
                    // 2. + 3. parameter define the allowed range of values for each variable
                    u[i] = model.numVarArray(number_locations, 0, 1);
                }
                r = model.numVarArray(number_locations, 0, 1);
            }

            IloLinearNumExpr[] areaconnection = new IloLinearNumExpr[number_locations];
            for (int j = 0; j < number_locations; j++){
                areaconnection[j] = model.linearNumExpr();
                for (int i = 0; i < number_areas; i++){
                    areaconnection[j].addTerm(1.0, u[i][j]);
                }
            }

            // define objective function
            IloLinearNumExpr objective = model.linearNumExpr();
            for (int i = 0; i < number_areas; i++){
                for (int j = 0; j < number_locations; j++){
                    objective.addTerm(connectioncost[i][j], u[i][j]);
                }
            }
            for (int j = 0; j < number_locations; j++){
                objective.addTerm(openingcost[j], r[j]);
            }
            model.addMinimize(objective);

            // define constraints
            for (int i = 0; i < number_areas; i++){
                model.addEq(model.sum(u[i]), 1);
            }

            for (int j = 0; j < number_locations; j++){
                model.addLe(areaconnection[j], model.prod(r[j], capacity[j]));
            }

            // removes any solution output
            model.setOut(null);

            // solve model
            if (model.solve()){
                if (type == 0){
                    System.out.println("Optimal solution result: "+model.getObjValue()+'\n');
                } else{
                    System.out.println("Relaxation result: "+model.getObjValue()+'\n');
                }
            }

            // LP output into an lp file
            //model.exportModel("export.lp");

        } catch (IloException e){
            e.printStackTrace();
        }

    }
}

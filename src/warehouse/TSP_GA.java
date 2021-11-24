package warehouse;

import java.util.*;

public class TSP_GA {
    int ROW = 40;
    int COL = 25;

    int scale; // group size
    int itemNum; // item size
    int MAX_GEN; // generations

    int bestT; // the generation of the best result
    int bestLength = Integer.MAX_VALUE; // the best length
    int[] bestTour; // the best order of items
    int[] start, end;

    /*
    population: each row is an orderlist of items (chromosome).
                total number is scale
     */
    int[][] oldPopulation;
    int[][] newPopulation;

    // less is better
    int[] fitness;
    float[] Pi; // the sum of the corresponding reciprocal of fitness

    float Pc; // crossover possibility
    float Pm; // mutate possibility
    int t; // current generation

    Random random;


    PrimaryController primaryController;
    ArrayList<Item> currentOrderItems = new ArrayList<>();
    char[][] warehouseMatrix = new char[ROW][COL];
    StringBuilder instructions = new StringBuilder();

    public TSP_GA() {

    }

    /**
     * constructor of GA
     *
     * @param s
     *            scale
     * @param n
     *            items size
     * @param g
     *            loop/generation
     * @param c
     *            crossover rate
     * @param m
     *            mutation rate
     *
     **/
    TSP_GA(int s, int n, int g, float c, float m) {
        scale = s;
        itemNum = n;
        MAX_GEN = g;
        Pc = c;
        Pm = m;
        start = new int[]{0, 0};
        end = new int[]{0,0};
    }

    @SuppressWarnings("resource")
    void init(int[] s, int[] e, ArrayList<Item> OrderItems, char[][] Matrix) {

        bestLength = Integer.MAX_VALUE;
        bestTour = new int[itemNum + 1];
        bestT = 0;
        t = 0;
        start = s;
        end = e;

        newPopulation = new int[scale][itemNum];
        oldPopulation = new int[scale][itemNum];
        fitness = new int[scale];
        Pi = new float[scale];

        primaryController = new PrimaryController();
        currentOrderItems = OrderItems;
        warehouseMatrix = Matrix;
        random = new Random(System.currentTimeMillis());

    }

    /**
     * Limit the running time within 60s,
     * output the best order of items and its distance
     * @param timeOut
     * @return
     */
    ArrayList<Integer> solve(int timeOut) {
        long startTime = System.currentTimeMillis();
        long endTime;
        int i;
        int k;

        /* ramdomly init the order of items
         * and store them in oldPopulation
         * */
        initGroup();
        // calculate init group's fitness，Fitness[max]
        for (k = 0; k < scale; k++) {
            // fitness <-> the whole length of items
            fitness[k] = evaluate(oldPopulation[k]);
        }
        // Calculate the cumulative probability
        // of each individual in the init group，Pi[max]
        countRate();

        for (t = 0; t < MAX_GEN; t++) {
            endTime = System.currentTimeMillis();
            if ((endTime-startTime)>timeOut){
                System.out.println("Time out!");
                break;
            }
            evolution();

            // set oldGroup from newGroup, ready to loop
            for (k = 0; k < scale; k++) {
                for (i = 0; i < itemNum; i++) {
                    oldPopulation[k][i] = newPopulation[k][i];
                }
            }

            for (k = 0; k < scale; k++) {
                fitness[k] = evaluate(oldPopulation[k]);
            }

            countRate();
        }

        System.out.println("the generation of the best length：");
        System.out.println(bestT);


        ArrayList<Integer> tour = new ArrayList<>();;
        tour.add(0);
        for (i = 0; i < itemNum; i++) {
            tour.add(bestTour[i]);
        }
        tour.add(0);
        System.out.println("distance:" + bestLength);
        tourToInstructions(tour);
//        System.out.println(instructions.toString().trim());
        return tour;
    }

    /*
     * ramdomly init the order of items
     * and store them in oldPopulation
     * */
    void initGroup() {
        // oldPopulation start and end in neededitems, exclude the (0,0)
        // the whole route should be 0 - 1 - 2 - 0, here store 1-2
        int i, j, k, num;
        for (k = 0; k < scale; k++) // loop for scale
        {
            for (i = 0; i < itemNum;)// chromosome's length
            {
                num = currentOrderItems.get(random.nextInt(65535) % itemNum).id;
                oldPopulation[k][i] = num;
                for (j = 0; j < i; j++) {
                    if (oldPopulation[k][i] == oldPopulation[k][j]) {
                        break;
                    }
                }
                if (j == i) {
                    i++;
                }
            }
        }
    }

    /*
        chromosome[] is a list of all items,exclude the start/end
    *   len = the whole distance of start point to items to end point
    * */
    int evaluate(int[] chromosome) {
        Vector<Coordinate> c = new Vector<>();
        c.add(new Coordinate(start[0], start[1]));
        int x = 0, y = 0, x1, y1;
        int len = 0;
        int i;
        ArrayList<Vertex> path;

        // c includes (0,0) to items, except the end (0,0)
        for (i = 0; i < itemNum; i++) {
            x = PrimaryController.getItemByID(chromosome[i]).row;
            y = PrimaryController.getItemByID(chromosome[i]).col;
            c.add(new Coordinate(x, y));
        }

        /*
         * start point should always be next to the dest node
         * end point should be the dest node itself
         * if start is the neighbor of end, then len should be 0
         * */
        Coordinate st = c.get(0), ed;
        for (i = 0; i < itemNum; i++) {
            ed = c.get(i + 1);
            if (!checkNeighbors(st, ed)) {
                path = setBFSPath(warehouseMatrix, st, ed);
                x1 = path.get(path.size() - 2).coordinate.x;
                y1 = path.get(path.size() - 2).coordinate.y;
                st = new Coordinate(x1, y1);
                len += path.size() - 2;
            } else {
                len += 0;
            }
        }

        //end point
        path = setBFSPath(warehouseMatrix, st, new Coordinate(end[0], end[1]));
        len += path.size() - 1;
        return len;
    }

    /*
     * Calculate the cumulative probability of each individual in the population
     * as part of the gambling wheel selection strategy
     * */
    void countRate() {
        int k;
        double sumFitness = 0;// sum of fitness

        // fitness: less is better, so tempf is the reciprocal of it
        double[] tempf = new double[scale];

        for (k = 0; k < scale; k++) {
            tempf[k] = 1.0 / fitness[k];
            sumFitness += tempf[k];
        }

        // less length, greater possibility to be selected
        Pi[0] = (float) (tempf[0] / sumFitness);
        for (k = 1; k < scale; k++) {
            Pi[k] = (float) (tempf[k] / sumFitness + Pi[k - 1]);
        }
    }

    /*
     * The individuals with the shortest fitness in a generation population were selected
     *  and copied directly into the offspring
     * */
    void selectBest() {
        int k, i, maxid;
        int minLength;

        maxid = 0;
        minLength = fitness[0];

        // select the shortest fitness
        for (k = 1; k < scale; k++) {
            if (minLength > fitness[k]) {
                minLength = fitness[k];
                maxid = k;
            }
        }

        if (bestLength > (minLength)) {
            bestLength = minLength;
            bestT = t;// best generation;

            for (i = 0; i < itemNum; i++) {
                bestTour[i] = oldPopulation[maxid][i];
            }
        }

        // copy the best individual into the first place of newPopulation
        copy(0, maxid);
    }

    /*
     * Copy chromosome, K represents the position of the new chromosome in the population,
     * and KK represents the position of the old chromosome in the population
     * */
    void copy(int k, int kk) {
        int i;
        for (i = 0; i < itemNum; i++) {
            newPopulation[k][i] = oldPopulation[kk][i];
        }
    }

    /* Wheel selection strategy
     * save the better chromosomes from crossover
     * (it's in the first place)
     * */
    void select() {
        int k, i, selectId;
        float ran1;

        // start from 1, loop (scale - 1) times
        for (k = 1; k < scale; k++) {
            ran1 = (float) (random.nextInt(65535) % 1000 / 1000.0);
            // better fitness , has more posibility to be selected and survive
            for (i = 0; i < scale; i++) {
                if (ran1 <= Pi[i]) {
                    break;
                }
            }
            selectId = i;
            copy(k, selectId);
        }
    }

    // save the best chromosomes from crossover
    void evolution() {
        int k;

        selectBest();
        select();

        // Random random = new Random(System.currentTimeMillis());
        float r;

        for (k = 1; k + 1 < scale ; k = k + 2) {
            r = random.nextFloat();
            //crossover
            if (r < Pc) {
                CrossOver(k, k + 1);
            }
            // mutation
            else {
                r = random.nextFloat();
                if (r < Pm) {
                    Mutation(k);
                }
                r = random.nextFloat();
                if (r < Pm) {
                    Mutation(k + 1);
                }
            }
        }
        if (k == scale  - 1)// the last one is left and do mutation
        {
            r = random.nextFloat();
            if (r < Pm) {
                Mutation(k);
            }
        }

    }

    // cross over
    /* switch like below:
     * len: ran1 x ran2
     *      c1 a b c                       new_c1 n m d
     *                   ---->
     *      c2 d e f                       new_c2 c i j
     *   change c and d
     *   n m are the elements that are not overlap with d, so do i j
     * */
    void CrossOver(int k1, int k2) {
        int i, j, k, flag;
        int ran1, ran2, temp;
        int[] Gh1 = new int[itemNum];
        int[] Gh2 = new int[itemNum];

        ran1 = random.nextInt(65535) % itemNum;
        ran2 = random.nextInt(65535) % itemNum;
        while (ran1 == ran2) {
            ran2 = random.nextInt(65535) % itemNum;
        }

        if (ran1 > ran2)//  make sure ran1<ran2
        {
            temp = ran1;
            ran1 = ran2;
            ran2 = temp;
        }

        // Move the end of chromosome 1 to the head of chromosome 2
        for (i = 0, j = ran2; j < itemNum; i++, j++) {
            Gh2[i] = newPopulation[k1][j];
        }

        flag = i;// start point of original c2

        for (k = 0, j = flag; j < itemNum;)
        {
            Gh2[j] = newPopulation[k2][k++];
            for (i = 0; i < flag; i++) {
                if (Gh2[i] == Gh2[j]) {
                    break;
                }
            }
            if (i == flag) {
                j++;
            }
        }

        // Gh1: [0 ~ ran1] store items that are not same with newPopulation[k2][0 ~ ran1]
        flag = ran1;
        for (k = 0, j = 0; k < itemNum;)
        {
            Gh1[j] = newPopulation[k1][k++];
            for (i = 0; i < flag; i++) {
                if (newPopulation[k2][i] == Gh1[j]) {
                    break;
                }
            }
            if (i == flag) {
                j++;
            }
        }

        //Gh1: [flag ~ end] store items that are same with newPopulation[k2][0 ~ ran1]
        flag = itemNum - ran1;

        for (i = 0, j = flag; j < itemNum; j++, i++) {
            Gh1[j] = newPopulation[k2][i];
        }

        //Put back the population after crossover
        for (i = 0; i < itemNum; i++) {
            newPopulation[k1][i] = Gh1[i];
            newPopulation[k2][i] = Gh2[i];
        }
    }


    /* mutation
        change order of items in multiply times
    */
    void Mutation(int k) {
        int ran1, ran2, temp;
        int count;// times of exchange

        // Random random = new Random(System.currentTimeMillis());
        count = random.nextInt(65535) % itemNum;

        for (int i = 0; i < count; i++) {

            ran1 = random.nextInt(65535) % itemNum;
            ran2 = random.nextInt(65535) % itemNum;
            while (ran1 == ran2) {
                ran2 = random.nextInt(65535) % itemNum;
            }
            temp = newPopulation[k][ran1];
            newPopulation[k][ran1] = newPopulation[k][ran2];
            newPopulation[k][ran2] = temp;
        }
    }

    /*
   return instructions
   * */
    String getInstructions(){
        return instructions.toString().trim();
    }

    /*
    return warehouseMatrix
    * */
    char[][] getMatrix(){
        return warehouseMatrix;
    }

    /*
     * return the bestLength
     * */
    int getBestLength(){
        return bestLength;
    }

    /*
     * check if the dest node is next to the source node
     * if true, the distance should be 0, no need BFS
     * if false, need BFS
     * */
    boolean checkNeighbors(Coordinate c1, Coordinate c2){
        int[] rowNum = {-1, 0, 1, 0};
        int[] colNum = {0, 1, 0, -1};
        int x1, y1;
        for(int i=0; i<4; i++){
            x1 = c2.x + rowNum[i];
            y1 = c2.y + colNum[i];
            if(c1.x == x1 && c1.y == y1)
                return true;
        }
        return false;
    }

    /**
     * Find the path from c1 to c2 in warehouseMatrix
     * @param warehouseMatrix
     * @param c1
     * @param c2
     * @return
     */
    ArrayList<Vertex> setBFSPath(char[][] warehouseMatrix, Coordinate c1, Coordinate c2){
        ArrayList<Vertex> path;
        Item2ItemPath itemPath = new Item2ItemPath();

        char temp1 = warehouseMatrix[c1.x][c1.y];
        char temp2 = warehouseMatrix[c2.x][c2.y];
        warehouseMatrix[c1.x][c1.y] = '.';
        warehouseMatrix[c2.x][c2.y] = '.';

        path = itemPath.findBFSPath(warehouseMatrix, c1, c2);

        warehouseMatrix[c1.x][c1.y] = temp1;
        warehouseMatrix[c2.x][c2.y] = temp2;
        return path;
    }

    /**
     * Call setInstructions() for each items to convert the pickup order
     * into a list of instructions
     * Mark the pickup items as ‘$’
     * Call setMatrix() to mark the path as ‘P’
     * @param tour order of pickup items
     */
    void tourToInstructions(ArrayList<Integer> tour) {
        ArrayList<Vertex> path;
        int x1, y1;
        Coordinate c1 =new Coordinate(start[0], start[1]);
        int count = 1;
        instructions.append("Path Instructions: \n");
        for(int i = 1; i< tour.size(); i++){
            if(i != tour.size()-1) {
                Coordinate c2 = new Coordinate(
                        PrimaryController.getItemByID(tour.get(i)).row,
                        PrimaryController.getItemByID(tour.get(i)).col);
                if(checkNeighbors(c1, c2)) {
                    String dir = pickupDirection(c1,c2);
                    instructions.append("Pickup item(s) ("+
                            PrimaryController.getItemByID(tour.get(i)).id +
                            ") from the shelf directly " + dir + " to you \n");

                    continue;
                }
                path = setBFSPath(warehouseMatrix, c1, c2);
                warehouseMatrix[c2.x][c2.y]='$';
                setMatrix(warehouseMatrix,path);
                setInstructions(path);

                String dir = getDirection(path.get(path.size() - 2).coordinate.x,path.get(path.size() - 2).coordinate.y,
                        c2.x, c2.y);
                instructions.append("Pickup item(s) ("+
                        PrimaryController.getItemByID(tour.get(i)).id +
                        ") from the shelf directly " + dir + " to you \n");

            }else{
                path = setBFSPath(warehouseMatrix, c1, new Coordinate(end[0],end[1]));
                setMatrix(warehouseMatrix,path);
                warehouseMatrix[start[0]][start[1]]='S';
                warehouseMatrix[end[0]][end[1]]='E';
                setInstructions(path);

                instructions.append("return to the start point \n");
                instructions.append("Path Complete \n");
            }

            x1 = path.get(path.size() - 2).coordinate.x;
            y1 = path.get(path.size() - 2).coordinate.y;
            c1 = new Coordinate(x1, y1);

        }
    }

    /**
     * Make corresponding path as ‘P’ in warehouseMatrix
     * @param warehouseMatrix
     * @param path
     */
    void setMatrix(char[][] warehouseMatrix, ArrayList<Vertex> path) {
        int size = path.size();
        int x,y;
        for(int i = 1; i<size-1; i++){
            x = path.get(i).coordinate.x;
            y = path.get(i).coordinate.y;
//            if(warehouseMatrix[x][y] =='.')
            warehouseMatrix[x][y] = 'P';
        }

    }

    /**
     * Calculate and store the distance and directions
     * in instructions according to the path
     * @param path
     */
    void setInstructions(ArrayList<Vertex> path) {
        int size = path.size();
        int x1,y1,x2,y2;
        String dir = "";
        int count = 0;
        for(int i = 1; i<size; i++){
            x1 = path.get(i-1).coordinate.x;
            y1 = path.get(i-1).coordinate.y;
            x2 = path.get(i).coordinate.x;
            y2 = path.get(i).coordinate.y;
            if(i == 1){
                dir = getDirection(x1,y1,x2,y2);
                count++;
            }else if(i < (size -1)){
                if(!Objects.equals(dir, getDirection(x1, y1, x2, y2))){
                    if(count > 1)
                        instructions.append("Move "+ dir + " "+ count + " "+ "units \n");
                    else{
                        instructions.append("Move "+ dir + " "+ count + " "+ "unit \n");
                    }
                    dir = getDirection(x1,y1,x2,y2);
                    count = 1;
                }else{
                    count++;
                }
            }else{
                if(x2 == 0 && y2 == 0)
                    count++;
                if(count > 1)
                    instructions.append("Move "+ dir + " "+ count + " "+ "units \n");
                else{
                    instructions.append("Move "+ dir + " "+ count + " "+ "unit \n");
                }
            }
        }
    }

    /**
     * Get the direction of adjacent node (x1,y1) and node(x2,y2)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    String getDirection(int x1, int y1, int x2, int y2) {
        int[] rowNum = {-1, 0, 1, 0};
        int[] colNum = {0, 1, 0, -1};
        int temp = 4;
        int tx, ty;
        String dir = "";
        for(int i=0; i<4; i++){
            tx = x1 + rowNum[i];
            ty = y1 + colNum[i];
            if(tx == x2 && ty == y2)
                temp = i;
        }
        if(temp == 0)
            dir = "west";
        else if(temp == 1)
            dir = "north";
        else if(temp == 2)
            dir = "east";
        else if(temp == 3)
            dir = "south";
        else
            dir = "error";

        return dir;
    }

    /**
     * Get and return the pickup direction
     * between worker’s location and needed item
     * @param c1 worker's location
     * @param c2 needed item
     */
    String pickupDirection(Coordinate c1, Coordinate c2) {
        int[] rowNum = {-1, 0, 1, 0};
        int[] colNum = {0, 1, 0, -1};
        int x1, y1;
        int temp = 4;
        String dir = "";
        for(int i=0; i<4; i++){
            x1 = c2.x + rowNum[i];
            y1 = c2.y + colNum[i];
            if(c1.x == x1 && c1.y == y1)
                temp = i;
        }
        if(temp == 0)
            dir = "east";
        else if(temp == 1)
            dir = "south";
        else if(temp == 2)
            dir = "west";
        else if(temp == 3)
            dir = "north";
        else
            dir = "error";

        return dir;

    }
}


package com.example.simplegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class theBot {

    String thePath = " ";

    private static final int maxDepth = 2;
    //botPlayer = true means its bot's turn
    public static Boolean botPlayer = true;
    public static Boolean gameOver = false;
    public static int[][] localBoard;
    Random random = new Random();
    public int getBestMove(int[][] theBoard) {
        System.out.flush();
        int bestScore = 0;
        int bestMove = 0;

        //below lines will copy the board
        localBoard = new int[7][7];
        for (int i = 0; i < 7; i++) {
            System.arraycopy(theBoard[i], 0, localBoard[i], 0, 7);
        }

        System.out.println(getAvailableColumns().size());

//        running the minimax
        if (botPlayer) {
            System.out.println("max called");
            return max(0).getColumn();
        }
        // If P2 plays then it wants to MINimize the heuristics value.
        else {
            return min(0).getColumn();
        }

    }


    public Move max(int depth) {
//        System.out.println("max :: depth :: "+depth);
        //termination condition

        int checkForGameOver = checkForGameOver();
        if(checkForGameOver != 0){
            if(checkForGameOver  > 0 ){
                return new Move(Integer.MAX_VALUE);
            }else{
                return new Move(Integer.MIN_VALUE);
            }
        }


        if (depth == maxDepth) {
            Move maxMove = new Move(Integer.MIN_VALUE);
//            System.out.println("Max termination condition");
            ArrayList<Integer> availableColumns = getAvailableColumns();
            for (int i : availableColumns) {

                makeMove(i, 2);


                int evaluatedScore = evaluate();
                System.out.println(thePath + " " + i + "--------> " + evaluatedScore);
                Move move = new Move(evaluatedScore);//evaluate function

                if (move.getValue() > maxMove.getValue() || ((move.getValue() == maxMove.getValue() && random.nextBoolean()))) {
                    //get random move if the score is equal with the maxMove score
                    maxMove.setValue(move.getValue());
                    maxMove.setColumn(i);
                }

//                System.out.println("evaluation score for "+i+ ": "+evaluatedScore);

                undoMove(i, 2);
            }

            thePath = " ";

            return maxMove;
        }

        Move maxMove = new Move(Integer.MIN_VALUE);

        ArrayList<Integer> availableColumns = getAvailableColumns();
        for (int i : availableColumns) {
            makeMove(i, 2);
            thePath = thePath + i;

            printLogicleCircle();
            Move move = min(depth + 1);
//            System.out.println("-------->"+ move.getValue());

            if (move.getValue() > maxMove.getValue() || ((move.getValue() == maxMove.getValue() && random.nextBoolean()))) {
                maxMove.setValue(move.getValue());
                maxMove.setColumn(i);
            }

            System.out.println("--------------------------------------------------------------->max :: " + maxMove.getValue());
            System.out.println("--------> "+ maxMove.getColumn());

            undoMove(i, 2);
        }
        return maxMove;
    }

    public Move min(int depth) {
//        System.out.println("min :: depth :: "+depth);
        //termination condition


        int checkForGameOver = checkForGameOver();
        if(checkForGameOver != 0){
            if(checkForGameOver  > 0 ){
                return new Move(Integer.MAX_VALUE);
            }else{
                return new Move(Integer.MIN_VALUE);
            }
        }


        if (depth == maxDepth) {
            Move minMove = new Move(Integer.MAX_VALUE);
//            System.out.println("Min termination condition");
            ArrayList<Integer> availableColumns = getAvailableColumns();
            for (int i : availableColumns) {
                makeMove(i, 1);



                int evaluatedScore = evaluate();

                System.out.println(thePath + " " + i + "--------> " + evaluatedScore);

                Move move = new Move(evaluatedScore);//evaluate function

                if (move.getValue() < minMove.getValue() || ((move.getValue() == minMove.getValue() && random.nextBoolean()))) {
                    minMove.setValue(move.getValue());
                    minMove.setColumn(i);
                }

                System.out.println("evaluation score for "+i+ ": "+evaluatedScore);
                undoMove(i, 1);
            }

            thePath = " ";
            System.out.println(minMove.getValue());
            return minMove;
        }

        Move minMove = new Move(Integer.MAX_VALUE);

        ArrayList<Integer> availableColumns = getAvailableColumns();
        for (int i : availableColumns) {
            makeMove(i, 1);
            thePath = thePath + i;


            Move move = max(depth + 1);

            if (move.getValue() < minMove.getValue() || ((move.getValue() == minMove.getValue() && random.nextBoolean()))) {
                minMove.setValue(move.getValue());
                minMove.setColumn(i);
            }

            undoMove(i, 1);
        }

        return minMove;
    }


    public int evaluate(){
//        printLogicleCircle();
        int player1Score = 0;
        int player2Score = 0;
        for (int i = 1; i < 4; i++) {
            player1Score += countTheBalls(i, !botPlayer) * Math.pow(5, i - 1);
            player2Score += countTheBalls(i, botPlayer) * Math.pow(5, i - 1);
        }

        int  winnerCounter = countTheBalls(4, !botPlayer);
        if (winnerCounter > 0) {
//            player2Score = Integer.MIN_VALUE;
            return Integer.MIN_VALUE;
        }

        winnerCounter = countTheBalls(4, botPlayer);
        if (winnerCounter > 0) {
//            player1Score = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }


//        System.out.println("player 1 score :: "+player1Score);
//        System.out.println("player 2 score :: "+player2Score);

        return player2Score - player1Score;
    }


    public int countTheBalls(int noOfBallsToCount,Boolean botPlayer){
        int times = 0;
        int player = (botPlayer) ? 2:1;
        //count in the middle column of the board
        if(noOfBallsToCount == 1){
            for (int i = 0; i < 7; i++) {
                if(localBoard[3][i] == ((botPlayer)? 2 :1)){
                    times++;
                }
            }
        }else {
            //count in the window of 4

            //verticle
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 4; j++) {
                    int[] tempArray = new int[]{localBoard[i][j],localBoard[i][j+1],localBoard[i][j+2],localBoard[i][j+3]};
                    if(countFromTheArrayOfFour(tempArray,player,noOfBallsToCount)){
                        times++;
                    }
                }
            }

            //horizontel
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 4; j++) {
                    int[] tempArray = new int[]{localBoard[j][i],localBoard[j+1][i],localBoard[j+2][i],localBoard[j+3][i]};

                    if(countFromTheArrayOfFour(tempArray,player,noOfBallsToCount)){
                        times++;
                    }
                }

            }

            //diagonal
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int[] tempArray = new int[]{localBoard[i][j],localBoard[i+1][j+1],localBoard[i+2][j+2],localBoard[i+3][j+3]};

                    if(countFromTheArrayOfFour(tempArray,player,noOfBallsToCount)){
                        times++;
                    }
                }
            }

            for (int i = 6; i >2; i--) {
                for (int j = 0; j < 4; j++) {
                    int[] tempArray = new int[]{localBoard[i][j],localBoard[i-1][j+1],localBoard[i-2][j+2],localBoard[i-3][j+3]};

                    if(countFromTheArrayOfFour(tempArray,player,noOfBallsToCount)){
                        times++;
                    }
                }
            }


        }
        return times;
    }

    public ArrayList<Integer> getAvailableColumns(){
        ArrayList<Integer> cols = new ArrayList<>();
        for (int col = 0; col < 7; col++) {
            if (localBoard[col][6] == 0) {
                cols.add(col);
            }
        }
        return cols;
    }

    public void makeMove(int columnNo, int player){
        for (int i = 0; i < 7; i++) {
            if(localBoard[columnNo][i] == 0){
                localBoard[columnNo][i] = player;
                break;
            }
        }
    }

    public void undoMove(int columnNo,int player){
        for (int i = 0; i < 6; i++) {
            if(localBoard[columnNo][i+1] == 0){
                localBoard[columnNo][i] = 0;
                break;
            }
        }
        if(localBoard[columnNo][6] != 0){
            localBoard[columnNo][6] = 0;
        }
    }
    public int checkForGameOver(){
        int counter = countTheBalls(4, botPlayer);
        if (counter > 0) {
            return Integer.MAX_VALUE;
        }

        counter = countTheBalls(4, !botPlayer);
        if (counter > 0) {
            return Integer.MIN_VALUE;
        }
        return 0;
    }

    public void printLogicleCircle(){
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(localBoard[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    public Boolean countFromTheArrayOfFour(int[] arrayOfFour,int player,int noOfBallsToCount){
        long countZeros = Arrays.stream(arrayOfFour).filter(num -> num == 0).count();
        long countPlayers = Arrays.stream(arrayOfFour).filter(num -> num == player).count();
        return countPlayers + countZeros == 4 && noOfBallsToCount == countPlayers;
    }

}

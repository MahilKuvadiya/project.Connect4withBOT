package com.example.simplegame;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.*;
import java.util.Scanner;

public class HelloApplication extends Application {

    boolean botIsplaying = true;
    theBot bot = new theBot();
    int startingCoX,startingCoY,destinationCoX,destinationCoY;
    Circle[][] circle;
    int[][] logicalCircles = new int[][]{
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
    };
    static Boolean currentPlayer = true;
    VBox outerBox;
    Line l;
    HBox labelBox;
    Label playerIndicator;
    HBox pillarBox;
    VBox[] pillar;
    Button[] b;
    StackPane s;
    HBox buttonBox;
    Label playerOneScore,playerTwoScore;
    BoxBlur blueEffect = new BoxBlur(5,5,3);
    Button replayButton;
    @Override
    public void start(Stage stage) throws IOException {
        replayButton = new Button("Replay");
        replayButton.setStyle("-fx-background-color: grey");
        replayButton.setPrefWidth(100);
        replayButton.setPrefHeight(30);

        labelBox = new HBox(15);
        outerBox = new VBox(7);

        playerIndicator = new Label("Player "+ 1);
        playerOneScore = new Label("Player 1: "+getPlayerOneScore());
        playerTwoScore = new Label("Player 2:"+getPlayerTwoScore());

        playerIndicator.setTextFill(Color.ROSYBROWN);
        playerOneScore.setTextFill(Color.BLUEVIOLET);
        playerTwoScore.setTextFill(Color.BLUEVIOLET);
        playerIndicator.setFont(Font.font("times new roman",  20));
        playerOneScore.setFont(Font.font("times new roman",  15));
        playerTwoScore.setFont(Font.font("times new roman",  15));
        buttonBox = new HBox(1);
        pillarBox = new HBox(1);
        outerBox.setAlignment(Pos.CENTER);
        labelBox.setAlignment(Pos.CENTER);
        buttonBox.setAlignment(Pos.CENTER);
        pillarBox.setAlignment(Pos.CENTER);
        labelBox.getChildren().addAll(playerOneScore,playerIndicator,playerTwoScore);
        b = new Button[7];

        pillar = new VBox[7];

        circle = new Circle[7][7];

        for (int i =0; i <7; i++) {
            b[i] = new Button("b1");
            b[i].setTextFill(Color.TRANSPARENT);
            buttonBox.getChildren().add(b[i]);
            b[i].setStyle("-fx-border-color: black; -fx-border-width: 1px;");


            int finalI = i;
            b[i].setOnMouseClicked(mouseEvent -> addBall(finalI));

            pillar[i] = new VBox(2);

            pillar[i].setOnMouseClicked(mouseEvent -> addBall(finalI));

            pillar[i].setStyle("-fx-border-color: black; -fx-border-width: 1px;");
            for (int j = 6; j >= 0; j--) {
                circle[i][j] = new Circle(15,Color.TRANSPARENT);

                circle[i][j].setCenterX(100 + (33) *i + ((i >3) ? 3:0));
                circle[i][j].setCenterY(208 - (32) * (j));



                pillar[i].getChildren().add(circle[i][j]);
            }
            pillarBox.getChildren().add(pillar[i]);
        }
        colorAllButton(!currentPlayer);
        s = new StackPane();

        l = new Line();

        Pane linePane = new Pane(l);
        linePane.setMouseTransparent(true);
        s.getChildren().addAll(pillarBox,linePane);

        outerBox.getChildren().addAll(labelBox,buttonBox,s,replayButton);

        replayButton.setOnMouseClicked(mouseEvent -> replayButtonEvent());

        Scene scene = new Scene(outerBox, 400, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void botMove(){
        addBall(bot.getBestMove(logicalCircles));
    }

    public Boolean getCurrentPlayer(){
        if(currentPlayer){
            playerIndicator.setText("Player " + 2);
        }else{
            playerIndicator.setText("Player " + 1);
        }
        currentPlayer = !currentPlayer;
        return currentPlayer;
    }

    public Color getPlayerColor(Boolean flag) {
        Color c;

        if (flag) {
            c = Color.AQUA;
            playerIndicator.setTextFill(c);
        } else {
            c = Color.ROSYBROWN;
            playerIndicator.setTextFill(c);
        }
        return c;
    }

    public void addBall(int x){
        int y=-1;

        for(int i =0 ;i<7;i++){
            if(logicalCircles[x][i] == 0){
                y=i;
                logicalCircles[x][i] = (currentPlayer) ? 1 : 2;
                Boolean tempBool = getCurrentPlayer();
                circle[x][i].setFill(getPlayerColor(tempBool));
                colorAllButton(!tempBool);
                if(y == 6){
                    pillar[x].setDisable(true);
                    b[x].setDisable(true);

                }
                break;
            }
        }
        printLogicleCircle();
        if(y != -1 && logicChecker(x,y)){
            lineDrawer(startingCoX,startingCoY,destinationCoX,destinationCoY);
            afterWin();
        }else{
            if(!currentPlayer && botIsplaying){
                botMove();
            }
        }

    }

    public void afterWin(){
        try{
        updateScore();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
        playerOneScore.setText("Player 1: "+getPlayerOneScore());
        playerTwoScore.setText("Player 2: " + getPlayerTwoScore());
        }catch(Exception e){
            e.printStackTrace();
        }
        playerIndicator.setText("Winner is Player " + ((currentPlayer) ? 2:1) );
        playerIndicator.setTextFill(getPlayerColor(currentPlayer));
        colorAllButton(currentPlayer);
        s.setEffect(blueEffect);
        pillarBox.setDisable(true);
        buttonBox.setDisable(true);
        labelBox.setSpacing(10);
    }
    public Boolean verticalChecker(int x, int y){
        if(y<3){
            return false;
        }else if(logicalCircles[x][y] == logicalCircles[x][y - 1]
                && logicalCircles[x][y - 1] == logicalCircles[x][y - 2]
                && logicalCircles[x][y - 2] == logicalCircles[x][y - 3]){
            setStartingCo(x,y);
            setDestinationCo(x,y-3);
            return true;
        }
        return false;
    }
    public Boolean horizontalChecker(int x, int y){
        for (int i = 0; i <4; i++) {
            if(logicalCircles[i][y] == logicalCircles[i + 1][y]
                && logicalCircles[i + 1][y] == logicalCircles[i + 2][y]
                && logicalCircles[i + 2][y] == logicalCircles[i + 3][y]
                && logicalCircles[i+3][y] != 0){
                setStartingCo(i,y);
                setDestinationCo(i+3,y);
            return true;
        }
        }
        return false;
    }
    public Boolean positiveSlopeChecker(int x,int y){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(logicalCircles[i][j] == logicalCircles[i + 1][j+ 1]
                        && logicalCircles[i + 1][j + 1] == logicalCircles[i + 2][j + 2]
                        && logicalCircles[i+ 2][j + 2] == logicalCircles[i + 3][j + 3]
                        && logicalCircles[i][j] !=0){
                    setStartingCo(i,j);
                    setDestinationCo(i+3,j+3);
                    return true;
                }
            }
        }
        return false;
    }
    public Boolean negetiveSlopeChecker(int x,int y){
        for (int i = 6; i >2; i--) {
            for (int j = 0; j < 4; j++) {
                if(logicalCircles[i][j] == logicalCircles[i - 1][j + 1]
                        && logicalCircles[i - 1][j + 1] == logicalCircles[i - 2][j + 2]
                        && logicalCircles[i - 2][j + 2] == logicalCircles[i - 3][j + 3]
                        && logicalCircles[i][j] !=0){
                    setStartingCo(i,j);
                    setDestinationCo(i-3,j+3);
                    return true;
                }
            }
        }
        return false;
    }
    public Boolean logicChecker(int x,int y){
        return verticalChecker(x, y) || horizontalChecker(x, y) || positiveSlopeChecker(x, y) || negetiveSlopeChecker(x, y);
    }
    public void colorAllButton(Boolean bool){
        for (int i = 0; i < 7; i++) {
            b[i].setBackground(new Background(new BackgroundFill(getPlayerColor(bool),null,null)));
        }
    }
    public void replayButtonEvent(){
        currentPlayer = true;
        playerIndicator.setText("Player "+((currentPlayer) ? 1:2));
        playerIndicator.setTextFill(getPlayerColor(!currentPlayer));
        colorAllButton(!currentPlayer);
        s.setEffect(null);
        pillarBox.setDisable(false);
        buttonBox.setDisable(false);
        labelBox.setSpacing(15);

        for (int i = 0; i < 7; i++) {
            pillar[i].setDisable(false);
            b[i].setDisable(false);
            for (int j = 0; j < 7; j++) {
                circle[i][j].setFill(Color.TRANSPARENT);
                logicalCircles[i][j] = 0;
            }
        }

        l.setStartX(0);
        l.setStartY(0);
        l.setEndX(0);
        l.setEndY(0);
    }
    public int getPlayerOneScore() throws IOException {
        int playerOneScore;
        String[] s = getScore();
        playerOneScore = Integer.parseInt(s[0]);
        return playerOneScore;
    }
    public int getPlayerTwoScore() throws FileNotFoundException{
        int playerTwoScore;
        String[] s = getScore();
        playerTwoScore = Integer.parseInt(s[1]);
        return playerTwoScore;
    }
    private String[] getScore() throws FileNotFoundException {
        File file = new File("ex.txt");
        Scanner sc = new Scanner(file);
        String[] s = new String[2];

        int i = 0;
        while (sc.hasNext()) {
            s[i] = sc.nextLine();
            i++;
        }

        return s;
    }
    public void updateScore() throws IOException {
        int playerOneScore = getPlayerOneScore()+ ((currentPlayer) ? 0:1);
        int playerTwoScore = getPlayerTwoScore()+ ((currentPlayer) ? 1:0);
        try{
            FileWriter fWriter= new FileWriter("ex.txt");
            fWriter.write(playerOneScore + "\n" + playerTwoScore);
            fWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void setDestinationCo(int x,int y){
        destinationCoX = x;
        destinationCoY = y;
    }

    public void setStartingCo(int x,int y){
        startingCoX = x;
        startingCoY = y;
    }

    public void lineDrawer(int x1,int y1,int x2,int y2){
//        l = new Line(circle[x1][y1].getCenterX(),circle[x1][y1].getCenterY(),circle[x2][y2].getCenterX(),circle[x2][y2].getCenterY());
        l.setEndX(circle[x1][y1].getCenterX());
        l.setEndY(circle[x1][y1].getCenterY());
        l.setStartX(circle[x2][y2].getCenterX());
        l.setStartY(circle[x2][y2].getCenterY());
        l.setStrokeWidth(2);
        l.setFill(Color.DARKGREY);
    }

    public void printLogicleCircle(){
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(logicalCircles[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
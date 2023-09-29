package com.example.simplegame;


public class Move {

    private int column;
    private int value;


    public Move(int value) {
        this.value = value;
    }

    public Move(int col, int value) {
        this.column = col;
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public int getColumn(){
        return column;
    }

    public void setValue(int value){
        this.value = value;
    }

    public void setColumn(int column){
        this.column = column;
    }

}

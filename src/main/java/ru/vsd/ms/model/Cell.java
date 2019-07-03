package ru.vsd.ms.model;

public class Cell {
    private int row;
    private int column;
    private CellState state;
    private boolean mined;
    private int counter;
    private boolean mistakeMarked;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
        mined = false;
        counter = 0;
        state = CellState.CLOSED;
        mistakeMarked = false;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public CellState getState() {
        return state;
    }

    public boolean isMined() {
        return mined;
    }

    public int getCounter() {
        return counter;
    }

    public boolean isMistakeMarked() {
        return mistakeMarked;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public void setMined(boolean mined) {
        this.mined = mined;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setMistakeMarked(boolean mistakeMarked) {
        this.mistakeMarked = mistakeMarked;
    }


}
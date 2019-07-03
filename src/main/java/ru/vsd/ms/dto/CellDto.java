package ru.vsd.ms.dto;

import ru.vsd.ms.model.Cell;
import ru.vsd.ms.model.CellState;

public class CellDto {
    private int row;
    private int column;
    private CellState state;
    private boolean mined;
    private int counter;
    private boolean isMistaked;

    public CellDto(Cell cell) {
        row = cell.getRow();
        column = cell.getColumn();
        state = cell.getState();
        mined = cell.isMined();
        counter = cell.getCounter();
        isMistaked = cell.isMistakeMarked();
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

    public boolean isMistaked() {
        return isMistaked;
    }
}

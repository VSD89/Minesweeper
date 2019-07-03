package ru.vsd.ms.model;

import ru.vsd.ms.dto.CellDto;
import ru.vsd.ms.dto.FieldDto;
import ru.vsd.ms.highscores.HighScores;
import ru.vsd.ms.highscores.Record;
import ru.vsd.ms.level.GameLevel;
import ru.vsd.ms.observer.Observer;
import ru.vsd.ms.observer.Subject;
import ru.vsd.ms.service.ScoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Model implements Subject {
    private static final int AMOUNT_OF_HIGHSCORES = 10;
    private int fieldWidth;
    private int fieldLength;
    private int amountOfMines;
    private GameLevel currentLevel;
    private HighScores highScores;
    private Cell[][] cells;
    private ArrayList<Observer> observers;
    private boolean isGameOver;
    private boolean isGameWin;
    private boolean isMistake;
    private boolean isAnythingPushed;
    private boolean hasPreviousTimer;

    public Model() {
        observers = new ArrayList<>();
        highScores = new HighScores();
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        int i = observers.indexOf(o);
        if (i >= 0) {
            observers.remove(o);
        }
    }

    @Override
    public void notifyObservers(CellDto cell) {
        for (Observer observer : observers) {
            observer.update(cell);
        }
    }

    @Override
    public void notifyGameBegins(FieldDto field) {
        for (Observer observer : observers) {
            observer.startGame(field);
        }
    }

    @Override
    public void notifyGameEnds(CellDto cell) {
        for (Observer observer : observers) {
            observer.finishGame(cell);
        }
    }

    @Override
    public void notifyGameWin() {
        for (Observer observer : observers) {
            observer.winGame();
        }
    }

    @Override
    public void notifyTimer(long time) {
        for (Observer observer : observers) {
            observer.printTime(time);
            if (isGameWin && (time > 0) && checkAddHighScore(time)) {
                observer.recordTime(time);
            }
        }
    }

    @Override
    public void notifyStartTimer() {
        for (Observer observer : observers) {
            observer.startTimer();
        }
    }

    @Override
    public void notifyStopTimer() {
        for (Observer observer : observers) {
            observer.stopTimer();
        }
    }

    @Override
    public void notifyShowHighScores(GameLevel level) {
        for (Observer observer : observers) {
            switch (level) {
                case JUNIOR:
                    observer.updateHighScores(highScores.getJuniors(), GameLevel.JUNIOR);
                    break;
                case MIDDLE:
                    observer.updateHighScores(highScores.getMiddles(), GameLevel.MIDDLE);
                    break;
                case SENIOR:
                    observer.updateHighScores(highScores.getSeniors(), GameLevel.SENIOR);
                    break;
                default:
                    break;
            }
        }
    }

    public void addNewRecord(long time, String name) {
        Record record = new Record(name, time, ScoreService.getGameLevelByCode(String.valueOf(currentLevel)));
        switch (currentLevel) {
            case JUNIOR:
                highScores.updateJuniors(record);
                break;
            case MIDDLE:
                highScores.updateMiddles(record);
                break;
            case SENIOR:
                highScores.updateSeniors(record);
                break;
            default:
                break;
        }
        notifyShowHighScores(currentLevel);
    }

    private boolean checkAddHighScore(long time) {
        switch (currentLevel) {
            case JUNIOR:
                if ((highScores.getJuniors().size() < AMOUNT_OF_HIGHSCORES) || (time < highScores.getJuniors().get(AMOUNT_OF_HIGHSCORES - 1).getTime())) {
                    return true;
                }
                break;
            case MIDDLE:
                if ((highScores.getMiddles().size() < AMOUNT_OF_HIGHSCORES) || (time < highScores.getMiddles().get(AMOUNT_OF_HIGHSCORES - 1).getTime())) {
                    return true;
                }
                break;
            case SENIOR:
                if ((highScores.getSeniors().size() < AMOUNT_OF_HIGHSCORES) || (time < highScores.getSeniors().get(AMOUNT_OF_HIGHSCORES - 1).getTime())) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void setCustomParameters(int width, int length, int mines) {
        fieldWidth = width;
        fieldLength = length;
        amountOfMines = mines;
    }

    public void generateField() {
        generateField(currentLevel);
    }

    public void generateField(GameLevel level) {
        currentLevel = level;
        switch (level) {
            case JUNIOR:
                fieldWidth = 9;
                fieldLength = 9;
                amountOfMines = 10;
                break;
            case MIDDLE:
                fieldWidth = 16;
                fieldLength = 16;
                amountOfMines = 40;
                break;
            case SENIOR:
                fieldWidth = 16;
                fieldLength = 30;
                amountOfMines = 99;
                break;
            default:
                break;
        }
        if (hasPreviousTimer) {
            notifyStopTimer();
        }
        isGameOver = false;
        isGameWin = false;
        isMistake = false;
        isAnythingPushed = false;
        cells = new Cell[fieldWidth][fieldLength];
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldLength; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        int fieldSize = fieldLength * fieldWidth;
        List<Boolean> list = new ArrayList<>();
        for (int i = 0; i < amountOfMines; i++) {
            list.add(true);
        }
        for (int i = 0; i < fieldSize - amountOfMines; i++) {
            list.add(false);
        }
        Collections.shuffle(list);

        for (int i = 0; i < fieldSize; i++) {
            int row = i / fieldLength;
            int column = i % fieldLength;
            cells[row][column].setMined(list.get(i));
        }

        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldLength; j++) {
                countMinesAroundCell(i, j);
            }
        }
        notifyGameBegins(new FieldDto(fieldWidth, fieldLength));
    }

    public void openThisCell(int row, int column) {
        if (!isAnythingPushed) {
            isAnythingPushed = true;
            hasPreviousTimer = true;
            notifyStartTimer();
        }
        if (isGameWin) {
            return;
        }
        switch (cells[row][column].getState()) {
            case OPENED:
                if (!isGameOver && !isMistake) {
                    openCellAroundThis(row, column);
                }
                break;
            case MARKED:
                if (isGameOver && cells[row][column].isMistakeMarked()) {
                    cells[row][column].setState(CellState.MISTAKED);
                }
                break;
            case CLOSED:
            case QUESTIONED:
                cells[row][column].setState(CellState.OPENED);
                if (!isMistake) {
                    openCellAroundThis(row, column);
                }
                break;
            default:
                break;
        }

        if (!isGameOver && (cells[row][column].isMined()) && (cells[row][column].getState().equals(CellState.OPENED))) {
            cells[row][column].setState(CellState.REDBOMBED);
            isGameOver = true;
            notifyStopTimer();
            notifyGameEnds(new CellDto(cells[row][column]));
        }

        notifyObservers(new CellDto(cells[row][column]));

        if ((isWinCondition()) && !isGameWin) {
            isGameWin = true;
            notifyStopTimer();
            notifyGameWin();
        }
    }

    private boolean checkFakesAroundThis(int row, int column) {
        boolean hasFakeAround = false;
        for (int i = row - 1; i < row + 2; i++) {
            for (int j = column - 1; j < column + 2; j++) {
                if (!checkOutOfBounds(row, column, i, j) && (cells[i][j].isMistakeMarked())) {
                    hasFakeAround = true;
                    break;
                }
            }
        }
        return hasFakeAround;
    }

    private void openCellAroundThis(int row, int column) {
        for (int i = row - 1; i < row + 2; i++) {
            for (int j = column - 1; j < column + 2; j++) {
                if (checkOutOfBounds(row, column, i, j)) continue;
                openCellAroundZeroCounter(row, column, i, j);
                openCellAroundNumberCounter(row, column, i, j);
            }
        }
    }

    private void openCellAroundZeroCounter(int row, int column, int i, int j) {
        if ((cells[row][column].getCounter() == 0) && !(cells[i][j].getState().equals(CellState.QUESTIONED) || (cells[i][j].getState().equals(CellState.OPENED)))) {
            openThisCell(i, j);
        }
    }

    private void openCellAroundNumberCounter(int row, int column, int i, int j) {
        if (isAllMinesAroundMarked(row, column) && (!(cells[i][j].getState().equals(CellState.QUESTIONED) || (cells[i][j].getState().equals(CellState.OPENED))))) {
            if (checkFakesAroundThis(row, column)) {
                isMistake = true;
            }
            openThisCell(i, j);
        }
    }

    private boolean isAllMinesAroundMarked(int row, int column) {
        int markedCounter = 0;
        for (int i = row - 1; i < row + 2; i++) {
            for (int j = column - 1; j < column + 2; j++) {
                if (checkOutOfBounds(row, column, i, j)) continue;
                if (cells[i][j].getState().equals(CellState.MARKED)) {
                    markedCounter++;
                }
            }
        }
        return cells[row][column].getCounter() == markedCounter;
    }

    public void markCell(int row, int column) {
        if (!isAnythingPushed) {
            isAnythingPushed = true;
            hasPreviousTimer = true;
            notifyStartTimer();
        }
        if (isGameWin) {
            return;
        }
        if (!isGameOver) {
            switch (cells[row][column].getState()) {
                case OPENED:
                    break;
                case CLOSED:
                    cells[row][column].setState(CellState.MARKED);
                    if (!cells[row][column].isMined()) {
                        cells[row][column].setMistakeMarked(true);
                    }
                    break;
                case MARKED:
                    cells[row][column].setState(CellState.QUESTIONED);
                    cells[row][column].setMistakeMarked(false);
                    break;
                case QUESTIONED:
                    cells[row][column].setState(CellState.CLOSED);
                    break;
                default:
                    break;
            }
        }
        notifyObservers(new CellDto(cells[row][column]));

        if (isWinCondition()) {
            isGameWin = true;
            notifyStopTimer();
            notifyGameWin();
        }
    }

    private void countMinesAroundCell(int row, int column) {
        for (int i = row - 1; i < row + 2; i++) {
            for (int j = column - 1; j < column + 2; j++) {
                if (checkOutOfBounds(row, column, i, j)) continue;
                if (cells[i][j].isMined()) {
                    cells[row][column].setCounter(cells[row][column].getCounter() + 1);
                }
            }
        }
    }

    private boolean checkOutOfBounds(int row, int column, int i, int j) {
        return (((i >= fieldWidth) || (j >= fieldLength) || (i < 0) || (j < 0)) || ((i == row) && (j == column)));
    }

    private boolean isWinCondition() {
        int counter = 0;

        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldLength; j++) {
                if (((cells[i][j].getState().equals(CellState.MARKED)) && (cells[i][j].isMined())) ||
                        ((cells[i][j].getState().equals(CellState.OPENED)) && (!cells[i][j].isMined()))) {
                    counter++;
                }
            }
        }
        return counter == fieldWidth * fieldLength;
    }
}
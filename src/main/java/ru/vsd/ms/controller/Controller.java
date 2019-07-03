package ru.vsd.ms.controller;

import ru.vsd.ms.level.GameLevel;
import ru.vsd.ms.model.Model;

import javax.swing.*;

import static java.lang.System.nanoTime;

public class Controller {
    private Model model;
    MinesweeperTimer minesweeperTimer;

    public Controller(Model model) {
        this.model = model;
    }

    public void openCell(int row, int column) {
        model.openThisCell(row, column);
    }

    public void markCell(int row, int column) {
        model.markCell(row, column);
    }

    public void newGame(GameLevel level) {
        model.generateField(level);
    }

    public void newGame(int width, int length, int mines) {
        model.setCustomParameters(width, length, mines);
        model.generateField(GameLevel.CUSTOM);
    }

    public void newSameGame() {
        model.generateField();
    }

    public void startTimer() {
        minesweeperTimer = new MinesweeperTimer();
    }

    public void stopTimer() {
        minesweeperTimer.timer.stop();
        minesweeperTimer.counter = 0;
        model.notifyTimer(minesweeperTimer.timerCount);
        minesweeperTimer.timerCount = 0;
        model.notifyTimer(minesweeperTimer.timerCount);
    }

    public void readHighScores(GameLevel level) {
        model.notifyShowHighScores(level);
    }

    public void addHighScore(long time, String name) {
        model.addNewRecord(time, name);
    }

    class MinesweeperTimer {
        private int counter;
        private long timerCount;
        private int timerDel;
        private int timerStep;
        private Timer timer;
        long startTime;

        public MinesweeperTimer() {
            timerDel = 20;
            timerStep = 10;
            startTime = nanoTime();
            timer = new Timer(timerStep, e -> {
                counter--;
                if (counter < 1) {
                    timerCount = (nanoTime() - startTime) / 1000000000;
                    model.notifyTimer(timerCount);
                    counter = timerDel;
                }
            }
            );
            timer.start();
        }
    }
}

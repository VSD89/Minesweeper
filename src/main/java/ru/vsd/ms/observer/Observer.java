package ru.vsd.ms.observer;

import ru.vsd.ms.dto.CellDto;
import ru.vsd.ms.dto.FieldDto;
import ru.vsd.ms.highscores.Record;
import ru.vsd.ms.level.GameLevel;

import java.util.List;

public interface Observer {
    void update(CellDto cell);

    void startGame(FieldDto field);

    void finishGame(CellDto cell);

    void winGame();

    void printTime(long time);

    void startTimer();

    void stopTimer();

    void showHighScores(GameLevel level);

    void updateHighScores(List<Record> scores, GameLevel watchingLevel);

    void recordTime(long time);

}

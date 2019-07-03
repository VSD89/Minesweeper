package ru.vsd.ms.observer;

import ru.vsd.ms.dto.CellDto;
import ru.vsd.ms.dto.FieldDto;
import ru.vsd.ms.level.GameLevel;

public interface Subject {
    void registerObserver(Observer o);

    void removeObserver(Observer o);

    void notifyObservers(CellDto cell);

    void notifyGameBegins(FieldDto field);

    void notifyGameEnds(CellDto cell);

    void notifyGameWin();

    void notifyTimer(long time);

    void notifyStartTimer();

    void notifyStopTimer();

    void notifyShowHighScores(GameLevel level);
}

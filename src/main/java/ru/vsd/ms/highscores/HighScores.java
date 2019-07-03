package ru.vsd.ms.highscores;

import ru.vsd.ms.level.GameLevel;
import ru.vsd.ms.service.ScoreService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.vsd.ms.level.GameLevel.*;

public class HighScores {
    private static final int AMOUNT_OF_HIGHSCORES = 10;
    private Map<GameLevel, List<Record>> usersByLevel;
    private List<Record> juniors;
    private List<Record> middles;
    private List<Record> seniors;
    private ScoreService scoreService;


    public HighScores() {
        scoreService = new ScoreService();
        List<Record> users = scoreService.readScores();
        usersByLevel = users.stream().collect(Collectors.groupingBy(Record::getLevel));
        juniors = new ArrayList<>();
        juniors = usersByLevel.get(JUNIOR);
        middles = usersByLevel.get(MIDDLE);
        seniors = usersByLevel.get(SENIOR);
    }

    public List<Record> getJuniors() {
        return juniors == null ? new ArrayList<>() : juniors;
    }

    public void updateJuniors(Record record) {
        juniors = getJuniors();
        if (juniors.size() == AMOUNT_OF_HIGHSCORES) {
            juniors.remove(AMOUNT_OF_HIGHSCORES - 1);
        }
        juniors.add(record);
        Collections.sort(juniors);
        writeRecords();
    }

    public List<Record> getMiddles() {
        return middles == null ? new ArrayList<>() : middles;
    }

    public void updateMiddles(Record record) {
        middles = getMiddles();
        if (middles.size() == AMOUNT_OF_HIGHSCORES) {
            middles.remove(AMOUNT_OF_HIGHSCORES - 1);
        }
        middles.add(record);
        Collections.sort(middles);
        writeRecords();
    }

    public List<Record> getSeniors() {
        return seniors == null ? new ArrayList<>() : seniors;
    }

    public void updateSeniors(Record record) {
        if (seniors.size() == AMOUNT_OF_HIGHSCORES) {
            seniors.remove(AMOUNT_OF_HIGHSCORES - 1);
        }
        seniors = getSeniors();
        seniors.add(record);
        Collections.sort(seniors);
        writeRecords();
    }

    public void writeRecords() {
        List<Record> objects = new ArrayList<>();
        objects.addAll(getJuniors());
        objects.addAll(getMiddles());
        objects.addAll(getSeniors());
        scoreService.writeScores(objects);
    }
}

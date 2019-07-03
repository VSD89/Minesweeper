package ru.vsd.ms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vsd.ms.highscores.Record;
import ru.vsd.ms.level.GameLevel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ru.vsd.ms.level.GameLevel.*;

public class ScoreService {
    private static final Logger log = LoggerFactory.getLogger(ScoreService.class);
    private static final String SEPARATOR = "$";
    private String filepath = getClass().getResource("/data/data.txt").getPath();

    public List<Record> readScores() {
        ArrayList<Record> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line = reader.readLine();
            while (line != null) {
                String[] userRaw = line.split("\\$");
                records.add(new Record(userRaw[0], Long.parseLong(userRaw[1]), getGameLevelByCode(userRaw[2])));
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            log.error("Не удалось открыть файл с рекордами", e);
        } catch (IOException e) {
            log.error("Произошла ошибка ввода/вывода", e);
        }
        return records;
    }

    public void writeScores(List<Record> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            StringBuilder s = new StringBuilder();
            for (Record record : records) {
                s.append(record.getName() + SEPARATOR + record.getTime() + SEPARATOR + getCodeByGameLevel(record.getLevel()) + "\n");
            }
            writer.write(s.toString());
            writer.flush();
        } catch (IOException e) {
            log.error("Произошла ошибка ввода/вывода", e);
        }
    }

    public static GameLevel getGameLevelByCode(String code) {
        switch (code) {
            case "0":
                return JUNIOR;
            case "1":
                return MIDDLE;
            case "2":
                return SENIOR;
            default:
                return JUNIOR;
        }
    }

    private String getCodeByGameLevel(GameLevel level) {
        switch (level) {
            case JUNIOR:
                return "0";
            case MIDDLE:
                return "1";
            case SENIOR:
                return "2";
            default:
                return "0";
        }
    }
}

package ru.vsd.ms.highscores;

import ru.vsd.ms.level.GameLevel;

import java.util.Objects;

public class Record implements Comparable<Record> {
    private String name;
    private long time;
    private GameLevel level;

    public Record(String name, long time, GameLevel level) {
        this.name = name;
        this.time = time;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public GameLevel getLevel() {
        return level;
    }

    public void setLevel(GameLevel level) {
        this.level = level;
    }

    @Override
    public int compareTo(Record r) {
        return (time < r.time) ? -1 : ((time == r.time) ? 0 : 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return time == record.time &&
                name.equals(record.name) &&
                level == record.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, time, level);
    }
}

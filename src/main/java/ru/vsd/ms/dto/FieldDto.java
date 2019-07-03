package ru.vsd.ms.dto;

public class FieldDto {
    private int fieldWidth;
    private int fieldLength;

    public FieldDto(int fieldWidth, int fieldLength) {
        this.fieldWidth = fieldWidth;
        this.fieldLength = fieldLength;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public int getFieldLength() {
        return fieldLength;
    }

}

package ru.khusyainov.gb.java2.hw2.Exceptions;

public class MyArrayDataException extends NumberFormatException {
    public MyArrayDataException(int row, int column, String cellValue) {
        super(getMoreDetailedDescription(row, column, cellValue));
    }

    private static String getMoreDetailedDescription(int row, int column, String cellValue) {
        String str = "";
        for (int i = 0; i < cellValue.length(); i++) {
            if (cellValue.charAt(i) < 48 || cellValue.charAt(i) > 57) {
                str = "the " + (i + 1) + " symbol doesn't starts with a number(s): " + cellValue.substring(i);
                break;
            }
        }
        return "Exception to parse integer in column " + ++column + " of row " + ++row + ", it's value: " + cellValue +
                " (because " + str + ").";
    }
}

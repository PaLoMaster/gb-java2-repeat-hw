package ru.khusyainov.gb.java2.hw2.Exceptions;

public class MyArraySizeException extends ArrayIndexOutOfBoundsException {
    private static int allowedArraySize;
    private static final String allowedDescription = "Allowed arrays only " + allowedArraySize + "*" +
            allowedArraySize + ".";

    public MyArraySizeException() {
        super("Array is not acceptable - it's empty, but " + allowedDescription.toLowerCase());
    }

    public MyArraySizeException(int rowsCount) {
        super("Array size is not acceptable (" + rowsCount + " rows instead of " + allowedArraySize + "). " +
                allowedDescription);
    }

    public MyArraySizeException(int row, int columnsCount) {
        super("Array size is not acceptable (" + columnsCount + " columns instead of " + allowedArraySize + " in " +
                ++row + " row). " + allowedDescription);
    }

    public static int getAllowedArraySize() {
        return allowedArraySize;
    }

    public static void setAllowedArraySize(int allowedArraySize) {
        MyArraySizeException.allowedArraySize = allowedArraySize;
    }
}

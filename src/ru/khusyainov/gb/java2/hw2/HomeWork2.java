package ru.khusyainov.gb.java2.hw2;

import ru.khusyainov.gb.java2.hw2.Exceptions.MyArrayDataException;
import ru.khusyainov.gb.java2.hw2.Exceptions.MyArraySizeException;

import java.util.Arrays;

public class HomeWork2 {
    public static void main(String[] args) {
        MyArraySizeException.setAllowedArraySize(4);
        arraySumSafeExecutor(null);
        arraySumSafeExecutor(new String[][]{{"1", "1", "5", "5", "3"}, {"hgfds"}});
        arraySumSafeExecutor(new String[][]{{"1", "1", "5", "5"}, {"hgfds"}, {"hgfds"}, {"hgfds"}});
        arraySumSafeExecutor(new String[][]{{"1", "1", "5", "5"}, {"30", "20", "5", "3"}, {"3", "4"}, {"3"}});
        arraySumSafeExecutor(new String[][]{{"1", "001", "5", "5"}, {"030", "20", "5", "3"}, {"1", "4", "5", "5"}, {"3", "2", "5", "5"}});
        arraySumSafeExecutor(new String[][]{{"1", "001", "5", "5"}, {"03O", "20", "5", "3"}, {"1", "4", "5", "5"}, {"3", "2", "5", "5"}});
    }

    private static void arraySumSafeExecutor(String[][] testingArray) {
        try {
            int sum = arraySum(testingArray);
            System.out.printf("Array: %s\nSum of array: %s\n", Arrays.deepToString(testingArray), sum);
        } catch (MyArraySizeException | MyArrayDataException e) {
            System.err.printf("Exception while array sum: %s\n%s\n", Arrays.deepToString(testingArray), e);
        }
    }

    private static int arraySum(String[][] array) throws RuntimeException {
        int allowedArraySize = MyArraySizeException.getAllowedArraySize();
        if (array == null) {
            throw new MyArraySizeException();
        } else if (array.length != allowedArraySize) {
            throw new MyArraySizeException(array.length);
        }
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].length != allowedArraySize) {
                throw new MyArraySizeException(i, array[i].length);
            }
            for (int j = 0; j < array[i].length; j++) {
                try {
                    sum += Integer.parseInt(array[i][j]);
                } catch (NumberFormatException e) {
                    throw new MyArrayDataException(i, j, array[i][j]);
                }
            }
        }
        return sum;
    }
}
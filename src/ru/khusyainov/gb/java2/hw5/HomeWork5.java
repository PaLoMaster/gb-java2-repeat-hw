package ru.khusyainov.gb.java2.hw5;

import java.util.Arrays;

public class HomeWork5 {
    public static void main(String[] args) {
        boolean arraysEqual = Arrays.equals(createFillRecalculateArrayInOneThread(),
                createFillRecalculateArrayInTwoThreads());
        System.out.println("Arrays of both threads are" + (arraysEqual ? "" : " not") + " equal (calculated correctly).");
    }

    public static float[] createAndFillArray() {
        float[] arr = new float[10_000_000];
        Arrays.fill(arr, 1f);
        return arr;
    }

    private static void recalculateArray(float[] arr) {
        recalculateArrayPart(arr, 0);
    }

    private static void recalculateArrayPart(float[] arr, int previousLength) {
        for (int i = 0, j; i < arr.length; i++) {
            j = i + previousLength;
            arr[i] = (float) (arr[i] * Math.sin(0.2f + j / 5) * Math.cos(0.2f + j / 5) * Math.cos(0.4f + j / 2));
        }
    }

    public static float[] createFillRecalculateArrayInOneThread() {
        float[] arr = createAndFillArray();
        long a = System.currentTimeMillis();
        recalculateArray(arr);
        System.out.println(System.currentTimeMillis() - a + "msec - in one thread (recalculating).");
        return arr;
    }

    public static float[] createFillRecalculateArrayInTwoThreads() {
        float[] arr = createAndFillArray();
        int h = arr.length / 2;
        long a = System.currentTimeMillis();
        float[] arrPart1 = new float[h];
        float[] arrPart2 = new float[h];
        Thread recalculateArrPart1 = new Thread(() -> recalculateArrayPart(arrPart1, 0));
        Thread recalculateArrPart2 = new Thread(() -> recalculateArrayPart(arrPart2, h));
        System.arraycopy(arr, 0, arrPart1, 0, h);
        System.arraycopy(arr, h, arrPart2, 0, h);
        System.out.println(System.currentTimeMillis() - a + "msec - split array into 2 parts.");
        recalculateArrPart1.start();
        recalculateArrPart2.start();
        try {
            recalculateArrPart1.join();
            recalculateArrPart2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - a + "msec - split and recalculating array.");
        System.arraycopy(arrPart1, 0, arr, 0, h);
        System.arraycopy(arrPart2, 0, arr, h, h);
        System.out.println(System.currentTimeMillis() - a +
                "msec - in two threads (split, recalculate and join the recalculated).");
        return arr;
    }
}

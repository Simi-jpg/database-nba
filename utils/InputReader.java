package utils;

import java.util.Scanner;

public class InputReader {
    private static Scanner sc = new Scanner (System.in);

    public static String getString(String str) {
        System.out.print(str);

        return sc.nextLine().trim();
    }
}

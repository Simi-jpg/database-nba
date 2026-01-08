package utils;

public class ClearConsole {
    
    private static final String CLEAR_SCREEN = "\u001b[2J";
    private static final String CURSOR_HOME = "\u001b[H";

    public static void clear() {

        System.out.print(CLEAR_SCREEN + CURSOR_HOME);
        System.out.flush();
    }
}

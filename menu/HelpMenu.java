package menu;

import utils.ClearConsole;
import utils.InputReader;

public class HelpMenu {
    private final String RED_TEXT = "\u001B[31m";
    private final String GREEN_TEXT = "\u001B[32m";
    private final String RESET_TEXT = "\u001B[0m";

    public void display() {
        boolean sessionRunning = true;

        String menu = """
                ====================================================================
                                    Database Info & Instructions
                ====================================================================
                Welcome to the NBA Analytics Database Interface!

                This database provides structured historical information from the National Basketball Association (NBA),
                a North American professional basketball league founded in 1946 and composed of 30 teams. It includes comprehensive data
                on players, teams, coaches, referees, arenas, and games spanning the seasons from 2000 through 2022. The dataset supports
                queries and analysis involving player statistics, team performance, game outcomes, long-term league trends, and more!\n
                Interface Features:
                \t* Run advanced NBA analytical queries
                \t* View and browase the tables within the database
                \t* Clear the database
                \t* Repopulate the database\n
                Navigation:
                \t* Enter a number to select our menu options
                \t* When prompted, you can enter desired input
                \t* Use arrow buttons to scroll up, left, down and right\n
                ** [WARNING] Queries may take several seconds to run depending on the user's input **
                ====================================================================
                """;

        ClearConsole.clear();

        System.out.println(menu);

        while (sessionRunning) {
            String userChoice = InputReader
                    .getString("Press " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");

            if (userChoice.isEmpty()) {
                sessionRunning = false;
            } else {
                System.out.println(RED_TEXT + "Invalid input. Try again." + RESET_TEXT);
            }
        }

        ClearConsole.clear();
    }
}

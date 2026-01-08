package menu;

import utils.ClearConsole;
import utils.InputReader;
import controls.PopulateDataBase;
import controls.ResetDatabase;

public class MainMenu {
    private final String RED_TEXT = "\u001B[31m";
    private final String GREEN_TEXT = "\u001B[32m";
    private final String RESET_TEXT = "\u001B[0m";

    private boolean invalidInput = false;

    public void start() {
        boolean sessionRunning = true;

        while (sessionRunning) {
            ClearConsole.clear();

            printMenu();

            System.out.println();

            String userChoice;
            if (!invalidInput)
                userChoice = InputReader.getString("Enter a menu option: ");
            else {
                System.out.println(RED_TEXT + "Invalid option. Try again." + RESET_TEXT);
                userChoice = InputReader.getString("Enter a menu option: ");
            }

            if (userChoice.matches(("\\d+"))) {
                int tableNum = Integer.parseInt(userChoice);

                switch (tableNum) {
                    case 1 -> { invalidInput = false; new HelpMenu().display(); }
                    case 2 -> { invalidInput = false; new TableMenu().display(); }
                    case 3 -> { invalidInput = false; new QueryMenu().display(); }
                    case 4 -> { invalidInput = false; new PopulateDataBase().repopulate(); }
                    case 5 -> { invalidInput = false; 
                        ResetDatabase.confirm();            
                        ResetDatabase.deleteData(); }
                    case 6 -> {
                        System.out.println(RED_TEXT + "\nClosing program...\n" + RESET_TEXT);
                        sessionRunning = false;
                    }
                    default -> invalidInput = true;
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("=======================================");
        System.out.println("               MAIN MENU               ");
        System.out.println("=======================================");
        System.out.println(GREEN_TEXT + "1." + RESET_TEXT + " Database Info & Instructions");
        System.out.println(GREEN_TEXT + "2." + RESET_TEXT + " View Raw Database Tables");
        System.out.println(GREEN_TEXT + "3." + RESET_TEXT + " Run Queries");
        System.out.println(GREEN_TEXT + "4." + RESET_TEXT + " Repopulate Database");
        System.out.println(GREEN_TEXT + "5." + RESET_TEXT + " Clear Database");
        System.out.println(RED_TEXT + "6." + RESET_TEXT + " Exit");
        System.out.println("=======================================");
    }
}
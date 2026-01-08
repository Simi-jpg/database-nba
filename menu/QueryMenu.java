package menu;

import java.util.Arrays;
import java.util.List;

import utils.ClearConsole;
import utils.InputReader;
import utils.Queries;

public class QueryMenu {
    private static final String RED_TEXT = "\u001B[31m";
    private static final String GREEN_TEXT = "\u001B[32m";
    private static final String BLUE_TEXT = "\u001B[34m";
    private static final String RESET_TEXT = "\u001B[0m";

    public void display() {
        boolean sessionRunning = true;

        while (sessionRunning) {
            ClearConsole.clear();

            int userChoice = printMenu();

            if (userChoice == 0) {
                ClearConsole.clear();
                sessionRunning = false;
                break;
            }

            switch (userChoice) {
                case 1 -> {
                    Queries.queryOne();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }
                case 2 -> {
                    Queries.queryTwo();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }
                case 3 -> {
                    Queries.queryThree();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 4 -> {
                    Queries.queryFour();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 5 -> {
                    Queries.queryFive();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 6 -> {
                    Queries.querySix();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 7 -> {
                    Queries.querySeven();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 8 -> {
                    Queries.queryEight();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 9 -> {
                    Queries.queryNine();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 10 -> {
                    Queries.queryTen();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 11 -> {
                    Queries.queryEleven();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 12 -> {
                    Queries.queryTwelve();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 13 -> {
                    Queries.queryThirteen();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 14 -> {
                    Queries.queryFourteen();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 15 -> {
                    Queries.queryFifteen();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 16 -> {
                    Queries.querySixteen();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 17 -> {
                    Queries.querySeventeen();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 18 -> {
                    Queries.queryEighteen();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 19 -> {
                    Queries.queryNineteen();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 20 -> {
                    Queries.queryTwenty();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 21 -> {
                    Queries.queryTwentyOne();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 22 -> {
                    Queries.queryTwentyTwo();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 23 -> {
                    Queries.queryTwentyThree();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                case 24 -> {
                    Queries.queryTwentyFour();
                    InputReader.getString("\nPress " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
                }

                default -> System.out.println(RED_TEXT + "Invalid input. Try again." + RESET_TEXT);
            }
        }
    }

    private static final List<String> OPTIONS = Arrays.asList(
            GREEN_TEXT + "1." + RESET_TEXT + " Top 10 teams that have scored the highest number of points in a given year *",
            GREEN_TEXT + "2." + RESET_TEXT + " What is the top 10 teams with the highest scoring average (a range of years) **",
            GREEN_TEXT + "3." + RESET_TEXT + " How many players in a particular position does a team have **",
            GREEN_TEXT + "4." + RESET_TEXT + " How many games did a particular team win in a specific arena **",
            GREEN_TEXT + "5." + RESET_TEXT + " Top 5 referees who officiated the most matches",
            GREEN_TEXT + "6." + RESET_TEXT + " Who is the current coach of a given team *",
            GREEN_TEXT + "7." + RESET_TEXT + " What is the average height and weight of players in a team *",
            GREEN_TEXT + "8." + RESET_TEXT + " Which countries have the most players in the top 10 teams (most points) for the season *",
            GREEN_TEXT + "9." + RESET_TEXT + " Players who played for more than n number of teams *",
            GREEN_TEXT + "10." + RESET_TEXT + " Who are the youngest players on each team *",
            GREEN_TEXT + "11." + RESET_TEXT + " Which teams have more losses than wins in each season",
            GREEN_TEXT + "12." + RESET_TEXT + " NBA players that became coaches",
            GREEN_TEXT + "13." + RESET_TEXT + " Which arena has the most playoff games",
            GREEN_TEXT + "14." + RESET_TEXT + " How many games did a player win in a given arena and year ***",
            GREEN_TEXT + "15." + RESET_TEXT + " Who is the least-performing coach (by losses) from a given team *",
            GREEN_TEXT + "16." + RESET_TEXT + " Which team improved the most (comparing the current year and the previous year) **",
            GREEN_TEXT + "17." + RESET_TEXT + " Teams that played against every other teams in a given season *",
            GREEN_TEXT + "18." + RESET_TEXT + " The players who have never lost a game they played in",
            GREEN_TEXT + "19." + RESET_TEXT + " Players who have won more than N games *",
            GREEN_TEXT + "20." + RESET_TEXT + " Top N players in each country (ranked by total games played) *",
            GREEN_TEXT + "21." + RESET_TEXT + " Referees who officiated games for every team in a season",
            GREEN_TEXT + "22." + RESET_TEXT + " Which teams performed the best at home in a specific year *",
            GREEN_TEXT + "23." + RESET_TEXT + " Find the coaches who stayed with teams the longest on average",
            GREEN_TEXT + "24." + RESET_TEXT + " Which arenas hosted the most competitive (close-call) games");

    private static final int PAGE_SIZE = 10;

    private int printMenu() {
        int page = 0;
        int total = OPTIONS.size(); // one option is to go back
        String status = "";

        while (true) {
            int from = page * PAGE_SIZE;
            int to = Math.min(from + PAGE_SIZE, total);

            ClearConsole.clear();

            if (!status.isEmpty()) {

                System.out.println(RED_TEXT + "Status: " + status + RESET_TEXT);
                status = "";
                System.out.println();
            }

            System.out.println("=======================================");
            System.out.println("               QUERIES                 ");
            System.out.println("=======================================");
            System.out.println();

            for (int i = from; i < to; i++) {
                System.out.println(OPTIONS.get(i));
                System.out.println();
            }

            System.out.println("=======================================");

            System.out.printf(BLUE_TEXT + "Page %d/%d" + RESET_TEXT + " || " + BLUE_TEXT + "[N]ext" + RESET_TEXT + " || " + BLUE_TEXT + "[P]rev" + RESET_TEXT + " || " + RED_TEXT + "[0] Return to Main Menu\n" + RESET_TEXT,
                    page + 1,
                    (int) Math.ceil(total / (double) PAGE_SIZE));

            System.out.println();

            String userChoice = InputReader.getString("Enter a query number, N/P, or 0: ");

            if (userChoice.equalsIgnoreCase("n")) {
                if ((page + 1) * PAGE_SIZE < total) {
                    page++;

                } else {
                    status = "Already at the last page";
                }
                continue;
            }

            if (userChoice.equalsIgnoreCase("p")) {
                if (page > 0) {
                    page--;

                } else {
                    status = "Already at the first page";
                }
                continue;
            }

            if (userChoice.matches("\\d+")) {
                int num = Integer.parseInt(userChoice);

                if (num == 0) {
                    return 0;
                }

                if (num >= 1 && num <= total) {

                    return num;
                }

                status = "Invalid query number.";
                continue;
            }

            status = "Invalid input. Use a number (1-24), N, P, or 0.";
        }
    }
}

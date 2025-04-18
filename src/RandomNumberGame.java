import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

class NumberGenerator {
    private Random random = new Random();
    private int[] numbers = new int[6];
    private int targetNumber;

    public void generateNumbers() {
        for (int i = 0; i < 5; i++) {
            numbers[i] = 1 + random.nextInt(9);
        }
        int[] specialNumbers = {25, 50, 75};
        numbers[5] = specialNumbers[random.nextInt(3)];
        targetNumber = 100 + random.nextInt(900);
    }

    public int[] getNumbers() {
        return numbers;
    }

    public int getTargetNumber() {
        return targetNumber;
    }
}

class Timer {
    public void startCountdown(int countdown, AtomicBoolean stopRequested) {
        System.out.println("Countdown begins:");
        for (int i = countdown; i > 0; i--) {
            if (stopRequested.get()) {
                System.out.println("\nTimer stopped!");
                return;
            }
            System.out.print(i + " ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nTime's up!\nYou earned 0 points. Game over.");
        System.exit(0);
    }
}

class UserInputHandler {
    public int getUserGuess(Scanner scanner, AtomicBoolean stopRequested) {
        int userGuess = -1;
        while (true) {
            System.out.print("Enter your guess (minimum 100, 0 = stop the timer): ");
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                if (input == 0) {
                    stopRequested.set(true);
                    return -1;
                }
                if (input < 100) {
                    System.out.println("Please enter a number with at least 3 digits!");
                    continue;
                }
                userGuess = input;
                break;
            } else {
                scanner.next();
            }
        }
        return userGuess;
    }
}

public class RandomNumberGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();
        System.out.println("Hello, " + firstName + " " + lastName + "! Let's start the game...\n");

        boolean playAgain;
        int totalScore = 0;
        int correctAnswers = 0;
        boolean bonusAwarded = false;

        do {
            System.out.println("Select difficulty level: 1 - Easy (90 sec), 2 - Medium (60 sec), 3 - Hard (30 sec)");
            int level = scanner.nextInt();
            int countdown = switch (level) {
                case 1 -> 90;
                case 2 -> 60;
                case 3 -> 30;
                default -> 60;
            };

            NumberGenerator generator = new NumberGenerator();
            generator.generateNumbers();
            int[] numbers = generator.getNumbers();
            int targetNumber = generator.getTargetNumber();

            System.out.print("Generated number set: ");
            for (int num : numbers) {
                System.out.print(num + " ");
            }
            System.out.println("\nTarget number: " + targetNumber);

            AtomicBoolean stopRequested = new AtomicBoolean(false);
            Timer timer = new Timer();
            UserInputHandler inputHandler = new UserInputHandler();

            System.out.println("Try to guess the target number before time runs out! Enter '0' to stop the timer.");
            Thread timerThread = new Thread(() -> timer.startCountdown(countdown, stopRequested));
            timerThread.start();

            long startTime = System.currentTimeMillis();
            int userGuess = inputHandler.getUserGuess(scanner, stopRequested);
            long endTime = System.currentTimeMillis();

            stopRequested.set(true);
            try {
                timerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int elapsedSeconds = (int) ((endTime - startTime) / 1000);
            int score = 0;

            if (userGuess == -1) {
                List<Integer> availableNumbers = new ArrayList<>();
                for (int n : numbers) availableNumbers.add(n);
                List<Integer> results = new ArrayList<>(availableNumbers);

                System.out.println("Entering math operation mode.");
                System.out.println("Available numbers: " + results);

                int startCloseValue = 0;
                for (int r : results) {
                    int diff = Math.abs(r - targetNumber);
                    if (diff <= 3 && diff > 0) {
                        startCloseValue = r;
                        break;
                    }
                }

                if (startCloseValue != 0) {
                    System.out.println("A number close to the target was found: " + startCloseValue);
                    System.out.println("Try reaching the target using operations!");
                }

                while (results.size() >= 2) {
                    System.out.println("Available numbers: " + results);
                    System.out.print("Select the first number: ");
                    int a = scanner.nextInt();
                    if (!results.contains(a)) {
                        System.out.println("This number is not available!");
                        continue;
                    }
                    results.remove((Integer) a);

                    System.out.print("Select the second number: ");
                    int b = scanner.nextInt();
                    if (!results.contains(b)) {
                        results.add(a);
                        System.out.println("This number is not available!");
                        continue;
                    }
                    results.remove((Integer) b);

                    System.out.print("Enter operation (+, -, *, /): ");
                    String op = scanner.next();
                    int res = 0;
                    switch (op) {
                        case "+" -> res = a + b;
                        case "-" -> res = a - b;
                        case "*" -> res = a * b;
                        case "/" -> {
                            if (b == 0 || a % b != 0) {
                                System.out.println("Invalid division!");
                                results.add(a);
                                results.add(b);
                                continue;
                            }
                            res = a / b;
                        }
                        default -> {
                            System.out.println("Invalid operation!");
                            results.add(a);
                            results.add(b);
                            continue;
                        }
                    }
                    System.out.println(a + " " + op + " " + b + " = " + res);
                    results.add(res);

                    int diff = Math.abs(res - targetNumber);
                    if (res == targetNumber) {
                        score = 10;
                        correctAnswers++;
                        break;
                    } else if (diff == 1) score = Math.max(score, 3);
                    else if (diff == 2) score = Math.max(score, 2);
                    else if (diff == 3) score = Math.max(score, 1);
                }
            } else {
                int diff = Math.abs(userGuess - targetNumber);
                if (userGuess == targetNumber) {
                    score = (elapsedSeconds <= 15) ? 10 : (elapsedSeconds <= 30) ? 7 : 5;
                    correctAnswers++;
                } else {
                    score = switch (diff) {
                        case 1 -> 3;
                        case 2 -> 2;
                        case 3 -> 1;
                        default -> 0;
                    };
                }
            }

            if (correctAnswers % 2 == 0 && correctAnswers > 0 && !bonusAwarded) {
                System.out.println("Congratulations! 2 correct guesses in a row, +5 bonus points!");
                score += 5;
                bonusAwarded = true;
            } else if (bonusAwarded && score == 0) {
                System.out.println("Bonus point canceled (wrong answer). -5 points deducted.");
                totalScore -= 5;
                bonusAwarded = false;
            }

            totalScore += score;

            System.out.println("Score: " + score);
            System.out.println("Total score: " + totalScore);

            if (score > 0) {
                System.out.print("Do you want to play again? (Y/N): ");
                char response = scanner.next().charAt(0);
                playAgain = (response == 'Y' || response == 'y');
            } else {
                playAgain = false;
            }
        } while (playAgain);

        System.out.println("Total score: " + totalScore);
        System.out.println("Game over. Exiting program...");
        System.exit(0);
    }
}
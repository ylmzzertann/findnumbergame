import java.util.Random;
import java.util.Scanner;
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
        System.out.println("Geri sayım başlıyor:");
        for (int i = countdown; i > 0; i--) {
            if (stopRequested.get()) {
                System.out.println("\nSüre durduruldu!");
                return;
            }
            System.out.print(i + " ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nSüre doldu!");
        System.out.println("0 Puan kazandınız. Oyun sona erdi.");
        System.exit(0);
    }
}

class UserInputHandler {
    public int getUserGuess(Scanner scanner, AtomicBoolean stopRequested) {
        int userGuess = -1;
        while (true) {
            System.out.print("Tahmininizi girin (En az 100 olmalı, 0 = süreyi durdur): ");
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                if (input == 0) {
                    stopRequested.set(true);
                    continue;
                }
                if (input < 100) {
                    System.out.println("Lütfen en az 3 basamaklı bir sayı girin!");
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
        System.out.print("Adınızı girin: ");
        String firstName = scanner.nextLine();
        System.out.print("Soyadınızı girin: ");
        String lastName = scanner.nextLine();
        System.out.println("Merhaba, " + firstName + " " + lastName + "! Oyun başlıyor...\n");

        boolean playAgain;
        int totalScore = 0;
        
        do {
            System.out.println("Zorluk seviyesini seçin: 1 - Kolay (90 sn), 2 - Orta (60 sn), 3 - Zor (30 sn)");
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

            System.out.print("Oluşturulan sayı dizisi: ");
            for (int num : numbers) {
                System.out.print(num + " ");
            }
            System.out.println("\nHedeflenen 3 basamaklı sayı: " + targetNumber);

            AtomicBoolean stopRequested = new AtomicBoolean(false);
            Timer timer = new Timer();
            UserInputHandler inputHandler = new UserInputHandler();

            System.out.println("Lütfen süre dolmadan hedef sayıyı tahmin edin! Süreyi durdurmak için '0' yazın.");
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
            int diff = Math.abs(userGuess - targetNumber);
            
            if (userGuess == targetNumber) {
                score = (elapsedSeconds <= 15) ? 10 : (elapsedSeconds <= 30) ? 7 : 5;
            } else {
                score = switch (diff) {
                    case 1 -> 3;
                    case 2 -> 2;
                    case 3 -> 1;
                    default -> 0;
                };
            }
            totalScore += score;
            
            System.out.println("Tahmininiz: " + userGuess + " | Hedef: " + targetNumber);
            System.out.println("Puanınız: " + score);
            System.out.println("Toplam Puanınız: " + totalScore);
            System.out.println("Tahmininiz hedef sayıya " + diff + " kadar uzaklıkta.");
            
            if (diff <= 3) {
                System.out.print("Oyunu tekrar oynamak ister misiniz? (Evet için 'E' / Hayır için 'H'): ");
                char response = scanner.next().charAt(0);
                playAgain = (response == 'E' || response == 'e');
            } else {
                playAgain = false;
            }
        } while (playAgain);
        
        System.out.println("Oyun sona erdi. Toplam puanınız: " + totalScore);
        System.out.println("Program kapanıyor...");
        System.exit(0);
    }
}

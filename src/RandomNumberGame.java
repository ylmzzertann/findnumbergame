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
        System.out.println("\nSüre doldu!\n0 puan kazandınız. Oyun sona erdi.");
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
                    return -1;
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
        int correctAnswers = 0;
        boolean bonusAwarded = false;

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

            if (userGuess == -1) {
                List<Integer> availableNumbers = new ArrayList<>();
                for (int n : numbers) availableNumbers.add(n);
                List<Integer> results = new ArrayList<>(availableNumbers);

                System.out.println("Matematik işlem moduna geçildi.");
                System.out.println("Kullanabileceğiniz sayılar: " + results);

                int startCloseValue = 0;
                for (int r : results) {
                    int diff = Math.abs(r - targetNumber);
                    if (diff <= 3 && diff > 0) {
                        startCloseValue = r;
                        break;
                    }
                }

                if (startCloseValue != 0) {
                    System.out.println("Başlangıçta hedef sayıya yakın bir sayı bulundu: " + startCloseValue);
                    System.out.println("İşlem yaparak hedefe ulaşmayı deneyin!");
                }

                while (results.size() >= 2) {
                    System.out.println("Kullanılabilir sayılar: " + results);
                    System.out.print("İlk sayıyı seçin: ");
                    int a = scanner.nextInt();
                    if (!results.contains(a)) {
                        System.out.println("Bu sayı kullanılamaz!");
                        continue;
                    }
                    results.remove((Integer) a);

                    System.out.print("İkinci sayıyı seçin: ");
                    int b = scanner.nextInt();
                    if (!results.contains(b)) {
                        results.add(a);
                        System.out.println("Bu sayı kullanılamaz!");
                        continue;
                    }
                    results.remove((Integer) b);

                    System.out.print("İşlem girin (+, -, *, /): ");
                    String op = scanner.next();
                    int res = 0;
                    switch (op) {
                        case "+" -> res = a + b;
                        case "-" -> res = a - b;
                        case "*" -> res = a * b;
                        case "/" -> {
                            if (b == 0 || a % b != 0) {
                                System.out.println("Geçersiz bölme işlemi!");
                                results.add(a);
                                results.add(b);
                                continue;
                            }
                            res = a / b;
                        }
                        default -> {
                            System.out.println("Geçersiz işlem!");
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
                System.out.println("Tebrikler! Üst üste 2 doğru tahmin yaptınız, +5 bonus puan!");
                score += 5;
                bonusAwarded = true;
            } else if (bonusAwarded && score == 0) {
                System.out.println("Bonus puanınız iptal edildi (yanlış cevap). -5 puan düşüldü.");
                totalScore -= 5;
                bonusAwarded = false;
            }

            totalScore += score;

            System.out.println("Puanınız: " + score);
            System.out.println("Toplam puanınız: " + totalScore);

            if (score > 0) {
                System.out.print("Oyunu tekrar oynamak ister misiniz? (Evet için 'E' / Hayır için 'H'): ");
                char response = scanner.next().charAt(0);
                playAgain = (response == 'E' || response == 'e');
            } else {
                playAgain = false;
            }
        } while (playAgain);

        System.out.println("Toplam puanınız: " + totalScore);
        System.out.println("Oyun sona erdi. Program kapanıyor...");
        System.exit(0);
    }
}

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomNumberGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Kullanıcıdan ad ve soyad alma
        System.out.print("Adınızı girin: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Soyadınızı girin: ");
        String lastName = scanner.nextLine();
        
        System.out.println("Merhaba, " + firstName + " " + lastName + "! Oyun başlıyor...\n");
        
        // Seviye seçimi
        System.out.println("Zorluk seviyesini seçin: 1 - Kolay (90 sn), 2 - Orta (60 sn), 3 - Zor (30 sn)");
        int level = scanner.nextInt();
        int countdown;
        
        switch (level) {
            case 1:
                countdown = 90;
                break;
            case 2:
                countdown = 60;
                break;
            case 3:
                countdown = 30;
                break;
            default:
                System.out.println("Geçersiz seçim! Varsayılan olarak Orta seviye seçildi (60 sn)");
                countdown = 60;
        }
        
        Random random = new Random();
        int[] numbers = new int[6];
        
        // 5 adet rastgele tek basamaklı sayı (1-9 arası, 0 hariç)
        for (int i = 0; i < 5; i++) {
            numbers[i] = 1 + random.nextInt(9);
        }
        
        // Çift basamaklı sayı (25, 50, 75)
        int[] specialNumbers = {25, 50, 75};
        numbers[5] = specialNumbers[random.nextInt(3)];
        
        // 3 basamaklı rastgele bir sayı (100-999 arası)
        int targetNumber = 100 + random.nextInt(900);
        
        // Sayıları ekrana yazdır
        System.out.print("Oluşturulan sayı dizisi: ");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
        
        System.out.println("\nHedeflenen 3 basamaklı sayı: " + targetNumber);
        
        // Geri sayım başlat
        AtomicInteger userGuess = new AtomicInteger(-1);
        AtomicInteger guessCount = new AtomicInteger(0);
        AtomicBoolean stopRequested = new AtomicBoolean(false);
        boolean[] correctGuess = {false};
        boolean[] countdownStopped = {false};
        long startTime = System.currentTimeMillis();

        System.out.println("Lütfen süre dolmadan hedef sayıyı tahmin edin! Çoklu tahmin hakkınız var. Süreyi durdurmak için '0' yazın.");
        
        Thread inputThread = new Thread(() -> {
            while (true) {
                System.out.print("Tahmininizi girin: ");
                if (scanner.hasNextInt()) {
                    int input = scanner.nextInt();
                    if (input == 0) {
                        stopRequested.set(true);
                        countdownStopped[0] = true;
                        break;
                    }
                    userGuess.set(input);
                    guessCount.incrementAndGet();
                    
                    if (userGuess.get() == targetNumber) {
                        correctGuess[0] = true;
                        break;
                    }
                }
            }
        });
        
        inputThread.start();
        
        System.out.print("Geri sayım: [");
        for (int i = countdown; i > 0; i--) {
            if (countdownStopped[0]) {
                break;
            }
            System.out.print("*");
            
            try {
                Thread.sleep(1000); // 1 saniye bekle
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (correctGuess[0] || !inputThread.isAlive()) {
                break;
            }
        }
        System.out.println("]");
        
        long endTime = System.currentTimeMillis();
        int elapsedSeconds = (int) ((endTime - startTime) / 1000);
        int score = 0;

        if (stopRequested.get()) {
            System.out.println("Süre kullanıcı tarafından durduruldu! Şimdi tahmininizi girin:");
            int finalGuess = scanner.nextInt();
            if (finalGuess == targetNumber) {
                if (elapsedSeconds <= 15) {
                    score = 10;
                } else if (elapsedSeconds <= 30) {
                    score = 7;
                } else {
                    score = 5;
                }
                System.out.println("Tebrikler! Doğru tahmin ettiniz: " + finalGuess + " Puanınız: " + score);
            } else {
                System.out.println("Yanlış tahmin! Doğru sayı: " + targetNumber);
            }
        } else if (correctGuess[0]) {
            if (elapsedSeconds <= 15) {
                score = 10;
            } else if (elapsedSeconds <= 30) {
                score = 7;
            } else {
                score = 5;
            }
            System.out.println("Tebrikler! Doğru tahmin ettiniz: " + userGuess.get() + " (" + guessCount.get() + " deneme yaptınız) Puanınız: " + score);
        } else {
            System.out.println("Süre doldu veya yanlış tahmin! Doğru sayı: " + targetNumber);
        }
        
        System.out.println("Oyun sona erdi. Program kapanıyor...");
        System.exit(0);
    }
}
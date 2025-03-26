import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Kullanıcıdan ad ve soyad alma
        System.out.print("Adınızı girin: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Soyadınızı girin: ");
        String lastName = scanner.nextLine();
        
        System.out.println("Merhaba, " + firstName + " " + lastName + "! Oyun başlıyor...\n");
        
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
        int countdown = 60;
        AtomicInteger userGuess = new AtomicInteger(-1);
        
        System.out.println("Lütfen süre dolmadan hedef sayıyı tahmin edin!");
        
        Thread inputThread = new Thread(() -> {
            System.out.print("Tahmininizi girin: ");
            if (scanner.hasNextInt()) {
                userGuess.set(scanner.nextInt());
            }
        });
        
        inputThread.start();
        
        while (countdown > 0) {
            System.out.println("Kalan süre: " + countdown + " saniye");
            
            try {
                Thread.sleep(1000); // 1 saniye bekle
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            countdown--;
            
            if (!inputThread.isAlive()) {
                break;
            }
        }
        
        if (userGuess.get() == targetNumber) {
            System.out.println("Tebrikler! Doğru tahmin ettiniz: " + userGuess.get());
        } else {
            System.out.println("Süre doldu veya yanlış tahmin! Doğru sayı: " + targetNumber);
        }
        
        System.out.println("Oyun sona erdi. Program kapanıyor...");
        System.exit(0);
    }
}

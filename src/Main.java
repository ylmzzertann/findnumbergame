import java.util.Random;

public class Main {
    public static void main(String[] args) {
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
        
        // Sayı dizisini yazdır
        System.out.print("Oluşturulan sayı dizisi: ");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
        
        System.out.println("\nHedeflenen 3 basamaklı sayı: " + targetNumber);
    }
}

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                int amountR = (int) Arrays.stream(generateRoute("RLRFR", 100).split(""))
                        .filter(x -> x.equals("R"))
                        .count();
                System.out.println(amountR);
                synchronized (sizeToFreq){
                    if(sizeToFreq.containsKey(amountR)){
                        sizeToFreq.put(amountR, sizeToFreq.get(amountR) + 1);
                    }else {
                        sizeToFreq.put(amountR, 1);
                    }
                    sizeToFreq.notify();
                }
            });
            threads.add(thread);
        }
        Thread printThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq){
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Max: " + sizeToFreq.keySet().stream().
                            sorted().
                            collect(Collectors.toList()).get(sizeToFreq.size() - 1));
                }
            }
        });
        printThread.start();
        for (Thread thread : threads) {
            thread.start();
            thread.join();
        }
        printThread.interrupt();

        int max = sizeToFreq.keySet().stream().sorted().collect(Collectors.toList()).get(sizeToFreq.size() - 1);
        System.out.printf("""
                Самое частое количество повторений %d (встретилось %d раз)
                Другие размеры:
                """, max, sizeToFreq.get(max));
        sizeToFreq.keySet().forEach(x -> System.out.printf("- %d (%d раз)\n", x, sizeToFreq.get(x)));
    }



    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}

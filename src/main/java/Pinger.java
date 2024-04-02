import java.io.IOException;
import java.net.InetAddress;

public class Pinger {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Pinger <host>");
            return;
        }

        String host = args[0]; // Целевой хост
        int sampleSize = 10; // Количество пакетов для отправки
        int timeoutMillis = 1000; // Тайм-аут в миллисекундах

        try {
            InetAddress address = InetAddress.getByName(host);
            int receivedCount = 0;
            long totalRTT = 0;
            long minRTT = Long.MAX_VALUE;
            long maxRTT = Long.MIN_VALUE;

            System.out.println("PING " + host + ":");

            for (int i = 0; i < sampleSize; i++) {
                long startTime = System.currentTimeMillis();

                try {
                    if (address.isReachable(timeoutMillis)) {
                        long rtt = System.currentTimeMillis() - startTime;
                        receivedCount++;
                        totalRTT += rtt;
                        minRTT = Math.min(minRTT, rtt);
                        maxRTT = Math.max(maxRTT, rtt);
                        System.out.println("Reply from " + address.getHostAddress() + ": time=" + rtt + "ms");
                    } else {
                        System.out.println("Request timed out");
                    }
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }

                try {
                    Thread.sleep(1000); // Ждем 1 секунду перед отправкой следующего пакета
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            double lossPercentage = (1.0 - (double) receivedCount / sampleSize) * 100;
            double avgRTT = (double) totalRTT / receivedCount;

            System.out.println("\n--- " + host + " ping statistics ---");
            System.out.println(sampleSize + " packets transmitted, " + receivedCount + " received, " +
                    String.format("%.2f%% packet loss", lossPercentage));
            System.out.println("rtt min/avg/max = " + minRTT + "/" + avgRTT + "/" + maxRTT + " ms");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * AnyOfCase
 *
 * @author mark
 * @date 2020/03/31
 */
public class AnyAllOfCase {
    public static void main(String[] args) throws Exception {
        CompletableFuture<String> completableFuture1 = getCompletableFuture(100);
        CompletableFuture<String> completableFuture2 = getCompletableFuture(200);
        CompletableFuture<String> completableFuture3 = getCompletableFuture(300);

        CompletableFuture[] futuresArray =
            new CompletableFuture[] {completableFuture1, completableFuture2, completableFuture3};

        CompletableFuture.anyOf(futuresArray).whenComplete((value, throwable) -> {
            System.out.println(value);
            System.out.println(completableFuture1.getNow(null));
            System.out.println(completableFuture2.getNow(null));
            System.out.println(completableFuture3.getNow(null));
            System.out.println("----------------------------------------");
        });

        CompletableFuture.allOf(futuresArray).whenComplete((value, throwable) -> {
            System.out.println(value);
            System.out.println(completableFuture1.getNow(null));
            System.out.println(completableFuture2.getNow(null));
            System.out.println(completableFuture3.getNow(null));
        });

        TimeUnit.MILLISECONDS.sleep(2000);
    }

    static CompletableFuture<String> getCompletableFuture(int timeoutMills) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(timeoutMills);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "completableFuture" + timeoutMills;
        });
    }
}

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * WhichThread
 *
 * @author mark
 * @date 2020/03/20
 */
public class WhichThread {
    public static void main(String[] args) throws Exception {
        WhichThread instance = new WhichThread();

        // main thread
        CompletableFuture.completedFuture(instance.getString());

        System.out.println("---------------------------------------");

        // on ForkJoinPool-> supplyAsync(cache pool) -> ForkJoinPool
        CompletableFuture.completedFuture(instance.getString()).thenComposeAsync((value) -> {
            System.out.printf("thenComposeAsync I am on %s Thread%n", Thread.currentThread().getName());
            return CompletableFuture.supplyAsync(instance::getString, Executors.newCachedThreadPool());
        }).thenRunAsync(() -> {
            System.out.printf("thenRunAsync I am on %s Thread%n", Thread.currentThread().getName());
        }).thenRun(() -> {
            System.out.printf("thenRun I am on %s Thread%n", Thread.currentThread().getName());
        });

        TimeUnit.MILLISECONDS.sleep(100);
    }

    private String getString() {
        System.out.printf("getString I am on %s Thread%n", Thread.currentThread().getName());
        return "getString";
    }
}


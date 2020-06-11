import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * CancelCase
 *
 * @author mark
 * @date 2020/03/28
 */
public class CancelCase {
    public static void main(String[] args) throws Exception {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("runAsync" + Thread.currentThread().getName());
        }).exceptionally(throwable -> {
            System.out.println("1 " + throwable);
            return null;
        });

        System.out.println(completableFuture.isCancelled());
        System.out.println(completableFuture.cancel(false));
        System.out.println(completableFuture.isCompletedExceptionally());
        System.out.println(completableFuture.isDone());
        System.out.println(completableFuture.isCancelled());

        completableFuture.exceptionally(throwable -> {
            System.out.println("2 " + throwable);
            return null;
        });

        TimeUnit.MILLISECONDS.sleep(1000);
    }
}

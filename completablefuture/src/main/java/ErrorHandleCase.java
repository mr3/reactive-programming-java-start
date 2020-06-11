import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * ErrorHandleCase
 *
 * @author mark
 * @date 2020/03/31
 */
public class ErrorHandleCase {
    public static void main(String[] args) throws Exception {
        CompletableFuture.completedFuture("error handle case").thenCompose(value -> {
            // return value.toUpperCase();
            return CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException("to upper case exception");
            });
        }).handle((item, throwable) -> {
            System.out.println("handle " + throwable.getMessage());
            throw new RuntimeException("handle exception");
        }).exceptionally(throwable -> {
            System.out.println("exceptionally " + throwable.getMessage());
            return "exceptionally";
        }).thenAccept(value -> {
            System.out.println("thenAccept " + value);
        });

        TimeUnit.MILLISECONDS.sleep(1000);
    }
}

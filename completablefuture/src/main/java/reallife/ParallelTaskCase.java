package reallife;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务并行执行，最终聚合结果返回一个CompletableFuture
 *
 * @author mark
 * @date 2020/03/31
 */
public class ParallelTaskCase {
    public static void main(String[] args) throws Exception {
        ParallelTaskCase instance = new ParallelTaskCase();

        List<Integer> requestList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        instance.getResultListFuture(requestList).thenAccept(resultList -> {
            System.out.printf("result list size:%d", resultList.size());
        });

        TimeUnit.MILLISECONDS.sleep(1000);
    }

    private CompletableFuture<List<Integer>> getResultListFuture(List<Integer> requestList) {
        CompletableFuture<List<Integer>> settableFuture = new CompletableFuture<>();
        final Object syncObject = new Object();
        List<Integer> responseList = new ArrayList<>(requestList.size());
        AtomicInteger executeCount = new AtomicInteger(requestList.size());

        for (Integer item : requestList) {
            CompletableFuture.completedFuture(item).thenComposeAsync(value -> {
                return getTaskFuture(value);
            }).exceptionally(throwable -> {
                return Integer.valueOf(throwable.getCause().getMessage());
            }).thenAccept(result -> {
                System.out.println("task done " + result + " " + Thread.currentThread().getName());
                synchronized (syncObject) {
                    responseList.add(result);
                }
                if (executeCount.decrementAndGet() < 1) {
                    settableFuture.complete(responseList);
                }
            });
        }

        return settableFuture;
    }


    private CompletableFuture<Integer> getTaskFuture(Integer value) {
        System.out.println("task begin " + value + " " + Thread.currentThread().getName());

        if (value <= 3) {
            try {
                TimeUnit.MILLISECONDS.sleep(value * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (value >= 7) {
            throw new RuntimeException(String.valueOf(value * 10));
        }

        return CompletableFuture.completedFuture(value);
    }
}

package reactive.programming.java.start.rxjava2.transforming;

import com.google.common.util.concurrent.SettableFuture;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * FlatMapCase
 * http://reactivex.io/documentation/operators/flatmap.html
 *
 * @author mark
 * @date 2020/03/26
 */
public class FlatMapCase {
    public static void main(String[] args) throws Exception {
        new FlatMapCase().flatMapTest().subscribe(item -> {
            System.out.println("----------------------------------------------");
            System.out.printf("Already done size %d, on thread: %s", item.size(), Thread.currentThread().getName());
        });

        TimeUnit.MILLISECONDS.sleep(1000);
    }

    Single<Integer> rpcCall(Integer item) {
        return Single.just(item).map(item2 -> {
            System.out.printf("%d execute at: %s on thread: %s%n", item,
                LocalTime.now().format(DateTimeFormatter.ofPattern("ss.SSS")), Thread.currentThread().getName());

            if (item2 <= 5) {
                TimeUnit.MILLISECONDS.sleep(item2 * 100);
            }

            if (item2 >= 15) {
                throw new RuntimeException(String.valueOf(item2 * 10));
            }
            return item2;
        }).onErrorReturn(throwable -> {
            return Integer.valueOf(throwable.getMessage());
        }).subscribeOn(Schedulers.computation());
    }

    Observable<List<Integer>> flatMapTest() {
        SettableFuture<List<Integer>> settableFuture = SettableFuture.create();
        final Object syncObject = new Object();
        List<Integer> requestList = IntStream.range(0, 20).boxed().collect(Collectors.toList());
        List<Integer> responseList = new ArrayList<>(requestList.size());
        AtomicInteger executeCount = new AtomicInteger(requestList.size());

        Observable.fromIterable(requestList).flatMap(item -> {
            return rpcCall(item).toObservable();
        }).subscribe(item -> {
            System.out.printf("%d done at: %s on thread: %s%n", item,
                LocalTime.now().format(DateTimeFormatter.ofPattern("ss.SSS")), Thread.currentThread().getName());
            synchronized (syncObject) {
                responseList.add(item);
            }
            if (executeCount.decrementAndGet() < 1) {
                settableFuture.set(responseList);
            }
        });

        return Observable.fromFuture(settableFuture);
    }
}

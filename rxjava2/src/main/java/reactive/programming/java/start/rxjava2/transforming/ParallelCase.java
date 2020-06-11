package reactive.programming.java.start.rxjava2.transforming;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ParallelCase
 * http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/parallel/ParallelFlowable.html
 *
 * @author mark
 * @date 2020/06/08
 */
public class ParallelCase {
    public static void main(String[] args) throws Exception {
        new ParallelCase().parallelTest().subscribe(item -> {
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
        });
    }

    Flowable<List<Integer>> parallelTest() {
        ReplaySubject<List<Integer>> publishSubject = ReplaySubject.create();

        List<Integer> requestList = IntStream.range(0, 20).boxed().collect(Collectors.toList());
        List<Integer> responseList = new ArrayList<>(requestList.size());
        AtomicInteger executeCount = new AtomicInteger(requestList.size());

        Flowable.fromIterable(requestList).parallel().runOn(Schedulers.computation()).flatMap(item -> {
            return rpcCall(item).toFlowable();
        }).sequential().subscribe(item -> {
            System.out.printf("%d done at: %s on thread: %s%n", item,
                LocalTime.now().format(DateTimeFormatter.ofPattern("ss.SSS")), Thread.currentThread().getName());
            responseList.add(item);
            if (executeCount.decrementAndGet() < 1) {
                publishSubject.onNext(responseList);
            }
        });

        System.out.println("publishSubject.subscribe");
        return publishSubject.toFlowable(BackpressureStrategy.MISSING);
    }
}

package reactive.programming.java.start.rxjava2.transforming;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.SingleSubject;

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
        new ParallelCase().parallelTest().subscribe(size -> {
            System.out.println("----------------------------------------------");
            System.out.printf("Already done size %d, on thread: %s%n", size, Thread.currentThread().getName());
        });

        TimeUnit.MILLISECONDS.sleep(1000);
    }

    Single<Integer> rpcCall(Integer value) {
        return Single.just(value).map(item -> {
            System.out.printf("%d execute at: %s on thread: %s%n", value,
                LocalTime.now().format(DateTimeFormatter.ofPattern("ss.SSS")), Thread.currentThread().getName());

            if (item <= 5) {
                TimeUnit.MILLISECONDS.sleep(item * 100);
            }

            if (item >= 15) {
                throw new RuntimeException(String.valueOf(item * 10));
            }
            return item;
        }).onErrorReturn(throwable -> {
            return Integer.valueOf(throwable.getMessage());
        });
    }

    Flowable<Integer> parallelTest() {
        SingleSubject<Integer> singleSubject = SingleSubject.create();

        List<Integer> requestList = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        List<Integer> responseList = new ArrayList<>(requestList.size());
        AtomicInteger executeCount = new AtomicInteger(requestList.size());

        Flowable.fromIterable(requestList).parallel().runOn(Schedulers.io()).flatMap(item -> {
            return rpcCall(item).toFlowable();
        }).sequential().subscribe(item -> {
            System.out.printf("%d done at: %s on thread: %s%n", item,
                LocalTime.now().format(DateTimeFormatter.ofPattern("ss.SSS")), Thread.currentThread().getName());
            responseList.add(item);
            if (executeCount.decrementAndGet() < 1) {
                System.out.printf("all items: %s, on thread: %s%n", responseList, Thread.currentThread().getName());
                singleSubject.onSuccess(responseList.size());
            }
        });

        return singleSubject.toFlowable();
    }
}

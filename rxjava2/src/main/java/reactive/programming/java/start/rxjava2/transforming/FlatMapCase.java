package reactive.programming.java.start.rxjava2.transforming;

import com.google.common.util.concurrent.SettableFuture;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.SingleSubject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
        new FlatMapCase().flatMapTest().subscribe(size -> {
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

    Observable<Integer> flatMapTest() {
        SingleSubject<Integer> singleSubject = SingleSubject.create();

        List<Integer> requestList = IntStream.range(0, 10).boxed().collect(Collectors.toList());

        Observable.fromIterable(requestList).flatMap(item -> {
            return rpcCall(item).subscribeOn(Schedulers.io()).toObservable();
        }).collect(() -> new ArrayList<Integer>(requestList.size()), (lists, item) -> {
            lists.add(item);
            System.out.printf("%d done at: %s on thread: %s%n", item,
                LocalTime.now().format(DateTimeFormatter.ofPattern("ss.SSS")), Thread.currentThread().getName());
        }).subscribe(responseList -> {
            singleSubject.onSuccess(responseList.size());
            System.out.printf("all items: %s, on thread: %s%n", responseList, Thread.currentThread().getName());
        });

        return singleSubject.toObservable();
    }
}

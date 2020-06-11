package reactive.programming.java.start.rxjava2.aggregate;

import io.reactivex.Flowable;

import java.util.concurrent.TimeUnit;

/**
 * ConcatWithCase
 * http://reactivex.io/documentation/operators/concat.html
 *
 * @author mark
 * @date 2020/03/19
 */
public class ConcatWithCase {
    public static void main(String[] args) throws Exception {
        Flowable.interval(100, TimeUnit.MILLISECONDS).map(item -> "x1")
            .concatWith(Flowable.interval(50, TimeUnit.MILLISECONDS).map(item -> "x2")).take(10)
            .subscribe(System.out::println);

        TimeUnit.MILLISECONDS.sleep(1000);
    }
}

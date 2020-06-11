package reactive.programming.java.start.rxjava2.combining;

import io.reactivex.Flowable;

import java.util.concurrent.TimeUnit;

/**
 * MergeWithCase
 * http://reactivex.io/documentation/operators/merge.html
 *
 * @author mark
 * @date 2020/03/24
 */
public class MergeWithCase {
    public static void main(String[] args) throws Exception {
        Flowable.interval(100, TimeUnit.MILLISECONDS).map(item -> "x1")
            .mergeWith(Flowable.interval(50, TimeUnit.MILLISECONDS).map(item -> "x2")).take(10)
            .subscribe(System.out::println);

        TimeUnit.MILLISECONDS.sleep(1000);
    }
}

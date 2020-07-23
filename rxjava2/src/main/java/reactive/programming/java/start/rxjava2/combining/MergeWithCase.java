package reactive.programming.java.start.rxjava2.combining;

import io.reactivex.Flowable;

import java.util.concurrent.TimeUnit;

/**
 * 合并操作符，合并多个Observable, 交叉发送
 * http://reactivex.io/documentation/operators/merge.html
 *
 * @author mark
 * @date 2020/03/24
 */
public class MergeWithCase {
    public static void main(String[] args) throws Exception {
        Flowable<String> f1 = Flowable.interval(100, TimeUnit.MILLISECONDS).map(item -> "x1");
        Flowable<String> f2 = Flowable.interval(50, TimeUnit.MILLISECONDS).map(item -> "x2");

        f1.mergeWith(f2).take(10).subscribe(System.out::println);

        TimeUnit.MILLISECONDS.sleep(1000);
    }
}

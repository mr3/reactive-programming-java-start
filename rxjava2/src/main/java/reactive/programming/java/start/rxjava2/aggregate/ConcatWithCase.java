package reactive.programming.java.start.rxjava2.aggregate;

import io.reactivex.Observable;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * 连接操作符，连接多个Observable, 前面的Observable先发完， 后面的Observable才会发
 * http://reactivex.io/documentation/operators/concat.html
 * http://reactivex.io/RxJava/javadoc/io/reactivex/Flowable.html#concatWith-io.reactivex.CompletableSource-
 *
 * @author mark
 * @date 2020/03/19
 */
public class ConcatWithCase {
    public static void main(String[] args) throws Exception {
        ConcatWithCase instance = new ConcatWithCase();

        instance.concatWith();
        instance.interValConcatWith();
        TimeUnit.MILLISECONDS.sleep(1500);

        instance.concatMap();
    }

    /**
     * x1 先发送， x2再开始发送
     */
    public void concatWith() {
        System.out.println("------------------concatWith----------------------");
        Observable.fromCallable(() -> {
            TimeUnit.MILLISECONDS.sleep(200);
            return "x1";
        }).concatWith(Observable.fromCallable(() -> {
            TimeUnit.MILLISECONDS.sleep(100);
            return "x2";
        })).subscribe(item -> {
            System.out.printf("%s at time: %s%n", item, Instant.now().toEpochMilli());
        });
    }

    /**
     * 一直发送x1，至到 take 10
     */
    public void interValConcatWith() {
        System.out.println("------------------interValConcatWith----------------------");
        Observable<String> o1 = Observable.interval(100, TimeUnit.MILLISECONDS).map(item -> "x1");
        Observable<String> o2 = Observable.interval(50, TimeUnit.MILLISECONDS).map(item -> "x2");

        o1.concatWith(o2).take(10).subscribe(item -> {
            System.out.println(item);
        });
    }

    /**
     * x1 联合并转换为 x2
     */
    public void concatMap() {
        System.out.println("------------------concatMap----------------------");
        Observable.fromCallable(() -> {
            TimeUnit.MILLISECONDS.sleep(200);
            return "x1";
        }).concatMap(item -> {
            return Observable.fromCallable(() -> {
                TimeUnit.MILLISECONDS.sleep(100);
                return item + " map to x2";
            });
        }).subscribe(item -> {
            System.out.printf("%s at time: %s%n", item, Instant.now().toEpochMilli());
        });
    }
}

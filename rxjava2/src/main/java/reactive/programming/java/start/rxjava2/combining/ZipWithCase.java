package reactive.programming.java.start.rxjava2.combining;

import io.reactivex.Observable;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.concurrent.TimeUnit;

/**
 * 压缩操作符 每个Observable选择一个进行压缩排序发送
 * http://reactivex.io/documentation/operators/zip.html
 *
 * @author zhang_hy
 * @date 2020/07/17
 */
public class ZipWithCase {
    public static void main(String[] args) throws Exception {
        Observable<Long> o1 = Observable.interval(50, TimeUnit.MILLISECONDS);
        Observable<Long> o2 = Observable.interval(20, TimeUnit.MILLISECONDS);

        o1.zipWith(o2, (item1, item2) -> {
            return item1 + " map " + item2 + " at: " + LocalTime.now().getLong(ChronoField.MILLI_OF_SECOND);
        }).subscribe(System.out::println);

        TimeUnit.MILLISECONDS.sleep(1000);
    }
}

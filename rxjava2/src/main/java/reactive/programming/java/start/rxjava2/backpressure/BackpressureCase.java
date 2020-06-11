package reactive.programming.java.start.rxjava2.backpressure;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * BackpressureCase
 *
 * @author mark
 * @date 2020/06/08
 */
public class BackpressureCase {
    public static void main(String[] args) throws Exception {
        // new BackpressureCase().bufferCase();
        // new BackpressureCase().windowCase();
        // new BackpressureCase().sampleCase();
        // new BackpressureCase().backpressureOverFlow();
        // new BackpressureCase().backpressureDrop();

        TimeUnit.SECONDS.sleep(5);
    }

    /**
     * 丢弃发送
     */
    private void backpressureDrop() {
        Flowable.range(1, 1_000).onBackpressureDrop(item -> {
            System.out.println("onDrop: " + item);
        }).observeOn(Schedulers.computation()).subscribe(this::compute, Throwable::printStackTrace);
    }

    /**
     * 根据背压策略发送
     */
    private void backpressureOverFlow() {
        Flowable.range(1, 1_000).onBackpressureBuffer(16, () -> {
            System.out.println("overflow action");
        }, BackpressureOverflowStrategy.DROP_OLDEST).observeOn(Schedulers.computation())
            .subscribe(this::compute, Throwable::printStackTrace);
    }

    /**
     * 采样发送
     */
    private void sampleCase() {
        // throttleFirst
        Flowable.interval(10, TimeUnit.MILLISECONDS).sample(100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation()).subscribe(this::compute, Throwable::printStackTrace);
    }

    private void compute(Long value) {
        try {
            System.out.println("compute long v: " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compute(Integer integer) {
        try {
            System.out.println("compute integer v: " + integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分组批量发送
     */
    private void windowCase() {
        Flowable.range(1, 1_000).window(10).subscribeOn(Schedulers.computation())
            .subscribe(this::compute, Throwable::printStackTrace);
    }

    private void compute(Flowable<Integer> integerFlowable) throws InterruptedException {
        integerFlowable.forEach(item -> {
            System.out.println("compute integer v: " + item + " " + Thread.currentThread().getName());
        });
        System.out.println("------------------------------");
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * 将不能发送的元素添加到缓冲区发送
     */
    private void bufferCase() {
        Flowable.range(1, 1_000).buffer(10).subscribeOn(Schedulers.computation())
            .subscribe(this::compute, Throwable::printStackTrace);
    }

    private void compute(List<Integer> integers) {
        try {
            System.out.println("compute integer v: " + integers);
            System.out.println("------------------------------");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

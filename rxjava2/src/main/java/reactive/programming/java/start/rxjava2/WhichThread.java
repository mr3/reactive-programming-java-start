package reactive.programming.java.start.rxjava2;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * WhichThread
 *
 * @author mark
 * @date 2020/03/20
 */
public class WhichThread {
    public static void main(String[] args) throws Exception {
        WhichThread instance = new WhichThread();

        // fromFuture(main) - subscribe(cache)
        Single.fromFuture(instance.fromFuture()).subscribeOn(Schedulers.io()).subscribe(instance::subscribe);

        TimeUnit.MILLISECONDS.sleep(100);

        // just(main) - compose(main/cache) - subscribe(cache)
        Single.just(instance.getString()).compose(instance::getSingle).subscribeOn(Schedulers.io())
            .subscribe(instance::subscribe);

        TimeUnit.MILLISECONDS.sleep(100);

        // just(main) - flatMap(cache/new[if switch thread]) - map(new) - subscribe(new)
        Single.just(instance.getString()).flatMap(instance::flatMap).subscribeOn(Schedulers.io()).map(item -> {
            System.out.printf("map + %s run on thread: %s%n", item, Thread.currentThread().getName());
            return item;
        }).observeOn(Schedulers.io()).subscribe(instance::subscribe);

        TimeUnit.MILLISECONDS.sleep(100);

        // fromCallable(cache) - subscribe(cache)
        Single.fromCallable(instance::fromCallable).subscribeOn(Schedulers.io()).subscribe(instance::subscribe);

        TimeUnit.MILLISECONDS.sleep(100);
    }

    Consumer<Void> subscribe(String value) {
        System.out.printf("subscribe run on %s%n", Thread.currentThread().getName());
        System.out.println("----------------------------------------");

        return s -> {
        };
    }

    private String getString() {
        System.out.printf("getString run on thread: %s%n", Thread.currentThread().getName());
        return "getString";
    }

    private Single<String> getSingle(Single<String> string) {
        System.out.printf("getSingle1 run on thread: %s%n", Thread.currentThread().getName());

        return string.map(item -> {
            System.out.printf("getSingle2 run on thread: %s%n", Thread.currentThread().getName());
            return "getSingle3";
        });
    }

    private ListenableFuture<String> fromFuture() {
        System.out.printf("fromFuture1 run on thread: %s%n", Thread.currentThread().getName());

        return Futures.submit(() -> {
            System.out.printf("fromFuture2 run on thread: %s%n", Thread.currentThread().getName());
            return "fromFuture3";
        }, Executors.newCachedThreadPool());
    }

    private String fromCallable() {
        System.out.printf("fromCallable1 run on thread: %s%n", Thread.currentThread().getName());
        return "fromCallable2";
    }

    private Single<String> flatMap(String value) {
        System.out.printf("flatMap1 run on thread: %s%n", Thread.currentThread().getName());

        return Single.just(value).subscribeOn(Schedulers.newThread()).map(item -> {
            System.out.printf("flatMap2 run on thread: %s%n", Thread.currentThread().getName());
            return "flatMap3";
        });
    }

}

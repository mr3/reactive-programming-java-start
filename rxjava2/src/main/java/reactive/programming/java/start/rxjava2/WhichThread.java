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

        // getFuture execute on main thread
        Single.fromFuture(instance.fromFuture()).subscribeOn(Schedulers.io()).subscribe(instance::subscribe);

        TimeUnit.MILLISECONDS.sleep(100);

        // getSingle execute on cache Thread
        Single.just(instance.getString()).compose(instance::getSingle).subscribeOn(Schedulers.io()).subscribe(instance::subscribe);

        TimeUnit.MILLISECONDS.sleep(100);

        // fromCallable execute on cache Thread
        Single.fromCallable(instance::fromCallable).subscribeOn(Schedulers.io()).subscribe(instance::subscribe);

        TimeUnit.MILLISECONDS.sleep(100);
    }

    Consumer<Void> subscribe(String value) {
        System.out.println(value + " subscribe " + Thread.currentThread().getName());
        System.out.println("----------------------------------------");

        return s -> {
        };
    }

    private String getString() {
        System.out.printf("getString I am on %s Thread%n", Thread.currentThread().getName());
        return "getString";
    }

    private Single<String> getSingle(Single<String> string) {
        System.out.printf("getSingle I am on %s Thread%n", Thread.currentThread().getName());

        return string.map(item -> {
            System.out.printf("getSingle I am on %s Thread%n", Thread.currentThread().getName());
            return "getSingle";
        });
    }

    private ListenableFuture<String> fromFuture() {
        System.out.printf("fromFuture I am on %s Thread%n", Thread.currentThread().getName());

        return Futures.submit(() -> {
            System.out.printf("fromFuture I am on %s Thread%n", Thread.currentThread().getName());
            return "fromFuture";
        }, Executors.newCachedThreadPool());
    }

    private String fromCallable() {
        System.out.printf("fromCallable I am on %s Thread%n", Thread.currentThread().getName());
        return "fromCallable";
    }

}

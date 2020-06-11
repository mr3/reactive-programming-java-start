package reactive.programming.java.start.rxjava2.errorhandle;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.exceptions.Exceptions;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * RetryCase
 * http://reactivex.io/documentation/operators/retry.html
 *
 * @author mark
 * @date 2020/01/03
 */
public class RetryCase {
    public static void main(String[] args) throws Exception {
        new RetryCase().retryBiPredicate();
    }

    int retryTimes = 3;

    private void retryWhen() {
        Single.fromCallable(this::invokeRpcAsync).doOnError(throwable -> {
            System.out.println(throwable.toString());
        }).retryWhen(handler -> handler.zipWith(Flowable.rangeLong(1, retryTimes), ((throwable, index) -> {
            // get index, do monitor
            if (index < retryTimes) {
                boolean retry = throwable instanceof SocketTimeoutException || throwable instanceof ConnectException;
                if (retry) {
                    System.out.printf("retry times: %d%n", index);
                    return index;
                }
            }

            throw Exceptions.propagate(throwable);
        }))).subscribe(System.out::println);
    }

    private void retryBiPredicate() {
        Single.fromCallable(this::invokeRpcAsync).doOnError(throwable -> {
            System.out.println("doOnError" + throwable.toString());
        }).retry((index, throwable) -> {
            boolean retry = false;
            // get index, do monitor
            if (index < retryTimes) {
                retry = throwable instanceof SocketTimeoutException || throwable instanceof ConnectException;
            }
            if (retry) {
                System.out.printf("retry times: %d%n", index);
            }
            return retry;
        }).doOnEvent((response, throwable) -> {
            System.out.println("doOnEvent");
        }).doAfterTerminate(() -> {
            System.out.println("doAfterTerminate");
        }).subscribe(System.out::println);
    }

    private void retryTimes() {
        Single.fromCallable(this::invokeRpcAsync).doOnError(throwable -> {
            System.out.println(throwable.toString());
        }).retry(retryTimes, throwable -> {
            return throwable instanceof SocketTimeoutException || throwable instanceof ConnectException;
        }).subscribe(System.out::println);
    }

    private int index = 0;

    /**
     * First time normally executed, then retry
     */
    private String invokeRpcAsync() throws SocketTimeoutException {
        System.out.printf("invoke times:%d%n ", index);
        if (index++ < 2) {
            throw new SocketTimeoutException();
        }

        return "success";
    }
}

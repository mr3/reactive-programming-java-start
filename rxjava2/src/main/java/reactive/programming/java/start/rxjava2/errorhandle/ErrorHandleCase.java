package reactive.programming.java.start.rxjava2.errorhandle;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.io.IOException;

/**
 * ErrorHandleCase
 * https://github.com/ReactiveX/RxJava/wiki/Error-Handling-Operators
 *
 * @author zhang_hy
 * @date 2020/07/23
 */
public class ErrorHandleCase {
    /**
    1. 吞下错误并切换到备用的 Observable 继续序列
        - onErrorResumeNext(Throwable)
        - onExceptionResumeNext(Exception)
    2. 吞下错误并发出默认项
        - onErrorReturn
        - onErrorReturnItem
    3. 吞下错误并立即重试失败的 Observable
    4. 吞下错误并在等待一段时间后重试失败的 Observable
        - retry
        - retryUntil
        - retryWhen
     **/
    public static void main(String[] args) {
        ErrorHandleCase instance = new ErrorHandleCase();

        instance.doOnError();

        instance.onErrorComplete();

        instance.onErrorResumeNext();

        instance.onErrorReturn();

        instance.onErrorReturnItem();

        instance.onExceptionResumeNext();
    }

    /**
     * 如果遇到{@link java.lang.Throwable}则调用指定的消费者, 错误继续向下游传递
     */
    void doOnError() {
        System.out.println("---------------doOnError---------------");
        Observable.error(new IOException("Something went wrong"))
            .doOnError(error -> System.err.println("The error message is: " + error.getMessage()))
            .subscribe(x -> System.out.println("onNext should never be printed!"), Throwable::printStackTrace,
                () -> System.out.println("onComplete should never be printed!"));
    }

    /**
     * 如果Completable发送{@link java.lang.Throwable}且满足条件, 则吞下错误发送一个onComplete事件
     */
    void onErrorComplete() {
        System.out.println("---------------onErrorComplete---------------");
        Completable.fromAction(() -> {
            throw new IOException();
        }).onErrorComplete(error -> {
            // Only ignore errors of type java.io.IOException.
            return error instanceof IOException;
        }).subscribe(() -> System.out.println("IOException was ignored"),
            error -> System.err.println("onError should not be printed!"));
    }

    /**
     * 如果遇到{@link java.lang.Throwable}则吞下错误并发送指定的Observable
     * 实现类: {@link io.reactivex.internal.operators.observable.ObservableOnErrorNext}
     */
    void onErrorResumeNext() {
        System.out.println("---------------onErrorResumeNext---------------");
        Observable<Integer> numbers = Observable.generate(() -> 1, (state, emitter) -> {
            emitter.onNext(state);

            return state + 1;
        });

        numbers.scan(Math::multiplyExact).onErrorResumeNext(Observable.just(-3))
            .subscribe(System.out::println, error -> System.err.println("onError should not be printed!"));
    }

    /**
     * 如果遇到{@link java.lang.Throwable}则吞下错误并根据Function发送指定项
     */
    void onErrorReturn() {
        System.out.println("---------------onErrorReturn---------------");
        Single.just("2A").map(v -> Integer.parseInt(v, 10)).onErrorReturn(error -> {
            if (error instanceof NumberFormatException) {
                return -3;
            } else {
                throw new IllegalArgumentException();
            }
        }).subscribe(System.out::println, error -> System.err.println("onError should not be printed!"));
    }

    /**
     * 如果遇到{@link java.lang.Throwable}则吞下错误并发送指定项
     */
    void onErrorReturnItem() {
        System.out.println("---------------onErrorReturnItem---------------");
        Single.just("2A").map(v -> Integer.parseInt(v, 10)).onErrorReturnItem(-3)
            .subscribe(System.out::println, error -> System.err.println("onError should not be printed!"));
    }

    /**
     * 如果遇到{@link java.lang.Exception}则吞下错误并发送指定的Observable
     */
    void onExceptionResumeNext() {
        System.out.println("---------------onExceptionResumeNext---------------");
        Observable<String> exception = Observable.<String>error(IOException::new)
            .onExceptionResumeNext(Observable.just("This value will be used to recover from the IOException"));

        Observable<String> error =
            Observable.<String>error(Error::new).onExceptionResumeNext(Observable.just("This value will not be used"));

        Observable.concat(exception, error).subscribe(message -> System.out.println("onNext: " + message),
            err -> System.err.println("onError: " + err));
    }
}

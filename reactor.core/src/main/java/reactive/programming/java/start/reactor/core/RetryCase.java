package reactive.programming.java.start.reactor.core;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * RetryCase
 *
 * @author mark
 * @date 2020/01/03
 */
public class RetryCase {
    public static void main(String[] args) throws Exception {
        new RetryCase().retryWhen();
    }

    int retryTimes = 3;

    /**
     * https://projectreactor.io/docs/core/release/reference/index.html#faq.retryWhen
     */
    private void retryWhen() {
        Mono.fromCallable(this::invokeRpcAsync).doOnError(throwable -> {
            System.out.println(throwable.toString());
        }).retryWhen(handler -> handler.zipWith(Flux.range(1, retryTimes), (throwable, index) -> {
            // get index, do monitor
            if (index < retryTimes) {
                boolean retry = throwable instanceof SocketTimeoutException || throwable instanceof ConnectException;
                if (retry) {
                    System.out.printf("retry times: %d%n", index);
                    return index;
                }
            }

            throw Exceptions.propagate(throwable);
        })).subscribe(System.out::println);
    }

    private int index = 0;

    /**
     * First time normally executed, then retry
     */
    private String invokeRpcAsync() throws SocketTimeoutException {
        System.out.printf("invoke times:%d ", index);
        if (index++ < 2) {
            throw new SocketTimeoutException();
        }

        return "success";
    }
}

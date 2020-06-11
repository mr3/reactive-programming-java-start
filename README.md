# Reactive-programming-java-start

Reactive programming framework use cases

## Docs

### [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html)
- [Java-8-completablefuture](https://www.baeldung.com/java-completablefuture)
- [Java-9-completablefuture](https://www.baeldung.com/java-9-completablefuture)

> Java 9 Enhancements to the CompletableFuture API
> - Executor defaultExecutor()
> - CompletableFuture<U> newIncompleteFuture()
> - CompletableFuture<T> copy()
> - CompletionStage<T> minimalCompletionStage()
> - CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor)
> - CompletableFuture<T> completeAsync(Supplier<? extends T> supplier)
> - CompletableFuture<T> orTimeout(long timeout, TimeUnit unit)
> - CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit)

### [rxjava](http://reactivex.io/documentation/operators.html)
### [project reactor](https://projectreactor.io/docs/core/release/reference/index.html)


## Framework version currently
```xml
<dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxjava</artifactId>
    <version>2.2.16</version>
</dependency>

<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.3.1.RELEASE</version>
</dependency>
```

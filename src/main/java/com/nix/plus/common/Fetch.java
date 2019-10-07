package com.nix.plus.common;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author keray
 * @date 2019/05/16 17:14
 */
public class Fetch<T> {
    private T data;
    private Exception e;
    private Catch<T> tCatch;

    public Fetch(T data) {
        this.data = data;
    }

    public Fetch(T data, Exception e) {
        this.data = data;
        this.e = e;
    }

    public static <T> Fetch<T> fetch(T data) {
        return new Fetch<>(data);
    }

    public static <T> Fetch<T> fetch(T data, Exception e) {
        return new Fetch<>(data, e);
    }

    public Fetch<T> then(Consumer<T> then) {
        if (e == null) {
            try {
                then.accept(data);
            } catch (Exception e) {
                this.e = e;
                if (tCatch != null) {
                    tCatch.accept(e, data);
                    this.e = null;
                }
            }
        } else {
            if (tCatch != null) {
                tCatch.accept(e, data);
                this.e = null;
            }
        }
        return this;
    }

    public <S> Fetch<S> then(Function<T, S> then) {
        if (e == null) {
            try {
                return fetch(then.apply(data));
            } catch (Exception e) {
                this.e = e;
                if (tCatch != null) {
                    tCatch.accept(e, data);
                    this.e = null;
                }
            }
        } else {
            if (tCatch != null) {
                tCatch.accept(e, data);
                this.e = null;
            }
        }
        return (Fetch<S>) this;
    }

    public Fetch<T> catchFetch(Catch<T> tCatch) {
        this.tCatch = tCatch;
        if (e != null) {
            tCatch.accept(e, data);
            this.e = null;
        }
        return this;
    }


    public void finallyFetch(Consumer<T> then) {
        try {
            then.accept(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Catch<T> {
        void accept(Exception e, T t);
    }

    public T getData() {
        if (e != null) {
            if (tCatch != null) {
                tCatch.accept(e, data);
                this.e = null;
            } else {
                throw new RuntimeException(e);
            }
        }
        return data;
    }
}


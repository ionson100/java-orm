package org.orm;


/**
 * The interface Function callback.
 *
 * @param <T> the type parameter
 */
public  interface ITask<T> {
    /**
     * Action.
     *
     * @param o the {@link Object}
     */
    void invoke(T o);

}

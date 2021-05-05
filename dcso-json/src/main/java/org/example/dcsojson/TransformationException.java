package org.example.dcsojson;

/**
 * Signals that the transformation process of a maDMP file was not completed successfully.
 */
public class TransformationException extends Exception {

    /**
     * Constructs a {@link TransformationException} with no detail message.
     */
    public TransformationException() {
    }

    /***
     * Constructs a {@link TransformationException} with the specified detail message.
     * @param message The detail message about the error which is saved for later retrieval by the {@link Throwable#getMessage()} method.
     */
    public TransformationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of <code>cause==null ? null : cause.toString()</code>
     * which typically contains the class and detail message of <code>cause</code>. This constructor is useful for exceptions that
     * are little more than wrappers for other {@link Throwable}s.
     *
     * @param cause The cause which is saved for later retrieval by the {@link Throwable#getCause()} method.
     *              A null value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public TransformationException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>
     * Constructs a new {@link TransformationException} with the specified detail message and cause.
     * </p>
     * <p>
     * Note that the detail message associated with cause is not automatically incorporated in this exception's detail message.
     * </p>
     *
     * @param message The detail message which is saved for later retrieval by the {@link Throwable#getMessage()} method.
     * @param cause   The cause which is saved for later retrieval by the {@link Throwable#getCause()} method.
     *                A null value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public TransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}

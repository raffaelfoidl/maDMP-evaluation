package org.example.dcsojson;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Represents an operation that accepts two {@link Path} input arguments and returns no result. This is a specialization
 * of the {@link BiConsumer} functional interface, tailored to representing methods with two parameters that are
 * declared in the {@link IDcsoJsonTransformer} interface.
 */
@FunctionalInterface
public interface DcsoJsonTransformation<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param arg1 The first input argument.
     * @param arg2 The second input argument.
     * @throws TransformationException Is thrown if the transformation represented by this
     *                                 {@link DcsoJsonTransformation} operation cannot be completed successfully.
     */
    void accept(T arg1, U arg2) throws TransformationException;

    /**
     * Returns a composed {@link DcsoJsonTransformer} that performs, in sequence, this operation followed by the
     * <code>after</code> operation. If performing either operation throws an exception, it is relayed to the caller of
     * the composed operation. Subsequently, if performing this operation throws an exception, thee <code>after</code>
     * operation will not be performed.
     *
     * @param after The operation to perform after this operation.
     * @return Returns a composed {@link DcsoJsonTransformer} that performs in sequence the operation represented by
     * this {@link DcsoJsonTransformer} followed by the <code>after</code> operation.
     */
    default DcsoJsonTransformation<T, U> andThen(DcsoJsonTransformation<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (l, r) -> {
            DcsoJsonTransformation.this.accept(l, r);
            after.accept(l, r);
        };
    }
}

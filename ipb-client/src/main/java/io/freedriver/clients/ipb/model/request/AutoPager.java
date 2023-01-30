package io.freedriver.clients.ipb.model.request;

import io.freedriver.clients.ipb.model.response.PaginatedResponse;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// Abstract
public class AutoPager<I extends PaginatedRequest<I>, O> implements Iterable<O> {

    private final Function<Integer, PaginatedRequest<I>> requestBuilder;
    private final Function<PaginatedRequest<I>, PaginatedResponse<O>> callFunction;

    public AutoPager(Function<Integer, PaginatedRequest<I>> requestBuilder, Function<PaginatedRequest<I>, PaginatedResponse<O>> callFunction) {
        this.requestBuilder = requestBuilder;
        this.callFunction = callFunction;
    }

    @Override
    public Iterator<O> iterator() {
        return new AutoPageIterator<>(requestBuilder, callFunction);
    }

    private static final class AutoPageIterator<I extends PaginatedRequest<I>, O> implements Iterator<O> {
        private final AtomicInteger pageNumber = new AtomicInteger(0);
        private final AtomicInteger idx = new AtomicInteger(0);
        private final Function<Integer, PaginatedRequest<I>> requestBuilder;
        private final Function<PaginatedRequest<I>, PaginatedResponse<O>> callFunction;

        private PaginatedResponse<O> currentPage;

        private AutoPageIterator(Function<Integer, PaginatedRequest<I>> requestBuilder, Function<PaginatedRequest<I>, PaginatedResponse<O>> callFunction) {
            this.requestBuilder = requestBuilder;
            this.callFunction = callFunction;
            fetchPage();
        }

        @Override
        public boolean hasNext() {
            return currentPage != null &&
                    currentPage.getTotalResults() > idx.get();
        }

        @Override
        public O next() {
            int positionInPage = idx.get() % currentPage.getPerPage();
            if (positionInPage >= currentPage.getResults().size()) {
                fetchPage();
                positionInPage = 0;
            }
            idx.getAndIncrement();
            return currentPage.getResults().get(positionInPage);
        }

        private void fetchPage() {
            currentPage = callFunction.apply(requestBuilder.apply(pageNumber.incrementAndGet()));
        }
    }

    public static <I extends PaginatedRequest<I>, O> Stream<O> stream(Function<Integer, PaginatedRequest<I>> requestBuilder, Function<PaginatedRequest<I>, PaginatedResponse<O>> callFunction) {
        AutoPager<I, O> autoPageIterator = new AutoPager<>(requestBuilder, callFunction);
        return StreamSupport.stream(autoPageIterator.spliterator(), false);
    }
}

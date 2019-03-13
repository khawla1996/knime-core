/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.core.data;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.knime.core.data.predicate.FilterPredicate;
import org.knime.core.node.util.CheckUtils;

/**
 * Classes extending this class iterate over the rows of a {@link DataTable}.
 * Each DataTable has its specific <code>RowIterator</code>, which returns
 * the rows one by one. A <code>RowIterator</code> must return the rows always
 * in the same order.
 *
 * <p>
 * Use RowIterators as follows:
 *
 * <pre>
 *     DataTable table = ...;
 *     for (RowIterator it = table.getRowIterator(); it.hasNext();) {
 *         DataRow row = it.next();
 *         ...
 *     }
 * </pre>
 *
 * <p>
 * or, if you don't need access to the iterator:
 *
 * <pre>
 *     DataTable table =...;
 *     for (DataRow row : table) {
 *       // access the row here
 *     }
 * </pre>
 *
 * <p>
 * Note, the difference of this class to a generic Iterator&lt;DataRow&gt; is
 * that it does not allow to remove elements.
 *
 * @see DataRow
 *
 * @author Thomas Gabriel, University of Konstanz
 */
public abstract class RowIterator implements Iterator<DataRow> {

    /**
     * Returns <code>true</code> if there are more rows and <code>false</code>
     * otherwise.
     *
     * @see RowIterator#next()
     * @return <code>true</code> if the iterator has more elements, otherwise
     *         <code>false</code>
     */
    @Override
    public abstract boolean hasNext();

    /**
     * Returns the next <code>DataRow</code>.
     *
     * @return the next row in the <code>DataTable</code>
     * @throws java.util.NoSuchElementException if there are no more rows
     */
    @Override
    public abstract DataRow next();

    /**
     * NOT supported by the DataTable iterator! DataTables are immutable
     * read-only objects after their creation. Do not call this method, it will
     * throw an exception.
     *
     * @throws UnsupportedOperationException if the <tt>remove</tt>
     *                operation is not supported by this Iterator.
     */
    @Override
    public final void remove() {
        throw new UnsupportedOperationException("Can't remove row from table."
                + " Data tables are read-only.");
    }


    /**
     * Abstract class for building {@link RowIterator}s that only iterate over filtered parts of a table.
     *
     * @param <T> recursive type parameter of this class, which is enable method chaining to work with subclasses
     *            (covariant return typing)
     * @author Marc Bux, KNIME AG, Zurich, Switzerland
     * @since 3.8
     */
    protected abstract static class Builder<T extends Builder<T>> {

        private final DataTableSpec m_spec;

        private int[] m_columnIndices;

        private long m_fromIndex = 0;

        private long m_toIndex = Long.MAX_VALUE;

        private FilterPredicate m_predicate;

        protected Builder(final DataTableSpec spec) {
            m_spec = CheckUtils.checkArgumentNotNull(spec, "Spec must not be null");
        }

        /**
         * Iterate only over selected columns of the table. Accessing {@link org.knime.core.data.DataCell}s with indices
         * other than the specified indices will lead to an
         * {@link org.knime.core.data.UnmaterializedCell.UnmaterializedDataCellException} being thrown.
         *
         * @param indices the indices of columns over which to iterate
         * @return this {@link RowIteratorBuilder}
         * @throws IndexOutOfBoundsException for indices smaller than 0 or larger than the width of the table
         * @throws IllegalArgumentException if there are duplicates amongst the indices
         *
         * @see org.knime.core.data.DataRow
         */
        public T filterColumns(final int... indices) {
            m_spec.verifyIndices(indices);
            m_columnIndices = indices;
            return self();
        }

        public T filterColumns(final String... columns) {
            return filterColumns(m_spec.columnsToIndices(columns));
        }

        public T filterRowsFrom(final long index) {
            CheckUtils.checkArgument(index >= 0, "Row index must be at least 0.");
            CheckUtils.checkArgument(index <= m_toIndex, "Row index to filter from cannot be higher than row index to filter to.");
            m_fromIndex = index;
            return self();
        }

        public T filterRowsTo(final long index) {
            CheckUtils.checkArgument(index >= 0, "Row index must be at least 0.");
            CheckUtils.checkArgument(index >= m_fromIndex, "Row index to filter to cannot be lower than row index to filter from.");
            m_toIndex = index;
            return self();
        }

        public T filter(final FilterPredicate predicate) {
            CheckUtils.checkArgumentNotNull(predicate, "Predicate must not be null.");
            m_predicate = predicate.validate(m_spec);
            return self();
        }

        /**
         * @return the indices of to-be-kept columns
         */
        protected int[] getColumnIndices() {
            return m_columnIndices;
        }

        /**
         * @return the index of the first to-be-kept row
         */
        protected long getFromIndex() {
            return m_fromIndex;
        }

        /**
         * @return the index of the first to-be-kept row
         */
        protected long getToIndex() {
            return m_toIndex;
        }

        protected FilterPredicate getPredicate() {
            return m_predicate;
        }

        /**
         * Build a new row iterator with the behavior specified via methods invoked in this builder.
         *
         * @return a new row iterator
         */
        public abstract RowIterator build();

        /**
         * Simulated self-type idiom, which is required to enable method chaining to work with subclasses (covariant
         * return typing).
         *
         * @return the type of the subclass
         */
        protected abstract T self();
    }

    /**
     * @since 3.8
     */
    public static class DefaultBuilder extends Builder<DefaultBuilder> implements RowIteratorBuilder<RowIterator> {

        private final Supplier<RowIterator> m_iteratorSupplier;

        public DefaultBuilder(final Supplier<RowIterator> iteratorSupplier, final DataTableSpec spec) {
            super(spec);
            m_iteratorSupplier = CheckUtils.checkArgumentNotNull(iteratorSupplier, "Argument must not be null");
        }

        @Override
        public RowIterator build() {
            // build a new row iterator that forwards to a FilterDelegateRowIterator
            return new RowIterator() {
                FilterDelegateRowIterator m_filter = new FilterDelegateRowIterator(m_iteratorSupplier.get(),
                    getFromIndex(), getToIndex(), getPredicate());

                @Override
                public DataRow next() {
                    return m_filter.next();
                }

                @Override
                public boolean hasNext() {
                    return m_filter.hasNext();
                }
            };
        }

        @Override
        protected DefaultBuilder self() {
            return this;
        }

    }

    /**
     * @since 3.8
     */
    public static class FilterDelegateRowIterator extends RowIterator {

        private final RowIterator m_delegate;

        private final long m_fromIndex;

        private final long m_toIndex;

        private final FilterPredicate m_predicate;

        private long m_index;

        private DataRow m_nextRow;

        public FilterDelegateRowIterator(final RowIterator iterator, final long fromIndex, final long toIndex,
            final FilterPredicate predicate) {
            m_delegate = iterator;
            m_fromIndex = fromIndex;
            m_toIndex = toIndex;
            m_predicate = predicate;
            m_index = 0;
            m_nextRow = internalNext();
        }

        @Override
        public boolean hasNext() {
            return m_nextRow != null;
        }

        @Override
        public DataRow next() {
            if (m_nextRow == null) {
                throw new NoSuchElementException();
            }
            DataRow nextRow = m_nextRow;
            m_nextRow = internalNext();
            return nextRow;
        }

        private DataRow internalNext() {
            while (m_delegate.hasNext() && m_index <= m_toIndex) {
                final DataRow row = m_delegate.next();
                if (m_index++ >= m_fromIndex && (m_predicate == null || m_predicate.keep(row))) {
                    return row;
                }
            }
            return null;
        }

    }

}

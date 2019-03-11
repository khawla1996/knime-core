/*
 * ------------------------------------------------------------------------
 *
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
 * ---------------------------------------------------------------------
 *
 * History
 *   11 Mar 2019 (Marc): created
 */
package org.knime.core.data.predicate;

import java.util.function.Predicate;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;

/**
 *
 * @author Marc
 * @since 3.8
 */
@SuppressWarnings("javadoc")
public abstract class ColumnPredicate<T> implements FilterPredicate {
    private final Column<T> m_column;

    ColumnPredicate(final Column<T> column) {
        m_column = column;
    }

    @Override
    public FilterPredicate validate(final DataTableSpec spec) {
        m_column.validate(spec);
        return this;
    }

    Column<T> getColumn() {
        return m_column;
    }

    public static final class CustomPredicate<T> extends ColumnPredicate<T> {
        Predicate<T> m_predicate;

        CustomPredicate(final Column<T> column, final Predicate<T> predicate) {
            super(column);
            m_predicate = predicate;
        }

        @Override
        public boolean keep(final DataRow row) {
            return m_predicate.test(getColumn().getValue(row));
        }
    }

    static abstract class ValuePredicate<T> extends ColumnPredicate<T> {
        private final T m_value;

        private ValuePredicate(final Column<T> column, final T value) {
            super(column);
            m_value = value;
        }

        public T getValue() {
            return m_value;
        }
    }

    public static final class EqualTo<T> extends ValuePredicate<T> {
        EqualTo(final Column<T> column, final T value) {
            super(column, value);
        }

        @Override
        public boolean keep(final DataRow row) {
            return getColumn().getValue(row).equals(getValue());
        }
    }

    public static final class NotEqualTo<T> extends ValuePredicate<T> {
        NotEqualTo(final Column<T> column, final T value) {
            super(column, value);
        }

        @Override
        public boolean keep(final DataRow row) {
            return !getColumn().getValue(row).equals(getValue());
        }
    }

    static abstract class OrderPredicate<T extends Comparable<T>> extends ValuePredicate<T> {
        private OrderPredicate(final Column<T> column, final T value) {
            super(column, value);
        }
    }

    public static final class LesserThan<T extends Comparable<T>> extends OrderPredicate<T> {
        LesserThan(final Column<T> column, final T value) {
            super(column, value);
        }

        @Override
        public boolean keep(final DataRow row) {
            return getColumn().getValue(row).compareTo(getValue()) < 0;
        }
    }

    public static final class LesserThanOrEqualTo<T extends Comparable<T>> extends OrderPredicate<T> {
        LesserThanOrEqualTo(final Column<T> column, final T value) {
            super(column, value);
        }

        @Override
        public boolean keep(final DataRow row) {
            return getColumn().getValue(row).compareTo(getValue()) <= 0;
        }
    }

    public static final class GreaterThan<T extends Comparable<T>> extends OrderPredicate<T> {
        GreaterThan(final Column<T> column, final T value) {
            super(column, value);
        }

        @Override
        public boolean keep(final DataRow row) {
            return getColumn().getValue(row).compareTo(getValue()) > 0;
        }
    }

    public static final class GreaterThanOrEqualTo<T extends Comparable<T>> extends OrderPredicate<T> {
        GreaterThanOrEqualTo(final Column<T> column, final T value) {
            super(column, value);
        }

        @Override
        public boolean keep(final DataRow row) {
            return getColumn().getValue(row).compareTo(getValue()) >= 0;
        }
    }

}

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
 *   7 Mar 2019 (Marc): created
 */
package org.knime.core.data.predicate;

import java.util.function.Predicate;

//import java.util.function.Predicate;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.predicate.BinaryLogicalPredicate.And;
import org.knime.core.data.predicate.BinaryLogicalPredicate.Or;
import org.knime.core.data.predicate.ColumnPredicate.CustomPredicate;
import org.knime.core.data.predicate.ColumnPredicate.EqualTo;
import org.knime.core.data.predicate.ColumnPredicate.GreaterThan;
import org.knime.core.data.predicate.ColumnPredicate.GreaterThanOrEqualTo;
import org.knime.core.data.predicate.ColumnPredicate.LesserThan;
import org.knime.core.data.predicate.ColumnPredicate.LesserThanOrEqualTo;
import org.knime.core.data.predicate.ColumnPredicate.NotEqualTo;

/**
 *
 * @author Marc
 * @since 3.8
 */
@SuppressWarnings("javadoc")
public interface FilterPredicate {

    boolean keep(DataRow row);

    FilterPredicate validate(DataTableSpec spec);

    default FilterPredicate negate() {
        return new Not(this);
    }

    default FilterPredicate or(final FilterPredicate other) {
        return new Or(this, other);
    }

    default FilterPredicate and(final FilterPredicate other) {
        return new And(this, other);
    }

    static <T, C extends Column<T>> FilterPredicate udf(final C column, final Predicate<T> predicate) {
        return new CustomPredicate<T>(column, predicate);
    }

    static <T, C extends Column<T>> FilterPredicate eq(final C column, final T value) {
        return new EqualTo<T>(column, value);
    }

    static <T, C extends Column<T>> FilterPredicate neq(final C column, final T value) {
        return new NotEqualTo<T>(column, value);
    }

    static <T extends Comparable<T>, C extends Column<T>> FilterPredicate lt(final C column, final T value) {
        return new LesserThan<T>(column, value);
    }

    static <T extends Comparable<T>, C extends Column<T>> FilterPredicate leq(final C column, final T value) {
        return new LesserThanOrEqualTo<T>(column, value);
    }

    static <T extends Comparable<T>, C extends Column<T>> FilterPredicate gt(final C column, final T value) {
        return new GreaterThan<T>(column, value);
    }

    static <T extends Comparable<T>, C extends Column<T>> FilterPredicate geq(final C column, final T value) {
        return new GreaterThanOrEqualTo<T>(column, value);
    }

}

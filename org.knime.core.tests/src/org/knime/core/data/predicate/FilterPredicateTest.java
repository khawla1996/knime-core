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
 *   13 Mar 2019 (Marc): created
 */
package org.knime.core.data.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.knime.core.data.predicate.Column.intCol;
import static org.knime.core.data.predicate.Column.longCol;
import static org.knime.core.data.predicate.FilterPredicate.eq;
import static org.knime.core.data.predicate.FilterPredicate.geq;
import static org.knime.core.data.predicate.FilterPredicate.gt;
import static org.knime.core.data.predicate.FilterPredicate.leq;
import static org.knime.core.data.predicate.FilterPredicate.lt;
import static org.knime.core.data.predicate.FilterPredicate.neq;
import static org.knime.core.data.predicate.FilterPredicate.udf;

import org.junit.Test;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;

/**
 *
 * @author Marc
 */
public class FilterPredicateTest {

    private static final DataTableSpec INT_SPEC = new DataTableSpec(new DataColumnSpecCreator("int", IntCell.TYPE).createSpec());
    private static final DataTableSpec LONG_SPEC = new DataTableSpec(new DataColumnSpecCreator("long", LongCell.TYPE).createSpec());
    private static final DataRow ONE = new DefaultRow(RowKey.createRowKey(1l), new IntCell(1));
    private static final DataRow TWO = new DefaultRow(RowKey.createRowKey(2l), new IntCell(2));
    private static final DataRow THREE = new DefaultRow(RowKey.createRowKey(3l), new IntCell(3));

    @Test
    public void testUdf() {
        final FilterPredicate udfEq2 = udf(intCol(0), i -> i == 2);
        assertFalse(udfEq2.keep(ONE));
        assertTrue(udfEq2.keep(TWO));
        assertFalse(udfEq2.keep(THREE));
    }

    @Test
    public void testEq() {
        final FilterPredicate eq2 = eq(intCol(0), 2);
        assertFalse(eq2.keep(ONE));
        assertTrue(eq2.keep(TWO));
        assertFalse(eq2.keep(THREE));
    }

    @Test
    public void testNeq() {
        final FilterPredicate neq2 = neq(intCol(0), 2);
        assertTrue(neq2.keep(ONE));
        assertFalse(neq2.keep(TWO));
        assertTrue(neq2.keep(THREE));
    }

    @Test
    public void testLt() {
        final FilterPredicate lt2 = lt(intCol(0), 2);
        assertTrue(lt2.keep(ONE));
        assertFalse(lt2.keep(TWO));
        assertFalse(lt2.keep(THREE));
    }

    @Test
    public void testLeq() {
        final FilterPredicate leq2 = leq(intCol(0), 2);
        assertTrue(leq2.keep(ONE));
        assertTrue(leq2.keep(TWO));
        assertFalse(leq2.keep(THREE));
    }

    @Test
    public void testGt() {
        final FilterPredicate gt2 = gt(intCol(0), 2);
        assertFalse(gt2.keep(ONE));
        assertFalse(gt2.keep(TWO));
        assertTrue(gt2.keep(THREE));
    }

    @Test
    public void testGeq() {
        final FilterPredicate geq2 = geq(intCol(0), 2);
        assertFalse(geq2.keep(ONE));
        assertTrue(geq2.keep(TWO));
        assertTrue(geq2.keep(THREE));
    }

    @Test
    public void testNegate() {
        final FilterPredicate neq2 = eq(intCol(0), 2).negate();
        assertTrue(neq2.keep(ONE));
        assertFalse(neq2.keep(TWO));
        assertTrue(neq2.keep(THREE));
    }

    @Test
    public void testOr() {
        final FilterPredicate lt2OrGt2 = lt(intCol(0), 2).or(gt(intCol(0), 2));
        assertTrue(lt2OrGt2.keep(ONE));
        assertFalse(lt2OrGt2.keep(TWO));
        assertTrue(lt2OrGt2.keep(THREE));
    }

    @Test
    public void testAnd() {
        final FilterPredicate leq2AndGeq2 = leq(intCol(0), 2).and(geq(intCol(0), 2));
        assertFalse(leq2AndGeq2.keep(ONE));
        assertTrue(leq2AndGeq2.keep(TWO));
        assertFalse(leq2AndGeq2.keep(THREE));
    }


    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsLow() {
        eq(intCol(-1), 2).validate(INT_SPEC);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsHigh() {
        eq(intCol(1), 2).validate(INT_SPEC);
    }

    public void testCompatibleTypes() {
        final FilterPredicate eq2l = eq(longCol(0), 2l).validate(INT_SPEC);
        assertFalse(eq2l.keep(ONE));
        assertTrue(eq2l.keep(TWO));
        assertFalse(eq2l.keep(THREE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleTypes() {
        eq(intCol(0), 2).validate(LONG_SPEC);
    }

}

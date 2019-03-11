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

import org.knime.core.data.BooleanValue;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.StringValue;

/**
 *
 * @author Marc
 * @since 3.8
 */
@SuppressWarnings("javadoc")
public abstract class IndexedColumn<T> implements Column<T> {
    private final int m_index;

    private IndexedColumn(final int index) {
        m_index = index;
    }

    int getIndex() {
        return m_index;
    }

    @Override
    public void validate(final DataTableSpec spec) {
        if (m_index < 0 || m_index >= spec.getNumColumns()) {
            throw new IndexOutOfBoundsException("Column index out of range: " + m_index);
        }

        DataType type = spec.getColumnSpec(m_index).getType();
        Class<? extends DataValue> dataValueClass = getDataValueClass();
        if (!type.isCompatible(getDataValueClass())) {
            throw new IllegalArgumentException("Column at index " + m_index + " is of type " + type.getName()
                + ", which is incompatible to " + dataValueClass.getName() + ".");
        }
    }

    abstract Class<? extends DataValue> getDataValueClass();

    public static final class IntColumn extends IndexedColumn<Integer> {
        IntColumn(final int index) {
            super(index);
        }

        @Override
        public Integer getValue(final DataRow row) {
            return ((IntValue)row.getCell(getIndex())).getIntValue();
        }

        @Override
        Class<IntValue> getDataValueClass() {
            return IntValue.class;
        }
    }

    public static final class LongColumn extends IndexedColumn<Long> {
        LongColumn(final int index) {
            super(index);
        }

        @Override
        public Long getValue(final DataRow row) {
            return ((LongValue)row.getCell(getIndex())).getLongValue();
        }

        @Override
        Class<LongValue> getDataValueClass() {
            return LongValue.class;
        }
    }

    public static final class DoubleColumn extends IndexedColumn<Double> {
        DoubleColumn(final int index) {
            super(index);
        }

        @Override
        public Double getValue(final DataRow row) {
            return ((DoubleValue)row.getCell(getIndex())).getDoubleValue();
        }

        @Override
        Class<DoubleValue> getDataValueClass() {
            return DoubleValue.class;
        }
    }

    public static final class BooleanColumn extends IndexedColumn<Boolean> {
        BooleanColumn(final int index) {
            super(index);
        }

        @Override
        public Boolean getValue(final DataRow row) {
            return ((BooleanValue)row.getCell(getIndex())).getBooleanValue();
        }

        @Override
        Class<BooleanValue> getDataValueClass() {
            return BooleanValue.class;
        }
    }

    public static final class StringColumn extends IndexedColumn<String> {
        StringColumn(final int index) {
            super(index);
        }

        @Override
        public String getValue(final DataRow row) {
            return ((StringValue)row.getCell(getIndex())).getStringValue();
        }

        @Override
        Class<StringValue> getDataValueClass() {
            return StringValue.class;
        }
    }

}

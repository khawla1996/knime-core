/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *
 * History
 *   10.05.2012 (kilian): created
 */
package org.knime.base.node.meta.looper.group;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;

/**
 * Creates the dialog of the group loop start node and provides static methods
 * which create the necessary settings models.
 *
 * @author Kilian Thiel, KNIME.com, Berlin, Germany
 */
class GroupLoopStartNodeDialog extends DefaultNodeSettingsPane {

    /**
     * Creates and returns the settings model, storing the selected columns.
     *
     * @return The settings model with the selected columns.
     */
    @SuppressWarnings("unchecked")
    static final SettingsModelColumnFilter2 getFilterDoubleColModel() {
        return new SettingsModelColumnFilter2(GroupLoopStartConfigKeys.COLUMN_NAMES);
    }

    /**
     * Creates and returns the settings model, storing the "sorted input table"
     * flag.
     *
     * @return The settings model with the "sorted input table" flag.
     */
    static final SettingsModelBoolean getSortedInputTableModel() {
        return new SettingsModelBoolean(
                GroupLoopStartConfigKeys.SORTED_INPUT_TABLE,
                GroupLoopStartNodeModel.DEF_SORTED_INPUT_TABLE);
    }

    /**
     * Creates new instance of <code>GroupLoopStartNodeDialog</code>.
     */
    public GroupLoopStartNodeDialog() {

        // column selection
        addDialogComponent(new DialogComponentColumnFilter2(
                getFilterDoubleColModel(), 0));

        // sorted input table
        addDialogComponent(
                new DialogComponentBoolean(getSortedInputTableModel(),
                        "Input is already sorted by group column(s) "
                        + "[execution fails if not correctly sorted]"));
    }
}

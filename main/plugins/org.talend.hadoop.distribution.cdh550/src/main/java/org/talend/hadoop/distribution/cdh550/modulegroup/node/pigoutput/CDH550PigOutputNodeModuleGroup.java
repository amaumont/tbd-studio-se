// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.hadoop.distribution.cdh550.modulegroup.node.pigoutput;

import java.util.HashSet;
import java.util.Set;

import org.talend.core.hadoop.version.EHadoopDistributions;
import org.talend.hadoop.distribution.ComponentType;
import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.cdh550.CDH550Constant;
import org.talend.hadoop.distribution.cdh550.CDH550Distribution;
import org.talend.hadoop.distribution.condition.BooleanOperator;
import org.talend.hadoop.distribution.condition.ComponentCondition;
import org.talend.hadoop.distribution.condition.EqualityOperator;
import org.talend.hadoop.distribution.condition.LinkedNodeExpression;
import org.talend.hadoop.distribution.condition.MultiComponentCondition;
import org.talend.hadoop.distribution.condition.SimpleComponentCondition;
import org.talend.hadoop.distribution.constants.PigOutputConstant;

public class CDH550PigOutputNodeModuleGroup {

    // This condition stands for:
    // (#LINK@NODE.ASSOCIATED_PIG_LOAD.DISTRIBUTION=='CLOUDERA' AND #LINK@NODE.ASSOCIATED_PIG_LOAD=='Cloudera_CDH5_5')
    private static final ComponentCondition condition = new MultiComponentCondition(new SimpleComponentCondition(
            new LinkedNodeExpression(PigOutputConstant.PIGSTORE_COMPONENT_LINKEDPARAMETER,
                    ComponentType.PIG.getDistributionParameter(), EHadoopDistributions.CLOUDERA.getName(), EqualityOperator.EQ)),
            new SimpleComponentCondition(new LinkedNodeExpression(PigOutputConstant.PIGSTORE_COMPONENT_LINKEDPARAMETER,
                    ComponentType.PIG.getVersionParameter(), CDH550Distribution.VERSION, EqualityOperator.EQ)),
            BooleanOperator.AND);

    public static Set<DistributionModuleGroup> getModuleGroups() {
        Set<DistributionModuleGroup> hs = new HashSet<>();
        DistributionModuleGroup dmg = new DistributionModuleGroup(CDH550Constant.PIG_PARQUET_MODULE_GROUP.getModuleName(), false,
                condition);
        hs.add(dmg);
        return hs;
    }

}

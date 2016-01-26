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
package org.talend.hadoop.distribution.component;

/**
 * Interface that exposes specific Sqoop methods.
 *
 */
public interface SqoopComponent extends MRComponent {

    /**
     * @return true if the distribution does support the file storage for the database password in Java API mode.
     */
    public boolean doJavaAPISupportStorePasswordInFile();

    /**
     * @return true if the distribution does support the target directory deletion for the Sqoop Import (in JAVA API
     * mode).
     */
    public boolean doJavaAPISqoopImportSupportDeleteTargetDir();

    /**
     * @return true if the distribution does support the table exclusion for the Sqoop Import All Tables (in JAVA API
     * mode).
     */
    public boolean doJavaAPISqoopImportAllTablesSupportExcludeTable();

}

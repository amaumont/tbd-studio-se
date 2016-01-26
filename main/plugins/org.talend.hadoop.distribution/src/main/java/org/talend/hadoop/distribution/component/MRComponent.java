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
 * Interface that exposes specific Map/Reduce methods.
 *
 */
public interface MRComponent extends HadoopComponent {

    /**
     * @return true if the distribution executes its M/R job trhough WebHCat. It returns false if the distribution
     * executes through the resource manager.
     */
    public boolean isExecutedThroughWebHCat();

    /**
     * @return true if the distribution is compatible with cloudera navigator (usually, cloudera distribution > 4.3).
     */
    public boolean doSupportClouderaNavigator();

    /**
     * @return true if the distribution supports the "cross platform submission" Hadoop property.
     */
    public boolean doSupportCrossPlatformSubmission();

    /**
     * @return true if the distribution supports the user impersonation when submitting a job.
     */
    public boolean doSupportImpersonation();

    /**
     * @return the yarn application classpath of the distribution. This method won't be used for @link{EHadoopVersion}
     * HADOOP_1
     */
    public String getYarnApplicationClasspath();

}

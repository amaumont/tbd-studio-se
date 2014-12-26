// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.hdfsbrowse.hadoop.service.check.provider;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.talend.core.utils.ReflectionUtils;
import org.talend.designer.hdfsbrowse.hadoop.service.HadoopServiceProperties;
import org.talend.designer.hdfsbrowse.hadoop.service.check.AbstractCheckedServiceProvider;
import org.talend.designer.hdfsbrowse.manager.HadoopClassLoaderFactory2;

/**
 * created by ycbai on Aug 6, 2014 Detailled comment
 *
 */
public class CheckedNamenodeProvider extends AbstractCheckedServiceProvider {

    @Override
    protected Object check(HadoopServiceProperties serviceProperties, ClassLoader classLoader) throws Exception {
        Object fileSystem = null;
        try {
            Object conf = Class.forName("org.apache.hadoop.conf.Configuration", true, classLoader).newInstance(); //$NON-NLS-1$
            URI nameNodeURI = URI.create(serviceProperties.getNameNode());
            String scheme = nameNodeURI.getScheme();
            ReflectionUtils.invokeMethod(conf, "set", new Object[] { String.format("fs.%s.impl.disable.cache", scheme), "true" }); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            ReflectionUtils.invokeMethod(conf, "set", new Object[] { "dfs.client.retry.policy.enabled", "false" }); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            ReflectionUtils.invokeMethod(conf, "set", new Object[] { "ipc.client.connect.max.retries", "0" }); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            setHadoopProperties(conf, serviceProperties);
            String userName = StringUtils.trimToNull(serviceProperties.getUserName());
            String group = StringUtils.trimToNull(serviceProperties.getGroup());
            boolean useKrb = serviceProperties.isUseKrb();
            if (useKrb) {
                String nameNodePrincipal = serviceProperties.getPrincipal();
                ReflectionUtils.invokeMethod(conf, "set", new Object[] { "dfs.namenode.kerberos.principal", nameNodePrincipal }); //$NON-NLS-1$//$NON-NLS-2$
                boolean useKeytab = serviceProperties.isUseKeytab();
                if (useKeytab) {
                    String keytabPrincipal = serviceProperties.getKeytabPrincipal();
                    String keytab = serviceProperties.getKeytab();
                    ReflectionUtils.invokeStaticMethod("org.apache.hadoop.security.UserGroupInformation", classLoader, //$NON-NLS-1$
                            "loginUserFromKeytab", new String[] { keytabPrincipal, keytab }); //$NON-NLS-1$
                }
            } else if (userName != null && group != null) {
                ReflectionUtils.invokeMethod(conf, "set", new Object[] { "hadoop.job.ugi", userName + "," + group }); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            }
            fileSystem = ReflectionUtils.invokeStaticMethod("org.apache.hadoop.fs.FileSystem", classLoader, "get", //$NON-NLS-1$ //$NON-NLS-2$
                    new Object[] { nameNodeURI, conf });
            if (fileSystem != null) {
                Object pathObj = ReflectionUtils.newInstance("org.apache.hadoop.fs.Path", classLoader, //$NON-NLS-1$
                        new Object[] { "/" }); //$NON-NLS-1$
                ReflectionUtils.invokeMethod(fileSystem, "listStatus", new Object[] { pathObj }); //$NON-NLS-1$
            }
        } finally {
            if (fileSystem != null) {
                try {
                    ReflectionUtils.invokeMethod(fileSystem, "close", new Object[0]); //$NON-NLS-1$
                } catch (Exception e) {
                    e.printStackTrace(); // Dont care this..
                }
            }
        }

        return fileSystem;
    }

    @Override
    protected ClassLoader getClassLoader(HadoopServiceProperties serviceProperties) {
        ClassLoader loader = null;
        if (serviceProperties.isCustom()) {
            loader = HadoopClassLoaderFactory2.getHadoopCustomClassLoader(serviceProperties.getUid(),
                    serviceProperties.getCustomJars());
        } else {
            loader = HadoopClassLoaderFactory2.getHDFSClassLoader(serviceProperties.getDistribution(),
                    serviceProperties.getVersion(), serviceProperties.isUseKrb());
        }

        return loader;
    }

}
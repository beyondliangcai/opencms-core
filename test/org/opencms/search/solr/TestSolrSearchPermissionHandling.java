/*
 * File   : $Source$
 * Date   : $Date$
 * Version: $Revision$
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.search.solr;

import org.opencms.file.CmsObject;
import org.opencms.main.CmsContextInfo;
import org.opencms.main.OpenCms;
import org.opencms.report.CmsShellReport;
import org.opencms.report.I_CmsReport;
import org.opencms.test.OpenCmsTestCase;
import org.opencms.test.OpenCmsTestProperties;

import java.util.Locale;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the Solr permission handling.<p>
 * 
 * @since 8.5.0
 */
public class TestSolrSearchPermissionHandling extends OpenCmsTestCase {

    /**
     * Default JUnit constructor.<p>
     * 
     * @param arg0 JUnit parameters
     */
    public TestSolrSearchPermissionHandling(String arg0) {

        super(arg0);
    }

    /**
     * Test suite for this test class.<p>
     * 
     * @return the test suite
     */
    public static Test suite() {

        OpenCmsTestProperties.initialize(org.opencms.test.AllTests.TEST_PROPERTIES_PATH);

        TestSuite suite = new TestSuite();
        suite.setName(TestSolrSearchPermissionHandling.class.getName());
        suite.addTest(new TestSolrSearchPermissionHandling("testPermissionHandling"));

        TestSetup wrapper = new TestSetup(suite) {

            @Override
            protected void setUp() {

                setupOpenCms("solrtest", "/");
            }

            @Override
            protected void tearDown() {

                removeOpenCms();
            }
        };

        return wrapper;
    }

    /**
     * @throws Throwable
     */
    public void testPermissionHandling() throws Throwable {

        echo("Testing search for permission check by comparing result counts");

        echo("ID Admin: " + getCmsObject().getRequestContext().getCurrentUser().getId().toString());
        echo("ID test1: " + getCmsObject().readUser("test1").getId().toString());
        echo("ID test2: " + getCmsObject().readUser("test2").getId().toString());

        I_CmsReport report = new CmsShellReport(Locale.ENGLISH);
        OpenCms.getSearchManager().rebuildIndex(AllSolrTests.SOLR_OFFLINE, report);

        CmsSolrIndex index = OpenCms.getSearchManager().getIndexSolr(AllSolrTests.SOLR_OFFLINE);

        CmsSolrQuery squery = new CmsSolrQuery(getCmsObject());
        squery.setSearchRoots("/sites/default/");
        CmsSolrResultList results = index.search(getCmsObject(), squery);
        AllSolrTests.printResults(getCmsObject(), results, true);

        CmsObject cms = OpenCms.initCmsObject(getCmsObject(), new CmsContextInfo("test1"));
        results = index.search(cms, squery);
        AllSolrTests.printResults(cms, results, true);

        cms = OpenCms.initCmsObject(getCmsObject(), new CmsContextInfo("test2"));
        results = index.search(cms, squery);
        AllSolrTests.printResults(cms, results, true);

        //        assertEquals(1, results.size());
        //        assertEquals("/sites/default/types/text.txt", (results.get(0)).getRootPath());

    }
}

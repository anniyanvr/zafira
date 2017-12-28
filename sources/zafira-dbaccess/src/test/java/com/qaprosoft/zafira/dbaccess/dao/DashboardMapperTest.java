/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.dbaccess.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.qaprosoft.zafira.dbaccess.utils.KeyGenerator;
import com.qaprosoft.zafira.dbaccess.utils.Sort;
import com.qaprosoft.zafira.models.db.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.qaprosoft.zafira.dbaccess.dao.mysql.DashboardMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.WidgetMapper;
import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.models.db.Widget;

@Test
@ContextConfiguration("classpath:com/qaprosoft/zafira/dbaccess/dbaccess-test.xml")
public class DashboardMapperTest extends AbstractTestNGSpringContextTests {

    /**
     * Turn this on to enable this test
     */
    private static final boolean ENABLED = false;

    private static final List<Widget> WIDGETS = new ArrayList<Widget>() {

        private static final long serialVersionUID = 1L;
        {
            add(new Widget() {
                private static final long serialVersionUID = 1L;
                {
                    setModel("m1");
                    setSql("s1");
                    setLocation("");
                    setTitle("t1");
                    setType("wt1");
                }
            });
        }
    };

    private static final Attribute ATTRIBUTE = new Attribute()
    {
        private static final long serialVersionUID = 1L;
        {
            setKey("k1" + KeyGenerator.getKey());
            setValue("v1");
        }
    };

    private static final Widget WIDGET = new Widget()
    {
        private static final long serialVersionUID = 1L;
        {
            setModel("m1");
            setSql("s1");
            setLocation("");
            setTitle("t1");
            setType("wt1");
        }
    };

    private static final Dashboard DASHBOARD = new Dashboard()
    {
        private static final long serialVersionUID = 1L;
        {
            setTitle("t1");
            setHidden(true);
            setPosition(0);
            setWidgets(WIDGETS);
        }
    };

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private WidgetMapper widgetMapper;

    @Test(enabled = ENABLED)
    public void createDashboard()
    {
        dashboardMapper.createDashboard(DASHBOARD);
        assertNotEquals(DASHBOARD.getId(), 0, "Dashboard ID must be set up by autogenerated keys");
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createDashboard"})
    public void addWidgetToDashboard() {
        widgetMapper.createWidget(WIDGET);
        dashboardMapper.addDashboardWidget(DASHBOARD.getId(), WIDGET);
        DASHBOARD.setWidgets(Arrays.asList(WIDGET));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createDashboard", "addWidgetToDashboard"})
    public void getDashboardById()
    {
        checkDashboard(dashboardMapper.getDashboardById(DASHBOARD.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createDashboard", "addWidgetToDashboard", "updateDashboard"})
    public void getDashboardByType() {
    	Sort<Dashboard> sort = new Sort<Dashboard>();
        List<Dashboard> dashboards = sort.sortById(dashboardMapper.getDashboardsByHidden(false));
        checkDashboard(dashboards.get(dashboards.size() - 1));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createDashboard", "addWidgetToDashboard"})
    public void getAllDashboards()
    {
        Sort<Dashboard> sort = new Sort<Dashboard>();
        List<Dashboard> dashboards = sort.sortById(dashboardMapper.getAllDashboards());
        checkDashboard(dashboards.get(dashboards.size() - 1));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createDashboard", "addWidgetToDashboard"})
    public void updateDashboard()
    {
        DASHBOARD.setTitle("t2");
        DASHBOARD.setHidden(false);
        DASHBOARD.setPosition(1);
        dashboardMapper.updateDashboard(DASHBOARD);
        checkDashboard(dashboardMapper.getDashboardById(DASHBOARD.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createDashboard", "getDashboardById", "getAllDashboards", "updateDashboard",
            "addWidgetToDashboard"})
    public void updateWidgetOnDashboard() {

        DASHBOARD.getWidgets().get(0).setLocation("");
        dashboardMapper.updateDashboardWidget(DASHBOARD.getId(), DASHBOARD.getWidgets().get(0));
        checkDashboard(dashboardMapper.getDashboardById(DASHBOARD.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createDashboard", "getDashboardById", "getAllDashboards", "updateDashboard",
            "addWidgetToDashboard", "updateWidgetOnDashboard"})
    public void removeWidgetFromDashboard() {
        dashboardMapper.deleteDashboardWidget(DASHBOARD.getId(), DASHBOARD.getWidgets().get(0).getId());
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createDashboard", "addWidgetToDashboard", "getDashboardById", "getAllDashboards", "updateDashboard", "updateWidgetOnDashboard", "removeWidgetFromDashboard"})
    public void deleteDashboardById()
    {
        dashboardMapper.deleteDashboardById((DASHBOARD.getId()));
        assertNull(dashboardMapper.getDashboardById(DASHBOARD.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"deleteDashboardById"})
    public void createDashboardAttribute() {
        dashboardMapper.createDashboard(DASHBOARD);
        dashboardMapper.createDashboardAttribute(DASHBOARD.getId(), ATTRIBUTE);
        Assert.assertNotNull(ATTRIBUTE.getId(), "");
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createDashboardAttribute"})
    public void getAttributesByDashboardId() {
        List<Attribute> attributeList = dashboardMapper.getAttributesByDashboardId(DASHBOARD.getId());
        Assert.assertEquals(ATTRIBUTE.getId(), attributeList.get(attributeList.size() - 1).getId());
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"getAttributesByDashboardId"})
    public void updateAttribute() {
        ATTRIBUTE.setKey("k1" + KeyGenerator.getKey());
        ATTRIBUTE.setValue("v2");
        dashboardMapper.updateAttribute(ATTRIBUTE);
        Attribute attribute = dashboardMapper.getAttributeById(ATTRIBUTE.getId());
        Assert.assertEquals(ATTRIBUTE.getKey(), attribute.getKey(), "");
        Assert.assertEquals(ATTRIBUTE.getValue(), attribute.getValue(), "");
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"updateAttribute"})
    public void deleteDashboardAttributeById() {
        dashboardMapper.deleteDashboardAttributeById(ATTRIBUTE.getId());
        Assert.assertNull(dashboardMapper.getAttributeById(ATTRIBUTE.getId()), "");
    }

    private void checkDashboard(Dashboard dashboard)
    {
        assertEquals(dashboard.getTitle(), DASHBOARD.getTitle(), "Dashboard title must match");
        assertEquals(dashboard.isHidden(), DASHBOARD.isHidden(), "Dashboard state must match");
        assertEquals(dashboard.getPosition(), DASHBOARD.getPosition(), "Dashboard position must match");
        List<Widget> widgets = dashboard.getWidgets();
        assertEquals(widgets.size(), DASHBOARD.getWidgets().size(), "Invalid amount of widgets!");
        for(int i = 0; i < widgets.size(); i++)
        {
        	assertEquals(widgets.get(i).getTitle(), DASHBOARD.getWidgets().get(i).getTitle(), "Widget title must match");
        	assertEquals(widgets.get(i).getType(), DASHBOARD.getWidgets().get(i).getType(), "Widget type must match");
        	assertEquals(widgets.get(i).getSql(), DASHBOARD.getWidgets().get(i).getSql(), "Widget sql must match");
        	assertEquals(widgets.get(i).getModel(), DASHBOARD.getWidgets().get(i).getModel(), "Widget model must match");
        	assertEquals(widgets.get(i).getLocation(), DASHBOARD.getWidgets().get(i).getLocation(), "Widget location must match");
        }
    }
}

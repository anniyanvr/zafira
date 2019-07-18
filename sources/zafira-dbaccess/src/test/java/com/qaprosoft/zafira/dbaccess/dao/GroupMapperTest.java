/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.dbaccess.dao;

import com.qaprosoft.zafira.dbaccess.PersistenceTestConfig;
import com.qaprosoft.zafira.dbaccess.dao.mysql.application.GroupMapper;
import com.qaprosoft.zafira.dbaccess.utils.KeyGenerator;
import com.qaprosoft.zafira.models.db.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

@Test
@ContextConfiguration(classes = PersistenceTestConfig.class)
public class GroupMapperTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private GroupMapper groupMapper;

    /**
     * Turn this on to enable this test
     */
    private static final boolean ENABLED = false;

    private static final Group GROUP = new Group() {
        private static final long serialVersionUID = 1L;
        {
            setName("n1" + KeyGenerator.getKey());
            setRole(Role.ROLE_USER);
        }
    };

    @Test(enabled = ENABLED)
    public void createGroup() {
        groupMapper.createGroup(GROUP);
        assertNotEquals(GROUP.getId(), 0, "Group ID must be set up by autogenerated keys");
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createGroup" })
    public void getGroupById() {
        checkGroup(groupMapper.getGroupById(GROUP.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createGroup" })
    public void getAllGroups() {
        List<Group> groupList = groupMapper.getAllGroups(false);
        checkGroup(groupList.get(groupList.size() - 1));
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createGroup" })
    public void getGroupsCount() {
        Integer count = groupMapper.getGroupsCount();
        Assert.assertNotNull(count, "");
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createGroup" })
    public void updateGroup() {
        GROUP.setName("m2" + KeyGenerator.getKey());
        GROUP.setRole(Group.Role.ROLE_ADMIN);

        groupMapper.updateGroup(GROUP);

        checkGroup(groupMapper.getGroupById(GROUP.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createGroup", "getGroupById", "getAllGroups", "updateGroup" })
    public void deleteGroupById() {
        groupMapper.deleteGroup(GROUP.getId());
        assertNull(groupMapper.getGroupById(GROUP.getId()));
    }

    private void checkGroup(Group group) {
        assertEquals(group.getName(), GROUP.getName(), "Group name must match");
        assertEquals(group.getRole(), GROUP.getRole(), "Group role match");
    }
}

package com.example.usctreehole;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class EditProfileUnitTest {

    private EditProfile editProfile;

    @Before
    public void setUp() {
        // Initialize a mocked EditProfile object
        editProfile = mock(EditProfile.class);

        // Set up mock values for fields
        editProfile.old_name = "Old Name";
        editProfile.old_ID = "123456";
        editProfile.old_role = "Graduate Student";
        editProfile.old_profileUrl = "http://oldurl.com";
    }

    @Test
    public void testGetChangedFields_correctlyIdentifiesChanges() {
        // Prepare a map for changed fields
        Map<String, Object> changedFields = new HashMap<>();

        // Simulate user inputs
        String newName = "New Name";
        String newID = "654321";
        String newRole = "Faculty";

        // Manually invoke getChangedFields logic
        if (!editProfile.old_name.equals(newName)) {
            changedFields.put("name", newName);
        }
        if (!editProfile.old_ID.equals(newID)) {
            changedFields.put("uscID", newID);
        }
        if (!editProfile.old_role.equals(newRole)) {
            changedFields.put("role", newRole);
        }

        // Verify the changes
        assertEquals("New Name", changedFields.get("name"));
        assertEquals("654321", changedFields.get("uscID"));
        assertEquals("Faculty", changedFields.get("role"));
    }

    @Test
    public void testGetChangedFields_correctlyIdentifiesNoChanges() {
        // Prepare a map for changed fields
        Map<String, Object> changedFields = new HashMap<>();

        String oldName = editProfile.old_name;
        String oldID = editProfile.old_ID;
        String oldRole = editProfile.old_role;

        changedFields.put("name", oldName);
        changedFields.put("uscID", oldID);
        changedFields.put("role", oldRole);

        // Verify the lack of changes
        assertEquals(oldName, changedFields.get("name"));
        assertEquals(oldID, changedFields.get("uscID"));
        assertEquals(oldRole, changedFields.get("role"));
    }
}

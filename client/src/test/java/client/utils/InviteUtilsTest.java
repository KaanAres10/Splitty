package client.utils;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class InviteUtilsTest {
    InviteUtils inviteUtils = new InviteUtils();

    @Test
    void generateRandomInviteCode() {
        assertEquals(inviteUtils.generateRandomInviteCode().length(), 8);
    }

    @Test
    void sendInvitation() {
        String code = inviteUtils.generateRandomInviteCode();
        String email = "altintaskaan55@hotmail.com";
        inviteUtils.sendInvitation(email,code);
        assertThrowsExactly(RuntimeException.class, () -> inviteUtils.sendInvitation("sdafsa","213214"));
    }
}
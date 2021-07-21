package Server;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class TestSecurity {

    //Test Random Salt Generation
    @Test
    public void generateSaltTest() throws Exception {
        Security Test = new Security();
        byte [] Salt = Test.generateSalt();
        assertNotEquals(Salt, Test.generateSalt());
    }

    //Tests clientHash correctly hashes a given string.
    @Test
    public void  clientHashTest() {
        Security Test = new Security();
        String test = Test.clientHash("Test");

        //Test to ensure plain text password is mutated and not recognisable .
        assertNotEquals(test,"Test");
        //Test to ensure SHA-512 Hashing algorithm is correctly functioning
        assertEquals("c6ee9e33cf5c6715a1d148fd73f7318884b41adcb916021e2bc0e800a5c5dd97f5142178f6ae88c8"+
                "fdd98e1afb0ce4c8d2c54b5f37b30b7da1997bb33b0b8a31",test);

    }

    //Tests ServerEncryption method correctly salts and hashes a given string and
    // that if the same input string is salted and hashed with the same salt the outputs will be the same.
    @Test
    public void  ServerEncryptionTest_1() throws Exception {
        Security Test = new Security();
        byte[] testSalt = Test.generateSalt();
        String test = Test.ServerEncryption("Test",testSalt);
        String test2 = Test.ServerEncryption("Test",testSalt);
        assertNotEquals(test,"Test");

        assertEquals(test,test2);

    }

    //Tests if the same string is salted with different salts then the output will be different.
    @Test
    public void  ServerEncryptionTest_2() throws Exception {
        Security Test = new Security();
        byte[] testSalt = Test.generateSalt();
        byte[] testSalt2 = Test.generateSalt();
        String test = Test.ServerEncryption("Test",testSalt);
        String test2 = Test.ServerEncryption("Test",testSalt2);

        assertNotEquals(test,test2);

    }

    //Tests to if different strings are salted and hashed using the same salt then the output will be different.
    @Test
    public void  ServerEncryptionTest_3() throws Exception {
        Security Test = new Security();
        byte[] testSalt = Test.generateSalt();
        String test = Test.ServerEncryption("Test",testSalt);
        String test2 = Test.ServerEncryption("Test123",testSalt);

        assertNotEquals(test,test2);
    }


}
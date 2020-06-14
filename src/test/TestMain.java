package test;

public class TestMain {
    public static ServerTests runServerTests;
    public static ChatTests runChatTests;

    public static void main(String args[]) {
        String mode = "";

        try {
            mode = args[0];
        } catch(Exception e){
            System.out.println("Nie wprowadzono zadnego argumentu!");
            System.exit(0);
        }

        if(mode.equals("server")){
            try {
                runServerTests = new ServerTests();
                serverTestPackage();
            } catch (Exception e) {
                System.out.println("Coś poszło nie tak! -> TestMain, testy serwera");
            }
        }
        else if(mode.equals("chat")) {
            try {
                runChatTests = new ChatTests();
                chatTestPackage();
            } catch (Exception e) {
                System.out.println("Coś poszło nie tak! -> TestMain, testy serwera");
            }
        }
        else {
            System.out.println("Wprowadz jeden z trybow testu przy ponownym uruchomieniu: server, chat");
        }
    }

    public static void serverTestPackage() {
        runServerTests.createThreadTest();
        runServerTests.createNameTest();
        runServerTests.createDocumentTest();
        runServerTests.openDocumentTest();
        runServerTests.editDocumentTest();
        runServerTests.endTests();
    }

    public static void chatTestPackage() {
        runChatTests.connectUserTest();
        runChatTests.sendMessageTest();
    }
}

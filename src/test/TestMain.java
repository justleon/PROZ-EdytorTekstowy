package test;

public class TestMain {
    public static ServerTests runTests = new ServerTests();

    public static void main(String args[]) {
        try {
            serverTestPackage();
        } catch (Exception e) {
            System.out.println("Coś poszło nie tak! -> TestMain, testy serwera");
        }
    }

    public static void serverTestPackage() {
        runTests.createThreadTest();
        runTests.createNameTest();
        runTests.createDocumentTest();
        runTests.openDocumentTest();
        runTests.editDocumentTest();
        runTests.endTests();
    }
}

package tests;

import manager.Managers;

import java.io.IOException;

class InMemoryTaskManagerTest extends TaskManagerTest {

    public InMemoryTaskManagerTest() throws IOException {
        this.manager = Managers.getDefault();
    }
}
package managerstest;

import manager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}
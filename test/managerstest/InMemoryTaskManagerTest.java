package managerstest;

import manager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}
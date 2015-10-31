package com.getknowledge.platform.modules.task;

import com.getknowledge.platform.base.services.AbstractService;
import org.springframework.stereotype.Service;

@Service("TaskService")
public class TaskService extends AbstractService {

    public void startup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        System.err.println("Start up");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}

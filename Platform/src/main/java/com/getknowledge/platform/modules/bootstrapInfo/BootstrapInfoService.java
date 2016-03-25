package com.getknowledge.platform.modules.bootstrapInfo;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.exceptions.ParseException;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import com.getknowledge.platform.utils.ModuleLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service("BootstrapInfoService")
public class BootstrapInfoService extends AbstractService {

    @Autowired
    private ModuleLocator moduleLocator;

    @Autowired
    private BootstrapInfoRepository repository;

    @Value("${bootstrap.password}")
    private String hashPassword;

    @Autowired
    private UserRepository userRepository;

    @Action(name = "do")
    @Transactional
    public Result doBootstrap(HashMap<String, Object> data) throws ParseException {

        boolean isAuthorized = false;

        User user = userRepository.getCurrentUser(data);

        if (user != null) {
            isAuthorized = true;
        }

        if (data.containsKey("initPassword")) {
            if (BCrypt.checkpw((String) data.get("initPassword"), hashPassword)) {
                isAuthorized = true;
            }
        }

        if (!isAuthorized) {
            return Result.AccessDenied();
        }

        repository.createFromServices();

        List<BootstrapService> bootstrapServices = moduleLocator.findAllBootstrapServices();
        bootstrapServices.sort(
                (o1, o2) -> {
                    if (o1.getBootstrapInfo().getOrder() > o2.getBootstrapInfo().getOrder()) {
                        return 1;
                    }

                    if (o1.getBootstrapInfo().getOrder() < o2.getBootstrapInfo().getOrder()) {
                        return -1;
                    }
                    return 0;
                }
        );
        for (BootstrapService bootstrapService : bootstrapServices) {
            repository.doBootstrap(bootstrapService,data);
        }

        return Result.Complete();
    }
}

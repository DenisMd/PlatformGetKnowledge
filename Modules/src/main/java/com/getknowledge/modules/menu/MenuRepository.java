package com.getknowledge.modules.menu;

import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

@Repository("MenuRepository")
public class MenuRepository extends ProtectedRepository<Menu> {

}

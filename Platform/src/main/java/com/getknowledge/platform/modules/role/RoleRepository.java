package com.getknowledge.platform.modules.role;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

@Repository(value = "RoleRepository")
public class RoleRepository extends BaseRepository<Role> {

    @Override
    protected Class<Role> getClassEntity() {
        return Role.class;
    }

    @Override
    public void remove(Long id) throws PlatformException {
        Role role = read(id);

        if (role == null) return;

        long countUsers = entityManager.createQuery("select count(u) from User u where u.role.id = :id" , Long.class).setParameter("id", id).getSingleResult();
        if (countUsers > 0) {
            throw new DeleteException("Error remove role : " + role.getRoleName() + " constrain by user");
        }

        super.remove(id);
    }
}

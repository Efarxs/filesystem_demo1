package exam.gcc.business.repository;

import exam.gcc.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {
    User findByUsername(String username);
    int createUser(User user);

    int updateUserVip(User user);
}

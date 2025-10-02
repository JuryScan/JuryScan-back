package unicap.juryscan.service.userComum;

import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.model.User;

public interface IUserComumService {

    User createUser(UserComumCreateDTO user);
}

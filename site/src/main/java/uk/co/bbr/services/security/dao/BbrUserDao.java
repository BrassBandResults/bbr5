package uk.co.bbr.services.security.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.co.bbr.services.framework.AbstractDao;

@Getter
@RequiredArgsConstructor
public class BbrUserDao extends AbstractDao {
    private final String name;
    private final UserRole role;
}

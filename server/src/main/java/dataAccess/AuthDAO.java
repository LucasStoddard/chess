package dataAccess;

import model.AuthData;

public interface AuthDAO {
    // authData and authToken are used interchangeably,
    // so I'll just stick to authData
    void addAuthData(AuthData authData);
    AuthData findAuthData(AuthData authData);
    void deleteAuthData(AuthData authData);
    void clear();
}

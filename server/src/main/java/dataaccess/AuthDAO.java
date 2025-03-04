package dataaccess;

import model.AuthData;

public interface AuthDAO {
    // authData and authToken are used interchangeably,
    // so I'll just stick to authData
    // NOTE: I may need to change these so that they have the Response and LoginResponse
    // datatypes, but I'm not sure.
        // Alternatively, you could use the model UserData object that you will also use when
        // you call your data access layer. Reusing these objects can create confusion with
        // what the method needs to operate, but it does simplify your architecture by
        // reducing the duplication of primary model objects.
    // OK I'm actually good hahaha.
    void addAuthData(AuthData authData);
//    void findAuthData(AuthData authData) throws DataAccessException;
    String checkAuthData(String authDataString) throws DataAccessException;
    void deleteAuthData(String authDataString) throws DataAccessException;
    void clear();
}

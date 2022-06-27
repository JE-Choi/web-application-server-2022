package webserver;

import db.DataBase;
import model.User;

import java.util.Map;

public class UserController {
    public Model createUser(final Map<String, String> stringStringMap) {
        DataBase.addUser(new User(stringStringMap));
        return new Model("/index.html",302);
    }
}
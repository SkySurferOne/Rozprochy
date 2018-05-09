package sr.middleware.Bank;

import Ice.Current;
import sr.middleware.slice.UserDoesNotExist;
import sr.middleware.slice._StandardUserDisp;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StandardUserImpl extends _StandardUserDisp {
    private final ConcurrentHashMap<UUID, UserEntity> userStorage;

    public StandardUserImpl(ConcurrentHashMap<UUID, UserEntity> userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public double checkAccountStatus(String id, Current __current) throws UserDoesNotExist {
        UserEntity userEntity = userStorage.get(UUID.fromString(id));

        if (userEntity == null) {
            throw new UserDoesNotExist();
        }

        return userEntity.getAccountState();
    }
}

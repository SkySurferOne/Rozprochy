package sr.middleware.Bank;

import Ice.Current;
import Ice.Identity;
import sr.middleware.slice.*;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class FactoryImpl extends _FactoryDisp {
    private static final Logger logger = Logger.getLogger(FactoryImpl.class.getName());

    private final Ice.ObjectAdapter adapter;
    private final ConcurrentHashMap<UUID, UserEntity> userStorage;
    private final Ice.Communicator communicator;
    private final String bankServerHost;
    private final String bankServerPort;
    private final long incomeThreshold = 2000;

    public FactoryImpl(ConcurrentHashMap<UUID, UserEntity> userStorage,
                       Ice.ObjectAdapter adapter, Ice.Communicator communicator,
                       String bankServerHost, String bankServerPort) {
        this.adapter = adapter;
        this.userStorage = userStorage;
        this.communicator = communicator;
        this.bankServerHost = bankServerHost;
        this.bankServerPort = bankServerPort;
    }

    @Override
    public String createAccount(UserInfo userInfo, Current __current) {
        UserEntity.UserType userType =  userInfo.monthlyIncome < incomeThreshold ? UserEntity.UserType.STANDARD : UserEntity.UserType.PREMIUM;
        UserEntity userEntity = new UserEntity(userInfo.firstname, userInfo.lastname,
                userInfo.peselNumber, userInfo.monthlyIncome, userType);
        UUID uuid = UUID.randomUUID();
        userStorage.put(uuid, userEntity);

        if (userType == UserEntity.UserType.STANDARD) {
            PremiumUserImpl premiumUser = new PremiumUserImpl();
            adapter.add(premiumUser, new Identity(userEntity.getPeselNumber(), userEntity.getUserType().toString()));
        } else {
            StandardUserImpl standardUser = new StandardUserImpl();
            adapter.add(standardUser, new Identity(userEntity.getPeselNumber(), userEntity.getUserType().toString()));
        }

        logger.info("Created user ("+uuid.toString()+"): "+userEntity.toString());

        return uuid.toString();
    }

    @Override
    public StandardUserPrx logInAsStandardUser(String id, Current __current) throws UserDoesNotExist {
        UserEntity userEntity = userStorage.get(UUID.fromString(id));

        if (userEntity == null) {
            throw new UserDoesNotExist();
        }

        String s1 = UserEntity.UserType.STANDARD.toString()+"/"+userEntity.getPeselNumber()+":" +
                "tcp -h "+bankServerHost+" -p "+bankServerPort+":udp -h "+bankServerHost+" -p "+bankServerPort;
        Ice.ObjectPrx base = communicator.stringToProxy(s1);
        logger.info("Standard user "+userEntity.getFirstname()+" "+userEntity.getLastname()+" logged in");

        return StandardUserPrxHelper.checkedCast(base);
    }

    @Override
    public PremiumUserPrx logInAsPremiumUser(String id, Current __current) throws PermissionViolation, UserDoesNotExist {
        UserEntity userEntity = userStorage.get(UUID.fromString(id));

        if (userEntity == null) {
            throw new UserDoesNotExist();
        }

        if (userEntity.getUserType() != UserEntity.UserType.PREMIUM) {
            throw new PermissionViolation();
        }

        String s1 = UserEntity.UserType.PREMIUM.toString()+"/"+userEntity.getPeselNumber()+":" +
                "tcp -h "+bankServerHost+" -p "+bankServerPort+":udp -h "+bankServerHost+" -p "+bankServerPort;
        Ice.ObjectPrx base = communicator.stringToProxy(s1);
        logger.info("Premium user "+userEntity.getFirstname()+" "+userEntity.getLastname()+" logged in");

        return PremiumUserPrxHelper.checkedCast(base);
    }
}

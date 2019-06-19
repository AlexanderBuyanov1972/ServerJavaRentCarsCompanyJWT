package cars.service.accounting;

import cars.dao.AuthRepository;
import cars.dto.Response;
import cars.entities.AccountMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Service
public class AccManagmentMongo implements IAccountsManagment {
    private static final String OK = "OK";
    private static final String USER_ALREADY_EXISTS = "user already exists";
    private static final String USER_IS_NOT_EXISTS = "user is not exists";
    private static final String ACCOUNT_IS_NOT_EXISTS = "account is not exists";
    private static final String PASSWORD_IS_WRONG = "password is wrong";
    private int goodCode = 200;
    private String currentDate = LocalDateTime.now().toString();

    @Autowired
    AuthRepository repository;
    @Autowired
    PasswordEncoder encoder;

    @Override
    public Response addAccount(String email, String password, String[] roles) {
        Response response = new Response().setCode(goodCode).setTimestamp(currentDate).setContent("");
        if (repository.existsById(email))
            return response.setMessage(USER_ALREADY_EXISTS);
        String encoderPassword = encoder.encode(password);
        Set<String> setRoles = new HashSet<>(Arrays.asList(roles));
        AccountMongo account = new AccountMongo(email, encoderPassword, setRoles);
        account.setDate(LocalDate.now());
        repository.save(account);
        return response.setMessage(OK);
    }

    @Override
    public Response removeAccount(String email) {
        Response response = new Response().setCode(goodCode).setTimestamp(currentDate).setContent("");
        AccountMongo account = repository.findById(email).orElse(null);
        System.out.println(account);
        if (account == null)
            return response.setMessage(USER_IS_NOT_EXISTS);
        repository.deleteById(email);
        return response.setMessage(OK);
    }

    @Override
    public Response updatePassword(String email, String password) {
        Response response = new Response().setCode(goodCode).setTimestamp(currentDate).setContent("");
        AccountMongo account = repository.findById(email).orElse(null);
        if (account == null)
            return response.setMessage(ACCOUNT_IS_NOT_EXISTS);
        if (encoder.matches(password, account.getPassword()))
            return response.setMessage(PASSWORD_IS_WRONG);
        account.setPassword(encoder.encode(password));
        account.setDate(LocalDate.now());
        repository.save(account);
        return response.setMessage(OK);

    }

    @Override
    public Response addRoles(String email, String[] roles) {
        Response response = new Response().setCode(goodCode).setTimestamp(currentDate).setContent("");
        AccountMongo account = repository.findById(email).orElse(null);
        if (account == null)
            return response.setMessage(ACCOUNT_IS_NOT_EXISTS);
        Set<String> setRolesAccount = account.getRoles();
        setRolesAccount.addAll(new HashSet<>(Arrays.asList(roles)));
        repository.save(account.setRoles(setRolesAccount));
        return response.setMessage(OK);
    }

    @Override
    public Response removeRoles(String email, String[] roles) {
        Response response = new Response().setCode(goodCode).setTimestamp(currentDate).setContent("");
        AccountMongo account = repository.findById(email).orElse(null);
        if (account == null)
            return response.setMessage(ACCOUNT_IS_NOT_EXISTS);
        Set<String> setRolesAccount = account.getRoles();
        setRolesAccount.removeAll(new HashSet<>(Arrays.asList(roles)));
        repository.save(account.setRoles(setRolesAccount));
        return response.setMessage(OK);
    }

}

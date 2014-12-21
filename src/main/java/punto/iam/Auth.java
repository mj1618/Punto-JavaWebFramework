package punto.iam;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Auth {


	public static boolean authenticate(Credential cred) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if(IamManager.store.user(cred.getUser()).isPresent())
            return PasswordHash.validatePassword(cred.getPass(), IamManager.store.user(cred.getUser()).get().getPasswordHash());
        else return false;
	}
}

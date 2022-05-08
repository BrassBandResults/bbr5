package uk.co.bbr.services.security.ex;

public class AuthenticationFailedException  extends Exception{
    public AuthenticationFailedException() {
        super("Username or password not recognised");
    }
}

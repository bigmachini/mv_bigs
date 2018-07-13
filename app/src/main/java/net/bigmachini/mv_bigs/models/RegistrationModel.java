package net.bigmachini.mv_bigs.models;

public class RegistrationModel {
    public String pin;
    public String phoneNumber;
    public int userId;
    public boolean verifyPin(String pin) {
        if (new String(this.pin).equals(new String(pin))) {
            return true;
        } else {
            return false;
        }
    }
}

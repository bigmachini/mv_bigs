package net.bigmachini.mv_bigs.models;

import net.bigmachini.mv_bigs.structures.RegistrationStructure;

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

    public RegistrationModel() {

    }

    public RegistrationModel(RegistrationStructure registrationStructure) {
        this.phoneNumber = registrationStructure.phoneNumber;
        this.pin = registrationStructure.pin;
        this.userId = registrationStructure.userId;
    }
}

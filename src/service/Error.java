package service;

public class Error {
    String error_name;
    String error_message;

    public Error(){}

    public Error(String error_name, String error_message) {
        this.error_name = error_name;
        this.error_message = error_message;
    }

    public String getError_name() {
        return error_name;
    }

    public void setError_name(String error_name) {
        this.error_name = error_name;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}

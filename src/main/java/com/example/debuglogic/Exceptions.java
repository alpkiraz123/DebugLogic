package com.example.debuglogic;

public class Exceptions {
    private String codeException;

    public String returnException() {
        switch (codeException) {
            case "ArithmeticException":
                System.out.println("Arithmetic Exception detected. Make sure:");
                System.out.println("1. Check for Overflow or Underflow.");
                System.out.println("2. For instance: 1 / 0 can give an exception.");
                System.out.println("3. Incorrect array indexing.");
                break;
            case "NullPointerException":
                System.out.println("Null Pointer Exception detected. Make sure:");
                System.out.println("1. Check for uninitialized variables.");
                System.out.println("2. Ensure that the object is not null before accessing its methods/fields.");
                break;
            // Add more cases for other exception types as needed.
            default:
                System.out.println("Unknown Exception: " + codeException);
                break;
        }
        return null;
    }

    public void setCodeException(String codeException) {
        this.codeException = codeException;
    }
}
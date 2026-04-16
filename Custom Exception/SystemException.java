/**
 * CONCEPT: CUSTOM EXCEPTIONS
 * Why: Improves debugging by allowing the developer to throw specific 
 * errors related to the business logic, making the stack trace easier to read.
 */

public class SystemException extends Exception {
    public SystemException(String message) {
        super(message);
    }
}
package Exceptions;

public class NonSquareMatrixException extends Exception {

    public NonSquareMatrixException() {
        super();
    }

    public NonSquareMatrixException(String message) {
        super(message);
    }

    public NonSquareMatrixException(String message, Throwable cause){
        super(message, cause);
    }

    public NonSquareMatrixException(Throwable cause){
        super(cause);
    }
}

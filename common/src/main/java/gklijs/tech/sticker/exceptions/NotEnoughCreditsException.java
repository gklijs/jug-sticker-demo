package gklijs.tech.sticker.exceptions;

public class NotEnoughCreditsException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "%d credits is not enough to order %d %s";

    NotEnoughCreditsException(String message) {
        super(message);
    }

    public NotEnoughCreditsException(int credits, int amount, String itemName) {
        this(String.format(MESSAGE_FORMAT, credits, amount, itemName));
    }
}

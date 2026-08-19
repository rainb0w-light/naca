package nacaLib.cics;

/** Text response emitted by EXEC CICS SEND TEXT. */
public record CicsTextOutput(String text, boolean erase, boolean freeKeyboard)
{
}

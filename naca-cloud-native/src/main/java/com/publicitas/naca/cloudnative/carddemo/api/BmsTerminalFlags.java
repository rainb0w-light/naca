package com.publicitas.naca.cloudnative.carddemo.api;

/** Terminal-control effects returned by SEND MAP. */
public record BmsTerminalFlags(boolean erase, boolean alarm, boolean freeKeyboard,
                               String cursorField)
{
}

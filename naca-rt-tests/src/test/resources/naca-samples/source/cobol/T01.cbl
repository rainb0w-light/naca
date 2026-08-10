IDENTIFICATION DIVISION.
       PROGRAM-ID. T01.
       AUTHOR. NACA-TEST.
      *
      * Test program for data type conversion verification
      * Tests: PIC X, PIC 9, PIC S9, COMP-3, COMP, decimals
      *
       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SOURCE-COMPUTER. IBM-3081.
       OBJECT-COMPUTER. IBM-3081.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
      *
      * Group 1: Alphanumeric (PIC X)
       01 WS-ALPHA-GROUP.
          05 WS-CHAR-1         PIC X(10) VALUE 'ABCDEFGHIJ'.
          05 WS-CHAR-2         PIC X(5)  VALUE 'HELLO'.
          05 WS-CHAR-3         PIC X     VALUE 'X'.
      *
      * Group 2: Unsigned Numeric Display (PIC 9)
       01 WS-NUM-UNSIGNED.
          05 WS-NUM-1          PIC 9(5)  VALUE 12345.
          05 WS-NUM-2          PIC 9(3)  VALUE 789.
          05 WS-NUM-3          PIC 9     VALUE 5.
      *
      * Group 3: Signed Numeric Display (PIC S9)
       01 WS-NUM-SIGNED.
          05 WS-SNUM-1         PIC S9(5) VALUE -12345.
          05 WS-SNUM-2         PIC S9(3) VALUE +789.
          05 WS-SNUM-3         PIC S9    VALUE -5.
      *
      * Group 4: Packed Decimal (COMP-3)
       01 WS-COMP3-GROUP.
          05 WS-COMP3-1        PIC S9(5) COMP-3 VALUE -12345.
          05 WS-COMP3-2        PIC 9(7) COMP-3 VALUE 1234567.
          05 WS-COMP3-3        PIC S9(3)V99 COMP-3 VALUE -123.45.
      *
      * Group 5: Binary (COMP)
       01 WS-COMP-GROUP.
          05 WS-COMP-1         PIC S9(4) COMP VALUE -1234.
          05 WS-COMP-2         PIC 9(9) COMP VALUE 123456789.
      *
      * Group 6: Decimal (V and explicit decimal)
       01 WS-DECIMAL-GROUP.
          05 WS-DEC-1          PIC 9(3)V9(2) VALUE 123.45.
          05 WS-DEC-2          PIC S9(5)V99 VALUE -12345.67.
          05 WS-DEC-3          PIC 9(4).99 VALUE 1234.56.
      *
      * Group 7: REDEFINES test
       01 WS-REDEFINES-BASE.
          05 WS-RAW-DATA       PIC X(10) VALUE '1234567890'.
       01 WS-REDEFINES-ALT REDEFINES WS-REDEFINES-BASE.
          05 WS-ALT-NUM        PIC 9(10).
      *
       PROCEDURE DIVISION.
       MAIN-PARA.
           DISPLAY '=== T01: Data Type Conversion Test ==='
           DISPLAY ' '
      *
      * Test Group 1: Alphanumeric
           DISPLAY 'Group 1: Alphanumeric (PIC X)'
           DISPLAY 'WS-CHAR-1: ' WS-CHAR-1
           DISPLAY 'WS-CHAR-2: ' WS-CHAR-2
           DISPLAY 'WS-CHAR-3: ' WS-CHAR-3
           DISPLAY ' '
      *
      * Test Group 2: Unsigned Numeric
           DISPLAY 'Group 2: Unsigned Numeric (PIC 9)'
           DISPLAY 'WS-NUM-1: ' WS-NUM-1
           DISPLAY 'WS-NUM-2: ' WS-NUM-2
           DISPLAY 'WS-NUM-3: ' WS-NUM-3
           DISPLAY ' '
      *
      * Test Group 3: Signed Numeric
           DISPLAY 'Group 3: Signed Numeric (PIC S9)'
           DISPLAY 'WS-SNUM-1: ' WS-SNUM-1
           DISPLAY 'WS-SNUM-2: ' WS-SNUM-2
           DISPLAY 'WS-SNUM-3: ' WS-SNUM-3
           DISPLAY ' '
      *
      * Test Group 4: COMP-3
           DISPLAY 'Group 4: Packed Decimal (COMP-3)'
           DISPLAY 'WS-COMP3-1: ' WS-COMP3-1
           DISPLAY 'WS-COMP3-2: ' WS-COMP3-2
           DISPLAY 'WS-COMP3-3: ' WS-COMP3-3
           DISPLAY ' '
      *
      * Test Group 5: Binary (COMP)
           DISPLAY 'Group 5: Binary (COMP)'
           DISPLAY 'WS-COMP-1: ' WS-COMP-1
           DISPLAY 'WS-COMP-2: ' WS-COMP-2
           DISPLAY ' '
      *
      * Test Group 6: Decimal
           DISPLAY 'Group 6: Decimal (V and explicit)'
           DISPLAY 'WS-DEC-1: ' WS-DEC-1
           DISPLAY 'WS-DEC-2: ' WS-DEC-2
           DISPLAY 'WS-DEC-3: ' WS-DEC-3
           DISPLAY ' '
      *
      * Test Group 7: REDEFINES
           DISPLAY 'Group 7: REDEFINES'
           DISPLAY 'WS-RAW-DATA: ' WS-RAW-DATA
           DISPLAY 'WS-ALT-NUM: ' WS-ALT-NUM
           DISPLAY ' '
      *
           DISPLAY '=== T01: Test Complete ==='
           STOP RUN.
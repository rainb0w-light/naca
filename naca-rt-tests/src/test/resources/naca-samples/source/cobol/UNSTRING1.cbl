       IDENTIFICATION DIVISION.
       PROGRAM-ID. UNSTRING1.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-STR          PIC X(20) VALUE 'one,two,three'.
       01 WS-P1           PIC X(10).
       01 WS-P2           PIC X(10).
       01 WS-P3           PIC X(10).
       01 WS-TALLY        PIC 9(3) VALUE 0.
       PROCEDURE DIVISION.
       MAIN-PARA.
           UNSTRING WS-STR DELIMITED BY ','
               INTO WS-P1, WS-P2, WS-P3.
           DISPLAY 'P1=[' WS-P1 ']'.
           DISPLAY 'P2=[' WS-P2 ']'.
           DISPLAY 'P3=[' WS-P3 ']'.
           MOVE 0 TO WS-TALLY.
           UNSTRING WS-STR DELIMITED BY ','
               INTO WS-P1, WS-P2, WS-P3
               TALLYING IN WS-TALLY.
           DISPLAY 'TALLY=' WS-TALLY.
           STOP RUN.

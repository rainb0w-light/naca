       IDENTIFICATION DIVISION.
       PROGRAM-ID. VERBS.
       AUTHOR. NACA-TEST.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-A            PIC 9(5) VALUE 1000.
       01 WS-B            PIC 9(5) VALUE 250.
       01 WS-RESULT       PIC 9(7) VALUE 0.
       01 WS-SIGNED       PIC S9(5) VALUE -500.
       01 WS-DEC          PIC 9(5)V99 VALUE 123.45.
       01 WS-STR          PIC X(20) VALUE SPACES.
       01 WS-GRP.
          05 WS-G1        PIC X(5) VALUE 'AAAAA'.
          05 WS-G2        PIC 9(3) VALUE 77.
       01 WS-IDX          PIC 9(3) VALUE 0.
       01 WS-TOTAL        PIC 9(7) VALUE 0.
       01 WS-DATE         PIC X(8).
       01 WS-COUNTER      PIC 9(3) VALUE 0.
       PROCEDURE DIVISION.
       MAIN-PARA.
           DISPLAY '=== VERBS TEST ==='.
      * ADD / SUBTRACT / MULTIPLY / DIVIDE / COMPUTE
           ADD WS-B TO WS-A.
           DISPLAY 'ADD: ' WS-A.
           SUBTRACT WS-B FROM WS-A.
           DISPLAY 'SUB: ' WS-A.
           MULTIPLY WS-B BY 3 GIVING WS-RESULT.
           DISPLAY 'MUL: ' WS-RESULT.
           DIVIDE WS-B INTO WS-A GIVING WS-RESULT.
           DISPLAY 'DIV: ' WS-RESULT.
           COMPUTE WS-RESULT = WS-A + WS-B * 2.
           DISPLAY 'COMP: ' WS-RESULT.
           ADD 1 TO WS-A ROUNDED.
           DISPLAY 'ADDR: ' WS-A.
      * SET
           SET WS-IDX TO 42.
           DISPLAY 'SET: ' WS-IDX.
      * INITIALIZE
           INITIALIZE WS-GRP.
           DISPLAY 'INIT: ' WS-G1 ':' WS-G2.
      * STRING
           STRING 'HELLO' '-' 'WORLD' DELIMITED BY SIZE INTO WS-STR.
           DISPLAY 'STR: ' WS-STR.
      * PERFORM TIMES (repetitions)
           MOVE 0 TO WS-TOTAL.
           PERFORM 3 TIMES
               ADD 10 TO WS-TOTAL
           END-PERFORM.
           DISPLAY 'PTIMES: ' WS-TOTAL.
      * PERFORM VARYING
           MOVE 0 TO WS-TOTAL.
           PERFORM VARYING WS-IDX FROM 1 BY 1 UNTIL WS-IDX > 4
               ADD WS-IDX TO WS-TOTAL
           END-PERFORM.
           DISPLAY 'PVARY: ' WS-TOTAL.
      * GO TO
           MOVE 5 TO WS-COUNTER.
           IF WS-COUNTER > 3
               GO TO BIG-PARA
           ELSE
               GO TO SMALL-PARA
           END-IF.
       BIG-PARA.
           DISPLAY 'GO: BIG'.
           GO TO DONE-PARA.
       SMALL-PARA.
           DISPLAY 'GO: SMALL'.
       DONE-PARA.
      * ACCEPT from date
           ACCEPT WS-DATE FROM DATE.
           DISPLAY 'ACCEPT-DATE-LEN: '.
      * EVALUATE
           EVALUATE WS-COUNTER
               WHEN 5
                   DISPLAY 'EVAL: FIVE'
               WHEN OTHER
                   DISPLAY 'EVAL: OTHER'
           END-EVALUATE.
           DISPLAY '=== DONE ==='.
           STOP RUN.

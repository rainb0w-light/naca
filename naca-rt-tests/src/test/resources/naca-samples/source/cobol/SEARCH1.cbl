       IDENTIFICATION DIVISION.
       PROGRAM-ID. SEARCH1.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-TBL.
          05 WS-ELEM OCCURS 5 TIMES PIC 9(3).
       01 WS-IDX          PIC 9(3) VALUE 0.
       01 WS-FOUND-FLAG   PIC 9(1) VALUE 0.
       PROCEDURE DIVISION.
       MAIN-PARA.
           MOVE 10 TO WS-ELEM(1).
           MOVE 20 TO WS-ELEM(2).
           MOVE 30 TO WS-ELEM(3).
           MOVE 40 TO WS-ELEM(4).
           MOVE 50 TO WS-ELEM(5).
           MOVE 1 TO WS-IDX.
           SEARCH WS-ELEM
               AT END
                   DISPLAY 'NOT-FOUND'
               WHEN WS-ELEM(WS-IDX) = 30
                   DISPLAY 'FOUND-AT ' WS-IDX
           END-SEARCH.
           STOP RUN.

       ID DIVISION.
       PROGRAM-ID. BMSJSON1.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
           COPY BMSJSM1.
           EXEC SQL INCLUDE BMSJSM1S END-EXEC.
           COPY DFHAID SUPPRESS.

       PROCEDURE DIVISION.
           EXEC CICS RECEIVE
                MAP('BMSJS0F')
                MAPSET('BMSJSM1')
                INTO(BMSJS0F)
           END-EXEC

           IF REQUESTI = 'PING'
              MOVE 'PONG' TO RESULTI SRESULTI
           ELSE
              MOVE 'UNKNOWN' TO RESULTI SRESULTI
           END-IF

           EXEC CICS SEND
                MAP('BMSJS0F')
                MAPSET('BMSJSM1')
                FROM(BMSJS0FS)
                FREEKB
                CURSOR
                ERASE
           END-EXEC

           EXEC CICS RETURN END-EXEC
           GOBACK.

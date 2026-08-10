       IDENTIFICATION DIVISION.
       PROGRAM-ID. INSPECT1.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-STR          PIC X(12) VALUE 'aBcAbCaBcX'.
       01 WS-STR2         PIC X(10) VALUE 'hello'.
       PROCEDURE DIVISION.
       MAIN-PARA.
           DISPLAY 'BEFORE: ' WS-STR.
           INSPECT WS-STR CONVERTING 'abc' TO 'xyz'.
           DISPLAY 'CONV1: ' WS-STR.
           INSPECT WS-STR2 CONVERTING 'aeiou' TO 'AEIOU'.
           DISPLAY 'CONV2: ' WS-STR2.
           STOP RUN.

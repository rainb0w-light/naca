       IDENTIFICATION DIVISION.
       PROGRAM-ID. REPLACE1.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-STR          PIC X(12) VALUE 'abcabcabc'.
       01 WS-STR2         PIC X(12) VALUE 'abcabcabc'.
       01 WS-STR3         PIC X(12) VALUE 'abcabcabc'.
       01 WS-STR4         PIC X(12) VALUE 'a b c d e'.
       PROCEDURE DIVISION.
       MAIN-PARA.
           INSPECT WS-STR REPLACING ALL 'a' BY 'X'.
           DISPLAY 'ALL: ' WS-STR.
           INSPECT WS-STR2 REPLACING FIRST 'a' BY 'X'.
           DISPLAY 'FIRST: ' WS-STR2.
           INSPECT WS-STR3 REPLACING LEADING 'a' BY 'X'.
           DISPLAY 'LEAD: ' WS-STR3.
           INSPECT WS-STR4 REPLACING ALL SPACES BY '_'.
           DISPLAY 'SPACES: ' WS-STR4.
           STOP RUN.

      *> ============================================================
      *> TEST-A.cbl - Self-contained version (all COPYs inlined)
      *> Alphabet to Numeric MOVE (Matrix 1.3 A1, 16 scenarios)
      *> ============================================================
       IDENTIFICATION DIVISION.
       PROGRAM-ID. TEST-A.
       DATA DIVISION.
       WORKING-STORAGE SECTION.

      *> === INLINED: WS-TEST-COMMON ===
       01 WS-SCENE-ID      PIC X(10).
       01 WS-STATUS        PIC X(3).
       01 WS-NOTE          PIC X(20).
       01 WS-ROLE          PIC X(5).
       01 WS-FIELD-NAME    PIC X(30).

       01 WS-DUMP-SRC      PIC X(20).
       01 WS-DUMP-LEN      PIC 99.
       01 WS-DUMP-HEX      PIC X(40).
       01 WS-DUMP-DISP     PIC X(20).
       01 WS-DUMP-IDX      PIC 99.
       01 WS-DUMP-BYTE     PIC 999.
       01 WS-DUMP-HI       PIC 99.
       01 WS-DUMP-LO       PIC 99.
       01 WS-BEFORE-HEX    PIC X(40).
       01 WS-AFTER-HEX     PIC X(40).
       01 WS-BEFORE-DISP   PIC X(20).
       01 WS-AFTER-DISP    PIC X(20).
       01 WS-HEX-CHARS     PIC X(16) VALUE "0123456789ABCDEF".
      *> === END WS-TEST-COMMON ===

      *> --- Helper variables ---
       01 WS-SRC-NAME    PIC X(30).
       01 WS-TGT-NAME    PIC X(30).
       01 WS-LEN         PIC 99.

      *> --- Alphabet source variables ---
       01 A5S  PIC X(5).
       01 A10S PIC X(10).
       01 A1S  PIC X(1).
       01 A4S  PIC X(4).
       01 A3S  PIC X(3).
       01 A2S  PIC X(2).

      *> --- Alphabet target variables ---
       01 A5T  PIC X(5).
       01 A4T  PIC X(4).
       01 A3T  PIC X(3).
       01 A2T  PIC X(2).

      *> --- DISPLAY unsigned target ---
       01 DU5T PIC 9(5).

      *> --- DISPLAY signed target ---
       01 DS5T PIC S9(5).

      *> --- COMP target ---
       01 CS2BT. 05 CS2BT-V PIC S9(4) COMP.
       01 CS2BT-X REDEFINES CS2BT PIC X(2).

      *> --- COMP-3 target ---
       01 PS5T.  05 PS5T-V  PIC S9(5) COMP-3.
       01 PS5T-X  REDEFINES PS5T  PIC X(3).

      *> --- COMP-5 target ---
       01 NS4BT. 05 NS4BT-V PIC S9(9) COMP-5.
       01 NS4BT-X REDEFINES NS4BT PIC X(4).
       01 NS2BT. 05 NS2BT-V PIC S9(4) COMP-5.
       01 NS2BT-X REDEFINES NS2BT PIC X(2).

      *> --- DISPLAY source variables (for reverse MOVE) ---
       01 DU5S PIC 9(5).
       01 DS5S PIC S9(5).

      *> --- COMP source variables ---
       01 CS2BS. 05 CS2BS-V PIC S9(4) COMP.
       01 CS2BS-X REDEFINES CS2BS PIC X(2).

      *> --- COMP-3 source variables ---
       01 PS5S.  05 PS5S-V  PIC S9(5) COMP-3.
       01 PS5S-X  REDEFINES PS5S  PIC X(3).

      *> --- COMP-5 source variables ---
       01 NS2BS. 05 NS2BS-V PIC S9(4) COMP-5.
       01 NS2BS-X REDEFINES NS2BS PIC X(2).

       PROCEDURE DIVISION.
       MAIN.
           DISPLAY '=== TEST-A: Alphabet to Numeric MOVE ==='

      *> ---- 1.3 Alphabet to DISPLAY unsigned ----
           PERFORM V-A1-001. PERFORM V-A1-002.
           PERFORM V-A1-003.

      *> ---- 1.3 Alphabet to DISPLAY signed ----
           PERFORM V-A1-004. PERFORM V-A1-005.
           PERFORM V-A1-006.

      *> ---- 1.3 Alphabet to COMP ----
           PERFORM V-A1-007.

      *> ---- 1.3 Alphabet to COMP-3 ----
           PERFORM V-A1-008.

      *> ---- 1.3 Alphabet to COMP-5 ----
           PERFORM V-A1-009.

      *> ---- 1.3 Numeric to Alphabet ----
           PERFORM V-A1-010. PERFORM V-A1-011.
           PERFORM V-A1-012. PERFORM V-A1-013.
           PERFORM V-A1-014.

      *> ---- 1.3 Length mismatch ----
           PERFORM V-A1-015. PERFORM V-A1-016.

           DISPLAY '=== DONE ==='.
           STOP RUN.

      *> ==========================================================
      *> Helper paragraphs
      *> ==========================================================
       R-HEX.
           MOVE WS-LEN TO WS-DUMP-LEN.
           PERFORM HEX-BYTES.

       T-B.
           PERFORM R-HEX.
           MOVE WS-DUMP-HEX  TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
       T-A.
           PERFORM R-HEX.
           MOVE WS-DUMP-HEX  TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'TGT' TO WS-ROLE.
           MOVE WS-TGT-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.

      *> ==========================================================
      *> 1.3 Alphabet to DISPLAY unsigned
      *> ==========================================================
       V-A1-001.
           MOVE 'A1-001' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A-DU5-DIGIT' TO WS-NOTE.
           MOVE 'A5S' TO WS-SRC-NAME MOVE 'DU5T' TO WS-TGT-NAME.
           MOVE SPACES TO A5S. MOVE 0 TO DU5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE '12345' TO A5S. MOVE A5S TO DU5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

       V-A1-002.
           MOVE 'A1-002' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A-DU5-ALPHA' TO WS-NOTE.
           MOVE 'A5S' TO WS-SRC-NAME MOVE 'DU5T' TO WS-TGT-NAME.
           MOVE SPACES TO A5S. MOVE 0 TO DU5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE 'ABCDE' TO A5S. MOVE A5S TO DU5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

       V-A1-003.
           MOVE 'A1-003' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A-DU5-SPACE' TO WS-NOTE.
           MOVE 'A5S' TO WS-SRC-NAME MOVE 'DU5T' TO WS-TGT-NAME.
           MOVE SPACES TO A5S. MOVE 0 TO DU5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE '12 45' TO A5S. MOVE A5S TO DU5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

      *> ==========================================================
      *> 1.3 Alphabet to DISPLAY signed
      *> ==========================================================
       V-A1-004.
           MOVE 'A1-004' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A-DS5-NEG' TO WS-NOTE.
           MOVE 'A5S' TO WS-SRC-NAME MOVE 'DS5T' TO WS-TGT-NAME.
           MOVE SPACES TO A5S. MOVE 0 TO DS5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE DS5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE '-1234' TO A5S. MOVE A5S TO DS5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE DS5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

       V-A1-005.
           MOVE 'A1-005' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A-DS5-POS' TO WS-NOTE.
           MOVE 'A5S' TO WS-SRC-NAME MOVE 'DS5T' TO WS-TGT-NAME.
           MOVE SPACES TO A5S. MOVE 0 TO DS5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE DS5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE '+1234' TO A5S. MOVE A5S TO DS5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE DS5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

       V-A1-006.
           MOVE 'A1-006' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A-DS5-DIGIT' TO WS-NOTE.
           MOVE 'A5S' TO WS-SRC-NAME MOVE 'DS5T' TO WS-TGT-NAME.
           MOVE SPACES TO A5S. MOVE 0 TO DS5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE DS5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE '12345' TO A5S. MOVE A5S TO DS5T.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE DS5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

      *> ==========================================================
      *> 1.3 Alphabet to COMP
      *> ==========================================================
       V-A1-007.
           MOVE 'A1-007' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A-CS2B-DIGIT' TO WS-NOTE.
           MOVE 'A5S' TO WS-SRC-NAME MOVE 'CS2BT-V' TO WS-TGT-NAME.
           MOVE SPACES TO A5S. MOVE 0 TO CS2BT-V.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE CS2BT-X TO WS-DUMP-SRC MOVE 2 TO WS-LEN PERFORM T-B.
           MOVE '12345' TO A5S. MOVE A5S TO CS2BT-V.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE CS2BT-X TO WS-DUMP-SRC MOVE 2 TO WS-LEN PERFORM T-A.

      *> ==========================================================
      *> 1.3 Alphabet to COMP-3
      *> ==========================================================
       V-A1-008.
           MOVE 'A1-008' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A-PS5-DIGIT' TO WS-NOTE.
           MOVE 'A5S' TO WS-SRC-NAME MOVE 'PS5T-V' TO WS-TGT-NAME.
           MOVE SPACES TO A5S. MOVE 0 TO PS5T-V.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE PS5T-X TO WS-DUMP-SRC MOVE 3 TO WS-LEN PERFORM T-B.
           MOVE '12345' TO A5S. MOVE A5S TO PS5T-V.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE PS5T-X TO WS-DUMP-SRC MOVE 3 TO WS-LEN PERFORM T-A.

      *> ==========================================================
      *> 1.3 Alphabet to COMP-5
      *> ==========================================================
       V-A1-009.
           MOVE 'A1-009' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A-NS4B-DIGIT' TO WS-NOTE.
           MOVE 'A5S' TO WS-SRC-NAME MOVE 'NS4BT-V' TO WS-TGT-NAME.
           MOVE SPACES TO A5S. MOVE 0 TO NS4BT-V.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE NS4BT-X TO WS-DUMP-SRC MOVE 4 TO WS-LEN PERFORM T-B.
           MOVE '12345' TO A5S. MOVE A5S TO NS4BT-V.
           MOVE A5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE NS4BT-X TO WS-DUMP-SRC MOVE 4 TO WS-LEN PERFORM T-A.

      *> ==========================================================
      *> 1.3 Numeric to Alphabet
      *> ==========================================================
       V-A1-010.
           MOVE 'A1-010' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'DU5-A' TO WS-NOTE.
           MOVE 'DU5S' TO WS-SRC-NAME MOVE 'A5T' TO WS-TGT-NAME.
           MOVE 0 TO DU5S. MOVE SPACES TO A5T.
           MOVE DU5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE A5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE 12345 TO DU5S. MOVE DU5S TO A5T.
           MOVE DU5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE A5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

       V-A1-011.
           MOVE 'A1-011' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'DS5-A' TO WS-NOTE.
           MOVE 'DS5S' TO WS-SRC-NAME MOVE 'A5T' TO WS-TGT-NAME.
           MOVE 0 TO DS5S. MOVE SPACES TO A5T.
           MOVE DS5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE A5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE -12345 TO DS5S. MOVE DS5S TO A5T.
           MOVE DS5S TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE A5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

       V-A1-012.
           MOVE 'A1-012' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'CS2B-A4' TO WS-NOTE.
           MOVE 'CS2BS-V' TO WS-SRC-NAME MOVE 'A4T' TO WS-TGT-NAME.
           MOVE 0 TO CS2BS-V. MOVE SPACES TO A4T.
           MOVE CS2BS-X TO WS-DUMP-SRC MOVE 2 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE A4T TO WS-DUMP-SRC MOVE 4 TO WS-LEN PERFORM T-B.
           MOVE -1234 TO CS2BS-V. MOVE CS2BS-V TO A4T.
           MOVE CS2BS-X TO WS-DUMP-SRC MOVE 2 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE A4T TO WS-DUMP-SRC MOVE 4 TO WS-LEN PERFORM T-A.

       V-A1-013.
           MOVE 'A1-013' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'PS5-A3' TO WS-NOTE.
           MOVE 'PS5S-V' TO WS-SRC-NAME MOVE 'A3T' TO WS-TGT-NAME.
           MOVE 0 TO PS5S-V. MOVE SPACES TO A3T.
           MOVE PS5S-X TO WS-DUMP-SRC MOVE 3 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE A3T TO WS-DUMP-SRC MOVE 3 TO WS-LEN PERFORM T-B.
           MOVE -12345 TO PS5S-V. MOVE PS5S-V TO A3T.
           MOVE PS5S-X TO WS-DUMP-SRC MOVE 3 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE A3T TO WS-DUMP-SRC MOVE 3 TO WS-LEN PERFORM T-A.

       V-A1-014.
           MOVE 'A1-014' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'NS2B-A2' TO WS-NOTE.
           MOVE 'NS2BS-V' TO WS-SRC-NAME MOVE 'A2T' TO WS-TGT-NAME.
           MOVE 0 TO NS2BS-V. MOVE SPACES TO A2T.
           MOVE NS2BS-X TO WS-DUMP-SRC MOVE 2 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE A2T TO WS-DUMP-SRC MOVE 2 TO WS-LEN PERFORM T-B.
           MOVE -1 TO NS2BS-V. MOVE NS2BS-V TO A2T.
           MOVE NS2BS-X TO WS-DUMP-SRC MOVE 2 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE A2T TO WS-DUMP-SRC MOVE 2 TO WS-LEN PERFORM T-A.

      *> ==========================================================
      *> 1.3 Length mismatch
      *> ==========================================================
       V-A1-015.
           MOVE 'A1-015' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A1-DU5' TO WS-NOTE.
           MOVE 'A1S' TO WS-SRC-NAME MOVE 'DU5T' TO WS-TGT-NAME.
           MOVE ' ' TO A1S. MOVE 0 TO DU5T.
           MOVE A1S TO WS-DUMP-SRC MOVE 1 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE '5' TO A1S. MOVE A1S TO DU5T.
           MOVE A1S TO WS-DUMP-SRC MOVE 1 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

       V-A1-016.
           MOVE 'A1-016' TO WS-SCENE-ID MOVE 'OK' TO WS-STATUS
           MOVE 'A10-DU5' TO WS-NOTE.
           MOVE 'A10S' TO WS-SRC-NAME MOVE 'DU5T' TO WS-TGT-NAME.
           MOVE SPACES TO A10S. MOVE 0 TO DU5T.
           MOVE A10S TO WS-DUMP-SRC MOVE 10 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-BEFORE-HEX.
           MOVE WS-DUMP-DISP TO WS-BEFORE-DISP.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-B.
           MOVE '0000012345' TO A10S. MOVE A10S TO DU5T.
           MOVE A10S TO WS-DUMP-SRC MOVE 10 TO WS-LEN PERFORM R-HEX.
           MOVE WS-DUMP-HEX TO WS-AFTER-HEX.
           MOVE WS-DUMP-DISP TO WS-AFTER-DISP.
           MOVE 'SRC' TO WS-ROLE. MOVE WS-SRC-NAME TO WS-FIELD-NAME.
           PERFORM OUTPUT-LINE.
           MOVE DU5T TO WS-DUMP-SRC MOVE 5 TO WS-LEN PERFORM T-A.

      *> === INLINED: WS-HEX-UTIL ===
       HEX-BYTES.
           MOVE SPACES TO WS-DUMP-HEX
                          WS-DUMP-DISP.
           MOVE 1 TO WS-DUMP-IDX.
           PERFORM VARYING WS-DUMP-IDX FROM 1 BY 1
                   UNTIL WS-DUMP-IDX > WS-DUMP-LEN
               COMPUTE WS-DUMP-BYTE =
                   FUNCTION ORD(WS-DUMP-SRC(WS-DUMP-IDX:1))
               DIVIDE WS-DUMP-BYTE BY 16
                   GIVING WS-DUMP-HI
                   REMAINDER WS-DUMP-LO
               ADD 1 TO WS-DUMP-HI
               ADD 1 TO WS-DUMP-LO
               STRING WS-DUMP-HEX(1:(WS-DUMP-IDX - 1) * 2)
                   WS-HEX-CHARS(WS-DUMP-HI:1)
                   WS-HEX-CHARS(WS-DUMP-LO:1)
                   INTO WS-DUMP-HEX
               END-STRING
               EVALUATE TRUE
                   WHEN WS-DUMP-BYTE = 0
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1) '_'
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE = 32
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1) '.'
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE >= 48 AND WS-DUMP-BYTE <= 57
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1)
                           WS-DUMP-SRC(WS-DUMP-IDX:1)
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE >= 65 AND WS-DUMP-BYTE <= 90
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1)
                           WS-DUMP-SRC(WS-DUMP-IDX:1)
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE >= 97 AND WS-DUMP-BYTE <= 122
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1)
                           WS-DUMP-SRC(WS-DUMP-IDX:1)
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE = 123
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1) '{'
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE = 125
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1) '}'
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE = 255
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1) '#'
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE = 43
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1) '+'
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE = 45
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1) '-'
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN WS-DUMP-BYTE = 46
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1) '.'
                           INTO WS-DUMP-DISP
                       END-STRING
                   WHEN OTHER
                       STRING WS-DUMP-DISP(1:WS-DUMP-IDX - 1) '?'
                           INTO WS-DUMP-DISP
                       END-STRING
               END-EVALUATE
           END-PERFORM.
      *> === END WS-HEX-UTIL ===

      *> === INLINED: WS-TEST-OUTPUT ===
       OUTPUT-LINE.
           DISPLAY WS-SCENE-ID '|' WS-ROLE '|' WS-FIELD-NAME
               '|' WS-BEFORE-HEX '|' WS-AFTER-HEX '|'
               WS-BEFORE-DISP '|' WS-AFTER-DISP '|'
               WS-STATUS '|' WS-NOTE.
      *> === END WS-TEST-OUTPUT ===

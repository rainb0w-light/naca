package com.publicitas.naca;

import org.openrewrite.Column;
import org.openrewrite.DataTable;
import org.openrewrite.Recipe;

/** Machine-readable audit rows emitted by {@link RemoveStringHungarianPrefix}. */
public final class HungarianNotationOutcomes extends DataTable<HungarianNotationOutcomes.Row> {

    /**
     * Creates the recipe-owned outcomes table.
     *
     * @param recipe owning recipe
     */
    public HungarianNotationOutcomes(Recipe recipe) {
        super(recipe, "Hungarian notation outcomes",
            "Every cs-prefixed declaration considered by the recipe and its outcome.");
    }

    /** One declaration-level recipe decision. */
    public static final class Row {
        @Column(displayName = "Source path", description = "Path of the Java source file.")
        private final String sourcePath;
        @Column(displayName = "Line", description = "Best-effort declaration line.")
        private final int line;
        @Column(displayName = "Declaration kind", description = "field, parameter, or local.")
        private final String declarationKind;
        @Column(displayName = "Old name", description = "Identifier before the recipe.")
        private final String oldName;
        @Column(displayName = "New name", description = "Proposed identifier after the recipe.")
        private final String newName;
        @Column(displayName = "Outcome", description = "renamed or skipped.")
        private final String outcome;
        @Column(displayName = "Reason", description = "Skip reason, empty for renamed declarations.")
        private final String reason;

        /**
         * Creates one declaration-level decision.
         *
         * @param sourcePath source path
         * @param line source line, or -1 when no range marker is available
         * @param declarationKind field, parameter, or local
         * @param oldName original name
         * @param newName proposed name
         * @param outcome renamed or skipped
         * @param reason skip reason, empty when renamed
         */
        public Row(String sourcePath, int line, String declarationKind, String oldName,
                String newName, String outcome, String reason) {
            this.sourcePath = sourcePath;
            this.line = line;
            this.declarationKind = declarationKind;
            this.oldName = oldName;
            this.newName = newName;
            this.outcome = outcome;
            this.reason = reason;
        }

        public String getSourcePath() {
            return sourcePath;
        }

        public int getLine() {
            return line;
        }

        public String getDeclarationKind() {
            return declarationKind;
        }

        public String getOldName() {
            return oldName;
        }

        public String getNewName() {
            return newName;
        }

        public String getOutcome() {
            return outcome;
        }

        public String getReason() {
            return reason;
        }
    }
}

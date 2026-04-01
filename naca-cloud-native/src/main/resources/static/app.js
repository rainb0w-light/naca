// Naca Cloud Native - Frontend JavaScript

let currentCobolSource = '';
let currentProgramName = '';
let lastTranspilationSuccess = false;

// Tab functionality
document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', function() {
        const tabName = this.dataset.tab;
        const parent = this.closest('.panel');

        // Update active tab
        parent.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        this.classList.add('active');

        // Update active content
        parent.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        document.getElementById(tabName + '-tab').classList.add('active');
    });
});

// File upload handling
document.getElementById('fileInput').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        document.getElementById('fileName').textContent = 'Selected: ' + file.name;

        // Extract program name from filename
        const programName = file.name.replace(/\.(cbl|cob)$/i, '');
        document.getElementById('programName').value = programName;

        // Read file content
        const reader = new FileReader();
        reader.onload = function(e) {
            currentCobolSource = e.target.result;
            document.getElementById('cobolSource').value = currentCobolSource;
            // Switch to paste tab to show content
            document.querySelector('[data-tab="paste"]').click();
        };
        reader.readAsText(file);
    }
});

// Load available samples
async function loadSamples() {
    try {
        const response = await fetch('/api/samples');
        const samples = await response.json();

        const select = document.getElementById('sampleSelect');
        select.innerHTML = '<option value="">Select a program...</option>';
        samples.forEach(sample => {
            const option = document.createElement('option');
            option.value = sample;
            option.textContent = sample;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Failed to load samples:', error);
        const select = document.getElementById('sampleSelect');
        select.innerHTML = '<option value="BATCH1">BATCH1</option>' +
                          '<option value="CALLMSG">CALLMSG</option>' +
                          '<option value="ONLINE1">ONLINE1</option>';
    }
}

// Load sample content
async function loadSample() {
    const sampleName = document.getElementById('sampleSelect').value;
    if (!sampleName) {
        alert('Please select a sample program');
        return;
    }

    // Set program name
    document.getElementById('programName').value = sampleName;

    // For now, show a message - in production, you would fetch the actual sample
    currentCobolSource = `* Sample program: ${sampleName}
* Load the actual COBOL source file to transpile and run.`;
    document.getElementById('cobolSource').value = currentCobolSource;
    document.querySelector('[data-tab="paste"]').click();
}

// Transpile COBOL to Java
async function transpile() {
    const cobolSource = document.getElementById('cobolSource').value;
    const programName = document.getElementById('programName').value;

    if (!cobolSource.trim()) {
        alert('Please enter COBOL source code');
        return;
    }

    const btn = document.getElementById('transpileBtn');
    const outputArea = document.getElementById('outputArea');

    btn.disabled = true;
    btn.textContent = 'Transpiling...';
    outputArea.innerHTML = '<div class="loading">Transpiling COBOL to Java</div>';

    try {
        const response = await fetch('/api/transpile', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                cobolSource: cobolSource,
                programName: programName
            })
        });

        const result = await response.json();

        if (result.success) {
            lastTranspilationSuccess = true;
            currentProgramName = programName;
            outputArea.innerHTML = `<pre class="success">${escapeHtml(result.javaSource)}</pre>`;
            document.getElementById('runBtn').disabled = false;
        } else {
            lastTranspilationSuccess = false;
            document.getElementById('runBtn').disabled = true;
            const errors = result.errors.map(e => `<div class="error">Error: ${escapeHtml(e)}</div>`).join('');
            outputArea.innerHTML = errors;
        }
    } catch (error) {
        lastTranspilationSuccess = false;
        document.getElementById('runBtn').disabled = true;
        outputArea.innerHTML = `<div class="error">Transpilation failed: ${escapeHtml(error.message)}</div>`;
    } finally {
        btn.disabled = false;
        btn.textContent = 'Transpile to Java';
    }
}

// Run the transpiled program
async function runProgram() {
    if (!lastTranspilationSuccess || !currentProgramName) {
        alert('Please transpile a program first');
        return;
    }

    const btn = document.getElementById('runBtn');
    const executionOutput = document.getElementById('executionOutput');

    btn.disabled = true;
    btn.textContent = 'Running...';
    executionOutput.innerHTML = '<div class="loading">Executing program</div>';

    // Switch to execution tab
    document.querySelector('[data-tab="execution"]').click();

    try {
        const response = await fetch('/api/run', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                programName: currentProgramName,
                programType: 'batch'
            })
        });

        const result = await response.json();

        if (result.success) {
            executionOutput.innerHTML = `<pre>${escapeHtml(result.output)}</pre>`;
        } else {
            const errors = result.errors.map(e => `<div class="error">Error: ${escapeHtml(e)}</div>`).join('');
            executionOutput.innerHTML = errors;
        }
    } catch (error) {
        executionOutput.innerHTML = `<div class="error">Execution failed: ${escapeHtml(error.message)}</div>`;
    } finally {
        btn.disabled = false;
        btn.textContent = 'Run Program';
    }
}

// Utility function to escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadSamples();

    // Load a default sample for demonstration
    const demoCobol = `       IDENTIFICATION DIVISION.
       PROGRAM-ID. BATCH1.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 WS-MESSAGE.
          05 WS-GREETING    PIC X(20) VALUE 'Hello from COBOL!'.
          05 WS-STATUS      PIC X(10) VALUE 'SUCCESS'.

       PROCEDURE DIVISION.
       MAIN-PROCEDURE.
           DISPLAY WS-GREETING.
           DISPLAY 'Status: ' WS-STATUS.
           STOP RUN.`;

    document.getElementById('cobolSource').value = demoCobol;
});

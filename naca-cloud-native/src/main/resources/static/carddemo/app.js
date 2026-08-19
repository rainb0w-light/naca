const form = document.querySelector('#terminal-form');
const state = document.querySelector('#request-state');
const output = document.querySelector('#response-output');
const submit = form.querySelector('button[type="submit"]');
const conversationId = crypto.randomUUID();

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  submit.disabled = true;
  state.textContent = '请求处理中…';

  const request = {
    requestId: crypto.randomUUID(),
    conversationId,
    transactionId: 'CC00',
    mapSet: 'COSGN00',
    map: 'COSGN0A',
    aid: document.querySelector('#aid').value,
    cursorField: 'USERID',
    fields: {
      USERID: field(document.querySelector('#user-id').value),
      ERRMSG: field(document.querySelector('#message').value)
    }
  };

  try {
    const response = await fetch('/api/carddemo/bms/adapter', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(request)
    });
    const body = await response.json();
    if (!response.ok) throw new Error(JSON.stringify(body));
    output.textContent = JSON.stringify(body, null, 2);
    state.textContent = `请求成功 · ${body.program}`;
  } catch (error) {
    output.textContent = `请求失败：${error.message}`;
    state.textContent = '请求失败';
  } finally {
    submit.disabled = false;
  }
});

function field(value) {
  return {value, modified: true, cleared: value.length === 0};
}

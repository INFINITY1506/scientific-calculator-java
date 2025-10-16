const expressionEl = document.getElementById('expression');
const resultEl = document.getElementById('result');
const angleToggle = document.getElementById('angleToggle');
const angleIndicator = document.getElementById('angleIndicator');
const memoryIndicator = document.getElementById('memoryIndicator');
const contrastToggle = document.getElementById('contrastToggle');
let angleMode = 'DEG';
let expr = '';
let memory = '0';

function updateDisplay() {
  expressionEl.textContent = expr;
}

function appendToken(t) {
  if (!expr && ['+','-','×','÷','%','^','.'].includes(t)) return;
  const last = expr.slice(-1);
  if (['+','-','×','÷','%','^','.'].includes(t) && ['+','-','×','÷','%','^','.','('].includes(last)) return;
  if (t === '.' && /\d*\.\d*$/.test(getCurrentNumber())) return;
  expr += t;
  updateDisplay();
}

function getCurrentNumber() {
  const m = expr.match(/([\d.]+)$/);
  return m ? m[1] : '';
}

function backspace() {
  expr = expr.slice(0, -1);
  updateDisplay();
}

function clearAll() { expr = ''; updateDisplay(); resultEl.textContent = '0'; }
function clearEntry() {
  const idx = expr.search(/([\d.]+|π|e)(?!.*([\d.]+|π|e))/);
  if (idx >= 0) expr = expr.slice(0, idx); else expr = '';
  updateDisplay();
}

async function evaluateExpr() {
  if (!expr) return;
  let toSend = expr.replaceAll('×','*').replaceAll('÷','/');
  const res = await fetch('/api/calc/eval', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ expression: toSend, angleMode }) });
  const data = await res.json();
  if (data.error) { resultEl.textContent = data.error; return; }
  resultEl.textContent = data.result;
}

function toggleAngle() {
  angleMode = angleMode === 'DEG' ? 'RAD' : 'DEG';
  angleToggle.textContent = angleMode;
  angleIndicator.textContent = angleMode;
}

async function memoryOp(op) {
  const val = resultEl.textContent && !isNaN(Number(resultEl.textContent.replaceAll(',',''))) ? resultEl.textContent.replaceAll(',','') : null;
  const res = await fetch('/api/calc/memory', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ op, value: val }) });
  const data = await res.json();
  memory = data.memory;
  memoryIndicator.textContent = new Intl.NumberFormat('en-US').format(Number(memory));
}

function insertFn(fn) {
  if (fn === 'square') { appendToken('^2'); return; }
  if (fn === 'pow') { appendToken('^'); return; }
  if (fn === 'fact') { appendToken('fact('); appendToken(')'); return; }
  if (['sin','cos','tan','asin','acos','atan','ln','log','sqrt','inv'].includes(fn)) { appendToken(fn + '('); return; }
}

function plusMinus() {
  const m = expr.match(/(.*?)([\d.]+|π|e)\s*$/);
  if (!m) return;
  const head = m[1];
  const num = m[2];
  if (head.endsWith('(-')) return;
  expr = head + '(-' + num + ')';
  updateDisplay();
}

function bindUI() {
  document.querySelectorAll('button[data-val]').forEach(b=>b.addEventListener('click',()=>appendToken(b.dataset.val)));
  document.querySelectorAll('button[data-op]').forEach(b=>b.addEventListener('click',()=>appendToken(b.dataset.op)));
  document.getElementById('equals').addEventListener('click', evaluateExpr);
  document.querySelectorAll('button[data-action]').forEach(b=>{
    const a = b.dataset.action;
    b.addEventListener('click', ()=>{
      if (a==='AC') clearAll();
      if (a==='C') clearEntry();
      if (a==='pm') plusMinus();
      if (['MC','MR','M+','M-'].includes(a)) memoryOp(a);
    });
  });
  document.querySelectorAll('button[data-fn]').forEach(b=>b.addEventListener('click',()=>insertFn(b.dataset.fn)));
  angleToggle.addEventListener('click', toggleAngle);
  contrastToggle.addEventListener('click', ()=> document.body.classList.toggle('high-contrast'));
}

function bindKeyboard() {
  window.addEventListener('keydown', e=>{
    if (e.key === 'Enter') { evaluateExpr(); e.preventDefault(); }
    else if (e.key === 'Backspace') { backspace(); e.preventDefault(); }
    else if (e.key === 'Escape') { clearAll(); e.preventDefault(); }
    else if ('0123456789'.includes(e.key)) appendToken(e.key);
    else if (e.key === '.') appendToken('.');
    else if (['+','-','*','/','^','%','(',')'].includes(e.key)) appendToken(e.key.replace('*','×').replace('/','÷'));
  });
}

if ('serviceWorker' in navigator) { navigator.serviceWorker.register('/service-worker.js'); }

bindUI();
bindKeyboard();
updateDisplay();



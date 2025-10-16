self.addEventListener('install', e=>{ self.skipWaiting(); });
self.addEventListener('activate', e=>{ self.clients.claim(); });
self.addEventListener('fetch', e=>{ e.respondWith(caches.open('v1').then(async c=>{ const r=await c.match(e.request); if(r) return r; const f=await fetch(e.request); c.put(e.request, f.clone()); return f; })); });



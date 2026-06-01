const CACHE_NAME = "emsl-mobile-approval-v3";

const APP_SHELL = [
    "/mobile/login.html",
    "/mobile/index.html",
    "/mobile/needs-request.html",
    "/mobile/approvals.html",
    "/mobile/my-requests.html",

    "/mobile/css/mobile.css",
    "/mobile/css/dialog.css",

    "/mobile/js/config.js",
    "/mobile/js/auth.js",
    "/mobile/js/home.js",
    "/mobile/js/mobile-dialog.js",
    "/mobile/js/needs-request.js",
    "/mobile/js/approvals.js",
    "/mobile/js/my-requests.js",
    "/mobile/js/pwa-register.js",

    "/mobile/images/user-avatar.png",

    "/mobile/icons/icon-180.png",
    "/mobile/icons/icon-192.png",
    "/mobile/icons/icon-512.png",

    "/mobile/manifest.json"
];

self.addEventListener("install", event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(APP_SHELL))
            .then(() => self.skipWaiting())
    );
});

self.addEventListener("activate", event => {
    event.waitUntil(
        caches.keys()
            .then(keys => {
                return Promise.all(
                    keys
                        .filter(key => key !== CACHE_NAME)
                        .map(key => caches.delete(key))
                );
            })
            .then(() => self.clients.claim())
    );
});

self.addEventListener("fetch", event => {
    const request = event.request;

    if (request.method !== "GET") {
        return;
    }

    if (request.url.includes("/api/")) {
        return;
    }

    event.respondWith(
        fetch(request)
            .then(response => {
                const responseClone = response.clone();

                caches.open(CACHE_NAME).then(cache => {
                    cache.put(request, responseClone);
                });

                return response;
            })
            .catch(() => caches.match(request))
    );
});
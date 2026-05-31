const CACHE_NAME = "emsl-mobile-approval-v1";

const APP_SHELL = [
    "/mobile/",
    "/mobile/index.html",
    "/mobile/login.html",
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
    "/mobile/images/user-avatar.png",
    "/mobile/icons/icon-192.png",
    "/mobile/icons/icon-512.png"
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
        caches.keys().then(keys => {
            return Promise.all(
                keys
                    .filter(key => key !== CACHE_NAME)
                    .map(key => caches.delete(key))
            );
        }).then(() => self.clients.claim())
    );
});

self.addEventListener("fetch", event => {
    const request = event.request;

    if (request.method !== "GET") {
        return;
    }

    event.respondWith(
        fetch(request)
            .then(response => {
                const clonedResponse = response.clone();

                caches.open(CACHE_NAME).then(cache => {
                    cache.put(request, clonedResponse);
                });

                return response;
            })
            .catch(() => caches.match(request))
    );
});

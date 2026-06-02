/* =========================================================
   EMS-L Mobile - Service Worker
   Full clean file
   Path: src/main/resources/static/mobile/service-worker.js
   ========================================================= */

const EMSL_CACHE_NAME = "emsl-mobile-cache-v4";

const EMSL_STATIC_FILES = [
    "/mobile/login.html",
    "/mobile/index.html",
    "/mobile/needs-request.html",
    "/mobile/approvals.html",
    "/mobile/my-requests.html",
    "/mobile/notifications.html",

    "/mobile/css/mobile.css",
    "/mobile/css/dialog.css",

    "/mobile/js/config.js",
    "/mobile/js/mobile-dialog.js",
    "/mobile/js/auth.js",
    "/mobile/js/login.js",
    "/mobile/js/needs-request.js",
    "/mobile/js/approvals.js",
    "/mobile/js/my-requests.js",
    "/mobile/js/notifications.js",
    "/mobile/js/pwa-register.js",

    "/mobile/manifest.json",

    "/mobile/icons/icon-192.png",
    "/mobile/icons/icon-512.png"
];

self.addEventListener("install", function (event) {
    self.skipWaiting();

    event.waitUntil(
        caches.open(EMSL_CACHE_NAME)
            .then(function (cache) {
                return cache.addAll(EMSL_STATIC_FILES);
            })
            .catch(function (error) {
                console.warn("EMS-L cache install warning:", error);
            })
    );
});

self.addEventListener("activate", function (event) {
    event.waitUntil(
        caches.keys()
            .then(function (cacheNames) {
                return Promise.all(
                    cacheNames.map(function (cacheName) {
                        if (cacheName !== EMSL_CACHE_NAME) {
                            return caches.delete(cacheName);
                        }

                        return Promise.resolve();
                    })
                );
            })
            .then(function () {
                return self.clients.claim();
            })
    );
});

self.addEventListener("fetch", function (event) {
    const request = event.request;

    if (request.method !== "GET") {
        return;
    }

    const url = new URL(request.url);

    if (url.pathname.startsWith("/api/")) {
        event.respondWith(fetch(request));
        return;
    }

    event.respondWith(
        fetch(request)
            .then(function (networkResponse) {
                const responseClone = networkResponse.clone();

                caches.open(EMSL_CACHE_NAME)
                    .then(function (cache) {
                        cache.put(request, responseClone);
                    })
                    .catch(function () {
                        // Ignore cache errors
                    });

                return networkResponse;
            })
            .catch(function () {
                return caches.match(request)
                    .then(function (cachedResponse) {
                        if (cachedResponse) {
                            return cachedResponse;
                        }

                        if (request.mode === "navigate") {
                            return caches.match("/mobile/login.html");
                        }

                        return new Response("Offline", {
                            status: 503,
                            statusText: "Offline"
                        });
                    });
            })
    );
});
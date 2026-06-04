/* =========================================================
   EMS-L Mobile - Service Worker
   Full clean file
   Path: src/main/resources/static/mobile/service-worker.js
   ========================================================= */

const CACHE_NAME = "emsl-mobile-pwa-v5";

self.addEventListener("install", function (event) {
    self.skipWaiting();

    event.waitUntil(
        caches.open(CACHE_NAME).then(function (cache) {
            return cache.addAll([
                "/mobile/login.html",
                "/mobile/index.html",
                "/mobile/manifest.json",
                "/mobile/css/mobile.css",
                "/mobile/css/dialog.css",
                "/mobile/js/config.js",
                "/mobile/js/auth.js",
                "/mobile/js/mobile-dialog.js",
                "/mobile/js/pwa-register.js",
                "/mobile/icons/icon-192.png",
                "/mobile/icons/icon-512.png"
            ]).catch(function (error) {
                console.warn("EMS-L pre-cache warning:", error);
            });
        })
    );
});

self.addEventListener("activate", function (event) {
    event.waitUntil(
        caches.keys()
            .then(function (cacheNames) {
                return Promise.all(
                    cacheNames.map(function (cacheName) {
                        if (cacheName !== CACHE_NAME) {
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

    if (!url.pathname.startsWith("/mobile/")) {
        event.respondWith(fetch(request));
        return;
    }

    event.respondWith(
        fetch(request)
            .then(function (networkResponse) {
                const responseClone = networkResponse.clone();

                caches.open(CACHE_NAME)
                    .then(function (cache) {
                        cache.put(request, responseClone);
                    })
                    .catch(function () {
                        // Ignore cache write errors
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
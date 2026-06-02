/* =========================================================
   EMS-L Mobile - PWA Register
   Full clean file
   Path: src/main/resources/static/mobile/js/pwa-register.js
   ========================================================= */

(function () {
    "use strict";

    window.addEventListener("load", function () {
        registerServiceWorker();
        detectStandaloneMode();
    });

    function registerServiceWorker() {
        if (!("serviceWorker" in navigator)) {
            console.warn("Service Worker is not supported on this browser.");
            return;
        }

        navigator.serviceWorker.register("/mobile/service-worker.js", {
            scope: "/mobile/"
        }).then(function (registration) {
            console.log("EMS-L service worker registered:", registration.scope);

            registration.update();

        }).catch(function (error) {
            console.error("EMS-L service worker registration failed:", error);
        });
    }

    function detectStandaloneMode() {
        const isStandalone =
            window.matchMedia("(display-mode: standalone)").matches ||
            window.navigator.standalone === true;

        if (isStandalone) {
            document.documentElement.classList.add("pwa-standalone");
            console.log("EMS-L is running in standalone app mode.");
        } else {
            document.documentElement.classList.remove("pwa-standalone");
            console.log("EMS-L is running in browser mode.");
        }
    }
})();
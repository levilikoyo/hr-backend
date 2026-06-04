/* =========================================================
   EMS-L Mobile - PWA Register
   Full clean file
   Path: src/main/resources/static/mobile/js/pwa-register.js
   ========================================================= */

(function () {
    "use strict";

    let deferredInstallPrompt = null;

    window.addEventListener("load", function () {
        registerServiceWorker();
        detectStandaloneMode();
        createInstallButtonIfNeeded();
    });

    window.addEventListener("beforeinstallprompt", function (event) {
        event.preventDefault();
        deferredInstallPrompt = event;

        console.log("EMS-L PWA install prompt is available.");

        createInstallButtonIfNeeded();
        showInstallButton();
    });

    window.addEventListener("appinstalled", function () {
        console.log("EMS-L PWA installed successfully.");
        deferredInstallPrompt = null;
        hideInstallButton();
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
            document.documentElement.classList.remove("pwa-browser");
            hideInstallButton();
        } else {
            document.documentElement.classList.add("pwa-browser");
            document.documentElement.classList.remove("pwa-standalone");
        }
    }

    function createInstallButtonIfNeeded() {
        if (document.getElementById("pwaInstallBox")) {
            return;
        }

        const box = document.createElement("div");
        box.id = "pwaInstallBox";
        box.style.display = "none";
        box.style.position = "fixed";
        box.style.left = "16px";
        box.style.right = "16px";
        box.style.bottom = "16px";
        box.style.zIndex = "9999";
        box.style.background = "#ffffff";
        box.style.border = "1px solid #dbeafe";
        box.style.borderRadius = "18px";
        box.style.boxShadow = "0 18px 45px rgba(15, 23, 42, 0.22)";
        box.style.padding = "14px";

        box.innerHTML = `
            <div style="font-weight:900;color:#111827;font-size:15px;margin-bottom:4px;">
                Install EMS-L
            </div>
            <div style="color:#6b7280;font-size:12px;font-weight:700;margin-bottom:12px;">
                Install the mobile app on this phone for a better experience.
            </div>
            <button type="button" id="pwaInstallBtn"
                style="width:100%;border:none;border-radius:14px;background:#0f6cbd;color:#ffffff;font-weight:900;padding:12px;font-size:14px;">
                Install app
            </button>
        `;

        document.body.appendChild(box);

        const installButton = document.getElementById("pwaInstallBtn");

        if (installButton) {
            installButton.addEventListener("click", installPwa);
        }
    }

    async function installPwa() {
        if (!deferredInstallPrompt) {
            if (window.MobileDialog && typeof window.MobileDialog.info === "function") {
                window.MobileDialog.info(
                    "Install EMS-L",
                    "Open Chrome menu and choose Install app or Add to Home screen."
                );
            } else {
                alert("Open Chrome menu and choose Install app or Add to Home screen.");
            }

            return;
        }

        deferredInstallPrompt.prompt();

        const choiceResult = await deferredInstallPrompt.userChoice;

        console.log("EMS-L install choice:", choiceResult.outcome);

        deferredInstallPrompt = null;
        hideInstallButton();
    }

    function showInstallButton() {
        const isStandalone =
            window.matchMedia("(display-mode: standalone)").matches ||
            window.navigator.standalone === true;

        if (isStandalone) {
            return;
        }

        const box = document.getElementById("pwaInstallBox");

        if (box) {
            box.style.display = "block";
        }
    }

    function hideInstallButton() {
        const box = document.getElementById("pwaInstallBox");

        if (box) {
            box.style.display = "none";
        }
    }

    window.EMSLPWA = {
        install: installPwa,
        showInstallButton: showInstallButton,
        hideInstallButton: hideInstallButton
    };
})();
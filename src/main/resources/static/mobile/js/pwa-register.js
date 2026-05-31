if ("serviceWorker" in navigator) {
    window.addEventListener("load", function () {
        navigator.serviceWorker.register("/mobile/service-worker.js")
            .then(function () {
                console.log("Service worker registered successfully.");
            })
            .catch(function (error) {
                console.log("Service worker registration failed:", error);
            });
    });
}

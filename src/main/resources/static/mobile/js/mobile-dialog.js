/* =========================================================
   EMS-L Mobile - Professional Dialog System
   Full clean file
   Path: src/main/resources/static/mobile/js/mobile-dialog.js
   ========================================================= */

(function () {
    "use strict";

    function ensureDialogRoot() {
        let root = document.getElementById("mobileDialogRoot");

        if (!root) {
            root = document.createElement("div");
            root.id = "mobileDialogRoot";
            document.body.appendChild(root);
        }

        return root;
    }

    function closeMobileDialog() {
        const root = ensureDialogRoot();
        root.innerHTML = "";
        document.body.classList.remove("dialog-open");
    }

    function showMessageDialog(title, message, type = "info") {
        return new Promise(function (resolve) {
            const root = ensureDialogRoot();

            const icon = getDialogIcon(type);
            const iconClass = getDialogIconClass(type);

            document.body.classList.add("dialog-open");

            root.innerHTML = `
                <div class="dialog-overlay show">
                    <div class="dialog-box">
                        <div class="dialog-header">
                            <div class="dialog-icon ${iconClass}">
                                ${icon}
                            </div>

                            <div>
                                <h3 class="dialog-title">${escapeDialogHtml(title)}</h3>
                            </div>
                        </div>

                        <div class="dialog-message">
                            ${escapeDialogHtml(message)}
                        </div>

                        <div class="dialog-actions single">
                            <button type="button" class="dialog-btn primary" id="dialogOkBtn">
                                OK
                            </button>
                        </div>
                    </div>
                </div>
            `;

            const okButton = document.getElementById("dialogOkBtn");

            if (okButton) {
                okButton.focus();

                okButton.onclick = function () {
                    closeMobileDialog();
                    resolve(true);
                };
            }
        });
    }

    function showConfirmDialog(title, message, type = "warning", confirmText = "Confirm", cancelText = "Cancel") {
        return new Promise(function (resolve) {
            const root = ensureDialogRoot();

            const icon = getDialogIcon(type);
            const iconClass = getDialogIconClass(type);
            const confirmClass = type === "danger" ? "danger" : "primary";

            document.body.classList.add("dialog-open");

            root.innerHTML = `
                <div class="dialog-overlay show">
                    <div class="dialog-box">
                        <div class="dialog-header">
                            <div class="dialog-icon ${iconClass}">
                                ${icon}
                            </div>

                            <div>
                                <h3 class="dialog-title">${escapeDialogHtml(title)}</h3>
                            </div>
                        </div>

                        <div class="dialog-message">
                            ${escapeDialogHtml(message)}
                        </div>

                        <div class="dialog-actions">
                            <button type="button" class="dialog-btn secondary" id="dialogCancelBtn">
                                ${escapeDialogHtml(cancelText)}
                            </button>

                            <button type="button" class="dialog-btn ${confirmClass}" id="dialogConfirmBtn">
                                ${escapeDialogHtml(confirmText)}
                            </button>
                        </div>
                    </div>
                </div>
            `;

            const cancelButton = document.getElementById("dialogCancelBtn");
            const confirmButton = document.getElementById("dialogConfirmBtn");

            if (cancelButton) {
                cancelButton.onclick = function () {
                    closeMobileDialog();
                    resolve(false);
                };
            }

            if (confirmButton) {
                confirmButton.focus();

                confirmButton.onclick = function () {
                    closeMobileDialog();
                    resolve(true);
                };
            }
        });
    }

    function showPromptDialog(title, message, placeholder = "", type = "warning", confirmText = "Submit", cancelText = "Cancel") {
        return new Promise(function (resolve) {
            const root = ensureDialogRoot();

            const icon = getDialogIcon(type);
            const iconClass = getDialogIconClass(type);

            document.body.classList.add("dialog-open");

            root.innerHTML = `
                <div class="dialog-overlay show">
                    <div class="dialog-box">
                        <div class="dialog-header">
                            <div class="dialog-icon ${iconClass}">
                                ${icon}
                            </div>

                            <div>
                                <h3 class="dialog-title">${escapeDialogHtml(title)}</h3>
                            </div>
                        </div>

                        <div class="dialog-message">
                            ${escapeDialogHtml(message)}
                        </div>

                        <div class="dialog-input-area">
                            <textarea id="dialogPromptInput" placeholder="${escapeDialogHtml(placeholder)}"></textarea>
                        </div>

                        <div class="dialog-actions">
                            <button type="button" class="dialog-btn secondary" id="dialogCancelBtn">
                                ${escapeDialogHtml(cancelText)}
                            </button>

                            <button type="button" class="dialog-btn primary" id="dialogConfirmBtn">
                                ${escapeDialogHtml(confirmText)}
                            </button>
                        </div>
                    </div>
                </div>
            `;

            const input = document.getElementById("dialogPromptInput");
            const cancelButton = document.getElementById("dialogCancelBtn");
            const confirmButton = document.getElementById("dialogConfirmBtn");

            if (input) {
                setTimeout(function () {
                    input.focus();
                }, 80);
            }

            if (cancelButton) {
                cancelButton.onclick = function () {
                    closeMobileDialog();
                    resolve(null);
                };
            }

            if (confirmButton) {
                confirmButton.onclick = function () {
                    const value = input ? input.value : "";
                    closeMobileDialog();
                    resolve(value);
                };
            }
        });
    }

    function showHtmlDialog(title, htmlContent, type = "info") {
        return new Promise(function (resolve) {
            const root = ensureDialogRoot();

            const icon = getDialogIcon(type);
            const iconClass = getDialogIconClass(type);

            document.body.classList.add("dialog-open");

            root.innerHTML = `
                <div class="dialog-overlay show">
                    <div class="dialog-box">
                        <div class="dialog-header">
                            <div class="dialog-icon ${iconClass}">
                                ${icon}
                            </div>

                            <div>
                                <h3 class="dialog-title">${escapeDialogHtml(title)}</h3>
                            </div>
                        </div>

                        <div class="dialog-message html">
                            ${htmlContent || ""}
                        </div>

                        <div class="dialog-actions single">
                            <button type="button" class="dialog-btn primary" id="dialogOkBtn">
                                OK
                            </button>
                        </div>
                    </div>
                </div>
            `;

            const okButton = document.getElementById("dialogOkBtn");

            if (okButton) {
                okButton.focus();

                okButton.onclick = function () {
                    closeMobileDialog();
                    resolve(true);
                };
            }
        });
    }

    function showToast(message, type = "info", duration = 2200) {
        const toastId = "mobileToastRoot";
        let root = document.getElementById(toastId);

        if (!root) {
            root = document.createElement("div");
            root.id = toastId;
            document.body.appendChild(root);
        }

        const icon = getDialogIcon(type);
        const toastClass = getDialogIconClass(type);

        root.innerHTML = `
            <div class="mobile-toast ${toastClass}">
                <span>${icon}</span>
                <strong>${escapeDialogHtml(message)}</strong>
            </div>
        `;

        setTimeout(function () {
            root.innerHTML = "";
        }, duration);
    }

    function getDialogIcon(type) {
        if (type === "success") {
            return "✓";
        }

        if (type === "danger" || type === "error") {
            return "!";
        }

        if (type === "warning") {
            return "!";
        }

        return "i";
    }

    function getDialogIconClass(type) {
        if (type === "success") {
            return "success";
        }

        if (type === "danger" || type === "error") {
            return "danger";
        }

        if (type === "warning") {
            return "warning";
        }

        return "info";
    }

    function escapeDialogHtml(value) {
        if (value === null || value === undefined) {
            return "";
        }

        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    window.MobileDialog = {
        info: function (title, message) {
            return showMessageDialog(title || "Information", message || "", "info");
        },

        success: function (title, message) {
            return showMessageDialog(title || "Success", message || "", "success");
        },

        warning: function (title, message) {
            return showMessageDialog(title || "Warning", message || "", "warning");
        },

        error: function (title, message) {
            return showMessageDialog(title || "Error", message || "", "danger");
        },

        confirm: function (title, message, confirmText, cancelText) {
            return showConfirmDialog(
                title || "Confirm action",
                message || "Do you want to continue?",
                "warning",
                confirmText || "Confirm",
                cancelText || "Cancel"
            );
        },

        confirmDanger: function (title, message, confirmText, cancelText) {
            return showConfirmDialog(
                title || "Confirm action",
                message || "Do you want to continue?",
                "danger",
                confirmText || "Confirm",
                cancelText || "Cancel"
            );
        },

        prompt: function (title, message, placeholder, confirmText) {
            return showPromptDialog(
                title || "Input required",
                message || "",
                placeholder || "",
                "warning",
                confirmText || "Submit"
            );
        },

        infoHtml: function (title, htmlContent) {
            return showHtmlDialog(title || "Information", htmlContent || "", "info");
        },

        successToast: function (message) {
            showToast(message || "Success", "success");
        },

        errorToast: function (message) {
            showToast(message || "Error", "danger");
        },

        warningToast: function (message) {
            showToast(message || "Warning", "warning");
        },

        close: closeMobileDialog
    };

    window.showMessageDialog = showMessageDialog;
    window.showConfirmDialog = showConfirmDialog;
    window.showPromptDialog = showPromptDialog;
    window.showHtmlDialog = showHtmlDialog;
    window.closeMobileDialog = closeMobileDialog;
})();
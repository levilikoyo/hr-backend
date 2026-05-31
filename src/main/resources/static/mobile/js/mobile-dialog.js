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
}

function showMessageDialog(title, message, type = "info") {
    return new Promise(resolve => {
        const root = ensureDialogRoot();

        const icon = getDialogIcon(type);
        const iconClass = getDialogIconClass(type);

        root.innerHTML = `
            <div class="dialog-overlay show">
                <div class="dialog-box">
                    <div class="dialog-header">
                        <div class="dialog-icon ${iconClass}">${icon}</div>
                        <h3 class="dialog-title">${escapeDialogHtml(title)}</h3>
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

        document.getElementById("dialogOkBtn").onclick = function () {
            closeMobileDialog();
            resolve(true);
        };
    });
}

function showConfirmDialog(title, message, type = "warning", confirmText = "Confirm") {
    return new Promise(resolve => {
        const root = ensureDialogRoot();

        const icon = getDialogIcon(type);
        const iconClass = getDialogIconClass(type);
        const confirmClass = type === "danger" ? "danger" : "primary";

        root.innerHTML = `
            <div class="dialog-overlay show">
                <div class="dialog-box">
                    <div class="dialog-header">
                        <div class="dialog-icon ${iconClass}">${icon}</div>
                        <h3 class="dialog-title">${escapeDialogHtml(title)}</h3>
                    </div>

                    <div class="dialog-message">
                        ${escapeDialogHtml(message)}
                    </div>

                    <div class="dialog-actions">
                        <button type="button" class="dialog-btn secondary" id="dialogCancelBtn">
                            Cancel
                        </button>

                        <button type="button" class="dialog-btn ${confirmClass}" id="dialogConfirmBtn">
                            ${escapeDialogHtml(confirmText)}
                        </button>
                    </div>
                </div>
            </div>
        `;

        document.getElementById("dialogCancelBtn").onclick = function () {
            closeMobileDialog();
            resolve(false);
        };

        document.getElementById("dialogConfirmBtn").onclick = function () {
            closeMobileDialog();
            resolve(true);
        };
    });
}

function showPromptDialog(title, message, placeholder = "", type = "warning", confirmText = "Submit") {
    return new Promise(resolve => {
        const root = ensureDialogRoot();

        const icon = getDialogIcon(type);
        const iconClass = getDialogIconClass(type);

        root.innerHTML = `
            <div class="dialog-overlay show">
                <div class="dialog-box">
                    <div class="dialog-header">
                        <div class="dialog-icon ${iconClass}">${icon}</div>
                        <h3 class="dialog-title">${escapeDialogHtml(title)}</h3>
                    </div>

                    <div class="dialog-message">
                        ${escapeDialogHtml(message)}
                    </div>

                    <div class="dialog-input-area">
                        <textarea id="dialogPromptInput" placeholder="${escapeDialogHtml(placeholder)}"></textarea>
                    </div>

                    <div class="dialog-actions">
                        <button type="button" class="dialog-btn secondary" id="dialogCancelBtn">
                            Cancel
                        </button>

                        <button type="button" class="dialog-btn primary" id="dialogConfirmBtn">
                            ${escapeDialogHtml(confirmText)}
                        </button>
                    </div>
                </div>
            </div>
        `;

        const input = document.getElementById("dialogPromptInput");

        if (input) {
            input.focus();
        }

        document.getElementById("dialogCancelBtn").onclick = function () {
            closeMobileDialog();
            resolve(null);
        };

        document.getElementById("dialogConfirmBtn").onclick = function () {
            const value = input ? input.value : "";
            closeMobileDialog();
            resolve(value);
        };
    });
}

function showHtmlDialog(title, htmlContent, type = "info") {
    return new Promise(resolve => {
        const root = ensureDialogRoot();

        const icon = getDialogIcon(type);
        const iconClass = getDialogIconClass(type);

        root.innerHTML = `
            <div class="dialog-overlay show">
                <div class="dialog-box">
                    <div class="dialog-header">
                        <div class="dialog-icon ${iconClass}">${icon}</div>
                        <h3 class="dialog-title">${escapeDialogHtml(title)}</h3>
                    </div>

                    <div class="dialog-message">
                        ${htmlContent}
                    </div>

                    <div class="dialog-actions single">
                        <button type="button" class="dialog-btn primary" id="dialogOkBtn">
                            OK
                        </button>
                    </div>
                </div>
            </div>
        `;

        document.getElementById("dialogOkBtn").onclick = function () {
            closeMobileDialog();
            resolve(true);
        };
    });
}

function getDialogIcon(type) {
    if (type === "success") {
        return "✓";
    }

    if (type === "danger") {
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

    if (type === "danger") {
        return "danger";
    }

    if (type === "warning") {
        return "warning";
    }

    return "";
}

function escapeDialogHtml(value) {
    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}
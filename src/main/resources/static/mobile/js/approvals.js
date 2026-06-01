/* =========================================================
   EMS-L Mobile - Approvals
   Full clean file
   Path: src/main/resources/static/mobile/js/approvals.js
   ========================================================= */

(function () {
    "use strict";

    let currentUser = null;
    let currentOrganization = "";
    let currentRole = "";
    let pendingRequests = [];

    let funds = [];
    let currencies = [];
    let glAccounts = [];
    let groupedDimensions = [];

    document.addEventListener("DOMContentLoaded", async function () {
        try {
            currentUser = requireLogin();
            currentOrganization = getCurrentOrganization();
            currentRole = normalizeRole(getCurrentUserRole());

            displayCurrentUser();

            await loadReferenceData();
            await loadPendingApprovals();

        } catch (error) {
            console.error("Approvals initialization error:", error);
            showError("Failed to initialize approvals page: " + error.message);
        }
    });

    async function loadReferenceData() {
        await Promise.all([
            loadFunds(),
            loadCurrencies(),
            loadGLAccounts(),
            loadGroupedDimensions()
        ]);
    }

    async function loadPendingApprovals() {
        setLoading(true);

        try {
            const url =
                `${BASE_URL}/api/needs-requests/pending-approval/${encodeURIComponent(currentOrganization)}/${encodeURIComponent(currentRole)}`;

            const response = await fetch(url, {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                },
                cache: "no-store"
            });

            const text = await response.text();

            if (!response.ok) {
                throw new Error(`HTTP ${response.status} - ${text}`);
            }

            const data = text ? JSON.parse(text) : [];
            pendingRequests = Array.isArray(data) ? data : [];

            renderApprovals();

        } catch (error) {
            console.error("Load approvals error:", error);
            showError("Failed to load pending approvals: " + error.message);
        } finally {
            setLoading(false);
        }
    }

    async function loadFunds() {
        try {
            const data = await fetchJson(
                `${BASE_URL}/api/funds/organization/${encodeURIComponent(currentOrganization)}`
            );

            funds = toArray(data);

        } catch (error) {
            console.warn("Funds loading failed:", error);
            funds = [];
        }
    }

    async function loadCurrencies() {
        try {
            const data = await fetchJson(
                `${BASE_URL}/api/currencies/organization/${encodeURIComponent(currentOrganization)}`
            );

            currencies = toArray(data);

        } catch (error) {
            console.warn("Currencies loading failed:", error);
            currencies = [];
        }
    }

    async function loadGLAccounts() {
        try {
            const data = await fetchJson(
                `${BASE_URL}/api/gl-accounts/organization/${encodeURIComponent(currentOrganization)}`
            );

            glAccounts = toArray(data);

        } catch (error) {
            console.warn("G/L accounts loading failed:", error);
            glAccounts = [];
        }
    }

    async function loadGroupedDimensions() {
        try {
            const data = await fetchJson(
                `${BASE_URL}/api/dimension-setups/organization/${encodeURIComponent(currentOrganization)}/grouped`
            );

            groupedDimensions = toArray(data).filter(function (dimension) {
                return normalizeCode(dimension.dimensionCode) !== "FUND";
            });

        } catch (error) {
            console.warn("Grouped dimensions loading failed:", error);
            groupedDimensions = [];
        }
    }

    async function fetchJson(url) {
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            },
            cache: "no-store"
        });

        const text = await response.text();

        if (!response.ok) {
            throw new Error(`HTTP ${response.status} - ${text}`);
        }

        return text ? JSON.parse(text) : [];
    }

    function renderApprovals() {
        const container = document.getElementById("pendingRequestsContainer");
        const count = document.getElementById("pendingCount");

        if (count) {
            count.textContent = String(pendingRequests.length);
        }

        if (!container) {
            return;
        }

        container.innerHTML = "";

        if (!pendingRequests.length) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">✓</div>
                    <h3>No pending request</h3>
                    <p>There is no request waiting for your approval.</p>
                </div>
            `;
            return;
        }

        pendingRequests.forEach(function (request) {
            container.appendChild(createApprovalCard(request));
        });
    }

    function createApprovalCard(request) {
        const card = document.createElement("div");
        card.className = "approval-card";
        card.dataset.requestId = request.id;

        const level = normalizeRole(request.currentApprovalLevel || currentRole);
        const roleForEdition = currentRole === "ADMIN" ? level : currentRole;

        card.innerHTML = `
            <div class="approval-card-header">
                <div>
                    <h3>${escapeHtml(request.title || "Untitled request")}</h3>
                    <p class="muted">${escapeHtml(request.requestNo || "No request number")}</p>
                </div>

                <span class="status-badge">${escapeHtml(request.status || "")}</span>
            </div>

            <div class="approval-card-body">
                <p class="approval-description">
                    ${escapeHtml(request.description || "No description provided.")}
                </p>

                <div class="info-grid">
                    <div>
                        <span class="label">Requested by</span>
                        <strong>${escapeHtml(request.requestedBy || request.requesterName || "")}</strong>
                    </div>

                    <div>
                        <span class="label">Date</span>
                        <strong>${escapeHtml(request.requestDate || "")}</strong>
                    </div>

                    <div>
                        <span class="label">Priority</span>
                        <strong>${escapeHtml(request.priority || "")}</strong>
                    </div>

                    <div>
                        <span class="label">Total</span>
                        <strong class="request-total" data-request-total="${request.id}">
                            ${formatAmount(request.estimatedAmount || 0)}
                        </strong>
                    </div>
                </div>

                ${renderFinanceEditSection(request, roleForEdition)}

                <div class="approval-items-title">Items</div>

                <div class="approval-items">
                    ${renderItems(request, roleForEdition)}
                </div>
            </div>

            <div class="approval-card-actions">
                <button type="button" class="btn-secondary small-btn" onclick="ApprovalPage.saveChanges(${request.id})">
                    Save
                </button>

                <button type="button" class="btn-approve small-btn" onclick="ApprovalPage.approve(${request.id})">
                    Approve
                </button>

                <button type="button" class="btn-reject small-btn" onclick="ApprovalPage.reject(${request.id})">
                    Reject
                </button>
            </div>
        `;

        setTimeout(function () {
            bindCardCalculation(card);
        }, 0);

        return card;
    }

    function renderFinanceEditSection(request, roleForEdition) {
        const canFinanceEdit = roleForEdition === "FINANCE";

        const budgetPlan = request.budgetPlan || "";
        const fundCode = request.fundCode || "";
        const currencyCode = request.currencyCode || "";
        const glAccountNo = request.glAccountNo || request.glAccountCode || "";
        const dimensionValues = parseDimensionValues(request.dimensionValues);

        if (!canFinanceEdit) {
            return `
                <div class="approval-info-box">
                    <strong>Accounting information</strong>
                    <p>Budget Plan: ${escapeHtml(budgetPlan || "-")}</p>
                    <p>Fund: ${escapeHtml(fundCode || "-")}</p>
                    <p>Currency: ${escapeHtml(currencyCode || "-")}</p>
                    <p>G/L Account: ${escapeHtml(glAccountNo || "-")}</p>
                </div>
            `;
        }

        return `
            <div class="approval-edit-section finance-edit-section">
                <div class="approval-items-title">Finance review</div>

                <div class="form-group">
                    <label>Budget Plan</label>
                    <input
                        type="text"
                        class="finance-budget-plan"
                        data-request-id="${request.id}"
                        value="${escapeHtml(budgetPlan)}"
                        placeholder="Budget Plan">
                </div>

                <div class="form-group">
                    <label>Fund</label>
                    <select class="finance-fund-code" data-request-id="${request.id}">
                        ${renderFundOptions(fundCode)}
                    </select>
                </div>

                <div class="form-group">
                    <label>Currency</label>
                    <select class="finance-currency-code" data-request-id="${request.id}">
                        ${renderCurrencyOptions(currencyCode)}
                    </select>
                </div>

                <div class="form-group">
                    <label>G/L Account</label>
                    <select class="finance-gl-account" data-request-id="${request.id}">
                        ${renderGLAccountOptions(glAccountNo)}
                    </select>
                </div>

                <div class="approval-items-title">Dimensions</div>

                <div class="finance-dimensions" data-request-id="${request.id}">
                    ${renderDimensionFields(request.id, dimensionValues)}
                </div>
            </div>
        `;
    }

    function renderItems(request, roleForEdition) {
        const items = Array.isArray(request.items) ? request.items : [];

        if (!items.length) {
            return `
                <div class="empty-message">
                    No item found for this request.
                </div>
            `;
        }

        return items.map(function (item) {
            return renderItemLine(request, item, roleForEdition);
        }).join("");
    }

    function renderItemLine(request, item, roleForEdition) {
        const canEditQuantity = roleForEdition === "HOD" || roleForEdition === "DIRECTOR";
        const canEditUnitPrice = roleForEdition === "FINANCE";

        const quantity = numberValue(item.quantity);
        const unitPrice = numberValue(item.unitPrice);
        const total = quantity * unitPrice;

        return `
            <div class="approval-edit-item"
                 data-request-id="${request.id}"
                 data-item-id="${item.id}">

                <div class="approval-edit-item-header">
                    <div>
                        <strong>${escapeHtml(item.itemName || "Item")}</strong>
                        <small>${escapeHtml(item.description || "")}</small>
                    </div>
                </div>

                <div class="row-2">
                    <div class="form-group">
                        <label>Quantity</label>
                        <input
                            type="number"
                            class="approval-quantity-input"
                            data-request-id="${request.id}"
                            data-item-id="${item.id}"
                            min="0"
                            step="0.01"
                            value="${quantity}"
                            ${canEditQuantity ? "" : "readonly"}>
                    </div>

                    <div class="form-group">
                        <label>Unit Price</label>
                        <input
                            type="number"
                            class="approval-unit-price-input"
                            data-request-id="${request.id}"
                            data-item-id="${item.id}"
                            min="0"
                            step="0.01"
                            value="${unitPrice}"
                            ${canEditUnitPrice ? "" : "readonly"}>
                    </div>
                </div>

                <div class="item-total-box">
                    <span>Line total</span>
                    <strong class="approval-line-total" data-request-id="${request.id}" data-item-id="${item.id}">
                        ${formatAmount(total)}
                    </strong>
                </div>
            </div>
        `;
    }

    function renderFundOptions(selectedValue) {
        let html = `<option value="">Select fund</option>`;

        funds.forEach(function (fund) {
            const code = firstValue(fund, ["fundCode", "fund_code", "code", "valueCode"]);
            const name = firstValue(fund, ["fundName", "fund_name", "name", "valueName", "description"]);

            if (!code) {
                return;
            }

            html += `
                <option value="${escapeHtml(code)}" ${code === selectedValue ? "selected" : ""}>
                    ${escapeHtml(name ? `${code} - ${name}` : code)}
                </option>
            `;
        });

        return html;
    }

    function renderCurrencyOptions(selectedValue) {
        let html = `<option value="">Select currency</option>`;

        currencies.forEach(function (currency) {
            const code = firstValue(currency, ["curencyCode", "currencyCode", "code", "valueCode"]);
            const name = firstValue(currency, ["curencyName", "currencyName", "name", "valueName", "description"]);

            if (!code) {
                return;
            }

            html += `
                <option value="${escapeHtml(code)}" ${code === selectedValue ? "selected" : ""}>
                    ${escapeHtml(name ? `${code} - ${name}` : code)}
                </option>
            `;
        });

        return html;
    }

    function renderGLAccountOptions(selectedValue) {
        let html = `<option value="">Select G/L account</option>`;

        glAccounts.forEach(function (account) {
            const code = firstValue(account, ["glCode", "glAccountCode", "accountCode", "code", "valueCode"]);
            const name = firstValue(account, ["glName", "glAccountName", "accountName", "name", "valueName", "description"]);

            if (!code) {
                return;
            }

            html += `
                <option value="${escapeHtml(code)}" ${code === selectedValue ? "selected" : ""}>
                    ${escapeHtml(name ? `${code} - ${name}` : code)}
                </option>
            `;
        });

        return html;
    }

    function renderDimensionFields(requestId, selectedDimensions) {
        if (!groupedDimensions.length) {
            return `
                <div class="empty-message">
                    No dimensions configured.
                </div>
            `;
        }

        return groupedDimensions.map(function (dimension) {
            const code = dimension.dimensionCode || "";
            const normalizedCode = normalizeCode(code);
            const label = dimension.dimensionName || dimension.dimensionCode || "Dimension";
            const selectedValue =
                selectedDimensions[code] ||
                selectedDimensions[normalizedCode] ||
                "";

            return `
                <div class="form-group">
                    <label>${escapeHtml(label)}</label>
                    <select
                        class="finance-dimension-select"
                        data-request-id="${requestId}"
                        data-dimension-code="${escapeHtml(code)}"
                        data-normalized-dimension-code="${escapeHtml(normalizedCode)}">
                        ${renderDimensionValueOptions(dimension, selectedValue)}
                    </select>
                </div>
            `;
        }).join("");
    }

    function renderDimensionValueOptions(dimension, selectedValue) {
        let html = `<option value="">Select ${escapeHtml(dimension.dimensionName || dimension.dimensionCode || "dimension")}</option>`;

        const values = Array.isArray(dimension.values) ? dimension.values : [];

        values.forEach(function (value) {
            const code = firstValue(value, ["valueCode", "value_code", "code"]);
            const name = firstValue(value, ["valueName", "value_name", "name", "description"]);

            if (!code) {
                return;
            }

            html += `
                <option value="${escapeHtml(code)}" ${code === selectedValue ? "selected" : ""}>
                    ${escapeHtml(name ? `${code} - ${name}` : code)}
                </option>
            `;
        });

        return html;
    }

    function bindCardCalculation(card) {
        const inputs = card.querySelectorAll(".approval-quantity-input, .approval-unit-price-input");

        inputs.forEach(function (input) {
            input.addEventListener("input", function () {
                recalculateCardTotals(card);
            });

            input.addEventListener("change", function () {
                recalculateCardTotals(card);
            });
        });

        recalculateCardTotals(card);
    }

    function recalculateCardTotals(card) {
        let requestTotal = 0;

        card.querySelectorAll(".approval-edit-item").forEach(function (itemRow) {
            const quantityInput = itemRow.querySelector(".approval-quantity-input");
            const unitPriceInput = itemRow.querySelector(".approval-unit-price-input");
            const lineTotalElement = itemRow.querySelector(".approval-line-total");

            const quantity = numberValue(quantityInput ? quantityInput.value : 0);
            const unitPrice = numberValue(unitPriceInput ? unitPriceInput.value : 0);
            const lineTotal = quantity * unitPrice;

            requestTotal += lineTotal;

            if (lineTotalElement) {
                lineTotalElement.textContent = formatAmount(lineTotal);
            }
        });

        const requestId = card.dataset.requestId;
        const totalElement = card.querySelector(`[data-request-total="${requestId}"]`);

        if (totalElement) {
            totalElement.textContent = formatAmount(requestTotal);
        }
    }

    async function saveChanges(requestId) {
        const request = findRequest(requestId);

        if (!request) {
            showError("Request not found.");
            return false;
        }

        const level = normalizeRole(request.currentApprovalLevel || currentRole);
        const roleForEdition = currentRole === "ADMIN" ? level : currentRole;

        try {
            if (roleForEdition === "HOD" || roleForEdition === "DIRECTOR") {
                await saveQuantityChanges(requestId);
            } else if (roleForEdition === "FINANCE") {
                await saveFinanceChanges(requestId);
            }

            showSuccess("Changes saved successfully.");
            await loadPendingApprovals();

            return true;

        } catch (error) {
            console.error("Save changes error:", error);
            showError("Failed to save changes: " + error.message);
            return false;
        }
    }

    async function saveQuantityChanges(requestId) {
        const card = getRequestCard(requestId);
        const quantityInputs = card.querySelectorAll(".approval-quantity-input");

        for (const input of quantityInputs) {
            const itemId = input.dataset.itemId;
            const quantity = input.value || "0";

            const url =
                `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/items/${encodeURIComponent(itemId)}/quantity`
                + `?quantity=${encodeURIComponent(quantity)}`
                + `&updatedBy=${encodeURIComponent(getApproverName())}`
                + `&role=${encodeURIComponent(currentRole)}`;

            const response = await fetch(url, {
                method: "PUT",
                headers: {
                    "Accept": "application/json"
                }
            });

            const text = await response.text();

            if (!response.ok) {
                throw new Error(`HTTP ${response.status} - ${text}`);
            }
        }
    }

    async function saveFinanceChanges(requestId) {
        const card = getRequestCard(requestId);

        const budgetPlan = getCardValue(card, ".finance-budget-plan");
        const fundCode = getCardValue(card, ".finance-fund-code");
        const currencyCode = getCardValue(card, ".finance-currency-code");
        const glAccountNo = getCardValue(card, ".finance-gl-account");
        const dimensionValues = collectFinanceDimensions(card);

        const headerPayload = {
            updatedBy: getApproverName(),
            role: currentRole,
            budgetPlan: budgetPlan,
            fundCode: fundCode,
            currencyCode: currencyCode,
            glAccountNo: glAccountNo,
            glAccountCode: glAccountNo,
            dimensionValues: JSON.stringify(dimensionValues),
            dimensions: dimensionValues
        };

        await putJson(
            `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/finance-fields`,
            headerPayload
        );

        const itemRows = card.querySelectorAll(".approval-edit-item");

        for (const itemRow of itemRows) {
            const itemId = itemRow.dataset.itemId;
            const unitPriceInput = itemRow.querySelector(".approval-unit-price-input");
            const unitPrice = unitPriceInput ? unitPriceInput.value : "0";

            const itemPayload = {
                updatedBy: getApproverName(),
                role: currentRole,
                unitPrice: unitPrice,
                budgetPlan: budgetPlan,
                fundCode: fundCode,
                glAccountNo: glAccountNo,
                glAccountCode: glAccountNo,
                dimensionValues: JSON.stringify(dimensionValues),
                dimensions: dimensionValues
            };

            await putJson(
                `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/items/${encodeURIComponent(itemId)}/finance-fields`,
                itemPayload
            );
        }
    }

    async function approve(requestId) {
        const confirmed = await confirmDialog(
            "Approve request",
            "Do you want to save changes and approve this request?",
            "Approve"
        );

        if (!confirmed) {
            return;
        }

        const saved = await saveChanges(requestId);

        if (!saved) {
            return;
        }

        try {
            const url =
                `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/approve`
                + `?approvedBy=${encodeURIComponent(getApproverName())}`
                + `&role=${encodeURIComponent(currentRole)}`
                + `&comment=${encodeURIComponent("Approved from mobile approval workflow")}`;

            const response = await fetch(url, {
                method: "PUT",
                headers: {
                    "Accept": "application/json"
                }
            });

            const text = await response.text();

            if (!response.ok) {
                throw new Error(`HTTP ${response.status} - ${text}`);
            }

            showSuccess("Request approved successfully.");
            await loadPendingApprovals();

        } catch (error) {
            console.error("Approve error:", error);
            showError("Failed to approve request: " + error.message);
        }
    }

    async function reject(requestId) {
        const reason = await promptDialog(
            "Reject request",
            "Please enter the rejection reason.",
            "Reason for rejection...",
            "Reject"
        );

        if (reason === null) {
            return;
        }

        if (!String(reason).trim()) {
            showError("Rejection reason is required.");
            return;
        }

        try {
            const url =
                `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/reject`
                + `?rejectedBy=${encodeURIComponent(getApproverName())}`
                + `&role=${encodeURIComponent(currentRole)}`
                + `&reason=${encodeURIComponent(reason)}`;

            const response = await fetch(url, {
                method: "PUT",
                headers: {
                    "Accept": "application/json"
                }
            });

            const text = await response.text();

            if (!response.ok) {
                throw new Error(`HTTP ${response.status} - ${text}`);
            }

            showSuccess("Request rejected successfully.");
            await loadPendingApprovals();

        } catch (error) {
            console.error("Reject error:", error);
            showError("Failed to reject request: " + error.message);
        }
    }

    async function putJson(url, payload) {
        const response = await fetch(url, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const text = await response.text();

        if (!response.ok) {
            throw new Error(`HTTP ${response.status} - ${text}`);
        }

        return text ? JSON.parse(text) : null;
    }

    function collectFinanceDimensions(card) {
        const dimensions = {};

        card.querySelectorAll(".finance-dimension-select").forEach(function (select) {
            const originalCode = select.dataset.dimensionCode || "";
            const normalizedCode = select.dataset.normalizedDimensionCode || normalizeCode(originalCode);
            const value = select.value || "";

            if (!originalCode) {
                return;
            }

            dimensions[originalCode] = value;
            dimensions[normalizedCode] = value;
        });

        return dimensions;
    }

    function getRequestCard(requestId) {
        const card = document.querySelector(`.approval-card[data-request-id="${requestId}"]`);

        if (!card) {
            throw new Error("Approval card not found.");
        }

        return card;
    }

    function getCardValue(card, selector) {
        const element = card.querySelector(selector);

        if (!element) {
            return "";
        }

        return String(element.value || "").trim();
    }

    function findRequest(requestId) {
        return pendingRequests.find(function (request) {
            return String(request.id) === String(requestId);
        });
    }

    function parseDimensionValues(value) {
        if (!value) {
            return {};
        }

        if (typeof value === "object") {
            return value;
        }

        try {
            return JSON.parse(value);
        } catch (error) {
            return {};
        }
    }

    function setLoading(isLoading) {
        const loading = document.querySelector(".loading");

        if (loading) {
            loading.style.display = isLoading ? "block" : "none";
        }
    }

    function showError(message) {
        if (window.MobileDialog && typeof window.MobileDialog.error === "function") {
            window.MobileDialog.error("Error", message);
            return;
        }

        alert(message);
    }

    function showSuccess(message) {
        if (window.MobileDialog && typeof window.MobileDialog.success === "function") {
            window.MobileDialog.success("Success", message);
            return;
        }

        alert(message);
    }

    function confirmDialog(title, message, confirmText) {
        if (window.MobileDialog && typeof window.MobileDialog.confirm === "function") {
            return window.MobileDialog.confirm(title, message, confirmText || "Confirm");
        }

        return Promise.resolve(confirm(message));
    }

    function promptDialog(title, message, placeholder, confirmText) {
        if (window.MobileDialog && typeof window.MobileDialog.prompt === "function") {
            return window.MobileDialog.prompt(title, message, placeholder || "", confirmText || "Submit");
        }

        return Promise.resolve(prompt(message, ""));
    }

    function getApproverName() {
        return (
            currentUser.fullName ||
            currentUser.name ||
            currentUser.username ||
            currentUser.email ||
            "Mobile User"
        );
    }

    function normalizeRole(role) {
        return String(role || "")
            .trim()
            .toUpperCase()
            .replace(/\s+/g, "_");
    }

    function normalizeCode(code) {
        return String(code || "")
            .trim()
            .toUpperCase()
            .replace(/\s+/g, "_")
            .replace(/-/g, "_");
    }

    function toArray(data) {
        if (Array.isArray(data)) {
            return data;
        }

        if (!data || typeof data !== "object") {
            return [];
        }

        if (Array.isArray(data.data)) {
            return data.data;
        }

        if (Array.isArray(data.content)) {
            return data.content;
        }

        if (Array.isArray(data.items)) {
            return data.items;
        }

        if (Array.isArray(data.result)) {
            return data.result;
        }

        if (Array.isArray(data.results)) {
            return data.results;
        }

        return [];
    }

    function firstValue(object, keys) {
        if (!object || typeof object !== "object") {
            return "";
        }

        for (const key of keys) {
            if (Object.prototype.hasOwnProperty.call(object, key)) {
                const value = object[key];

                if (value !== null && value !== undefined && String(value).trim() !== "") {
                    return String(value).trim();
                }
            }
        }

        return "";
    }

    function numberValue(value) {
        const number = Number(value || 0);

        if (Number.isNaN(number)) {
            return 0;
        }

        return number;
    }

    function formatAmount(value) {
        return Number(value || 0).toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    }

    function escapeHtml(value) {
        return String(value || "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    window.ApprovalPage = {
        saveChanges: saveChanges,
        approve: approve,
        reject: reject,
        reload: loadPendingApprovals
    };
})();
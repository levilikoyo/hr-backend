/* =========================================================
   EMS-L Mobile - Approvals
   Full clean file
   Path: src/main/resources/static/mobile/js/approvals.js

   Logic:
   - HOD sees only requests addressed to their department
   - Finance sees all requests pending finance review
   - Director sees all requests pending director approval
   - HOD / Director can modify quantity
   - Finance can modify unit price and finance fields
   - Changes are saved automatically
   ========================================================= */

(function () {
    "use strict";

    let currentUser = null;
    let currentOrganization = "";
    let currentRole = "";
    let currentDepartment = "";
    let pendingRequests = [];

    let funds = [];
    let currencies = [];
    let glAccounts = [];
    let groupedDimensions = [];

    const autoSaveTimers = {};

    document.addEventListener("DOMContentLoaded", async function () {
        try {
            currentUser = requireLogin();
            currentOrganization = getCurrentOrganization();
            currentRole = normalizeRole(getCurrentUserRole());
            currentDepartment = getCurrentUserDepartment();

            displayCurrentUser();

            await loadReferenceData();
            await loadPendingApprovals();

        } catch (error) {
            console.error("Approvals initialization error:", error);
            showError("Failed to initialize approvals page: " + error.message);
        }
    });

    /* =========================================================
       INITIAL DATA
       ========================================================= */

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
            let url =
                `${BASE_URL}/api/needs-requests/pending-approval/${encodeURIComponent(currentOrganization)}/${encodeURIComponent(currentRole)}`;

            if (currentRole === "HOD") {
                url += `?department=${encodeURIComponent(currentDepartment)}`;
            }

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
                return !isExcludedFinanceDimension(dimension.dimensionCode);
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

    /* =========================================================
       RENDER APPROVALS
       ========================================================= */

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
                        <span class="label">Requester Dept.</span>
                        <strong>${escapeHtml(request.requesterDepartment || "-")}</strong>
                    </div>

                    <div>
                        <span class="label">Addressed Dept.</span>
                        <strong>${escapeHtml(request.addressedDepartment || request.department || "-")}</strong>
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

                <div class="approval-items receipt-items-list">
                    ${renderItems(request, roleForEdition)}
                </div>
            </div>

            <div class="approval-card-actions approval-card-actions-two">
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
            bindAutoSaveEvents(card, roleForEdition);
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

                <div class="auto-save-hint">
                    Changes are saved automatically.
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

        return items.map(function (item, index) {
            return renderItemLine(request, item, roleForEdition, index);
        }).join("");
    }

    function renderItemLine(request, item, roleForEdition, index) {
        const canEditQuantity = roleForEdition === "HOD" || roleForEdition === "DIRECTOR";
        const canEditUnitPrice = roleForEdition === "FINANCE";

        const quantity = numberValue(item.quantity);
        const unitPrice = numberValue(item.unitPrice);
        const total = quantity * unitPrice;
        const unit = item.unitOfMeasure || item.unit || "PCS";

        return `
            <div class="approval-receipt-item"
                 data-request-id="${request.id}"
                 data-item-id="${item.id}">

                <div class="approval-receipt-main-row">
                    <div class="approval-receipt-name">
                        <strong>${index + 1}. ${escapeHtml(item.itemName || "Item")}</strong>
                    </div>

                    <div class="approval-receipt-total">
                        <strong class="approval-line-total" data-request-id="${request.id}" data-item-id="${item.id}">
                            ${formatAmount(total)}
                        </strong>
                    </div>
                </div>

                <div class="approval-receipt-formula">
                    <span>Qty:</span>

                    ${
                        canEditQuantity
                            ? `
                                <input
                                    type="number"
                                    class="approval-inline-input approval-quantity-input"
                                    data-request-id="${request.id}"
                                    data-item-id="${item.id}"
                                    min="0"
                                    step="0.01"
                                    value="${quantity}">
                              `
                            : `
                                <span class="approval-static-value approval-formula-qty" data-item-id="${item.id}">
                                    ${formatPlainNumber(quantity)}
                                </span>
                              `
                    }

                    <span class="approval-formula-unit" data-item-id="${item.id}">
                        ${escapeHtml(unit)}
                    </span>

                    <span>×</span>

                    ${
                        canEditUnitPrice
                            ? `
                                <input
                                    type="number"
                                    class="approval-inline-input approval-unit-price-input"
                                    data-request-id="${request.id}"
                                    data-item-id="${item.id}"
                                    min="0"
                                    step="0.01"
                                    value="${unitPrice}">
                              `
                            : `
                                <span class="approval-static-value approval-formula-unit-price" data-item-id="${item.id}">
                                    ${formatAmount(unitPrice)}
                                </span>
                              `
                    }

                    <span class="auto-save-status" data-item-status="${item.id}"></span>
                </div>
            </div>
        `;
    }

    /* =========================================================
       SELECT OPTIONS
       ========================================================= */

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

    /* =========================================================
       AUTO SAVE
       ========================================================= */

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

    function bindAutoSaveEvents(card, roleForEdition) {
        if (roleForEdition === "HOD" || roleForEdition === "DIRECTOR") {
            const quantityInputs = card.querySelectorAll(".approval-quantity-input");

            quantityInputs.forEach(function (input) {
                input.addEventListener("input", function () {
                    scheduleQuantityAutoSave(input);
                });

                input.addEventListener("change", function () {
                    scheduleQuantityAutoSave(input);
                });
            });
        }

        if (roleForEdition === "FINANCE") {
            const financeFields = card.querySelectorAll(
                ".finance-budget-plan, .finance-fund-code, .finance-currency-code, .finance-gl-account, .finance-dimension-select"
            );

            financeFields.forEach(function (field) {
                field.addEventListener("input", function () {
                    scheduleFinanceHeaderAutoSave(card);
                });

                field.addEventListener("change", function () {
                    scheduleFinanceHeaderAutoSave(card);
                });
            });

            const unitPriceInputs = card.querySelectorAll(".approval-unit-price-input");

            unitPriceInputs.forEach(function (input) {
                input.addEventListener("input", function () {
                    scheduleUnitPriceAutoSave(input);
                });

                input.addEventListener("change", function () {
                    scheduleUnitPriceAutoSave(input);
                });
            });
        }
    }

    function scheduleQuantityAutoSave(input) {
        const requestId = input.dataset.requestId;
        const itemId = input.dataset.itemId;
        const key = `qty-${requestId}-${itemId}`;

        setItemAutoSaveStatus(itemId, "Saving...");

        clearTimeout(autoSaveTimers[key]);

        autoSaveTimers[key] = setTimeout(async function () {
            try {
                await updateQuantityDirectly(input);
                setItemAutoSaveStatus(itemId, "Saved");

            } catch (error) {
                console.error("Quantity auto-save error:", error);
                setItemAutoSaveStatus(itemId, "Error");
                showError("Failed to save quantity: " + error.message);
            }
        }, 700);
    }

    function scheduleUnitPriceAutoSave(input) {
        const requestId = input.dataset.requestId;
        const itemId = input.dataset.itemId;
        const key = `price-${requestId}-${itemId}`;

        setItemAutoSaveStatus(itemId, "Saving...");

        clearTimeout(autoSaveTimers[key]);

        autoSaveTimers[key] = setTimeout(async function () {
            try {
                await updateItemFinanceDirectly(input);
                setItemAutoSaveStatus(itemId, "Saved");

            } catch (error) {
                console.error("Unit price auto-save error:", error);
                setItemAutoSaveStatus(itemId, "Error");
                showError("Failed to save unit price: " + error.message);
            }
        }, 700);
    }

    function scheduleFinanceHeaderAutoSave(card) {
        const requestId = card.dataset.requestId;
        const key = `finance-header-${requestId}`;

        setFinanceAutoSaveStatus(card, "Saving...");

        clearTimeout(autoSaveTimers[key]);

        autoSaveTimers[key] = setTimeout(async function () {
            try {
                await updateFinanceHeaderDirectly(card);
                setFinanceAutoSaveStatus(card, "Saved automatically");

            } catch (error) {
                console.error("Finance header auto-save error:", error);
                setFinanceAutoSaveStatus(card, "Error");
                showError("Failed to save finance information: " + error.message);
            }
        }, 700);
    }

    async function updateQuantityDirectly(input) {
        const requestId = input.dataset.requestId;
        const itemId = input.dataset.itemId;
        const quantity = input.value || "0";

        const url =
            `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/items/${encodeURIComponent(itemId)}/quantity`
            + `?quantity=${encodeURIComponent(quantity)}`
            + `&updatedBy=${encodeURIComponent(getApproverName())}`
            + `&role=${encodeURIComponent(currentRole)}`
            + `&department=${encodeURIComponent(currentDepartment)}`;

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

        updateLocalRequestFromResponse(requestId, text);
    }

    async function updateFinanceHeaderDirectly(card) {
        const requestId = card.dataset.requestId;

        const payload = buildFinancePayload(card);

        const result = await putJson(
            `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/finance-fields`,
            payload
        );

        updateLocalRequestObject(requestId, result);
    }

    async function updateItemFinanceDirectly(input) {
        const requestId = input.dataset.requestId;
        const itemId = input.dataset.itemId;

        const card = getRequestCard(requestId);
        const payload = buildFinancePayload(card);

        payload.unitPrice = input.value || "0";

        const result = await putJson(
            `${BASE_URL}/api/needs-requests/${encodeURIComponent(requestId)}/items/${encodeURIComponent(itemId)}/finance-fields`,
            payload
        );

        updateLocalRequestObject(requestId, result);
    }

    function buildFinancePayload(card) {
        const dimensionValues = collectFinanceDimensions(card);
        const glAccountNo = getCardValue(card, ".finance-gl-account");

        return {
            updatedBy: getApproverName(),
            role: currentRole,
            budgetPlan: getCardValue(card, ".finance-budget-plan"),
            fundCode: getCardValue(card, ".finance-fund-code"),
            currencyCode: getCardValue(card, ".finance-currency-code"),
            glAccountNo: glAccountNo,
            glAccountCode: glAccountNo,
            dimensionValues: JSON.stringify(dimensionValues),
            dimensions: dimensionValues
        };
    }

    /* =========================================================
       APPROVE / REJECT
       ========================================================= */

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
            await updateQuantityDirectly(input);
        }
    }

    async function saveFinanceChanges(requestId) {
        const card = getRequestCard(requestId);

        await updateFinanceHeaderDirectly(card);

        const itemRows = card.querySelectorAll(".approval-receipt-item");

        for (const itemRow of itemRows) {
            const input = itemRow.querySelector(".approval-unit-price-input");

            if (input) {
                await updateItemFinanceDirectly(input);
            }
        }
    }

    async function approve(requestId) {
        const confirmed = await confirmDialog(
            "Approve request",
            "Do you want to approve this request?",
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
                + `&department=${encodeURIComponent(currentDepartment)}`
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
                + `&department=${encodeURIComponent(currentDepartment)}`
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

    /* =========================================================
       CALCULATIONS
       ========================================================= */

    function recalculateCardTotals(card) {
        let requestTotal = 0;

        card.querySelectorAll(".approval-receipt-item").forEach(function (itemRow) {
            const quantity = getItemQuantity(itemRow);
            const unitPrice = getItemUnitPrice(itemRow);
            const lineTotal = quantity * unitPrice;

            requestTotal += lineTotal;

            const lineTotalElement = itemRow.querySelector(".approval-line-total");

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

    function getItemQuantity(itemRow) {
        const input = itemRow.querySelector(".approval-quantity-input");

        if (input) {
            return numberValue(input.value);
        }

        const staticQty = itemRow.querySelector(".approval-formula-qty");

        if (staticQty) {
            return numberValue(staticQty.textContent);
        }

        return 0;
    }

    function getItemUnitPrice(itemRow) {
        const input = itemRow.querySelector(".approval-unit-price-input");

        if (input) {
            return numberValue(input.value);
        }

        const staticPrice = itemRow.querySelector(".approval-formula-unit-price");

        if (staticPrice) {
            return numberValue(String(staticPrice.textContent || "").replace(/,/g, ""));
        }

        return 0;
    }

    /* =========================================================
       STATUS HELPERS
       ========================================================= */

    function setItemAutoSaveStatus(itemId, message) {
        const element = document.querySelector(`[data-item-status="${itemId}"]`);

        if (!element) {
            return;
        }

        element.textContent = message || "";

        if (message === "Saved") {
            setTimeout(function () {
                element.textContent = "";
            }, 1200);
        }
    }

    function setFinanceAutoSaveStatus(card, message) {
        const element = card.querySelector(".auto-save-hint");

        if (!element) {
            return;
        }

        element.textContent = message || "Changes are saved automatically.";

        if (message === "Saved automatically") {
            setTimeout(function () {
                element.textContent = "Changes are saved automatically.";
            }, 1300);
        }
    }

    function updateLocalRequestFromResponse(requestId, text) {
        if (!text) {
            return;
        }

        try {
            const data = JSON.parse(text);
            updateLocalRequestObject(requestId, data);
        } catch (error) {
            console.warn("Could not parse update response:", error);
        }
    }

    function updateLocalRequestObject(requestId, updatedRequest) {
        if (!updatedRequest || !updatedRequest.id) {
            return;
        }

        pendingRequests = pendingRequests.map(function (request) {
            if (String(request.id) === String(requestId)) {
                return updatedRequest;
            }

            return request;
        });
    }

    /* =========================================================
       GENERAL HELPERS
       ========================================================= */

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

    function getCurrentUserDepartment() {
        return (
            currentUser.department ||
            currentUser.departement ||
            currentUser.costCenter ||
            currentUser.cost_center ||
            ""
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

    function isExcludedFinanceDimension(code) {
        const normalizedCode = normalizeCode(code);

        return (
            normalizedCode === "FUND" ||
            normalizedCode === "DEPARTMENT" ||
            normalizedCode === "DEPARTMENTS" ||
            normalizedCode === "DEPARTEMENT" ||
            normalizedCode === "DEPARTEMENTS" ||
            normalizedCode === "COST_CENTER" ||
            normalizedCode === "COSTCENTRE" ||
            normalizedCode === "COST_CENTRE"
        );
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

    function formatPlainNumber(value) {
        const number = numberValue(value);

        if (Number.isInteger(number)) {
            return String(number);
        }

        return String(number);
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
        approve: approve,
        reject: reject,
        reload: loadPendingApprovals
    };
})();
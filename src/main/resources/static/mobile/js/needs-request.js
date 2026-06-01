/* =========================================================
   EMS-L Mobile - Needs Request
   Full clean file
   Path: src/main/resources/static/mobile/js/needs-request.js
   ========================================================= */

let currentUser = null;
let currentOrganization = "";
let dynamicDimensions = [];
let allGroupedDimensions = [];

/* =========================================================
   Page initialization
   ========================================================= */

document.addEventListener("DOMContentLoaded", async function () {
    try {
        currentUser = requireLogin();
        currentOrganization = getCurrentOrganization();

        if (!currentOrganization) {
            showError("Organization not found. Please login again.");
            return;
        }

        displayCurrentUser();

        setDefaultRequestDate();
        bindEvents();

        await loadGroupedDimensions();
        await loadFunds();
        await loadCurrencies();
        await loadGLAccounts();
        renderDynamicDimensions();

        ensureAtLeastOneItem();

    } catch (error) {
        console.error("Needs request initialization error:", error);
        showError(error.message || "Failed to initialize needs request page.");
    }
});

/* =========================================================
   Events
   ========================================================= */

function bindEvents() {
    const addItemBtn = findElement(
        "addItemBtn",
        "btnAddItem",
        "add-item-btn",
        "addItemButton"
    );

    if (addItemBtn) {
        addItemBtn.addEventListener("click", function (event) {
            event.preventDefault();
            addItemRow();
        });
    }

    const form = findElement(
        "needsRequestForm",
        "requestForm",
        "needRequestForm",
        "needsForm"
    );

    if (form) {
        form.addEventListener("submit", async function (event) {
            event.preventDefault();
            await submitNeedsRequest();
        });
    }

    const cancelBtn = findElement("cancelBtn", "btnCancel", "cancelButton");

    if (cancelBtn) {
        cancelBtn.addEventListener("click", function () {
            window.location.href = "index.html";
        });
    }
}

/* =========================================================
   Grouped dimensions
   ========================================================= */

async function loadGroupedDimensions() {
    try {
        const response = await fetch(
            `${BASE_URL}/api/dimension-setups/organization/${encodeURIComponent(currentOrganization)}/grouped`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status} - ${errorText}`);
        }

        const data = await response.json();

        if (!Array.isArray(data)) {
            allGroupedDimensions = [];
            dynamicDimensions = [];
            return;
        }

        allGroupedDimensions = data;

        dynamicDimensions = data.filter(function (dimension) {
            const normalizedCode = normalizeDimensionCode(dimension.dimensionCode);

            /*
               FUND is already fixed on the form.
               If the funds table is empty, we use this FUND dimension as fallback
               inside loadFunds().
            */
            return normalizedCode !== "FUND";
        });

    } catch (error) {
        console.error("Grouped dimensions loading error:", error);
        showError(`Failed to load dimensions: ${error.message}`);
        allGroupedDimensions = [];
        dynamicDimensions = [];
    }
}

/* =========================================================
   Fixed dropdowns
   ========================================================= */

async function loadFunds() {
    const select = findElement(
        "fundCode",
        "fundSelect",
        "fund_code",
        "fund",
        "funds",
        "requestFund"
    );

    if (!select) {
        console.warn("Fund dropdown not found in HTML.");
        return;
    }

    clearSelect(select, "Select fund");

    let loaded = false;

    try {
        const response = await fetch(
            `${BASE_URL}/api/funds/organization/${encodeURIComponent(currentOrganization)}`
        );

        if (response.ok) {
            const funds = await response.json();

            if (Array.isArray(funds) && funds.length > 0) {
                funds.forEach(function (fund) {
                    const code =
                        fund.fundCode ||
                        fund.code ||
                        fund.valueCode ||
                        "";

                    const name =
                        fund.fundName ||
                        fund.name ||
                        fund.valueName ||
                        "";

                    if (!code) {
                        return;
                    }

                    addOption(select, code, name ? `${code} - ${name}` : code);
                });

                loaded = true;
            }
        } else {
            console.warn("Funds endpoint failed:", response.status, await response.text());
        }

    } catch (error) {
        console.warn("Funds endpoint error:", error);
    }

    /*
       Fallback: use FUND dimension values if funds table has no data.
       Your grouped dimensions already contain:
       dimensionCode: FUND
       values: CONTROL, LRF26, etc.
    */
    if (!loaded) {
        const fundDimension = findDimensionByCode("FUND");

        if (fundDimension && Array.isArray(fundDimension.values)) {
            fundDimension.values.forEach(function (value) {
                const code = value.valueCode || "";
                const name = value.valueName || "";

                if (!code) {
                    return;
                }

                addOption(select, code, name ? `${code} - ${name}` : code);
            });

            loaded = fundDimension.values.length > 0;
        }
    }

    if (!loaded) {
        addDisabledOption(select, "No fund found");
    }
}

async function loadCurrencies() {
    const select = findElement(
        "currencyCode",
        "curencyCode",
        "currencySelect",
        "currency_code",
        "curency_code",
        "currency",
        "devise",
        "deviseCode"
    );

    if (!select) {
        console.warn("Currency dropdown not found in HTML.");
        return;
    }

    clearSelect(select, "Select currency");

    let loaded = false;

    try {
        const response = await fetch(
            `${BASE_URL}/api/currencies/organization/${encodeURIComponent(currentOrganization)}`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status} - ${errorText}`);
        }

        const currencies = await response.json();

        if (Array.isArray(currencies) && currencies.length > 0) {
            currencies.forEach(function (currency) {
                const code =
                    currency.curencyCode ||
                    currency.currencyCode ||
                    currency.code ||
                    currency.valueCode ||
                    "";

                const name =
                    currency.curencyName ||
                    currency.currencyName ||
                    currency.name ||
                    currency.valueName ||
                    "";

                if (!code) {
                    return;
                }

                addOption(select, code, name ? `${code} - ${name}` : code);
            });

            loaded = true;
        }

    } catch (error) {
        console.error("Currencies loading error:", error);
        showError(`Failed to load currencies: ${error.message}`);
    }

    if (!loaded) {
        addDisabledOption(select, "No currency found");
    }
}

async function loadGLAccounts() {
    const select = findElement(
        "glAccountCode",
        "glAccountSelect",
        "gl_account_code",
        "glAccount",
        "gl_account",
        "accountCode",
        "account_code"
    );

    if (!select) {
        console.warn("G/L account dropdown not found in HTML.");
        return;
    }

    clearSelect(select, "Select G/L account");

    let loaded = false;

    const urls = [
        `${BASE_URL}/api/gl-accounts/organization/${encodeURIComponent(currentOrganization)}`,
        `${BASE_URL}/api/glaccounts/organization/${encodeURIComponent(currentOrganization)}`,
        `${BASE_URL}/api/gl-account/organization/${encodeURIComponent(currentOrganization)}`
    ];

    for (const url of urls) {
        try {
            const response = await fetch(url);

            if (!response.ok) {
                continue;
            }

            const accounts = await response.json();

            if (!Array.isArray(accounts) || accounts.length === 0) {
                continue;
            }

            accounts.forEach(function (account) {
                const code =
                    account.glCode ||
                    account.glAccountCode ||
                    account.accountCode ||
                    account.code ||
                    account.valueCode ||
                    "";

                const name =
                    account.glName ||
                    account.glAccountName ||
                    account.accountName ||
                    account.name ||
                    account.valueName ||
                    "";

                if (!code) {
                    return;
                }

                addOption(select, code, name ? `${code} - ${name}` : code);
            });

            loaded = true;
            break;

        } catch (error) {
            console.warn("G/L account endpoint failed:", url, error);
        }
    }

    if (!loaded) {
        addDisabledOption(select, "No G/L account found");
    }
}

/* =========================================================
   Render dynamic dimensions
   ========================================================= */

function renderDynamicDimensions() {
    const container = findElement(
        "dynamicDimensionsContainer",
        "dimensionsContainer",
        "dynamic_dimensions_container"
    );

    if (!container) {
        console.warn("Dynamic dimensions container not found in HTML.");
        return;
    }

    container.innerHTML = "";

    if (!Array.isArray(dynamicDimensions) || dynamicDimensions.length === 0) {
        container.innerHTML = createEmptyMessage("No dimensions configured for this organization.");
        return;
    }

    dynamicDimensions.forEach(function (dimension) {
        container.appendChild(createDimensionField(dimension));
    });
}

function createDimensionField(dimension) {
    const wrapper = document.createElement("div");
    wrapper.className = "form-group dynamic-dimension-group";

    const label = document.createElement("label");
    label.textContent = dimension.dimensionName || dimension.dimensionCode || "Dimension";

    const normalizedCode = normalizeDimensionCode(dimension.dimensionCode);

    if (dimension.required === true) {
        const requiredMark = document.createElement("span");
        requiredMark.textContent = " *";
        requiredMark.className = "required-mark";
        label.appendChild(requiredMark);
    }

    const select = document.createElement("select");
    select.className = "form-control dynamic-dimension-select";
    select.dataset.dimensionCode = dimension.dimensionCode || "";
    select.dataset.normalizedDimensionCode = normalizedCode;

    if (dimension.required === true) {
        select.required = true;
    }

    addOption(
        select,
        "",
        `Select ${dimension.dimensionName || dimension.dimensionCode || "dimension"}`
    );

    const values = Array.isArray(dimension.values) ? dimension.values : [];

    values.forEach(function (value) {
        const valueCode = value.valueCode || "";
        const valueName = value.valueName || "";

        if (!valueCode) {
            return;
        }

        addOption(select, valueCode, valueName ? `${valueCode} - ${valueName}` : valueCode);
    });

    wrapper.appendChild(label);
    wrapper.appendChild(select);

    return wrapper;
}

/* =========================================================
   Items management
   ========================================================= */

function ensureAtLeastOneItem() {
    const container = findElement(
        "itemsContainer",
        "requestItemsContainer",
        "itemsList"
    );

    if (!container) {
        return;
    }

    const existingItems = container.querySelectorAll(".request-item");

    if (existingItems.length === 0) {
        addItemRow();
    }
}

function addItemRow() {
    const container = findElement(
        "itemsContainer",
        "requestItemsContainer",
        "itemsList"
    );

    if (!container) {
        console.warn("Items container not found in HTML.");
        return;
    }

    const index = container.querySelectorAll(".request-item").length + 1;

    const item = document.createElement("div");
    item.className = "request-item";
    item.dataset.index = String(index);

    item.innerHTML = `
        <div class="item-header">
            <strong>Item ${index}</strong>
            <button type="button" class="btn-remove-item" onclick="removeItemRow(this)">Remove</button>
        </div>

        <div class="form-group">
            <label>Description *</label>
            <input type="text" class="form-control item-description" placeholder="Item description" required>
        </div>

        <div class="form-row">
            <div class="form-group">
                <label>Quantity *</label>
                <input type="number" class="form-control item-quantity" min="1" step="1" value="1" required>
            </div>

            <div class="form-group">
                <label>Unit Cost</label>
                <input type="number" class="form-control item-unit-cost" min="0" step="0.01" value="0">
            </div>
        </div>

        <div class="form-group">
            <label>Remarks</label>
            <textarea class="form-control item-remarks" rows="2" placeholder="Optional remarks"></textarea>
        </div>
    `;

    container.appendChild(item);
}

function removeItemRow(button) {
    const container = findElement(
        "itemsContainer",
        "requestItemsContainer",
        "itemsList"
    );

    const item = button.closest(".request-item");

    if (!container || !item) {
        return;
    }

    const totalItems = container.querySelectorAll(".request-item").length;

    if (totalItems <= 1) {
        showError("At least one item is required.");
        return;
    }

    item.remove();
    renumberItems();
}

function renumberItems() {
    const items = document.querySelectorAll(".request-item");

    items.forEach(function (item, index) {
        item.dataset.index = String(index + 1);

        const header = item.querySelector(".item-header strong");
        if (header) {
            header.textContent = `Item ${index + 1}`;
        }
    });
}

/* =========================================================
   Submit request
   ========================================================= */

async function submitNeedsRequest() {
    try {
        const payload = buildPayload();

        if (!payload) {
            return;
        }

        setSubmitLoading(true);

        const response = await fetch(`${BASE_URL}/api/needs-requests`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status} - ${errorText}`);
        }

        await response.json();

        showSuccess("Needs request submitted successfully.");

        setTimeout(function () {
            window.location.href = "my-requests.html";
        }, 800);

    } catch (error) {
        console.error("Submit needs request error:", error);
        showError(`Failed to submit request: ${error.message}`);
    } finally {
        setSubmitLoading(false);
    }
}

function buildPayload() {
    const title = getValue(
        "title",
        "requestTitle",
        "needTitle",
        "needsTitle"
    );

    const description = getValue(
        "description",
        "requestDescription",
        "needDescription",
        "needsDescription"
    );

    const requestDate = getValue(
        "requestDate",
        "date",
        "needDate",
        "needsDate"
    );

    const priority = getValue(
        "priority",
        "requestPriority",
        "needPriority",
        "needsPriority"
    );

    const budgetPlan = getValue(
        "budgetPlan",
        "budget_plan",
        "budgetPlanCode"
    );

    const fundCode = getValue(
        "fundCode",
        "fundSelect",
        "fund_code",
        "fund",
        "funds",
        "requestFund"
    );

    const currencyCode = getValue(
        "currencyCode",
        "curencyCode",
        "currencySelect",
        "currency_code",
        "curency_code",
        "currency",
        "devise",
        "deviseCode"
    );

    const glAccountCode = getValue(
        "glAccountCode",
        "glAccountSelect",
        "gl_account_code",
        "glAccount",
        "gl_account",
        "accountCode",
        "account_code"
    );

    const dimensionObject = collectDynamicDimensions();
    const department = deriveDepartmentFromDimensions(dimensionObject);

    if (!title) {
        showError("Please enter request title.");
        return null;
    }

    if (!requestDate) {
        showError("Please select request date.");
        return null;
    }

    if (!fundCode) {
        showError("Please select fund.");
        return null;
    }

    if (!currencyCode) {
        showError("Please select currency.");
        return null;
    }

    if (!glAccountCode) {
        showError("Please select G/L account.");
        return null;
    }

    const missingRequiredDimension = validateRequiredDimensions();

    if (missingRequiredDimension) {
        showError(`Please select ${missingRequiredDimension}.`);
        return null;
    }

    const items = collectItems();

    if (items.length === 0) {
        showError("Please add at least one item.");
        return null;
    }

    const requesterName =
        currentUser.fullName ||
        currentUser.name ||
        currentUser.username ||
        currentUser.email ||
        "";

    return {
        organization: currentOrganization,

        title: title,
        description: description,
        requestDate: requestDate,
        priority: priority || "NORMAL",
        budgetPlan: budgetPlan,

        requesterId: currentUser.id || null,
        requesterName: requesterName,
        requesterEmail: currentUser.email || "",
        requesterRole: currentUser.role || "",

        department: department,

        fundCode: fundCode,
        currencyCode: currencyCode,
        glAccountCode: glAccountCode,

        dimensionValues: JSON.stringify(dimensionObject),

        status: "PENDING_HOD",
        items: items
    };
}

function collectItems() {
    const rows = document.querySelectorAll(".request-item");
    const items = [];

    rows.forEach(function (row) {
        const descriptionInput = row.querySelector(".item-description");
        const quantityInput = row.querySelector(".item-quantity");
        const unitCostInput = row.querySelector(".item-unit-cost");
        const remarksInput = row.querySelector(".item-remarks");

        const description = descriptionInput ? descriptionInput.value.trim() : "";
        const quantity = quantityInput ? Number(quantityInput.value || 0) : 0;
        const unitCost = unitCostInput ? Number(unitCostInput.value || 0) : 0;
        const remarks = remarksInput ? remarksInput.value.trim() : "";

        if (!description) {
            return;
        }

        items.push({
            organization: currentOrganization,
            description: description,
            quantity: quantity,
            unitCost: unitCost,
            totalCost: quantity * unitCost,
            remarks: remarks,

            fundCode: getValue(
                "fundCode",
                "fundSelect",
                "fund_code",
                "fund",
                "funds",
                "requestFund"
            ),

            currencyCode: getValue(
                "currencyCode",
                "curencyCode",
                "currencySelect",
                "currency_code",
                "curency_code",
                "currency",
                "devise",
                "deviseCode"
            ),

            glAccountCode: getValue(
                "glAccountCode",
                "glAccountSelect",
                "gl_account_code",
                "glAccount",
                "gl_account",
                "accountCode",
                "account_code"
            ),

            dimensionValues: JSON.stringify(collectDynamicDimensions())
        });
    });

    return items;
}

/* =========================================================
   Dynamic dimension helpers
   ========================================================= */

function collectDynamicDimensions() {
    const dimensionValues = {};

    document.querySelectorAll(".dynamic-dimension-select").forEach(function (select) {
        const originalCode = select.dataset.dimensionCode || "";
        const normalizedCode = select.dataset.normalizedDimensionCode || normalizeDimensionCode(originalCode);
        const value = select.value || "";

        if (!originalCode) {
            return;
        }

        dimensionValues[originalCode] = value;
        dimensionValues[normalizedCode] = value;
    });

    return dimensionValues;
}

function validateRequiredDimensions() {
    for (const dimension of dynamicDimensions) {
        if (dimension.required !== true) {
            continue;
        }

        const code = dimension.dimensionCode || "";
        const normalizedCode = normalizeDimensionCode(code);

        const select = document.querySelector(
            `.dynamic-dimension-select[data-normalized-dimension-code="${normalizedCode}"]`
        );

        if (select && !select.value) {
            return dimension.dimensionName || dimension.dimensionCode || "required dimension";
        }
    }

    return "";
}

function deriveDepartmentFromDimensions(dimensionObject) {
    return (
        dimensionObject["COST CENTER"] ||
        dimensionObject["COST_CENTER"] ||
        dimensionObject["DEPARTMENT"] ||
        dimensionObject["DEPARTMENTS"] ||
        dimensionObject["DEPARTEMENTS"] ||
        ""
    );
}

function findDimensionByCode(code) {
    const normalizedTarget = normalizeDimensionCode(code);

    return allGroupedDimensions.find(function (dimension) {
        return normalizeDimensionCode(dimension.dimensionCode) === normalizedTarget;
    });
}

function normalizeDimensionCode(code) {
    return String(code || "")
        .trim()
        .toUpperCase()
        .replace(/\s+/g, "_")
        .replace(/-/g, "_");
}

/* =========================================================
   UI helpers
   ========================================================= */

function setDefaultRequestDate() {
    const dateInput = findElement(
        "requestDate",
        "date",
        "needDate",
        "needsDate"
    );

    if (!dateInput) {
        return;
    }

    if (!dateInput.value) {
        const today = new Date();
        dateInput.value = today.toISOString().split("T")[0];
    }
}

function setSubmitLoading(isLoading) {
    const submitBtn = findElement(
        "submitBtn",
        "btnSubmit",
        "submitRequestBtn",
        "saveBtn"
    );

    if (!submitBtn) {
        return;
    }

    submitBtn.disabled = isLoading;
    submitBtn.textContent = isLoading ? "Submitting..." : "Submit Request";
}

function clearSelect(select, placeholder) {
    select.innerHTML = "";
    addOption(select, "", placeholder || "Select");
}

function addOption(select, value, text) {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = text;
    select.appendChild(option);
}

function addDisabledOption(select, text) {
    const option = document.createElement("option");
    option.value = "";
    option.textContent = text;
    option.disabled = true;
    select.appendChild(option);
}

function createEmptyMessage(message) {
    return `
        <div class="empty-message">
            ${escapeHtml(message)}
        </div>
    `;
}

function showError(message) {
    if (window.Swal) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: message
        });
        return;
    }

    alert(message);
}

function showSuccess(message) {
    if (window.Swal) {
        Swal.fire({
            icon: "success",
            title: "Success",
            text: message,
            timer: 1500,
            showConfirmButton: false
        });
        return;
    }

    alert(message);
}

function findElement() {
    for (let i = 0; i < arguments.length; i++) {
        const key = arguments[i];

        if (!key) {
            continue;
        }

        const byId = document.getElementById(key);
        if (byId) {
            return byId;
        }

        const byName = document.querySelector(`[name="${key}"]`);
        if (byName) {
            return byName;
        }
    }

    return null;
}

function getValue() {
    const element = findElement.apply(null, arguments);

    if (!element) {
        return "";
    }

    return String(element.value || "").trim();
}

function escapeHtml(value) {
    return String(value || "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
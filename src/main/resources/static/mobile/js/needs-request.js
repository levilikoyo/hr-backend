/* =========================================================
   EMS-L Mobile - Needs Request
   Full clean file
   Path: src/main/resources/static/mobile/js/needs-request.js
   ========================================================= */

let currentUser = null;
let currentOrganization = "";
let allGroupedDimensions = [];
let dynamicDimensions = [];

/* =========================================================
   Page initialization
   ========================================================= */

document.addEventListener("DOMContentLoaded", async function () {
    try {
        currentUser = requireLogin();
        currentOrganization = getCurrentOrganization();

        console.log("Current user:", currentUser);
        console.log("Current organization:", currentOrganization);

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
        ["addItemBtn", "btnAddItem", "add-item-btn", "addItemButton"],
        ["Add Item", "Add item", "Ajouter"]
    );

    if (addItemBtn) {
        addItemBtn.addEventListener("click", function (event) {
            event.preventDefault();
            addItemRow();
        });
    }

    const form = findElement(
        ["needsRequestForm", "requestForm", "needRequestForm", "needsForm"],
        []
    );

    if (form) {
        form.addEventListener("submit", async function (event) {
            event.preventDefault();
            await submitNeedsRequest();
        });
    }

    const cancelBtn = findElement(
        ["cancelBtn", "btnCancel", "cancelButton"],
        ["Cancel", "Annuler"]
    );

    if (cancelBtn) {
        cancelBtn.addEventListener("click", function () {
            window.location.href = "index.html";
        });
    }
}

/* =========================================================
   API helpers
   ========================================================= */

async function fetchJson(url) {
    console.log("Fetching:", url);

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

    if (!text) {
        return [];
    }

    try {
        return JSON.parse(text);
    } catch (error) {
        console.error("Invalid JSON response:", text);
        throw new Error("Invalid JSON response from server.");
    }
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

/* =========================================================
   Grouped dimensions
   ========================================================= */

async function loadGroupedDimensions() {
    try {
        const url =
            `${BASE_URL}/api/dimension-setups/organization/${encodeURIComponent(currentOrganization)}/grouped`;

        const data = await fetchJson(url);
        const dimensions = toArray(data);

        console.log("Grouped dimensions:", dimensions);

        allGroupedDimensions = dimensions;

        dynamicDimensions = dimensions.filter(function (dimension) {
            const normalizedCode = normalizeDimensionCode(dimension.dimensionCode);
            return normalizedCode !== "FUND";
        });

    } catch (error) {
        console.error("Grouped dimensions loading error:", error);
        allGroupedDimensions = [];
        dynamicDimensions = [];
        showError(`Failed to load dimensions: ${error.message}`);
    }
}

/* =========================================================
   Fixed dropdowns
   ========================================================= */

async function loadFunds() {
    const select = findSelect(
        ["fundCode", "fundSelect", "fund_code", "fund", "funds", "requestFund"],
        ["Fund", "Fonds"]
    );

    if (!select) {
        console.warn("Fund dropdown not found in HTML.");
        return;
    }

    clearSelect(select, "Select fund");

    let loaded = false;

    try {
        const url =
            `${BASE_URL}/api/funds/organization/${encodeURIComponent(currentOrganization)}`;

        const data = await fetchJson(url);
        const funds = toArray(data);

        console.log("Funds response:", funds);

        funds.forEach(function (fund) {
            const code = firstValue(fund, [
                "fundCode",
                "fund_code",
                "code",
                "valueCode",
                "value_code"
            ]);

            const name = firstValue(fund, [
                "fundName",
                "fund_name",
                "name",
                "valueName",
                "value_name",
                "description"
            ]);

            if (!code) {
                return;
            }

            addOption(select, code, name ? `${code} - ${name}` : code);
            loaded = true;
        });

    } catch (error) {
        console.warn("Funds endpoint error:", error);
    }

    if (!loaded) {
        const fundDimension = findDimensionByCode("FUND");

        if (fundDimension && Array.isArray(fundDimension.values)) {
            console.log("Using FUND dimension fallback:", fundDimension.values);

            fundDimension.values.forEach(function (value) {
                const code = firstValue(value, ["valueCode", "value_code", "code"]);
                const name = firstValue(value, ["valueName", "value_name", "name", "description"]);

                if (!code) {
                    return;
                }

                addOption(select, code, name ? `${code} - ${name}` : code);
                loaded = true;
            });
        }
    }

    if (!loaded) {
        addDisabledOption(select, "No fund found");
    }
}

async function loadCurrencies() {
    const select = findSelect(
        [
            "currencyCode",
            "curencyCode",
            "currencySelect",
            "currency_code",
            "curency_code",
            "currency",
            "curency",
            "devise",
            "deviseCode"
        ],
        ["Currency", "Curency", "Devise"]
    );

    if (!select) {
        console.warn("Currency dropdown not found in HTML.");
        return;
    }

    clearSelect(select, "Select currency");

    let loaded = false;

    try {
        const url =
            `${BASE_URL}/api/currencies/organization/${encodeURIComponent(currentOrganization)}`;

        const data = await fetchJson(url);
        const currencies = toArray(data);

        console.log("Currencies response:", currencies);

        currencies.forEach(function (currency) {
            const code = firstValue(currency, [
                "curencyCode",
                "currencyCode",
                "curency_code",
                "currency_code",
                "code",
                "valueCode",
                "value_code"
            ]);

            const name = firstValue(currency, [
                "curencyName",
                "currencyName",
                "curency_name",
                "currency_name",
                "name",
                "valueName",
                "value_name",
                "description"
            ]);

            if (!code) {
                return;
            }

            addOption(select, code, name ? `${code} - ${name}` : code);
            loaded = true;
        });

    } catch (error) {
        console.error("Currencies loading error:", error);
        showError(`Failed to load currencies: ${error.message}`);
    }

    if (!loaded) {
        addDisabledOption(select, "No currency found");
    }
}

async function loadGLAccounts() {
    const select = findSelect(
        [
            "glAccountCode",
            "glAccountSelect",
            "gl_account_code",
            "glAccount",
            "gl_account",
            "accountCode",
            "account_code",
            "glCode",
            "gLAccount"
        ],
        ["G/L Account", "GL Account", "G/L", "Account"]
    );

    if (!select) {
        console.warn("G/L account dropdown not found in HTML.");
        return;
    }

    clearSelect(select, "Select G/L account");

    let loaded = false;

    const urls = [
        `${BASE_URL}/api/gl-accounts/organization/${encodeURIComponent(currentOrganization)}`
    ];

    for (const url of urls) {
        try {
            const data = await fetchJson(url);
            const accounts = toArray(data);

            console.log("G/L accounts response from:", url, accounts);

            if (!Array.isArray(accounts) || accounts.length === 0) {
                continue;
            }

            accounts.forEach(function (account) {
                const code = firstValue(account, [
                    "glCode",
                    "glAccountCode",
                    "accountCode",
                    "gl_code",
                    "gl_account_code",
                    "account_code",
                    "code",
                    "valueCode",
                    "value_code"
                ]);

                const name = firstValue(account, [
                    "glName",
                    "glAccountName",
                    "accountName",
                    "gl_name",
                    "gl_account_name",
                    "account_name",
                    "name",
                    "valueName",
                    "value_name",
                    "description"
                ]);

                if (!code) {
                    return;
                }

                addOption(select, code, name ? `${code} - ${name}` : code);
                loaded = true;
            });

            if (loaded) {
                break;
            }

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
        ["dynamicDimensionsContainer", "dimensionsContainer", "dynamic_dimensions_container"],
        ["Dimensions"]
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

    addOption(select, "", `Select ${dimension.dimensionName || dimension.dimensionCode || "dimension"}`);

    const values = Array.isArray(dimension.values) ? dimension.values : [];

    values.forEach(function (value) {
        const valueCode = firstValue(value, ["valueCode", "value_code", "code"]);
        const valueName = firstValue(value, ["valueName", "value_name", "name", "description"]);

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
        ["itemsContainer", "requestItemsContainer", "itemsList"],
        ["Items"]
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
        ["itemsContainer", "requestItemsContainer", "itemsList"],
        ["Items"]
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
            <label>Item Name *</label>
            <input type="text" class="form-control item-name" placeholder="Item name" required>
        </div>

        <div class="form-group">
            <label>Description</label>
            <input type="text" class="form-control item-description" placeholder="Item description">
        </div>

        <div class="form-row">
            <div class="form-group">
                <label>Quantity *</label>
                <input type="number" class="form-control item-quantity" min="1" step="1" value="1" required>
            </div>

            <div class="form-group">
                <label>Unit Price</label>
                <input type="number" class="form-control item-unit-price" min="0" step="0.01" value="0">
            </div>
        </div>

        <div class="form-group">
            <label>Unit of Measure</label>
            <input type="text" class="form-control item-unit-measure" placeholder="PCS, Month, Day..." value="PCS">
        </div>

        <div class="form-group">
            <label>Item Category</label>
            <input type="text" class="form-control item-category" placeholder="Optional category">
        </div>
    `;

    container.appendChild(item);
}

function removeItemRow(button) {
    const container = findElement(
        ["itemsContainer", "requestItemsContainer", "itemsList"],
        ["Items"]
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

        console.log("Submitting needs request payload:", payload);

        setSubmitLoading(true);

        const response = await fetch(`${BASE_URL}/api/needs-requests`, {
            method: "POST",
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
    const title = getValue(["title", "requestTitle", "needTitle", "needsTitle"], ["Title", "Request Title"]);
    const description = getValue(["description", "requestDescription", "needDescription", "needsDescription"], ["Description"]);
    const requestDate = getValue(["requestDate", "date", "needDate", "needsDate"], ["Date", "Request Date"]);
    const priority = getValue(["priority", "requestPriority", "needPriority", "needsPriority"], ["Priority"]);
    const budgetPlan = getValue(["budgetPlan", "budget_plan", "budgetPlanCode"], ["Budget Plan"]);

    const fundCode = getValue(["fundCode", "fundSelect", "fund_code", "fund", "funds", "requestFund"], ["Fund", "Fonds"]);

    const currencyCode = getValue(
        ["currencyCode", "curencyCode", "currencySelect", "currency_code", "curency_code", "currency", "curency", "devise", "deviseCode"],
        ["Currency", "Curency", "Devise"]
    );

    const glAccountCode = getValue(
        ["glAccountCode", "glAccountSelect", "gl_account_code", "glAccount", "gl_account", "accountCode", "account_code", "glCode"],
        ["G/L Account", "GL Account", "G/L", "Account"]
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

        requestedBy: requesterName,
        requesterName: requesterName,
        requesterEmail: currentUser.email || "",
        requesterRole: currentUser.role || "",

        department: department,

        fundCode: fundCode,
        currencyCode: currencyCode,

        glAccountNo: glAccountCode,
        glAccountCode: glAccountCode,

        dimensionValues: JSON.stringify(dimensionObject),

        status: "PENDING_HOD",
        currentApprovalLevel: "HOD",

        items: items
    };
}

function collectItems() {
    const rows = document.querySelectorAll(".request-item");
    const items = [];

    rows.forEach(function (row) {
        const itemNameInput = row.querySelector(".item-name");
        const descriptionInput = row.querySelector(".item-description");
        const quantityInput = row.querySelector(".item-quantity");
        const unitPriceInput = row.querySelector(".item-unit-price");
        const unitMeasureInput = row.querySelector(".item-unit-measure");
        const categoryInput = row.querySelector(".item-category");

        const itemName = itemNameInput ? itemNameInput.value.trim() : "";
        const description = descriptionInput ? descriptionInput.value.trim() : "";
        const quantity = quantityInput ? Number(quantityInput.value || 0) : 0;
        const unitPrice = unitPriceInput ? Number(unitPriceInput.value || 0) : 0;
        const unitOfMeasure = unitMeasureInput ? unitMeasureInput.value.trim() : "PCS";
        const itemCategory = categoryInput ? categoryInput.value.trim() : "";

        if (!itemName) {
            return;
        }

        const totalAmount = quantity * unitPrice;

        const dimensionValues = JSON.stringify(collectDynamicDimensions());

        const fundCode = getValue(["fundCode", "fundSelect", "fund_code", "fund", "funds", "requestFund"], ["Fund", "Fonds"]);

        const glAccountCode = getValue(
            ["glAccountCode", "glAccountSelect", "gl_account_code", "glAccount", "gl_account", "accountCode", "account_code", "glCode"],
            ["G/L Account", "GL Account", "G/L", "Account"]
        );

        items.push({
            organization: currentOrganization,

            itemName: itemName,
            description: description || itemName,
            itemCategory: itemCategory,

            quantity: quantity,
            unitPrice: unitPrice,
            unitCost: unitPrice,
            totalAmount: totalAmount,
            totalCost: totalAmount,

            unitOfMeasure: unitOfMeasure,

            budgetPlan: getValue(["budgetPlan", "budget_plan", "budgetPlanCode"], ["Budget Plan"]),
            fundCode: fundCode,

            glAccountNo: glAccountCode,
            glAccountCode: glAccountCode,

            dimensionValues: dimensionValues
        });
    });

    return items;
}

/* =========================================================
   Dimension helpers
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

        const normalizedCode = normalizeDimensionCode(dimension.dimensionCode);

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
    const dateInput = findElement(["requestDate", "date", "needDate", "needsDate"], ["Date", "Request Date"]);

    if (!dateInput) {
        return;
    }

    if (!dateInput.value) {
        const today = new Date();
        dateInput.value = today.toISOString().split("T")[0];
    }
}

function setSubmitLoading(isLoading) {
    const submitBtn = findElement(["submitBtn", "btnSubmit", "submitRequestBtn", "saveBtn"], ["Submit", "Save", "Send"]);

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
    if (window.MobileDialog && typeof window.MobileDialog.error === "function") {
        window.MobileDialog.error("Error", message);
        return;
    }

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
    if (window.MobileDialog && typeof window.MobileDialog.success === "function") {
        window.MobileDialog.success("Success", message);
        return;
    }

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

function findSelect(keys, labelTexts) {
    const element = findElement(keys, labelTexts);

    if (element && element.tagName && element.tagName.toLowerCase() === "select") {
        return element;
    }

    if (element) {
        const innerSelect = element.querySelector("select");
        if (innerSelect) {
            return innerSelect;
        }
    }

    return null;
}

function findElement(keys, labelTexts) {
    const keyList = Array.isArray(keys) ? keys : [];
    const labelList = Array.isArray(labelTexts) ? labelTexts : [];

    for (const key of keyList) {
        if (!key) {
            continue;
        }

        const byId = document.getElementById(key);
        if (byId) {
            return byId;
        }

        const byName = document.querySelector(`[name="${cssEscape(key)}"]`);
        if (byName) {
            return byName;
        }

        const byDataField = document.querySelector(`[data-field="${cssEscape(key)}"]`);
        if (byDataField) {
            return byDataField;
        }
    }

    for (const labelText of labelList) {
        const found = findElementByLabelText(labelText);
        if (found) {
            return found;
        }
    }

    return null;
}

function findElementByLabelText(labelText) {
    if (!labelText) {
        return null;
    }

    const labels = document.querySelectorAll("label");

    for (const label of labels) {
        const text = String(label.textContent || "").trim().toUpperCase();
        const expected = String(labelText || "").trim().toUpperCase();

        if (!text.includes(expected)) {
            continue;
        }

        const forId = label.getAttribute("for");

        if (forId) {
            const element = document.getElementById(forId);
            if (element) {
                return element;
            }
        }

        const parent = label.parentElement;
        if (parent) {
            const select = parent.querySelector("select");
            if (select) {
                return select;
            }

            const input = parent.querySelector("input, textarea");
            if (input) {
                return input;
            }
        }
    }

    return null;
}

function getValue(keys, labelTexts) {
    const element = findElement(keys, labelTexts);

    if (!element) {
        return "";
    }

    return String(element.value || "").trim();
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

function cssEscape(value) {
    if (window.CSS && typeof window.CSS.escape === "function") {
        return window.CSS.escape(value);
    }

    return String(value).replace(/"/g, '\\"');
}

function escapeHtml(value) {
    return String(value || "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
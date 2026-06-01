let currentUser = null;

let itemIndex = 0;
let fundsList = [];
let currenciesList = [];
let glAccountsList = [];
let dimensionGroupsList = [];

document.addEventListener("DOMContentLoaded", async function () {
    currentUser = requireLogin();

    if (!currentUser) {
        return;
    }

    displayCurrentUser();

    addItemRow();

    await loadPageData();

    const form = document.getElementById("needsRequestForm");

    if (form) {
        form.addEventListener("submit", submitNeedsRequest);
    }
});

async function loadPageData() {
    await Promise.all([
        loadFunds(),
        loadCurrencies(),
        loadGLAccounts(),
        loadDynamicDimensions()
    ]);
}

function addItemRow() {
    itemIndex++;

    const container = document.getElementById("itemsContainer");

    const itemCard = document.createElement("div");
    itemCard.className = "item-card";
    itemCard.dataset.itemIndex = itemIndex;

    itemCard.innerHTML = `
        <div class="item-card-header">
            <strong>Item ${itemIndex}</strong>

            <button type="button" class="item-remove-btn" onclick="removeItemRow(this)">
                ×
            </button>
        </div>

        <div class="form-group">
            <label>Item name</label>
            <input
                    type="text"
                    class="item-name"
                    placeholder="Ex: Fuel, laptop, printing service"
                    required>
        </div>

        <div class="form-group">
            <label>Description</label>
            <textarea
                    class="item-description"
                    placeholder="Describe the item..."></textarea>
        </div>

        <div class="row-2">
            <div class="form-group">
                <label>Category</label>
                <input
                        type="text"
                        class="item-category"
                        placeholder="Ex: Supplies">
            </div>

            <div class="form-group">
                <label>Unit</label>
                <input
                        type="text"
                        class="item-unit"
                        placeholder="Ex: Litre, Piece">
            </div>
        </div>

        <div class="row-2">
            <div class="form-group">
                <label>Quantity</label>
                <input
                        type="number"
                        min="0"
                        step="0.01"
                        class="item-quantity"
                        value="1"
                        oninput="calculateGrandTotal()"
                        required>
            </div>

            <div class="form-group">
                <label>Unit price</label>
                <input
                        type="number"
                        min="0"
                        step="0.01"
                        class="item-unit-price"
                        value="0"
                        oninput="calculateGrandTotal()"
                        required>
            </div>
        </div>

        <div class="item-total-box">
            Line Total:
            <strong class="item-line-total">0.00</strong>
        </div>
    `;

    container.appendChild(itemCard);

    updateRemoveButtons();
    calculateGrandTotal();
}

function removeItemRow(button) {
    const itemCard = button.closest(".item-card");

    if (!itemCard) {
        return;
    }

    const items = document.querySelectorAll(".item-card");

    if (items.length <= 1) {
        showMessageDialog(
            "Item required",
            "You must keep at least one item.",
            "warning"
        );
        return;
    }

    itemCard.remove();

    updateRemoveButtons();
    renumberItems();
    calculateGrandTotal();
}

function updateRemoveButtons() {
    const items = document.querySelectorAll(".item-card");

    items.forEach(item => {
        const button = item.querySelector(".item-remove-btn");

        if (button) {
            button.style.display = items.length <= 1 ? "none" : "inline-flex";
        }
    });
}

function renumberItems() {
    const items = document.querySelectorAll(".item-card");

    items.forEach((item, index) => {
        const title = item.querySelector(".item-card-header strong");

        if (title) {
            title.textContent = `Item ${index + 1}`;
        }
    });
}

function calculateGrandTotal() {
    let grandTotal = 0;

    document.querySelectorAll(".item-card").forEach(item => {
        const quantity = parseNumber(item.querySelector(".item-quantity")?.value);
        const unitPrice = parseNumber(item.querySelector(".item-unit-price")?.value);
        const lineTotal = quantity * unitPrice;

        const lineTotalLabel = item.querySelector(".item-line-total");

        if (lineTotalLabel) {
            lineTotalLabel.textContent = formatAmount(lineTotal);
        }

        grandTotal += lineTotal;
    });

    const grandTotalLabel = document.getElementById("grandTotal");

    if (grandTotalLabel) {
        grandTotalLabel.textContent = formatAmount(grandTotal);
    }

    return grandTotal;
}

async function loadFunds() {
    const fundSelect = document.getElementById("fundCode");
    const organization = getCurrentOrganization();

    fundSelect.innerHTML = `<option value="">Loading funds...</option>`;

    try {
        const response = await fetch(
            `${BASE_URL}/api/funds/organization/${encodeURIComponent(organization)}`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        fundsList = await response.json();

        fundSelect.innerHTML = `<option value="">Select fund</option>`;

        fundsList.forEach(fund => {
            const fundCode = fund.fundCode || "";
            const fundName = fund.fundName || "";

            if (!fundCode) {
                return;
            }

            const option = document.createElement("option");

            option.value = fundCode;
            option.textContent = fundName
                ? `${fundCode} - ${fundName}`
                : fundCode;

            fundSelect.appendChild(option);
        });

    } catch (error) {
        console.error(error);

        fundSelect.innerHTML = `<option value="">Failed to load funds</option>`;

        await showMessageDialog(
            "Funds error",
            "Failed to load funds: " + error.message,
            "danger"
        );
    }
}

async function loadCurrencies() {
    const currencySelect = document.getElementById("currencyCode");
    const organization = getCurrentOrganization();

    currencySelect.innerHTML = `<option value="">Loading currencies...</option>`;

    try {
        const response = await fetch(
            `${BASE_URL}/api/currencies/organization/${encodeURIComponent(organization)}`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        currenciesList = await response.json();

        currencySelect.innerHTML = `<option value="">Select currency</option>`;

        currenciesList.forEach(currency => {
            const currencyCode =
                currency.curencyCode ||
                currency.currencyCode ||
                currency.code ||
                "";

            const currencyName =
                currency.curencyName ||
                currency.currencyName ||
                currency.name ||
                "";

            if (!currencyCode) {
                return;
            }

            const option = document.createElement("option");

            option.value = currencyCode;
            option.textContent = currencyName
                ? `${currencyCode} - ${currencyName}`
                : currencyCode;

            currencySelect.appendChild(option);
        });

    } catch (error) {
        console.error(error);

        currencySelect.innerHTML = `<option value="">Failed to load currencies</option>`;

        await showMessageDialog(
            "Currency error",
            "Failed to load currencies: " + error.message,
            "danger"
        );
    }
}

async function loadGLAccounts() {
    const glSelect = document.getElementById("glAccountNo");
    const organization = getCurrentOrganization();

    glSelect.innerHTML = `<option value="">Loading G/L accounts...</option>`;

    try {
        const response = await fetch(
            `${BASE_URL}/api/gl-accounts/organization/${encodeURIComponent(organization)}`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        glAccountsList = await response.json();

        glSelect.innerHTML = `<option value="">Select G/L account</option>`;

        glAccountsList.forEach(account => {
            const glCode =
                account.glCode ||
                account.accountNo ||
                account.code ||
                "";

            const glName =
                account.glName ||
                account.accountName ||
                account.name ||
                "";

            if (!glCode) {
                return;
            }

            const option = document.createElement("option");

            option.value = glCode;
            option.textContent = glName
                ? `${glCode} - ${glName}`
                : glCode;

            glSelect.appendChild(option);
        });

    } catch (error) {
        console.error(error);

        glSelect.innerHTML = `<option value="">Failed to load G/L accounts</option>`;

        await showMessageDialog(
            "G/L Account error",
            "Failed to load G/L accounts: " + error.message,
            "danger"
        );
    }
}

async function loadDynamicDimensions() {
    const container = document.getElementById("dynamicDimensionsContainer");
    const organization = getCurrentOrganization();

    container.innerHTML = `
        <div class="empty-state small-empty">
            <div class="empty-icon">...</div>
            <h3>Loading dimensions</h3>
            <p>Please wait...</p>
        </div>
    `;

    try {
        const response = await fetch(
            `${BASE_URL}/api/dimensions/organization/${encodeURIComponent(organization)}/grouped`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        dimensionGroupsList = await response.json();

        container.innerHTML = "";

        if (!dimensionGroupsList || dimensionGroupsList.length === 0) {
            container.innerHTML = `
                <div class="empty-state small-empty">
                    <div class="empty-icon">i</div>
                    <h3>No dimensions</h3>
                    <p>No active dimensions found for this organization.</p>
                </div>
            `;
            return;
        }

        dimensionGroupsList.forEach(group => {
            container.appendChild(buildDimensionSelect(group));
        });

    } catch (error) {
        console.error(error);

        container.innerHTML = `
            <div class="empty-state small-empty">
                <div class="empty-icon">!</div>
                <h3>Failed to load dimensions</h3>
                <p>${escapeHtml(error.message)}</p>
            </div>
        `;

        await showMessageDialog(
            "Dimensions error",
            "Failed to load dimensions: " + error.message,
            "danger"
        );
    }
}

function buildDimensionSelect(group) {
    const wrapper = document.createElement("div");
    wrapper.className = "form-group dynamic-dimension-group";

    const dimensionCode = group.dimensionCode || "";
    const dimensionName = group.dimensionName || dimensionCode;
    const required = group.required === true;

    const label = document.createElement("label");
    label.textContent = required ? `${dimensionName} *` : dimensionName;

    const select = document.createElement("select");
    select.className = "dynamic-dimension-select";
    select.dataset.dimensionCode = dimensionCode;

    if (required) {
        select.required = true;
    }

    const firstOption = document.createElement("option");
    firstOption.value = "";
    firstOption.textContent = `Select ${dimensionName}`;

    select.appendChild(firstOption);

    const values = group.values || [];

    values.forEach(value => {
        const valueCode = value.valueCode || "";
        const valueName = value.valueName || "";

        if (!valueCode) {
            return;
        }

        const option = document.createElement("option");

        option.value = valueCode;
        option.textContent = valueName
            ? `${valueCode} - ${valueName}`
            : valueCode;

        select.appendChild(option);
    });

    wrapper.appendChild(label);
    wrapper.appendChild(select);

    return wrapper;
}

function collectDynamicDimensions() {
    const dimensionValues = {};

    document.querySelectorAll(".dynamic-dimension-select").forEach(select => {
        const dimensionCode = select.dataset.dimensionCode;

        if (!dimensionCode) {
            return;
        }

        dimensionValues[dimensionCode] = select.value || "";
    });

    return dimensionValues;
}

function getDepartmentFromDimensions(dimensionValues) {
    if (!dimensionValues) {
        return "";
    }

    return dimensionValues.DEPARTMENT ||
            dimensionValues.DEPARTEMENTS ||
            dimensionValues.COST_CENTER ||
            dimensionValues.COST_CENTRE ||
            "";
}

function collectItems(headerValues) {
    const items = [];

    document.querySelectorAll(".item-card").forEach(item => {
        const quantity = parseNumber(item.querySelector(".item-quantity")?.value);
        const unitPrice = parseNumber(item.querySelector(".item-unit-price")?.value);
        const totalAmount = quantity * unitPrice;

        items.push({
            organization: headerValues.organization,
            itemName: item.querySelector(".item-name")?.value.trim() || "",
            description: item.querySelector(".item-description")?.value.trim() || "",
            itemCategory: item.querySelector(".item-category")?.value.trim() || "",
            unitOfMeasure: item.querySelector(".item-unit")?.value.trim() || "",
            quantity: quantity,
            unitPrice: unitPrice,
            totalAmount: totalAmount,
            budgetPlan: headerValues.budgetPlan,
            glAccountNo: headerValues.glAccountNo,
            fundCode: headerValues.fundCode,
            dimensionValues: headerValues.dimensionValues
        });
    });

    return items;
}

async function submitNeedsRequest(event) {
    event.preventDefault();

    const organization = getCurrentOrganization();

    if (!organization) {
        await showMessageDialog(
            "Organization missing",
            "Your login session has no organization. Please login again.",
            "danger"
        );
        logoutUser();
        return;
    }

    const dimensionObject = collectDynamicDimensions();
    const dimensionValues = JSON.stringify(dimensionObject);
    const department = getDepartmentFromDimensions(dimensionObject);

    const attachmentInput = document.getElementById("attachment");
    const attachmentName =
        attachmentInput && attachmentInput.files && attachmentInput.files.length > 0
            ? attachmentInput.files[0].name
            : "";

    const headerValues = {
        organization: organization,
        budgetPlan: document.getElementById("budgetPlan").value,
        fundCode: document.getElementById("fundCode").value,
        currencyCode: document.getElementById("currencyCode").value,
        glAccountNo: document.getElementById("glAccountNo").value,
        dimensionValues: dimensionValues
    };

    const requestBody = {
        organization: organization,

        requestDate: getTodayDate(),
        requiredDate: document.getElementById("requiredDate").value || null,

        requestedBy: currentUser.fullName || currentUser.email,
        requesterEmail: currentUser.email,

        department: department,

        title: document.getElementById("title").value.trim(),
        description: document.getElementById("description").value.trim(),
        priority: document.getElementById("priority").value,

        budgetPlan: headerValues.budgetPlan,
        currencyCode: headerValues.currencyCode,
        glAccountNo: headerValues.glAccountNo,
        fundCode: headerValues.fundCode,
        dimensionValues: headerValues.dimensionValues,

        attachmentName: attachmentName,

        createdBy: currentUser.fullName || currentUser.email,

        items: collectItems(headerValues)
    };

    if (!validateRequest(requestBody)) {
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/api/needs-requests`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Request submission failed");
        }

        const saved = await response.json();

        await showMessageDialog(
            "Request submitted",
            `Your request ${saved.requestNo || ""} has been submitted successfully.`,
            "success"
        );

        window.location.href = "my-requests.html";

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Submission failed",
            error.message,
            "danger"
        );
    }
}

function validateRequest(requestBody) {
    if (!requestBody.title) {
        showMessageDialog(
            "Missing title",
            "Please enter the request title.",
            "warning"
        );
        return false;
    }

    if (!requestBody.budgetPlan) {
        showMessageDialog(
            "Missing budget plan",
            "Please select the budget plan.",
            "warning"
        );
        return false;
    }

    if (!requestBody.fundCode) {
        showMessageDialog(
            "Missing fund",
            "Please select the fund.",
            "warning"
        );
        return false;
    }

    if (!requestBody.currencyCode) {
        showMessageDialog(
            "Missing currency",
            "Please select the currency.",
            "warning"
        );
        return false;
    }

    if (!requestBody.glAccountNo) {
        showMessageDialog(
            "Missing G/L account",
            "Please select the G/L account.",
            "warning"
        );
        return false;
    }

    for (const item of requestBody.items) {
        if (!item.itemName) {
            showMessageDialog(
                "Missing item name",
                "Please enter the item name for all items.",
                "warning"
            );
            return false;
        }

        if (item.quantity <= 0) {
            showMessageDialog(
                "Invalid quantity",
                "Quantity must be greater than zero.",
                "warning"
            );
            return false;
        }
    }

    const invalidRequiredDimension = findMissingRequiredDimension();

    if (invalidRequiredDimension) {
        showMessageDialog(
            "Missing dimension",
            `Please select ${invalidRequiredDimension}.`,
            "warning"
        );
        return false;
    }

    return true;
}

function findMissingRequiredDimension() {
    const selects = document.querySelectorAll(".dynamic-dimension-select");

    for (const select of selects) {
        if (select.required && !select.value) {
            const label = select.closest(".form-group")?.querySelector("label");
            return label ? label.textContent.replace("*", "").trim() : "required dimension";
        }
    }

    return "";
}

function parseNumber(value) {
    const number = Number(value);

    if (Number.isNaN(number)) {
        return 0;
    }

    return number;
}

function formatAmount(value) {
    const number = Number(value || 0);

    return number.toLocaleString("en-US", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function getTodayDate() {
    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

function escapeHtml(value) {
    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;");
}
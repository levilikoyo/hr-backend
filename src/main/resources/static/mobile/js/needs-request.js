let itemIndex = 0;
let currentUser = null;

function setTodayDate() {
    const requiredDate = document.getElementById("requiredDate");

    if (!requiredDate) {
        return;
    }

    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, "0");
    const dd = String(today.getDate()).padStart(2, "0");

    requiredDate.value = `${yyyy}-${mm}-${dd}`;
}

function addItemRow() {
    collapseAllItems();

    itemIndex++;

    const container = document.getElementById("itemsContainer");

    if (!container) {
        console.error("itemsContainer not found");
        return;
    }

    const card = document.createElement("div");
    card.className = "item-card";
    card.setAttribute("data-item-card", "true");

    card.innerHTML = `
        <div class="item-header" onclick="toggleItemCard(this)">
            <div class="item-header-left">
                <div class="item-title">Item ${itemIndex}</div>
                <div class="item-summary">New item • Qty: 1 • Total: 0.00</div>
            </div>

            <div class="item-actions">
                <span class="collapse-icon">⌃</span>
                <button type="button" class="remove-item" onclick="event.stopPropagation(); removeItemRow(this);">×</button>
            </div>
        </div>

        <div class="item-body">

            <div class="form-group">
                <label>Item name</label>
                <input 
                    type="text" 
                    class="item-name" 
                    placeholder="Ex: Carburant"
                    oninput="updateItemSummary(this)"
                    required>
            </div>

            <div class="form-group">
                <label>Description</label>
                <textarea 
                    class="item-description" 
                    placeholder="Détail de l'article..."></textarea>
            </div>

            <div class="row-2">
                <div class="form-group">
                    <label>Category</label>
                    <select class="item-category">
                        <option value="">Select</option>
                        <option value="GOODS">GOODS</option>
                        <option value="SERVICE">SERVICE</option>
                        <option value="WORKS">WORKS</option>
                        <option value="ASSET">ASSET</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>Unit</label>
                    <select class="item-unit" onchange="updateItemSummary(this)">
                        <option value="PCS">PCS</option>
                        <option value="LITRE">LITRE</option>
                        <option value="KG">KG</option>
                        <option value="BOX">BOX</option>
                        <option value="SERVICE">SERVICE</option>
                        <option value="DAY">DAY</option>
                        <option value="MONTH">MONTH</option>
                    </select>
                </div>
            </div>

            <div class="row-2">
                <div class="form-group">
                    <label>Quantity</label>
                    <input 
                        type="text" 
                        class="item-quantity" 
                        value="1"
                        inputmode="decimal"
                        placeholder="0"
                        oninput="sanitizeDecimalInput(this); calculateTotals(); updateItemSummary(this);">
                </div>

                <div class="form-group">
                    <label>Unit Price</label>
                    <input 
                        type="text" 
                        class="item-unit-price" 
                        value="0"
                        inputmode="decimal"
                        placeholder="0.00"
                        oninput="sanitizeDecimalInput(this); calculateTotals(); updateItemSummary(this);">
                </div>
            </div>

            <div class="form-group">
                <label>Total</label>
                <input type="text" class="item-total" value="0.00" readonly>
            </div>

        </div>
    `;

    container.appendChild(card);

    calculateTotals();
    updateItemSummary(card);

    const itemNameInput = card.querySelector(".item-name");

    if (itemNameInput) {
        itemNameInput.focus();
    }
}

function sanitizeDecimalInput(input) {
    let value = input.value;

    value = value.replace(/,/g, ".");
    value = value.replace(/[^0-9.]/g, "");

    const parts = value.split(".");

    if (parts.length > 2) {
        value = parts[0] + "." + parts.slice(1).join("");
    }

    input.value = value;
}

function parseNumber(value) {
    if (value === null || value === undefined) {
        return 0;
    }

    const cleanValue = String(value)
        .replace(/\s/g, "")
        .replace(/,/g, ".")
        .replace(/[^0-9.-]/g, "");

    const number = parseFloat(cleanValue);

    return isNaN(number) ? 0 : number;
}

function formatAmount(amount) {
    const number = parseNumber(amount);

    return number
        .toFixed(2)
        .replace(/\B(?=(\d{3})+(?!\d))/g, " ");
}

function collapseAllItems() {
    document.querySelectorAll(".item-card").forEach(card => {
        card.classList.add("collapsed");

        const icon = card.querySelector(".collapse-icon");

        if (icon) {
            icon.textContent = "⌄";
        }
    });
}

function expandItemCard(card) {
    card.classList.remove("collapsed");

    const icon = card.querySelector(".collapse-icon");

    if (icon) {
        icon.textContent = "⌃";
    }
}

function toggleItemCard(headerElement) {
    const card = headerElement.closest(".item-card");

    if (!card) {
        return;
    }

    const isCollapsed = card.classList.contains("collapsed");

    collapseAllItems();

    if (isCollapsed) {
        expandItemCard(card);
    }
}

function removeItemRow(button) {
    const card = button.closest(".item-card");

    if (card) {
        card.remove();
    }

    renumberItems();
    calculateTotals();
}

function renumberItems() {
    let number = 1;

    document.querySelectorAll(".item-card").forEach(card => {
        const title = card.querySelector(".item-title");

        if (title) {
            title.textContent = "Item " + number;
        }

        number++;
    });

    itemIndex = number - 1;
}

function calculateTotals() {
    let grandTotal = 0;

    document.querySelectorAll(".item-card").forEach(card => {
        const qty = parseNumber(card.querySelector(".item-quantity").value);
        const price = parseNumber(card.querySelector(".item-unit-price").value);
        const total = qty * price;

        const totalInput = card.querySelector(".item-total");

        if (totalInput) {
            totalInput.value = formatAmount(total);
        }

        grandTotal += total;
        updateItemSummary(card);
    });

    const grandTotalElement = document.getElementById("grandTotal");

    if (grandTotalElement) {
        grandTotalElement.textContent = formatAmount(grandTotal);
    }
}

function updateItemSummary(sourceElement) {
    const card = sourceElement.closest
        ? sourceElement.closest(".item-card")
        : sourceElement;

    if (!card) {
        return;
    }

    const name = card.querySelector(".item-name")?.value.trim() || "New item";
    const qty = parseNumber(card.querySelector(".item-quantity")?.value);
    const unit = card.querySelector(".item-unit")?.value || "";
    const total = parseNumber(card.querySelector(".item-total")?.value);

    const summary = card.querySelector(".item-summary");

    if (summary) {
        summary.textContent = `${name} • Qty: ${qty} ${unit} • Total: ${formatAmount(total)}`;
    }
}

function collectItems() {
    const items = [];

    document.querySelectorAll(".item-card").forEach(card => {
        const quantity = parseNumber(card.querySelector(".item-quantity").value);
        const unitPrice = parseNumber(card.querySelector(".item-unit-price").value);
        const totalAmount = quantity * unitPrice;

        items.push({
            itemName: card.querySelector(".item-name").value.trim(),
            description: card.querySelector(".item-description").value.trim(),
            itemCategory: card.querySelector(".item-category").value,
            unitOfMeasure: card.querySelector(".item-unit").value,
            quantity: quantity,
            unitPrice: unitPrice,
            totalAmount: totalAmount,

            budgetPlan: document.getElementById("budgetPlan").value,
            glAccountNo: document.getElementById("glAccountNo").value.trim(),
            fundCode: document.getElementById("fundCode").value.trim()
        });
    });

    return items;
}

async function validateRequest(items) {
    if (!document.getElementById("title").value.trim()) {
        await showMessageDialog("Missing title", "Please enter the request title.", "warning");
        return false;
    }

    if (!document.getElementById("budgetPlan").value.trim()) {
        await showMessageDialog("Missing Budget Plan", "Please select Budget Plan.", "warning");
        return false;
    }

    if (!document.getElementById("department").value.trim()) {
        await showMessageDialog("Missing department", "Please enter department.", "warning");
        return false;
    }

    if (items.length === 0) {
        await showMessageDialog("No item", "Please add at least one item.", "warning");
        return false;
    }

    for (let i = 0; i < items.length; i++) {
        if (!items[i].itemName.trim()) {
            await showMessageDialog(
                "Missing item name",
                "Please enter item name for Item " + (i + 1) + ".",
                "warning"
            );
            return false;
        }

        if (items[i].quantity <= 0) {
            await showMessageDialog(
                "Invalid quantity",
                "Quantity must be greater than zero for Item " + (i + 1) + ".",
                "warning"
            );
            return false;
        }

        if (items[i].unitPrice <= 0) {
            await showMessageDialog(
                "Invalid unit price",
                "Unit price must be greater than zero for Item " + (i + 1) + ".",
                "warning"
            );
            return false;
        }
    }

    return true;
}

function setSubmitLoading(isLoading) {
    const button = document.querySelector("#needsRequestForm button[type='submit']");

    if (!button) {
        return;
    }

    if (isLoading) {
        button.disabled = true;
        button.textContent = "Submitting...";
    } else {
        button.disabled = false;
        button.textContent = "Soumettre la demande";
    }
}

document.addEventListener("DOMContentLoaded", function () {
    currentUser = requireLogin();

    if (!currentUser) {
        return;
    }

    displayCurrentUser();

    setTodayDate();
    addItemRow();

    const form = document.getElementById("needsRequestForm");

    if (!form) {
        console.error("needsRequestForm not found");
        return;
    }

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const items = collectItems();

        if (!await validateRequest(items)) {
            return;
        }

        const confirmed = await showConfirmDialog(
            "Submit request",
            "Do you want to submit this expression de besoin for approval?",
            "warning",
            "Submit"
        );

        if (!confirmed) {
            return;
        }

        const grandTotal = items.reduce((sum, item) => {
            return sum + parseNumber(item.totalAmount);
        }, 0);

        const attachmentInput = document.getElementById("attachment");

        const attachmentFile = attachmentInput && attachmentInput.files.length > 0
            ? attachmentInput.files[0]
            : null;

        const data = {
            organization: ORGANIZATION,

            requestDate: new Date().toISOString().substring(0, 10),
            requiredDate: document.getElementById("requiredDate").value,

            requestedBy: currentUser.fullName,
            requesterEmail: currentUser.email,

            department: document.getElementById("department").value.trim(),
            title: document.getElementById("title").value.trim(),
            description: document.getElementById("description").value.trim(),

            priority: document.getElementById("priority").value,
            budgetPlan: document.getElementById("budgetPlan").value,
            estimatedAmount: grandTotal,
            currencyCode: document.getElementById("currencyCode").value,

            frameworkCode: "OHADA",
            glAccountNo: document.getElementById("glAccountNo").value.trim(),
            fundCode: document.getElementById("fundCode").value.trim(),

            attachmentName: attachmentFile ? attachmentFile.name : "",
            status: "PENDING_APPROVAL",
            createdBy: currentUser.fullName,

            items: items
        };

        try {
            setSubmitLoading(true);

            const response = await fetch(`${BASE_URL}/api/needs-requests`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error("HTTP " + response.status + " - " + errorText);
            }

            const savedRequest = await response.json();

            await showMessageDialog(
                "Submitted",
                "Expression de besoin soumise avec succès. No: " + savedRequest.requestNo,
                "success"
            );

            window.location.href = "my-requests.html";

        } catch (error) {
            console.error(error);

            await showMessageDialog(
                "Submit failed",
                error.message,
                "danger"
            );
        } finally {
            setSubmitLoading(false);
        }
    });
});
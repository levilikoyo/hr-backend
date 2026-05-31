let currentUser = null;

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

document.addEventListener("DOMContentLoaded", function () {
    currentUser = requireRole(["HOD", "FINANCE", "DIRECTOR", "ADMIN"]);

    if (!currentUser) {
        return;
    }

    displayCurrentUser();
    loadApprovalRequests();
});

async function loadApprovalRequests() {
    const approvalList = document.getElementById("approvalList");
    const pendingCount = document.getElementById("pendingCount");

    approvalList.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">...</div>
            <h3>Loading approvals</h3>
            <p>Please wait...</p>
        </div>
    `;

    try {
        const response = await fetch(
            `${BASE_URL}/api/needs-requests/pending-approval/${encodeURIComponent(ORGANIZATION)}/${encodeURIComponent(currentUser.role)}`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        const pendingRequests = await response.json();

        approvalList.innerHTML = "";
        pendingCount.textContent = pendingRequests.length;

        if (pendingRequests.length === 0) {
            approvalList.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">✓</div>
                    <h3>No pending approvals</h3>
                    <p>No request is waiting for your approval level.</p>
                </div>
            `;
            return;
        }

        pendingRequests.forEach(request => {
            const card = document.createElement("div");
            card.className = "approval-card";
            card.setAttribute("data-request-id", request.id);

            card.innerHTML = `
                <div class="approval-card-header">
                    <div>
                        <div class="request-no">${escapeHtml(request.requestNo)}</div>
                        <div class="request-title">${escapeHtml(request.title)}</div>
                    </div>
                    <span class="status-badge">${escapeHtml(request.currentApprovalLevel || request.status)}</span>
                </div>

                <div class="approval-info">
                    <div>
                        <span>Requester</span>
                        <strong>${escapeHtml(request.requestedBy || "")}</strong>
                    </div>

                    <div>
                        <span>Department</span>
                        <strong>${escapeHtml(request.department || "")}</strong>
                    </div>

                    <div>
                        <span>Budget Plan</span>
                        <strong>${escapeHtml(request.budgetPlan || "")}</strong>
                    </div>

                    <div>
                        <span>Total</span>
                        <strong class="request-total">
                            ${formatAmount(request.estimatedAmount)} ${escapeHtml(request.currencyCode || "")}
                        </strong>
                    </div>
                </div>

                <div class="workflow-box">
                    <div class="workflow-title">Approval workflow</div>
                    ${buildWorkflowHtml(request)}
                </div>

                <div class="approval-description">
                    ${escapeHtml(request.description || "")}
                </div>

                <div class="approval-items">
                    ${buildItemsHtml(request)}
                </div>

                <div class="approval-actions">
                    <button type="button" class="btn-reject" onclick="rejectRequest(${request.id})">
                        Reject
                    </button>

                    <button type="button" class="btn-approve" onclick="approveRequest(${request.id})">
                        ${getApproveButtonText(request)}
                    </button>
                </div>
            `;

            approvalList.appendChild(card);
        });

    } catch (error) {
        console.error(error);

        pendingCount.textContent = "0";

        approvalList.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">!</div>
                <h3>Failed to load approvals</h3>
                <p>${escapeHtml(error.message)}</p>
            </div>
        `;
    }
}

function buildWorkflowHtml(request) {
    const level = request.currentApprovalLevel || "";

    return `
        <div class="workflow-steps">
            <div class="${getWorkflowStepClass(level, "HOD", request.hodApprovedBy)}">
                <span>1</span>
                <strong>HOD</strong>
                <small>${request.hodApprovedBy ? escapeHtml(request.hodApprovedBy) : "Waiting"}</small>
            </div>

            <div class="${getWorkflowStepClass(level, "FINANCE", request.financeReviewedBy)}">
                <span>2</span>
                <strong>Finance</strong>
                <small>${request.financeReviewedBy ? escapeHtml(request.financeReviewedBy) : "Waiting"}</small>
            </div>

            <div class="${getWorkflowStepClass(level, "DIRECTOR", request.directorApprovedBy)}">
                <span>3</span>
                <strong>Director</strong>
                <small>${request.directorApprovedBy ? escapeHtml(request.directorApprovedBy) : "Waiting"}</small>
            </div>
        </div>
    `;
}

function getWorkflowStepClass(currentLevel, stepLevel, approvedBy) {
    if (approvedBy) {
        return "workflow-step completed";
    }

    if (currentLevel === stepLevel) {
        return "workflow-step active";
    }

    return "workflow-step";
}

function getApproveButtonText(request) {
    const level = request.currentApprovalLevel;

    if (level === "HOD") {
        return "Approve as HOD";
    }

    if (level === "FINANCE") {
        return "Review as Finance";
    }

    if (level === "DIRECTOR") {
        return "Approve as Director";
    }

    return "Approve";
}

function buildItemsHtml(request) {
    const items = request.items;

    if (!items || items.length === 0) {
        return `
            <div class="approval-item-line">
                <div>
                    <strong>No items</strong>
                    <span>This request has no item line.</span>
                </div>
            </div>
        `;
    }

    return items.map((item, index) => {
        return `
            <div 
                class="approval-item-line editable-item-line" 
                data-item-id="${item.id}"
                data-item-index="${index}">
                
                <div class="approval-item-main">
                    <strong>${index + 1}. ${escapeHtml(item.itemName)}</strong>

                    <div class="qty-same-line">
                        <span>Qty:</span>

                        <input
                            type="text"
                            class="approval-qty-input"
                            value="${item.quantity}"
                            inputmode="decimal"
                            oninput="sanitizeDecimalInput(this); updateApprovalItemTotalOnScreen(this)"
                            onchange="updateItemQuantity(${request.id}, ${item.id}, this)">

                        <span>
                            ${escapeHtml(item.unitOfMeasure || "")} × ${formatAmount(item.unitPrice)}
                        </span>
                    </div>
                </div>

                <div class="approval-line-right">
                    <strong 
                        class="approval-line-total"
                        data-unit-price="${item.unitPrice}">
                        ${formatAmount(item.totalAmount)}
                    </strong>

                    <button
                        type="button"
                        class="delete-line-btn"
                        onclick="deleteApprovalItem(${request.id}, ${item.id})">
                        Delete
                    </button>
                </div>

            </div>
        `;
    }).join("");
}

function updateApprovalItemTotalOnScreen(input) {
    const line = input.closest(".editable-item-line");

    if (!line) {
        return;
    }

    const qty = parseNumber(input.value);
    const totalElement = line.querySelector(".approval-line-total");

    if (!totalElement) {
        return;
    }

    const unitPrice = parseNumber(totalElement.getAttribute("data-unit-price"));
    const lineTotal = qty * unitPrice;

    totalElement.textContent = formatAmount(lineTotal);

    const card = input.closest(".approval-card");

    if (!card) {
        return;
    }

    let requestTotal = 0;

    card.querySelectorAll(".editable-item-line").forEach(itemLine => {
        const qtyInput = itemLine.querySelector(".approval-qty-input");
        const totalEl = itemLine.querySelector(".approval-line-total");

        const q = parseNumber(qtyInput.value);
        const price = parseNumber(totalEl.getAttribute("data-unit-price"));

        requestTotal += q * price;
    });

    const requestTotalElement = card.querySelector(".request-total");

    if (requestTotalElement) {
        const currentText = requestTotalElement.textContent.trim();
        const parts = currentText.split(" ");
        const currency = parts[parts.length - 1] || "";

        requestTotalElement.textContent = `${formatAmount(requestTotal)} ${currency}`;
    }
}

async function updateItemQuantity(requestId, itemId, input) {
    const quantity = parseNumber(input.value);

    if (quantity <= 0) {
        await showMessageDialog(
            "Invalid quantity",
            "Quantity must be greater than zero.",
            "warning"
        );
        input.focus();
        return;
    }

    try {
        const response = await fetch(
            `${BASE_URL}/api/needs-requests/${requestId}/items/${itemId}/quantity?quantity=${encodeURIComponent(quantity)}`,
            {
                method: "PUT"
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        const updatedRequest = await response.json();

        updateCardTotal(requestId, updatedRequest.estimatedAmount, updatedRequest.currencyCode);

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Update failed",
            "Failed to update quantity: " + error.message,
            "danger"
        );

        loadApprovalRequests();
    }
}

async function deleteApprovalItem(requestId, itemId) {
    const confirmed = await showConfirmDialog(
        "Delete item line",
        "Do you want to delete this item line?",
        "danger",
        "Delete"
    );

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(
            `${BASE_URL}/api/needs-requests/${requestId}/items/${itemId}`,
            {
                method: "DELETE"
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        await response.json();

        await showMessageDialog(
            "Deleted",
            "Item line deleted successfully.",
            "success"
        );

        loadApprovalRequests();

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Delete failed",
            "Failed to delete item: " + error.message,
            "danger"
        );
    }
}

async function approveRequest(id) {
    const card = document.querySelector(`.approval-card[data-request-id="${id}"]`);

    if (!card) {
        await showMessageDialog("Not found", "Request not found.", "danger");
        return;
    }

    const totalText = card.querySelector(".request-total")?.textContent || "";

    const confirmed = await showConfirmDialog(
        "Approve request",
        "Confirm this approval level for total " + totalText.trim() + "?",
        "success",
        "Approve"
    );

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(
            `${BASE_URL}/api/needs-requests/${id}/approve?approvedBy=${encodeURIComponent(currentUser.fullName)}&role=${encodeURIComponent(currentUser.role)}&comment=${encodeURIComponent("Approved from mobile")}`,
            {
                method: "PUT"
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        const updatedRequest = await response.json();

        await showMessageDialog(
            "Approved",
            buildApprovalSuccessMessage(updatedRequest),
            "success"
        );

        loadApprovalRequests();

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Approval failed",
            error.message,
            "danger"
        );
    }
}

function buildApprovalSuccessMessage(request) {
    if (request.status === "APPROVED") {
        return "Request fully approved.";
    }

    if (request.currentApprovalLevel === "FINANCE") {
        return "HOD approval completed. Request moved to Finance review.";
    }

    if (request.currentApprovalLevel === "DIRECTOR") {
        return "Finance review completed. Request moved to Director approval.";
    }

    return "Approval completed successfully.";
}

async function rejectRequest(id) {
    const reason = await showPromptDialog(
        "Reject request",
        "Enter rejection reason:",
        "Write the reason here...",
        "warning",
        "Reject"
    );

    if (reason === null) {
        return;
    }

    if (!reason.trim()) {
        await showMessageDialog(
            "Missing reason",
            "Please enter a rejection reason.",
            "warning"
        );
        return;
    }

    try {
        const response = await fetch(
            `${BASE_URL}/api/needs-requests/${id}/reject?rejectedBy=${encodeURIComponent(currentUser.fullName)}&role=${encodeURIComponent(currentUser.role)}&reason=${encodeURIComponent(reason)}`,
            {
                method: "PUT"
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        await showMessageDialog(
            "Rejected",
            "Request rejected successfully.",
            "success"
        );

        loadApprovalRequests();

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Reject failed",
            error.message,
            "danger"
        );
    }
}

function updateCardTotal(requestId, estimatedAmount, currencyCode) {
    const card = document.querySelector(`.approval-card[data-request-id="${requestId}"]`);

    if (!card) {
        return;
    }

    const requestTotalElement = card.querySelector(".request-total");

    if (requestTotalElement) {
        requestTotalElement.textContent = `${formatAmount(estimatedAmount)} ${currencyCode || ""}`;
    }
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
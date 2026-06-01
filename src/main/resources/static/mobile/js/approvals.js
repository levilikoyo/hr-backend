let currentUser = null;

document.addEventListener("DOMContentLoaded", function () {
    currentUser = requireRole(["HOD", "FINANCE", "DIRECTOR", "ADMIN"]);

    if (!currentUser) {
        return;
    }

    displayCurrentUser();
    loadPendingApprovals();
});

async function loadPendingApprovals() {
    const approvalList = document.getElementById("approvalList");
    const pendingCount = document.getElementById("pendingCount");

    approvalList.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">...</div>
            <h3>Loading pending requests</h3>
            <p>Please wait...</p>
        </div>
    `;

    try {
        const organization = getCurrentOrganization();

        const response = await fetch(
            `${BASE_URL}/api/needs-requests/pending-approval/${encodeURIComponent(organization)}/${encodeURIComponent(currentUser.role)}`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        const requests = await response.json();

        pendingCount.textContent = requests.length;
        approvalList.innerHTML = "";

        if (!requests || requests.length === 0) {
            approvalList.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">✓</div>
                    <h3>No pending request</h3>
                    <p>You have no request waiting for approval.</p>
                </div>
            `;
            return;
        }

        requests.forEach(request => {
            approvalList.appendChild(buildApprovalCard(request));
        });

    } catch (error) {
        console.error(error);

        pendingCount.textContent = "0";

        approvalList.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">!</div>
                <h3>Failed to load requests</h3>
                <p>${escapeHtml(error.message)}</p>
            </div>
        `;
    }
}

function buildApprovalCard(request) {
    const card = document.createElement("div");
    card.className = "approval-card";

    const itemsHtml = buildItemsHtml(request.items || [], request.id);

    card.innerHTML = `
        <div class="approval-card-header">
            <div>
                <div class="request-no">${escapeHtml(request.requestNo || "")}</div>
                <div class="request-title">${escapeHtml(request.title || "")}</div>
            </div>

            <span class="status-badge">
                ${escapeHtml(getReadableStatus(request.status))}
            </span>
        </div>

        <div class="request-summary-box">
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
                <strong>${formatAmount(request.estimatedAmount)} ${escapeHtml(request.currencyCode || "")}</strong>
            </div>
        </div>

        ${buildWorkflowHtml(request)}

        <div class="request-description-clean">
            ${escapeHtml(request.description || "No description")}
        </div>

        <div class="items-clean-list">
            ${itemsHtml}
        </div>

        <div class="approval-actions">
            <button type="button" class="btn-danger" onclick="rejectRequest(${request.id})">
                Reject
            </button>

            <button type="button" class="btn-primary" onclick="approveRequest(${request.id})">
                Approve
            </button>
        </div>
    `;

    return card;
}

function buildItemsHtml(items, requestId) {
    if (!items || items.length === 0) {
        return `
            <div class="clean-item-line">
                <div>
                    <strong>No items</strong>
                    <span>No item found for this request.</span>
                </div>
            </div>
        `;
    }

    return items.map(item => {
        return `
            <div class="clean-item-line">
                <div>
                    <strong>${escapeHtml(item.itemName || "")}</strong>
                    <span>
                        Qty:
                        <input
                            type="number"
                            min="0"
                            step="0.01"
                            class="qty-input"
                            value="${item.quantity || 0}"
                            onchange="updateItemQuantity(${requestId}, ${item.id}, this.value)">
                        × ${formatAmount(item.unitPrice)}
                        = ${formatAmount(item.totalAmount)}
                    </span>
                    <small>${escapeHtml(item.description || "")}</small>
                </div>

                <button type="button" class="item-delete-btn" onclick="deleteItem(${requestId}, ${item.id})">
                    Delete
                </button>
            </div>
        `;
    }).join("");
}

function buildWorkflowHtml(request) {
    const status = request.status || "";
    const currentLevel = request.currentApprovalLevel || "";

    const hodClass = getWorkflowStepClass("HOD", status, currentLevel, request);
    const financeClass = getWorkflowStepClass("FINANCE", status, currentLevel, request);
    const directorClass = getWorkflowStepClass("DIRECTOR", status, currentLevel, request);

    return `
        <div class="workflow-box">
            <div class="workflow-title">Approval workflow</div>

            <div class="workflow-steps">
                <div class="workflow-step ${hodClass}">
                    <span>1</span>
                    <strong>HOD</strong>
                    <small>${escapeHtml(request.hodApprovedBy || "Pending")}</small>
                </div>

                <div class="workflow-step ${financeClass}">
                    <span>2</span>
                    <strong>Finance</strong>
                    <small>${escapeHtml(request.financeReviewedBy || "Pending")}</small>
                </div>

                <div class="workflow-step ${directorClass}">
                    <span>3</span>
                    <strong>Director</strong>
                    <small>${escapeHtml(request.directorApprovedBy || "Pending")}</small>
                </div>
            </div>
        </div>
    `;
}

function getWorkflowStepClass(level, status, currentLevel, request) {
    if (status === "REJECTED") {
        if (currentLevel === "REJECTED") {
            return "rejected";
        }
    }

    if (level === "HOD" && request.hodApprovedBy) {
        return "completed";
    }

    if (level === "FINANCE" && request.financeReviewedBy) {
        return "completed";
    }

    if (level === "DIRECTOR" && request.directorApprovedBy) {
        return "completed";
    }

    if (currentLevel === level) {
        return "active";
    }

    return "";
}

async function approveRequest(requestId) {
    const confirmed = await showConfirmDialog(
        "Approve request",
        "Do you want to approve this request?",
        "success",
        "Approve"
    );

    if (!confirmed) {
        return;
    }

    try {
        const approvedBy = currentUser.fullName || currentUser.email;
        const role = currentUser.role || "";
        const comment = "Approved from mobile";

        const response = await fetch(
            `${BASE_URL}/api/needs-requests/${requestId}/approve?approvedBy=${encodeURIComponent(approvedBy)}&role=${encodeURIComponent(role)}&comment=${encodeURIComponent(comment)}`,
            {
                method: "PUT"
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Approval failed");
        }

        await showMessageDialog(
            "Approved",
            "The request has been approved successfully.",
            "success"
        );

        await loadPendingApprovals();

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Approval failed",
            error.message,
            "danger"
        );
    }
}

async function rejectRequest(requestId) {
    const reason = await showPromptDialog(
        "Reject request",
        "Please enter the rejection reason:",
        "Reason"
    );

    if (!reason) {
        return;
    }

    try {
        const rejectedBy = currentUser.fullName || currentUser.email;
        const role = currentUser.role || "";

        const response = await fetch(
            `${BASE_URL}/api/needs-requests/${requestId}/reject?rejectedBy=${encodeURIComponent(rejectedBy)}&role=${encodeURIComponent(role)}&reason=${encodeURIComponent(reason)}`,
            {
                method: "PUT"
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Rejection failed");
        }

        await showMessageDialog(
            "Rejected",
            "The request has been rejected.",
            "success"
        );

        await loadPendingApprovals();

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Rejection failed",
            error.message,
            "danger"
        );
    }
}

async function updateItemQuantity(requestId, itemId, quantity) {
    try {
        const response = await fetch(
            `${BASE_URL}/api/needs-requests/${requestId}/items/${itemId}/quantity?quantity=${encodeURIComponent(quantity)}`,
            {
                method: "PUT"
            }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Quantity update failed");
        }

        await loadPendingApprovals();

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Update failed",
            error.message,
            "danger"
        );
    }
}

async function deleteItem(requestId, itemId) {
    const confirmed = await showConfirmDialog(
        "Delete item",
        "Do you want to delete this item?",
        "warning",
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
            throw new Error(errorText || "Delete failed");
        }

        await loadPendingApprovals();

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Delete failed",
            error.message,
            "danger"
        );
    }
}

function getReadableStatus(status) {
    if (status === "PENDING_HOD_APPROVAL") {
        return "Pending HOD";
    }

    if (status === "PENDING_FINANCE_REVIEW") {
        return "Pending Finance";
    }

    if (status === "PENDING_DIRECTOR_APPROVAL") {
        return "Pending Director";
    }

    if (status === "APPROVED") {
        return "Approved";
    }

    if (status === "REJECTED") {
        return "Rejected";
    }

    return status || "";
}

function formatAmount(value) {
    const number = Number(value || 0);

    return number.toLocaleString("en-US", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
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
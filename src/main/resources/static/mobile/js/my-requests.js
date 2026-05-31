let currentFilter = "ALL";
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

function formatDate(dateValue) {
    if (!dateValue) {
        return "";
    }

    return String(dateValue).substring(0, 10);
}

document.addEventListener("DOMContentLoaded", function () {
    currentUser = requireLogin();

    if (!currentUser) {
        return;
    }

    displayCurrentUser();
    loadMyRequests();
});

function filterRequests(status, button) {
    currentFilter = status;

    document.querySelectorAll(".tab-btn").forEach(btn => {
        btn.classList.remove("active");
    });

    button.classList.add("active");

    loadMyRequests();
}

async function loadMyRequests() {
    const list = document.getElementById("myRequestsList");
    const count = document.getElementById("requestCount");

    list.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">...</div>
            <h3>Loading requests</h3>
            <p>Please wait...</p>
        </div>
    `;

    try {
        const response = await fetch(
            `${BASE_URL}/api/needs-requests/organization/${encodeURIComponent(ORGANIZATION)}`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        const requests = await response.json();

        let filtered = requests;

        if (currentUser.role === "REQUESTER") {
            filtered = filtered.filter(request => {
                return request.requesterEmail === currentUser.email
                    || request.requestedBy === currentUser.fullName;
            });
        }

        if (currentFilter !== "ALL") {
            filtered = filtered.filter(request => request.status === currentFilter);
        }

        count.textContent = filtered.length;
        list.innerHTML = "";

        if (filtered.length === 0) {
            list.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">📄</div>
                    <h3>No request found</h3>
                    <p>No expression de besoin found for this filter.</p>
                </div>
            `;
            return;
        }

        filtered.forEach(request => {
            const card = document.createElement("div");
            card.className = "approval-card request-card";

            card.innerHTML = `
                <div class="approval-card-header">
                    <div>
                        <div class="request-no">${escapeHtml(request.requestNo)}</div>
                        <div class="request-title">${escapeHtml(request.title)}</div>
                    </div>

                    <span class="${getStatusClass(request.status)}">
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

                <div class="workflow-box">
                    <div class="workflow-title">Approval workflow</div>
                    ${buildWorkflowHtml(request)}
                </div>

                ${buildDescriptionHtml(request)}

                <div class="items-clean-list">
                    ${buildItemsHtml(request.items)}
                </div>

                ${buildRejectionReason(request)}

                <div class="approval-actions">
                    <button type="button" class="btn-secondary" onclick="viewApprovalHistory(${request.id})">
                        View History
                    </button>
                </div>
            `;

            list.appendChild(card);
        });

    } catch (error) {
        console.error(error);

        count.textContent = "0";

        list.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">!</div>
                <h3>Failed to load requests</h3>
                <p>${escapeHtml(error.message)}</p>
            </div>
        `;
    }
}

function buildDescriptionHtml(request) {
    if (!request.description) {
        return "";
    }

    return `
        <div class="request-description-clean">
            ${escapeHtml(request.description)}
        </div>
    `;
}

function buildWorkflowHtml(request) {
    const currentLevel = request.currentApprovalLevel || "";

    return `
        <div class="workflow-steps">
            <div class="${getWorkflowStepClass(currentLevel, "HOD", request.hodApprovedBy, request.status)}">
                <span>1</span>
                <strong>HOD</strong>
                <small>${request.hodApprovedBy ? escapeHtml(request.hodApprovedBy) : getStepText(currentLevel, "HOD", request.status)}</small>
            </div>

            <div class="${getWorkflowStepClass(currentLevel, "FINANCE", request.financeReviewedBy, request.status)}">
                <span>2</span>
                <strong>Finance</strong>
                <small>${request.financeReviewedBy ? escapeHtml(request.financeReviewedBy) : getStepText(currentLevel, "FINANCE", request.status)}</small>
            </div>

            <div class="${getWorkflowStepClass(currentLevel, "DIRECTOR", request.directorApprovedBy, request.status)}">
                <span>3</span>
                <strong>Director</strong>
                <small>${request.directorApprovedBy ? escapeHtml(request.directorApprovedBy) : getStepText(currentLevel, "DIRECTOR", request.status)}</small>
            </div>
        </div>
    `;
}

function getWorkflowStepClass(currentLevel, stepLevel, approvedBy, status) {
    if (approvedBy) {
        return "workflow-step completed";
    }

    if (status === "REJECTED" && currentLevel === "REJECTED") {
        return "workflow-step rejected";
    }

    if (currentLevel === stepLevel) {
        return "workflow-step active";
    }

    return "workflow-step";
}

function getStepText(currentLevel, stepLevel, status) {
    if (status === "APPROVED") {
        return "Completed";
    }

    if (status === "REJECTED") {
        return "Stopped";
    }

    if (currentLevel === stepLevel) {
        return "Waiting";
    }

    return "Pending";
}

function buildItemsHtml(items) {
    if (!items || items.length === 0) {
        return `
            <div class="clean-item-line">
                <div>
                    <strong>No items</strong>
                    <span>This request has no item line.</span>
                </div>
            </div>
        `;
    }

    return items.map((item, index) => {
        return `
            <div class="clean-item-line">
                <div>
                    <strong>${index + 1}. ${escapeHtml(item.itemName)}</strong>
                    <span>
                        Qty: ${item.quantity} ${escapeHtml(item.unitOfMeasure || "")} × ${formatAmount(item.unitPrice)}
                    </span>
                    ${item.description ? `<small>${escapeHtml(item.description)}</small>` : ""}
                </div>

                <strong>${formatAmount(item.totalAmount)}</strong>
            </div>
        `;
    }).join("");
}

function buildRejectionReason(request) {
    if (request.status !== "REJECTED") {
        return "";
    }

    return `
        <div class="approval-info-box rejection-box">
            <strong>Rejected by:</strong>
            <p>${escapeHtml(request.rejectedBy || "Not specified")}</p>

            <strong>Reason:</strong>
            <p>${escapeHtml(request.rejectionReason || "No reason provided.")}</p>
        </div>
    `;
}

async function viewApprovalHistory(requestId) {
    try {
        const response = await fetch(
            `${BASE_URL}/api/needs-requests/${requestId}/history`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        const history = await response.json();

        if (!history || history.length === 0) {
            await showMessageDialog(
                "Approval history",
                "No approval history found for this request.",
                "warning"
            );
            return;
        }

        let html = `<div class="history-list">`;

        history.forEach(item => {
            html += `
                <div class="history-item">
                    <div class="history-dot"></div>

                    <div class="history-content">
                        <strong>${escapeHtml(item.action || "")}</strong>
                        <span>${escapeHtml(item.approvalLevel || "")}</span>
                        <p>
                            By ${escapeHtml(item.actedBy || "Not specified")}
                            ${item.actedRole ? " - " + escapeHtml(item.actedRole) : ""}
                        </p>
                        ${item.actionComment ? `<small>${escapeHtml(item.actionComment)}</small>` : ""}
                    </div>
                </div>
            `;
        });

        html += `</div>`;

        await showHtmlDialog(
            "Approval history",
            html,
            "info"
        );

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "Failed to load history",
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

function getStatusClass(status) {
    if (status === "APPROVED") {
        return "status-approved";
    }

    if (status === "REJECTED") {
        return "status-rejected";
    }

    return "status-badge";
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
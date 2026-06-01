let currentUser = null;
let allRequests = [];
let currentFilter = "ALL";

document.addEventListener("DOMContentLoaded", function () {
    currentUser = requireLogin();

    if (!currentUser) {
        return;
    }

    displayCurrentUser();
    loadMyRequests();
});

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
        const organization = getCurrentOrganization();

        const response = await fetch(
            `${BASE_URL}/api/needs-requests/organization/${encodeURIComponent(organization)}`
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error("HTTP " + response.status + " - " + errorText);
        }

        const requests = await response.json();

        allRequests = filterRequestsForCurrentUser(requests || []);

        count.textContent = allRequests.length;

        renderRequests();

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

function filterRequestsForCurrentUser(requests) {
    const role = String(currentUser.role || "").toUpperCase();

    if (role === "REQUESTER") {
        return requests.filter(request => {
            return request.requesterEmail === currentUser.email
                || request.requestedBy === currentUser.fullName;
        });
    }

    return requests;
}

function filterRequests(status, button) {
    currentFilter = status;

    document.querySelectorAll(".tab-btn").forEach(btn => {
        btn.classList.remove("active");
    });

    if (button) {
        button.classList.add("active");
    }

    renderRequests();
}

function renderRequests() {
    const list = document.getElementById("myRequestsList");

    let requests = allRequests;

    if (currentFilter !== "ALL") {
        requests = allRequests.filter(request => request.status === currentFilter);
    }

    list.innerHTML = "";

    if (!requests || requests.length === 0) {
        list.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">📄</div>
                <h3>No request found</h3>
                <p>No request matches the selected filter.</p>
            </div>
        `;
        return;
    }

    requests.forEach(request => {
        list.appendChild(buildRequestCard(request));
    });
}

function buildRequestCard(request) {
    const card = document.createElement("div");
    card.className = "request-card";

    card.innerHTML = `
        <div class="approval-card-header">
            <div>
                <div class="request-no">${escapeHtml(request.requestNo || "")}</div>
                <div class="request-title">${escapeHtml(request.title || "")}</div>
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

        ${buildWorkflowHtml(request)}

        ${buildDescriptionHtml(request.description)}

        ${buildItemsHtml(request.items || [])}

        ${buildRejectionReason(request)}

        <div class="approval-actions">
            <button type="button" class="btn-secondary" onclick="viewApprovalHistory(${request.id})">
                View History
            </button>
        </div>
    `;

    return card;
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
        return "rejected";
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

function buildDescriptionHtml(description) {
    if (!description) {
        return `
            <div class="request-description-clean">
                No description
            </div>
        `;
    }

    return `
        <div class="request-description-clean">
            ${escapeHtml(description)}
        </div>
    `;
}

function buildItemsHtml(items) {
    if (!items || items.length === 0) {
        return `
            <div class="items-clean-list">
                <div class="clean-item-line">
                    <div>
                        <strong>No items</strong>
                        <span>No item found for this request.</span>
                    </div>
                </div>
            </div>
        `;
    }

    const html = items.map(item => {
        return `
            <div class="clean-item-line">
                <div>
                    <strong>${escapeHtml(item.itemName || "")}</strong>
                    <span>
                        Qty: ${formatAmount(item.quantity)}
                        × ${formatAmount(item.unitPrice)}
                        = ${formatAmount(item.totalAmount)}
                    </span>
                    <small>${escapeHtml(item.description || "")}</small>
                </div>
            </div>
        `;
    }).join("");

    return `
        <div class="items-clean-list">
            ${html}
        </div>
    `;
}

function buildRejectionReason(request) {
    if (request.status !== "REJECTED") {
        return "";
    }

    return `
        <div class="approval-info-box rejection-box">
            <strong>Rejection reason</strong>
            <p>${escapeHtml(request.rejectionReason || "No reason provided")}</p>
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
                "No approval history found.",
                "info"
            );
            return;
        }

        const html = history.map(item => {
            return `
                <div class="history-line">
                    <strong>${escapeHtml(item.action || "")}</strong>
                    <span>${escapeHtml(item.approvalLevel || "")}</span>
                    <small>
                        By ${escapeHtml(item.actedBy || "")}
                        ${item.actedAt ? " - " + escapeHtml(formatDateTime(item.actedAt)) : ""}
                    </small>
                    ${item.actionComment ? `<p>${escapeHtml(item.actionComment)}</p>` : ""}
                </div>
            `;
        }).join("");

        await showHtmlDialog(
            "Approval history",
            `<div class="history-list">${html}</div>`,
            "info"
        );

    } catch (error) {
        console.error(error);

        await showMessageDialog(
            "History failed",
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

function formatDateTime(value) {
    if (!value) {
        return "";
    }

    return String(value).replace("T", " ").substring(0, 16);
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